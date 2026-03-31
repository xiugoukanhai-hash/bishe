const api = require('../../../config/api');

// DeepSeek AI 配置
const DEEPSEEK_API_KEY = 'sk-352e58d90401415f90e44df381869d1a';
const DEEPSEEK_API_URL = 'https://api.deepseek.com/v1/chat/completions';

// 系统提示词 - 设定AI角色和知识背景
const SYSTEM_PROMPT = `你是一个专业友好的酒店智能客服助手，名叫"小智"。你服务于一家名为"星际酒店"的高档酒店。

【酒店基本信息】
- 酒店名称：星际酒店
- 地址：广东省深圳市南山区海岸城路168号
- 前台电话：400-888-9999
- 入住时间：下午14:00后
- 退房时间：中午12:00前

【房型与价格】
- 豪华大床房：428元/晚
- 标准双床房：388元/晚
- 商务套房：688元/晚
- 总统套房：1288元/晚

【会员权益】
- 会员享9折优惠
- 积分可抵现
- 免费延迟退房（需提前申请）
- 免费早餐（套房会员）

【设施服务】
- 免费高速WiFi
- 24小时热水
- 免费停车场
- 健身房、游泳池
- 中西餐厅

【预订与退款政策】
- 在线预订需填写入住人信息
- 支持微信支付
- 未支付订单可随时取消
- 已支付订单可申请退款，24小时内处理
- 入住当天18:00后取消扣除首晚房费

【回复要求】
1. 始终保持热情、专业、耐心
2. 回答简洁明了，不超过100字
3. 如遇到无法回答的问题，引导用户拨打前台电话
4. 适当使用表情让对话更亲切
5. 主动询问是否还有其他需要帮助的`;

