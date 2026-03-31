/**
 * 在线客服页面
 * 接入 DeepSeek AI 实现智能对话
 */
const app = getApp();

// DeepSeek AI 配置
const DEEPSEEK_API_KEY = 'sk-352e58d90401415f90e44df381869d1a';
const DEEPSEEK_API_URL = 'https://api.deepseek.com/v1/chat/completions';

// 客服系统提示词
const SYSTEM_PROMPT = `你是星际酒店的在线客服"小星"，一个热情友好的客服代表。

【酒店信息】
- 名称：星际酒店
- 地址：广东省深圳市南山区海岸城路168号
- 前台：400-888-9999
- 入住：14:00后 | 退房：12:00前

【房型价格】豪华大床房428元，标准双床房388元，商务套房688元，总统套房1288元

【会员权益】9折优惠、积分抵现、免费延迟退房、套房送早餐

【设施】免费WiFi、24小时热水、免费停车、健身房、泳池、中西餐厅

【退款政策】未支付可直接取消，已支付24小时内可申请退款，入住当天18:00后取消扣首晚房费

【回复要求】
1. 热情专业，像真人客服一样交流
2. 回复简洁，控制在80字以内
3. 适当使用表情增加亲和力
4. 不确定的问题建议拨打前台电话`;

Page({
  data: {
    messages: [],
    inputValue: '',
    scrollToView: '',
    userInfo: {},
    conversationHistory: [],
    isTyping: false
  },

  onLoad() {
    this.setData({
      userInfo: app.globalData.userInfo || {},
      conversationHistory: [
        { role: 'system', content: SYSTEM_PROMPT }
      ]
    });
    this.loadChatHistory();
  },

  onShow() {
    // 滚动到底部
    if (this.data.messages.length > 0) {
      this.setData({
        scrollToView: `msg-${this.data.messages.length - 1}`
      });
    }
  },

  // 加载聊天记录
  loadChatHistory() {
    const userInfo = app.globalData.userInfo || {};
    const storageKey = `chat_history_${userInfo.id || 'guest'}`;
    let messages = wx.getStorageSync(storageKey) || [];
    
    // 如果没有聊天记录，添加欢迎消息
    if (messages.length === 0) {
      messages = [{
        id: Date.now(),
        isSelf: false,
        content: '您好！欢迎咨询，请问有什么可以帮您？',
        time: this.formatTime(new Date())
      }];
      this.saveChatHistory(messages);
    }
    
    this.setData({ 
      messages,
      scrollToView: messages.length > 0 ? `msg-${messages.length - 1}` : ''
    });
  },

  // 保存聊天记录
  saveChatHistory(messages) {
    const userInfo = app.globalData.userInfo || {};
    const storageKey = `chat_history_${userInfo.id || 'guest'}`;
    // 只保留最近100条消息
    const toSave = messages.slice(-100);
    wx.setStorageSync(storageKey, toSave);
  },

  // 格式化时间
  formatTime(date) {
    const h = String(date.getHours()).padStart(2, '0');
    const m = String(date.getMinutes()).padStart(2, '0');
    return `${h}:${m}`;
  },

  onInput(e) {
    this.setData({ inputValue: e.detail.value });
  },

  sendMessage() {
    const { inputValue, messages, userInfo, conversationHistory } = this.data;
    
    if (!inputValue.trim()) return;
    
    const userMessage = {
      id: Date.now(),
      isSelf: true,
      content: inputValue,
      avatar: userInfo.touxiang,
      time: this.formatTime(new Date())
    };
    
    const newMessages = [...messages, userMessage];
    
    // 添加到对话历史
    const newHistory = [...conversationHistory, {
      role: 'user',
      content: inputValue
    }];
    
    this.setData({
      messages: newMessages,
      conversationHistory: newHistory,
      inputValue: '',
      isTyping: true,
      scrollToView: `msg-${newMessages.length - 1}`
    });
    
    this.saveChatHistory(newMessages);
    this.callDeepSeekAI(inputValue, newHistory);
  },

  // 调用 DeepSeek AI
  callDeepSeekAI(userInput, history) {
    wx.request({
      url: DEEPSEEK_API_URL,
      method: 'POST',
      header: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${DEEPSEEK_API_KEY}`
      },
      data: {
        model: 'deepseek-chat',
        messages: history,
        max_tokens: 300,
        temperature: 0.8,
        stream: false
      },
      success: (res) => {
        console.log('DeepSeek response:', res);
        
        if (res.statusCode === 200 && res.data?.choices?.[0]?.message?.content) {
          const aiResponse = res.data.choices[0].message.content;
          this.handleAIResponse(aiResponse);
        } else {
          console.error('DeepSeek error:', res);
          this.fallbackResponse(userInput);
        }
      },
      fail: (err) => {
        console.error('Request failed:', err);
        this.fallbackResponse(userInput);
      }
    });
  },

  // 处理AI响应
  handleAIResponse(response) {
    const { messages, conversationHistory } = this.data;
    
    // 更新对话历史
    const newHistory = [...conversationHistory, {
      role: 'assistant',
      content: response
    }];
    
    // 限制历史长度
    if (newHistory.length > 20) {
      newHistory.splice(1, 2);
    }
    
    const serviceMessage = {
      id: Date.now(),
      isSelf: false,
      content: response,
      time: this.formatTime(new Date())
    };
    
    const newMessages = [...messages, serviceMessage];
    
    this.setData({
      messages: newMessages,
      conversationHistory: newHistory,
      isTyping: false,
      scrollToView: `msg-${newMessages.length - 1}`
    });
    
    this.saveChatHistory(newMessages);
  },

  // 备用本地回复
  fallbackResponse(input) {
    const text = (input || '').trim().toLowerCase();
    
    const responses = [
      { keys: ['你好', '您好', 'hi', 'hello', '嗨', '哈喽', '在吗', '有人吗'], msg: '您好！我是客服小星，很高兴为您服务～请问有什么可以帮您？😊' },
      { keys: ['谢谢', '感谢', '多谢'], msg: '不客气！有问题随时找我～' },
      { keys: ['再见', '拜拜', '88'], msg: '再见！祝您入住愉快～👋' },
      { keys: ['预订', '预定', '订房', '预约'], msg: '您可以在首页或客房列表选择房间，点击"预订"填写信息即可。🏨' },
      { keys: ['退房', '几点退'], msg: '退房时间为中午12:00前，延迟退房请提前联系前台。🕐' },
      { keys: ['退款', '取消'], msg: '已支付订单可申请退款，我们会在24小时内处理。💰' },
      { keys: ['优惠', '折扣', '活动'], msg: '会员享9折优惠，还有积分福利哦！🎉' },
      { keys: ['会员'], msg: '在"我的"页面可注册会员，享专属优惠！⭐' },
      { keys: ['wifi', '无线', '网络'], msg: '所有客房配备免费高速WiFi～📶' },
      { keys: ['价格', '多少钱', '房价'], msg: '大床房428元，双床房388元，套房688元起。会员9折！💰' },
      { keys: ['地址', '位置', '在哪'], msg: '地址：深圳市南山区海岸城路168号。📍' },
      { keys: ['电话', '联系'], msg: '前台电话：400-888-9999 📞' }
    ];
    
    let response = '抱歉，网络有点问题。您可以拨打前台电话400-888-9999获取帮助～';
    
    for (const item of responses) {
      if (item.keys.some(k => text.includes(k))) {
        response = item.msg;
        break;
      }
    }
    
    const { messages } = this.data;
    const serviceMessage = {
      id: Date.now(),
      isSelf: false,
      content: response,
      time: this.formatTime(new Date())
    };
    
    const newMessages = [...messages, serviceMessage];
    
    this.setData({
      messages: newMessages,
      isTyping: false,
      scrollToView: `msg-${newMessages.length - 1}`
    });
    
    this.saveChatHistory(newMessages);
  },

  // 清空聊天记录
  clearHistory() {
    wx.showModal({
      title: '确认清空',
      content: '确定要清空所有聊天记录吗？',
      success: (res) => {
        if (res.confirm) {
          const userInfo = app.globalData.userInfo || {};
          const storageKey = `chat_history_${userInfo.id || 'guest'}`;
          wx.removeStorageSync(storageKey);
          
          // 重新加载（显示欢迎消息）
          this.loadChatHistory();
          wx.showToast({ title: '已清空' });
        }
      }
    });
  },

  // 拨打电话
  callPhone() {
    wx.makePhoneCall({
      phoneNumber: '400-888-9999'
    });
  }
});
