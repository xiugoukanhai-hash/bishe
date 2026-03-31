import Vue from 'vue';
//配置路由
import VueRouter from 'vue-router'
Vue.use(VueRouter);
//1.创建组件
import Index from '@/views/index'
import Home from '@/views/home'
import Login from '@/views/login'
import NotFound from '@/views/404'
import UpdatePassword from '@/views/update-password'
import pay from '@/views/pay'
import register from '@/views/register'
import center from '@/views/center'
    import news from '@/views/modules/news/list'
    import kefangxinxi from '@/views/modules/kefangxinxi/list'
    import huiyuan from '@/views/modules/huiyuan/list'
    import qingsaofangjian from '@/views/modules/qingsaofangjian/list'
    import huiyuanyuyue from '@/views/modules/huiyuanyuyue/list'
    import storeup from '@/views/modules/storeup/list'
    import yonghutuifang from '@/views/modules/yonghutuifang/list'
    import yonghuyuyue from '@/views/modules/yonghuyuyue/list'
    import yonghuruzhu from '@/views/modules/yonghuruzhu/list'
    import huiyuantuifang from '@/views/modules/huiyuantuifang/list'
    import yonghu from '@/views/modules/yonghu/list'
    import chat from '@/views/modules/chat/list'
    import yonghuquxiao from '@/views/modules/yonghuquxiao/list'
    import huiyuanquxiao from '@/views/modules/huiyuanquxiao/list'
    import messages from '@/views/modules/messages/list'
    import qingjierenyuan from '@/views/modules/qingjierenyuan/list'
    import config from '@/views/modules/config/list'
    import huiyuanruzhu from '@/views/modules/huiyuanruzhu/list'
    import qiantairenyuan from '@/views/modules/qiantairenyuan/list'
    import tongzhi from '@/views/modules/tongzhi/list'
    import aikefuzhishiku from '@/views/modules/aikefuzhishiku/list'
    import fuwujilu from '@/views/modules/fuwujilu/list'
    import jifen from '@/views/modules/jifen/list'
    import daiqingsao from '@/views/modules/daiqingsao/list'
    import pinglun from '@/views/modules/pinglun/list'
    
    // 前台人员页面
    import qiantaiDashboard from '@/views/qiantai/dashboard'
    import qiantaiCheckin from '@/views/qiantai/checkin'
    import qiantaiCheckout from '@/views/qiantai/checkout'
    import qiantaiRoomstatus from '@/views/qiantai/roomstatus'
    
    // 统计报表页面
    import statistics from '@/views/modules/statistics/index'
    
    // 管理员工作台页面
    import adminDashboard from '@/views/admin/dashboard'
    
    // 订单综合管理页面
    import orderList from '@/views/order/list'
    
    // 系统配置页面
    import configHotel from '@/views/config/hotel'


//2.配置路由   注意：名字
const routes = [{
    path: '/index',
    name: '首页',
    component: Index,
    children: [{
      // 这里不设置值，是把main作为默认页面
      path: '/',
      name: '首页',
      component: Home,
      meta: {icon:'', title:'center'}
    }, {
      path: '/updatePassword',
      name: '修改密码',
      component: UpdatePassword,
      meta: {icon:'', title:'updatePassword'}
    }, {
      path: '/pay',
      name: '支付',
      component: pay,
      meta: {icon:'', title:'pay'}
    }, {
      path: '/center',
      name: '个人信息',
      component: center,
      meta: {icon:'', title:'center'}
    }
      ,{
	path: '/news',
        name: '酒店资讯',
        component: news
      }
      ,{
	path: '/kefangxinxi',
        name: '客房信息',
        component: kefangxinxi
      }
      ,{
	path: '/huiyuan',
        name: '会员',
        component: huiyuan
      }
      ,{
	path: '/qingsaofangjian',
        name: '清扫房间',
        component: qingsaofangjian
      }
      ,{
	path: '/huiyuanyuyue',
        name: '会员预约',
        component: huiyuanyuyue
      }
      ,{
	path: '/storeup',
        name: '我的收藏管理',
        component: storeup
      }
      ,{
	path: '/yonghutuifang',
        name: '用户退房',
        component: yonghutuifang
      }
      ,{
	path: '/yonghuyuyue',
        name: '用户预约',
        component: yonghuyuyue
      }
      ,{
	path: '/yonghuruzhu',
        name: '用户入住',
        component: yonghuruzhu
      }
      ,{
	path: '/huiyuantuifang',
        name: '会员退房',
        component: huiyuantuifang
      }
      ,{
	path: '/yonghu',
        name: '用户',
        component: yonghu
      }
      ,{
	path: '/chat',
        name: '在线客服',
        component: chat
      }
      ,{
	path: '/yonghuquxiao',
        name: '用户取消',
        component: yonghuquxiao
      }
      ,{
	path: '/huiyuanquxiao',
        name: '会员取消',
        component: huiyuanquxiao
      }
      ,{
	path: '/messages',
        name: '留言板管理',
        component: messages
      }
      ,{
	path: '/qingjierenyuan',
        name: '清洁人员',
        component: qingjierenyuan
      }
      ,{
	path: '/config',
        name: '轮播图管理',
        component: config
      }
      ,{
	path: '/pinglun',
        name: '客户评价管理',
        component: pinglun
      }
      ,{
	path: '/huiyuanruzhu',
        name: '会员入住',
        component: huiyuanruzhu
      }
      ,{
	path: '/qiantairenyuan',
        name: '前台人员',
        component: qiantairenyuan
      }
      ,{
	path: '/tongzhi',
        name: '通知管理',
        component: tongzhi
      }
      ,{
	path: '/aikefuzhishiku',
        name: 'AI知识库',
        component: aikefuzhishiku
      }
      ,{
	path: '/fuwujilu',
        name: '服务记录',
        component: fuwujilu
      }
      ,{
	path: '/jifen',
        name: '积分记录',
        component: jifen
      }
      ,{
	path: '/daiqingsao',
        name: '待清扫房间',
        component: daiqingsao
      }
      // 前台人员路由
      ,{
        path: '/qiantai/dashboard',
        name: '前台工作台',
        component: qiantaiDashboard
      }
      ,{
        path: '/qiantai/checkin',
        name: '入住办理',
        component: qiantaiCheckin
      }
      ,{
        path: '/qiantai/checkout',
        name: '退房办理',
        component: qiantaiCheckout
      }
      ,{
        path: '/qiantai/roomstatus',
        name: '房态查看',
        component: qiantaiRoomstatus
      }
      // 统计报表路由
      ,{
        path: '/statistics',
        name: '统计报表',
        component: statistics
      }
      // 管理员工作台路由
      ,{
        path: '/admin/dashboard',
        name: '管理员工作台',
        component: adminDashboard
      }
      // 订单综合管理路由
      ,{
        path: '/order/list',
        name: '订单管理',
        component: orderList
      }
      // 系统配置路由
      ,{
        path: '/config/hotel',
        name: '酒店配置',
        component: configHotel
      }
    ]
  },
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: {icon:'', title:'login'}
  },
  {
    path: '/register',
    name: 'register',
    component: register,
    meta: {icon:'', title:'register'}
  },
  {
    path: '/',
    name: '首页',
    redirect: '/index'
  }, /*默认跳转路由*/
  {
    path: '*',
    component: NotFound
  }
]
//3.实例化VueRouter  注意：名字
const router = new VueRouter({
  mode: 'hash',
  /*hash模式改为history*/
  routes // （缩写）相当于 routes: routes
})

export default router;
