<template>
  <div class="main-content">
    <div v-if="showFlag">
      <el-form :inline="true" :model="searchForm" class="form-content">
        <el-row :gutter="20" class="slt">
          <el-form-item label="通知标题">
            <el-input prefix-icon="el-icon-search" v-model="searchForm.title" placeholder="通知标题" clearable></el-input>
          </el-form-item>
          <el-form-item label="通知类型">
            <el-select v-model="searchForm.type" placeholder="请选择类型" clearable>
              <el-option v-for="item in typeOptions" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-search" type="success" @click="search()">查询</el-button>
          </el-form-item>
        </el-row>
        <el-row class="ad">
          <el-form-item>
            <el-button v-if="isAuth('tongzhi','新增')" type="success" icon="el-icon-plus" @click="addOrUpdateHandler()">发送通知</el-button>
            <el-button v-if="isAuth('tongzhi','删除')" :disabled="dataListSelections.length <= 0" type="danger" icon="el-icon-delete" @click="deleteHandler()">批量删除</el-button>
          </el-form-item>
        </el-row>
      </el-form>
      <div class="table-content">
        <el-table class="tables" size="medium" :border="true" :stripe="true" v-if="isAuth('tongzhi','查看')" :data="dataList" v-loading="dataListLoading" @selection-change="selectionChangeHandler">
          <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
          <el-table-column label="索引" type="index" width="50" />
          <el-table-column prop="title" header-align="center" align="center" label="通知标题" min-width="150"></el-table-column>
          <el-table-column prop="type" header-align="center" align="center" label="通知类型" width="120">
            <template slot-scope="scope">
              <el-tag :type="getTypeTagType(scope.row.type)">{{scope.row.type}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="tablename" header-align="center" align="center" label="用户类型" width="100">
            <template slot-scope="scope">{{getUserTypeName(scope.row.tablename)}}</template>
          </el-table-column>
          <el-table-column prop="isread" header-align="center" align="center" label="阅读状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="scope.row.isread==1?'success':'info'">{{scope.row.isread==1?'已读':'未读'}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="addtime" header-align="center" align="center" label="发送时间" width="160"></el-table-column>
          <el-table-column width="200" align="center" header-align="center" label="操作">
            <template slot-scope="scope">
              <el-button v-if="isAuth('tongzhi','查看')" type="success" icon="el-icon-tickets" size="mini" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
              <el-button v-if="isAuth('tongzhi','删除')" type="danger" icon="el-icon-delete" size="mini" @click="deleteHandler(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination layout="total, sizes, prev, pager, next, jumper" @size-change="sizeChangeHandle" @current-change="currentChangeHandle" :current-page="pageIndex" :page-sizes="[10, 20, 50, 100]" :page-size="pageSize" :total="totalPage" class="pagination-content"></el-pagination>
      </div>
    </div>
    <add-or-update v-if="addOrUpdateFlag" :parent="this" ref="addOrUpdate"></add-or-update>
  </div>
</template>
<script>
import AddOrUpdate from "./add-or-update";
export default {
  data() {
    return {
      searchForm: { key: "" },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showFlag: true,
      addOrUpdateFlag: false,
      typeOptions: ['预约审核', '入住通知', '退房通知', '清扫通知', '系统通知', '积分变动']
    };
  },
  created() {
    this.getDataList();
  },
  components: { AddOrUpdate },
  methods: {
    contentStyleChange() {},
    getTypeTagType(type) {
      const typeMap = { '预约审核': 'warning', '入住通知': 'success', '退房通知': 'info', '清扫通知': 'primary', '系统通知': '', '积分变动': 'danger' };
      return typeMap[type] || '';
    },
    getUserTypeName(tablename) {
      const map = { 'yonghu': '普通用户', 'huiyuan': '会员', 'qiantairenyuan': '前台人员', 'qingjierenyuan': '清洁人员', 'users': '管理员' };
      return map[tablename] || tablename;
    },
    search() {
      this.pageIndex = 1;
      this.getDataList();
    },
    getDataList() {
      this.dataListLoading = true;
      let params = { page: this.pageIndex, limit: this.pageSize, sort: 'addtime', order: 'desc' }
      if(this.searchForm.title) params['title'] = '%' + this.searchForm.title + '%'
      if(this.searchForm.type) params['type'] = this.searchForm.type
      this.$http({ url: "tongzhi/page", method: "get", params: params }).then(({ data }) => {
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
    sizeChangeHandle(val) { this.pageSize = val; this.pageIndex = 1; this.getDataList(); },
    currentChangeHandle(val) { this.pageIndex = val; this.getDataList(); },
    selectionChangeHandler(val) { this.dataListSelections = val; },
    addOrUpdateHandler(id, type) {
      this.showFlag = false;
      this.addOrUpdateFlag = true;
      if(type!='info') type = 'else';
      this.$nextTick(() => { this.$refs.addOrUpdate.init(id, type); });
    },
    deleteHandler(id) {
      var ids = id ? [Number(id)] : this.dataListSelections.map(item => Number(item.id));
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", { confirmButtonText: "确定", cancelButtonText: "取消", type: "warning" }).then(() => {
        this.$http({ url: "tongzhi/delete", method: "post", data: ids }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({ message: "操作成功", type: "success", duration: 1500, onClose: () => { this.search(); } });
          } else { this.$message.error(data.msg); }
        });
      });
    },
  }
};
</script>
<style lang="scss" scoped>
.slt, .ad { margin: 0 !important; display: flex; }
.el-button+.el-button { margin: 0; }
.tables { & ::v-deep .el-button { margin: 4px; } }
</style>
