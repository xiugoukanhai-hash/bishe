/**
 * 评分组件
 * 支持整星/半星评分
 */
Component({
  options: {
    addGlobalClass: true
  },

  properties: {
    // 当前评分值
    value: {
      type: Number,
      value: 0
    },
    // 星星总数
    count: {
      type: Number,
      value: 5
    },
    // 是否只读
    readonly: {
      type: Boolean,
      value: false
    },
    // 是否禁用
    disabled: {
      type: Boolean,
      value: false
    },
    // 是否允许半星
    allowHalf: {
      type: Boolean,
      value: false
    },
    // 星星大小（rpx）
    size: {
      type: Number,
      value: 40
    },
    // 星星间距（rpx）
    gutter: {
      type: Number,
      value: 8
    },
    // 选中颜色
    activeColor: {
      type: String,
      value: '#ffd21e'
    },
    // 未选中颜色
    inactiveColor: {
      type: String,
      value: '#c8c9cc'
    },
    // 星星图标
    icon: {
      type: String,
      value: '★'
    },
    // 是否显示文字
    showText: {
      type: Boolean,
      value: false
    },
    // 自定义文字
    text: {
      type: String,
      value: ''
    },
    // 文字大小（rpx）
    textSize: {
      type: Number,
      value: 28
    },
    // 文字颜色
    textColor: {
      type: String,
      value: '#ff976a'
    },
    // 文字与星星间距（rpx）
    textGap: {
      type: Number,
      value: 16
    },
    // 自定义样式
    customStyle: {
      type: String,
      value: ''
    }
  },

  data: {
    stars: [],
    displayText: ''
  },

  lifetimes: {
    attached() {
      this.updateStars();
    }
  },

  observers: {
    'value, count': function() {
      this.updateStars();
    }
  },

  methods: {
    /**
     * 更新星星状态
     */
    updateStars() {
      const { value, count, text } = this.data;
      const stars = [];
      
      for (let i = 0; i < count; i++) {
        let percent = 0;
        if (value >= i + 1) {
          percent = 100;
        } else if (value > i) {
          percent = (value - i) * 100;
        }
        stars.push({ percent });
      }
      
      // 显示文字
      let displayText = text;
      if (!displayText && this.data.showText) {
        displayText = `${value}分`;
      }
      
      this.setData({ stars, displayText });
    },

    /**
     * 星星点击
     */
    onStarClick(e) {
      if (this.data.readonly || this.data.disabled) return;
      
      const index = e.currentTarget.dataset.index;
      let newValue = index + 1;
      
      // 如果允许半星，点击当前高亮的星星则减半
      if (this.data.allowHalf) {
        if (newValue === this.data.value) {
          newValue -= 0.5;
        } else if (newValue - 0.5 === this.data.value) {
          newValue -= 0.5;
        }
      } else {
        // 整星模式：再次点击同一颗星可取消
        if (newValue === this.data.value) {
          newValue = 0;
        }
      }
      
      // 确保值在有效范围内
      newValue = Math.max(0, Math.min(newValue, this.data.count));
      
      this.setData({ value: newValue });
      this.updateStars();
      
      this.triggerEvent('change', { value: newValue });
    }
  }
});
