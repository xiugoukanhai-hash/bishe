<template>
    <div class="statistics-container">
        <!-- 今日概览 -->
        <el-row :gutter="20" class="overview-row">
            <el-col :span="6">
                <div class="stat-card blue">
                    <div class="stat-icon">
                        <i class="el-icon-s-home"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">{{ overview.todayCheckIn || 0 }}</div>
                        <div class="stat-label">今日入住</div>
                    </div>
                </div>
            </el-col>
            <el-col :span="6">
                <div class="stat-card green">
                    <div class="stat-icon">
                        <i class="el-icon-s-release"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">{{ overview.todayCheckOut || 0 }}</div>
                        <div class="stat-label">今日退房</div>
                    </div>
                </div>
            </el-col>
            <el-col :span="6">
                <div class="stat-card orange">
                    <div class="stat-icon">
                        <i class="el-icon-s-order"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">{{ overview.todayBooking || 0 }}</div>
                        <div class="stat-label">今日预约</div>
                    </div>
                </div>
            </el-col>
            <el-col :span="6">
                <div class="stat-card red">
                    <div class="stat-icon">
                        <i class="el-icon-money"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">¥{{ formatMoney(overview.todayRevenue) }}</div>
                        <div class="stat-label">今日营收</div>
                    </div>
                </div>
            </el-col>
        </el-row>
        
        <el-row :gutter="20">
            <!-- 房态统计 -->
            <el-col :span="8">
                <el-card class="box-card">
                    <div slot="header" class="card-header">
                        <span>房态分布</span>
                    </div>
                    <div class="room-status-stats">
                        <div class="status-item" v-for="(item, index) in roomStatusList" :key="index">
                            <div class="status-bar">
                                <div class="status-name">{{ item.name }}</div>
                                <el-progress 
                                    :percentage="item.percentage" 
                                    :color="item.color"
                                    :format="() => item.count + '间'">
                                </el-progress>
                            </div>
                        </div>
                        <div class="occupancy-info">
                            <span>当前入住率</span>
                            <el-progress 
                                type="circle" 
                                :percentage="overview.occupancyRate || 0"
                                :width="100"
                                :stroke-width="10"
                                :color="getOccupancyColor(overview.occupancyRate)">
                            </el-progress>
                        </div>
                    </div>
                </el-card>
            </el-col>
            
            <!-- 近7天营收趋势 -->
            <el-col :span="16">
                <el-card class="box-card">
                    <div slot="header" class="card-header">
                        <span>近7天营收趋势</span>
                    </div>
                    <div class="revenue-chart" ref="revenueChart"></div>
                </el-card>
            </el-col>
        </el-row>
        
        <el-row :gutter="20" style="margin-top: 20px;">
            <!-- 订单统计 -->
            <el-col :span="12">
                <el-card class="box-card">
                    <div slot="header" class="card-header">
                        <span>近7天订单统计</span>
                    </div>
                    <div class="order-chart" ref="orderChart"></div>
                </el-card>
            </el-col>
            
            <!-- 房型统计 -->
            <el-col :span="12">
                <el-card class="box-card">
                    <div slot="header" class="card-header">
                        <span>房型预订排名</span>
                    </div>
                    <el-table :data="roomTypeStats" size="small" max-height="280">
                        <el-table-column prop="roomType" label="房型"></el-table-column>
                        <el-table-column prop="totalCount" label="房间数" width="80"></el-table-column>
                        <el-table-column prop="bookingCount" label="预订数" width="80"></el-table-column>
                        <el-table-column label="营收" width="120">
                            <template slot-scope="scope">
                                <span style="color: #F56C6C;">¥{{ formatMoney(scope.row.revenue) }}</span>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-col>
        </el-row>
        
        <!-- 月度报表 -->
        <el-card class="box-card" style="margin-top: 20px;">
            <div slot="header" class="card-header">
                <span>月度报表</span>
                <div>
                    <el-date-picker
                        v-model="selectedMonth"
                        type="month"
                        placeholder="选择月份"
                        value-format="yyyy-MM"
                        @change="loadMonthlyReport"
                        size="small">
                    </el-date-picker>
                </div>
            </div>
            <el-descriptions :column="4" border v-if="monthlyReport">
                <el-descriptions-item label="总营收">
                    <span style="color: #F56C6C; font-size: 18px; font-weight: bold;">
                        ¥{{ formatMoney(monthlyReport.totalRevenue) }}
                    </span>
                </el-descriptions-item>
                <el-descriptions-item label="总订单">
                    {{ monthlyReport.totalOrders }} 单
                </el-descriptions-item>
                <el-descriptions-item label="平均入住率">
                    {{ monthlyReport.averageOccupancy }}%
                </el-descriptions-item>
                <el-descriptions-item label="统计周期">
                    {{ monthlyReport.startDate }} 至 {{ monthlyReport.endDate }}
                </el-descriptions-item>
            </el-descriptions>
        </el-card>
    </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
    name: 'Statistics',
    data() {
        return {
            overview: {},
            roomStatus: {},
            roomStatusList: [],
            revenueData: [],
            orderData: [],
            roomTypeStats: [],
            monthlyReport: null,
            selectedMonth: '',
            revenueChart: null,
            orderChart: null
        };
    },
    mounted() {
        const now = new Date();
        this.selectedMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
        
        this.loadOverview();
        this.loadRoomStatus();
        this.loadRevenueData();
        this.loadOrderData();
        this.loadRoomTypeStats();
        this.loadMonthlyReport();
        
        window.addEventListener('resize', this.handleResize);
    },
    beforeDestroy() {
        window.removeEventListener('resize', this.handleResize);
        if (this.revenueChart) this.revenueChart.dispose();
        if (this.orderChart) this.orderChart.dispose();
    },
    methods: {
        async loadOverview() {
            try {
                const res = await this.$http({
                    url: `${this.$http.adornUrl('/statistics/todayOverview')}`,
                    method: 'get'
                });
                if (res.data.code === 0) {
                    this.overview = res.data.data;
                }
            } catch (error) {
                console.error('加载概览失败', error);
            }
        },
        async loadRoomStatus() {
            try {
                const res = await this.$http({
                    url: `${this.$http.adornUrl('/statistics/roomStatus')}`,
                    method: 'get'
                });
                if (res.data.code === 0) {
                    this.roomStatus = res.data.data;
                    this.processRoomStatus();
                }
            } catch (error) {
                console.error('加载房态统计失败', error);
            }
        },
        processRoomStatus() {
            const total = this.roomStatus.total || 1;
            this.roomStatusList = [
                { name: '空闲', count: this.roomStatus['空闲'] || 0, color: '#67C23A', percentage: Math.round((this.roomStatus['空闲'] || 0) / total * 100) },
                { name: '已预约', count: this.roomStatus['已预约'] || 0, color: '#E6A23C', percentage: Math.round((this.roomStatus['已预约'] || 0) / total * 100) },
                { name: '已入住', count: this.roomStatus['已入住'] || 0, color: '#409EFF', percentage: Math.round((this.roomStatus['已入住'] || 0) / total * 100) },
                { name: '待清扫', count: this.roomStatus['待清扫'] || 0, color: '#F56C6C', percentage: Math.round((this.roomStatus['待清扫'] || 0) / total * 100) }
            ];
        },
        async loadRevenueData() {
            const endDate = this.formatDate(new Date());
            const startDate = this.formatDate(new Date(Date.now() - 6 * 24 * 60 * 60 * 1000));
            
            try {
                const res = await this.$http({
                    url: `${this.$http.adornUrl('/statistics/revenue')}`,
                    method: 'get',
                    params: this.$http.adornParams({
                        startDate,
                        endDate,
                        dimension: 'day'
                    })
                });
                if (res.data.code === 0) {
                    this.revenueData = res.data.data || [];
                    this.initRevenueChart();
                }
            } catch (error) {
                console.error('加载营收数据失败', error);
            }
        },
        async loadOrderData() {
            const endDate = this.formatDate(new Date());
            const startDate = this.formatDate(new Date(Date.now() - 6 * 24 * 60 * 60 * 1000));
            
            try {
                const res = await this.$http({
                    url: `${this.$http.adornUrl('/statistics/orders')}`,
                    method: 'get',
                    params: this.$http.adornParams({
                        startDate,
                        endDate,
                        dimension: 'day'
                    })
                });
                if (res.data.code === 0) {
                    this.orderData = res.data.data || [];
                    this.initOrderChart();
                }
            } catch (error) {
                console.error('加载订单数据失败', error);
            }
        },
        async loadRoomTypeStats() {
            const endDate = this.formatDate(new Date());
            const startDate = this.formatDate(new Date(Date.now() - 30 * 24 * 60 * 60 * 1000));
            
            try {
                const res = await this.$http({
                    url: `${this.$http.adornUrl('/statistics/roomType')}`,
                    method: 'get',
                    params: this.$http.adornParams({
                        startDate,
                        endDate
                    })
                });
                if (res.data.code === 0) {
                    this.roomTypeStats = res.data.data || [];
                }
            } catch (error) {
                console.error('加载房型统计失败', error);
            }
        },
        async loadMonthlyReport() {
            if (!this.selectedMonth) return;
            
            const [year, month] = this.selectedMonth.split('-');
            
            try {
                const res = await this.$http({
                    url: `${this.$http.adornUrl('/statistics/monthlyReport')}`,
                    method: 'get',
                    params: this.$http.adornParams({
                        year: parseInt(year),
                        month: parseInt(month)
                    })
                });
                if (res.data.code === 0) {
                    this.monthlyReport = res.data.data;
                }
            } catch (error) {
                console.error('加载月度报表失败', error);
            }
        },
        initRevenueChart() {
            if (!this.$refs.revenueChart) return;
            
            if (this.revenueChart) {
                this.revenueChart.dispose();
            }
            
            this.revenueChart = echarts.init(this.$refs.revenueChart);
            
            const dates = this.revenueData.map(item => item.date.substring(5));
            const yonghuRevenue = this.revenueData.map(item => item.yonghuRevenue || 0);
            const huiyuanRevenue = this.revenueData.map(item => item.huiyuanRevenue || 0);
            
            const option = {
                tooltip: {
                    trigger: 'axis',
                    formatter: function(params) {
                        let result = params[0].axisValue + '<br/>';
                        params.forEach(param => {
                            result += `${param.marker} ${param.seriesName}: ¥${param.value}<br/>`;
                        });
                        return result;
                    }
                },
                legend: {
                    data: ['用户营收', '会员营收']
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    data: dates
                },
                yAxis: {
                    type: 'value',
                    axisLabel: {
                        formatter: '¥{value}'
                    }
                },
                series: [
                    {
                        name: '用户营收',
                        type: 'line',
                        stack: 'Total',
                        areaStyle: {},
                        emphasis: { focus: 'series' },
                        data: yonghuRevenue,
                        itemStyle: { color: '#409EFF' }
                    },
                    {
                        name: '会员营收',
                        type: 'line',
                        stack: 'Total',
                        areaStyle: {},
                        emphasis: { focus: 'series' },
                        data: huiyuanRevenue,
                        itemStyle: { color: '#67C23A' }
                    }
                ]
            };
            
            this.revenueChart.setOption(option);
        },
        initOrderChart() {
            if (!this.$refs.orderChart) return;
            
            if (this.orderChart) {
                this.orderChart.dispose();
            }
            
            this.orderChart = echarts.init(this.$refs.orderChart);
            
            const dates = this.orderData.map(item => item.date.substring(5));
            const yonghuOrders = this.orderData.map(item => item.yonghuOrders || 0);
            const huiyuanOrders = this.orderData.map(item => item.huiyuanOrders || 0);
            
            const option = {
                tooltip: {
                    trigger: 'axis'
                },
                legend: {
                    data: ['用户订单', '会员订单']
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    type: 'category',
                    data: dates
                },
                yAxis: {
                    type: 'value'
                },
                series: [
                    {
                        name: '用户订单',
                        type: 'bar',
                        data: yonghuOrders,
                        itemStyle: { color: '#409EFF' }
                    },
                    {
                        name: '会员订单',
                        type: 'bar',
                        data: huiyuanOrders,
                        itemStyle: { color: '#67C23A' }
                    }
                ]
            };
            
            this.orderChart.setOption(option);
        },
        handleResize() {
            if (this.revenueChart) this.revenueChart.resize();
            if (this.orderChart) this.orderChart.resize();
        },
        formatDate(date) {
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            return `${year}-${month}-${day}`;
        },
        formatMoney(value) {
            if (!value) return '0.00';
            return Number(value).toFixed(2);
        },
        getOccupancyColor(rate) {
            if (rate >= 80) return '#F56C6C';
            if (rate >= 60) return '#E6A23C';
            if (rate >= 40) return '#409EFF';
            return '#67C23A';
        }
    }
};
</script>

