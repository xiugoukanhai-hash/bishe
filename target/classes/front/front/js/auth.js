/**
 * 认证状态管理模块
 * 提供用户登录、注册、状态管理等功能
 */
var Auth = (function() {
    'use strict';

    // 配置
    var config = {
        TOKEN_KEY: 'Token',
        USER_INFO_KEY: 'userInfo',
        USER_TABLE_KEY: 'userTable',
        USER_ID_KEY: 'userid'
    };

    // 用户类型
    var USER_TYPE = {
        YONGHU: 'yonghu',
        HUIYUAN: 'huiyuan'
    };

    /**
     * 检查是否已登录
     */
    function isLoggedIn() {
        return !!getToken();
    }

    /**
     * 获取Token
     */
    function getToken() {
        return localStorage.getItem(config.TOKEN_KEY) || '';
    }

    /**
     * 设置Token
     */
    function setToken(token) {
        localStorage.setItem(config.TOKEN_KEY, token);
    }

    /**
     * 获取用户信息
     */
    function getUserInfo() {
        var info = localStorage.getItem(config.USER_INFO_KEY);
        if (info) {
            try {
                return JSON.parse(info);
            } catch (e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 设置用户信息
     */
    function setUserInfo(userInfo) {
        localStorage.setItem(config.USER_INFO_KEY, JSON.stringify(userInfo));
        if (userInfo.tableName) {
            localStorage.setItem(config.USER_TABLE_KEY, userInfo.tableName);
        }
        if (userInfo.userId) {
            localStorage.setItem(config.USER_ID_KEY, userInfo.userId);
        }
    }

    /**
     * 获取用户类型
     */
    function getUserType() {
        return localStorage.getItem(config.USER_TABLE_KEY) || '';
    }

    /**
     * 获取用户ID
     */
    function getUserId() {
        return localStorage.getItem(config.USER_ID_KEY) || '';
    }

    /**
     * 登录
     * @param {string} type - 用户类型 (yonghu/huiyuan)
     * @param {string} username - 用户名
     * @param {string} password - 密码
     */
    function login(type, username, password) {
        return new Promise(function(resolve, reject) {
            if (!username) {
                reject(new Error('请输入账号'));
                return;
            }
            if (!password) {
                reject(new Error('请输入密码'));
                return;
            }

            API.login(type, { username: username, password: password })
                .then(function(response) {
                    if (response.code === 0) {
                        var token = response.token || (response.data && response.data.token);
                        if (token) {
                            setToken(token);
                            setUserInfo({
                                userId: response.data ? response.data.userId : '',
                                username: username,
                                role: response.data ? response.data.role : '',
                                tableName: type
                            });
                            resolve({ success: true, token: token });
                        } else {
                            resolve({ success: true });
                        }
                    } else {
                        reject(new Error(response.msg || '登录失败'));
                    }
                })
                .catch(function(error) {
                    reject(error);
                });
        });
    }

    /**
     * 注册
     * @param {string} type - 用户类型
     * @param {Object} data - 注册数据
     */
    function register(type, data) {
        return new Promise(function(resolve, reject) {
            // 基础校验
            if (!data.zhanghao) {
                reject(new Error('请输入账号'));
                return;
            }
            if (!data.mima) {
                reject(new Error('请输入密码'));
                return;
            }
            if (data.mima.length < 6 || data.mima.length > 20) {
                reject(new Error('密码长度为6-20位'));
                return;
            }
            if (!data.xingming) {
                reject(new Error('请输入姓名'));
                return;
            }
            if (!data.shouji) {
                reject(new Error('请输入手机号'));
                return;
            }
            if (!/^1[3-9]\d{9}$/.test(data.shouji)) {
                reject(new Error('手机号格式不正确'));
                return;
            }
            if (!data.shenfenzheng) {
                reject(new Error('请输入身份证号'));
                return;
            }
            if (!/^\d{17}[\dXx]$/.test(data.shenfenzheng)) {
                reject(new Error('身份证号格式不正确'));
                return;
            }

            API.register(type, data)
                .then(function(response) {
                    if (response.code === 0) {
                        resolve({ success: true });
                    } else {
                        reject(new Error(response.msg || '注册失败'));
                    }
                })
                .catch(function(error) {
                    reject(error);
                });
        });
    }

    /**
     * 退出登录
     */
    function logout() {
        var type = getUserType();
        
        // 清除本地存储
        localStorage.removeItem(config.TOKEN_KEY);
        localStorage.removeItem(config.USER_INFO_KEY);
        localStorage.removeItem(config.USER_TABLE_KEY);
        localStorage.removeItem(config.USER_ID_KEY);
        
        // 跳转到登录页
        window.location.href = './pages/login/login.html';
    }

    /**
     * 检查登录状态，未登录则跳转
     */
    function checkLogin() {
        if (!isLoggedIn()) {
            var currentUrl = window.location.href;
            window.location.href = './pages/login/login.html?redirect=' + encodeURIComponent(currentUrl);
            return false;
        }
        return true;
    }

    /**
     * 获取重定向地址
     */
    function getRedirectUrl() {
        var params = new URLSearchParams(window.location.search);
        return params.get('redirect') || '../index.html';
    }

    /**
     * 修改密码
     * @param {string} oldPassword - 原密码
     * @param {string} newPassword - 新密码
     */
    function updatePassword(oldPassword, newPassword) {
        return new Promise(function(resolve, reject) {
            if (!oldPassword) {
                reject(new Error('请输入原密码'));
                return;
            }
            if (!newPassword) {
                reject(new Error('请输入新密码'));
                return;
            }
            if (newPassword.length < 6 || newPassword.length > 20) {
                reject(new Error('新密码长度为6-20位'));
                return;
            }

            var type = getUserType();
            API.updatePassword(type, {
                oldPassword: oldPassword,
                newPassword: newPassword
            })
            .then(function(response) {
                if (response.code === 0) {
                    resolve({ success: true });
                } else {
                    reject(new Error(response.msg || '修改失败'));
                }
            })
            .catch(function(error) {
                reject(error);
            });
        });
    }

    /**
     * 获取当前用户详细信息
     */
    function getCurrentUserInfo() {
        return new Promise(function(resolve, reject) {
            var type = getUserType();
            if (!type) {
                reject(new Error('未登录'));
                return;
            }

            API.getUserInfo(type)
                .then(function(response) {
                    if (response.code === 0 && response.data) {
                        resolve(response.data);
                    } else {
                        reject(new Error(response.msg || '获取用户信息失败'));
                    }
                })
                .catch(function(error) {
                    reject(error);
                });
        });
    }

    /**
     * 更新用户信息
     * @param {Object} data - 用户信息
     */
    function updateUserInfo(data) {
        return new Promise(function(resolve, reject) {
            var type = getUserType();
            if (!type) {
                reject(new Error('未登录'));
                return;
            }

            API.post('/' + type + '/update', data)
                .then(function(response) {
                    if (response.code === 0) {
                        resolve({ success: true });
                    } else {
                        reject(new Error(response.msg || '更新失败'));
                    }
                })
                .catch(function(error) {
                    reject(error);
                });
        });
    }

    // 暴露公共方法
    return {
        USER_TYPE: USER_TYPE,
        isLoggedIn: isLoggedIn,
        getToken: getToken,
        setToken: setToken,
        getUserInfo: getUserInfo,
        setUserInfo: setUserInfo,
        getUserType: getUserType,
        getUserId: getUserId,
        login: login,
        register: register,
        logout: logout,
        checkLogin: checkLogin,
        getRedirectUrl: getRedirectUrl,
        updatePassword: updatePassword,
        getCurrentUserInfo: getCurrentUserInfo,
        updateUserInfo: updateUserInfo
    };
})();
