/**
 * 公共工具函数扩展
 * 提供更丰富的工具方法
 * 创建日期：2026-02-28
 */

var CommonUtils = (function() {
    'use strict';

    // ==================== 日期时间处理 ====================

    /**
     * 日期格式化
     * @param {Date|string|number} date - 日期对象、字符串或时间戳
     * @param {string} format - 格式字符串，默认 'YYYY-MM-DD'
     * @returns {string}
     */
    function formatDate(date, format) {
        format = format || 'YYYY-MM-DD';
        if (!date) return '';
        
        var d = date instanceof Date ? date : new Date(date);
        if (isNaN(d.getTime())) return '';

        var year = d.getFullYear();
        var month = String(d.getMonth() + 1).padStart(2, '0');
        var day = String(d.getDate()).padStart(2, '0');
        var hour = String(d.getHours()).padStart(2, '0');
        var minute = String(d.getMinutes()).padStart(2, '0');
        var second = String(d.getSeconds()).padStart(2, '0');

        return format
            .replace('YYYY', year)
            .replace('MM', month)
            .replace('DD', day)
            .replace('HH', hour)
            .replace('mm', minute)
            .replace('ss', second);
    }

    /**
     * 获取当前日期时间
     * @param {string} format - 格式字符串
     * @returns {string}
     */
    function getNow(format) {
        return formatDate(new Date(), format || 'YYYY-MM-DD HH:mm:ss');
    }

    /**
     * 获取相对时间描述
     * @param {Date|string|number} date - 日期
     * @returns {string}
     */
    function getRelativeTime(date) {
        var d = date instanceof Date ? date : new Date(date);
        var now = new Date();
        var diff = now.getTime() - d.getTime();
        
        var minute = 60 * 1000;
        var hour = 60 * minute;
        var day = 24 * hour;
        var month = 30 * day;
        var year = 365 * day;

        if (diff < minute) {
            return '刚刚';
        } else if (diff < hour) {
            return Math.floor(diff / minute) + '分钟前';
        } else if (diff < day) {
            return Math.floor(diff / hour) + '小时前';
        } else if (diff < month) {
            return Math.floor(diff / day) + '天前';
        } else if (diff < year) {
            return Math.floor(diff / month) + '个月前';
        } else {
            return Math.floor(diff / year) + '年前';
        }
    }

    /**
     * 计算两个日期之间的天数
     * @param {Date|string} start - 开始日期
     * @param {Date|string} end - 结束日期
     * @returns {number}
     */
    function daysBetween(start, end) {
        var startDate = start instanceof Date ? start : new Date(start);
        var endDate = end instanceof Date ? end : new Date(end);
        var diff = endDate.getTime() - startDate.getTime();
        return Math.ceil(diff / (24 * 60 * 60 * 1000));
    }

    // ==================== 金额处理 ====================

    /**
     * 金额格式化
     * @param {number} amount - 金额
     * @param {number} decimals - 小数位数，默认2
     * @returns {string}
     */
    function formatMoney(amount, decimals) {
        if (amount === null || amount === undefined) return '0.00';
        decimals = decimals !== undefined ? decimals : 2;
        return parseFloat(amount).toFixed(decimals);
    }

    /**
     * 金额千分位格式化
     * @param {number} amount - 金额
     * @returns {string}
     */
    function formatMoneyWithComma(amount) {
        if (amount === null || amount === undefined) return '0.00';
        var num = parseFloat(amount).toFixed(2);
        var parts = num.split('.');
        parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',');
        return parts.join('.');
    }

    // ==================== 字符串处理 ====================

    /**
     * 手机号脱敏
     * @param {string} phone - 手机号
     * @returns {string}
     */
    function maskPhone(phone) {
        if (!phone || phone.length !== 11) return phone;
        return phone.substring(0, 3) + '****' + phone.substring(7);
    }

    /**
     * 身份证号脱敏
     * @param {string} idCard - 身份证号
     * @returns {string}
     */
    function maskIdCard(idCard) {
        if (!idCard || idCard.length < 15) return idCard;
        return idCard.substring(0, 6) + '********' + idCard.substring(14);
    }

    /**
     * 姓名脱敏
     * @param {string} name - 姓名
     * @returns {string}
     */
    function maskName(name) {
        if (!name || name.length < 2) return name;
        if (name.length === 2) {
            return name.substring(0, 1) + '*';
        }
        return name.substring(0, 1) + '*'.repeat(name.length - 2) + name.substring(name.length - 1);
    }

    /**
     * 截断字符串
     * @param {string} str - 字符串
     * @param {number} length - 最大长度
     * @param {string} suffix - 后缀，默认 '...'
     * @returns {string}
     */
    function truncate(str, length, suffix) {
        if (!str) return '';
        suffix = suffix !== undefined ? suffix : '...';
        if (str.length <= length) return str;
        return str.substring(0, length) + suffix;
    }

    // ==================== 验证方法 ====================

    /**
     * 验证手机号
     * @param {string} phone - 手机号
     * @returns {boolean}
     */
    function isPhone(phone) {
        return /^1[3-9]\d{9}$/.test(phone);
    }

    /**
     * 验证身份证号
     * @param {string} idCard - 身份证号
     * @returns {boolean}
     */
    function isIdCard(idCard) {
        return /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/.test(idCard);
    }

    /**
     * 验证邮箱
     * @param {string} email - 邮箱
     * @returns {boolean}
     */
    function isEmail(email) {
        return /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email);
    }

    /**
     * 验证URL
     * @param {string} url - URL
     * @returns {boolean}
     */
    function isUrl(url) {
        return /^(https?:\/\/)?([\da-z.-]+)\.([a-z.]{2,6})([\/\w .-]*)*\/?$/.test(url);
    }

    // ==================== UI工具 ====================

    /**
     * 显示Toast提示
     * @param {string} message - 消息内容
     * @param {string} type - 类型: success, error, warning, info
     * @param {number} duration - 显示时长(毫秒)
     */
    function toast(message, type, duration) {
        type = type || 'info';
        duration = duration || 3000;

        // 移除已存在的toast
        var existingToast = document.querySelector('.common-toast');
        if (existingToast) {
            existingToast.remove();
        }

        var colorMap = {
            success: '#52c41a',
            error: '#f5222d',
            warning: '#faad14',
            info: '#1890ff'
        };

        var toastEl = document.createElement('div');
        toastEl.className = 'common-toast';
        toastEl.textContent = message;
        toastEl.style.cssText = [
            'position: fixed',
            'top: 80px',
            'left: 50%',
            'transform: translateX(-50%)',
            'padding: 12px 24px',
            'background-color: ' + (colorMap[type] || colorMap.info),
            'color: #fff',
            'border-radius: 4px',
            'font-size: 14px',
            'z-index: 9999',
            'box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15)',
            'animation: toastFadeIn 0.3s ease'
        ].join(';');

        // 添加动画样式
        if (!document.querySelector('#toast-style')) {
            var style = document.createElement('style');
            style.id = 'toast-style';
            style.textContent = '@keyframes toastFadeIn { from { opacity: 0; transform: translateX(-50%) translateY(-20px); } to { opacity: 1; transform: translateX(-50%) translateY(0); } }';
            document.head.appendChild(style);
        }

        document.body.appendChild(toastEl);

        setTimeout(function() {
            toastEl.style.animation = 'toastFadeIn 0.3s ease reverse';
            setTimeout(function() {
                toastEl.remove();
            }, 300);
        }, duration);
    }

    /**
     * 显示全局Loading
     */
    function showLoading(text) {
        var existingLoading = document.getElementById('global-loading');
        if (existingLoading) {
            existingLoading.style.display = 'flex';
            return;
        }

        var loadingEl = document.createElement('div');
        loadingEl.id = 'global-loading';
        loadingEl.innerHTML = [
            '<div style="position: fixed; top: 0; left: 0; right: 0; bottom: 0; ',
            'background: rgba(255,255,255,0.8); display: flex; flex-direction: column; ',
            'align-items: center; justify-content: center; z-index: 9999;">',
            '<div style="width: 40px; height: 40px; border: 3px solid #f0f0f0; ',
            'border-top-color: #1890ff; border-radius: 50%; animation: spin 0.8s linear infinite;"></div>',
            '<div style="margin-top: 16px; color: #666; font-size: 14px;">' + (text || '加载中...') + '</div>',
            '</div>'
        ].join('');

        if (!document.querySelector('#loading-style')) {
            var style = document.createElement('style');
            style.id = 'loading-style';
            style.textContent = '@keyframes spin { to { transform: rotate(360deg); } }';
            document.head.appendChild(style);
        }

        document.body.appendChild(loadingEl);
    }

    /**
     * 隐藏全局Loading
     */
    function hideLoading() {
        var loadingEl = document.getElementById('global-loading');
        if (loadingEl) {
            loadingEl.style.display = 'none';
        }
    }

    /**
     * 确认对话框
     * @param {string} message - 消息内容
     * @param {string} title - 标题
     * @returns {Promise<boolean>}
     */
    function confirm(message, title) {
        title = title || '提示';
        return new Promise(function(resolve) {
            var result = window.confirm(message);
            resolve(result);
        });
    }

    // ==================== 其他工具 ====================

    /**
     * 获取URL参数
     * @param {string} name - 参数名
     * @returns {string|null}
     */
    function getQueryParam(name) {
        var urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(name);
    }

    /**
     * 设置URL参数
     * @param {string} name - 参数名
     * @param {string} value - 参数值
     */
    function setQueryParam(name, value) {
        var urlParams = new URLSearchParams(window.location.search);
        urlParams.set(name, value);
        var newUrl = window.location.pathname + '?' + urlParams.toString();
        window.history.replaceState(null, '', newUrl);
    }

    /**
     * 防抖函数
     * @param {Function} func - 要执行的函数
     * @param {number} wait - 等待时间
     * @returns {Function}
     */
    function debounce(func, wait) {
        wait = wait || 300;
        var timeout;
        return function() {
            var context = this;
            var args = arguments;
            clearTimeout(timeout);
            timeout = setTimeout(function() {
                func.apply(context, args);
            }, wait);
        };
    }

    /**
     * 节流函数
     * @param {Function} func - 要执行的函数
     * @param {number} wait - 间隔时间
     * @returns {Function}
     */
    function throttle(func, wait) {
        wait = wait || 300;
        var lastTime = 0;
        return function() {
            var now = Date.now();
            if (now - lastTime >= wait) {
                lastTime = now;
                func.apply(this, arguments);
            }
        };
    }

    /**
     * 深拷贝
     * @param {Object} obj - 要拷贝的对象
     * @returns {Object}
     */
    function deepClone(obj) {
        if (obj === null || typeof obj !== 'object') {
            return obj;
        }
        if (obj instanceof Date) {
            return new Date(obj.getTime());
        }
        if (obj instanceof Array) {
            return obj.map(function(item) {
                return deepClone(item);
            });
        }
        if (obj instanceof Object) {
            var copy = {};
            for (var key in obj) {
                if (obj.hasOwnProperty(key)) {
                    copy[key] = deepClone(obj[key]);
                }
            }
            return copy;
        }
        return obj;
    }

    /**
     * 获取图片完整URL
     * @param {string} path - 图片路径
     * @returns {string}
     */
    function getImageUrl(path) {
        if (!path) return '';
        if (path.indexOf('http') === 0) return path;
        return '/springboot6alf1/upload/' + path;
    }

    /**
     * 本地存储封装
     */
    var storage = {
        get: function(key) {
            var value = localStorage.getItem(key);
            try {
                return JSON.parse(value);
            } catch (e) {
                return value;
            }
        },
        set: function(key, value) {
            if (typeof value === 'object') {
                localStorage.setItem(key, JSON.stringify(value));
            } else {
                localStorage.setItem(key, value);
            }
        },
        remove: function(key) {
            localStorage.removeItem(key);
        },
        clear: function() {
            localStorage.clear();
        }
    };

    // 暴露公共方法
    return {
        // 日期时间
        formatDate: formatDate,
        getNow: getNow,
        getRelativeTime: getRelativeTime,
        daysBetween: daysBetween,

        // 金额
        formatMoney: formatMoney,
        formatMoneyWithComma: formatMoneyWithComma,

        // 字符串
        maskPhone: maskPhone,
        maskIdCard: maskIdCard,
        maskName: maskName,
        truncate: truncate,

        // 验证
        isPhone: isPhone,
        isIdCard: isIdCard,
        isEmail: isEmail,
        isUrl: isUrl,

        // UI
        toast: toast,
        showLoading: showLoading,
        hideLoading: hideLoading,
        confirm: confirm,

        // 其他
        getQueryParam: getQueryParam,
        setQueryParam: setQueryParam,
        debounce: debounce,
        throttle: throttle,
        deepClone: deepClone,
        getImageUrl: getImageUrl,
        storage: storage
    };
})();
