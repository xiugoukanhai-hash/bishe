/**
 * 本地存储工具类
 * 封装wx.storage相关方法
 */

const { STORAGE_KEYS } = require('../config/constants');

/**
 * 同步存储数据
 * @param {string} key - 键名
 * @param {any} value - 值
 * @returns {boolean} 是否成功
 */
const set = (key, value) => {
  try {
    wx.setStorageSync(key, value);
    return true;
  } catch (e) {
    console.error('Storage set failed:', key, e);
    return false;
  }
};

/**
 * 同步获取数据
 * @param {string} key - 键名
 * @param {any} defaultValue - 默认值
 * @returns {any}
 */
const get = (key, defaultValue = null) => {
  try {
    const value = wx.getStorageSync(key);
    return value !== '' && value !== undefined ? value : defaultValue;
  } catch (e) {
    console.error('Storage get failed:', key, e);
    return defaultValue;
  }
};

/**
 * 同步删除数据
 * @param {string} key - 键名
 * @returns {boolean} 是否成功
 */
const remove = (key) => {
  try {
    wx.removeStorageSync(key);
    return true;
  } catch (e) {
    console.error('Storage remove failed:', key, e);
    return false;
  }
};

/**
 * 清空所有数据
 * @returns {boolean} 是否成功
 */
const clear = () => {
  try {
    wx.clearStorageSync();
    return true;
  } catch (e) {
    console.error('Storage clear failed:', e);
    return false;
  }
};

/**
 * 获取存储信息
 * @returns {object}
 */
const getInfo = () => {
  try {
    return wx.getStorageInfoSync();
  } catch (e) {
    console.error('Storage getInfo failed:', e);
    return { keys: [], currentSize: 0, limitSize: 0 };
  }
};

/**
 * 检查键是否存在
 * @param {string} key - 键名
 * @returns {boolean}
 */
const has = (key) => {
  try {
    const info = getInfo();
    return info.keys.includes(key);
  } catch (e) {
    return false;
  }
};

/**
 * 获取所有键名
 * @returns {array}
 */
const keys = () => {
  try {
    const info = getInfo();
    return info.keys || [];
  } catch (e) {
    return [];
  }
};

/**
 * 异步存储数据
 * @param {string} key - 键名
 * @param {any} value - 值
 * @returns {Promise}
 */
const setAsync = (key, value) => {
  return new Promise((resolve, reject) => {
    wx.setStorage({
      key,
      data: value,
      success: () => resolve(true),
      fail: (e) => {
        console.error('Storage setAsync failed:', key, e);
        reject(e);
      }
    });
  });
};

/**
 * 异步获取数据
 * @param {string} key - 键名
 * @param {any} defaultValue - 默认值
 * @returns {Promise}
 */
const getAsync = (key, defaultValue = null) => {
  return new Promise((resolve, reject) => {
    wx.getStorage({
      key,
      success: (res) => resolve(res.data),
      fail: () => resolve(defaultValue)
    });
  });
};

/**
 * 异步删除数据
 * @param {string} key - 键名
 * @returns {Promise}
 */
const removeAsync = (key) => {
  return new Promise((resolve, reject) => {
    wx.removeStorage({
      key,
      success: () => resolve(true),
      fail: (e) => {
        console.error('Storage removeAsync failed:', key, e);
        reject(e);
      }
    });
  });
};

// ============================================
// 业务相关存储方法
// ============================================

/**
 * 保存搜索历史
 * @param {string} keyword - 搜索关键词
 * @param {number} maxLength - 最大保存数量
 */
const saveSearchHistory = (keyword, maxLength = 10) => {
  if (!keyword || !keyword.trim()) return;
  
  let history = get(STORAGE_KEYS.SEARCH_HISTORY, []);
  
  // 去重并放到最前面
  history = history.filter(item => item !== keyword);
  history.unshift(keyword);
  
  // 限制数量
  if (history.length > maxLength) {
    history = history.slice(0, maxLength);
  }
  
  set(STORAGE_KEYS.SEARCH_HISTORY, history);
};

/**
 * 获取搜索历史
 * @returns {array}
 */
const getSearchHistory = () => {
  return get(STORAGE_KEYS.SEARCH_HISTORY, []);
};

/**
 * 清空搜索历史
 */
const clearSearchHistory = () => {
  remove(STORAGE_KEYS.SEARCH_HISTORY);
};

/**
 * 保存最近浏览的房间
 * @param {object} room - 房间信息
 * @param {number} maxLength - 最大保存数量
 */
const saveRecentRoom = (room, maxLength = 20) => {
  if (!room || !room.id) return;
  
  let recentRooms = get(STORAGE_KEYS.RECENT_ROOMS, []);
  
  // 去重
  recentRooms = recentRooms.filter(item => item.id !== room.id);
  
  // 添加到最前面
  recentRooms.unshift({
    id: room.id,
    kefanghao: room.kefanghao,
    kefangleixing: room.kefangleixing,
    jiage: room.jiage,
    tupian: room.tupian,
    viewTime: Date.now()
  });
  
  // 限制数量
  if (recentRooms.length > maxLength) {
    recentRooms = recentRooms.slice(0, maxLength);
  }
  
  set(STORAGE_KEYS.RECENT_ROOMS, recentRooms);
};

/**
 * 获取最近浏览的房间
 * @returns {array}
 */
const getRecentRooms = () => {
  return get(STORAGE_KEYS.RECENT_ROOMS, []);
};

/**
 * 清空最近浏览
 */
const clearRecentRooms = () => {
  remove(STORAGE_KEYS.RECENT_ROOMS);
};

/**
 * 保存带过期时间的数据
 * @param {string} key - 键名
 * @param {any} value - 值
 * @param {number} expireTime - 过期时间（毫秒）
 */
const setWithExpire = (key, value, expireTime) => {
  const data = {
    value,
    expire: Date.now() + expireTime
  };
  set(key, data);
};

/**
 * 获取带过期时间的数据
 * @param {string} key - 键名
 * @param {any} defaultValue - 默认值
 * @returns {any}
 */
const getWithExpire = (key, defaultValue = null) => {
  const data = get(key);
  
  if (!data) return defaultValue;
  
  // 检查是否为带过期时间的数据格式
  if (data.expire !== undefined && data.value !== undefined) {
    if (Date.now() > data.expire) {
      // 已过期，删除并返回默认值
      remove(key);
      return defaultValue;
    }
    return data.value;
  }
  
  return data;
};

// 导出存储键名常量
module.exports = {
  set,
  get,
  remove,
  clear,
  getInfo,
  has,
  keys,
  setAsync,
  getAsync,
  removeAsync,
  saveSearchHistory,
  getSearchHistory,
  clearSearchHistory,
  saveRecentRoom,
  getRecentRooms,
  clearRecentRooms,
  setWithExpire,
  getWithExpire,
  KEYS: STORAGE_KEYS
};
