<template>
    <div class="config-container">
        <el-card class="config-card">
            <div slot="header">
                <span><i class="el-icon-office-building"></i> 酒店信息配置</span>
            </div>
            <el-form :model="hotelInfo" :rules="rules" ref="hotelForm" label-width="120px" 
                     v-loading="loading" class="config-form">
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="酒店名称" prop="hotelName">
                            <el-input v-model="hotelInfo.hotelName" placeholder="请输入酒店名称">
                            </el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="联系电话" prop="hotelPhone">
                            <el-input v-model="hotelInfo.hotelPhone" placeholder="请输入联系电话">
                                <template slot="prepend"><i class="el-icon-phone"></i></template>
                            </el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                
                <el-form-item label="酒店地址" prop="hotelAddress">
                    <el-input v-model="hotelInfo.hotelAddress" placeholder="请输入详细地址">
                        <template slot="prepend"><i class="el-icon-location"></i></template>
                    </el-input>
                </el-form-item>
                
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="电子邮箱" prop="hotelEmail">
                            <el-input v-model="hotelInfo.hotelEmail" placeholder="请输入电子邮箱">
                                <template slot="prepend"><i class="el-icon-message"></i></template>
                            </el-input>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-divider content-position="left">入住退房规则</el-divider>
                
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="入住时间" prop="checkInTime">
                            <el-time-select v-model="hotelInfo.checkInTime" 
                                           :picker-options="{ start: '00:00', step: '00:30', end: '23:30' }"
                                           placeholder="选择入住时间" style="width: 100%;">
                            </el-time-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="退房时间" prop="checkOutTime">
                            <el-time-select v-model="hotelInfo.checkOutTime"
                                           :picker-options="{ start: '00:00', step: '00:30', end: '23:30' }"
                                           placeholder="选择退房时间" style="width: 100%;">
                            </el-time-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                
                <el-form-item label="酒店简介" prop="hotelDescription">
                    <el-input v-model="hotelInfo.hotelDescription" type="textarea" :rows="4"
                              placeholder="请输入酒店简介，将展示在前台页面">
                    </el-input>
                </el-form-item>
                
                <el-form-item>
                    <el-button type="primary" @click="saveConfig" :loading="saving">
                        <i class="el-icon-check"></i> 保存配置
                    </el-button>
                    <el-button @click="loadConfig">
                        <i class="el-icon-refresh"></i> 重新加载
                    </el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <el-card class="config-card" style="margin-top: 20px;">
            <div slot="header">
                <span><i class="el-icon-setting"></i> 系统参数配置</span>
            </div>
            <el-form :model="systemParams" ref="systemForm" label-width="160px" 
                     v-loading="systemLoading" class="config-form">
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="预约过期时间(小时)">
                            <el-input-number v-model="systemParams.bookingExpireHours" :min="1" :max="72"
                                            style="width: 100%;"></el-input-number>
                            <div class="form-tip">预约未支付超过此时间将自动取消</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="超时费率(%)">
                            <el-input-number v-model="systemParams.overtimeRate" :min="0" :max="100" 
                                            :precision="0" style="width: 100%;"></el-input-number>
                            <div class="form-tip">超时退房按日房价的百分比收取费用</div>
                        </el-form-item>
                    </el-col>
                </el-row>
                
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="会员折扣(%)">
                            <el-input-number v-model="systemParams.memberDiscount" :min="50" :max="100"
                                            :precision="0" style="width: 100%;"></el-input-number>
                            <div class="form-tip">会员预订享受的折扣比例</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="积分比例">
                            <el-input-number v-model="systemParams.pointsRate" :min="1" :max="100"
                                            :precision="0" style="width: 100%;"></el-input-number>
                            <div class="form-tip">每消费1元获得的积分数</div>
                        </el-form-item>
                    </el-col>
                </el-row>
                
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="取消预约截止(小时)">
                            <el-input-number v-model="systemParams.cancelDeadlineHours" :min="0" :max="48"
                                            style="width: 100%;"></el-input-number>
                            <div class="form-tip">入住前多少小时可免费取消预约</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="最大预订天数">
                            <el-input-number v-model="systemParams.maxBookingDays" :min="1" :max="365"
                                            style="width: 100%;"></el-input-number>
                            <div class="form-tip">单次预订的最大入住天数</div>
                        </el-form-item>
                    </el-col>
                </el-row>
                
                <el-form-item>
                    <el-button type="primary" @click="saveSystemParams" :loading="systemSaving">
                        <i class="el-icon-check"></i> 保存参数
                    </el-button>
                    <el-button @click="loadSystemParams">
                        <i class="el-icon-refresh"></i> 重新加载
                    </el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script>
