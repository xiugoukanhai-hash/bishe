const app = getApp();
const { api } = require('../../../config/api');
const { checkLogin, isMember } = require('../../../utils/auth');
const { post } = require('../../../utils/request');

Page({
  data: {
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
    passwordStrength: 0,
    strengthText: '',
    canSubmit: false,
    loading: false
  },

  onLoad() {
    if (!checkLogin()) {
      wx.redirectTo({ url: '/pages/login/login' });
      return;
    }
  },

  onOldPasswordInput(e) {
    this.setData({ oldPassword: e.detail.value });
    this.checkCanSubmit();
  },

  onNewPasswordInput(e) {
    const password = e.detail.value;
    const strength = this.calculateStrength(password);
    const strengthText = ['', '弱', '中', '强'][strength] || '';
    
    this.setData({
      newPassword: password,
      passwordStrength: strength,
      strengthText
    });
    this.checkCanSubmit();
  },

  onConfirmPasswordInput(e) {
    this.setData({ confirmPassword: e.detail.value });
    this.checkCanSubmit();
  },

  calculateStrength(password) {
    if (!password || password.length < 6) return 0;
    
    let strength = 1;
    if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++;
    if (/\d/.test(password) && /[a-zA-Z]/.test(password)) strength++;
    
    return Math.min(strength, 3);
  },

  checkCanSubmit() {
    const { oldPassword, newPassword, confirmPassword } = this.data;
    const canSubmit = !!(
      oldPassword &&
      newPassword &&
      newPassword.length >= 6 &&
      confirmPassword &&
      newPassword === confirmPassword
    );
    this.setData({ canSubmit });
  },

  submitPassword() {
    const { oldPassword, newPassword, confirmPassword, canSubmit } = this.data;
    
    if (!canSubmit) return;
    
    if (!oldPassword) {
      wx.showToast({ title: '请输入原密码', icon: 'none' });
      return;
    }
    
    if (!newPassword || newPassword.length < 6) {
      wx.showToast({ title: '新密码至少6位', icon: 'none' });
      return;
    }
    
    if (newPassword !== confirmPassword) {
      wx.showToast({ title: '两次密码输入不一致', icon: 'none' });
      return;
    }
    
    if (oldPassword === newPassword) {
      wx.showToast({ title: '新密码不能与原密码相同', icon: 'none' });
      return;
    }
    
    this.doChangePassword();
  },

  doChangePassword() {
    const { oldPassword, newPassword } = this.data;
    const userInfo = app.globalData.userInfo || {};
    
    this.setData({ loading: true });
    
    const updateUrl = isMember() ? api.auth.memberUpdate : api.auth.userUpdate;
    
    post(updateUrl, {
      id: userInfo.id,
      mima: newPassword
    }, { loading: false, showError: true }).then(res => {
      if (res.code === 0) {
        wx.showToast({ title: '修改成功' });
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      }
    }).catch(err => {
      console.error('Password change error:', err);
    }).finally(() => {
      this.setData({ loading: false });
    });
  }
});
