/**
 * 找回密码页
 */
const { post, get } = require('../../utils/request');
const { api } = require('../../config/api');
const { isPhone, isPassword } = require('../../utils/validate');

Page({
  data: {
    step: 1, // 1: 验证身份, 2: 设置新密码
    userType: 'user', // 'user' 或 'member'
    username: '',
    phone: '',
    newPassword: '',
    confirmPassword: '',
    showPassword: false,
    submitting: false,
    canSubmit: false,
    verifiedUserId: null
  },

  onLoad: function(options) {
    if (options.type === 'member') {
      this.setData({ userType: 'member' });
    }
  },

  onUsernameInput: function(e) {
    this.setData({ username: e.detail.value });
    this.checkCanSubmit();
  },

  onPhoneInput: function(e) {
    this.setData({ phone: e.detail.value });
    this.checkCanSubmit();
  },

  onNewPasswordInput: function(e) {
    this.setData({ newPassword: e.detail.value });
    this.checkCanSubmit();
  },

  onConfirmPasswordInput: function(e) {
    this.setData({ confirmPassword: e.detail.value });
    this.checkCanSubmit();
  },

  togglePassword: function() {
    this.setData({ showPassword: !this.data.showPassword });
  },

  checkCanSubmit: function() {
    const { step, username, phone, newPassword, confirmPassword } = this.data;
    
    let canSubmit = false;
    
    if (step === 1) {
      canSubmit = username.trim().length >= 2 && phone.length === 11;
    } else {
      canSubmit = newPassword.length >= 6 && confirmPassword.length >= 6;
    }
    
    this.setData({ canSubmit });
  },

  onSubmit: function() {
    if (this.data.step === 1) {
      this.verifyIdentity();
    } else {
      this.resetPassword();
    }
  },

  /**
   * 验证身份
   */
  verifyIdentity: function() {
    const { username, phone, userType } = this.data;
    
    if (!username.trim()) {
      wx.showToast({ title: '请输入账号', icon: 'none' });
      return;
    }
    
    if (!isPhone(phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' });
      return;
    }
    
    this.setData({ submitting: true });
    
    // 查询用户是否存在
    const listUrl = userType === 'member' ? '/huiyuan/list' : '/yonghu/list';
    
    get(listUrl, { 
      zhanghao: username.trim(),
      shouji: phone,
      page: 1, 
      limit: 1 
    }, { loading: true, loadingText: '验证中...' }).then(res => {
      if (res.code === 0 && res.data?.list?.length > 0) {
        const user = res.data.list[0];
        if (user.shouji === phone) {
          this.setData({ 
            step: 2,
            canSubmit: false,
            verifiedUserId: user.id
          });
          wx.showToast({ title: '验证通过', icon: 'success' });
        } else {
          wx.showToast({ title: '账号与手机号不匹配', icon: 'none' });
        }
      } else {
        wx.showToast({ title: '账号不存在', icon: 'none' });
      }
    }).catch(err => {
      console.error('Verify error:', err);
      wx.showToast({ title: '验证失败', icon: 'none' });
    }).finally(() => {
      this.setData({ submitting: false });
    });
  },

  /**
   * 重置密码
   */
  resetPassword: function() {
    const { newPassword, confirmPassword, userType, verifiedUserId } = this.data;
    
    if (!verifiedUserId) {
      wx.showToast({ title: '请先验证身份', icon: 'none' });
      this.setData({ step: 1 });
      return;
    }
    
    if (!isPassword(newPassword)) {
      wx.showToast({ title: '密码长度为6-20位', icon: 'none' });
      return;
    }
    
    if (newPassword !== confirmPassword) {
      wx.showToast({ title: '两次密码不一致', icon: 'none' });
      return;
    }
    
    this.setData({ submitting: true });
    
    // 调用后端更新接口
    const apiUrl = userType === 'member' 
      ? api.auth.memberUpdate 
      : api.auth.userUpdate;
    
    post(apiUrl, {
      id: verifiedUserId,
      mima: newPassword
    }, {
      loading: true,
      loadingText: '重置中...'
    }).then(res => {
      if (res.code === 0) {
        wx.showToast({ title: '密码重置成功', icon: 'success' });
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      } else {
        wx.showToast({ title: res.msg || '重置失败', icon: 'none' });
      }
    }).catch(err => {
      console.error('Reset password error:', err);
    }).finally(() => {
      this.setData({ submitting: false });
    });
  },

  goLogin: function() {
    wx.navigateBack();
  }
});
