/**
 * API配置文件
 * 定义所有后端接口地址
 */

const BASE_URL = 'http://localhost:8080/springboot6alf1';

const getUrl = (path) => {
  if (!path) return BASE_URL;
  if (path.startsWith('http')) return path;
  return BASE_URL + path;
};

const getFileUrl = (filename) => {
  if (!filename) return '';
  if (filename.startsWith('http')) return filename;
  // 后端静态文件在 /upload/ 路径下
  const name = (filename + '').replace(/^upload\/+/, '');
  return BASE_URL + '/upload/' + name;
};

const api = {
  // 认证相关
  auth: {
    userLogin: getUrl('/yonghu/login'),
    userRegister: getUrl('/yonghu/register'),
    memberLogin: getUrl('/huiyuan/login'),
    memberRegister: getUrl('/huiyuan/register'),
    userSession: getUrl('/yonghu/session'),
    memberSession: getUrl('/huiyuan/session'),
    userUpdate: getUrl('/yonghu/update'),
    memberUpdate: getUrl('/huiyuan/update'),
    userLogout: getUrl('/yonghu/logout'),
    memberLogout: getUrl('/huiyuan/logout'),
    userBalance: getUrl('/yonghu/balance'),
    memberBalance: getUrl('/huiyuan/balance'),
    userRecharge: getUrl('/yonghu/recharge'),
    memberRecharge: getUrl('/huiyuan/recharge')
  },
  
  // 首页相关
  home: {
    banner: getUrl('/config/list'),
    hotelInfo: getUrl('/config/hotelInfo'),
    hotRooms: getUrl('/kefangxinxi/list')
  },
  
  // 客房相关
  room: {
    list: getUrl('/kefangxinxi/list'),
    detail: (id) => getUrl('/kefangxinxi/detail/' + id),
    info: (id) => getUrl('/kefangxinxi/info/' + id),
    typeList: getUrl('/kefangxinxi/roomTypes'),
    update: getUrl('/kefangxinxi/update')
  },
  
  // 收藏相关
  collect: {
    add: getUrl('/storeup/save'),
    remove: getUrl('/storeup/delete'),
    list: getUrl('/storeup/page'),
    check: getUrl('/storeup/check')
  },
  
  // 预订相关
  booking: {
    userBook: getUrl('/yonghuyuyue/add'),
    memberBook: getUrl('/huiyuanyuyue/add')
  },
  
  // 订单相关
  order: {
    userList: getUrl('/yonghuyuyue/page'),
    memberList: getUrl('/huiyuanyuyue/page'),
    userDetail: (id) => getUrl('/yonghuyuyue/detail/' + id),
    memberDetail: (id) => getUrl('/huiyuanyuyue/detail/' + id),
    userUpdate: getUrl('/yonghuyuyue/update'),
    memberUpdate: getUrl('/huiyuanyuyue/update'),
    userCancel: (id) => getUrl('/yonghuyuyue/cancel/' + id),
    memberCancel: (id) => getUrl('/huiyuanyuyue/cancel/' + id),
    userPay: (id) => getUrl('/yonghuyuyue/pay/' + id),
    memberPay: (id) => getUrl('/huiyuanyuyue/pay/' + id),
    userSetEvaluated: (id) => getUrl('/yonghuyuyue/setEvaluated/' + id),
    memberSetEvaluated: (id) => getUrl('/huiyuanyuyue/setEvaluated/' + id)
  },
  
  // 评价相关
  evaluate: {
    add: getUrl('/pinglun/add'),
    list: getUrl('/pinglun/page'),
    roomList: (roomId) => getUrl('/pinglun/page?kefangId=' + roomId)
  },
  
  // 新闻资讯（使用 /list 前端接口，无需鉴权）
  news: {
    list: getUrl('/news/list'),
    detail: (id) => getUrl('/news/detail/' + id)
  },
  
  // 消息相关（本地模拟，无后端接口）
  message: {
    list: null,
    read: null
  },
  
  // 积分相关（本地模拟，无后端接口）
  points: {
    records: null,
    taskStatus: null,
    signin: null
  },
  
  // 通用
  common: {
    upload: getUrl('/file/upload')
  }
};

module.exports = {
  BASE_URL,
  api,
  getUrl,
  getFileUrl
};
