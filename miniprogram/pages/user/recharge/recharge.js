/**
 * 余额充值页面
 */
const app = getApp();
const { api } = require('../../../config/api');
const { get, post } = require('../../../utils/request');
const { isMember, isLoggedIn } = require('../../../utils/auth');

Page({
  data: {
    balance: 0,
    selectedAmount: 0,
    customAmount: '',
    loading: false,
    isMemberUser: false,
    amountOptions: [50, 100, 200, 500, 1000, 2000]
  },

  onLoad() {
    if (!isLoggedIn()) {
      wx.showModal({
        title: '提示',
        content: '请先登录',
        showCancel: false,
        success: () => {
          wx.navigateTo({ url: '/pages/login/login' });
        }
      });
      return;
    }
    
    this.setData({ isMemberUser: isMember() });
    this.loadBalance();
  },

  onShow() {
    if (isLoggedIn()) {
      this.loadBalance();
    }
  },

  loadBalance() {
    const url = this.data.isMemberUser ? api.auth.memberBalance : api.auth.userBalance;
    
    get(url, {}, { loading: false }).then(res => {
      if (res.code === 0) {
        this.setData({
          balance: res.balance || 0,
          points: res.points || 0
        });
      }
    }).catch(err => {
      console.error('Load balance error:', err);
    });
  },

  selectAmount(e) {
    const amount = parseInt(e.currentTarget.dataset.amount);
    this.setData({
      selectedAmount: amount,
      customAmount: ''
    });
  },

  onCustomAmountInput(e) {
    const value = e.detail.value;
    this.setData({
      customAmount: value,
      selectedAmount: 0
    });
  },

  doRecharge() {
    const { selectedAmount, customAmount, loading } = this.data;
    
    if (loading) return;
    
    let amount = selectedAmount;
    if (customAmount) {
      amount = parseFloat(customAmount);
      if (isNaN(amount) || amount <= 0) {
        wx.showToast({ title: '请输入有效金额', icon: 'none' });
        return;
      }
    }
    
    if (amount <= 0) {
      wx.showToast({ title: '请选择充值金额', icon: 'none' });
      return;
    }
    
    if (amount > 10000) {
      wx.showToast({ title: '单次充值不超过10000元', icon: 'none' });
      return;
    }
    
    wx.showModal({
      title: '确认充值',
      content: `确定充值 ¥${amount.toFixed(2)} 吗？`,
      success: (res) => {
        if (res.confirm) {
          this.submitRecharge(amount);
        }
      }
    });
  },

  submitRecharge(amount) {
    this.setData({ loading: true });
    
    const url = this.data.isMemberUser ? api.auth.memberRecharge : api.auth.userRecharge;
    
    post(url, { amount }, { loading: true }).then(res => {
      if (res.code === 0) {
        let message = '充值成功';
        if (res.bonusPoints) {
          message += `，赠送${res.bonusPoints}积分`;
        }
        
        wx.showToast({ title: message, icon: 'success', duration: 2000 });
        
        this.setData({
          balance: res.balance || this.data.balance + amount,
          points: res.points || this.data.points,
          selectedAmount: 0,
          customAmount: ''
        });
        
        // 更新全局用户信息
        if (app.globalData.userInfo) {
          app.globalData.userInfo.yue = res.balance;
          if (res.points !== undefined) {
            app.globalData.userInfo.jifen = res.points;
          }
        }
      } else {
        wx.showToast({ title: res.msg || '充值失败', icon: 'none' });
      }
    }).catch(err => {
      console.error('Recharge error:', err);
      wx.showToast({ title: '充值失败', icon: 'none' });
    }).finally(() => {
      this.setData({ loading: false });
    });
  }
});