<style scoped>
.statistics-container {
    padding: 20px;
}

.overview-row {
    margin-bottom: 20px;
}

.stat-card {
    display: flex;
    align-items: center;
    padding: 20px;
    border-radius: 8px;
    color: #fff;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.stat-card.blue {
    background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
}

.stat-card.green {
    background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
}

.stat-card.orange {
    background: linear-gradient(135deg, #E6A23C 0%, #ebb563 100%);
}

.stat-card.red {
    background: linear-gradient(135deg, #F56C6C 0%, #f78989 100%);
}

.stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.2);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16px;
}

.stat-icon i {
    font-size: 28px;
}

.stat-value {
    font-size: 28px;
    font-weight: 600;
}

.stat-label {
    font-size: 14px;
    opacity: 0.9;
    margin-top: 4px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.room-status-stats {
    padding: 10px 0;
}

.status-item {
    margin-bottom: 15px;
}

.status-name {
    font-size: 14px;
    color: #606266;
    margin-bottom: 5px;
}

.occupancy-info {
    text-align: center;
    padding-top: 20px;
    border-top: 1px solid #EBEEF5;
}

.occupancy-info span {
    display: block;
    color: #909399;
    margin-bottom: 10px;
}

.revenue-chart,
.order-chart {
    height: 280px;
}
</style>
