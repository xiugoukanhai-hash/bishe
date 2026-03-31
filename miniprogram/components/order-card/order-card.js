/**
 * 订单卡片组件
 * 用于展示订单列表项
 */
const util = require('../../utils/util');
const { ORDER_STATUS_COLOR } = require('../../config/constants');
const { getFileUrl } = require('../../config/api');

Component({
  options: {
    addGlobalClass: true
  },

  properties: {
    order: {
      type: Object,
      value: {}
    },
    showDetailBtn: {
      type: Boolean,
      value: true
    }
  },

  data: {
    imageUrl: '',
    statusText: '',
    statusClass: 'status-pending',
    displayCheckIn: '--',
    displayPrice: '0',
    nightCount: 0,
    showPayBtn: false,
    showCancelBtn: false,
    cancelBtnText: '取消订单',
    showEvaluateBtn: false,
    showRebookBtn: false,
    hasActions: false
  },

  lifetimes: {
    attached() {
      this.initData();
    }
  },

  observers: {
    'order': function(order) {
      if (order && Object.keys(order).length > 0) {
        this.initData();
      }
    }
  },

  methods: {
    initData() {
      const order = this.data.order;
      if (!order) return;
      
      // 处理图片 - 优先使用已处理的imageUrl，其次尝试多个图片字段
      let imageUrl = '/images/default/room.png';
      if (order.imageUrl && order.imageUrl.startsWith('http')) {
        imageUrl = order.imageUrl;
      } else {
        const imgPath = order.kefangtupian || order.tupian || order.picture;
        if (imgPath) {
          imageUrl = getFileUrl(imgPath);
        }
      }
      
      // 确保图片URL有效
      if (!imageUrl || imageUrl === 'http://localhost:8080/springboot6alf1/upload/') {
        imageUrl = '/images/default/room.png';
      }
      
      // 处理状态
      const status = order.yuyuezhuangtai || order.ispay || order.zhuangtai || '待支付';
      
      // 状态样式类映射
      let statusClass = 'status-pending';
      if (status === '已支付' || status === '待入住') {
        statusClass = 'status-paid';
      } else if (status === '已入住') {
        statusClass = 'status-checkedin';
      } else if (status === '已完成') {
        statusClass = 'status-completed';
      } else if (status === '已取消' || status === '已拒绝') {
        statusClass = 'status-cancelled';
      } else if (status === '申请退款') {
        statusClass = 'status-refunding';
      } else if (status === '已退款') {
        statusClass = 'status-refunded';
      }
      
      // 处理入住日期显示
      let displayCheckIn = '--';
      if (order.ruzhushijian) {
        displayCheckIn = this.formatDate(order.ruzhushijian);
      } else if (order.ruzhuriqi) {
        displayCheckIn = this.formatDate(order.ruzhuriqi);
      }
      
      // 处理价格显示
      let displayPrice = '0';
      const price = order.zongjia || order.zongjine || order.jiage || 0;
      displayPrice = parseFloat(price).toFixed(0);
      
      // 计算入住天数
      let nightCount = order.tianshu || 0;
      if (!nightCount && order.ruzhuriqi && order.tuifangriqi) {
        nightCount = util.daysBetween(order.ruzhuriqi, order.tuifangriqi);
        if (nightCount < 0) nightCount = 0;
      }
      
      // 按钮显示逻辑
      let showPayBtn = false;
      let showCancelBtn = false;
      let showEvaluateBtn = false;
      let showRebookBtn = false;
      
      let cancelBtnText = '取消订单';
      switch (status) {
        case '待支付':
          showPayBtn = true;
          showCancelBtn = true;
          break;
        case '已支付':
        case '待入住':
          showCancelBtn = true;
          cancelBtnText = '申请退款';
          break;
        case '已入住':
          break;
        case '已完成':
          const isEvaluated = order.ispingjia === '是' || order.ispingjia === 1 || order.ispingjia === '1' || order.ispingjia === true;
          if (!isEvaluated) {
            showEvaluateBtn = true;
          }
          showRebookBtn = true;
          break;
        case '申请退款':
          // 申请退款中，不显示任何操作按钮，等待客服处理
          break;
        case '已取消':
        case '已退款':
          showRebookBtn = true;
          break;
      }
      
      const hasActions = showPayBtn || showCancelBtn || showEvaluateBtn || showRebookBtn;
      
      this.setData({
        imageUrl,
        statusText: status,
        statusClass,
        displayCheckIn,
        displayPrice,
        nightCount,
        showPayBtn,
        showCancelBtn,
        cancelBtnText,
        showEvaluateBtn,
        showRebookBtn,
        hasActions
      });
    },

    formatDate(dateStr) {
      if (!dateStr) return '--';
      try {
        const date = new Date(dateStr.replace(/-/g, '/'));
        if (isNaN(date.getTime())) return dateStr.split(' ')[0];
        const month = date.getMonth() + 1;
        const day = date.getDate();
        return `${month}月${day}日`;
      } catch (e) {
        return dateStr.split(' ')[0];
      }
    },

    onImageError() {
      this.setData({
        imageUrl: '/images/default/room.png'
      });
    },

    onClick() {
      this.triggerEvent('click', { order: this.data.order });
    },

    onPay() {
      this.triggerEvent('pay', { order: this.data.order });
    },

    onCancel() {
      this.triggerEvent('cancel', { order: this.data.order });
    },

    onEvaluate() {
      this.triggerEvent('evaluate', { order: this.data.order });
    },

    onRebook() {
      this.triggerEvent('rebook', { order: this.data.order });
    }
  }
});
