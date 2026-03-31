const app = getApp();
const { api, getFileUrl } = require('../../../config/api');
const { checkLogin, isMember } = require('../../../utils/auth');
const { isPhone } = require('../../../utils/validate');
const { post, get } = require('../../../utils/request');

Page({
  data: {
    roomId: '',
    roomInfo: {},
    checkInDate: '',
    checkOutDate: '',
    nights: 1,
    guestName: '',
    guestPhone: '',
    guestIdCard: '',
    roomPrice: 0,
    discount: 0,
    totalPrice: 0,
    canSubmit: false,
    loading: false
  },

  onLoad(options) {
    console.log('=== Confirm page onLoad ===');
    
    // 清除所有可能残留的系统UI元素
    try {
      wx.hideLoading();
      wx.hideToast();
      wx.hideNavigationBarLoading();
      wx.hideKeyboard();
      // 尝试关闭任何可能打开的picker
      try { wx.hideActionSheet && wx.hideActionSheet(); } catch(e) {}
    } catch (e) {
      console.log('Clear UI error:', e);
    }
    
    const { roomId } = options;
    
    const loginStatus = checkLogin();
    console.log('Confirm page - checkLogin result:', loginStatus);
    console.log('Confirm page - userInfo:', app.globalData.userInfo);
    
    if (!loginStatus) {
      console.warn('Confirm page - user not logged in, redirecting');
      wx.redirectTo({ url: '/pages/login/login' });
      return;
    }
    
    console.log('Confirm page - user is logged in, proceeding');
    
    const today = this.formatDate(new Date());
    const tomorrow = this.formatDate(new Date(Date.now() + 86400000));
    
    this.setData({
      roomId,
      checkInDate: today,
      checkOutDate: tomorrow,
      minCheckInDate: today,
      minCheckOutDate: tomorrow
    });
    
    this.loadRoomInfo(roomId);
    this.loadUserInfo();
  },

  onShow() {
    // 每次页面显示时清除可能残留的系统UI
    try {
      wx.hideLoading();
      wx.hideToast();
      wx.hideNavigationBarLoading();
      wx.hideKeyboard();
    } catch (e) {}
  },

  formatDate(date) {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
  },

  loadRoomInfo(roomId) {
    if (!roomId) return;
    
    this.setData({ loading: true });
    
    get(api.room.detail(roomId), {}, { loading: false }).then(res => {
      if (res.code === 0) {
        const room = res.data || {};
        const status = room.kefangzhuangtai;
        
        if (status !== '空闲' && status !== '未入住') {
          wx.showModal({
            title: '提示',
            content: `该客房当前状态为"${status || '不可预订'}"，暂不可预约`,
            showCancel: false,
            success: () => {
              wx.navigateBack();
            }
          });
          return;
        }
        
        this.setData({
          roomInfo: {
            ...room,
            imageUrl: getFileUrl(room.kefangtupian || room.tupian)
          }
        });
        this.calculatePrice();
      }
    }).catch(err => {
      console.error('loadRoomInfo error:', err);
    }).finally(() => {
      this.setData({ loading: false });
    });
  },

  loadUserInfo() {
    const userInfo = app.globalData.userInfo || {};
    this.setData({
      guestName: userInfo.xingming || '',
      guestPhone: userInfo.shouji || '',
      guestIdCard: userInfo.shenfenzheng || ''
    });
    this.checkCanSubmit();
  },

  bindCheckInChange(e) {
    const checkIn = e.detail.value;
    let checkOut = this.data.checkOutDate;
    
    const nextDay = new Date(checkIn);
    nextDay.setDate(nextDay.getDate() + 1);
    const minCheckOut = this.formatDate(nextDay);
    
    if (checkIn >= checkOut) {
      checkOut = minCheckOut;
    }
    this.setData({ 
      checkInDate: checkIn, 
      checkOutDate: checkOut,
      minCheckOutDate: minCheckOut
    });
    this.calculatePrice();
  },

  bindCheckOutChange(e) {
    this.setData({ checkOutDate: e.detail.value });
    this.calculatePrice();
  },

  calculatePrice() {
    const { checkInDate, checkOutDate, roomInfo } = this.data;
    if (!checkInDate || !checkOutDate || !roomInfo.jiage) return;

    const inDate = new Date(checkInDate);
    const outDate = new Date(checkOutDate);
    const nights = Math.max(1, Math.ceil((outDate - inDate) / 86400000));
    const roomPrice = nights * (roomInfo.jiage || 0);
    
    let discount = 0;
    if (isMember()) {
      discount = Math.floor(roomPrice * 0.1);
    }
    
    const totalPrice = roomPrice - discount;
    
    this.setData({ nights, roomPrice, discount, totalPrice });
    this.checkCanSubmit();
  },

  onNameInput(e) {
    this.setData({ guestName: e.detail.value });
    this.checkCanSubmit();
  },

  onPhoneInput(e) {
    this.setData({ guestPhone: e.detail.value });
    this.checkCanSubmit();
  },

  onIdCardInput(e) {
    this.setData({ guestIdCard: e.detail.value });
  },

  selectCheckIn() {
    // 日期选择通过 wxml 的 picker 完成，此处保留兼容
  },

  selectCheckOut() {
    // 日期选择通过 wxml 的 picker 完成，此处保留兼容
  },

  checkCanSubmit() {
    const { guestName, guestPhone, checkInDate, checkOutDate, roomInfo } = this.data;
    const canSubmit = !!(guestName && guestPhone && checkInDate && checkOutDate && roomInfo.id);
    this.setData({ canSubmit });
  },

  submitOrder() {
    const { guestName, guestPhone, canSubmit } = this.data;
    
    if (!canSubmit) return;
    
    if (!guestName.trim()) {
      wx.showToast({ title: '请输入入住人姓名', icon: 'none' });
      return;
    }
    
    if (!isPhone(guestPhone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' });
      return;
    }
    
    this.createOrder();
  },

  createOrder() {
    const { roomId, roomInfo, checkInDate, checkOutDate, nights, guestName, guestPhone, guestIdCard, totalPrice, roomPrice } = this.data;
    const isMemberUser = isMember();
    const userInfo = app.globalData.userInfo || {};
    
    this.setData({ loading: true });
    
    const url = isMemberUser ? api.booking.memberBook : api.booking.userBook;
    
    // 将日期转换为后端期望的格式 (yyyy-MM-dd HH:mm:ss)
    const ruzhushijian = checkInDate + ' 14:00:00';
    const now = new Date();
    const yuyueshijian = `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}-${String(now.getDate()).padStart(2,'0')} ${String(now.getHours()).padStart(2,'0')}:${String(now.getMinutes()).padStart(2,'0')}:${String(now.getSeconds()).padStart(2,'0')}`;
    
    // 生成预约编号：YY + 时间戳 + 随机数
    const yuyuebianhao = 'YY' + now.getTime() + Math.floor(Math.random() * 1000);
    
    const data = {
      yuyuebianhao: yuyuebianhao,
      kefanghao: roomInfo.kefanghao,
      ruzhushijian: ruzhushijian,
      jiage: String(roomInfo.jiage || roomPrice),
      tianshu: nights,
      zongjia: String(totalPrice),
      yuyueshijian: yuyueshijian,
      zhanghao: userInfo.zhanghao,
      xingming: guestName,
      shouji: guestPhone,
      shenfenzheng: guestIdCard,
      sfsh: '待审核',
      ispay: '未支付',
      yuyuezhuangtai: '待支付'
    };
    
    console.log('createOrder - data:', data);
    
    post(url, data, { loading: false, showError: true }).then(res => {
      console.log('createOrder - success:', res);
      const orderId = res.data?.id || res.data;
      wx.redirectTo({
        url: `/pages/booking/pay/pay?orderId=${orderId}&amount=${totalPrice}`
      });
    }).catch(err => {
      console.error('createOrder - error:', err);
    }).finally(() => {
      this.setData({ loading: false });
    });
  }
});
