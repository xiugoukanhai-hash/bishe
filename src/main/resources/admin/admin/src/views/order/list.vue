<template>
    <div class="order-list-container">
        <!-- 统计卡片 -->
        <el-row :gutter="20" class="overview-cards">
            <el-col :span="6">
                <div class="overview-card" style="border-left-color: #409EFF;">
                    <div class="card-value">{{ overview.todayOrders || 0 }}</div>
                    <div class="card-label">今日订单</div>
                </div>
            </el-col>
            <el-col :span="6">
                <div class="overview-card" style="border-left-color: #E6A23C;">
                    <div class="card-value">{{ overview.pendingOrders || 0 }}</div>
                    <div class="card-label">待审核</div>
                </div>
            </el-col>
            <el-col :span="6">
                <div class="overview-card" style="border-left-color: #F56C6C;">
                    <div class="card-value">{{ overview.unpaidOrders || 0 }}</div>
                    <div class="card-label">待支付</div>
                </div>
            </el-col>
            <el-col :span="6">
                <div class="overview-card" style="border-left-color: #67C23A;">
                    <div class="card-value">{{ overview.totalOrders || 0 }}</div>
                    <div class="card-label">总订单数</div>
                </div>
            </el-col>
        </el-row>

        <el-card>
            <!-- 搜索筛选区域 -->
            <el-form :inline="true" class="search-form">
                <el-form-item label="订单类型">
                    <el-select v-model="searchParams.type" placeholder="全部类型" clearable>
                        <el-option label="全部" value="all"></el-option>
                        <el-option label="用户订单" value="yonghu"></el-option>
                        <el-option label="会员订单" value="huiyuan"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="订单状态">
                    <el-select v-model="searchParams.status" placeholder="全部状态" clearable>
                        <el-option label="全部" value=""></el-option>
                        <el-option label="待审核" value="pending"></el-option>
                        <el-option label="已审核" value="approved"></el-option>
                        <el-option label="已支付" value="paid"></el-option>
                        <el-option label="待支付" value="unpaid"></el-option>
                        <el-option label="已取消" value="cancelled"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="关键词">
                    <el-input v-model="searchParams.keyword" placeholder="订单号/房间号/姓名/手机" 
                              clearable style="width: 200px;"></el-input>
                </el-form-item>
                <el-form-item label="日期范围">
                    <el-date-picker v-model="dateRange" type="daterange" range-separator="至"
                                    start-placeholder="开始日期" end-placeholder="结束日期"
                                    value-format="yyyy-MM-dd" @change="handleDateChange">
                    </el-date-picker>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="el-icon-search" @click="search">搜索</el-button>
                    <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
                </el-form-item>
            </el-form>

            <!-- 批量操作工具栏 -->
            <div class="toolbar" v-if="selectedOrders.length > 0">
                <span class="selected-info">已选择 {{ selectedOrders.length }} 条订单</span>
                <el-button type="success" size="small" @click="handleBatchAudit('是')" 
                           :disabled="!canBatchAudit">
                    <i class="el-icon-check"></i> 批量通过
                </el-button>
                <el-button type="danger" size="small" @click="handleBatchAudit('否')"
                           :disabled="!canBatchAudit">
                    <i class="el-icon-close"></i> 批量拒绝
                </el-button>
                <el-button type="info" size="small" @click="handleExport">
                    <i class="el-icon-download"></i> 导出选中
                </el-button>
            </div>

            <!-- 订单列表表格 -->
            <el-table :data="orderList" v-loading="loading" @selection-change="handleSelectionChange"
                      :header-cell-style="{background:'#f5f7fa'}" border>
                <el-table-column type="selection" width="50" align="center"></el-table-column>
                <el-table-column prop="yuyuebianhao" label="订单编号" width="170" show-overflow-tooltip>
                    <template slot-scope="scope">
                        <el-link type="primary" @click="showOrderDetail(scope.row)">
                            {{ scope.row.yuyuebianhao }}
                        </el-link>
                    </template>
                </el-table-column>
                <el-table-column label="类型" width="80" align="center">
                    <template slot-scope="scope">
                        <el-tag size="mini" :type="scope.row.userTypeCode === 'huiyuan' ? 'warning' : 'primary'">
                            {{ scope.row.userType }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="kefanghao" label="房间号" width="80" align="center"></el-table-column>
                <el-table-column prop="kefangleixing" label="房型" width="100"></el-table-column>
                <el-table-column prop="xingming" label="客户姓名" width="90"></el-table-column>
                <el-table-column prop="shouji" label="手机号" width="120"></el-table-column>
                <el-table-column prop="ruzhushijianStr" label="入住时间" width="160"></el-table-column>
                <el-table-column prop="tianshu" label="天数" width="60" align="center">
                    <template slot-scope="scope">{{ scope.row.tianshu || 1 }}天</template>
                </el-table-column>
                <el-table-column prop="jiage" label="金额" width="90" align="right">
                    <template slot-scope="scope">
                        <span style="color: #E6A23C; font-weight: 500;">¥{{ scope.row.jiage || 0 }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="状态" width="90" align="center">
                    <template slot-scope="scope">
                        <el-tag size="small" :type="getStatusType(scope.row.statusText)">
                            {{ scope.row.statusText }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="addtimeStr" label="创建时间" width="160"></el-table-column>
                <el-table-column label="操作" width="180" fixed="right" align="center">
                    <template slot-scope="scope">
                        <el-button type="text" size="small" @click="showOrderDetail(scope.row)">
                            详情
                        </el-button>
                        <el-button type="text" size="small" @click="showStatusHistory(scope.row)">
                            流程
                        </el-button>
                        <el-button type="text" size="small" @click="handleAudit(scope.row)" 
                                   v-if="scope.row.sfsh === '否'">
                            审核
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                           :current-page="pagination.page" :page-sizes="[10, 20, 50, 100]"
                           :page-size="pagination.limit" :total="pagination.total"
                           layout="total, sizes, prev, pager, next, jumper"
                           style="margin-top: 20px; text-align: right;">
            </el-pagination>
        </el-card>

        <!-- 订单详情对话框 -->
        <el-dialog title="订单详情" :visible.sync="detailDialogVisible" width="700px">
            <div v-if="currentOrder">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="订单编号">{{ currentOrder.yuyuebianhao }}</el-descriptions-item>
                    <el-descriptions-item label="订单类型">
                        <el-tag size="small" :type="currentOrder.userTypeCode === 'huiyuan' ? 'warning' : 'primary'">
                            {{ currentOrder.userType }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="房间号">{{ currentOrder.kefanghao }}</el-descriptions-item>
                    <el-descriptions-item label="房型">{{ currentOrder.kefangleixing }}</el-descriptions-item>
                    <el-descriptions-item label="客户姓名">{{ currentOrder.xingming }}</el-descriptions-item>
                    <el-descriptions-item label="联系电话">{{ currentOrder.shouji }}</el-descriptions-item>
                    <el-descriptions-item label="入住时间">{{ currentOrder.ruzhushijianStr }}</el-descriptions-item>
                    <el-descriptions-item label="入住天数">{{ currentOrder.tianshu || 1 }}天</el-descriptions-item>
                    <el-descriptions-item label="订单金额">
                        <span style="color: #E6A23C; font-size: 18px; font-weight: 600;">
                            ¥{{ currentOrder.jiage || 0 }}
                        </span>
                    </el-descriptions-item>
                    <el-descriptions-item label="订单状态">
                        <el-tag :type="getStatusType(currentOrder.statusText)">
                            {{ currentOrder.statusText }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="审核状态">{{ currentOrder.sfsh === '是' ? '已通过' : '待审核' }}</el-descriptions-item>
                    <el-descriptions-item label="支付状态">{{ currentOrder.ispay }}</el-descriptions-item>
                    <el-descriptions-item label="创建时间" :span="2">{{ currentOrder.addtimeStr }}</el-descriptions-item>
                    <el-descriptions-item label="审核回复" :span="2" v-if="currentOrder.shhf">
                        {{ currentOrder.shhf }}
                    </el-descriptions-item>
                </el-descriptions>
            </div>
            <span slot="footer">
                <el-button @click="detailDialogVisible = false">关闭</el-button>
                <el-button type="primary" @click="handleAudit(currentOrder)" 
                           v-if="currentOrder && currentOrder.sfsh === '否'">
                    审核订单
                </el-button>
            </span>
        </el-dialog>

        <!-- 状态流程对话框 -->
        <el-dialog title="订单状态流程" :visible.sync="historyDialogVisible" width="600px">
            <el-timeline>
                <el-timeline-item v-for="(item, index) in statusHistory" :key="index"
                                  :color="item.color" :icon="item.icon">
                    <div class="timeline-content">
                        <div class="timeline-title">{{ item.status }}</div>
                        <div class="timeline-time">{{ item.timeStr }}</div>
                        <div class="timeline-remark" v-if="item.remark">{{ item.remark }}</div>
                    </div>
                </el-timeline-item>
            </el-timeline>
        </el-dialog>

        <!-- 审核对话框 -->
        <el-dialog title="订单审核" :visible.sync="auditDialogVisible" width="500px">
            <el-form :model="auditForm" label-width="100px">
                <el-form-item label="审核结果">
                    <el-radio-group v-model="auditForm.sfsh">
                        <el-radio label="是">通过</el-radio>
                        <el-radio label="否">拒绝</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="审核回复">
                    <el-input type="textarea" v-model="auditForm.shhf" :rows="3"
                              placeholder="请输入审核意见"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer">
                <el-button @click="auditDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitAudit">确定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: 'OrderList',
    data() {
        return {
            loading: false,
            orderList: [],
            selectedOrders: [],
            searchParams: {
                type: 'all',
                status: '',
                keyword: '',
                startDate: '',
                endDate: ''
            },
            dateRange: [],
            pagination: {
                page: 1,
                limit: 10,
                total: 0
            },
            overview: {
                todayOrders: 0,
                pendingOrders: 0,
                unpaidOrders: 0,
                totalOrders: 0
            },
            detailDialogVisible: false,
            historyDialogVisible: false,
            auditDialogVisible: false,
            currentOrder: null,
            statusHistory: [],
            auditForm: {
                sfsh: '是',
                shhf: ''
            },
            auditTarget: null
        };
    },
    computed: {
        canBatchAudit() {
            return this.selectedOrders.every(order => order.sfsh === '否');
        }
    },
    mounted() {
        this.loadOverview();
        this.loadOrders();
    },
    methods: {
        async loadOverview() {
            try {
                const res = await this.$http.get('/order/overview');
                if (res.data.code === 0) {
                    this.overview = res.data.data;
                }
            } catch (error) {
                console.error('加载统计失败', error);
            }
        },
        async loadOrders() {
            this.loading = true;
            try {
                const params = {
                    ...this.searchParams,
                    page: this.pagination.page,
                    limit: this.pagination.limit
                };
                const res = await this.$http.get('/order/list', { params });
                if (res.data.code === 0 && res.data.data) {
                    this.orderList = res.data.data.list || [];
                    this.pagination.total = res.data.data.total || 0;
                }
            } catch (error) {
                this.$message.error('加载订单列表失败');
                console.error(error);
            } finally {
                this.loading = false;
            }
        },
        search() {
            this.pagination.page = 1;
            this.loadOrders();
        },
        resetSearch() {
            this.searchParams = {
                type: 'all',
                status: '',
                keyword: '',
                startDate: '',
                endDate: ''
            };
            this.dateRange = [];
            this.pagination.page = 1;
            this.loadOrders();
        },
        handleDateChange(val) {
            if (val && val.length === 2) {
                this.searchParams.startDate = val[0];
                this.searchParams.endDate = val[1];
            } else {
                this.searchParams.startDate = '';
                this.searchParams.endDate = '';
            }
        },
        handleSizeChange(val) {
            this.pagination.limit = val;
            this.loadOrders();
        },
        handleCurrentChange(val) {
            this.pagination.page = val;
            this.loadOrders();
        },
        handleSelectionChange(val) {
            this.selectedOrders = val;
        },
        getStatusType(status) {
            const typeMap = {
                '待审核': 'warning',
                '待支付': '',
                '已支付': 'success',
                '已取消': 'info'
            };
            return typeMap[status] || 'info';
        },
        showOrderDetail(row) {
            this.currentOrder = row;
            this.detailDialogVisible = true;
        },
        async showStatusHistory(row) {
            try {
                const res = await this.$http.get(`/order/statusHistory/${row.yuyuebianhao}`);
                if (res.data.code === 0) {
                    this.statusHistory = res.data.data || [];
                    this.historyDialogVisible = true;
                }
            } catch (error) {
                this.$message.error('加载状态历史失败');
            }
        },
        handleAudit(row) {
            this.auditTarget = row;
            this.auditForm = { sfsh: '是', shhf: '' };
            this.auditDialogVisible = true;
        },
        async submitAudit() {
            if (!this.auditTarget) return;
            
            try {
                const res = await this.$http.post('/order/batchAudit', {
                    orderIds: [this.auditTarget.id],
                    type: this.auditTarget.orderType,
                    sfsh: this.auditForm.sfsh,
                    shhf: this.auditForm.shhf
                });
                if (res.data.code === 0) {
                    this.$message.success('审核成功');
                    this.auditDialogVisible = false;
                    this.detailDialogVisible = false;
                    this.loadOrders();
                    this.loadOverview();
                } else {
                    this.$message.error(res.data.msg || '审核失败');
                }
            } catch (error) {
                this.$message.error('审核失败');
            }
        },
        async handleBatchAudit(sfsh) {
            if (this.selectedOrders.length === 0) {
                this.$message.warning('请选择要审核的订单');
                return;
            }

            const yonghuOrders = this.selectedOrders.filter(o => o.orderType === 'yonghu');
            const huiyuanOrders = this.selectedOrders.filter(o => o.orderType === 'huiyuan');

            try {
                let successCount = 0;
                let failCount = 0;

                if (yonghuOrders.length > 0) {
                    const res = await this.$http.post('/order/batchAudit', {
                        orderIds: yonghuOrders.map(o => o.id),
                        type: 'yonghu',
                        sfsh: sfsh,
                        shhf: sfsh === '是' ? '批量审核通过' : '批量审核拒绝'
                    });
                    if (res.data.code === 0) {
                        successCount += res.data.data.successCount || 0;
                        failCount += res.data.data.failCount || 0;
                    }
                }

                if (huiyuanOrders.length > 0) {
                    const res = await this.$http.post('/order/batchAudit', {
                        orderIds: huiyuanOrders.map(o => o.id),
                        type: 'huiyuan',
                        sfsh: sfsh,
                        shhf: sfsh === '是' ? '批量审核通过' : '批量审核拒绝'
                    });
                    if (res.data.code === 0) {
                        successCount += res.data.data.successCount || 0;
                        failCount += res.data.data.failCount || 0;
                    }
                }

                this.$message.success(`批量审核完成：成功${successCount}条，失败${failCount}条`);
                this.loadOrders();
                this.loadOverview();
            } catch (error) {
                this.$message.error('批量审核失败');
            }
        },
        async handleExport() {
            const exportOrders = this.selectedOrders.length > 0 ? this.selectedOrders : null;
            
            try {
                const params = exportOrders ? {} : { ...this.searchParams };
                const res = await this.$http.get('/order/export', { params });
                
                if (res.data.code === 0 && res.data.data) {
                    let data = res.data.data;
                    
                    // 如果有选中的订单，只导出选中的
                    if (exportOrders) {
                        const selectedIds = exportOrders.map(o => o.id);
                        data = data.filter(o => selectedIds.includes(o.id));
                    }
                    
                    if (data.length === 0) {
                        this.$message.warning('没有可导出的数据');
                        return;
                    }
                    
                    // 构建CSV内容
                    const headers = ['订单编号', '类型', '房间号', '客户姓名', '手机号', '入住时间', '天数', '金额', '状态', '创建时间'];
                    const rows = data.map(o => [
                        o.yuyuebianhao || '',
                        o.userType || '',
                        o.kefanghao || '',
                        o.xingming || '',
                        o.shouji || '',
                        o.ruzhushijianStr || '',
                        o.tianshu || 1,
                        o.jiage || 0,
                        o.statusText || '',
                        o.addtimeStr || ''
                    ]);
                    
                    let csvContent = '\uFEFF'; // BOM for UTF-8
                    csvContent += headers.join(',') + '\n';
                    rows.forEach(row => {
                        csvContent += row.map(cell => `"${cell}"`).join(',') + '\n';
                    });
                    
                    // 创建并下载文件
                    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
                    const link = document.createElement('a');
                    const url = URL.createObjectURL(blob);
                    link.setAttribute('href', url);
                    link.setAttribute('download', `订单数据_${new Date().toISOString().split('T')[0]}.csv`);
                    document.body.appendChild(link);
                    link.click();
                    document.body.removeChild(link);
                    URL.revokeObjectURL(url);
                    
                    this.$message.success(`成功导出 ${data.length} 条订单数据`);
                } else {
                    this.$message.error(res.data.msg || '导出失败');
                }
            } catch (error) {
                this.$message.error('导出失败');
                console.error(error);
            }
        }
    }
};
</script>

<style scoped>
.order-list-container {
    padding: 20px;
}

.overview-cards {
    margin-bottom: 20px;
}

.overview-card {
    background: #fff;
    padding: 20px;
    border-radius: 4px;
    border-left: 4px solid;
    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.card-value {
    font-size: 28px;
    font-weight: 600;
    color: #303133;
}

.card-label {
    font-size: 14px;
    color: #909399;
    margin-top: 4px;
}

.search-form {
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 1px solid #ebeef5;
}

.toolbar {
    margin-bottom: 15px;
    padding: 10px 15px;
    background: #f0f9eb;
    border-radius: 4px;
    display: flex;
    align-items: center;
    gap: 10px;
}

.selected-info {
    color: #67C23A;
    font-size: 14px;
    margin-right: 10px;
}

.timeline-content {
    padding-bottom: 10px;
}

.timeline-title {
    font-size: 16px;
    font-weight: 500;
    color: #303133;
}

.timeline-time {
    font-size: 13px;
    color: #909399;
    margin-top: 4px;
}

.timeline-remark {
    font-size: 14px;
    color: #606266;
    margin-top: 6px;
    padding: 8px 12px;
    background: #f5f7fa;
    border-radius: 4px;
}
</style>
