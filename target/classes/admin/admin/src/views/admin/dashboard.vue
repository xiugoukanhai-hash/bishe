<template>
    <div class="admin-dashboard">
        <!-- 核心指标卡片 -->
        <el-row :gutter="20" class="stat-cards">
            <el-col :span="6" v-for="(card, index) in statCards" :key="index">
                <div class="stat-card" :style="{ borderColor: card.color }">
                    <div class="stat-icon" :style="{ backgroundColor: card.color }">
                        <i :class="card.icon"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">{{ card.value }}</div>
                        <div class="stat-label">{{ card.label }}</div>
                        <div class="stat-trend" v-if="card.trend !== undefined">
                            <span :class="card.trend >= 0 ? 'up' : 'down'">
                                {{ card.trend >= 0 ? '+' : '' }}{{ card.trend }}%
                            </span>
                            <span class="trend-text">较昨日</span>
                        </div>
                    </div>
                </div>
            </el-col>
        </el-row>
        
        <!-- 图表区域第一行 -->
        <el-row :gutter="20" style="margin-bottom: 20px;">
            <el-col :span="16">
                <el-card class="chart-card">
                    <div slot="header" class="card-header">
                        <span>近7日营收趋势</span>
                        <el-button type="text" @click="refreshRevenueChart">
                            <i class="el-icon-refresh"></i>
                        </el-button>
                    </div>
                    <div class="chart-container" ref="revenueChart"></div>
                </el-card>
            </el-col>
            <el-col :span="8">
                <el-card class="chart-card">
                    <div slot="header" class="card-header">
                        <span>房态分布</span>
                    </div>
                    <div class="chart-container" ref="roomStatusChart"></div>
                </el-card>
            </el-col>
        </el-row>
        
        <!-- 图表区域第二行 -->
        <el-row :gutter="20">
            <el-col :span="12">
                <el-card class="chart-card">
                    <div slot="header" class="card-header">
                        <span>房型预订排行</span>
                    </div>
                    <div class="chart-container" ref="roomTypeChart"></div>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card class="table-card">
                    <div slot="header" class="card-header">
                        <span>近期订单</span>
                        <el-button type="text" @click="$router.push('/order/list')">
                            查看全部 <i class="el-icon-arrow-right"></i>
                        </el-button>
                    </div>
                    <el-table :data="recentOrders" size="small" max-height="280" 
                              :header-cell-style="{background:'#f5f7fa'}">
                        <el-table-column prop="yuyuebianhao" label="订单号" width="140" show-overflow-tooltip>
                        </el-table-column>
                        <el-table-column label="类型" width="70">
                            <template slot-scope="scope">
                                <el-tag size="mini" :type="scope.row.userTypeCode === 'huiyuan' ? 'warning' : 'primary'">
                                    {{ scope.row.userType }}
                                </el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="xingming" label="客户" width="80"></el-table-column>
                        <el-table-column prop="kefanghao" label="房间" width="70"></el-table-column>
                        <el-table-column prop="jiage" label="金额" width="80">
                            <template slot-scope="scope">
                                <span style="color: #E6A23C;">¥{{ scope.row.jiage || 0 }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="状态" width="80">
                            <template slot-scope="scope">
                                <el-tag size="mini" :type="getStatusType(scope.row)">
                                    {{ scope.row.statusText }}
                                </el-tag>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-col>
        </el-row>

        <!-- 快捷操作区域 -->
        <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="24">
                <el-card>
                    <div slot="header">快捷操作</div>
                    <el-row :gutter="20">
                        <el-col :span="4" v-for="(action, index) in quickActions" :key="index">
                            <div class="quick-action" @click="handleQuickAction(action)">
                                <i :class="action.icon" :style="{ color: action.color }"></i>
                                <span>{{ action.label }}</span>
                            </div>
                        </el-col>
                    </el-row>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
    name: 'AdminDashboard',
    data() {
        return {
            statCards: [
                { icon: 'el-icon-money', label: '今日营收', value: '¥0', color: '#409EFF', trend: 0 },
                { icon: 'el-icon-s-order', label: '今日订单', value: '0', color: '#67C23A', trend: 0 },
                { icon: 'el-icon-s-home', label: '今日入住', value: '0', color: '#E6A23C', trend: 0 },
                { icon: 'el-icon-s-data', label: '入住率', value: '0%', color: '#F56C6C', trend: undefined }
            ],
            recentOrders: [],
            revenueChartInstance: null,
            roomStatusChartInstance: null,
            roomTypeChartInstance: null,
            quickActions: [
                { icon: 'el-icon-plus', label: '新增客房', color: '#409EFF', action: 'addRoom' },
                { icon: 'el-icon-s-check', label: '审核订单', color: '#67C23A', action: 'auditOrder' },
                { icon: 'el-icon-user', label: '会员管理', color: '#E6A23C', action: 'memberManage' },
                { icon: 'el-icon-message', label: '通知管理', color: '#909399', action: 'notification' },
                { icon: 'el-icon-data-analysis', label: '数据统计', color: '#F56C6C', action: 'statistics' },
                { icon: 'el-icon-setting', label: '系统设置', color: '#606266', action: 'settings' }
            ],
            roomTypeStats: []
        };
    },
    mounted() {
        this.loadOverview();
        this.loadRecentOrders();
        this.$nextTick(() => {
            this.initCharts();
        });
        window.addEventListener('resize', this.handleResize);
    },
    methods: {
        async loadOverview() {
            try {
                const res = await this.$http.get('/statistics/todayOverview');
                if (res.data.code === 0) {
                    const data = res.data.data;
                    this.statCards[0].value = `¥${data.todayRevenue || 0}`;
                    this.statCards[1].value = data.todayBooking || 0;
                    this.statCards[2].value = data.todayCheckIn || 0;
                    this.statCards[3].value = `${data.occupancyRate || 0}%`;
                }
            } catch (error) {
                console.error('加载概览失败', error);
            }
        },
        async loadRecentOrders() {
            try {
                const res = await this.$http.get('/order/list', {
                    params: { page: 1, limit: 8, type: 'all' }
                });
                if (res.data.code === 0 && res.data.data) {
                    this.recentOrders = res.data.data.list || [];
                }
            } catch (error) {
                console.error('加载订单失败', error);
            }
        },
        async initCharts() {
            await this.loadRevenueChart();
            await this.loadRoomStatusChart();
            await this.loadRoomTypeChart();
        },
        async loadRevenueChart() {
            const chartDom = this.$refs.revenueChart;
            if (!chartDom) return;
            
            const chart = echarts.init(chartDom);
            this.revenueChartInstance = chart;
            
            const dates = [];
            const now = new Date();
            for (let i = 6; i >= 0; i--) {
                const d = new Date(now);
                d.setDate(d.getDate() - i);
                dates.push(`${d.getMonth() + 1}/${d.getDate()}`);
            }
            
            let revenueData = [0, 0, 0, 0, 0, 0, 0];
            try {
                const endDate = this.formatDate(new Date());
                const startDate = this.formatDate(new Date(Date.now() - 6 * 24 * 60 * 60 * 1000));
                const res = await this.$http.get('/statistics/revenue', {
                    params: { startDate, endDate, dimension: 'day' }
                });
                if (res.data.code === 0 && res.data.data) {
                    if (Array.isArray(res.data.data)) {
                        revenueData = res.data.data.map(item => item.revenue || 0);
                    } else if (res.data.data.values) {
                        revenueData = res.data.data.values;
                    }
                }
            } catch (error) {
                console.error('加载营收数据失败', error);
            }
            
            const option = {
                tooltip: { 
                    trigger: 'axis',
                    formatter: '{b}<br/>营收: ¥{c}'
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    type: 'category',
                    data: dates,
                    boundaryGap: false
                },
                yAxis: { 
                    type: 'value',
                    axisLabel: {
                        formatter: '¥{value}'
                    }
                },
                series: [{
                    name: '营收',
                    type: 'line',
                    smooth: true,
                    data: revenueData,
                    areaStyle: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
                        ])
                    },
                    lineStyle: { color: '#409EFF', width: 2 },
                    itemStyle: { color: '#409EFF' }
                }]
            };
            
            chart.setOption(option);
        },
        async loadRoomStatusChart() {
            const chartDom = this.$refs.roomStatusChart;
            if (!chartDom) return;
            
            const chart = echarts.init(chartDom);
            this.roomStatusChartInstance = chart;
            
            let pieData = [
                { value: 0, name: '空闲', itemStyle: { color: '#67C23A' } },
                { value: 0, name: '已入住', itemStyle: { color: '#409EFF' } },
                { value: 0, name: '已预订', itemStyle: { color: '#E6A23C' } },
                { value: 0, name: '待清扫', itemStyle: { color: '#909399' } },
                { value: 0, name: '维修中', itemStyle: { color: '#F56C6C' } }
            ];
            
            try {
                const res = await this.$http.get('/statistics/roomStatus');
                if (res.data.code === 0 && res.data.data) {
                    const data = res.data.data;
                    pieData[0].value = data['空闲'] || 0;
                    pieData[1].value = data['已入住'] || 0;
                    pieData[2].value = data['已预订'] || data['已预约'] || 0;
                    pieData[3].value = data['待清扫'] || 0;
                    pieData[4].value = data['维修中'] || 0;
                }
            } catch (error) {
                console.error('加载房态统计失败', error);
            }
            
            const option = {
                tooltip: { 
                    trigger: 'item',
                    formatter: '{b}: {c}间 ({d}%)'
                },
                legend: { 
                    bottom: 0,
                    itemWidth: 10,
                    itemHeight: 10
                },
                series: [{
                    type: 'pie',
                    radius: ['40%', '65%'],
                    center: ['50%', '45%'],
                    avoidLabelOverlap: false,
                    label: {
                        show: false,
                        position: 'center'
                    },
                    emphasis: {
                        label: {
                            show: true,
                            fontSize: '16',
                            fontWeight: 'bold'
                        }
                    },
                    labelLine: { show: false },
                    data: pieData
                }]
            };
            
            chart.setOption(option);
        },
        async loadRoomTypeChart() {
            const chartDom = this.$refs.roomTypeChart;
            if (!chartDom) return;
            
            const chart = echarts.init(chartDom);
            this.roomTypeChartInstance = chart;
            
            let roomTypes = [];
            let bookingCounts = [];
            
            try {
                const res = await this.$http.get('/statistics/roomTypeRanking');
                if (res.data.code === 0 && res.data.data) {
                    const data = res.data.data;
                    roomTypes = data.map(item => item.roomType || item.kefangleixing);
                    bookingCounts = data.map(item => item.count || item.bookingCount);
                }
            } catch (error) {
                console.error('加载房型统计失败', error);
                roomTypes = ['大床房', '双床房', '豪华套房', '商务间', '家庭房'];
                bookingCounts = [0, 0, 0, 0, 0];
            }
            
            const option = {
                tooltip: { 
                    trigger: 'axis',
                    axisPointer: { type: 'shadow' }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    type: 'category',
                    data: roomTypes,
                    axisLabel: {
                        interval: 0,
                        rotate: 30
                    }
                },
                yAxis: { type: 'value' },
                series: [{
                    name: '预订量',
                    type: 'bar',
                    data: bookingCounts,
                    barWidth: '50%',
                    itemStyle: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                            { offset: 0, color: '#409EFF' },
                            { offset: 1, color: '#66b1ff' }
                        ]),
                        borderRadius: [4, 4, 0, 0]
                    }
                }]
            };
            
            chart.setOption(option);
        },
        refreshRevenueChart() {
            this.loadRevenueChart();
            this.$message.success('数据已刷新');
        },
        getStatusType(row) {
            const status = row.statusText;
            if (status === '待审核') return 'warning';
            if (status === '已支付') return 'success';
            if (status === '已取消') return 'info';
            if (status === '待支付') return '';
            return 'info';
        },
        handleQuickAction(action) {
            switch (action.action) {
                case 'addRoom':
                    this.$router.push('/kefangxinxi');
                    break;
                case 'auditOrder':
                    this.$router.push('/order/list');
                    break;
                case 'memberManage':
                    this.$router.push('/huiyuan');
                    break;
                case 'notification':
                    this.$router.push('/tongzhi');
                    break;
                case 'statistics':
                    this.$router.push('/statistics');
                    break;
                case 'settings':
                    this.$router.push('/config/hotel');
                    break;
                default:
                    this.$message.info('功能开发中');
            }
        },
        handleResize() {
            if (this.revenueChartInstance) this.revenueChartInstance.resize();
            if (this.roomStatusChartInstance) this.roomStatusChartInstance.resize();
            if (this.roomTypeChartInstance) this.roomTypeChartInstance.resize();
        },
        formatDate(date) {
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            return `${year}-${month}-${day}`;
        }
    },
    beforeDestroy() {
        window.removeEventListener('resize', this.handleResize);
        if (this.revenueChartInstance) this.revenueChartInstance.dispose();
        if (this.roomStatusChartInstance) this.roomStatusChartInstance.dispose();
        if (this.roomTypeChartInstance) this.roomTypeChartInstance.dispose();
    }
};
</script>

