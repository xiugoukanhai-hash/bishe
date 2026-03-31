const app = getApp();
const { api, getFileUrl } = require('../../../config/api');
const { checkLogin, isMember } = require('../../../utils/auth');
const { isPhone } = require('../../../utils/validate');
const { post, get } = require('../../../utils/request');

Page({
  data: {
    userInfo: {},
    genderOptions: ['保密', '男', '女'],
    genderIndex: 0,
    loading: false
  },

  onLoad() {
    console.log('=== User info page onLoad ===');
    
    // 强制关闭所有可能残留的原生UI元素
    this.clearNativeUI();
    
    const loginStatus = checkLogin();
    console.log('User info - checkLogin:', loginStatus);
    
    if (!loginStatus) {
      console.warn('User info - not logged in, redirecting');
      wx.redirectTo({ url: '/pages/login/login' });
      return;
    }
    
    console.log('User info - loading user data');
    this.loadUserInfo();
  },
  
  clearNativeUI() {
    // 强制关闭所有可能的原生UI
    try { wx.hideLoading({ noConflict: true }); } catch (e) {}
    try { wx.hideToast({ noConflict: true }); } catch (e) {}
    try { wx.hideNavigationBarLoading(); } catch (e) {}
    // 尝试关闭任何可能卡住的键盘
    try { wx.hideKeyboard(); } catch (e) {}
  },

  onShow() {
    if (checkLogin()) {
      this.loadUserInfo();
    }
  },

  loadUserInfo() {
    const raw = app.globalData.userInfo;
    console.log('loadUserInfo - globalData.userInfo:', raw);
    
    if (!raw || !raw.id) {
      console.warn('No userInfo or missing id in globalData');
      return;
    }
    
    // 如果userInfo不完整（缺少xingming），需要重新获取
    if (!raw.xingming) {
      console.log('User info incomplete, fetching full info...');
      this.fetchFullUserInfo(raw.id);
      return;
    }
    
    this.applyUserInfo(raw);
  },
  
  fetchFullUserInfo(userId) {
    const isMemberUser = isMember();
    const infoUrl = isMemberUser 
      ? `/huiyuan/info/${userId}`
      : `/yonghu/info/${userId}`;
    
    get(infoUrl, {}, { loading: false }).then(res => {
      console.log('fetchFullUserInfo response:', res);
      if (res.code === 0 && res.data) {
        const fullInfo = res.data;
        app.globalData.userInfo = fullInfo;
        wx.setStorageSync('userInfo', fullInfo);
        this.applyUserInfo(fullInfo);
      } else {
        wx.showToast({ title: '获取用户信息失败', icon: 'none' });
      }
    }).catch(err => {
      console.error('fetchFullUserInfo failed:', err);
      wx.showToast({ title: '网络错误', icon: 'none' });
    });
  },
  
  applyUserInfo(raw) {
    const userInfo = { ...raw };
    // 保存原始头像路径用于提交
    const avatarPath = userInfo.zhaopian || userInfo.touxiang;
    userInfo.avatarRaw = avatarPath; // 保存原始路径
    userInfo.avatarUrl = getFileUrl(avatarPath); // 显示用的完整URL
    
    const genderIndex = Math.max(0, this.data.genderOptions.indexOf(userInfo.xingbie || '保密'));
    
    console.log('applyUserInfo - avatarRaw:', userInfo.avatarRaw);
    console.log('applyUserInfo - avatarUrl:', userInfo.avatarUrl);
    
    this.setData({
      userInfo,
      genderIndex
    });
  },

  chooseAvatar() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const filePath = res.tempFilePaths[0];
        this.uploadAvatar(filePath);
      }
    });
  },

  uploadAvatar(filePath) {
    wx.showLoading({ title: '上传中...' });
    
    const token = app.globalData.token || '';
    
    wx.uploadFile({
      url: api.common.upload,
      filePath,
      name: 'file',
      header: {
        'Token': token
      },
      success: (res) => {
        console.log('uploadAvatar - response:', res.data);
        try {
          const data = JSON.parse(res.data);
          if (data.code === 0) {
            // 服务器返回的是相对路径，如 "upload/xxx.jpg"
            const avatarPath = data.file || data.data;
            console.log('uploadAvatar - new avatarPath:', avatarPath);
            
            const userInfo = {
              ...this.data.userInfo,
              avatarRaw: avatarPath, // 保存原始路径用于提交
              avatarUrl: getFileUrl(avatarPath) // 显示用的完整URL
            };
            this.setData({ userInfo });
            wx.showToast({ title: '头像上传成功', icon: 'success' });
          } else {
            wx.showToast({ title: data.msg || '上传失败', icon: 'none' });
          }
        } catch (e) {
          console.error('uploadAvatar - parse error:', e);
          wx.showToast({ title: '上传失败', icon: 'none' });
        }
      },
      fail: (err) => {
        console.error('uploadAvatar - fail:', err);
        wx.showToast({ title: '上传失败', icon: 'none' });
      },
      complete: () => {
        wx.hideLoading();
      }
    });
  },

  onNicknameInput(e) {
    const userInfo = { ...this.data.userInfo, xingming: e.detail.value };
    this.setData({ userInfo });
  },

  onGenderChange(e) {
    const genderIndex = e.detail.value;
    const userInfo = { ...this.data.userInfo, xingbie: this.data.genderOptions[genderIndex] };
    this.setData({ userInfo, genderIndex });
  },

  onPhoneInput(e) {
    const userInfo = { ...this.data.userInfo, shouji: e.detail.value };
    this.setData({ userInfo });
  },

  onIdCardInput(e) {
    const userInfo = { ...this.data.userInfo, shenfenzheng: e.detail.value };
    this.setData({ userInfo });
  },

  onAvatarError() {
    console.log('onAvatarError - using default avatar');
    const userInfo = { 
      ...this.data.userInfo, 
      avatarUrl: '/images/default/avatar.png'
    };
    this.setData({ userInfo });
  },

  saveInfo() {
    const { userInfo } = this.data;
    
    if (!userInfo.xingming || !userInfo.xingming.trim()) {
      wx.showToast({ title: '请输入昵称', icon: 'none' });
      return;
    }
    
    if (userInfo.shouji && !isPhone(userInfo.shouji)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' });
      return;
    }
    
    this.setData({ loading: true });
    
    const updateUrl = isMember() ? api.auth.memberUpdate : api.auth.userUpdate;
    
    console.log('saveInfo - updating user:', userInfo.id);
    console.log('saveInfo - updateUrl:', updateUrl);
    
    // 使用avatarRaw（原始路径）而不是avatarUrl（完整URL）
    const avatarToSave = userInfo.avatarRaw || userInfo.zhaopian || userInfo.touxiang;
    console.log('saveInfo - avatarToSave:', avatarToSave);
    
    post(updateUrl, {
      id: userInfo.id,
      xingming: userInfo.xingming,
      xingbie: userInfo.xingbie,
      shouji: userInfo.shouji,
      shenfenzheng: userInfo.shenfenzheng,
      touxiang: avatarToSave,
      zhaopian: avatarToSave
    }, {
      loading: false,
      showError: true
    }).then(res => {
      console.log('saveInfo - success:', res);
      
      // 更新全局用户信息，确保头像路径正确同步
      const updatedUserInfo = { 
        ...app.globalData.userInfo, 
        ...userInfo,
        // 关键：将新头像路径同步到所有相关字段
        zhaopian: avatarToSave,
        touxiang: avatarToSave,
        avatarRaw: avatarToSave,
        avatarUrl: getFileUrl(avatarToSave)
      };
      
      console.log('saveInfo - updatedUserInfo.zhaopian:', updatedUserInfo.zhaopian);
      console.log('saveInfo - updatedUserInfo.avatarUrl:', updatedUserInfo.avatarUrl);
      
      app.globalData.userInfo = updatedUserInfo;
      wx.setStorageSync('userInfo', updatedUserInfo);
      
      wx.showToast({ title: '保存成功' });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }).catch(err => {
      console.error('saveInfo - error:', err);
    }).finally(() => {
      this.setData({ loading: false });
    });
  }
});
