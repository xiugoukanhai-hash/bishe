/**
 * 自定义导航栏组件
 */
Component({
  options: {
    addGlobalClass: true,
    multipleSlots: true
  },

  properties: {
    // 标题
    title: {
      type: String,
      value: ''
    },
    // 标题颜色
    titleColor: {
      type: String,
      value: '#ffffff'
    },
    // 是否固定在顶部
    fixed: {
      type: Boolean,
      value: true
    },
    // 固定时是否显示占位
    placeholder: {
      type: Boolean,
      value: true
    },
    // 是否透明背景
    transparent: {
      type: Boolean,
      value: false
    },
    // 是否显示返回按钮
    showBack: {
      type: Boolean,
      value: true
    },
    // 是否显示首页按钮
    showHome: {
      type: Boolean,
      value: false
    },
    // 自定义返回图标
    backIcon: {
      type: String,
      value: ''
    },
    // 自定义首页图标
    homeIcon: {
      type: String,
      value: ''
    }
  },

  data: {
    statusBarHeight: 20,
    navBarHeight: 44
  },

  lifetimes: {
    attached() {
      const app = getApp();
      this.setData({
        statusBarHeight: app.globalData.statusBarHeight || 20,
        navBarHeight: app.globalData.navBarHeight || 44
      });
    }
  },

  methods: {
    /**
     * 返回上一页
     */
    onBack() {
      const pages = getCurrentPages();
      if (pages.length > 1) {
        wx.navigateBack();
      } else {
        wx.switchTab({
          url: '/pages/index/index'
        });
      }
      this.triggerEvent('back');
    },

    /**
     * 返回首页
     */
    onHome() {
      wx.switchTab({
        url: '/pages/index/index'
      });
      this.triggerEvent('home');
    }
  }
});
