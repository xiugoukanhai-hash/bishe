<template>
    <div class="roomstatus-container">
        <!-- 房态概览 -->
        <el-card class="overview-card">
            <div slot="header" class="card-header">
                <span>房态概览</span>
                <el-button type="text" icon="el-icon-refresh" @click="loadData">刷新</el-button>
            </div>
            <div class="status-summary">
                <div class="status-item" v-for="item in statusSummary" :key="item.status">
                    <div class="status-count" :style="{color: item.color}">{{ item.count }}</div>
                    <div class="status-label">
                        <span class="status-dot" :style="{background: item.color}"></span>
                        {{ item.status }}
                    </div>
                </div>
                <div class="status-item">
                    <div class="status-count" style="color: #303133;">{{ roomList.length }}</div>
                    <div class="status-label">
                        <span class="status-dot" style="background: #909399;"></span>
                        总计
                    </div>
                </div>
            </div>
        </el-card>
        
        <!-- 筛选 -->
        <el-card class="filter-card">
            <el-form :inline="true">
                <el-form-item label="房间状态">
                    <el-select v-model="filterStatus" placeholder="全部" clearable @change="filterRooms">
                        <el-option label="全部" value=""></el-option>
                        <el-option label="空闲" value="空闲"></el-option>
                        <el-option label="已预约" value="已预约"></el-option>
                        <el-option label="已入住" value="已入住"></el-option>
                        <el-option label="待清扫" value="待清扫"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="房型">
                    <el-select v-model="filterType" placeholder="全部" clearable @change="filterRooms">
                        <el-option label="全部" value=""></el-option>
                        <el-option v-for="type in roomTypes" :key="type" :label="type" :value="type"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="楼层">
                    <el-select v-model="filterFloor" placeholder="全部" clearable @change="filterRooms">
                        <el-option label="全部" value=""></el-option>
                        <el-option v-for="floor in floors" :key="floor" :label="floor + '楼'" :value="floor"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
        </el-card>
        
        <!-- 房间列表 -->
        <div class="room-grid">
            <div class="room-card" 
                 v-for="room in filteredRooms" 
                 :key="room.id" 
                 :class="getRoomClass(room)"
                 @click="showRoomDetail(room)">
                <div class="room-number">{{ room.kefanghao }}</div>
                <div class="room-type">{{ room.kefangleixing }}</div>
                <div class="room-status">
                    <el-tag :type="getStatusTagType(room.kefangzhuangtai)" size="mini">
                        {{ room.kefangzhuangtai }}
                    </el-tag>
                </div>
                <div class="room-price">¥{{ room.jiage }}/晚</div>
                <div class="room-clean" v-if="room.weishengqingkuang === '待清扫'">
                    <i class="el-icon-warning" style="color: #E6A23C;"></i> 待清扫
                </div>
            </div>
        </div>
        
        <!-- 房间详情弹窗 -->
        <el-dialog :title="'房间详情 - ' + (currentRoom ? currentRoom.kefanghao + '号' : '')" 
                   :visible.sync="detailDialogVisible" 
                   width="600px">
            <div v-if="currentRoom">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="房间号">{{ currentRoom.kefanghao }}</el-descriptions-item>
                    <el-descriptions-item label="房型">{{ currentRoom.kefangleixing }}</el-descriptions-item>
                    <el-descriptions-item label="所属酒店">{{ currentRoom.suoshujiudian }}</el-descriptions-item>
                    <el-descriptions-item label="价格">¥{{ currentRoom.jiage }}/晚</el-descriptions-item>
                    <el-descriptions-item label="房间状态">
                        <el-tag :type="getStatusTagType(currentRoom.kefangzhuangtai)">
                            {{ currentRoom.kefangzhuangtai }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="卫生情况">
                        <el-tag :type="currentRoom.weishengqingkuang === '已清扫' ? 'success' : 'warning'">
                            {{ currentRoom.weishengqingkuang }}
                        </el-tag>
                    </el-descriptions-item>
                </el-descriptions>
                
                <!-- 在住客人信息 -->
                <div v-if="currentGuest" style="margin-top: 20px;">
                    <el-divider content-position="left">
                        在住客人
                        <el-tag :type="currentGuest.userType === 'huiyuan' ? 'warning' : 'primary'" size="mini" style="margin-left: 10px;">
                            {{ currentGuest.userType === 'huiyuan' ? '会员' : '用户' }}
                        </el-tag>
                    </el-divider>
                    <el-descriptions :column="2" border>
                        <el-descriptions-item label="入住人">{{ currentGuest.xingming }}</el-descriptions-item>
                        <el-descriptions-item label="手机号">{{ currentGuest.shouji }}</el-descriptions-item>
                        <el-descriptions-item label="入住时间">{{ formatDateTime(currentGuest.ruzhushijian) }}</el-descriptions-item>
                        <el-descriptions-item label="预离时间">{{ formatDateTime(currentGuest.yulifangshijian) }}</el-descriptions-item>
                    </el-descriptions>
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="detailDialogVisible = false">关 闭</el-button>
                <el-button type="primary" @click="handleAction" v-if="currentRoom">
                    {{ getActionText() }}
                </el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: 'RoomStatus',
    data() {
        return {
            roomList: [],
            filteredRooms: [],
            filterStatus: '',
            filterType: '',
            filterFloor: '',
            roomTypes: [],
            floors: [],
            detailDialogVisible: false,
            currentRoom: null,
            currentGuest: null,
            statusSummary: [
                { status: '空闲', count: 0, color: '#67C23A' },
                { status: '已预约', count: 0, color: '#E6A23C' },
                { status: '已入住', count: 0, color: '#409EFF' },
                { status: '待清扫', count: 0, color: '#F56C6C' }
            ]
        };
    },
    mounted() {
        this.loadData();
    },
    methods: {
        async loadData() {
            try {
                const res = await this.$http({
                    url: `${this.$http.adornUrl('/kefangxinxi/page')}`,
                    method: 'get',
                    params: this.$http.adornParams({
                        page: 1,
                        limit: 1000
                    })
                });
                if (res.data.code === 0) {
                    this.roomList = res.data.data.list || [];
                    this.processRoomData();
                    this.filterRooms();
                }
            } catch (error) {
                this.$message.error('加载房间数据失败');
            }
        },
        processRoomData() {
            const types = new Set();
            const floors = new Set();
            
            this.statusSummary.forEach(item => item.count = 0);
            
            this.roomList.forEach(room => {
                if (room.kefangleixing) types.add(room.kefangleixing);
                
                const floor = this.getFloor(room.kefanghao);
                if (floor) floors.add(floor);
                
                const statusItem = this.statusSummary.find(s => s.status === room.kefangzhuangtai);
                if (statusItem) {
                    statusItem.count++;
                }
            });
            
            this.roomTypes = Array.from(types);
            this.floors = Array.from(floors).sort((a, b) => a - b);
        },
        getFloor(kefanghao) {
            if (!kefanghao) return null;
            const num = parseInt(kefanghao);
            if (isNaN(num)) return null;
            return Math.floor(num / 100);
        },
        filterRooms() {
            this.filteredRooms = this.roomList.filter(room => {
                if (this.filterStatus && room.kefangzhuangtai !== this.filterStatus) return false;
                if (this.filterType && room.kefangleixing !== this.filterType) return false;
                if (this.filterFloor) {
                    const floor = this.getFloor(room.kefanghao);
                    if (floor !== parseInt(this.filterFloor)) return false;
                }
                return true;
            });
        },
        getRoomClass(room) {
            const classes = ['room-' + this.getStatusClass(room.kefangzhuangtai)];
            if (room.weishengqingkuang === '待清扫') {
                classes.push('need-clean');
            }
            return classes;
        },
        getStatusClass(status) {
            const map = {
                '空闲': 'free',
                '已预约': 'booked',
                '已入住': 'occupied',
                '待清扫': 'cleaning'
            };
            return map[status] || 'free';
        },
        getStatusTagType(status) {
            const map = {
                '空闲': 'success',
                '已预约': 'warning',
                '已入住': 'primary',
                '待清扫': 'danger'
            };
            return map[status] || 'info';
        },
        async showRoomDetail(room) {
            this.currentRoom = room;
            this.currentGuest = null;
            this.detailDialogVisible = true;
            
            if (room.kefangzhuangtai === '已入住') {
                await this.loadGuestInfo(room.kefanghao);
            }
        },
        async loadGuestInfo(kefanghao) {
            try {
                // 先查用户入住
                let res = await this.$http({
                    url: `${this.$http.adornUrl('/yonghuruzhu/guestByRoom/' + kefanghao)}`,
                    method: 'get'
                });
                if (res.data.code === 0 && res.data.data) {
                    this.currentGuest = { ...res.data.data, userType: 'yonghu' };
                    return;
                }
                
                // 再查会员入住
                res = await this.$http({
                    url: `${this.$http.adornUrl('/huiyuanruzhu/guestByRoom/' + kefanghao)}`,
                    method: 'get'
                });
                if (res.data.code === 0 && res.data.data) {
                    this.currentGuest = { ...res.data.data, userType: 'huiyuan' };
                }
            } catch (error) {
                console.error('加载在住客人信息失败', error);
            }
        },
        getActionText() {
            if (!this.currentRoom) return '';
            const status = this.currentRoom.kefangzhuangtai;
            if (status === '空闲') return '办理入住';
            if (status === '已入住') return '办理退房';
            if (status === '待清扫') return '安排清扫';
            return '查看详情';
        },
        handleAction() {
            if (!this.currentRoom) return;
            const status = this.currentRoom.kefangzhuangtai;
            
            if (status === '空闲') {
                this.$router.push({
                    path: '/qiantai/checkin',
                    query: { kefanghao: this.currentRoom.kefanghao }
                });
            } else if (status === '已入住' && this.currentGuest) {
                this.$router.push({
                    path: '/qiantai/checkout',
                    query: { ruzhuId: this.currentGuest.id }
                });
            } else if (status === '待清扫') {
                this.$router.push('/modules/qingsaofangjian/list');
            }
            
            this.detailDialogVisible = false;
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
.roomstatus-container {
    padding: 20px;
}

.overview-card {
    margin-bottom: 20px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.status-summary {
    display: flex;
    justify-content: space-around;
}

.status-item {
    text-align: center;
    padding: 10px 30px;
}

.status-count {
    font-size: 36px;
    font-weight: 600;
}

.status-label {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 8px;
    color: #606266;
}

.status-dot {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    margin-right: 6px;
}

.filter-card {
    margin-bottom: 20px;
}

.room-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 15px;
}

.room-card {
    background: #fff;
    border-radius: 8px;
    padding: 15px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s;
    border: 2px solid transparent;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.room-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.room-free {
    border-color: #67C23A;
    background: linear-gradient(135deg, #f0f9eb 0%, #fff 100%);
}

.room-booked {
    border-color: #E6A23C;
    background: linear-gradient(135deg, #fdf6ec 0%, #fff 100%);
}

.room-occupied {
    border-color: #409EFF;
    background: linear-gradient(135deg, #ecf5ff 0%, #fff 100%);
}

.room-cleaning {
    border-color: #F56C6C;
    background: linear-gradient(135deg, #fef0f0 0%, #fff 100%);
}

.room-number {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
}

.room-type {
    font-size: 12px;
    color: #909399;
    margin: 5px 0;
}

.room-status {
    margin: 8px 0;
}

.room-price {
    font-size: 14px;
    color: #F56C6C;
    font-weight: 500;
}

.room-clean {
    font-size: 12px;
    color: #E6A23C;
    margin-top: 5px;
}

.need-clean {
    position: relative;
}

.need-clean::after {
    content: '';
    position: absolute;
    top: 5px;
    right: 5px;
    width: 8px;
    height: 8px;
    background: #E6A23C;
    border-radius: 50%;
    animation: pulse 1.5s infinite;
}

@keyframes pulse {
    0% {
        transform: scale(1);
        opacity: 1;
    }
    50% {
        transform: scale(1.2);
        opacity: 0.7;
    }
    100% {
        transform: scale(1);
        opacity: 1;
    }
}
</style>
