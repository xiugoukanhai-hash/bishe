/**
 * 调试工具函数
 * 用于诊断和排查小程序问题
 */

const debugUtils = {
  /**
   * 打印完整的登录状态诊断信息
   */
  printLoginDiagnostics: function() {
    console.log('\n======== 登录状态诊断 ========');
    
    const app = getApp();
    
    // 1. 检查 app 实例
    console.log('1. App实例:', app ? '✓ 存在' : '✗ 不存在');
    
    if (!app) {
      console.log('  错误: 无法获取 App 实例');
      return;
    }
    
    // 2. 检查 globalData
    console.log('2. GlobalData:');
    console.log('  - token:', app.globalData.token ? 
      '✓ 存在 (' + app.globalData.token.substring(0, 20) + '...)' : '✗ 空');
    console.log('  - userInfo:', app.globalData.userInfo);
    console.log('  - userType:', app.globalData.userType);
    
    // 3. 检查 localStorage
    console.log('3. LocalStorage:');
    try {
      const lsToken = wx.getStorageSync('token');
      const lsUserInfo = wx.getStorageSync('userInfo');
      const lsUserType = wx.getStorageSync('userType');
      console.log('  - token:', lsToken ? '✓ 存在' : '✗ 空');
      console.log('  - userInfo:', lsUserInfo);
      console.log('  - userType:', lsUserType);
    } catch (e) {
      console.log('  错误: 读取 localStorage 失败', e);
    }
    
    // 4. 检查 isLoggedIn 函数
    console.log('4. isLoggedIn():', app.isLoggedIn ? app.isLoggedIn() : '函数不存在');
    
    // 5. 检查 auth.js 的 checkLogin
    try {
      const { checkLogin } = require('./auth');
      console.log('5. checkLogin():', checkLogin());
    } catch (e) {
      console.log('5. checkLogin(): 导入失败', e);
    }
    
    console.log('================================\n');
  },
  
  /**
   * 打印当前页面栈信息
   */
  printPageStack: function() {
    console.log('\n======== 页面栈信息 ========');
    const pages = getCurrentPages();
    pages.forEach((page, index) => {
      console.log(`${index}: ${page.route}`, page.options);
    });
    console.log('============================\n');
  },
  
  /**
   * 清除所有可能的UI覆盖
   */
  clearAllUI: function() {
    console.log('正在清除所有UI覆盖...');
    try {
      wx.hideLoading();
      wx.hideToast();
      wx.hideNavigationBarLoading();
      console.log('✓ UI清除完成');
    } catch (e) {
      console.log('UI清除时出错:', e);
    }
  },
  
  /**
   * 强制刷新登录状态
   */
  refreshLoginState: function() {
    console.log('正在刷新登录状态...');
    const app = getApp();
    
    try {
      const token = wx.getStorageSync('token');
      const userInfo = wx.getStorageSync('userInfo');
      const userType = wx.getStorageSync('userType');
      
      if (token && userInfo && userInfo.id) {
        app.globalData.token = token;
        app.globalData.userInfo = userInfo;
        app.globalData.userType = userType;
        console.log('✓ 登录状态已从 localStorage 恢复');
        return true;
      } else {
        console.log('✗ localStorage 中没有有效的登录信息');
        return false;
      }
    } catch (e) {
      console.log('刷新登录状态时出错:', e);
      return false;
    }
  },
  
  /**
   * 完全登出并清除所有数据
   */
  forceLogout: function() {
    console.log('正在强制登出...');
    const app = getApp();
    
    // 清除 globalData
    app.globalData.token = '';
    app.globalData.userInfo = null;
    app.globalData.userType = '';
    
    // 清除 localStorage
    try {
      wx.removeStorageSync('token');
      wx.removeStorageSync('userInfo');
      wx.removeStorageSync('userType');
      console.log('✓ 所有登录数据已清除');
    } catch (e) {
      console.log('清除数据时出错:', e);
    }
  },
  
  /**
   * 运行完整诊断
   */
  runFullDiagnostics: function() {
    console.log('\n\n========================================');
    console.log('       小程序完整诊断报告');
    console.log('========================================\n');
    
    this.printLoginDiagnostics();
    this.printPageStack();
    
    console.log('诊断提示:');
    console.log('- 如果登录状态不一致，请调用 refreshLoginState()');
    console.log('- 如果UI卡住，请调用 clearAllUI()');
    console.log('- 如果需要重新登录，请调用 forceLogout()');
    console.log('\n========================================\n');
  }
};

module.exports = debugUtils;