Page({
  data: {
    messages: [],
    inputValue: '',
    isTyping: false,
    scrollToView: '',
    conversationHistory: [], // 保存对话历史用于上下文
    quickQuestions: [
      '如何预订房间？',
      '退房时间是几点？',
      '有什么优惠活动？',
      '如何成为会员？',
      '房间有无线网络吗？'
    ]
  },

  onLoad() {
    // 初始化对话历史
    this.setData({
      conversationHistory: [
        { role: 'system', content: SYSTEM_PROMPT }
      ]
    });
  },

  onInput(e) {
    this.setData({ inputValue: e.detail.value });
  },

  sendQuickQuestion(e) {
    const { question } = e.currentTarget.dataset;
    this.setData({ inputValue: question });
    this.sendMessage();
  },

  sendMessage() {
    const { inputValue, messages, conversationHistory } = this.data;
    
    if (!inputValue.trim()) return;
    
    const userMessage = inputValue.trim();
    const newMessages = [...messages, {
      role: 'user',
      content: userMessage
    }];
    
    // 添加到对话历史
    const newHistory = [...conversationHistory, {
      role: 'user',
      content: userMessage
    }];
    
    this.setData({
      messages: newMessages,
      conversationHistory: newHistory,
      inputValue: '',
      isTyping: true,
      scrollToView: `msg-${newMessages.length - 1}`
    });
    
    // 调用 DeepSeek AI
    this.callDeepSeekAI(userMessage, newHistory);
  },

  // 调用 DeepSeek AI API
  callDeepSeekAI(question, history) {
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
        max_tokens: 500,
        temperature: 0.7,
        stream: false
      },
      success: (res) => {
        console.log('DeepSeek API response:', res);
        
        if (res.statusCode === 200 && res.data?.choices?.[0]?.message?.content) {
          const aiResponse = res.data.choices[0].message.content;
          this.handleAIResponse(aiResponse);
        } else {
          console.error('DeepSeek API error:', res);
          // API调用失败时使用本地备用回复
          this.fallbackResponse(question);
        }
      },
      fail: (err) => {
        console.error('DeepSeek API request failed:', err);
        // 网络错误时使用本地备用回复
        this.fallbackResponse(question);
      }
    });
  },

  // 处理AI响应
  handleAIResponse(response) {
    const { messages, conversationHistory } = this.data;
    
    // 添加AI回复到对话历史
    const newHistory = [...conversationHistory, {
      role: 'assistant',
      content: response
    }];
    
    // 限制历史记录长度，避免token过多
    if (newHistory.length > 20) {
      newHistory.splice(1, 2); // 保留system prompt，删除最早的一轮对话
    }
    
    const newMessages = [...messages, {
      role: 'ai',
      content: response
    }];
    
    this.setData({
      messages: newMessages,
      conversationHistory: newHistory,
      isTyping: false,
      scrollToView: `msg-${newMessages.length - 1}`
    });
  },

  // 备用本地回复（当API不可用时）
  fallbackResponse(question) {
    const q = (question || '').trim().toLowerCase();
    const responses = [
      { keys: ['你好', '您好', 'hi', 'hello', '嗨', '哈喽', '在吗', '有人吗', '喂'], msg: '您好呀！很高兴为您服务～请问有什么可以帮您的？😊' },
      { keys: ['谢谢', '感谢', '多谢'], msg: '不客气！很高兴能帮到您，有其他问题随时找我哦～' },
      { keys: ['再见', '拜拜', '88'], msg: '再见！祝您入住愉快，期待下次为您服务～👋' },
      { keys: ['预订', '预定', '订房', '预约', '怎么订', '如何订'], msg: '您可以在首页或客房列表浏览房间，选择心仪的房间后点击"预订"按钮，填写入住人信息并完成支付即可。🏨' },
      { keys: ['支付', '付款', '怎么付', '如何付'], msg: '预订后在支付页面选择微信支付完成付款，支付成功后订单状态会变为"已支付"。💳' },
      { keys: ['退房', '退房时间', '几点退'], msg: '退房时间为中午12:00前，如需延迟退房请提前联系前台。🕐' },
      { keys: ['入住', '入住时间', '几点入住'], msg: '入住时间为下午14:00后，如提前到达可将行李寄存于前台。🛎️' },
      { keys: ['优惠', '活动', '折扣', '促销'], msg: '目前有会员专享9折、连住优惠等活动，成为会员可享受更多专属优惠！🎉' },
      { keys: ['会员', '成为会员', '注册会员'], msg: '在"我的"页面点击"注册会员"，完成注册后即可享受会员专属折扣和积分。⭐' },
      { keys: ['wifi', '无线', '网络', '上网'], msg: '所有客房都配备免费高速WiFi，入住后可在房间内查看连接信息。📶' },
      { keys: ['取消', '退订', '退款'], msg: '未支付订单可直接取消。已支付订单可申请退款，我们会在24小时内处理。💰' },
      { keys: ['电话', '联系', '人工'], msg: '您可拨打前台电话400-888-9999获取人工帮助。📞' },
      { keys: ['地址', '位置', '在哪', '怎么去'], msg: '酒店地址：广东省深圳市南山区海岸城路168号，毗邻海岸城购物中心。📍' },
      { keys: ['价格', '多少钱', '房价'], msg: '豪华大床房428元/晚，标准双床房388元/晚，商务套房688元/晚，总统套房1288元/晚。会员享9折优惠！💰' }
    ];
    
    let response = '抱歉，AI服务暂时不可用。您可以试试问我：如何预订、退房时间、会员优惠等。或拨打前台电话400-888-9999获取人工帮助～';
    
    for (const item of responses) {
      if (item.keys.some(k => q.includes(k))) {
        response = item.msg;
        break;
      }
    }
    
    const { messages } = this.data;
    const newMessages = [...messages, {
      role: 'ai',
      content: response
    }];
    
    this.setData({
      messages: newMessages,
      isTyping: false,
      scrollToView: `msg-${newMessages.length - 1}`
    });
  },

  // 清空对话
  clearChat() {
    wx.showModal({
      title: '确认',
      content: '确定要清空对话记录吗？',
      success: (res) => {
        if (res.confirm) {
          this.setData({
            messages: [],
            conversationHistory: [
              { role: 'system', content: SYSTEM_PROMPT }
            ]
          });
        }
      }
    });
  }
});
