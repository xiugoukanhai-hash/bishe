import axios from 'axios'
import router from '@/router/router-static'
import storage from '@/utils/storage'

const http = axios.create({
    timeout: 1000 * 86400,
    withCredentials: true,
    baseURL: '/springboot6alf1',
    headers: {
        'Content-Type': 'application/json; charset=utf-8'
    }
})
// 请求拦截
http.interceptors.request.use(config => {
    config.headers['Token'] = storage.get('Token') // 请求头带上token
    return config
}, error => {
    return Promise.reject(error)
})
// 响应拦截
http.interceptors.response.use(response => {
    if (response.data && response.data.code === 401) { // 401, token失效
        router.push({ name: 'login' })
    }
    return response
}, error => {
    return Promise.reject(error)
})

/**
 * 拼接URL路径
 * @param {String} actionName 接口路径
 */
http.adornUrl = (actionName) => {
    return actionName
}

/**
 * 包装请求参数
 * @param {Object} params 请求参数
 * @param {Boolean} openDefaultParams 是否开启默认参数
 */
http.adornParams = (params = {}, openDefaultParams = true) => {
    return params
}

/**
 * 包装请求体数据
 * @param {Object} data 请求体数据
 */
http.adornData = (data = {}) => {
    return data
}

export default http