<style scoped>
.admin-dashboard {
    padding: 20px;
    background: #f0f2f5;
    min-height: calc(100vh - 84px);
}

.stat-cards {
    margin-bottom: 20px;
}

.stat-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    border-left: 4px solid;
    transition: transform 0.3s;
}

.stat-card:hover {
    transform: translateY(-3px);
}

.stat-icon {
    width: 56px;
    height: 56px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16px;
    flex-shrink: 0;
}

.stat-icon i {
    font-size: 26px;
    color: #fff;
}

.stat-content {
    flex: 1;
}

.stat-value {
    font-size: 26px;
    font-weight: 600;
    color: #303133;
    line-height: 1.2;
}

.stat-label {
    font-size: 14px;
    color: #909399;
    margin-top: 4px;
}

.stat-trend {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
}

.stat-trend .up {
    color: #67C23A;
    font-weight: 500;
}

.stat-trend .down {
    color: #F56C6C;
    font-weight: 500;
}

.stat-trend .trend-text {
    margin-left: 4px;
}

.chart-card, .table-card {
    height: 360px;
}

.chart-card ::v-deep .el-card__body {
    padding: 10px 15px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.chart-container {
    height: 280px;
}

.quick-action {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
    cursor: pointer;
    border-radius: 8px;
    transition: background-color 0.3s;
}

.quick-action:hover {
    background-color: #f5f7fa;
}

.quick-action i {
    font-size: 32px;
    margin-bottom: 10px;
}

.quick-action span {
    font-size: 14px;
    color: #606266;
}
</style>
