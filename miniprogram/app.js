/**
 * 酒店住房管理小程序 - 全局入口
 */
const debug = require('./utils/debug');

App({
  /**
   * 全局数据
   */
  globalData: {
    // 用户信息
    userInfo: null,
    // 登录Token
    token: '',
    // 用户类型: 'yonghu' 普通用户 | 'huiyuan' 会员
    userType: '',
    // API基础地址
    baseUrl: 'http://localhost:8080/springboot6alf1',
    // 网络状态
    networkType: 'unknown',
    // 是否联网
    isConnected: true,
    // 系统信息
    systemInfo: null,
    // 状态栏高度
    statusBarHeight: 0,
    // 导航栏高度
    navBarHeight: 44,
    // 菜单按钮位置信息
    menuButtonInfo: null,
    // 版本号
    version: '1.0.0',
    // 是否首次启动
    isFirstLaunch: false
  },

  /**
   * 小程序启动时执行
   */
  onLaunch: function(options) {
    console.log('=== App onLaunch ===', options);
    
    // 初始化系统信息
    this.initSystemInfo();
    
    // 检查登录状态
    this.checkLoginStatus();
    
    // 监听网络状态
    this.watchNetworkStatus();
    
    // 检查更新
    this.checkUpdate();
    
    // 检查是否首次启动
    this.checkFirstLaunch();
    
    // 打印登录状态诊断信息
    this.printDiagnostics();
    
    // 将调试工具暴露到全局，方便在控制台使用
    this.debug = debug;
    console.log('调试提示: 在控制台输入 getApp().debug.runFullDiagnostics() 运行完整诊断');
  },
  
  /**
   * 打印诊断信息
   */
  printDiagnostics: function() {
    console.log('=== 登录状态诊断 ===');
    console.log('token:', this.globalData.token ? '存在 (' + this.globalData.token.substring(0, 20) + '...)' : '空');
    console.log('userInfo:', this.globalData.userInfo);
    console.log('userType:', this.globalData.userType);
    console.log('isLoggedIn:', this.isLoggedIn());
    console.log('localStorage token:', wx.getStorageSync('token') ? '存在' : '空');
    console.log('localStorage userInfo:', wx.getStorageSync('userInfo'));
    console.log('===================');
    
    // 清除可能残留的模态框
    try {
      wx.hideLoading();
      wx.hideToast();
    } catch (e) {
      // ignore
    }
  },

  /**
   * 小程序显示时执行
   */
  onShow: function(options) {
    console.log('App onShow', options);
    
    // 清除可能残留的UI元素
    this.clearStaleUI();
    
    // 检查场景值，处理特殊入口
    if (options.scene) {
      this.handleScene(options.scene, options.query);
    }
  },
  
  /**
   * 清除可能残留的UI元素
   */
  clearStaleUI: function() {
    try {
      wx.hideLoading({ noConflict: true });
      wx.hideToast({ noConflict: true });
    } catch (e) {
      // 忽略错误
    }
  },

  /**
   * 小程序隐藏时执行
   */
  onHide: function() {
    console.log('App onHide');
  },

  /**
   * 全局错误处理
   */
  onError: function(error) {
    console.error('App onError:', error);
    // 可以在这里上报错误日志
  },

  /**
   * 页面不存在处理
   */
  onPageNotFound: function(res) {
    console.warn('Page not found:', res);
    wx.redirectTo({
      url: '/pages/index/index'
    });
  },

  /**
   * 未处理的Promise拒绝
   */
  onUnhandledRejection: function(res) {
    console.error('Unhandled rejection:', res);
  },

  /**
   * 初始化系统信息
   */
  initSystemInfo: function() {
    try {
      // 获取系统信息
      const systemInfo = wx.getSystemInfoSync();
      this.globalData.systemInfo = systemInfo;
      this.globalData.statusBarHeight = systemInfo.statusBarHeight || 20;
      
      // 获取菜单按钮位置信息
      const menuButtonInfo = wx.getMenuButtonBoundingClientRect();
      this.globalData.menuButtonInfo = menuButtonInfo;
      
      // 计算导航栏高度
      if (menuButtonInfo) {
        this.globalData.navBarHeight = (menuButtonInfo.top - systemInfo.statusBarHeight) * 2 + menuButtonInfo.height;
      }
      
      console.log('System Info:', {
        statusBarHeight: this.globalData.statusBarHeight,
        navBarHeight: this.globalData.navBarHeight,
        platform: systemInfo.platform,
        SDKVersion: systemInfo.SDKVersion
      });
    } catch (e) {
      console.error('Failed to get system info:', e);
    }
  },

  /**
   * 检查登录状态
   */
  checkLoginStatus: function() {
    try {
      const token = wx.getStorageSync('token');
      const userInfo = wx.getStorageSync('userInfo');
      const userType = wx.getStorageSync('userType');
      const tokenExpire = wx.getStorageSync('tokenExpire');
      
      // 检查Token是否过期
      if (tokenExpire && new Date().getTime() > tokenExpire) {
        this.clearLoginInfo();
        console.log('Token expired, cleared login info');
        return;
      }
      
      if (token && userInfo) {
        this.globalData.token = token;
        this.globalData.userInfo = userInfo;
        this.globalData.userType = userType;
        console.log('Login status restored:', { userType, userId: userInfo.id, userInfo });
        
        // 如果用户信息不完整（缺少xingming字段），重新获取
        if (userInfo.id && !userInfo.xingming) {
          console.log('User info incomplete, refreshing...');
          this.refreshUserInfo(token, userInfo.id, userType);
        }
      }
    } catch (e) {
      console.error('Failed to check login status:', e);
    }
  },
  
  /**
   * 刷新用户信息
   */
  refreshUserInfo: function(token, userId, userType) {
    const that = this;
    const infoUrl = userType === 'huiyuan' 
      ? `${this.globalData.baseUrl}/huiyuan/info/${userId}`
      : `${this.globalData.baseUrl}/yonghu/info/${userId}`;
    
    wx.request({
      url: infoUrl,
      header: { 'Token': token },
      success: function(res) {
        if (res.data.code === 0 && res.data.data) {
          const fullUserInfo = res.data.data;
          that.globalData.userInfo = fullUserInfo;
          wx.setStorageSync('userInfo', fullUserInfo);
          console.log('User info refreshed:', fullUserInfo);
        }
      },
      fail: function(err) {
        console.error('Failed to refresh user info:', err);
      }
    });
  },

  /**
   * 设置登录信息
   * @param {string} token - 登录Token
   * @param {object} userInfo - 用户信息
   * @param {string} userType - 用户类型
   * @param {number} expireTime - 过期时间（毫秒），默认7天
   */
  setLoginInfo: function(token, userInfo, userType, expireTime = 7 * 24 * 60 * 60 * 1000) {
    this.globalData.token = token;
    this.globalData.userInfo = userInfo;
    this.globalData.userType = userType;
    
    try {
      wx.setStorageSync('token', token);
      wx.setStorageSync('userInfo', userInfo);
      wx.setStorageSync('userType', userType);
      wx.setStorageSync('tokenExpire', new Date().getTime() + expireTime);
      console.log('Login info saved:', { userType, userId: userInfo.id });
    } catch (e) {
      console.error('Failed to save login info:', e);
    }
  },

  /**
   * 更新用户信息
   * @param {object} userInfo - 新的用户信息
   */
  updateUserInfo: function(userInfo) {
    this.globalData.userInfo = { ...this.globalData.userInfo, ...userInfo };
    try {
      wx.setStorageSync('userInfo', this.globalData.userInfo);
    } catch (e) {
      console.error('Failed to update user info:', e);
    }
  },

  /**
   * 清除登录信息
   */
  clearLoginInfo: function() {
    this.globalData.token = '';
    this.globalData.userInfo = null;
    this.globalData.userType = '';
    
    try {
      wx.removeStorageSync('token');
      wx.removeStorageSync('userInfo');
      wx.removeStorageSync('userType');
      wx.removeStorageSync('tokenExpire');
      console.log('Login info cleared');
    } catch (e) {
      console.error('Failed to clear login info:', e);
    }
  },

  /**
   * 检查是否已登录
   * @returns {boolean}
   */
  isLoggedIn: function() {
    // 以 userInfo 为准，本地开发时 token 可能为空
    return !!this.globalData.userInfo;
  },

  /**
   * 获取Token
   * @returns {string}
   */
  getToken: function() {
    return this.globalData.token;
  },

  /**
   * 获取用户信息
   * @returns {object|null}
   */
  getUserInfo: function() {
    return this.globalData.userInfo;
  },

  /**
   * 获取用户类型
   * @returns {string}
   */
  getUserType: function() {
    return this.globalData.userType;
  },

  /**
   * 是否为会员
   * @returns {boolean}
   */
  isMember: function() {
    return this.globalData.userType === 'huiyuan';
  },

  /**
   * 需要登录时跳转
   * @param {string} redirectUrl - 登录后跳转的页面，不传则返回当前页
   */
  requireLogin: function(redirectUrl) {
    if (this.isLoggedIn()) {
      return true;
    }
    
    wx.showToast({
      title: '请先登录',
      icon: 'none',
      duration: 1500
    });
    
    const url = redirectUrl ? `/pages/login/login?redirect=${encodeURIComponent(redirectUrl)}` : '/pages/login/login';
    
    setTimeout(() => {
      wx.navigateTo({
        url: url,
        fail: () => {
          // 如果是tabBar页面，使用switchTab
          wx.switchTab({
            url: '/pages/user/index/index'
          });
        }
      });
    }, 1500);
    
    return false;
  },

  /**
   * 监听网络状态变化
   */
  watchNetworkStatus: function() {
    // 获取当前网络状态
    wx.getNetworkType({
      success: (res) => {
        this.globalData.networkType = res.networkType;
        this.globalData.isConnected = res.networkType !== 'none';
        console.log('Network type:', res.networkType);
      }
    });
    
    // 监听网络变化
    wx.onNetworkStatusChange((res) => {
      this.globalData.isConnected = res.isConnected;
      this.globalData.networkType = res.networkType;
      console.log('Network status changed:', res);
      
      if (!res.isConnected) {
        wx.showToast({
          title: '网络已断开',
          icon: 'none',
          duration: 2000
        });
      } else {
        wx.showToast({
          title: '网络已恢复',
          icon: 'none',
          duration: 1500
        });
      }
    });
  },

  /**
   * 检查小程序更新
   */
  checkUpdate: function() {
    if (!wx.canIUse('getUpdateManager')) {
      console.log('Update manager not supported');
      return;
    }
    
    const updateManager = wx.getUpdateManager();
    
    updateManager.onCheckForUpdate((res) => {
      console.log('Check for update:', res.hasUpdate);
    });
    
    updateManager.onUpdateReady(() => {
      wx.showModal({
        title: '更新提示',
        content: '新版本已经准备好，是否重启应用？',
        showCancel: false,
        confirmText: '立即更新',
        success: () => {
          updateManager.applyUpdate();
        }
      });
    });
    
    updateManager.onUpdateFailed(() => {
      wx.showToast({
        title: '更新失败，请删除小程序重新打开',
        icon: 'none',
        duration: 3000
      });
    });
  },

  /**
   * 检查是否首次启动
   */
  checkFirstLaunch: function() {
    try {
      const hasLaunched = wx.getStorageSync('hasLaunched');
      if (!hasLaunched) {
        this.globalData.isFirstLaunch = true;
        wx.setStorageSync('hasLaunched', true);
        console.log('First launch detected');
      }
    } catch (e) {
      console.error('Failed to check first launch:', e);
    }
  },

  /**
   * 处理场景值
   * @param {number} scene - 场景值
   * @param {object} query - 启动参数
   */
  handleScene: function(scene, query) {
    console.log('Handle scene:', scene, query);
    
    // 场景值处理
    switch (scene) {
      case 1007: // 单人聊天会话中的小程序消息卡片
      case 1008: // 群聊会话中的小程序消息卡片
        // 分享进入，可以记录分享来源
        break;
      case 1011: // 扫描二维码
      case 1047: // 扫描小程序码
        // 扫码进入，处理query参数
        if (query && query.roomId) {
          // 跳转到房间详情
          wx.navigateTo({
            url: `/pages/room/detail/detail?id=${query.roomId}`
          });
        }
        break;
      default:
        break;
    }
  },

  /**
   * 获取基础URL
   * @returns {string}
   */
  getBaseUrl: function() {
    return this.globalData.baseUrl;
  },

  /**
   * 设置基础URL（用于切换环境）
   * @param {string} url - 新的基础URL
   */
  setBaseUrl: function(url) {
    this.globalData.baseUrl = url;
    console.log('Base URL changed to:', url);
  },

  /**
   * 显示全局Loading
   * @param {string} title - 提示文字
   */
  showLoading: function(title = '加载中...') {
    wx.showLoading({
      title: title,
      mask: true
    });
  },

  /**
   * 隐藏全局Loading
   */
  hideLoading: function() {
    wx.hideLoading();
  },

  /**
   * 显示Toast提示
   * @param {string} title - 提示文字
   * @param {string} icon - 图标类型
   * @param {number} duration - 显示时长
   */
  showToast: function(title, icon = 'none', duration = 2000) {
    wx.showToast({
      title: title,
      icon: icon,
      duration: duration
    });
  },

  /**
   * 显示确认弹窗
   * @param {object} options - 配置选项
   * @returns {Promise}
   */
  showConfirm: function(options) {
    return new Promise((resolve, reject) => {
      wx.showModal({
        title: options.title || '提示',
        content: options.content || '',
        showCancel: options.showCancel !== false,
        cancelText: options.cancelText || '取消',
        confirmText: options.confirmText || '确定',
        confirmColor: options.confirmColor || '#1989fa',
        success: (res) => {
          if (res.confirm) {
            resolve(true);
          } else {
            resolve(false);
          }
        },
        fail: reject
      });
    });
  }
});
