/**
 * 客房详情页
 */
const app = getApp();
const { api, getFileUrl } = require('../../../config/api');
const { checkLogin, isMember } = require('../../../utils/auth');
const { get, post } = require('../../../utils/request');

Page({
  data: {
    roomId: '',
    roomInfo: {},
    images: [],
    facilities: [],
    reviews: [],
    reviewCount: 0,
    statusText: '可预订',
    statusClass: 'available',
    memberPrice: 0,
    collected: false,
    loading: false
  },

  onLoad(options) {
    const { id } = options;
    if (id) {
      this.setData({ roomId: id });
      this.loadRoomDetail(id);
    }
  },

  onShow() {
    if (this.data.roomId && checkLogin()) {
      this.checkCollected(this.data.roomId);
    }
  },

  loadRoomDetail(id) {
    this.setData({ loading: true });
    
    get(api.room.detail(id), {}, { loading: false }).then(res => {
      if (res.code === 0) {
        const room = res.data || {};
        
        // 解析多图：支持逗号分隔的多图格式
        const images = [];
        if (room.kefangtupian) {
          const imgList = room.kefangtupian.split(',').filter(img => img.trim());
          imgList.forEach(img => {
            images.push(getFileUrl(img.trim()));
          });
        }
        
        const facilities = [];
        if (room.kefanghuanjing) {
          facilities.push(...room.kefanghuanjing.split(/[,，、\/]/));
        }
        
        let statusText = '不可预订';
        let statusClass = 'occupied';
        const status = room.kefangzhuangtai;
        if (status === '空闲' || status === '未入住') {
          statusText = '可预订';
          statusClass = 'available';
        } else if (status) {
          statusText = status;
        }
        
        let memberPrice = 0;
        if (room.jiage) {
          memberPrice = Math.floor(room.jiage * 0.9);
        }
        
        this.setData({
          roomInfo: {
            ...room,
            imageUrl: getFileUrl(room.kefangtupian)
          },
          images,
          facilities,
          statusText,
          statusClass,
          memberPrice
        });
        
        this.loadReviews(id);
      } else {
        wx.showToast({ title: '加载失败', icon: 'none' });
      }
    }).catch(err => {
      console.error('loadRoomDetail error:', err);
      wx.showToast({ title: '网络错误', icon: 'none' });
    }).finally(() => {
      this.setData({ loading: false });
    });
  },

  loadReviews(roomId) {
    get(api.evaluate.list, { kefangId: roomId, refid: roomId, page: 1, limit: 3 }, { loading: false, showError: false }).then(res => {
      if (res.code === 0) {
        const data = res.data || {};
        // 为每个评价生成星级数组用于渲染
        const reviews = (data.list || []).map(item => ({
          ...item,
          stars: Array(item.pingfen || 5).fill(1)
        }));
        this.setData({
          reviews,
          reviewCount: data.total || 0
        });
      }
    }).catch(() => {});
  },

  checkCollected(roomId) {
    if (!checkLogin()) {
      console.log('checkCollected - user not logged in');
      return;
    }
    
    const userInfo = app.globalData.userInfo || {};
    console.log('checkCollected - userInfo:', userInfo);
    
    get(api.collect.list, { 
      userid: userInfo.id,
      refid: roomId,
      tablename: 'kefangxinxi'
    }, { loading: false, showError: false }).then(res => {
      console.log('checkCollected response:', res);
      if (res.code === 0) {
        const data = res.data || {};
        const list = data.list || [];
        this.setData({ collected: list.length > 0 });
      }
    }).catch(() => {});
  },

  toggleCollect() {
    console.log('toggleCollect - checking login status');
    const loginStatus = checkLogin();
    console.log('toggleCollect - checkLogin result:', loginStatus);
    console.log('toggleCollect - userInfo:', app.globalData.userInfo);
    console.log('toggleCollect - token exists:', !!app.globalData.token);
    
    if (!loginStatus) {
      console.warn('toggleCollect - User not logged in, showing login prompt');
      wx.showModal({
        title: '收藏提示',
        content: '请先登录后再收藏',
        confirmText: '去登录',
        cancelText: '取消',
        success: (res) => {
          console.log('toggleCollect - modal result:', res);
          if (res.confirm) {
            wx.navigateTo({ url: '/pages/login/login' });
          }
        }
      });
      return;
    }
    
    console.log('User is logged in, proceeding with collect action');
    
    const { collected, roomId, roomInfo } = this.data;
    const userInfo = app.globalData.userInfo || {};
    
    if (collected) {
      // 取消收藏 - 需要先获取收藏记录的ID
      get(api.collect.list, {
        userid: userInfo.id,
        refid: roomId,
        tablename: 'kefangxinxi'
      }, { loading: false }).then(res => {
        if (res.code === 0 && res.data?.list?.length > 0) {
          const collectId = res.data.list[0].id;
          // 后端接口接收 Long[] 数组格式
          post(api.collect.remove, [collectId], { loading: false }).then(delRes => {
            if (delRes.code === 0) {
              this.setData({ collected: false });
              wx.showToast({ title: '已取消收藏', icon: 'none' });
            } else {
              wx.showToast({ title: delRes.msg || '取消失败', icon: 'none' });
            }
          });
        }
      });
    } else {
      // 添加收藏
      post(api.collect.add, {
        userid: userInfo.id,
        refid: roomId,
        tablename: 'kefangxinxi',
        name: roomInfo.kefangleixing + ' ' + roomInfo.kefanghao,
        picture: roomInfo.kefangtupian || ''
      }, { loading: false }).then(res => {
        if (res.code === 0) {
          this.setData({ collected: true });
          wx.showToast({ title: '收藏成功', icon: 'none' });
        } else {
          wx.showToast({ title: res.msg || '收藏失败', icon: 'none' });
        }
      });
    }
  },

  contactService() {
    wx.navigateTo({ url: '/pages/service/chat/chat' });
  },

  goBooking() {
    console.log('goBooking - checking login...');
    const loginStatus = checkLogin();
    console.log('goBooking - checkLogin result:', loginStatus);
    
    if (!loginStatus) {
      console.log('goBooking - showing login modal');
      wx.showModal({
        title: '提示',
        content: '请先登录后再预订',
        confirmText: '去登录',
        cancelText: '取消',
        success: (res) => {
          console.log('goBooking - modal result:', res);
          if (res.confirm) {
            wx.navigateTo({ url: '/pages/login/login' });
          }
        }
      });
      return;
    }
    
    if (this.data.statusClass !== 'available') {
      wx.showToast({ title: '该房间暂不可预订', icon: 'none' });
      return;
    }
    
    console.log('goBooking - navigating to confirm page');
    wx.navigateTo({
      url: `/pages/booking/confirm/confirm?roomId=${this.data.roomId}`
    });
  },

  onShareAppMessage() {
    const { roomInfo } = this.data;
    return {
      title: roomInfo.kefanghao || '客房详情',
      path: `/pages/room/detail/detail?id=${this.data.roomId}`
    };
  }
});
