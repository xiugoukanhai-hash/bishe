/**
 * 空状态组件
 * 用于展示列表为空、无数据等场景
 */
Component({
  options: {
    addGlobalClass: true,
    multipleSlots: true
  },

  properties: {
    // 图片类型: empty-order, empty-collect, empty-message, empty-data, empty-search, empty-network
    type: {
      type: String,
      value: 'empty-data'
    },
    // 自定义图片路径（优先级高于type）
    image: {
      type: String,
      value: ''
    },
    // 图片大小（rpx）
    imageSize: {
      type: Number,
      value: 240
    },
    // 主提示文字
    text: {
      type: String,
      value: '暂无数据'
    },
    // 副提示文字
    description: {
      type: String,
      value: ''
    },
    // 是否显示按钮
    showButton: {
      type: Boolean,
      value: false
    },
    // 按钮文字
    buttonText: {
      type: String,
      value: '去看看'
    },
    // 按钮类型: primary, default
    buttonType: {
      type: String,
      value: 'default'
    },
    // 顶部内边距（rpx）
    paddingTop: {
      type: Number,
      value: 80
    },
    // 底部内边距（rpx）
    paddingBottom: {
      type: Number,
      value: 80
    }
  },

  data: {
    imageSrc: ''
  },

  lifetimes: {
    attached() {
      this.setImageSrc();
    }
  },

  observers: {
    'type, image': function() {
      this.setImageSrc();
    }
  },

  methods: {
    /**
     * 设置图片路径
     */
    setImageSrc() {
      // 如果传入自定义图片，优先使用
      if (this.data.image) {
        this.setData({ imageSrc: this.data.image });
        return;
      }
      
      // 根据类型设置默认图片
      const imageMap = {
        'empty-order': '/images/default/empty-order.png',
        'empty-collect': '/images/default/empty-collect.png',
        'empty-message': '/images/default/empty-message.png',
        'empty-data': '/images/default/empty-data.png',
        'empty-search': '/images/default/empty-search.png',
        'empty-network': '/images/default/empty-network.png',
        'empty-cart': '/images/default/empty-cart.png'
      };
      
      this.setData({
        imageSrc: imageMap[this.data.type] || imageMap['empty-data']
      });
    },

    /**
     * 按钮点击事件
     */
    onButtonClick() {
      this.triggerEvent('buttonclick');
      this.triggerEvent('click');
    }
  }
});
