const { api, getFileUrl } = require('../../../config/api');
const { get } = require('../../../utils/request');

Page({
  data: {
    newsList: [],
    page: 1,
    limit: 10,
    loading: false,
    noMore: false
  },

  onLoad() {
    this.loadNewsList();
  },

  onPullDownRefresh() {
    this.setData({ page: 1, noMore: false, newsList: [] });
    this.loadNewsList().finally(() => {
      wx.stopPullDownRefresh();
    });
  },

  onReachBottom() {
    if (!this.data.noMore && !this.data.loading) {
      this.loadMore();
    }
  },

  loadNewsList() {
    this.setData({ loading: true });
    console.log('loadNewsList - calling API:', api.news.list);
    
    return get(api.news.list, {
      page: this.data.page,
      limit: this.data.limit,
      sort: 'addtime',
      order: 'desc'
    }, { loading: false }).then(res => {
      console.log('loadNewsList - response:', res);
      if (res.code === 0) {
        const data = res.data || {};
        const list = (data.list || []).map(item => ({
          ...item,
          imageUrl: getFileUrl(item.picture),
          addtime: item.addtime ? (typeof item.addtime === 'string' ? item.addtime.substring(0, 16) : '') : ''
        }));
        const total = data.total || 0;
        console.log('loadNewsList - processed list:', list.length, 'total:', total);
        
        this.setData({
          newsList: this.data.page === 1 ? list : this.data.newsList.concat(list),
          noMore: this.data.page * this.data.limit >= total
        });
      }
    }).catch(err => {
      console.error('loadNewsList - error:', err);
    }).finally(() => {
      this.setData({ loading: false });
    });
  },

  loadMore() {
    this.setData({ page: this.data.page + 1 });
    this.loadNewsList();
  },

  goDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/news/detail/detail?id=${id}`
    });
  }
});
