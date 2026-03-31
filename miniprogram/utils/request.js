/**
 * 网络请求封装
 * 统一处理请求/响应/错误
 */

const { BASE_URL } = require('../config/api');

// 请求队列（用于防止重复请求）
const requestQueue = new Map();

// 请求拦截器
const requestInterceptors = [];

// 响应拦截器
const responseInterceptors = [];

/**
 * 添加请求拦截器
 * @param {function} interceptor - 拦截器函数
 */
const addRequestInterceptor = (interceptor) => {
  requestInterceptors.push(interceptor);
};

/**
 * 添加响应拦截器
 * @param {function} interceptor - 拦截器函数
 */
const addResponseInterceptor = (interceptor) => {
  responseInterceptors.push(interceptor);
};

/**
 * 生成请求唯一标识
 * @param {object} options - 请求选项
 * @returns {string}
 */
const generateRequestKey = (options) => {
  return `${options.method || 'GET'}_${options.url}_${JSON.stringify(options.data || {})}`;
};

/**
 * 核心请求函数
 * @param {object} options - 请求配置
 * @returns {Promise}
 */
const request = (options) => {
  return new Promise((resolve, reject) => {
    const app = getApp();
    
    // 默认配置
    const defaultOptions = {
      method: 'GET',
      data: {},
      header: {
        'Content-Type': 'application/json'
      },
      timeout: 30000,
      loading: true,
      loadingText: '加载中...',
      showError: true,
      retry: 0,
      retryDelay: 1000,
      preventRepeat: false
    };
    
    // 合并配置
    options = { ...defaultOptions, ...options };
    
    // 处理URL
    if (!options.url.startsWith('http')) {
      options.url = BASE_URL + options.url;
    }
    
    // 添加Token
    const token = app ? app.getToken() : wx.getStorageSync('token');
    if (token) {
      options.header['Token'] = token;
    }
    
    // 执行请求拦截器
    for (let interceptor of requestInterceptors) {
      options = interceptor(options) || options;
    }
    
    // 防止重复请求
    if (options.preventRepeat) {
      const requestKey = generateRequestKey(options);
      if (requestQueue.has(requestKey)) {
        console.log('Duplicate request prevented:', requestKey);
        reject({ code: -2, msg: '请勿重复请求' });
        return;
      }
      requestQueue.set(requestKey, true);
    }
    
    // 显示Loading
    if (options.loading) {
      wx.showLoading({
        title: options.loadingText,
        mask: true
      });
    }
    
    // 发起请求
    const requestTask = wx.request({
      url: options.url,
      method: options.method,
      data: options.data,
      header: options.header,
      timeout: options.timeout,
      
      success: (res) => {
        // 隐藏Loading
        if (options.loading) {
          wx.hideLoading();
        }
        
        // 清除请求队列
        if (options.preventRepeat) {
          requestQueue.delete(generateRequestKey(options));
        }
        
        // HTTP状态码判断
        if (res.statusCode >= 200 && res.statusCode < 300) {
          let data = res.data;
          
          // 执行响应拦截器
          for (let interceptor of responseInterceptors) {
            data = interceptor(data, res) || data;
          }
          
          // 业务状态码判断
          if (data.code === 0 || data.code === 200) {
            resolve(data);
          } else if (data.code === 401 || data.code === 403) {
            // Token失效
            handleAuthError(app, options);
            reject(data);
          } else {
            // 业务错误
            if (options.showError) {
              wx.showToast({
                title: data.msg || '请求失败',
                icon: 'none',
                duration: 2500
              });
            }
            reject(data);
          }
        } else if (res.statusCode === 401 || res.statusCode === 403) {
          // 未授权
          handleAuthError(app, options);
          reject({ code: res.statusCode, msg: '未授权访问' });
        } else if (res.statusCode === 404) {
          if (options.showError) {
            wx.showToast({
              title: '请求的资源不存在',
              icon: 'none'
            });
          }
          reject({ code: 404, msg: '资源不存在' });
        } else if (res.statusCode >= 500) {
          if (options.showError) {
            wx.showToast({
              title: '服务器繁忙，请稍后重试',
              icon: 'none'
            });
          }
          reject({ code: res.statusCode, msg: '服务器错误' });
        } else {
          if (options.showError) {
            wx.showToast({
              title: `请求失败(${res.statusCode})`,
              icon: 'none'
            });
          }
          reject({ code: res.statusCode, msg: '请求失败' });
        }
      },
      
      fail: (err) => {
        // 隐藏Loading
        if (options.loading) {
          wx.hideLoading();
        }
        
        // 清除请求队列
        if (options.preventRepeat) {
          requestQueue.delete(generateRequestKey(options));
        }
        
        console.error('Request failed:', err);
        
        // 重试机制
        if (options.retry > 0) {
          console.log(`Retrying request, ${options.retry} attempts left`);
          setTimeout(() => {
            request({ ...options, retry: options.retry - 1 })
              .then(resolve)
              .catch(reject);
          }, options.retryDelay);
          return;
        }
        
        // 网络错误提示
        let errorMsg = '网络连接失败';
        if (err.errMsg) {
          if (err.errMsg.includes('timeout')) {
            errorMsg = '请求超时，请检查网络';
          } else if (err.errMsg.includes('fail')) {
            errorMsg = '网络异常，请检查网络设置';
          }
        }
        
        if (options.showError) {
          wx.showToast({
            title: errorMsg,
            icon: 'none',
            duration: 2500
          });
        }
        
        reject({ code: -1, msg: errorMsg, error: err });
      }
    });
    
    // 返回请求任务，支持取消
    if (options.getTask) {
      options.getTask(requestTask);
    }
  });
};

