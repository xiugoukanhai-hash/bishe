/**
 * API请求封装
 * 统一管理前端API调用
 * 创建日期：2026-02-28
 */

var API = (function() {
    'use strict';

    // API基础配置
    var config = {
        baseUrl: '/springboot6alf1',
        timeout: 30000,
        tokenKey: 'Token'
    };

    /**
     * 获取存储的Token
     */
    function getToken() {
        return localStorage.getItem(config.tokenKey) || '';
    }

    /**
     * 设置Token
     */
    function setToken(token) {
        localStorage.setItem(config.tokenKey, token);
    }

    /**
     * 清除Token
     */
    function clearToken() {
        localStorage.removeItem(config.tokenKey);
    }

    /**
     * 基础请求方法
     * @param {string} url - 请求地址
     * @param {Object} options - 请求选项
     * @returns {Promise}
     */
    function request(url, options) {
        options = options || {};
        var method = options.method || 'GET';
        var data = options.data;
        var headers = options.headers || {};

        // 添加Token到请求头
        var token = getToken();
        if (token) {
            headers[config.tokenKey] = token;
        }

        // 构建完整URL
        var fullUrl = url.indexOf('http') === 0 ? url : config.baseUrl + url;

        // 处理GET请求参数
        if (method === 'GET' && data) {
            var params = [];
            for (var key in data) {
                if (data.hasOwnProperty(key) && data[key] !== undefined && data[key] !== null && data[key] !== '') {
                    params.push(encodeURIComponent(key) + '=' + encodeURIComponent(data[key]));
                }
            }
            if (params.length > 0) {
                fullUrl += (fullUrl.indexOf('?') === -1 ? '?' : '&') + params.join('&');
            }
        }

        return new Promise(function(resolve, reject) {
            var xhr = new XMLHttpRequest();
            xhr.open(method, fullUrl, true);
            xhr.timeout = config.timeout;

            // 设置请求头
            if (method !== 'GET') {
                xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
            }
            for (var headerKey in headers) {
                if (headers.hasOwnProperty(headerKey)) {
                    xhr.setRequestHeader(headerKey, headers[headerKey]);
                }
            }

            xhr.onload = function() {
                if (xhr.status >= 200 && xhr.status < 300) {
                    try {
                        var response = JSON.parse(xhr.responseText);
                        if (response.code === 0) {
                            resolve(response);
                        } else if (response.code === 401) {
                            // Token失效
                            clearToken();
                            localStorage.removeItem('userTable');
                            localStorage.removeItem('userid');
                            window.location.href = './pages/login/login.html';
                            reject(new Error('登录已过期，请重新登录'));
                        } else {
                            reject(new Error(response.msg || '请求失败'));
                        }
                    } catch (e) {
                        reject(new Error('数据解析错误'));
                    }
                } else {
                    reject(new Error('请求失败：' + xhr.status));
                }
            };

            xhr.onerror = function() {
                reject(new Error('网络错误'));
            };

            xhr.ontimeout = function() {
                reject(new Error('请求超时'));
            };

            // 发送请求
            if (method !== 'GET' && data) {
                xhr.send(JSON.stringify(data));
            } else {
                xhr.send();
            }
        });
    }

    /**
     * GET请求
     */
    function get(url, params) {
        return request(url, { method: 'GET', data: params });
    }

    /**
     * POST请求
     */
    function post(url, data) {
        return request(url, { method: 'POST', data: data });
    }

    /**
     * PUT请求
     */
    function put(url, data) {
        return request(url, { method: 'PUT', data: data });
    }

    /**
     * DELETE请求
     */
    function del(url, data) {
        return request(url, { method: 'DELETE', data: data });
    }

    // ==================== 用户相关API ====================

    /**
     * 用户登录
     * @param {string} table - 用户表名
     * @param {Object} data - 登录数据 {username, password}
     */
    function login(table, data) {
        return post('/' + table + '/login', data);
    }

    /**
     * 用户注册
     * @param {string} table - 用户表名
     * @param {Object} data - 注册数据
     */
    function register(table, data) {
        return post('/' + table + '/register', data);
    }

    /**
     * 获取当前用户信息
     * @param {string} table - 用户表名
     */
    function getUserInfo(table) {
        return get('/' + table + '/session');
    }

    /**
     * 修改密码
     * @param {string} table - 用户表名
     * @param {Object} data - {oldPassword, newPassword}
     */
    function updatePassword(table, data) {
        return post('/' + table + '/updatePassword', data);
    }

    /**
     * 退出登录
     * @param {string} table - 用户表名
     */
    function logout(table) {
        return post('/' + table + '/logout').finally(function() {
            clearToken();
            localStorage.removeItem('userTable');
            localStorage.removeItem('userid');
        });
    }

    // ==================== 客房相关API ====================

    /**
     * 获取客房列表
     */
    function getRoomList(params) {
        return get('/kefangxinxi/page', params);
    }

    /**
     * 获取客房详情
     */
    function getRoomDetail(id) {
        return get('/kefangxinxi/detail/' + id);
    }

    /**
     * 获取推荐客房
     */
    function getRecommendRooms(params) {
        return get('/kefangxinxi/autoSort', params);
    }

    /**
     * 搜索客房
     */
    function searchRooms(params) {
        return get('/kefangxinxi/search', params);
    }

    /**
     * 获取房型列表
     */
    function getRoomTypes() {
        return get('/kefangxinxi/roomTypes');
    }

    /**
     * 检查客房是否可预订
     */
    function canBookRoom(kefanghao) {
        return get('/kefangxinxi/canBook/' + kefanghao);
    }

    /**
     * 获取客房状态统计
     */
    function getRoomStatistics() {
        return get('/kefangxinxi/statusStatistics');
    }

    // ==================== 预约相关API ====================

    /**
     * 创建预约
     * @param {string} type - 用户类型 (yonghu/huiyuan)
     * @param {Object} data - 预约数据
     */
    function createBooking(type, data) {
        return post('/' + type + 'yuyue/add', data);
    }

    /**
     * 获取我的预约列表
     * @param {string} type - 用户类型
     */
    function getMyBookings(type, params) {
        return get('/' + type + 'yuyue/page', params);
    }

    /**
     * 取消预约
     * @param {string} type - 用户类型
     * @param {Object} data - 取消数据
     */
    function cancelBooking(type, data) {
        return post('/' + type + 'quxiao/add', data);
    }

    /**
     * 支付预约
     * @param {string} type - 用户类型
     * @param {number} id - 预约ID
     */
    function payBooking(type, id) {
        return post('/' + type + 'yuyue/pay/' + id);
    }

    /**
     * 计算预约价格
     * @param {string} type - 用户类型
     * @param {Object} data - {kefanghao, tianshu}
     */
    function calculatePrice(type, data) {
        return post('/' + type + 'yuyue/calcPrice', data);
    }

    /**
     * 获取我的预约列表（新接口）
     * @param {string} type - 用户类型
     */
    function getMyBookingList(type, params) {
        return get('/' + type + 'yuyue/mylist', params);
    }

    // ==================== AI客服相关API ====================

    /**
     * 智能问答
     * @param {string} question - 问题内容
     */
    function askAi(question) {
        return post('/aikefu/ask', { question: question });
    }

    /**
     * 获取热门问题
     */
    function getHotQuestions() {
        return get('/aikefu/hotQuestions');
    }

    /**
     * 获取问候语
     */
    function getGreeting() {
        return get('/aikefu/greeting');
    }

    /**
     * 发送客服消息
     */
    function sendChatMessage(data) {
        return post('/chat/add', data);
    }

    /**
     * 获取聊天记录
     */
    function getChatHistory(params) {
        return get('/chat/page', params);
    }

    // ==================== 通知相关API ====================

    /**
     * 获取我的通知
     */
    function getMyNotifications(params) {
        return get('/tongzhi/page', params);
    }

    /**
     * 获取未读通知数量
     */
    function getUnreadCount() {
        return get('/tongzhi/unreadCount');
    }

    /**
     * 标记通知已读
     */
    function markNotificationRead(id) {
        return post('/tongzhi/read/' + id);
    }

    /**
     * 标记所有通知已读
     */
    function markAllNotificationsRead() {
        return post('/tongzhi/readAll');
    }

    // ==================== 收藏相关API ====================

    /**
     * 添加收藏
     */
    function addFavorite(data) {
        return post('/storeup/add', data);
    }

    /**
     * 获取我的收藏
     */
    function getMyFavorites(params) {
        return get('/storeup/page', params);
    }

    /**
     * 取消收藏
     */
    function removeFavorite(ids) {
        return post('/storeup/delete', ids);
    }

    /**
     * 检查是否已收藏
     */
    function checkFavorite(params) {
        return get('/storeup/check', params);
    }

    // ==================== 入住/退房相关API ====================

    /**
     * 获取入住记录
     */
    function getCheckInList(type, params) {
        return get('/' + type + 'ruzhu/page', params);
    }

    /**
     * 获取退房记录
     */
    function getCheckOutList(type, params) {
        return get('/' + type + 'tuifang/page', params);
    }

    // ==================== 评价相关API ====================

    /**
     * 提交评价
     */
    function submitReview(data) {
        return post('/pinglun/add', data);
    }

    /**
     * 获取评价列表
     */
    function getReviews(params) {
        return get('/pinglun/page', params);
    }

    // ==================== 配置相关API ====================

    /**
     * 获取系统配置
     */
    function getConfig(name) {
        return get('/config/info/' + name);
    }

    /**
     * 获取轮播图
     */
    function getBanners() {
        return get('/config/list');
    }

    // 暴露公共方法
    return {
        // 基础方法
        request: request,
        get: get,
        post: post,
        put: put,
        delete: del,

        // Token管理
        getToken: getToken,
        setToken: setToken,
        clearToken: clearToken,

        // 用户相关
        login: login,
        register: register,
        getUserInfo: getUserInfo,
        updatePassword: updatePassword,
        logout: logout,

        // 客房相关
        getRoomList: getRoomList,
        getRoomDetail: getRoomDetail,
        getRecommendRooms: getRecommendRooms,
        searchRooms: searchRooms,
        getRoomTypes: getRoomTypes,
        canBookRoom: canBookRoom,
        getRoomStatistics: getRoomStatistics,

        // 预约相关
        createBooking: createBooking,
        getMyBookings: getMyBookings,
        getMyBookingList: getMyBookingList,
        cancelBooking: cancelBooking,
        payBooking: payBooking,
        calculatePrice: calculatePrice,

        // AI客服
        askAi: askAi,
        getHotQuestions: getHotQuestions,
        getGreeting: getGreeting,
        sendChatMessage: sendChatMessage,
        getChatHistory: getChatHistory,

        // 通知相关
        getMyNotifications: getMyNotifications,
        getUnreadCount: getUnreadCount,
        markNotificationRead: markNotificationRead,
        markAllNotificationsRead: markAllNotificationsRead,

        // 收藏相关
        addFavorite: addFavorite,
        getMyFavorites: getMyFavorites,
        removeFavorite: removeFavorite,
        checkFavorite: checkFavorite,

        // 入住/退房
        getCheckInList: getCheckInList,
        getCheckOutList: getCheckOutList,

        // 评价
        submitReview: submitReview,
        getReviews: getReviews,

        // 配置
        getConfig: getConfig,
        getBanners: getBanners
    };
})();
