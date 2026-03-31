<template>
    <div class="checkout-container">
        <el-card class="box-card">
            <div slot="header" class="card-header">
                <span>退房办理</span>
            </div>
            
            <!-- 查询表单 -->
            <el-form :inline="true" class="search-form">
                <el-form-item label="房间号">
                    <el-input v-model="searchForm.kefanghao" placeholder="请输入房间号" 
                              clearable @keyup.enter.native="searchGuests"></el-input>
                </el-form-item>
                <el-form-item label="入住人">
                    <el-input v-model="searchForm.xingming" placeholder="请输入入住人姓名" 
                              clearable @keyup.enter.native="searchGuests"></el-input>
                </el-form-item>
                <el-form-item label="手机号">
                    <el-input v-model="searchForm.shouji" placeholder="请输入手机号" 
                              clearable @keyup.enter.native="searchGuests"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="el-icon-search" @click="searchGuests">查询</el-button>
                    <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
                </el-form-item>
            </el-form>
            
            <!-- 在住客人列表 -->
            <el-table :data="guestList" stripe border v-loading="loading">
                <el-table-column prop="kefanghao" label="房间号" width="100"></el-table-column>
                <el-table-column label="类型" width="80">
                    <template slot-scope="scope">
                        <el-tag :type="scope.row.userType === 'huiyuan' ? 'warning' : 'primary'" size="mini">
                            {{ scope.row.userType === 'huiyuan' ? '会员' : '用户' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="kefangleixing" label="房型" width="100"></el-table-column>
                <el-table-column prop="xingming" label="入住人" width="100"></el-table-column>
                <el-table-column prop="shouji" label="手机号" width="120"></el-table-column>
                <el-table-column label="入住时间" width="160">
                    <template slot-scope="scope">
                        {{ formatDateTime(scope.row.ruzhushijian) }}
                    </template>
                </el-table-column>
                <el-table-column label="预离时间" width="160">
                    <template slot-scope="scope">
                        <span :class="{'overdue': isOverdue(scope.row.yulifangshijian)}">
                            {{ formatDateTime(scope.row.yulifangshijian) }}
                        </span>
                        <el-tag v-if="isOverdue(scope.row.yulifangshijian)" type="danger" size="mini" style="margin-left: 5px;">
                            超时
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="入住费用" width="100">
                    <template slot-scope="scope">
                        <span style="color: #409EFF; font-weight: bold;">
                            ¥{{ scope.row.jiage || 0 }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="ispay" label="支付状态" width="100">
                    <template slot-scope="scope">
                        <el-tag :type="scope.row.ispay === '已支付' ? 'success' : 'warning'" size="small">
                            {{ scope.row.ispay || '未支付' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="150" fixed="right">
                    <template slot-scope="scope">
                        <el-button type="primary" size="mini" @click="handleCheckOut(scope.row)"
                                   v-if="scope.row.kefangzhuangtai === '已入住'">
                            办理退房
                        </el-button>
                        <el-tag v-else type="info" size="small">已退房</el-tag>
                    </template>
                </el-table-column>
            </el-table>
            
            <el-pagination
                @current-change="handlePageChange"
                :current-page="page"
                :page-size="limit"
                layout="total, prev, pager, next"
                :total="total"
                style="margin-top: 20px;">
            </el-pagination>
        </el-card>
        
        <!-- 退房结算弹窗 -->
        <el-dialog title="退房结算" :visible.sync="checkoutDialogVisible" width="600px">
            <div class="checkout-info" v-if="currentGuest">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="房间号">{{ currentGuest.kefanghao }}</el-descriptions-item>
                    <el-descriptions-item label="房型">{{ currentGuest.kefangleixing }}</el-descriptions-item>
                    <el-descriptions-item label="入住人">{{ currentGuest.xingming }}</el-descriptions-item>
                    <el-descriptions-item label="手机号">{{ currentGuest.shouji }}</el-descriptions-item>
                    <el-descriptions-item label="入住时间">{{ formatDateTime(currentGuest.ruzhushijian) }}</el-descriptions-item>
                    <el-descriptions-item label="预离时间">{{ formatDateTime(currentGuest.yulifangshijian) }}</el-descriptions-item>
                </el-descriptions>
                
                <el-divider content-position="left">费用明细</el-divider>
                
                <div class="fee-detail">
                    <div class="fee-item">
                        <span class="fee-label">房费（{{ calculateDays() }}天）</span>
                        <span class="fee-value">¥{{ currentGuest.jiage || 0 }}</span>
                    </div>
                    <div class="fee-item" v-if="overtimeFee > 0">
                        <span class="fee-label">超时费用</span>
                        <span class="fee-value warning">¥{{ overtimeFee }}</span>
                    </div>
                    <div class="fee-item">
                        <span class="fee-label">其他费用</span>
                        <el-input-number v-model="otherFee" :min="0" :precision="2" size="small"></el-input-number>
                    </div>
                    <el-divider></el-divider>
                    <div class="fee-item total">
                        <span class="fee-label">应付总计</span>
                        <span class="fee-value total">¥{{ totalFee }}</span>
                    </div>
                    <div class="fee-item" v-if="currentGuest.ispay === '已支付'">
                        <span class="fee-label">已支付</span>
                        <span class="fee-value success">¥{{ currentGuest.jiage || 0 }}</span>
                    </div>
                    <div class="fee-item total" v-if="needPay > 0">
                        <span class="fee-label">还需支付</span>
                        <span class="fee-value warning">¥{{ needPay }}</span>
                    </div>
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="checkoutDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="confirmCheckOut" :loading="submitting">确认退房</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: 'CheckOut',
    data() {
        return {
            loading: false,
            submitting: false,
            searchForm: {
                kefanghao: '',
                xingming: '',
                shouji: ''
            },
            guestList: [],
            page: 1,
            limit: 10,
            total: 0,
            checkoutDialogVisible: false,
            currentGuest: null,
            otherFee: 0,
            overtimeFee: 0
        };
    },
    computed: {
        totalFee() {
            if (!this.currentGuest) return 0;
            return (this.currentGuest.jiage || 0) + this.overtimeFee + this.otherFee;
        },
        needPay() {
            if (!this.currentGuest) return 0;
            const paid = this.currentGuest.ispay === '已支付' ? (this.currentGuest.jiage || 0) : 0;
            return Math.max(0, this.totalFee - paid);
        }
    },
    mounted() {
        this.loadGuests();
        
        if (this.$route.query.ruzhuId) {
            this.loadGuestById(this.$route.query.ruzhuId);
        }
    },
    methods: {
        async loadGuests() {
            this.loading = true;
            try {
                const params = {
                    page: this.page,
                    limit: this.limit,
                    ...this.searchForm
                };
                Object.keys(params).forEach(key => {
                    if (params[key] === '') delete params[key];
                });
                
                // 加载用户入住
                const res1 = await this.$http({
                    url: `${this.$http.adornUrl('/yonghuruzhu/page')}`,
                    method: 'get',
                    params: this.$http.adornParams(params)
                });
                
                // 加载会员入住
                const res2 = await this.$http({
                    url: `${this.$http.adornUrl('/huiyuanruzhu/page')}`,
                    method: 'get',
                    params: this.$http.adornParams(params)
                });
                
                let allGuests = [];
                
                if (res1.data.code === 0 && res1.data.data.list) {
                    const yonghuList = res1.data.data.list
                        .filter(item => item.kefangzhuangtai === '已入住' || !item.kefangzhuangtai)
                        .map(item => ({ ...item, userType: 'yonghu' }));
                    allGuests = allGuests.concat(yonghuList);
                }
                
                if (res2.data.code === 0 && res2.data.data.list) {
                    const huiyuanList = res2.data.data.list
                        .filter(item => item.kefangzhuangtai === '已入住' || !item.kefangzhuangtai)
                        .map(item => ({ ...item, userType: 'huiyuan' }));
                    allGuests = allGuests.concat(huiyuanList);
                }
                
                this.guestList = allGuests;
                this.total = allGuests.length;
            } catch (error) {
                this.$message.error('加载入住列表失败');
            } finally {
                this.loading = false;
            }
        },
        async loadGuestById(id) {
            try {
                // 先尝试查用户入住
                let res = await this.$http({
                    url: `${this.$http.adornUrl('/yonghuruzhu/info/' + id)}`,
                    method: 'get'
                });
                if (res.data.code === 0 && res.data.data) {
                    this.handleCheckOut({ ...res.data.data, userType: 'yonghu' });
                    return;
                }
                
                // 再查会员入住
                res = await this.$http({
                    url: `${this.$http.adornUrl('/huiyuanruzhu/info/' + id)}`,
                    method: 'get'
                });
                if (res.data.code === 0 && res.data.data) {
                    this.handleCheckOut({ ...res.data.data, userType: 'huiyuan' });
                }
            } catch (error) {
                console.error('加载入住信息失败', error);
            }
        },
        searchGuests() {
            this.page = 1;
            this.loadGuests();
        },
        resetSearch() {
            this.searchForm = { kefanghao: '', xingming: '', shouji: '' };
            this.searchGuests();
        },
        handlePageChange(page) {
            this.page = page;
            this.loadGuests();
        },
        handleCheckOut(row) {
            this.currentGuest = { ...row };
            this.otherFee = 0;
            this.calculateOvertimeFee();
            this.checkoutDialogVisible = true;
        },
        calculateOvertimeFee() {
            if (!this.currentGuest || !this.currentGuest.yulifangshijian) {
                this.overtimeFee = 0;
                return;
            }
            
            const now = new Date();
            const yulifang = new Date(this.currentGuest.yulifangshijian);
            
            if (now > yulifang) {
                const hours = Math.ceil((now - yulifang) / (1000 * 60 * 60));
                const roomPrice = this.currentGuest.jiage || 0;
                const days = this.calculateDays();
                const pricePerDay = days > 0 ? roomPrice / days : roomPrice;
                
                if (hours <= 2) {
                    this.overtimeFee = 0;
                } else if (hours <= 6) {
                    this.overtimeFee = Math.round(pricePerDay * 0.5);
                } else {
                    this.overtimeFee = Math.round(pricePerDay);
                }
            } else {
                this.overtimeFee = 0;
            }
        },
        calculateDays() {
            if (!this.currentGuest || !this.currentGuest.ruzhushijian) return 1;
            
            const ruzhu = new Date(this.currentGuest.ruzhushijian);
            const now = new Date();
            const days = Math.ceil((now - ruzhu) / (1000 * 60 * 60 * 24));
            return Math.max(1, days);
        },
        async confirmCheckOut() {
            if (!this.currentGuest) return;
            
            try {
                const userTypeText = this.currentGuest.userType === 'huiyuan' ? '（会员）' : '';
                await this.$confirm(`确认办理退房${userTypeText}？`, '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                });
                
                this.submitting = true;
                
                // 根据用户类型调用不同的退房接口
                const apiUrl = this.currentGuest.userType === 'huiyuan'
                    ? '/huiyuantuifang/checkOut/' + this.currentGuest.id
                    : '/yonghutuifang/checkOut/' + this.currentGuest.id;
                
                const res = await this.$http({
                    url: `${this.$http.adornUrl(apiUrl)}`,
                    method: 'post',
                    data: this.$http.adornData({
                        jiage: this.totalFee
                    })
                });
                
                if (res.data.code === 0) {
                    this.$message.success('退房办理成功');
                    this.checkoutDialogVisible = false;
                    this.loadGuests();
                } else {
                    this.$message.error(res.data.msg || '退房办理失败');
                }
            } catch (error) {
                if (error !== 'cancel') {
                    this.$message.error('退房办理失败');
                }
            } finally {
                this.submitting = false;
            }
        },
        isOverdue(yulifangshijian) {
            if (!yulifangshijian) return false;
            return new Date() > new Date(yulifangshijian);
        },
        formatDateTime(date) {
            if (!date) return '';
            const d = new Date(date);
            const year = d.getFullYear();
            const month = String(d.getMonth() + 1).padStart(2, '0');
            const day = String(d.getDate()).padStart(2, '0');
            const hour = String(d.getHours()).padStart(2, '0');
            const minute = String(d.getMinutes()).padStart(2, '0');
            return `${year}-${month}-${day} ${hour}:${minute}`;
        }
    }
};
</script>

<style scoped>
.checkout-container {
    padding: 20px;
}

.card-header {
    font-size: 16px;
    font-weight: 600;
}

.search-form {
    margin-bottom: 20px;
    padding: 15px;
    background: #f5f7fa;
    border-radius: 4px;
}

.overdue {
    color: #F56C6C;
}

.checkout-info {
    padding: 10px;
}

.fee-detail {
    margin-top: 20px;
}

.fee-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 0;
    border-bottom: 1px dashed #EBEEF5;
}

.fee-item:last-child {
    border-bottom: none;
}

.fee-label {
    color: #606266;
}

.fee-value {
    font-weight: 600;
    color: #303133;
}

.fee-value.warning {
    color: #E6A23C;
}

.fee-value.success {
    color: #67C23A;
}

.fee-item.total .fee-label {
    font-size: 16px;
    font-weight: 600;
}

.fee-item.total .fee-value {
    font-size: 24px;
    color: #F56C6C;
}
</style>