/**
 * 处理认证错误
 * @param {object} app - App实例
 * @param {object} options - 请求选项
 */
const handleAuthError = (app, options) => {
  if (app) {
    app.clearLoginInfo();
  } else {
    wx.removeStorageSync('token');
    wx.removeStorageSync('userInfo');
    wx.removeStorageSync('userType');
  }
  
  wx.showToast({
    title: '登录已过期，请重新登录',
    icon: 'none',
    duration: 2000
  });
  
  // 延迟跳转，让用户看到提示
  setTimeout(() => {
    const pages = getCurrentPages();
    const currentPage = pages[pages.length - 1];
    const currentRoute = currentPage ? currentPage.route : '';
    
    // 避免在登录页重复跳转
    if (!currentRoute.includes('login')) {
      wx.navigateTo({
        url: '/pages/login/login'
      });
    }
  }, 2000);
};

/**
 * GET请求
 * @param {string} url - 请求地址
 * @param {object} data - 请求参数
 * @param {object} options - 额外配置
 * @returns {Promise}
 */
const get = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'GET',
    data,
    ...options
  });
};

/**
 * POST请求
 * @param {string} url - 请求地址
 * @param {object} data - 请求数据
 * @param {object} options - 额外配置
 * @returns {Promise}
 */
const post = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'POST',
    data,
    ...options
  });
};

/**
 * PUT请求
 * @param {string} url - 请求地址
 * @param {object} data - 请求数据
 * @param {object} options - 额外配置
 * @returns {Promise}
 */
const put = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'PUT',
    data,
    ...options
  });
};

/**
 * DELETE请求
 * @param {string} url - 请求地址
 * @param {object} data - 请求数据
 * @param {object} options - 额外配置
 * @returns {Promise}
 */
const del = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'DELETE',
    data,
    ...options
  });
};

/**
 * 文件上传
 * @param {string} filePath - 本地文件路径
 * @param {object} formData - 额外表单数据
 * @param {object} options - 额外配置
 * @returns {Promise}
 */
const upload = (filePath, formData = {}, options = {}) => {
  return new Promise((resolve, reject) => {
    const app = getApp();
    const token = app ? app.getToken() : wx.getStorageSync('token');
    
    // 显示上传进度
    wx.showLoading({
      title: options.loadingText || '上传中...',
      mask: true
    });
    
    const uploadTask = wx.uploadFile({
      url: BASE_URL + (options.url || '/file/upload'),
      filePath: filePath,
      name: options.name || 'file',
      formData: formData,
      header: {
        'Token': token || ''
      },
      
      success: (res) => {
        wx.hideLoading();
        
        if (res.statusCode === 200) {
          try {
            const data = JSON.parse(res.data);
            if (data.code === 0 || data.code === 200) {
              resolve(data);
            } else {
              wx.showToast({
                title: data.msg || '上传失败',
                icon: 'none'
              });
              reject(data);
            }
          } catch (e) {
            wx.showToast({
              title: '上传失败',
              icon: 'none'
            });
            reject({ code: -1, msg: '解析响应失败' });
          }
        } else {
          wx.showToast({
            title: '上传失败',
            icon: 'none'
          });
          reject({ code: res.statusCode, msg: '上传失败' });
        }
      },
      
      fail: (err) => {
        wx.hideLoading();
        console.error('Upload failed:', err);
        wx.showToast({
          title: '上传失败，请重试',
          icon: 'none'
        });
        reject({ code: -1, msg: '上传失败', error: err });
      }
    });
    
    // 上传进度回调
    if (options.onProgress) {
      uploadTask.onProgressUpdate((res) => {
        options.onProgress(res.progress, res.totalBytesSent, res.totalBytesExpectedToSend);
      });
    }
    
    // 返回上传任务，支持取消
    if (options.getTask) {
      options.getTask(uploadTask);
    }
  });
};

/**
 * 下载文件
 * @param {string} url - 文件URL
 * @param {object} options - 额外配置
 * @returns {Promise}
 */
const download = (url, options = {}) => {
  return new Promise((resolve, reject) => {
    if (!url.startsWith('http')) {
      url = BASE_URL + url;
    }
    
    wx.showLoading({
      title: options.loadingText || '下载中...',
      mask: true
    });
    
    const downloadTask = wx.downloadFile({
      url: url,
      header: options.header || {},
      
      success: (res) => {
        wx.hideLoading();
        
        if (res.statusCode === 200) {
          resolve({
            tempFilePath: res.tempFilePath,
            statusCode: res.statusCode
          });
        } else {
          wx.showToast({
            title: '下载失败',
            icon: 'none'
          });
          reject({ code: res.statusCode, msg: '下载失败' });
        }
      },
      
      fail: (err) => {
        wx.hideLoading();
        console.error('Download failed:', err);
        wx.showToast({
          title: '下载失败，请重试',
          icon: 'none'
        });
        reject({ code: -1, msg: '下载失败', error: err });
      }
    });
    
    // 下载进度回调
    if (options.onProgress) {
      downloadTask.onProgressUpdate((res) => {
        options.onProgress(res.progress, res.totalBytesWritten, res.totalBytesExpectedToWrite);
      });
    }
    
    // 返回下载任务，支持取消
    if (options.getTask) {
      options.getTask(downloadTask);
    }
  });
};

module.exports = {
  request,
  get,
  post,
  put,
  del,
  upload,
  download,
  addRequestInterceptor,
  addResponseInterceptor
};
