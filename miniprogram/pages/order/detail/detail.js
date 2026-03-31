const app = getApp();
const { api, getFileUrl } = require('../../../config/api');
const { isMember } = require('../../../utils/auth');
const { get, post } = require('../../../utils/request');

const STATUS_MAP = {
  '待支付': { text: '待支付', desc: '请尽快完成支付', color: '#ff976a', class: 'pending' },
  '已支付': { text: '已支付', desc: '请按时入住', color: '#1989fa', class: 'paid' },
  '待入住': { text: '待入住', desc: '请按时入住', color: '#1989fa', class: 'paid' },
  '已入住': { text: '已入住', desc: '祝您入住愉快', color: '#52c41a', class: 'checkedin' },
  '已完成': { text: '已完成', desc: '感谢您的光临', color: '#52c41a', class: 'completed' },
  '已取消': { text: '已取消', desc: '订单已取消', color: '#999999', class: 'cancelled' },
  '已拒绝': { text: '已拒绝', desc: '预约未通过审核', color: '#ff4d4f', class: 'cancelled' },
  '申请退款': { text: '申请退款', desc: '退款申请处理中，请耐心等待', color: '#fa8c16', class: 'refunding' },
  '已退款': { text: '已退款', desc: '退款已完成', color: '#722ed1', class: 'refunded' }
};

Page({
  data: {
    orderId: '',
    orderInfo: {},
    statusText: '',
    statusDesc: '',
    statusColor: '',
    statusClass: 'pending',
    nights: 1,
    roomPrice: 0,
    totalPrice: 0,
    hasActions: false,
    showCancelBtn: false,
    showPayBtn: false,
    showEvaluateBtn: false,
    showRebookBtn: false,
    loading: true
  },

  onLoad(options) {
    const { id } = options;
    if (id) {
      this.setData({ orderId: id });
      this.loadOrderDetail(id);
    } else {
      wx.showToast({ title: '订单不存在', icon: 'none' });
      setTimeout(() => wx.navigateBack(), 1500);
    }
  },

  onShow() {
    if (this.data.orderId) {
      this.loadOrderDetail(this.data.orderId);
    }
  },

  loadOrderDetail(id) {
    this.setData({ loading: true });
    const isMemberUser = isMember();
    const url = isMemberUser ? api.order.memberDetail(id) : api.order.userDetail(id);
    
    get(url, {}, { loading: false }).then(res => {
      if (res.code === 0 && res.data) {
        const order = res.data;
        const status = order.yuyuezhuangtai || order.ispay || '待支付';
        const statusInfo = STATUS_MAP[status] || STATUS_MAP['已取消'];
        
        // 计算入住天数和价格
        const nights = parseInt(order.tianshu) || 1;
        const unitPrice = parseFloat(order.jiage) || 0;
        const roomPrice = unitPrice * nights;
        const totalPrice = parseFloat(order.zongjia) || roomPrice;
        
        // 处理入住日期显示
        let displayCheckIn = order.ruzhushijian || order.ruzhuriqi || '--';
        if (displayCheckIn && displayCheckIn.includes(' ')) {
          displayCheckIn = displayCheckIn.split(' ')[0];
        }
        
        const showCancelBtn = status === '待支付' || status === '已支付';
        const showPayBtn = status === '待支付';
        const isEvaluated = order.ispingjia === '是' || order.ispingjia === 1 || order.ispingjia === '1' || order.ispingjia === true;
        const showEvaluateBtn = status === '已完成' && !isEvaluated;
        const showRebookBtn = ['已完成', '已取消'].includes(status);
        const hasActions = showCancelBtn || showPayBtn || showEvaluateBtn || showRebookBtn;
        
        // 处理图片URL
        let imageUrl = '';
        const imgPath = order.kefangtupian || order.tupian;
        if (imgPath) {
          imageUrl = getFileUrl(imgPath);
        }
        
        this.setData({
          orderInfo: {
            ...order,
            imageUrl,
            displayCheckIn
          },
          statusText: statusInfo.text,
          statusDesc: statusInfo.desc,
          statusColor: statusInfo.color,
          statusClass: statusInfo.class,
          nights,
          roomPrice,
          totalPrice,
          hasActions,
          showCancelBtn,
          showPayBtn,
          showEvaluateBtn,
          showRebookBtn
        });
        
        // 如果订单中没有图片，从房间信息获取
        if (!imageUrl && order.kefanghao) {
          this.loadRoomImage(order.kefanghao);
        }
      } else {
        wx.showToast({ title: res.msg || '加载失败', icon: 'none' });
      }
    }).catch(err => {
      console.error('loadOrderDetail error:', err);
      wx.showToast({ title: '加载失败', icon: 'none' });
    }).finally(() => {
      this.setData({ loading: false });
    });
  },

  cancelOrder() {
    wx.showModal({
      title: '确认取消',
      content: '确定要取消这个订单吗？',
      success: (res) => {
        if (res.confirm) {
          this.doCancelOrder();
        }
      }
    });
  },

  doCancelOrder() {
    const isMemberUser = isMember();
    const { orderInfo } = this.data;
    // 判断是否已支付
    const isPaid = orderInfo.ispay === '已支付' || orderInfo.yuyuezhuangtai === '已支付' || orderInfo.yuyuezhuangtai === '待入住';
    
    // 使用专门的取消接口
    const url = isMemberUser 
      ? api.order.memberCancel(this.data.orderId) 
      : api.order.userCancel(this.data.orderId);
    
    post(url, { reason: '用户取消' }, { loading: true }).then(res => {
      if (res.code === 0) {
        // 根据原订单状态显示不同提示
        const msg = isPaid ? '已申请退款' : '订单已取消';
        wx.showToast({ title: msg, icon: 'success', duration: 2000 });
        // 刷新订单详情
        setTimeout(() => {
          this.loadOrderDetail(this.data.orderId);
        }, 1000);
      } else {
        wx.showToast({ title: res.msg || '取消失败', icon: 'none' });
      }
    }).catch(err => {
      console.error('doCancelOrder error:', err);
      wx.showToast({ title: '取消失败', icon: 'none' });
    });
  },

  loadRoomImage(kefanghao) {
    get(api.room.list, { kefanghao: kefanghao, page: 1, limit: 1 }, { loading: false, showError: false }).then(res => {
      if (res.code === 0 && res.data?.list?.length > 0) {
        const room = res.data.list[0];
        const imageUrl = getFileUrl(room.kefangtupian || room.tupian);
        if (imageUrl) {
          this.setData({
            'orderInfo.imageUrl': imageUrl
          });
        }
      }
    }).catch(err => {
      console.log('loadRoomImage error:', err);
    });
  },

  goPay() {
    const { orderInfo } = this.data;
    wx.navigateTo({
      url: `/pages/booking/pay/pay?orderId=${orderInfo.id}&amount=${orderInfo.jiage}`
    });
  },

  goEvaluate() {
    wx.navigateTo({
      url: `/pages/order/evaluate/evaluate?orderId=${this.data.orderId}`
    });
  },

  rebook() {
    const { orderInfo } = this.data;
    if (orderInfo.kefangid) {
      wx.navigateTo({
        url: `/pages/room/detail/detail?id=${orderInfo.kefangid}`
      });
    } else {
      wx.switchTab({ url: '/pages/room/list/list' });
    }
  },

  callPhone() {
    wx.makePhoneCall({ phoneNumber: '400-888-9999' });
  }
});
