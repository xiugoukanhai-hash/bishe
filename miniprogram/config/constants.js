/**
 * 常量配置文件
 * 定义项目中使用的各种常量
 */

// 用户类型
const USER_TYPE = {
  USER: 'yonghu',      // 普通用户
  MEMBER: 'huiyuan'    // 会员用户
};

// 订单状态
const ORDER_STATUS = {
  PENDING_PAY: '待支付',
  PAID: '已支付',
  CHECKED_IN: '已入住',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
  REFUNDING: '申请退款',
  REFUNDED: '已退款'
};

// 订单状态颜色
const ORDER_STATUS_COLOR = {
  '待支付': '#ff976a',
  '已支付': '#1989fa',
  '已入住': '#07c160',
  '已完成': '#52c41a',
  '已取消': '#999999',
  '申请退款': '#fa8c16',
  '已退款': '#722ed1'
};

// 房间状态
const ROOM_STATUS = {
  AVAILABLE: '空房',
  OCCUPIED: '已入住',
  CLEANING: '待清扫',
  MAINTENANCE: '维修中',
  RESERVED: '已预订'
};

// 房间状态颜色映射
const ROOM_STATUS_COLOR = {
  '空房': '#07c160',
  '已入住': '#ee0a24',
  '待清扫': '#ff976a',
  '维修中': '#969799',
  '已预订': '#1989fa'
};

// 支付方式
const PAY_METHOD = {
  WECHAT: 'wechat',     // 微信支付
  BALANCE: 'balance',   // 余额支付
  OFFLINE: 'offline'    // 线下支付
};

// 会员等级
const MEMBER_LEVEL = {
  NORMAL: '普通会员',
  SILVER: '银卡会员',
  GOLD: '金卡会员',
  DIAMOND: '钻石会员'
};

// 会员折扣
const MEMBER_DISCOUNT = {
  '普通会员': 0.95,
  '银卡会员': 0.90,
  '金卡会员': 0.85,
  '钻石会员': 0.80
};

// 积分规则
const POINTS_RULE = {
  BOOKING: 10,        // 每笔订单基础积分
  PER_YUAN: 1,        // 每消费1元获得积分
  EVALUATE: 5,        // 评价获得积分
  SIGN_IN: 2,         // 签到获得积分
  FIRST_ORDER: 50     // 首单额外积分
};

// 本地存储Key
const STORAGE_KEYS = {
  TOKEN: 'token',
  USER_INFO: 'userInfo',
  USER_TYPE: 'userType',
  TOKEN_EXPIRE: 'tokenExpire',
  SEARCH_HISTORY: 'searchHistory',
  RECENT_ROOMS: 'recentRooms',
  HAS_LAUNCHED: 'hasLaunched',
  CART: 'cart'
};

// 分页配置
const PAGE_CONFIG = {
  PAGE_SIZE: 10,      // 默认每页数量
  MAX_PAGE_SIZE: 50   // 最大每页数量
};

// 验证规则
const VALIDATE_RULES = {
  PHONE: /^1[3-9]\d{9}$/,
  ID_CARD: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
  PASSWORD: /^.{6,20}$/,
  USERNAME: /^[a-zA-Z0-9_]{4,20}$/,
  EMAIL: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
};

// 评价等级
const RATING_LEVEL = {
  1: '很差',
  2: '较差',
  3: '一般',
  4: '满意',
  5: '非常满意'
};

// 快捷评价标签
const QUICK_TAGS = [
  '房间干净整洁',
  '服务态度好',
  '位置便利',
  '设施齐全',
  '性价比高',
  '隔音效果好',
  '早餐丰富',
  '前台热情'
];

// 客房设施图标映射
const FACILITY_ICONS = {
  'WiFi': 'wifi',
  '空调': 'air-conditioning',
  '电视': 'tv',
  '冰箱': 'fridge',
  '保险箱': 'safe',
  '吹风机': 'hair-dryer',
  '热水壶': 'kettle',
  '洗漱用品': 'toiletries',
  '拖鞋': 'slippers',
  '浴袍': 'bathrobe',
  '迷你吧': 'minibar',
  '书桌': 'desk',
  '衣柜': 'wardrobe',
  '阳台': 'balcony'
};

// 取消订单原因
const CANCEL_REASONS = [
  '行程有变',
  '订错日期',
  '订错房型',
  '找到更好的选择',
  '其他原因'
];

module.exports = {
  USER_TYPE,
  ORDER_STATUS,
  ORDER_STATUS_COLOR,
  ROOM_STATUS,
  ROOM_STATUS_COLOR,
  PAY_METHOD,
  MEMBER_LEVEL,
  MEMBER_DISCOUNT,
  POINTS_RULE,
  STORAGE_KEYS,
  PAGE_CONFIG,
  VALIDATE_RULES,
  RATING_LEVEL,
  QUICK_TAGS,
  FACILITY_ICONS,
  CANCEL_REASONS
};
