const { api, getFileUrl } = require('../../../config/api');
const { get } = require('../../../utils/request');

Page({
  data: {
    newsId: '',
    newsInfo: {},
    loading: false
  },

  onLoad(options) {
    const { id } = options;
    if (id) {
      this.setData({ newsId: id });
      this.loadNewsDetail(id);
    } else {
      wx.showToast({ title: '缺少资讯ID', icon: 'none' });
      setTimeout(() => wx.navigateBack(), 1500);
    }
  },

  loadNewsDetail(id) {
    this.setData({ loading: true });
    console.log('loadNewsDetail - calling API:', api.news.detail(id));
    
    get(api.news.detail(id), {}, { loading: false }).then(res => {
      console.log('loadNewsDetail - response:', res);
      if (res.code === 0) {
        const news = res.data || {};
        console.log('loadNewsDetail - raw news data:', news);
        const imgPath = news.picture || news.tupian;
        this.setData({
          newsInfo: {
            ...news,
            imageUrl: getFileUrl(imgPath),
            addtime: news.addtime ? (typeof news.addtime === 'string' ? news.addtime.substring(0, 16) : '') : ''
          }
        });
      } else {
        wx.showToast({ title: res.msg || '加载失败', icon: 'none' });
      }
    }).catch(err => {
      console.error('loadNewsDetail - error:', err);
      wx.showToast({ title: '网络错误', icon: 'none' });
    }).finally(() => {
      this.setData({ loading: false });
    });
  }
});
