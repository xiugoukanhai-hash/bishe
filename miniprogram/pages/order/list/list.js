/**
 * 订单列表页
 */
const { get, post } = require('../../../utils/request');
const { api, getFileUrl } = require('../../../config/api');
const { isMember } = require('../../../utils/auth');

Page({
  data: {
    isLoggedIn: false,
    loading: true,
    loadingMore: false,
    noMore: false,
    
    tabs: [
      { name: '全部', status: '' },
      { name: '待支付', status: '待支付' },
      { name: '已支付', status: '已支付' },
      { name: '已完成', status: '已完成' },
      { name: '已取消', status: '已取消' }
    ],
    currentTab: 0,
    
    orderList: [],
    page: 1,
    limit: 10,
    total: 0
  },

  onLoad(options) {
    if (options.tab) {
      const tabIndex = parseInt(options.tab);
      if (!isNaN(tabIndex) && tabIndex >= 0 && tabIndex < this.data.tabs.length) {
        this.setData({ currentTab: tabIndex });
      }
    }
  },

  onShow() {
    const app = getApp();
    
    // 处理从其他页面传递的tab参数
    if (app.globalData.orderTabIndex !== undefined && app.globalData.orderTabIndex !== null) {
      const tabIndex = app.globalData.orderTabIndex;
      app.globalData.orderTabIndex = null;
      
      if (tabIndex !== this.data.currentTab) {
        this.setData({ currentTab: tabIndex, page: 1, noMore: false, orderList: [] });
      }
    }
    
    this.checkLogin();
  },

  onPullDownRefresh() {
    this.onRefresh();
  },

  onReachBottom() {
    this.loadMore();
  },

  checkLogin() {
    const app = getApp();
    const { checkLogin } = require('../../../utils/auth');
    const isLoggedIn = checkLogin();
    
    console.log('Order list - checkLogin result:', isLoggedIn);
    this.setData({ isLoggedIn });
    
    if (isLoggedIn) {
      this.loadOrderList();
    } else {
      this.setData({ loading: false });
    }
  },

  onRefresh() {
    if (!this.data.isLoggedIn) {
      wx.stopPullDownRefresh();
      return;
    }
    
    this.setData({ page: 1, noMore: false });
    this.loadOrderList().finally(() => {
      wx.stopPullDownRefresh();
    });
  },

  loadMore() {
    if (this.data.loadingMore || this.data.noMore || !this.data.isLoggedIn) return;
    this.setData({ page: this.data.page + 1 });
    this.loadOrderList(true);
  },

  loadOrderList(append = false) {
    const app = getApp();
    const userInfo = app.getUserInfo();
    const isMemberUser = isMember();
    
    if (!append) {
      this.setData({ loading: true });
    } else {
      this.setData({ loadingMore: true });
    }
    
    const apiUrl = isMemberUser ? api.order.memberList : api.order.userList;
    
    const params = {
      page: this.data.page,
      limit: this.data.limit,
      sort: 'addtime',
      order: 'desc'
    };
    
    // 添加用户账号筛选
    if (isMemberUser) {
      params.zhanghao = userInfo.zhanghao;
    } else {
      params.zhanghao = userInfo.zhanghao;
    }
    
    // 添加状态筛选
    const currentStatus = this.data.tabs[this.data.currentTab].status;
    if (currentStatus) {
      params.yuyuezhuangtai = currentStatus;
    }
    
    return get(apiUrl, params, { loading: false }).then(res => {
      if (res.code === 0 && res.data) {
        const list = (res.data.list || []).map(item => {
          // 尝试获取图片URL
          let imageUrl = '';
          const imgPath = item.kefangtupian || item.tupian || item.picture;
          if (imgPath) {
            imageUrl = getFileUrl(imgPath);
          }
          return {
            ...item,
            imageUrl
          };
        });
        
        const total = res.data.total || 0;
        const noMore = this.data.page * this.data.limit >= total;
        
        this.setData({
          orderList: append ? [...this.data.orderList, ...list] : list,
          total,
          noMore
        });
        
        // 如果有订单没有图片，尝试获取房间图片
        this.loadMissingRoomImages(list);
      }
    }).finally(() => {
      this.setData({ loading: false, loadingMore: false });
    });
  },

  loadMissingRoomImages(orderList) {
    // 找出没有图片的订单
    const ordersWithoutImage = orderList.filter(order => !order.imageUrl && order.kefanghao);
    if (ordersWithoutImage.length === 0) return;
    
    // 获取唯一的房间号
    const roomNumbers = [...new Set(ordersWithoutImage.map(o => o.kefanghao))];
    
    // 为每个房间号查询图片
    roomNumbers.forEach(kefanghao => {
      get(api.room.list, { kefanghao: kefanghao, page: 1, limit: 1 }, { loading: false, showError: false }).then(res => {
        if (res.code === 0 && res.data?.list?.length > 0) {
          const room = res.data.list[0];
          const roomImageUrl = getFileUrl(room.kefangtupian || room.tupian);
          
          // 更新相关订单的图片
          const updatedList = this.data.orderList.map(order => {
            if (order.kefanghao === kefanghao && !order.imageUrl) {
              return { ...order, imageUrl: roomImageUrl };
            }
            return order;
          });
          
          this.setData({ orderList: updatedList });
        }
      });
    });
  },

  switchTab(e) {
    const index = e.currentTarget.dataset.index;
    if (index === this.data.currentTab) return;
    
    this.setData({ currentTab: index, page: 1, noMore: false, orderList: [] });
    this.loadOrderList();
  },

  goOrderDetail(e) {
    const id = e.currentTarget.dataset.id || e.detail?.order?.id;
    if (id) {
      wx.navigateTo({ url: `/pages/order/detail/detail?id=${id}` });
    }
  },

  onPayOrder(e) {
    const order = e.detail?.order || e.currentTarget.dataset.order;
    if (order?.id) {
      wx.navigateTo({ url: `/pages/booking/pay/pay?orderId=${order.id}` });
    }
  },

  onCancelOrder(e) {
    const order = e.detail?.order || e.currentTarget.dataset.order;
    if (!order?.id) return;
    
    wx.showModal({
      title: '取消订单',
      content: '确定要取消该订单吗？',
      success: (res) => {
        if (res.confirm) {
          this.doCancelOrder(order);
        }
      }
    });
  },

  doCancelOrder(order) {
    const isMemberUser = isMember();
    const isPaid = order.ispay === '已支付' || order.yuyuezhuangtai === '已支付';
    // 使用专门的取消接口，ID作为URL路径参数
    const apiUrl = isMemberUser ? api.order.memberCancel(order.id) : api.order.userCancel(order.id);
    
    post(apiUrl, {
      reason: '用户取消'
    }, { loading: true }).then(res => {
      if (res.code === 0) {
        // 根据原订单状态显示不同提示（文案简短避免被截断）
        const msg = isPaid ? '已申请退款' : '订单已取消';
        wx.showToast({ title: msg, icon: 'success', duration: 2000 });
        this.onRefresh();
      } else {
        wx.showToast({ title: res.msg || '取消失败', icon: 'none' });
      }
    }).catch(err => {
      console.error('Cancel order error:', err);
      wx.showToast({ title: '取消失败', icon: 'none' });
    });
  },

  goEvaluate(e) {
    const order = e.detail?.order || e.currentTarget.dataset.order;
    if (order?.id) {
      wx.navigateTo({ url: `/pages/order/evaluate/evaluate?orderId=${order.id}` });
    }
  },

  onRebook(e) {
    const order = e.detail?.order || e.currentTarget.dataset.order;
    if (order?.kefangid) {
      wx.navigateTo({ url: `/pages/room/detail/detail?id=${order.kefangid}` });
    } else {
      wx.switchTab({ url: '/pages/room/list/list' });
    }
  },

  goLogin() {
    wx.navigateTo({ url: '/pages/login/login' });
  },

  goRoomList() {
    wx.switchTab({ url: '/pages/room/list/list' });
  },

  onShareAppMessage() {
    return { title: '我的订单', path: '/pages/order/list/list' };
  }
});
