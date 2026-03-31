/**
 * 首页
 */
const { get } = require('../../utils/request');
const { api, getFileUrl } = require('../../config/api');

Page({
  data: {
    loading: true,
    banners: [],
    hotelInfo: null,
    hotRooms: [],
    newsList: []
  },

  onLoad() {
    this.loadData();
  },

  onShow() {
    const app = getApp();
    if (app.isLoggedIn()) {
      this.setData({ userInfo: app.getUserInfo() });
    }
  },

  onPullDownRefresh() {
    this.loadData().finally(() => {
      wx.stopPullDownRefresh();
    });
  },

  async loadData() {
    this.setData({ loading: true });
    
    try {
      await Promise.all([
        this.loadBanners(),
        this.loadHotelInfo(),
        this.loadHotRooms(),
        this.loadNewsList()
      ]);
    } catch (err) {
      console.error('Load data error:', err);
    } finally {
      this.setData({ loading: false });
    }
  },

  loadBanners() {
    return new Promise((resolve) => {
      get(api.home.banner, { page: 1, limit: 10 }, { loading: false }).then(res => {
        console.log('banner response:', res);
        if (res.code === 0 && res.data?.list) {
          const banners = res.data.list
            .filter(item => item.name && item.name.startsWith('picture'))
            .map(item => {
              console.log('banner item:', item.name, item.value);
              return {
                id: item.id,
                imageUrl: getFileUrl(item.value),
                link: ''
              };
            });
          console.log('final banners:', banners);
          this.setData({ banners });
        }
        resolve();
      }).catch((err) => {
        console.error('banner error:', err);
        resolve();
      });
    });
  },

  loadHotelInfo() {
    return new Promise((resolve) => {
      get(api.home.hotelInfo, {}, { loading: false }).then(res => {
        console.log('hotelInfo response:', res);
        if (res.code === 0 && res.data) {
          this.setData({ 
            hotelInfo: {
              name: res.data.hotelName || '海景假日酒店',
              content: res.data.hotelDescription || '为您提供舒适、便捷的住宿体验',
              phone: res.data.hotelPhone || '',
              address: res.data.hotelAddress || ''
            }
          });
        }
        resolve();
      }).catch((err) => {
        console.error('hotelInfo error:', err);
        resolve();
      });
    });
  },

  loadHotRooms() {
    return new Promise((resolve) => {
      console.log('loadHotRooms - calling API:', api.home.hotRooms);
      get(api.home.hotRooms, { page: 1, limit: 4 }, { loading: false }).then(res => {
        console.log('loadHotRooms - response:', res);
        if (res.code === 0 && res.data?.list) {
          const hotRooms = res.data.list.map(item => ({
            ...item,
            tupian: item.kefangtupian,
            imageUrl: getFileUrl(item.kefangtupian)
          }));
          console.log('loadHotRooms - processed rooms:', hotRooms.length);
          this.setData({ hotRooms });
        } else {
          console.warn('loadHotRooms - no data or error:', res);
        }
        resolve();
      }).catch((err) => {
        console.error('loadHotRooms - error:', err);
        resolve();
      });
    });
  },

  loadNewsList() {
    return new Promise((resolve) => {
      get(api.news.list, { page: 1, limit: 3 }, { loading: false }).then(res => {
        if (res.code === 0 && res.data?.list) {
          const newsList = res.data.list.map(item => ({
            ...item,
            imageUrl: getFileUrl(item.picture)
          }));
          this.setData({ newsList });
        }
        resolve();
      }).catch(() => resolve());
    });
  },

  goSearch() {
    wx.switchTab({ url: '/pages/room/list/list' });
  },

  onBannerClick(e) {
    const item = e.currentTarget.dataset.item;
    if (item?.link?.startsWith('/pages')) {
      wx.navigateTo({ url: item.link });
    }
  },

  goRoomList() {
    wx.switchTab({ url: '/pages/room/list/list' });
  },

  goOrderList() {
    const app = getApp();
    if (!app.isLoggedIn()) {
      wx.navigateTo({ url: '/pages/login/login' });
      return;
    }
    wx.switchTab({ url: '/pages/order/list/list' });
  },

  goAIService() {
    wx.navigateTo({ url: '/pages/service/ai/ai' });
  },

  goNews() {
    wx.navigateTo({ url: '/pages/news/list/list' });
  },

  goNewsDetail(e) {
    const { id } = e.currentTarget.dataset;
    if (id) {
      wx.navigateTo({ url: `/pages/news/detail/detail?id=${id}` });
    }
  },

  goRoomDetail(e) {
    const id = e.currentTarget.dataset.id || e.detail?.room?.id;
    if (id) {
      wx.navigateTo({ url: `/pages/room/detail/detail?id=${id}` });
    }
  },

  onBannerImageError(e) {
    const index = e.currentTarget.dataset.index;
    const banners = [...this.data.banners];
    if (banners[index]) {
      banners[index].imageUrl = '/images/default/banner.png';
      this.setData({ banners });
    }
  },

  onNewsImageError(e) {
    const index = e.currentTarget.dataset.index;
    const newsList = [...this.data.newsList];
    if (newsList[index]) {
      newsList[index].imageUrl = '/images/default/news.png';
      this.setData({ newsList });
    }
  },

  onShareAppMessage() {
    return {
      title: '酒店住房管理',
      path: '/pages/index/index'
    };
  }
});
