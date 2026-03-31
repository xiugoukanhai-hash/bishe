/**
 * 我的收藏页面
 */
const app = getApp();
const { api, getFileUrl } = require('../../../config/api');
const { checkLogin } = require('../../../utils/auth');
const { get, post } = require('../../../utils/request');

Page({
  data: {
    collectList: [],
    isEditMode: false,
    isAllSelected: false,
    selectedCount: 0,
    loading: false
  },

  onLoad() {
    if (!checkLogin()) {
      wx.redirectTo({ url: '/pages/login/login' });
      return;
    }
  },

  onShow() {
    this.loadCollectList();
  },

  loadCollectList() {
    this.setData({ loading: true });
    const userInfo = app.globalData.userInfo || {};
    
    console.log('loadCollectList - userId:', userInfo.id);
    
    get(api.collect.list, {
      userid: userInfo.id,
      page: 1,
      limit: 100
    }, { loading: false }).then(res => {
      console.log('loadCollectList - response:', res);
      if (res.code === 0) {
        const data = res.data || {};
        const list = (data.list || []).map(item => ({
          ...item,
          imageUrl: getFileUrl(item.picture || item.tupian || item.kefangtupian),
          roomId: item.refid,
          selected: false
        }));
        this.setData({ collectList: list });
        // 收藏表没有价格字段，需要联查房间信息获取价格
        this.loadRoomPrices(list);
      } else {
        console.warn('loadCollectList - error:', res);
        this.setData({ collectList: [] });
      }
    }).catch(err => {
      console.error('loadCollectList - failed:', err);
      this.setData({ collectList: [] });
    }).finally(() => {
      this.setData({ loading: false });
    });
  },
  
  // 加载房间价格信息
  loadRoomPrices(list) {
    if (!list || list.length === 0) return;
    
    // 获取所有房间ID
    const roomIds = list.map(item => item.refid).filter(id => id);
    if (roomIds.length === 0) return;
    
    // 查询房间信息获取价格
    get(api.room.list, { page: 1, limit: 100 }, { loading: false, showError: false }).then(res => {
      if (res.code === 0 && res.data?.list) {
        // 创建房间ID到价格的映射
        const roomPriceMap = {};
        res.data.list.forEach(room => {
          roomPriceMap[room.id] = {
            jiage: room.jiage,
            kefangleixing: room.kefangleixing,
            kefangtupian: room.kefangtupian
          };
        });
        
        // 更新收藏列表的价格
        const updatedList = this.data.collectList.map(item => {
          const roomInfo = roomPriceMap[item.refid];
          if (roomInfo) {
            return {
              ...item,
              jiage: roomInfo.jiage || item.jiage,
              typename: roomInfo.kefangleixing || item.typename,
              imageUrl: item.imageUrl || getFileUrl(roomInfo.kefangtupian)
            };
          }
          return item;
        });
        
        this.setData({ collectList: updatedList });
      }
    }).catch(err => {
      console.log('Load room prices error:', err);
    });
  },

  toggleEditMode() {
    const isEditMode = !this.data.isEditMode;
    
    if (!isEditMode) {
      const collectList = this.data.collectList.map(item => ({
        ...item,
        selected: false
      }));
      this.setData({
        collectList,
        isAllSelected: false,
        selectedCount: 0
      });
    }
    
    this.setData({ isEditMode });
  },

  toggleSelect(e) {
    const { index } = e.currentTarget.dataset;
    const collectList = [...this.data.collectList];
    collectList[index].selected = !collectList[index].selected;
    
    const selectedCount = collectList.filter(item => item.selected).length;
    const isAllSelected = selectedCount === collectList.length && collectList.length > 0;
    
    this.setData({ collectList, selectedCount, isAllSelected });
  },

  toggleSelectAll() {
    const { isAllSelected, collectList } = this.data;
    const newSelected = !isAllSelected;
    
    const newList = collectList.map(item => ({
      ...item,
      selected: newSelected
    }));
    
    this.setData({
      collectList: newList,
      isAllSelected: newSelected,
      selectedCount: newSelected ? newList.length : 0
    });
  },

  deleteSelected() {
    const { collectList } = this.data;
    const selectedItems = collectList.filter(item => item.selected);
    
    if (selectedItems.length === 0) return;
    
    wx.showModal({
      title: '确认删除',
      content: `确定要删除选中的${selectedItems.length}个收藏吗？`,
      success: (res) => {
        if (res.confirm) {
          this.doDelete(selectedItems);
        }
      }
    });
  },

  doDelete(items) {
    wx.showLoading({ title: '删除中...' });
    
    // 后端接口接收 Long[] 数组格式
    const ids = items.map(item => item.id);
    
    post(api.collect.remove, ids, { loading: false, showError: false }).then(res => {
      wx.hideLoading();
      if (res.code === 0) {
        wx.showToast({ title: '删除成功' });
        this.loadCollectList();
        this.setData({
          isEditMode: false,
          isAllSelected: false,
          selectedCount: 0
        });
      } else {
        wx.showToast({ title: res.msg || '删除失败', icon: 'none' });
      }
    }).catch(err => {
      console.error('Delete collect error:', err);
      wx.hideLoading();
      wx.showToast({ title: '删除失败', icon: 'none' });
    });
  },

  goRoomDetail(e) {
    if (this.data.isEditMode) return;
    
    const { id } = e.currentTarget.dataset;
    if (id) {
      wx.navigateTo({ url: `/pages/room/detail/detail?id=${id}` });
    }
  },

  goRoomList() {
    wx.switchTab({ url: '/pages/room/list/list' });
  },

  // empty组件的按钮点击事件
  onEmptyButtonClick() {
    this.goRoomList();
  }
});
