/**
 * 通用工具函数
 */

const { BASE_URL } = require('../config/api');
const { ORDER_STATUS_COLOR, ROOM_STATUS_COLOR } = require('../config/constants');

// ============================================
// 日期时间处理
// ============================================

/**
 * 格式化时间
 * @param {Date|string|number} date - 日期
 * @param {string} format - 格式
 * @returns {string}
 */
const formatTime = (date, format = 'YYYY-MM-DD HH:mm:ss') => {
  if (!date) return '';
  
  // 处理各种日期输入格式
  if (typeof date === 'string') {
    // 处理iOS兼容性问题
    date = new Date(date.replace(/-/g, '/'));
  } else if (typeof date === 'number') {
    date = new Date(date);
  }
  
  if (isNaN(date.getTime())) return '';
  
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hour = date.getHours();
  const minute = date.getMinutes();
  const second = date.getSeconds();
  
  const pad = (n) => n.toString().padStart(2, '0');
  
  return format
    .replace('YYYY', year)
    .replace('MM', pad(month))
    .replace('DD', pad(day))
    .replace('HH', pad(hour))
    .replace('mm', pad(minute))
    .replace('ss', pad(second))
    .replace('M', month)
    .replace('D', day)
    .replace('H', hour)
    .replace('m', minute)
    .replace('s', second);
};

/**
 * 格式化日期（仅日期部分）
 * @param {Date|string|number} date - 日期
 * @returns {string}
 */
const formatDate = (date) => {
  return formatTime(date, 'YYYY-MM-DD');
};

/**
 * 获取今天日期
 * @returns {string}
 */
const getToday = () => {
  return formatDate(new Date());
};

/**
 * 获取明天日期
 * @returns {string}
 */
const getTomorrow = () => {
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  return formatDate(tomorrow);
};

/**
 * 获取N天后的日期
 * @param {number} days - 天数
 * @returns {string}
 */
const getDateAfterDays = (days) => {
  const date = new Date();
  date.setDate(date.getDate() + days);
  return formatDate(date);
};

/**
 * 计算两个日期之间的天数
 * @param {string} startDate - 开始日期
 * @param {string} endDate - 结束日期
 * @returns {number}
 */
const daysBetween = (startDate, endDate) => {
  if (!startDate || !endDate) return 0;
  
  const start = new Date(startDate.replace(/-/g, '/'));
  const end = new Date(endDate.replace(/-/g, '/'));
  const diff = end.getTime() - start.getTime();
  
  return Math.ceil(diff / (1000 * 60 * 60 * 24));
};

/**
 * 获取相对时间描述
 * @param {Date|string|number} date - 日期
 * @returns {string}
 */
const getRelativeTime = (date) => {
  if (!date) return '';
  
  if (typeof date === 'string') {
    date = new Date(date.replace(/-/g, '/'));
  } else if (typeof date === 'number') {
    date = new Date(date);
  }
  
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  const seconds = Math.floor(diff / 1000);
  const minutes = Math.floor(seconds / 60);
  const hours = Math.floor(minutes / 60);
  const days = Math.floor(hours / 24);
  
  if (seconds < 60) return '刚刚';
  if (minutes < 60) return `${minutes}分钟前`;
  if (hours < 24) return `${hours}小时前`;
  if (days < 7) return `${days}天前`;
  if (days < 30) return `${Math.floor(days / 7)}周前`;
  if (days < 365) return `${Math.floor(days / 30)}个月前`;
  return `${Math.floor(days / 365)}年前`;
};

/**
 * 获取星期几
 * @param {Date|string} date - 日期
 * @returns {string}
 */
const getWeekDay = (date) => {
  if (!date) return '';
  
  if (typeof date === 'string') {
    date = new Date(date.replace(/-/g, '/'));
  }
  
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  return weekDays[date.getDay()];
};

// ============================================
// 数字金额处理
// ============================================

/**
 * 格式化金额
 * @param {number|string} amount - 金额
 * @param {number} decimals - 小数位数
 * @returns {string}
 */
