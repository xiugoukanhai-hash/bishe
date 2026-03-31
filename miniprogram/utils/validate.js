/**
 * 表单验证工具类
 */

const { VALIDATE_RULES } = require('../config/constants');

/**
 * 验证手机号
 * @param {string} phone - 手机号
 * @returns {boolean}
 */
const isPhone = (phone) => {
  if (!phone) return false;
  return VALIDATE_RULES.PHONE.test(phone);
};

/**
 * 验证身份证号
 * @param {string} idCard - 身份证号
 * @returns {boolean}
 */
const isIdCard = (idCard) => {
  if (!idCard) return false;
  return VALIDATE_RULES.ID_CARD.test(idCard);
};

/**
 * 验证密码（6-20位）
 * @param {string} password - 密码
 * @returns {boolean}
 */
const isPassword = (password) => {
  if (!password) return false;
  return VALIDATE_RULES.PASSWORD.test(password);
};

/**
 * 验证用户名（4-20位字母数字下划线）
 * @param {string} username - 用户名
 * @returns {boolean}
 */
const isUsername = (username) => {
  if (!username) return false;
  return VALIDATE_RULES.USERNAME.test(username);
};

/**
 * 验证邮箱
 * @param {string} email - 邮箱
 * @returns {boolean}
 */
const isEmail = (email) => {
  if (!email) return false;
  return VALIDATE_RULES.EMAIL.test(email);
};

/**
 * 验证是否为空
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
 * 验证是否为数字
 * @param {any} value - 值
 * @returns {boolean}
 */
const isNumber = (value) => {
  if (isEmpty(value)) return false;
  return !isNaN(parseFloat(value)) && isFinite(value);
};

/**
 * 验证是否为正整数
 * @param {any} value - 值
 * @returns {boolean}
 */
const isPositiveInteger = (value) => {
  if (isEmpty(value)) return false;
  const num = parseInt(value);
  return !isNaN(num) && num > 0 && num.toString() === value.toString();
};

/**
 * 验证金额
 * @param {any} value - 值
 * @returns {boolean}
 */
const isMoney = (value) => {
  if (isEmpty(value)) return false;
  return /^\d+(\.\d{1,2})?$/.test(value);
};

/**
 * 验证日期格式（YYYY-MM-DD）
 * @param {string} date - 日期
 * @returns {boolean}
 */
const isDate = (date) => {
  if (!date) return false;
  if (!/^\d{4}-\d{2}-\d{2}$/.test(date)) return false;
  
  const d = new Date(date.replace(/-/g, '/'));
  return !isNaN(d.getTime());
};

/**
 * 验证日期范围
 * @param {string} startDate - 开始日期
 * @param {string} endDate - 结束日期
 * @returns {boolean}
 */
const isValidDateRange = (startDate, endDate) => {
  if (!isDate(startDate) || !isDate(endDate)) return false;
  
  const start = new Date(startDate.replace(/-/g, '/'));
  const end = new Date(endDate.replace(/-/g, '/'));
  
  return start.getTime() < end.getTime();
};

/**
 * 验证URL
 * @param {string} url - URL
 * @returns {boolean}
 */
const isUrl = (url) => {
  if (!url) return false;
  return /^(https?:\/\/)?([\da-z.-]+)\.([a-z.]{2,6})([/\w .-]*)*\/?$/.test(url);
};

/**
 * 验证中文姓名
 * @param {string} name - 姓名
 * @returns {boolean}
 */
const isChineseName = (name) => {
  if (!name) return false;
  return /^[\u4e00-\u9fa5]{2,10}$/.test(name);
};

/**
 * 表单验证器
 * @param {array} rules - 验证规则数组
 * @param {object} data - 待验证数据
 * @returns {object} { valid: boolean, errors: array }
 */
const validate = (rules, data) => {
  const errors = [];
  
  for (let rule of rules) {
    const { field, label, required, type, min, max, pattern, validator, message } = rule;
    const value = data[field];
    
    // 必填验证
    if (required && isEmpty(value)) {
      errors.push({
        field,
        message: message || `请输入${label}`
      });
      continue;
    }
    
    // 非必填且为空则跳过后续验证
    if (!required && isEmpty(value)) continue;
    
    // 类型验证
    if (type) {
      let valid = true;
      let typeMsg = '';
      
      switch (type) {
        case 'phone':
          valid = isPhone(value);
          typeMsg = '请输入正确的手机号';
          break;
        case 'idCard':
          valid = isIdCard(value);
          typeMsg = '请输入正确的身份证号';
          break;
        case 'email':
          valid = isEmail(value);
          typeMsg = '请输入正确的邮箱地址';
          break;
        case 'number':
          valid = isNumber(value);
          typeMsg = '请输入数字';
          break;
        case 'date':
          valid = isDate(value);
          typeMsg = '请输入正确的日期';
          break;
        case 'money':
          valid = isMoney(value);
          typeMsg = '请输入正确的金额';
          break;
        case 'chineseName':
          valid = isChineseName(value);
          typeMsg = '请输入正确的中文姓名';
          break;
      }
      
      if (!valid) {
        errors.push({
          field,
          message: message || typeMsg
        });
        continue;
      }
    }
    
    // 长度验证
    if (min !== undefined && value.length < min) {
      errors.push({
        field,
        message: message || `${label}长度不能小于${min}位`
      });
      continue;
    }
    
    if (max !== undefined && value.length > max) {
      errors.push({
        field,
        message: message || `${label}长度不能大于${max}位`
      });
      continue;
    }
    
    // 正则验证
    if (pattern && !pattern.test(value)) {
      errors.push({
        field,
        message: message || `${label}格式不正确`
      });
      continue;
    }
    
    // 自定义验证器
    if (validator && typeof validator === 'function') {
      const result = validator(value, data);
      if (result !== true) {
        errors.push({
          field,
          message: typeof result === 'string' ? result : (message || `${label}验证失败`)
        });
        continue;
      }
    }
  }
  
  return {
    valid: errors.length === 0,
    errors
  };
};

/**
 * 显示第一个验证错误
 * @param {array} errors - 错误数组
 */
const showFirstError = (errors) => {
  if (errors && errors.length > 0) {
    wx.showToast({
      title: errors[0].message,
      icon: 'none',
      duration: 2000
    });
  }
};

/**
 * 验证并显示错误
 * @param {array} rules - 验证规则
 * @param {object} data - 待验证数据
 * @returns {boolean} 是否验证通过
 */
const validateAndShow = (rules, data) => {
  const result = validate(rules, data);
  if (!result.valid) {
    showFirstError(result.errors);
  }
  return result.valid;
};

module.exports = {
  isPhone,
  isIdCard,
  isPassword,
  isUsername,
  isEmail,
  isEmpty,
  isNumber,
  isPositiveInteger,
  isMoney,
  isDate,
  isValidDateRange,
  isUrl,
  isChineseName,
  validate,
  showFirstError,
  validateAndShow
};
