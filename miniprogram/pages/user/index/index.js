/**
 * 个人中心页
 */
const { logout, checkLogin, isMember } = require('../../../utils/auth');
const { BASE_URL, getFileUrl, api } = require('../../../config/api');
const { get } = require('../../../utils/request');

Page({
  data: {
    isLoggedIn: false,
    isMember: false,
    userInfo: null,
    maskedPhone: '',
    balance: '0.00',
    statusBarHeight: 20,
    navBarHeight: 44,

    orderCounts: {
      pending: 0,
      paid: 0,
      toEvaluate: 0
    },
    unreadCount: 0
  },

  onLoad() {
    this.initNavBarHeight();
  },

  onShow() {
    this.checkLoginStatus();
  },

  initNavBarHeight() {
    const app = getApp();
    this.setData({
      statusBarHeight: app.globalData.statusBarHeight || 20,
      navBarHeight: app.globalData.navBarHeight || 44
    });
  },

  checkLoginStatus() {
    const app = getApp();
    const isLoggedIn = checkLogin();
    console.log('User index - checkLoginStatus:', isLoggedIn);

    if (isLoggedIn) {
      const userInfo = app.globalData.userInfo;
      const isMemberUser = isMember();
      const maskedPhone = this.maskPhone(userInfo?.shouji || '');

      console.log('User index - userInfo:', userInfo);

      this.setData({
        isLoggedIn: true,
        isMember: isMemberUser,
        userInfo: {
          ...userInfo,
          avatarUrl: getFileUrl(userInfo?.zhaopian || userInfo?.touxiang)
        },
        maskedPhone
      }, () => {
        // 确保状态更新后再加载余额
        this.loadBalance();
      });
    } else {
      this.setData({
        isLoggedIn: false,
        isMember: false,
        userInfo: null,
        maskedPhone: '',
        orderCounts: { pending: 0, paid: 0, toEvaluate: 0 },
        unreadCount: 0
      });
    }
  },

  maskPhone(phone) {
    if (!phone || phone.length !== 11) return phone || '';
    return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
  },

  goLogin() {
    wx.navigateTo({ url: '/pages/login/login' });
  },

  goEditInfo() {
    if (!this.data.isLoggedIn) {
      this.goLogin();
      return;
    }
    wx.navigateTo({ url: '/pages/user/info/info' });
  },

  loadBalance() {
    if (!this.data.isLoggedIn) {
      this.setData({ balance: '0.00' });
      return;
    }

    const isMemberUser = this.data.isMember;
    const url = isMemberUser ? api.auth.memberBalance : api.auth.userBalance;

    get(url, {}, { loading: false, showError: false }).then(res => {
      if (res.code === 0) {
        const balance = (res.balance || 0).toFixed(2);
        this.setData({ balance });

        const app = getApp();
        if (app.globalData.userInfo) {
          app.globalData.userInfo.yue = res.balance;
          if (res.points !== undefined) {
            app.globalData.userInfo.jifen = res.points;
          }
        }
      } else {
        // 接口返回错误时，显示默认余额
        console.log('Load balance failed:', res.msg);
        this.setData({ balance: '0.00' });
      }
    }).catch(err => {
      console.error('Load balance error:', err);
      // 网络错误时显示默认余额，不影响其他功能
      this.setData({ balance: '0.00' });
    });
  },

  goRecharge() {
    if (!this.data.isLoggedIn) {
      this.goLogin();
      return;
    }
    wx.navigateTo({ url: '/pages/user/recharge/recharge' });
  },

  goPoints() {
    if (!this.data.isLoggedIn) {
      this.goLogin();
      return;
    }

    if (!this.data.isMember) {
      wx.showModal({
        title: '会员专属',
        content: '积分功能仅限会员使用，是否前往注册会员？',
        confirmText: '去注册',
        cancelText: '取消',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({ url: '/pages/register/register?type=member' });
          }
        }
      });
      return;
    }

    wx.navigateTo({ url: '/pages/user/points/points' });
  },

  goOrderList(e) {
    if (!this.data.isLoggedIn) {
      this.goLogin();
      return;
    }

    const tab = e.currentTarget.dataset.tab || 0;
    const app = getApp();
    app.globalData.orderTabIndex = parseInt(tab);
    wx.switchTab({ url: '/pages/order/list/list' });
  },

  goEvaluateList() {
    if (!this.data.isLoggedIn) {
      this.goLogin();
      return;
    }
    const app = getApp();
    app.globalData.orderTabIndex = 3;
    wx.switchTab({ url: '/pages/order/list/list' });
  },

  goCollect() {
    if (!this.data.isLoggedIn) {
      this.goLogin();
      return;
    }
    wx.navigateTo({ url: '/pages/user/collect/collect' });
  },

  goMessage() {
    if (!this.data.isLoggedIn) {
      this.goLogin();
      return;
    }
    wx.navigateTo({ url: '/pages/user/message/message' });
  },

  goAIService() {
    wx.navigateTo({ url: '/pages/service/ai/ai' });
  },

  goFAQ() {
    wx.navigateTo({ url: '/pages/service/faq/faq' });
  },

  goPassword() {
    if (!this.data.isLoggedIn) {
      this.goLogin();
      return;
    }
    wx.navigateTo({ url: '/pages/user/password/password' });
  },

  onLogout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          logout().then(() => {
            this.checkLoginStatus();
          });
        }
      }
    });
  },

  onShareAppMessage() {
    return {
      title: '酒店住房管理',
      path: '/pages/index/index'
    };
  }
});
