/**
 * 房间卡片组件
 * 用于展示客房列表项
 */
const util = require('../../utils/util');

Component({
  options: {
    addGlobalClass: true
  },

  properties: {
    // 房间数据
    room: {
      type: Object,
      value: {}
    },
    // 布局模式: horizontal(水平), vertical(垂直)
    mode: {
      type: String,
      value: 'horizontal'
    },
    // 是否显示状态标签
    showStatus: {
      type: Boolean,
      value: true
    },
    // 是否显示收藏按钮
    showCollect: {
      type: Boolean,
      value: false
    },
    // 是否已收藏
    collected: {
      type: Boolean,
      value: false
    },
    // 是否显示操作按钮
    showAction: {
      type: Boolean,
      value: false
    }
  },

  data: {
    imageUrl: '',
    tags: [],
    facilities: [],
    statusText: '',
    statusClass: ''
  },

  lifetimes: {
    attached() {
      this.initData();
    }
  },

  observers: {
    'room': function(room) {
      if (room && Object.keys(room).length > 0) {
        this.initData();
      }
    }
  },

  methods: {
    /**
     * 初始化数据
     */
    initData() {
      const room = this.data.room;
      if (!room) return;
      
      // 处理图片
      const imageUrl = util.getImageUrl(
        room.tupian || room.kefangtupian,
        '/images/default/room.png'
      );
      
      // 处理标签
      const tags = [];
      if (room.chuangxing) tags.push(room.chuangxing);
      if (room.louceng) tags.push(room.louceng);
      if (room.fangjianmianji) tags.push(room.fangjianmianji);
      
      // 处理设施（限制显示数量）
      let facilities = [];
      if (room.sheshi) {
        if (typeof room.sheshi === 'string') {
          facilities = room.sheshi.split(',').filter(Boolean).slice(0, 4);
        } else if (Array.isArray(room.sheshi)) {
          facilities = room.sheshi.slice(0, 4);
        }
      }
      
      // 处理状态 - 只有"空闲"和"未入住"才可预订
      const statusMap = {
        '空闲': { text: '可预订', class: 'available' },
        '未入住': { text: '可预订', class: 'available' },
        '空房': { text: '可预订', class: 'available' },
        '已入住': { text: '已入住', class: 'occupied' },
        '待清扫': { text: '清扫中', class: 'cleaning' },
        '维修中': { text: '维修中', class: 'maintenance' },
        '已预订': { text: '已预订', class: 'reserved' },
        '已预约': { text: '已预约', class: 'reserved' }
      };
      
      const roomStatus = room.kefangzhuangtai;
      const status = statusMap[roomStatus] || { text: roomStatus || '不可预订', class: 'unavailable' };
      
      this.setData({
        imageUrl,
        tags,
        facilities,
        statusText: status.text,
        statusClass: status.class
      });
    },

    /**
     * 图片加载失败
     */
    onImageError() {
      this.setData({
        imageUrl: '/images/default/room.png'
      });
    },

    /**
     * 卡片点击
     */
    onClick() {
      this.triggerEvent('click', { room: this.data.room });
    },

    /**
     * 收藏点击
     */
    onCollectClick() {
      this.triggerEvent('collect', { 
        room: this.data.room,
        collected: !this.data.collected
      });
    }
  }
});
