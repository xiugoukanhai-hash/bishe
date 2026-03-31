/**
 * 加载组件
 * 支持多种加载动画样式
 */
Component({
  options: {
    addGlobalClass: true
  },

  properties: {
    // 是否显示
    show: {
      type: Boolean,
      value: false
    },
    // 加载类型: spinner(圆点), circular(圆环), dots(跳动点)
    type: {
      type: String,
      value: 'circular'
    },
    // 加载文字
    text: {
      type: String,
      value: ''
    },
    // 是否全屏遮罩
    fullscreen: {
      type: Boolean,
      value: false
    },
    // 加载图标大小（rpx）- 仅行内模式有效
    size: {
      type: Number,
      value: 48
    },
    // 文字颜色 - 仅行内模式有效
    textColor: {
      type: String,
      value: '#646566'
    }
  },

  methods: {
    /**
     * 阻止触摸穿透
     */
    preventTouchMove() {
      return false;
    }
  }
});
