/**
 * 消息通知页面
 * 由于没有后端消息接口，使用本地存储模拟
 */
const app = getApp();
const { checkLogin } = require('../../../utils/auth');

Page({
  data: {
    messageList: [],
    currentType: '',
    types: [
      { key: '', name: '全部' },
      { key: 'system', name: '系统' },
      { key: 'order', name: '订单' }
    ],
    loading: false
  },

  onLoad() {
    if (!checkLogin()) {
      wx.redirectTo({ url: '/pages/login/login' });
      return;
    }
  },

  onShow() {
    this.loadMessageList();
  },

  loadMessageList() {
    this.setData({ loading: true });
    
    // 从本地存储读取消息
    const userInfo = app.globalData.userInfo || {};
    const storageKey = `messages_${userInfo.id || 'guest'}`;
    let messages = wx.getStorageSync(storageKey) || [];
    
    // 如果没有消息，生成一些默认消息
    if (messages.length === 0) {
      messages = this.generateDefaultMessages();
      wx.setStorageSync(storageKey, messages);
    }
    
    // 根据类型过滤
    const { currentType } = this.data;
    let filteredList = messages;
    if (currentType) {
      filteredList = messages.filter(m => m.type === currentType);
    }
    
    this.setData({ 
      messageList: filteredList,
      loading: false 
    });
  },

  generateDefaultMessages() {
    const now = new Date();
    return [
      {
        id: 1,
        type: 'system',
        typeName: '系统通知',
        title: '欢迎使用酒店管理系统',
        content: '感谢您使用我们的服务，祝您入住愉快！',
        time: this.formatTime(now),
        isRead: false
      },
      {
        id: 2,
        type: 'system',
        typeName: '系统通知',
        title: '会员权益说明',
        content: '成为会员可享受9折优惠，还能获取积分兑换好礼。',
        time: this.formatTime(new Date(now.getTime() - 86400000)),
        isRead: true
      }
    ];
  },

  formatTime(date) {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    const h = String(date.getHours()).padStart(2, '0');
    const min = String(date.getMinutes()).padStart(2, '0');
    return `${y}-${m}-${d} ${h}:${min}`;
  },

  switchType(e) {
    const { type } = e.currentTarget.dataset;
    this.setData({ currentType: type });
    this.loadMessageList();
  },

  readMessage(e) {
    const { id } = e.currentTarget.dataset;
    const userInfo = app.globalData.userInfo || {};
    const storageKey = `messages_${userInfo.id || 'guest'}`;
    
    let messages = wx.getStorageSync(storageKey) || [];
    messages = messages.map(item => {
      if (item.id === id) {
        return { ...item, isRead: true };
      }
      return item;
    });
    
    wx.setStorageSync(storageKey, messages);
    this.loadMessageList();
  },

  deleteMessage(e) {
    const { id } = e.currentTarget.dataset;
    
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这条消息吗？',
      success: (res) => {
        if (res.confirm) {
          const userInfo = app.globalData.userInfo || {};
          const storageKey = `messages_${userInfo.id || 'guest'}`;
          
          let messages = wx.getStorageSync(storageKey) || [];
          messages = messages.filter(item => item.id !== id);
          
          wx.setStorageSync(storageKey, messages);
          wx.showToast({ title: '删除成功' });
          this.loadMessageList();
        }
      }
    });
  }
});