const formatMoney = (amount, decimals = 2) => {
  const num = parseFloat(amount);
  if (isNaN(num)) return '0.00';
  return num.toFixed(decimals);
};

/**
 * 金额转大写
 * @param {number} amount - 金额
 * @returns {string}
 */
const amountToChinese = (amount) => {
  const digitUppercase = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'];
  const unit = ['', '拾', '佰', '仟'];
  const unitBig = ['', '万', '亿'];
  
  amount = parseFloat(amount);
  if (isNaN(amount) || amount < 0) return '零元整';
  
  const integerPart = Math.floor(amount);
  const decimalPart = Math.round((amount - integerPart) * 100);
  
  let result = '';
  
  if (integerPart === 0) {
    result = '零';
  } else {
    let numStr = integerPart.toString();
    let zeroFlag = false;
    
    for (let i = 0; i < numStr.length; i++) {
      let digit = parseInt(numStr[i]);
      let pos = numStr.length - i - 1;
      let unitPos = pos % 4;
      let bigUnitPos = Math.floor(pos / 4);
      
      if (digit === 0) {
        zeroFlag = true;
        if (unitPos === 0 && bigUnitPos > 0) {
          result += unitBig[bigUnitPos];
        }
      } else {
        if (zeroFlag) {
          result += '零';
          zeroFlag = false;
        }
        result += digitUppercase[digit] + unit[unitPos];
        if (unitPos === 0) {
          result += unitBig[bigUnitPos];
        }
      }
    }
  }
  
  result += '元';
  
  if (decimalPart === 0) {
    result += '整';
  } else {
    const jiao = Math.floor(decimalPart / 10);
    const fen = decimalPart % 10;
    
    if (jiao > 0) {
      result += digitUppercase[jiao] + '角';
    }
    if (fen > 0) {
      result += digitUppercase[fen] + '分';
    }
  }
  
  return result;
};

/**
 * 千分位格式化
 * @param {number|string} num - 数字
 * @returns {string}
 */
const formatThousands = (num) => {
  const n = parseFloat(num);
  if (isNaN(n)) return '0';
  return n.toLocaleString('zh-CN');
};

// ============================================
// 字符串处理
// ============================================

/**
 * 手机号脱敏
 * @param {string} phone - 手机号
 * @returns {string}
 */
const maskPhone = (phone) => {
  if (!phone || phone.length !== 11) return phone || '';
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
};

/**
 * 身份证脱敏
 * @param {string} idCard - 身份证号
 * @returns {string}
 */
const maskIdCard = (idCard) => {
  if (!idCard || idCard.length < 15) return idCard || '';
  return idCard.replace(/(\d{4})\d+(\d{4})/, '$1**********$2');
};

/**
 * 姓名脱敏
 * @param {string} name - 姓名
 * @returns {string}
 */
const maskName = (name) => {
  if (!name) return '';
  if (name.length <= 1) return name;
  if (name.length === 2) return name[0] + '*';
  return name[0] + '*'.repeat(name.length - 2) + name[name.length - 1];
};

/**
 * 截取字符串
 * @param {string} str - 字符串
 * @param {number} maxLength - 最大长度
 * @param {string} suffix - 后缀
 * @returns {string}
 */
const truncate = (str, maxLength, suffix = '...') => {
  if (!str) return '';
  if (str.length <= maxLength) return str;
  return str.substring(0, maxLength) + suffix;
};

/**
 * 首字母大写
 * @param {string} str - 字符串
 * @returns {string}
 */
const capitalize = (str) => {
  if (!str) return '';
  return str.charAt(0).toUpperCase() + str.slice(1);
};

// ============================================
// 图片URL处理
// ============================================

/**
 * 获取图片完整URL
 * @param {string} path - 图片路径
 * @param {string} defaultImg - 默认图片
 * @returns {string}
 */
