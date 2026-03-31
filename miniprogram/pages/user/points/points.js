const app = getApp();
const { api } = require('../../../config/api');
const { checkLogin, isMember } = require('../../../utils/auth');

Page({
  data: {
    userInfo: {},
    recentRecords: [],
    tasks: [
      { id: 'signin', name: '每日签到', desc: '每天签到可获得积分', points: 10, icon: '📅', done: false },
      { id: 'booking', name: '预订房间', desc: '完成一次预订', points: 50, icon: '🏨', done: false },
      { id: 'review', name: '评价订单', desc: '对已完成的订单进行评价', points: 20, icon: '⭐', done: false },
      { id: 'share', name: '分享给好友', desc: '将酒店分享给好友', points: 5, icon: '📤', done: false }
    ],
    loading: false
  },

  onLoad() {
    if (!checkLogin()) {
      wx.redirectTo({ url: '/pages/login/login' });
      return;
    }
    
    if (!isMember()) {
      wx.showModal({
        title: '提示',
        content: '积分功能仅限会员使用，是否去注册会员？',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({ url: '/pages/register/register?type=member' });
          } else {
            wx.navigateBack();
          }
        }
      });
      return;
    }
  },

  onShow() {
    if (isMember()) {
      this.loadUserInfo();
      this.loadPointsRecords();
      this.checkTasks();
    }
  },

  loadUserInfo() {
    const userInfo = app.globalData.userInfo || {};
    this.setData({ userInfo });
  },

  loadPointsRecords() {
    // 积分记录API暂未实现，使用模拟数据
    if (!api.points.records) {
      const mockRecords = [
        { id: 1, type: '签到奖励', points: '+10', time: '2026-03-01 09:00' },
        { id: 2, type: '预订奖励', points: '+50', time: '2026-02-28 14:30' },
        { id: 3, type: '评价奖励', points: '+20', time: '2026-02-27 16:00' }
      ];
      this.setData({ recentRecords: mockRecords });
      return;
    }
    
    wx.request({
      url: api.points.records,
      data: { page: 1, limit: 5 },
      success: (res) => {
        if (res.data.code === 0) {
          this.setData({
            recentRecords: res.data.data.list || res.data.data || []
          });
        }
      }
    });
  },

  checkTasks() {
    // 任务状态API暂未实现，使用模拟数据
    if (!api.points.taskStatus) {
      const today = new Date().toDateString();
      const signinKey = 'signin_' + today;
      const signinDone = wx.getStorageSync(signinKey) || false;
      
      const tasks = this.data.tasks.map(task => ({
        ...task,
        done: task.id === 'signin' ? signinDone : false
      }));
      this.setData({ tasks });
      return;
    }
    
    wx.request({
      url: api.points.taskStatus,
      success: (res) => {
        if (res.data.code === 0) {
          const taskStatus = res.data.data || {};
          const tasks = this.data.tasks.map(task => ({
            ...task,
            done: taskStatus[task.id] || false
          }));
          this.setData({ tasks });
        }
      }
    });
  },

  showRules() {
    wx.showModal({
      title: '积分规则',
      content: '1. 每日签到：+10积分\n2. 预订房间：+50积分\n3. 评价订单：+20积分\n4. 分享好友：+5积分\n5. 积分可用于兑换优惠券和礼品',
      showCancel: false
    });
  },

  goExchange() {
    wx.showToast({ title: '功能开发中', icon: 'none' });
  },

  goHistory() {
    wx.showToast({ title: '功能开发中', icon: 'none' });
  },

  goTask() {
    const taskSection = this.selectComponent('.section:last-child');
    if (taskSection) {
      wx.pageScrollTo({ selector: '.section:last-child' });
    }
  },

  doTask(e) {
    const { id } = e.currentTarget.dataset;
    const task = this.data.tasks.find(t => t.id === id);
    
    if (!task || task.done) return;
    
    if (id === 'signin') {
      this.doSignin();
    } else if (id === 'share') {
      this.onShareAppMessage();
    } else {
      wx.showToast({ title: '请完成对应操作', icon: 'none' });
    }
  },

  doSignin() {
    // 签到API暂未实现，使用本地模拟
    if (!api.points.signin) {
      const today = new Date().toDateString();
      const signinKey = 'signin_' + today;
      
      if (wx.getStorageSync(signinKey)) {
        wx.showToast({ title: '今日已签到', icon: 'none' });
        return;
      }
      
      wx.setStorageSync(signinKey, true);
      
      // 模拟增加积分
      const userInfo = this.data.userInfo;
      userInfo.jifen = (userInfo.jifen || 0) + 10;
      app.updateUserInfo({ jifen: userInfo.jifen });
      
      wx.showToast({ title: '签到成功 +10积分' });
      this.loadUserInfo();
      this.checkTasks();
      return;
    }
    
    wx.request({
      url: api.points.signin,
      method: 'POST',
      success: (res) => {
        if (res.data.code === 0) {
          wx.showToast({ title: '签到成功 +10积分' });
          this.loadUserInfo();
          this.checkTasks();
        } else {
          wx.showToast({ title: res.data.msg || '签到失败', icon: 'none' });
        }
      }
    });
  },

  onShareAppMessage() {
    return {
      title: '推荐一家不错的酒店',
      path: '/pages/index/index'
    };
  }
});
