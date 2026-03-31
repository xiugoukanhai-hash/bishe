Page({
  data: {
    searchKey: '',
    currentCategory: '',
    categories: [
      { id: 'booking', name: '预订相关' },
      { id: 'payment', name: '支付问题' },
      { id: 'checkin', name: '入住退房' },
      { id: 'member', name: '会员服务' },
      { id: 'other', name: '其他问题' }
    ],
    faqList: [
      { id: 1, category: 'booking', question: '如何预订房间？', answer: '您可以在首页浏览房间，选择心仪的房型后点击"预订"按钮，填写入住日期和个人信息后提交订单即可。', expanded: false },
      { id: 2, category: 'booking', question: '可以提前多久预订？', answer: '您可以提前30天预订房间，旺季建议提前预订以确保房源。', expanded: false },
      { id: 3, category: 'payment', question: '支持哪些支付方式？', answer: '目前支持微信支付和余额支付两种方式。', expanded: false },
      { id: 4, category: 'payment', question: '取消订单如何退款？', answer: '订单取消后，款项将在3-5个工作日内原路退回。', expanded: false },
      { id: 5, category: 'checkin', question: '入住时间是几点？', answer: '入住时间为下午14:00以后，如提前到达可先寄存行李。', expanded: false },
      { id: 6, category: 'checkin', question: '退房时间是几点？', answer: '退房时间为中午12:00前，延迟退房请提前联系前台。', expanded: false },
      { id: 7, category: 'member', question: '如何成为会员？', answer: '在"我的"页面点击注册会员，完善个人信息即可成为会员。', expanded: false },
      { id: 8, category: 'member', question: '会员有什么优惠？', answer: '会员可享受9折优惠、积分累积、专属活动等权益。', expanded: false }
    ],
    allFaqList: [],
    loading: false
  },

  onLoad() {
    this.setData({ allFaqList: this.data.faqList });
  },

  onSearch(e) {
    const searchKey = e.detail.value;
    this.setData({ searchKey });
    this.filterFaq();
  },

  switchCategory(e) {
    const { category } = e.currentTarget.dataset;
    this.setData({ currentCategory: category });
    this.filterFaq();
  },

  filterFaq() {
    const { searchKey, currentCategory, allFaqList } = this.data;
    
    let filtered = [...allFaqList];
    
    if (currentCategory) {
      filtered = filtered.filter(item => item.category === currentCategory);
    }
    
    if (searchKey) {
      const key = searchKey.toLowerCase();
      filtered = filtered.filter(item => 
        item.question.toLowerCase().includes(key) || 
        item.answer.toLowerCase().includes(key)
      );
    }
    
    this.setData({ faqList: filtered });
  },

  toggleAnswer(e) {
    const { index } = e.currentTarget.dataset;
    const faqList = [...this.data.faqList];
    faqList[index].expanded = !faqList[index].expanded;
    this.setData({ faqList });
  },

  goChat() {
    wx.navigateTo({ url: '/pages/service/chat/chat' });
  }
});
