/**
 * 认证工具类
 */
const { post, get } = require('./request');
const { api } = require('../config/api');

/**
 * 普通用户登录
 */
const userLogin = (username, password) => {
  return new Promise((resolve, reject) => {
    post(api.auth.userLogin, { username, password }, { 
      loading: true,
      loadingText: '登录中...',
      header: { 'Content-Type': 'application/x-www-form-urlencoded' }
    }).then(res => {
      if (res.code === 0) {
        const app = getApp();
        const token = res.token || '';
        const userId = res.id;
        
        // 登录成功后使用 /info/{id} 获取完整用户信息
        get(`/yonghu/info/${userId}`, {}, {
          loading: false,
          header: { 'Token': token }
        }).then(infoRes => {
          if (infoRes.code === 0 && infoRes.data) {
            const userInfo = infoRes.data;
            userInfo.zhanghao = userInfo.zhanghao || username;
            app.setLoginInfo(token, userInfo, 'yonghu');
            resolve({ ...res, userInfo });
          } else {
            // 如果获取info失败，使用登录返回的基本信息
            const basicInfo = { id: userId, zhanghao: username };
            app.setLoginInfo(token, basicInfo, 'yonghu');
            resolve(res);
          }
        }).catch(() => {
          // 获取info失败，使用登录返回的基本信息
          const basicInfo = { id: userId, zhanghao: username };
          app.setLoginInfo(token, basicInfo, 'yonghu');
          resolve(res);
        });
      } else {
        reject(res);
      }
    }).catch(reject);
  });
};

/**
 * 会员登录
 */
const memberLogin = (username, password) => {
  return new Promise((resolve, reject) => {
    post(api.auth.memberLogin, { username, password }, { 
      loading: true,
      loadingText: '登录中...',
      header: { 'Content-Type': 'application/x-www-form-urlencoded' }
    }).then(res => {
      if (res.code === 0) {
        const app = getApp();
        const token = res.token || '';
        const userId = res.id;
        
        // 登录成功后使用 /info/{id} 获取完整用户信息
        get(`/huiyuan/info/${userId}`, {}, {
          loading: false,
          header: { 'Token': token }
        }).then(infoRes => {
          if (infoRes.code === 0 && infoRes.data) {
            const userInfo = infoRes.data;
            userInfo.zhanghao = userInfo.zhanghao || username;
            app.setLoginInfo(token, userInfo, 'huiyuan');
            resolve({ ...res, userInfo });
          } else {
            const basicInfo = { id: userId, zhanghao: username };
            app.setLoginInfo(token, basicInfo, 'huiyuan');
            resolve(res);
          }
        }).catch(() => {
          const basicInfo = { id: userId, zhanghao: username };
          app.setLoginInfo(token, basicInfo, 'huiyuan');
          resolve(res);
        });
      } else {
        reject(res);
      }
    }).catch(reject);
  });
};

/**
 * 用户注册
 */
const userRegister = (data) => {
  return post(api.auth.userRegister, data, {
    loading: true,
    loadingText: '注册中...'
  });
};

/**
 * 会员注册
 */
const memberRegister = (data) => {
  return post(api.auth.memberRegister, data, {
    loading: true,
    loadingText: '注册中...'
  });
};

/**
 * 登出
 */
const logout = () => {
  return new Promise((resolve) => {
    const app = getApp();
    const userType = app.getUserType();
    const url = userType === 'huiyuan' ? api.auth.memberLogout : api.auth.userLogout;
    
    post(url, {}, { loading: false }).finally(() => {
      app.clearLoginInfo();
      wx.showToast({ title: '已退出登录', icon: 'success' });
      resolve({ code: 0 });
    });
  });
};

/**
 * 检查登录状态 (增强版 - 同时检查globalData和localStorage)
 */
const checkLogin = () => {
  const app = getApp();
  if (!app) {
    console.warn('checkLogin: app instance not available');
    return false;
  }
  
  if (app.isLoggedIn && app.isLoggedIn()) {
    console.log('checkLogin: logged in via globalData');
    return true;
  }
  
  try {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    const userType = wx.getStorageSync('userType');
    
    if (token && userInfo && userInfo.id) {
      console.log('checkLogin: restoring login state from localStorage');
      app.globalData.token = token;
      app.globalData.userInfo = userInfo;
      app.globalData.userType = userType;
      return true;
    }
  } catch (e) {
    console.error('checkLogin: error checking localStorage', e);
  }
  
  console.log('checkLogin: not logged in');
  return false;
};

/**
 * 是否为会员 (增强版 - 同时检查globalData和localStorage)
 */
const isMember = () => {
  const app = getApp();
  if (app && app.isMember && app.isMember()) {
    return true;
  }
  
  try {
    const userType = wx.getStorageSync('userType');
    return userType === 'huiyuan';
  } catch (e) {
    return false;
  }
};

/**
 * 需要登录装饰器
 */
const requireLogin = (callback) => {
  return function(...args) {
    if (!checkLogin()) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      setTimeout(() => {
        wx.navigateTo({ url: '/pages/login/login' });
      }, 1500);
      return Promise.reject({ code: -1, msg: '未登录' });
    }
    return callback.apply(this, args);
  };
};

module.exports = {
  userLogin,
  memberLogin,
  userRegister,
  memberRegister,
  logout,
  checkLogin,
  isMember,
  requireLogin
};
