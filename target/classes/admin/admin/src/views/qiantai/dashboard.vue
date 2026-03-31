<template>
    <div class="dashboard-container">
        <!-- 今日概览 -->
        <el-row :gutter="20" class="stat-row">
            <el-col :span="6">
                <div class="stat-card">
                    <div class="stat-icon" style="background: #409EFF;">
                        <i class="el-icon-s-home"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">{{ overview.todayCheckIn || 0 }}</div>
                        <div class="stat-label">今日入住</div>
                    </div>
                </div>
            </el-col>
            <el-col :span="6">
                <div class="stat-card">
                    <div class="stat-icon" style="background: #67C23A;">
                        <i class="el-icon-s-release"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">{{ overview.todayCheckOut || 0 }}</div>
                        <div class="stat-label">今日退房</div>
                    </div>
                </div>
            </el-col>
            <el-col :span="6">
                <div class="stat-card">
                    <div class="stat-icon" style="background: #E6A23C;">
                        <i class="el-icon-s-order"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">{{ overview.pendingOrders || 0 }}</div>
                        <div class="stat-label">待审核订单</div>
                    </div>
                </div>
            </el-col>
            <el-col :span="6">
                <div class="stat-card">
                    <div class="stat-icon" style="background: #F56C6C;">
                        <i class="el-icon-s-grid"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">{{ overview.freeRooms || 0 }}</div>
                        <div class="stat-label">空闲房间</div>
                    </div>
                </div>
            </el-col>
        </el-row>
        
        <!-- 房态统计 -->
        <el-row :gutter="20">
            <el-col :span="12">
                <el-card class="box-card">
                    <div slot="header" class="card-header">
                        <span>房态统计</span>
                    </div>
                    <div class="room-status-summary">
                        <div class="status-item">
                            <div class="status-count free">{{ roomStatus['空闲'] || 0 }}</div>
                            <div class="status-name">空闲</div>
                        </div>
                        <div class="status-item">
                            <div class="status-count booked">{{ roomStatus['已预约'] || 0 }}</div>
                            <div class="status-name">已预约</div>
                        </div>
                        <div class="status-item">
                            <div class="status-count occupied">{{ roomStatus['已入住'] || 0 }}</div>
                            <div class="status-name">已入住</div>
                        </div>
                        <div class="status-item">
                            <div class="status-count cleaning">{{ roomStatus['待清扫'] || 0 }}</div>
                            <div class="status-name">待清扫</div>
                        </div>
                    </div>
                    <div class="occupancy-rate">
                        <span>当前入住率：</span>
                        <el-progress 
                            :percentage="overview.occupancyRate || 0" 
                            :format="format"
                            :stroke-width="18">
                        </el-progress>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card class="box-card">
                    <div slot="header" class="card-header">
                        <span>待处理事项</span>
                        <el-badge :value="pendingItems.length" class="badge-item" v-if="pendingItems.length > 0"></el-badge>
                    </div>
                    <div class="pending-list">
                        <div class="pending-item" v-for="item in pendingItems" :key="item.id">
                            <el-tag :type="item.tagType" size="small">{{ item.type }}</el-tag>
                            <span class="pending-content">{{ item.content }}</span>
                            <el-button size="mini" type="primary" @click="handlePending(item)">
                                处理
                            </el-button>
                        </div>
                        <div class="empty-text" v-if="pendingItems.length === 0">
                            <i class="el-icon-circle-check"></i>
                            <span>暂无待处理事项</span>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>
        
        <!-- 快捷操作 -->
        <el-card class="quick-actions">
            <div slot="header" class="card-header">
                <span>快捷操作</span>
            </div>
            <el-row :gutter="20">
                <el-col :span="6">
                    <div class="action-btn" @click="goTo('/qiantai/checkin')">
                        <i class="el-icon-s-home"></i>
                        <span>入住办理</span>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="action-btn" @click="goTo('/qiantai/checkout')">
                        <i class="el-icon-s-release"></i>
                        <span>退房办理</span>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="action-btn" @click="goTo('/modules/yonghuyuyue/list')">
                        <i class="el-icon-s-order"></i>
                        <span>预约管理</span>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="action-btn" @click="goTo('/qiantai/roomstatus')">
                        <i class="el-icon-s-grid"></i>
                        <span>房态查看</span>
                    </div>
                </el-col>
            </el-row>
        </el-card>
        
        <!-- 今日入住/退房列表 -->
        <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="12">
                <el-card class="box-card">
                    <div slot="header" class="card-header">
                        <span>今日预计入住</span>
                    </div>
                    <el-table :data="todayCheckInList" size="small" max-height="300">
                        <el-table-column prop="kefanghao" label="房间号" width="70"></el-table-column>
                        <el-table-column label="类型" width="60">
                            <template slot-scope="scope">
                                <el-tag :type="scope.row.userType === 'huiyuan' ? 'warning' : 'primary'" size="mini">
                                    {{ scope.row.userType === 'huiyuan' ? '会员' : '用户' }}
                                </el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="xingming" label="入住人" width="80"></el-table-column>
                        <el-table-column prop="shouji" label="手机号" width="120"></el-table-column>
                        <el-table-column label="状态" width="80">
                            <template slot-scope="scope">
                                <el-tag :type="getYuyueStatusType(scope.row.yuyuezhuangtai)" size="mini">
                                    {{ getYuyueStatusText(scope.row.yuyuezhuangtai) }}
                                </el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="80">
                            <template slot-scope="scope">
                                <el-button 
                                    type="text" 
                                    size="mini" 
                                    @click="handleQuickCheckIn(scope.row)"
                                    v-if="scope.row.yuyuezhuangtai === 'paid'">
                                    办理
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <div class="empty-text" v-if="todayCheckInList.length === 0">
                        今日暂无预约入住
                    </div>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card class="box-card">
                    <div slot="header" class="card-header">
                        <span>今日预计退房</span>
                    </div>
                    <el-table :data="todayCheckOutList" size="small" max-height="300">
                        <el-table-column prop="kefanghao" label="房间号" width="70"></el-table-column>
                        <el-table-column label="类型" width="60">
                            <template slot-scope="scope">
                                <el-tag :type="scope.row.userType === 'huiyuan' ? 'warning' : 'primary'" size="mini">
                                    {{ scope.row.userType === 'huiyuan' ? '会员' : '用户' }}
                                </el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="xingming" label="入住人" width="80"></el-table-column>
                        <el-table-column prop="shouji" label="手机号" width="120"></el-table-column>
                        <el-table-column label="预离时间" width="100">
                            <template slot-scope="scope">
                                {{ formatTime(scope.row.yulifangshijian) }}
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="80">
                            <template slot-scope="scope">
                                <el-button type="text" size="mini" @click="handleQuickCheckOut(scope.row)">
                                    退房
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <div class="empty-text" v-if="todayCheckOutList.length === 0">
                        今日暂无预计退房
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script>
export default {
    name: 'QiantaiDashboard',
    data() {
        return {
            overview: {},
            roomStatus: {},
            pendingItems: [],
            todayCheckInList: [],
            todayCheckOutList: []
        };
    },
    mounted() {
        this.loadOverview();
        this.loadRoomStatus();
        this.loadPendingItems();
        this.loadTodayCheckIn();
        this.loadTodayCheckOut();
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
                }
            } catch (error) {
                console.error('加载房态统计失败', error);
            }
        },
        async loadPendingItems() {
            this.pendingItems = [];
            
            // 加载待审核的用户预约
            try {
                const res = await this.$http({
                    url: `${this.$http.adornUrl('/yonghuyuyue/page')}`,
                    method: 'get',
                    params: this.$http.adornParams({
                        page: 1,
                        limit: 10,
                        yuyuezhuangtai: 'pending'
                    })
                });
                if (res.data.code === 0 && res.data.data.list) {
                    res.data.data.list.forEach(item => {
                        this.pendingItems.push({
                            id: item.id,
                            type: '待审核',
                            tagType: 'warning',
                            content: `用户${item.xingming}预约${item.kefanghao}号房`,
                            action: 'audit',
                            data: item
                        });
                    });
                }
            } catch (error) {
                console.error('加载待审核订单失败', error);
            }
            
            // 加载待审核的会员预约
            try {
                const res = await this.$http({
                    url: `${this.$http.adornUrl('/huiyuanyuyue/page')}`,
                    method: 'get',
                    params: this.$http.adornParams({
                        page: 1,
                        limit: 10,
                        yuyuezhuangtai: 'pending'
                    })
                });
                if (res.data.code === 0 && res.data.data.list) {
                    res.data.data.list.forEach(item => {
                        this.pendingItems.push({
                            id: 'h_' + item.id,
                            type: '会员待审核',
                            tagType: 'warning',
                            content: `会员${item.xingming}预约${item.kefanghao}号房`,
                            action: 'audit_huiyuan',
                            data: item
                        });
                    });
                }
            } catch (error) {
                console.error('加载会员待审核订单失败', error);
            }
        },
        async loadTodayCheckIn() {
            const today = this.formatDate(new Date());
            try {
                // 加载用户预约
                const res1 = await this.$http({
                    url: `${this.$http.adornUrl('/yonghuyuyue/page')}`,
                    method: 'get',
                    params: this.$http.adornParams({
                        page: 1,
                        limit: 20,
                        yuyuezhuangtai: 'paid'
                    })
                });
                
                // 加载会员预约
                const res2 = await this.$http({
                    url: `${this.$http.adornUrl('/huiyuanyuyue/page')}`,
                    method: 'get',
                    params: this.$http.adornParams({
                        page: 1,
                        limit: 20,
                        yuyuezhuangtai: 'paid'
                    })
                });
                
                let allBookings = [];
                
                if (res1.data.code === 0 && res1.data.data.list) {
                    const yonghuList = res1.data.data.list
                        .filter(item => this.formatDate(new Date(item.ruzhushijian)) === today)
                        .map(item => ({ ...item, userType: 'yonghu' }));
                    allBookings = allBookings.concat(yonghuList);
                }
                
                if (res2.data.code === 0 && res2.data.data.list) {
                    const huiyuanList = res2.data.data.list
                        .filter(item => this.formatDate(new Date(item.ruzhushijian)) === today)
                        .map(item => ({ ...item, userType: 'huiyuan' }));
                    allBookings = allBookings.concat(huiyuanList);
                }
                
                this.todayCheckInList = allBookings;
            } catch (error) {
                console.error('加载今日入住列表失败', error);
            }
        },
        async loadTodayCheckOut() {
            const today = this.formatDate(new Date());
            try {
                // 加载用户入住
                const res1 = await this.$http({
                    url: `${this.$http.adornUrl('/yonghuruzhu/page')}`,
                    method: 'get',
                    params: this.$http.adornParams({
                        page: 1,
                        limit: 20,
                        kefangzhuangtai: '已入住'
                    })
                });
                
                // 加载会员入住
                const res2 = await this.$http({
                    url: `${this.$http.adornUrl('/huiyuanruzhu/page')}`,
                    method: 'get',
                    params: this.$http.adornParams({
                        page: 1,
                        limit: 20,
                        kefangzhuangtai: '已入住'
                    })
                });
                
                let allCheckouts = [];
                
                if (res1.data.code === 0 && res1.data.data.list) {
                    const yonghuList = res1.data.data.list
                        .filter(item => {
                            if (!item.yulifangshijian) return false;
                            const checkOutDate = this.formatDate(new Date(item.yulifangshijian));
                            return checkOutDate <= today;
                        })
                        .map(item => ({ ...item, userType: 'yonghu' }));
                    allCheckouts = allCheckouts.concat(yonghuList);
                }
                
                if (res2.data.code === 0 && res2.data.data.list) {
                    const huiyuanList = res2.data.data.list
                        .filter(item => {
                            if (!item.yulifangshijian) return false;
                            const checkOutDate = this.formatDate(new Date(item.yulifangshijian));
                            return checkOutDate <= today;
                        })
                        .map(item => ({ ...item, userType: 'huiyuan' }));
                    allCheckouts = allCheckouts.concat(huiyuanList);
                }
                
                this.todayCheckOutList = allCheckouts;
            } catch (error) {
                console.error('加载今日退房列表失败', error);
            }
        },
        handlePending(item) {
            if (item.action === 'audit') {
                this.$router.push(`/modules/yonghuyuyue/list`);
            } else if (item.action === 'audit_huiyuan') {
                this.$router.push(`/modules/huiyuanyuyue/list`);
            }
        },
        async handleQuickCheckIn(row) {
            try {
                const userTypeText = row.userType === 'huiyuan' ? '（会员）' : '';
                await this.$confirm(`确认为该预约${userTypeText}办理入住？`, '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                });
                
                const apiUrl = row.userType === 'huiyuan'
                    ? '/huiyuanruzhu/checkInByYuyue/' + row.id
                    : '/yonghuruzhu/checkInByYuyue/' + row.id;
                    
                const res = await this.$http({
                    url: `${this.$http.adornUrl(apiUrl)}`,
                    method: 'post'
                });
                if (res.data.code === 0) {
                    this.$message.success(res.data.msg || '入住办理成功');
                    this.loadOverview();
                    this.loadRoomStatus();
                    this.loadTodayCheckIn();
                } else {
                    this.$message.error(res.data.msg || '入住办理失败');
                }
            } catch (error) {
                if (error !== 'cancel') {
                    this.$message.error('入住办理失败');
                }
            }
        },
        handleQuickCheckOut(row) {
            this.$router.push({
                path: '/qiantai/checkout',
                query: { ruzhuId: row.id }
            });
        },
        goTo(path) {
            this.$router.push(path);
        },
        format(percentage) {
            return percentage + '%';
        },
        formatDate(date) {
            if (!date) return '';
            const d = new Date(date);
            const year = d.getFullYear();
            const month = String(d.getMonth() + 1).padStart(2, '0');
            const day = String(d.getDate()).padStart(2, '0');
            return `${year}-${month}-${day}`;
        },
        formatTime(date) {
            if (!date) return '';
            const d = new Date(date);
            const month = String(d.getMonth() + 1).padStart(2, '0');
            const day = String(d.getDate()).padStart(2, '0');
            const hour = String(d.getHours()).padStart(2, '0');
            const minute = String(d.getMinutes()).padStart(2, '0');
            return `${month}-${day} ${hour}:${minute}`;
        },
        getYuyueStatusType(status) {
            const map = {
                'pending': 'info',
                'approved': 'warning',
                'rejected': 'danger',
                'paid': 'success',
                'checkedin': 'primary',
                'cancelled': 'info'
            };
            return map[status] || 'info';
        },
        getYuyueStatusText(status) {
            const map = {
                'pending': '待审核',
                'approved': '待支付',
                'rejected': '已拒绝',
                'paid': '待入住',
                'checkedin': '已入住',
                'cancelled': '已取消'
            };
            return map[status] || status;
        }
    }
};
</script>

