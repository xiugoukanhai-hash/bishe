<template>
  <div class="main-content">
    <!-- 列表页 -->
    <div v-if="showFlag">
      <el-form :inline="true" :model="searchForm" class="form-content">
        <el-row :gutter="20" class="slt">
          <el-form-item label="客房号">
            <el-input prefix-icon="el-icon-search" v-model="searchForm.kefanghao" placeholder="客房号" clearable></el-input>
          </el-form-item>
          <el-form-item label="客房类型">
            <el-input prefix-icon="el-icon-search" v-model="searchForm.kefangleixing" placeholder="客房类型" clearable></el-input>
          </el-form-item>
          <el-form-item label="清洁姓名">
            <el-input prefix-icon="el-icon-search" v-model="searchForm.qingjiexingming" placeholder="清洁姓名" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-search" type="success" @click="search()">查询</el-button>
          </el-form-item>
        </el-row>
        <el-row class="ad">
          <el-form-item>
            <el-button v-if="isAuth('qingsaofangjian','新增')" type="success" icon="el-icon-plus" @click="addOrUpdateHandler()">新增</el-button>
            <el-button v-if="isAuth('qingsaofangjian','删除')" :disabled="dataListSelections.length <= 0" type="danger" icon="el-icon-delete" @click="deleteHandler()">批量删除</el-button>
            <el-button v-if="isAuth('qingsaofangjian','统计')" type="info" icon="el-icon-s-data" @click="showStatistics()">清扫统计</el-button>
            <el-button v-if="isAuth('qingsaofangjian','排名')" type="warning" icon="el-icon-trophy" @click="showRanking()">清扫排名</el-button>
          </el-form-item>
        </el-row>
      </el-form>
      <div class="table-content">
        <el-table class="tables" size="medium" border stripe v-if="isAuth('qingsaofangjian','查看')" :data="dataList" v-loading="dataListLoading" @selection-change="selectionChangeHandler">
          <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
          <el-table-column label="索引" type="index" width="50" align="center"></el-table-column>
          <el-table-column prop="kefanghao" header-align="center" align="center" label="客房号"></el-table-column>
          <el-table-column prop="kefangleixing" header-align="center" align="center" label="客房类型"></el-table-column>
          <el-table-column prop="suoshujiudian" header-align="center" align="center" label="所属酒店"></el-table-column>
          <el-table-column prop="shifoudasao" header-align="center" align="center" label="是否打扫">
            <template slot-scope="scope">
              <el-tag :type="scope.row.shifoudasao=='是'?'success':'danger'">{{scope.row.shifoudasao}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="dasaoshijian" header-align="center" align="center" label="打扫时间" width="160"></el-table-column>
          <el-table-column prop="qingjiezhanghao" header-align="center" align="center" label="清洁账号"></el-table-column>
          <el-table-column prop="qingjiexingming" header-align="center" align="center" label="清洁姓名"></el-table-column>
          <el-table-column width="200" header-align="center" align="center" label="操作">
            <template slot-scope="scope">
              <el-button v-if="isAuth('qingsaofangjian','查看')" type="success" icon="el-icon-tickets" size="mini" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
              <el-button v-if="isAuth('qingsaofangjian','修改')" type="primary" icon="el-icon-edit" size="mini" @click="addOrUpdateHandler(scope.row.id)">修改</el-button>
              <el-button v-if="isAuth('qingsaofangjian','删除')" type="danger" icon="el-icon-delete" size="mini" @click="deleteHandler(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          :layout="'total, sizes, prev, pager, next, jumper'"
          @size-change="sizeChangeHandle"
          @current-change="currentChangeHandle"
          :current-page="pageIndex"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          :total="totalPage"
          class="pagination-content"
          background
          style="text-align:right;margin-top:15px;"
        ></el-pagination>
      </div>
    </div>
    <add-or-update v-if="addOrUpdateFlag" :parent="this" ref="addOrUpdate"></add-or-update>
    
    <!-- 清扫统计对话框 -->
    <el-dialog title="清扫统计" :visible.sync="statisticsDialogVisible" width="800px">
      <el-form :inline="true" :model="statisticsForm" style="margin-bottom: 20px;">
        <el-form-item label="清洁账号">
          <el-input v-model="statisticsForm.qingjiehao" placeholder="请输入清洁账号" clearable></el-input>
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="statisticsForm.startDate" type="date" placeholder="开始日期" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="statisticsForm.endDate" type="date" placeholder="结束日期" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getStatistics()">查询统计</el-button>
        </el-form-item>
      </el-form>
      <div v-if="statisticsData">
        <el-row :gutter="20" style="margin-bottom: 20px;">
          <el-col :span="8">
            <el-card shadow="hover">
              <div slot="header">总清扫任务数</div>
              <div class="stat-number">{{ statisticsData.totalTasks }}</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover">
              <div slot="header">清洁人员</div>
              <div class="stat-number">{{ statisticsData.cleanerName || '全部' }}</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover">
              <div slot="header">查询时间段</div>
              <div class="stat-text">{{ statisticsData.dateRange || '全部时间' }}</div>
            </el-card>
          </el-col>
        </el-row>
        <el-table :data="statisticsData.dailyStats" border style="width: 100%">
          <el-table-column prop="date" label="日期" align="center"></el-table-column>
          <el-table-column prop="count" label="清扫数量" align="center"></el-table-column>
        </el-table>
      </div>
    </el-dialog>
    
    <!-- 清扫排名对话框 -->
    <el-dialog title="清扫排名" :visible.sync="rankingDialogVisible" width="700px">
      <el-form :inline="true" :model="rankingForm" style="margin-bottom: 20px;">
        <el-form-item label="开始日期">
          <el-date-picker v-model="rankingForm.startDate" type="date" placeholder="开始日期" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="rankingForm.endDate" type="date" placeholder="结束日期" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getRanking()">查询排名</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="rankingData" border style="width: 100%">
        <el-table-column type="index" label="排名" width="80" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.$index === 0" type="warning" effect="dark">
              <i class="el-icon-trophy"></i> 1
            </el-tag>
            <el-tag v-else-if="scope.$index === 1" type="info" effect="dark">2</el-tag>
            <el-tag v-else-if="scope.$index === 2" type="danger" effect="plain">3</el-tag>
            <span v-else>{{ scope.$index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="qingjiehao" label="清洁账号" align="center"></el-table-column>
        <el-table-column prop="qingjiexingming" label="清洁姓名" align="center"></el-table-column>
        <el-table-column prop="taskCount" label="清扫任务数" align="center">
          <template slot-scope="scope">
            <el-tag type="success">{{ scope.row.taskCount }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>
<script>
import AddOrUpdate from "./add-or-update";
export default {
  data() {
    return {
      searchForm: {
        kefanghao: "",
        kefangleixing: "",
        qingjiexingming: ""
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showFlag: true,
      addOrUpdateFlag: false,
      statisticsDialogVisible: false,
      statisticsForm: {
        qingjiehao: '',
        startDate: '',
        endDate: ''
      },
      statisticsData: null,
      rankingDialogVisible: false,
      rankingForm: {
        startDate: '',
        endDate: ''
      },
      rankingData: []
    };
  },
  created() {
    this.getDataList();
  },
  components: {
    AddOrUpdate
  },
  methods: {
    search() {
      this.pageIndex = 1;
      this.getDataList();
    },
    getDataList() {
      this.dataListLoading = true;
      let params = {
        page: this.pageIndex,
        limit: this.pageSize,
        sort: 'id'
      };
      if(this.searchForm.kefanghao) {
        params['kefanghao'] = '%' + this.searchForm.kefanghao + '%';
      }
      if(this.searchForm.kefangleixing) {
        params['kefangleixing'] = '%' + this.searchForm.kefangleixing + '%';
      }
      if(this.searchForm.qingjiexingming) {
        params['qingjiexingming'] = '%' + this.searchForm.qingjiexingming + '%';
      }
      this.$http({
        url: "qingsaofangjian/page",
        method: "get",
        params: params
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.dataList = data.data.list;
          this.totalPage = data.data.total;
        } else {
          this.dataList = [];
          this.totalPage = 0;
        }
        this.dataListLoading = false;
      });
    },
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.pageIndex = 1;
      this.getDataList();
    },
    currentChangeHandle(val) {
      this.pageIndex = val;
      this.getDataList();
    },
    selectionChangeHandler(val) {
      this.dataListSelections = val;
    },
    addOrUpdateHandler(id, type) {
      this.showFlag = false;
      this.addOrUpdateFlag = true;
      if(type != 'info') {
        type = 'else';
      }
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id, type);
      });
    },
    deleteHandler(id) {
      var ids = id ? [Number(id)] : this.dataListSelections.map(item => Number(item.id));
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$http({
          url: "qingsaofangjian/delete",
          method: "post",
          data: ids
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.search();
              }
            });
          } else {
            this.$message.error(data.msg);
          }
        });
      });
    },
    showStatistics() {
      this.statisticsDialogVisible = true;
      this.statisticsData = null;
    },
    getStatistics() {
      let params = {};
      if (this.statisticsForm.qingjiehao) {
        params.qingjiehao = this.statisticsForm.qingjiehao;
      }
      if (this.statisticsForm.startDate) {
        params.startDate = this.statisticsForm.startDate;
      }
      if (this.statisticsForm.endDate) {
        params.endDate = this.statisticsForm.endDate;
      }
      this.$http({
        url: "qingsaofangjian/statistics",
        method: "get",
        params: params
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.statisticsData = data.data;
        } else {
          this.$message.error(data.msg || '获取统计数据失败');
        }
      });
    },
    showRanking() {
      this.rankingDialogVisible = true;
      this.getRanking();
    },
    getRanking() {
      let params = {};
      if (this.rankingForm.startDate) {
        params.startDate = this.rankingForm.startDate;
      }
      if (this.rankingForm.endDate) {
        params.endDate = this.rankingForm.endDate;
      }
      this.$http({
        url: "qingsaofangjian/ranking",
        method: "get",
        params: params
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.rankingData = data.data || [];
        } else {
          this.$message.error(data.msg || '获取排名数据失败');
        }
      });
    }
  }
};
</script>
<style lang="scss" scoped>
.slt {
  margin: 0 !important;
  display: flex;
}
.ad {
  margin: 0 !important;
  display: flex;
}
.tables {
  & ::v-deep .el-button {
    margin: 4px;
  }
}
.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  text-align: center;
}
.stat-text {
  font-size: 14px;
  color: #606266;
  text-align: center;
}
</style>
