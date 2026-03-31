/**
 * 登录页 - 简化版
 */
const { userLogin, memberLogin } = require('../../utils/auth');

Page({
  data: {
    userType: 'user',
    username: '',
    password: '',
    showPassword: false,
    submitting: false,
    canSubmit: false,
    redirectUrl: ''
  },

  onLoad(options) {
    if (options.redirect) {
      this.setData({
        redirectUrl: decodeURIComponent(options.redirect)
      });
    }
  },

  switchUserType(e) {
    const type = e.currentTarget.dataset.type;
    this.setData({ userType: type });
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value });
    this.checkCanSubmit();
  },

  clearUsername() {
    this.setData({ username: '' });
    this.checkCanSubmit();
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value });
    this.checkCanSubmit();
  },

  togglePassword() {
    this.setData({
      showPassword: !this.data.showPassword
    });
  },

  checkCanSubmit() {
    const { username, password } = this.data;
    // 简化条件：用户名和密码都不为空即可
    const canSubmit = username.trim().length > 0 && password.length > 0;
    this.setData({ canSubmit });
  },

  onLogin() {
    const { username, password, userType, submitting, canSubmit } = this.data;
    
    if (submitting || !canSubmit) return;
    
    if (!username.trim()) {
      wx.showToast({ title: '请输入用户名', icon: 'none' });
      return;
    }
    
    if (!password) {
      wx.showToast({ title: '请输入密码', icon: 'none' });
      return;
    }
    
    this.setData({ submitting: true });
    
    const loginFn = userType === 'member' ? memberLogin : userLogin;
    
    loginFn(username.trim(), password).then(res => {
      wx.showToast({ title: '登录成功', icon: 'success' });
      
      setTimeout(() => {
        this.handleLoginSuccess();
      }, 1500);
      
    }).catch(err => {
      console.error('Login error:', err);
      wx.showToast({ title: err.msg || err.message || '登录失败', icon: 'none' });
    }).finally(() => {
      this.setData({ submitting: false });
    });
  },

  handleLoginSuccess() {
    const { redirectUrl } = this.data;
    
    if (redirectUrl) {
      wx.redirectTo({
        url: redirectUrl,
        fail: () => {
          wx.switchTab({
            url: redirectUrl,
            fail: () => {
              wx.switchTab({ url: '/pages/index/index' });
            }
          });
        }
      });
    } else {
      const pages = getCurrentPages();
      if (pages.length > 1) {
        wx.navigateBack();
      } else {
        wx.switchTab({ url: '/pages/index/index' });
      }
    }
  },

  goRegister() {
    wx.navigateTo({ url: '/pages/register/register' });
  },

  goForget() {
    const type = this.data.userType === 'member' ? 'member' : 'user';
    wx.navigateTo({ url: `/pages/login/forget?type=${type}` });
  }
});
