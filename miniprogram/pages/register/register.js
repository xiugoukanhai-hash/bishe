/**
 * 注册页面
 * 支持普通用户和会员两种注册类型
 */
const { userRegister, memberRegister } = require('../../utils/auth');
const { isPhone, isIdCard, isPassword, isUsername } = require('../../utils/validate');

Page({
  data: {
    userType: 'user', // 'user' 或 'member'
    
    // 表单数据
    form: {
      username: '',
      password: '',
      confirmPassword: '',
      xingming: '',
      shouji: '',
      shenfenzheng: '',
      xingbie: '男',
      nianling: '',
      touxiang: ''
    },
    
    // UI状态
    showPassword: false,
    agreed: false,
    submitting: false,
    canSubmit: false,
    
    // 性别选项
    genderOptions: ['男', '女'],
    genderIndex: 0
  },

  onLoad: function(options) {
    // 从参数获取注册类型
    if (options.type === 'member') {
      this.setData({ userType: 'member' });
    }
  },

  /**
   * 切换用户类型
   */
  switchUserType: function(e) {
    const type = e.currentTarget.dataset.type;
    this.setData({ userType: type });
  },

  /**
   * 输入用户名
   */
  onUsernameInput: function(e) {
    this.setData({ 'form.username': e.detail.value });
    this.checkCanSubmit();
  },

  /**
   * 输入密码
   */
  onPasswordInput: function(e) {
    this.setData({ 'form.password': e.detail.value });
    this.checkCanSubmit();
  },

  /**
   * 输入确认密码
   */
  onConfirmPasswordInput: function(e) {
    this.setData({ 'form.confirmPassword': e.detail.value });
    this.checkCanSubmit();
  },

  /**
   * 输入姓名
   */
  onNameInput: function(e) {
    this.setData({ 'form.xingming': e.detail.value });
    this.checkCanSubmit();
  },

  /**
   * 输入手机号
   */
  onPhoneInput: function(e) {
    this.setData({ 'form.shouji': e.detail.value });
    this.checkCanSubmit();
  },

  /**
   * 输入身份证
   */
  onIdCardInput: function(e) {
    this.setData({ 'form.shenfenzheng': e.detail.value });
    this.checkCanSubmit();
  },

  /**
   * 选择性别
   */
  onGenderChange: function(e) {
    const index = e.detail.value;
    this.setData({
      genderIndex: index,
      'form.xingbie': this.data.genderOptions[index]
    });
  },

  /**
   * 输入年龄
   */
  onAgeInput: function(e) {
    const value = e.detail.value;
    if (value && (parseInt(value) < 1 || parseInt(value) > 150)) {
      wx.showToast({ title: '请输入有效年龄', icon: 'none' });
      return;
    }
    this.setData({ 'form.nianling': value });
  },

  /**
   * 切换密码显示
   */
  togglePassword: function() {
    this.setData({ showPassword: !this.data.showPassword });
  },

  /**
   * 切换协议同意
   */
  toggleAgreement: function() {
    this.setData({ agreed: !this.data.agreed });
    this.checkCanSubmit();
  },

  /**
   * 检查是否可以提交
   */
  checkCanSubmit: function() {
    const { form, agreed } = this.data;
    
    const canSubmit = 
      form.username.trim().length >= 4 &&
      form.password.length >= 6 &&
      form.confirmPassword.length >= 6 &&
      form.xingming.trim().length >= 2 &&
      form.shouji.length === 11 &&
      form.shenfenzheng.length >= 15 &&
      agreed;
    
    this.setData({ canSubmit });
  },

  /**
   * 验证表单
   */
  validateForm: function() {
    const { form } = this.data;
    
    if (!isUsername(form.username)) {
      wx.showToast({ title: '账号为4-20位字母、数字或下划线', icon: 'none' });
      return false;
    }
    
    if (!isPassword(form.password)) {
      wx.showToast({ title: '密码长度为6-20位', icon: 'none' });
      return false;
    }
    
    if (form.password !== form.confirmPassword) {
      wx.showToast({ title: '两次密码输入不一致', icon: 'none' });
      return false;
    }
    
    if (!form.xingming.trim()) {
      wx.showToast({ title: '请输入姓名', icon: 'none' });
      return false;
    }
    
    if (!isPhone(form.shouji)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' });
      return false;
    }
    
    if (!isIdCard(form.shenfenzheng)) {
      wx.showToast({ title: '请输入正确的身份证号', icon: 'none' });
      return false;
    }
    
    if (!this.data.agreed) {
      wx.showToast({ title: '请先同意用户协议', icon: 'none' });
      return false;
    }
    
    return true;
  },

  /**
   * 提交注册
   */
  onSubmit: function() {
    if (this.data.submitting || !this.data.canSubmit) return;
    
    if (!this.validateForm()) return;
    
    this.setData({ submitting: true });
    
    const { form, userType } = this.data;
    
    // 构建注册数据 - 后端字段为 zhanghao/mima
    const registerData = {
      zhanghao: form.username.trim(),
      mima: form.password,
      xingming: form.xingming.trim(),
      shouji: form.shouji,
      shenfenzheng: form.shenfenzheng,
      xingbie: form.xingbie,
      nianling: form.nianling ? parseInt(form.nianling) : null,
      zhaopian: form.touxiang || ''
    };
    
    // 根据用户类型调用不同接口
    const registerFn = userType === 'member' ? memberRegister : userRegister;
    
    registerFn(registerData).then(res => {
      if (res.code === 0) {
        wx.showToast({
          title: '注册成功',
          icon: 'success'
        });
        
        setTimeout(() => {
          // 跳转到登录页
          wx.redirectTo({
            url: '/pages/login/login'
          });
        }, 1500);
      }
    }).catch(err => {
      console.error('Register error:', err);
    }).finally(() => {
      this.setData({ submitting: false });
    });
  },

  /**
   * 跳转登录
   */
  goLogin: function() {
    wx.navigateBack({
      fail: () => {
        wx.redirectTo({
          url: '/pages/login/login'
        });
      }
    });
  },

  /**
   * 用户协议
   */
  goUserAgreement: function() {
    wx.showToast({ title: '用户协议', icon: 'none' });
  },

  /**
   * 隐私政策
   */
  goPrivacy: function() {
    wx.showToast({ title: '隐私政策', icon: 'none' });
  }
});
