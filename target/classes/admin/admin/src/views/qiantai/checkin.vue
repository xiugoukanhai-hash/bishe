<template>
    <div class="checkin-container">
        <el-card class="box-card">
            <div slot="header" class="card-header">
                <span>入住办理</span>
            </div>
            
            <!-- 预约查询 -->
            <el-form :inline="true" class="search-form">
                <el-form-item label="预约编号">
                    <el-input v-model="searchForm.yuyuebianhao" placeholder="请输入预约编号" 
                              clearable @keyup.enter.native="searchBooking"></el-input>
                </el-form-item>
                <el-form-item label="手机号">
                    <el-input v-model="searchForm.shouji" placeholder="请输入手机号" 
                              clearable @keyup.enter.native="searchBooking"></el-input>
                </el-form-item>
                <el-form-item label="入住人姓名">
                    <el-input v-model="searchForm.xingming" placeholder="请输入姓名" 
                              clearable @keyup.enter.native="searchBooking"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="el-icon-search" @click="searchBooking">查询</el-button>
                    <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
                </el-form-item>
            </el-form>
            
            <!-- 预约列表 -->
            <el-table :data="bookingList" stripe border v-loading="loading" style="margin-bottom: 20px;">
                <el-table-column prop="yuyuebianhao" label="预约编号" width="180"></el-table-column>
                <el-table-column label="类型" width="80">
                    <template slot-scope="scope">
                        <el-tag :type="scope.row.userType === 'huiyuan' ? 'warning' : 'primary'" size="mini">
                            {{ scope.row.userType === 'huiyuan' ? '会员' : '用户' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="kefanghao" label="房间号" width="100"></el-table-column>
                <el-table-column prop="kefangleixing" label="房型" width="100"></el-table-column>
                <el-table-column prop="xingming" label="入住人" width="100"></el-table-column>
                <el-table-column prop="shouji" label="手机号" width="120"></el-table-column>
                <el-table-column label="预约入住日期" width="120">
                    <template slot-scope="scope">
                        {{ formatDate(scope.row.ruzhushijian) }}
                    </template>
                </el-table-column>
                <el-table-column prop="tianshu" label="天数" width="60"></el-table-column>
                <el-table-column label="金额" width="100">
                    <template slot-scope="scope">
                        <span style="color: #F56C6C; font-weight: bold;">
                            ¥{{ scope.row.zongjia || scope.row.jiage }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column label="预约状态" width="100">
                    <template slot-scope="scope">
                        <el-tag :type="getStatusType(scope.row.yuyuezhuangtai)" size="small">
                            {{ getStatusText(scope.row.yuyuezhuangtai) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="150" fixed="right">
                    <template slot-scope="scope">
                        <el-button type="primary" size="mini" 
                                   @click="handleCheckIn(scope.row)"
                                   :disabled="scope.row.yuyuezhuangtai !== 'paid'">
                            办理入住
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            
            <el-pagination
                @current-change="handlePageChange"
                :current-page="page"
                :page-size="limit"
                layout="total, prev, pager, next"
                :total="total">
            </el-pagination>
        </el-card>
        
        <!-- 现场入住 -->
        <el-card class="walkin-card">
            <div slot="header" class="card-header">
                <span>现场入住（散客）</span>
            </div>
            <el-form :model="walkInForm" :rules="walkInRules" ref="walkInForm" 
                     label-width="100px">
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="选择房间" prop="kefanghao">
                            <el-select v-model="walkInForm.kefanghao" placeholder="请选择房间" 
                                       filterable @change="handleRoomChange" style="width: 100%;">
                                <el-option v-for="room in freeRooms" :key="room.kefanghao"
                                           :label="`${room.kefanghao}号 - ${room.kefangleixing} - ¥${room.jiage}/晚`"
                                           :value="room.kefanghao">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="入住天数" prop="tianshu">
                            <el-input-number v-model="walkInForm.tianshu" :min="1" :max="30"
                                             @change="calculatePrice" style="width: 100%;"></el-input-number>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="入住人姓名" prop="xingming">
                            <el-input v-model="walkInForm.xingming" placeholder="请输入姓名"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="手机号" prop="shouji">
                            <el-input v-model="walkInForm.shouji" placeholder="请输入手机号" maxlength="11"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="身份证号" prop="shenfenzheng">
                            <el-input v-model="walkInForm.shenfenzheng" 
                                      placeholder="请输入身份证号" maxlength="18"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="应付金额">
                            <div class="price-display">¥{{ totalPrice }}</div>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20" v-if="selectedRoom">
                    <el-col :span="24">
                        <el-form-item label="房间信息">
                            <div class="room-info">
                                <span><i class="el-icon-house"></i> {{ selectedRoom.kefanghao }}号房</span>
                                <span><i class="el-icon-tickets"></i> {{ selectedRoom.kefangleixing }}</span>
                                <span><i class="el-icon-location"></i> {{ selectedRoom.suoshujiudian }}</span>
                                <span><i class="el-icon-money"></i> ¥{{ selectedRoom.jiage }}/晚</span>
                            </div>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item>
                    <el-button type="primary" @click="submitWalkIn" :loading="submitting">
                        <i class="el-icon-check"></i> 确认入住
                    </el-button>
                    <el-button @click="resetWalkInForm">
                        <i class="el-icon-refresh"></i> 重置
                    </el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script>
export default {
    name: 'CheckIn',
    data() {
        const validatePhone = (rule, value, callback) => {
            if (!value) {
                callback(new Error('请输入手机号'));
            } else if (!/^1[3-9]\d{9}$/.test(value)) {
                callback(new Error('请输入正确的手机号'));
            } else {
                callback();
            }
        };
        const validateIdCard = (rule, value, callback) => {
            if (!value) {
                callback(new Error('请输入身份证号'));
            } else if (!/^\d{17}[\dXx]$/.test(value)) {
                callback(new Error('请输入正确的身份证号'));
            } else {
                callback();
            }
        };
        return {
            loading: false,
            submitting: false,
            searchForm: {
                yuyuebianhao: '',
                shouji: '',
                xingming: ''
            },
            bookingList: [],
            page: 1,
            limit: 10,
            total: 0,
            freeRooms: [],
            walkInForm: {
                kefanghao: '',
                tianshu: 1,
                xingming: '',
                shouji: '',
                shenfenzheng: ''
            },
            walkInRules: {
                kefanghao: [{ required: true, message: '请选择房间', trigger: 'change' }],
                xingming: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
                shouji: [{ required: true, validator: validatePhone, trigger: 'blur' }],
                shenfenzheng: [{ required: true, validator: validateIdCard, trigger: 'blur' }]
            },
            selectedRoom: null,
            totalPrice: 0
        };
    },
    mounted() {
        this.loadBookings();
        this.loadFreeRooms();
    },
    methods: {
        async loadBookings() {
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
                
                // 加载用户预约
                const res1 = await this.$http({
                    url: `${this.$http.adornUrl('/yonghuyuyue/page')}`,
                    method: 'get',
                    params: this.$http.adornParams(params)
                });
                
                // 加载会员预约
                const res2 = await this.$http({
                    url: `${this.$http.adornUrl('/huiyuanyuyue/page')}`,
                    method: 'get',
                    params: this.$http.adornParams(params)
                });
                
                let allBookings = [];
                
                if (res1.data.code === 0 && res1.data.data.list) {
                    const yonghuList = res1.data.data.list
                        .filter(item => 
                            item.yuyuezhuangtai === 'paid' || 
                            item.yuyuezhuangtai === 'approved' ||
                            (item.sfsh === '是' && item.ispay === '已支付')
                        )
                        .map(item => ({ ...item, userType: 'yonghu' }));
                    allBookings = allBookings.concat(yonghuList);
                }
                
                if (res2.data.code === 0 && res2.data.data.list) {
                    const huiyuanList = res2.data.data.list
                        .filter(item => 
                            item.yuyuezhuangtai === 'paid' || 
                            item.yuyuezhuangtai === 'approved' ||
                            (item.sfsh === '是' && item.ispay === '已支付')
                        )
                        .map(item => ({ ...item, userType: 'huiyuan' }));
                    allBookings = allBookings.concat(huiyuanList);
                }
                
                this.bookingList = allBookings;
                this.total = this.bookingList.length;
            } catch (error) {
                this.$message.error('加载预约列表失败');
            } finally {
                this.loading = false;
            }
        },
        async loadFreeRooms() {
            try {
                const res = await this.$http({
                    url: `${this.$http.adornUrl('/yonghuruzhu/freeRooms')}`,
                    method: 'get'
                });
                if (res.data.code === 0) {
                    this.freeRooms = res.data.data || [];
                }
            } catch (error) {
                console.error('加载空闲房间失败', error);
            }
        },
        searchBooking() {
            this.page = 1;
            this.loadBookings();
        },
        resetSearch() {
            this.searchForm = { yuyuebianhao: '', shouji: '', xingming: '' };
            this.searchBooking();
        },
        handlePageChange(page) {
            this.page = page;
            this.loadBookings();
        },
        async handleCheckIn(row) {
            try {
                const userTypeText = row.userType === 'huiyuan' ? '（会员）' : '';
                await this.$confirm(`确认为${row.xingming}${userTypeText}办理入住？房间：${row.kefanghao}号`, '确认入住', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                });
                
                // 根据用户类型调用不同接口
                const apiUrl = row.userType === 'huiyuan' 
                    ? '/huiyuanruzhu/checkInByYuyue/' + row.id
                    : '/yonghuruzhu/checkInByYuyue/' + row.id;
                    
                const res = await this.$http({
                    url: `${this.$http.adornUrl(apiUrl)}`,
                    method: 'post'
                });
                if (res.data.code === 0) {
                    this.$message.success(res.data.msg || '入住办理成功');
                    this.loadBookings();
                    this.loadFreeRooms();
                } else {
                    this.$message.error(res.data.msg || '入住办理失败');
                }
            } catch (error) {
                if (error !== 'cancel') {
                    this.$message.error('入住办理失败');
                }
            }
        },
        handleRoomChange(kefanghao) {
            this.selectedRoom = this.freeRooms.find(r => r.kefanghao === kefanghao);
            this.calculatePrice();
        },
        calculatePrice() {
            if (this.selectedRoom) {
                this.totalPrice = (this.selectedRoom.jiage || 0) * this.walkInForm.tianshu;
            } else {
                this.totalPrice = 0;
            }
        },
        async submitWalkIn() {
            try {
                await this.$refs.walkInForm.validate();
                
                await this.$confirm(`确认办理入住？\n房间：${this.walkInForm.kefanghao}号\n入住人：${this.walkInForm.xingming}\n应付金额：¥${this.totalPrice}`, '确认入住', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                });
                
                this.submitting = true;
                const res = await this.$http({
                    url: `${this.$http.adornUrl('/yonghuruzhu/walkIn')}`,
                    method: 'post',
                    data: this.$http.adornData(this.walkInForm)
                });
                if (res.data.code === 0) {
                    this.$message.success(res.data.msg || '入住办理成功');
                    this.resetWalkInForm();
                    this.loadFreeRooms();
                } else {
                    this.$message.error(res.data.msg || '入住办理失败');
                }
            } catch (error) {
                if (error !== 'cancel' && error !== false) {
                    console.error('入住办理失败', error);
                }
            } finally {
                this.submitting = false;
            }
        },
        resetWalkInForm() {
            this.$refs.walkInForm.resetFields();
            this.selectedRoom = null;
            this.totalPrice = 0;
        },
        formatDate(date) {
            if (!date) return '';
            const d = new Date(date);
            const year = d.getFullYear();
            const month = String(d.getMonth() + 1).padStart(2, '0');
            const day = String(d.getDate()).padStart(2, '0');
            return `${year}-${month}-${day}`;
        },
        getStatusType(status) {
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
        getStatusText(status) {
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
.checkin-container {
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

.walkin-card {
    margin-top: 20px;
}

.price-display {
    font-size: 28px;
    font-weight: 600;
    color: #F56C6C;
    line-height: 40px;
}

.room-info {
    display: flex;
    gap: 20px;
    padding: 10px 15px;
    background: #f5f7fa;
    border-radius: 4px;
}

.room-info span {
    color: #606266;
}

.room-info i {
    margin-right: 5px;
    color: #409EFF;
}
</style>