const getImageUrl = (path, defaultImg = '/images/default/default.png') => {
  if (!path) return defaultImg;
  if (path.startsWith('http')) return path;
  if (path.startsWith('/')) return path;
  const name = (path + '').replace(/^upload\/+/, '');
  return BASE_URL + '/upload/' + name;
};

/**
 * 处理图片列表
 * @param {string|array} images - 图片字符串或数组
 * @returns {array}
 */
const parseImages = (images) => {
  if (!images) return [];
  if (Array.isArray(images)) {
    return images.map(img => getImageUrl(img));
  }
  if (typeof images === 'string') {
    return images.split(',').filter(Boolean).map(img => getImageUrl(img.trim()));
  }
  return [];
};

// ============================================
// 状态处理
// ============================================

/**
 * 获取订单状态文字
 * @param {string} status - 状态
 * @returns {string}
 */
const getOrderStatusText = (status) => {
  return status || '未知';
};

/**
 * 获取订单状态颜色
 * @param {string} status - 状态
 * @returns {string}
 */
const getOrderStatusColor = (status) => {
  return ORDER_STATUS_COLOR[status] || '#323233';
};

/**
 * 获取房间状态颜色
 * @param {string} status - 状态
 * @returns {string}
 */
const getRoomStatusColor = (status) => {
  return ROOM_STATUS_COLOR[status] || '#323233';
};

// ============================================
// 函数工具
// ============================================

/**
 * 防抖函数
 * @param {function} fn - 函数
 * @param {number} delay - 延迟时间
 * @returns {function}
 */
const debounce = (fn, delay = 300) => {
  let timer = null;
  return function(...args) {
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
      fn.apply(this, args);
      timer = null;
    }, delay);
  };
};

/**
 * 节流函数
 * @param {function} fn - 函数
 * @param {number} delay - 间隔时间
 * @returns {function}
 */
const throttle = (fn, delay = 300) => {
  let lastTime = 0;
  return function(...args) {
    const now = Date.now();
    if (now - lastTime >= delay) {
      lastTime = now;
      fn.apply(this, args);
    }
  };
};

/**
 * 延迟执行
 * @param {number} ms - 毫秒数
 * @returns {Promise}
 */
const sleep = (ms) => {
  return new Promise(resolve => setTimeout(resolve, ms));
};

// ============================================
// 对象处理
// ============================================

/**
 * 深拷贝
 * @param {any} obj - 对象
 * @returns {any}
 */
const deepClone = (obj) => {
  if (obj === null || typeof obj !== 'object') return obj;
  
  if (obj instanceof Date) return new Date(obj.getTime());
  if (obj instanceof RegExp) return new RegExp(obj);
  
  const clone = Array.isArray(obj) ? [] : {};
  for (let key in obj) {
    if (obj.hasOwnProperty(key)) {
      clone[key] = deepClone(obj[key]);
    }
  }
  return clone;
};

/**
 * 对象合并
 * @param {object} target - 目标对象
 * @param  {...object} sources - 源对象
 * @returns {object}
 */
const merge = (target, ...sources) => {
  if (!sources.length) return target;
  const source = sources.shift();
  
  if (isObject(target) && isObject(source)) {
    for (const key in source) {
      if (isObject(source[key])) {
        if (!target[key]) Object.assign(target, { [key]: {} });
        merge(target[key], source[key]);
      } else {
        Object.assign(target, { [key]: source[key] });
      }
    }
  }
  
  return merge(target, ...sources);
};

/**
 * 判断是否为对象
 * @param {any} item - 值
 * @returns {boolean}
 */
const isObject = (item) => {
  return item && typeof item === 'object' && !Array.isArray(item);
};

/**
 * 判断是否为空
 * @param {any} value - 值
 * @returns {boolean}
 */
const isEmpty = (value) => {
  if (value === null || value === undefined) return true;
  if (typeof value === 'string') return value.trim() === '';
  if (Array.isArray(value)) return value.length === 0;
  if (typeof value === 'object') return Object.keys(value).length === 0;
  return false;
};

/**
 * 移除对象空属性
 * @param {object} obj - 对象
 * @returns {object}
 */
