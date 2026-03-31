const app = getApp();
const { api, getFileUrl } = require('../../../config/api');
const { isMember } = require('../../../utils/auth');
const { get } = require('../../../utils/request');

Page({
  data: {
    resultType: 'success',
    orderId: '',
    orderNo: '',
    roomName: '',
    roomType: '',
    checkInDate: '',
    checkOutDate: '',
    nights: 0,
    guestName: '',
    guestPhone: '',
    totalAmount: 0,
    loading: false
  },

  onLoad(options) {
    console.log('Result page onLoad, options:', options);
    const { type, orderId, amount } = options;
    this.setData({
      resultType: type || 'success',
      totalAmount: parseFloat(amount) || 0
    });
    
    if (orderId) {
      this.setData({ orderId: orderId });
      this.loadOrderInfo(orderId);
    }
  },

  loadOrderInfo(orderId) {
    this.setData({ loading: true, orderId: orderId });
    
    const isMemberUser = isMember();
    const detailUrl = isMemberUser ? api.order.memberDetail(orderId) : api.order.userDetail(orderId);
    
    console.log('Loading order info from:', detailUrl);
    
    get(detailUrl, {}, { loading: false }).then(res => {
      console.log('Order detail response:', res);
      if (res.code === 0 && res.data) {
        const order = res.data || {};
        this.setData({
          orderNo: order.yuyuebianhao || order.id,
          roomName: order.kefanghao || '房间',
          roomType: order.kefangleixing || '标准房',
          checkInDate: order.ruzhushijian ? order.ruzhushijian.split(' ')[0] : '--',
          nights: order.tianshu || 1,
          guestName: order.xingming || '--',
          guestPhone: order.shouji || '--',
          totalAmount: parseFloat(order.zongjia) || this.data.totalAmount
        });
      }
    }).catch(err => {
      console.error('Load order info error:', err);
    }).finally(() => {
      this.setData({ loading: false });
    });
  },

  goHome() {
    wx.switchTab({ url: '/pages/index/index' });
  },

  goOrderDetail() {
    const id = this.data.orderId || this.data.orderNo;
    if (id) {
      wx.redirectTo({
        url: `/pages/order/detail/detail?id=${id}`
      });
    } else {
      wx.switchTab({ url: '/pages/order/list/list' });
    }
  }
});
