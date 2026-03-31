const app = getApp();
const { api, getFileUrl } = require('../../../config/api');
const { isMember } = require('../../../utils/auth');
const { post, get } = require('../../../utils/request');

Page({
  data: {
    orderId: '',
    orderInfo: null,
    totalAmount: 0,
    balance: 0,
    payMethod: 'wechat',
    loading: false,
    paying: false,
    isMemberUser: false
  },

  onLoad(options) {
    const { orderId, amount } = options;
    this.setData({
      orderId,
      totalAmount: parseFloat(amount) || 0,
      isMemberUser: isMember()
    });
    this.loadBalance();
    this.loadOrderInfo(orderId);
  },

  onShow() {
    this.loadBalance();
  },

  loadBalance() {
    const isMemberUser = this.data.isMemberUser;
    const url = isMemberUser ? api.auth.memberBalance : api.auth.userBalance;
    
    get(url, {}, { loading: false }).then(res => {
      if (res.code === 0) {
        this.setData({ balance: res.balance || 0 });
        if (app.globalData.userInfo) {
          app.globalData.userInfo.yue = res.balance;
        }
      }
    }).catch(err => {
      console.error('Load balance error:', err);
      const userInfo = app.globalData.userInfo || {};
      this.setData({ balance: parseFloat(userInfo.yue) || 0 });
    });
  },

  loadOrderInfo(orderId) {
    if (!orderId) return;
    
    const isMemberUser = isMember();
    const detailUrl = isMemberUser ? api.order.memberDetail(orderId) : api.order.userDetail(orderId);
    
    get(detailUrl, {}, { loading: false }).then(res => {
      if (res.code === 0 && res.data) {
        const order = res.data;
        // 检查订单是否已支付
        if (order.ispay === '已支付' || order.yuyuezhuangtai === '已支付' || order.yuyuezhuangtai === '待入住') {
          wx.showModal({
            title: '提示',
            content: '该订单已支付，无需重复支付',
            showCancel: false,
            success: () => {
              wx.navigateBack();
            }
          });
          return;
        }
        this.setData({
          orderInfo: order,
          totalAmount: parseFloat(order.zongjia) || parseFloat(order.jiage) || this.data.totalAmount
        });
      }
    }).catch(err => {
      console.error('Load order info error:', err);
    });
  },

  selectMethod(e) {
    const method = e.currentTarget.dataset.method;
    
    if (method === 'balance' && this.data.balance < this.data.totalAmount) {
      wx.showToast({ title: '余额不足', icon: 'none' });
      return;
    }
    
    this.setData({ payMethod: method });
  },

  doPay() {
    if (this.data.paying) {
      console.log('doPay - already paying, preventing duplicate');
      return;
    }
    
    // 再次检查订单状态，防止重复支付
    const { orderInfo, orderId, payMethod, balance, totalAmount } = this.data;
    if (orderInfo && (orderInfo.ispay === '已支付' || orderInfo.yuyuezhuangtai === '已支付' || orderInfo.yuyuezhuangtai === '待入住')) {
      wx.showToast({ title: '订单已支付，无需重复支付', icon: 'none' });
      setTimeout(() => wx.navigateBack(), 1500);
      return;
    }
    
    if (!orderId) {
      wx.showToast({ title: '订单信息异常', icon: 'none' });
      return;
    }
    
    // 余额支付时检查余额是否充足
    if (payMethod === 'balance' && balance < totalAmount) {
      wx.showModal({
        title: '余额不足',
        content: `当前余额 ¥${balance.toFixed(2)}，需支付 ¥${totalAmount.toFixed(2)}，是否去充值？`,
        confirmText: '去充值',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({ url: '/pages/user/recharge/recharge' });
          }
        }
      });
      return;
    }
    
    this.setData({ paying: true });
    wx.showLoading({ title: '支付中...', mask: true });
    
    const isMemberUser = isMember();
    // 使用专用的 pay 接口
    const payUrl = isMemberUser ? api.order.memberPay(orderId) : api.order.userPay(orderId);
    
    // 传递支付方式参数
    post(payUrl, { payType: payMethod }, { loading: false, showError: true }).then(res => {
      wx.hideLoading();
      if (res.code === 0) {
        this.paySuccess();
      } else {
        wx.showToast({ title: res.msg || '支付失败', icon: 'none' });
      }
    }).catch(err => {
      wx.hideLoading();
      console.error('Payment error:', err);
      wx.showToast({ title: '支付失败，请重试', icon: 'none' });
    }).finally(() => {
      this.setData({ paying: false });
    });
  },

  goRecharge() {
    wx.navigateTo({ url: '/pages/user/recharge/recharge' });
  },

  paySuccess() {
    wx.showToast({
      title: '支付成功',
      icon: 'success',
      duration: 1500
    });
    
    setTimeout(() => {
      wx.redirectTo({
        url: `/pages/booking/result/result?type=success&orderId=${this.data.orderId}&amount=${this.data.totalAmount}`
      });
    }, 1500);
  },

  goBack() {
    wx.navigateBack();
  }
});