const removeEmpty = (obj) => {
  const result = {};
  for (let key in obj) {
    if (obj.hasOwnProperty(key) && !isEmpty(obj[key])) {
      result[key] = obj[key];
    }
  }
  return result;
};

// ============================================
// 数组处理
// ============================================

/**
 * 数组去重
 * @param {array} arr - 数组
 * @param {string} key - 根据key去重
 * @returns {array}
 */
const unique = (arr, key) => {
  if (!Array.isArray(arr)) return [];
  if (!key) return [...new Set(arr)];
  
  const map = new Map();
  return arr.filter(item => {
    if (map.has(item[key])) return false;
    map.set(item[key], true);
    return true;
  });
};

/**
 * 数组分组
 * @param {array} arr - 数组
 * @param {string} key - 分组key
 * @returns {object}
 */
const groupBy = (arr, key) => {
  if (!Array.isArray(arr)) return {};
  return arr.reduce((result, item) => {
    const groupKey = item[key];
    if (!result[groupKey]) {
      result[groupKey] = [];
    }
    result[groupKey].push(item);
    return result;
  }, {});
};

// ============================================
// ID生成
// ============================================

/**
 * 生成订单号
 * @param {string} prefix - 前缀
 * @returns {string}
 */
const generateOrderNo = (prefix = '') => {
  const now = new Date();
  const year = now.getFullYear();
  const month = (now.getMonth() + 1).toString().padStart(2, '0');
  const day = now.getDate().toString().padStart(2, '0');
  const hour = now.getHours().toString().padStart(2, '0');
  const minute = now.getMinutes().toString().padStart(2, '0');
  const second = now.getSeconds().toString().padStart(2, '0');
  const ms = now.getMilliseconds().toString().padStart(3, '0');
  const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0');
  
  return `${prefix}${year}${month}${day}${hour}${minute}${second}${ms}${random}`;
};

/**
 * 生成UUID
 * @returns {string}
 */
const generateUUID = () => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
};

// ============================================
// 页面工具
// ============================================

/**
 * 获取当前页面路径
 * @returns {string}
 */
const getCurrentPagePath = () => {
  const pages = getCurrentPages();
  if (pages.length === 0) return '';
  const currentPage = pages[pages.length - 1];
  return '/' + currentPage.route;
};

/**
 * 获取当前页面带参数URL
 * @returns {string}
 */
const getCurrentPageUrl = () => {
  const pages = getCurrentPages();
  if (pages.length === 0) return '';
  const currentPage = pages[pages.length - 1];
  const options = currentPage.options || {};
  const params = Object.keys(options).map(key => `${key}=${options[key]}`).join('&');
  return '/' + currentPage.route + (params ? '?' + params : '');
};

/**
 * 解析URL参数
 * @param {string} url - URL
 * @returns {object}
 */
const parseUrlParams = (url) => {
  const params = {};
  const queryString = url.split('?')[1];
  if (!queryString) return params;
  
  queryString.split('&').forEach(pair => {
    const [key, value] = pair.split('=');
    if (key) {
      params[decodeURIComponent(key)] = value ? decodeURIComponent(value) : '';
    }
  });
  
  return params;
};

module.exports = {
  formatTime,
  formatDate,
  getToday,
  getTomorrow,
  getDateAfterDays,
  daysBetween,
  getRelativeTime,
  getWeekDay,
  formatMoney,
  amountToChinese,
  formatThousands,
  maskPhone,
  maskIdCard,
  maskName,
  truncate,
  capitalize,
  getImageUrl,
  parseImages,
  getOrderStatusText,
  getOrderStatusColor,
  getRoomStatusColor,
  debounce,
  throttle,
  sleep,
  deepClone,
  merge,
  isObject,
  isEmpty,
  removeEmpty,
  unique,
  groupBy,
  generateOrderNo,
  generateUUID,
  getCurrentPagePath,
  getCurrentPageUrl,
  parseUrlParams
};
