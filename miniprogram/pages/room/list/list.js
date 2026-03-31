/**
 * 客房列表页
 */
const { get } = require('../../../utils/request');
const { api, getFileUrl } = require('../../../config/api');

Page({
  data: {
    loading: true,
    loadingMore: false,
    noMore: false,
    keyword: '',
    roomList: [],
    roomTypes: [],
    selectedType: '',
    priceSort: '',
    onlyAvailable: false,
    showTypePopup: false,
    page: 1,
    limit: 10,
    total: 0,
    refreshing: false,
    currentFilter: ''
  },

  onLoad(options) {
    if (options.type) {
      this.setData({ selectedType: options.type });
    }
    this.loadRoomTypes();
    this.loadRoomList();
  },

  onPullDownRefresh() {
    this.setData({ page: 1, noMore: false });
    this.loadRoomList().finally(() => {
      wx.stopPullDownRefresh();
    });
  },
  
  // scroll-view 下拉刷新
  onRefresh() {
    this.setData({ page: 1, noMore: false, refreshing: true });
    this.loadRoomList().finally(() => {
      this.setData({ refreshing: false });
    });
  },

  onReachBottom() {
    this.loadMore();
  },

  loadRoomTypes() {
    // 从房间列表中提取不重复的房间类型
    get(api.room.list, { page: 1, limit: 100 }, { loading: false }).then(res => {
      if (res.code === 0 && res.data?.list) {
        const types = new Set();
        res.data.list.forEach(item => {
          if (item.kefangleixing) types.add(item.kefangleixing);
        });
        this.setData({ roomTypes: Array.from(types) });
      }
    }).catch(() => {});
  },

  loadMore() {
    if (this.data.loadingMore || this.data.noMore) return;
    this.setData({ page: this.data.page + 1 });
    this.loadRoomList(true);
  },

  loadRoomList(append = false) {
    if (!append) {
      this.setData({ loading: true });
    } else {
      this.setData({ loadingMore: true });
    }
    
    const params = { page: this.data.page, limit: this.data.limit };
    
    // 模糊搜索 - 搜索房号和房型
    const keyword = this.data.keyword ? this.data.keyword.trim().toLowerCase() : '';
    
    // 如果有关键词，获取更多数据进行前端筛选
    if (keyword) {
      params.limit = 100;
      params.page = 1;
    }
    
    if (this.data.selectedType) {
      params.kefangleixing = this.data.selectedType;
    }
    if (this.data.onlyAvailable) {
      params.kefangzhuangtai = '空闲';
    }
    if (this.data.priceSort) {
      params.sort = 'jiage';
      params.order = this.data.priceSort;
    }
    
    return get(api.room.list, params, { loading: false }).then(res => {
      if (res.code === 0 && res.data) {
        let list = (res.data.list || []).map(item => ({
          ...item,
          tupian: item.kefangtupian,
          imageUrl: getFileUrl(item.kefangtupian)
        }));
        
        // 前端模糊搜索 - 匹配房号、房型、环境描述
        if (keyword) {
          list = list.filter(item => {
            const roomNo = (item.kefanghao || '').toLowerCase();
            const roomType = (item.kefangleixing || '').toLowerCase();
            const env = (item.kefanghuanjing || '').toLowerCase();
            const price = String(item.jiage || '');
            
            return roomNo.includes(keyword) || 
                   roomType.includes(keyword) || 
                   env.includes(keyword) ||
                   price.includes(keyword);
          });
        }
        
        const total = keyword ? list.length : (res.data.total || 0);
        const noMore = keyword ? true : (this.data.page * this.data.limit >= total);
        
        this.setData({
          roomList: append ? [...this.data.roomList, ...list] : list,
          total,
          noMore
        });
      }
    }).finally(() => {
      this.setData({ loading: false, loadingMore: false });
    });
  },

  onSearchInput(e) {
    const keyword = e.detail.value;
    this.setData({ keyword });
    
    // 防抖：延迟300ms后自动搜索
    if (this.searchTimer) {
      clearTimeout(this.searchTimer);
    }
    this.searchTimer = setTimeout(() => {
      this.setData({ page: 1, noMore: false });
      this.loadRoomList();
    }, 300);
  },

  onSearch() {
    if (this.searchTimer) {
      clearTimeout(this.searchTimer);
    }
    this.setData({ page: 1, noMore: false });
    this.loadRoomList();
  },

  clearKeyword() {
    this.setData({ keyword: '', page: 1, noMore: false });
    this.loadRoomList();
  },

  showTypeFilter() {
    this.setData({ showTypePopup: true });
  },

  hideTypeFilter() {
    this.setData({ showTypePopup: false });
  },

  selectType(e) {
    const type = e.currentTarget.dataset.type || '';
    this.setData({ selectedType: type, showTypePopup: false, page: 1, noMore: false, currentFilter: type ? 'type' : '' });
    this.loadRoomList();
  },
  
  // 重置房型筛选
  resetTypeFilter() {
    this.setData({ selectedType: '', showTypePopup: false, page: 1, noMore: false, currentFilter: '' });
    this.loadRoomList();
  },

  togglePriceSort() {
    let priceSort = this.data.priceSort;
    if (!priceSort) priceSort = 'asc';
    else if (priceSort === 'asc') priceSort = 'desc';
    else priceSort = '';
    
    this.setData({ priceSort, page: 1, noMore: false });
    this.loadRoomList();
  },

  toggleAvailable() {
    this.setData({ onlyAvailable: !this.data.onlyAvailable, page: 1, noMore: false });
    this.loadRoomList();
  },

  goRoomDetail(e) {
    const id = e.currentTarget.dataset.id || e.detail?.room?.id;
    if (id) {
      wx.navigateTo({ url: `/pages/room/detail/detail?id=${id}` });
    }
  },

  preventMove() {
    return false;
  },

  onShareAppMessage() {
    return { title: '客房列表', path: '/pages/room/list/list' };
  }
});