<style scoped>
.dashboard-container {
    padding: 20px;
}

.stat-row {
    margin-bottom: 20px;
}

.stat-card {
    display: flex;
    align-items: center;
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16px;
}

.stat-icon i {
    font-size: 28px;
    color: #fff;
}

.stat-value {
    font-size: 28px;
    font-weight: 600;
    color: #303133;
}

.stat-label {
    font-size: 14px;
    color: #909399;
    margin-top: 4px;
}

.box-card {
    margin-bottom: 20px;
}

.card-header {
    display: flex;
    align-items: center;
}

.badge-item {
    margin-left: 10px;
}

.room-status-summary {
    display: flex;
    justify-content: space-around;
    margin-bottom: 20px;
}

.status-item {
    text-align: center;
}

.status-count {
    font-size: 32px;
    font-weight: 600;
}

.status-count.free {
    color: #67C23A;
}

.status-count.booked {
    color: #E6A23C;
}

.status-count.occupied {
    color: #409EFF;
}

.status-count.cleaning {
    color: #F56C6C;
}

.status-name {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
}

.occupancy-rate {
    display: flex;
    align-items: center;
}

.occupancy-rate span {
    margin-right: 10px;
    white-space: nowrap;
}

.occupancy-rate .el-progress {
    flex: 1;
}

.pending-list {
    max-height: 200px;
    overflow-y: auto;
}

.pending-item {
    display: flex;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #EBEEF5;
}

.pending-item:last-child {
    border-bottom: none;
}

.pending-content {
    flex: 1;
    margin: 0 12px;
    color: #606266;
    font-size: 14px;
}

.empty-text {
    text-align: center;
    color: #909399;
    padding: 40px 0;
}

.empty-text i {
    font-size: 36px;
    display: block;
    margin-bottom: 10px;
}

.quick-actions {
    margin-top: 20px;
}

.action-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 30px;
    background: #f5f7fa;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;
}

.action-btn:hover {
    background: #409EFF;
    color: #fff;
}

.action-btn i {
    font-size: 36px;
    margin-bottom: 12px;
}

.action-btn span {
    font-size: 14px;
}
</style>