export default {
    name: 'HotelConfig',
    data() {
        return {
            loading: false,
            saving: false,
            systemLoading: false,
            systemSaving: false,
            hotelInfo: {
                hotelName: '',
                hotelAddress: '',
                hotelPhone: '',
                hotelEmail: '',
                checkInTime: '14:00',
                checkOutTime: '12:00',
                hotelDescription: ''
            },
            systemParams: {
                bookingExpireHours: 24,
                overtimeRate: 50,
                memberDiscount: 90,
                pointsRate: 1,
                cancelDeadlineHours: 24,
                maxBookingDays: 30
            },
            rules: {
                hotelName: [
                    { required: true, message: '请输入酒店名称', trigger: 'blur' }
                ],
                hotelPhone: [
                    { required: true, message: '请输入联系电话', trigger: 'blur' }
                ]
            }
        };
    },
    mounted() {
        this.loadConfig();
        this.loadSystemParams();
    },
    methods: {
        async loadConfig() {
            this.loading = true;
            try {
                const res = await this.$http.get('/config/hotelInfo');
                if (res.data.code === 0 && res.data.data) {
                    const data = res.data.data;
                    this.hotelInfo = {
                        hotelName: data.hotelName || '',
                        hotelAddress: data.hotelAddress || '',
                        hotelPhone: data.hotelPhone || '',
                        hotelEmail: data.hotelEmail || '',
                        checkInTime: data.checkInTime || '14:00',
                        checkOutTime: data.checkOutTime || '12:00',
                        hotelDescription: data.hotelDescription || ''
                    };
                }
            } catch (error) {
                console.error('加载配置失败', error);
            } finally {
                this.loading = false;
            }
        },
        async saveConfig() {
            this.$refs.hotelForm.validate(async (valid) => {
                if (!valid) return;
                
                this.saving = true;
                try {
                    const res = await this.$http.post('/config/saveHotelInfo', this.hotelInfo);
                    if (res.data.code === 0) {
                        this.$message.success('酒店信息保存成功');
                    } else {
                        this.$message.error(res.data.msg || '保存失败');
                    }
                } catch (error) {
                    this.$message.error('保存失败');
                } finally {
                    this.saving = false;
                }
            });
        },
        async loadSystemParams() {
            this.systemLoading = true;
            try {
                const res = await this.$http.get('/config/systemParams');
                if (res.data.code === 0 && res.data.data) {
                    const data = res.data.data;
                    this.systemParams = {
                        bookingExpireHours: parseInt(data.bookingExpireHours) || 24,
                        overtimeRate: parseInt(data.overtimeRate) || 50,
                        memberDiscount: parseInt(data.memberDiscount) || 90,
                        pointsRate: parseInt(data.pointsRate) || 1,
                        cancelDeadlineHours: parseInt(data.cancelDeadlineHours) || 24,
                        maxBookingDays: parseInt(data.maxBookingDays) || 30
                    };
                }
            } catch (error) {
                console.error('加载系统参数失败', error);
            } finally {
                this.systemLoading = false;
            }
        },
        async saveSystemParams() {
            this.systemSaving = true;
            try {
                const params = {
                    bookingExpireHours: String(this.systemParams.bookingExpireHours),
                    overtimeRate: String(this.systemParams.overtimeRate),
                    memberDiscount: String(this.systemParams.memberDiscount),
                    pointsRate: String(this.systemParams.pointsRate),
                    cancelDeadlineHours: String(this.systemParams.cancelDeadlineHours),
                    maxBookingDays: String(this.systemParams.maxBookingDays)
                };
                const res = await this.$http.post('/config/saveSystemParams', params);
                if (res.data.code === 0) {
                    this.$message.success('系统参数保存成功');
                } else {
                    this.$message.error(res.data.msg || '保存失败');
                }
            } catch (error) {
                this.$message.error('保存失败');
            } finally {
                this.systemSaving = false;
            }
        }
    }
};
</script>

<style scoped>
.config-container {
    padding: 20px;
    max-width: 1000px;
}

.config-card {
    margin-bottom: 20px;
}

.config-card ::v-deep .el-card__header {
    background: #f5f7fa;
    padding: 15px 20px;
    font-weight: 500;
}

.config-form {
    padding: 20px;
}

.form-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
    line-height: 1.4;
}
</style>
