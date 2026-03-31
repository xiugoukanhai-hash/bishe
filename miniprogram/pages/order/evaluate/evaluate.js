const app = getApp();
const { api, getFileUrl } = require('../../../config/api');
const { isMember } = require('../../../utils/auth');
const { post, get } = require('../../../utils/request');

const RATING_TEXT = ['', '非常差', '差', '一般', '好', '非常好'];

Page({
  data: {
    orderId: '',
    orderInfo: {},
    rating: 5,
    ratingText: '非常好',
    content: '',
    images: [],
    loading: false
  },

  onLoad(options) {
    const { orderId } = options;
    if (orderId) {
      this.setData({ orderId });
      this.loadOrderInfo(orderId);
    }
  },

  loadOrderInfo(orderId) {
    this.setData({ loading: true });
    const isMemberUser = isMember();
    const url = isMemberUser ? api.order.memberDetail(orderId) : api.order.userDetail(orderId);
    
    get(url, {}, { loading: false }).then(res => {
      if (res.code === 0) {
        const order = res.data || {};
        this.setData({
          orderInfo: {
            ...order,
            imageUrl: getFileUrl(order.kefangtupian || order.tupian)
          }
        });
      }
    }).catch(err => {
      console.error('loadOrderInfo error:', err);
      wx.showToast({ title: '加载订单失败', icon: 'none' });
    }).finally(() => {
      this.setData({ loading: false });
    });
  },

  onRatingChange(e) {
    const rating = e.detail?.value || e.currentTarget.dataset.rating || 5;
    this.setData({
      rating,
      ratingText: RATING_TEXT[rating] || ''
    });
  },

  selectRating(e) {
    const rating = parseInt(e.currentTarget.dataset.rating);
    this.setData({
      rating,
      ratingText: RATING_TEXT[rating] || ''
    });
  },

  onContentInput(e) {
    this.setData({ content: e.detail.value });
  },

  chooseImage() {
    const { images } = this.data;
    const count = 3 - images.length;
    
    if (count <= 0) {
      wx.showToast({ title: '最多上传3张图片', icon: 'none' });
      return;
    }
    
    wx.chooseImage({
      count,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        this.setData({ images: images.concat(res.tempFilePaths) });
      }
    });
  },

  deleteImage(e) {
    const { index } = e.currentTarget.dataset;
    const images = [...this.data.images];
    images.splice(index, 1);
    this.setData({ images });
  },

  submitEvaluate() {
    const { orderId, orderInfo, rating, content } = this.data;
    const userInfo = app.globalData.userInfo || {};
    
    if (!content.trim()) {
      wx.showToast({ title: '请输入评价内容', icon: 'none' });
      return;
    }
    
    this.setData({ loading: true });
    
    post(api.evaluate.add, {
      refid: orderInfo.kefangid || orderInfo.id,
      tablename: 'kefangxinxi',
      userid: userInfo.id,
      nickname: userInfo.xingming || userInfo.zhanghao || userInfo.username,
      content: content,
      pingfen: rating,
      dingdanbianhao: orderInfo.yuyuebianhao || orderInfo.id
    }, { loading: false }).then(res => {
      if (res.code === 0) {
        this.updateOrderEvaluated();
      } else {
        wx.showToast({ title: res.msg || '评价失败', icon: 'none' });
        this.setData({ loading: false });
      }
    }).catch(err => {
      wx.showToast({ title: '网络错误', icon: 'none' });
      this.setData({ loading: false });
    });
  },
  
  updateOrderEvaluated() {
    const { orderId } = this.data;
    const isMemberUser = isMember();
    // 使用专用的 setEvaluated 接口
    const setEvaluatedUrl = isMemberUser ? api.order.memberSetEvaluated(orderId) : api.order.userSetEvaluated(orderId);
    
    post(setEvaluatedUrl, {}, { loading: false }).then(res => {
      if (res.code === 0) {
        wx.showToast({ title: '评价成功' });
      } else {
        wx.showToast({ title: '评价成功' });
      }
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }).catch(() => {
      wx.showToast({ title: '评价成功' });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }).finally(() => {
      this.setData({ loading: false });
    });
  }
});
