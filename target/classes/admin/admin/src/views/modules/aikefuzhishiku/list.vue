<template>
  <div class="main-content">
    <div v-if="showFlag">
      <el-form :inline="true" :model="searchForm" class="form-content">
        <el-row :gutter="20" class="slt">
          <el-form-item label="问题类型">
            <el-select v-model="searchForm.wentileixing" placeholder="请选择类型" clearable>
              <el-option v-for="item in typeOptions" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input prefix-icon="el-icon-search" v-model="searchForm.guanjianci" placeholder="关键词" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-search" type="success" @click="search()">查询</el-button>
          </el-form-item>
        </el-row>
        <el-row class="ad">
          <el-form-item>
            <el-button v-if="isAuth('aikefuzhishiku','新增')" type="success" icon="el-icon-plus" @click="addOrUpdateHandler()">新增</el-button>
            <el-button v-if="isAuth('aikefuzhishiku','删除')" :disabled="dataListSelections.length <= 0" type="danger" icon="el-icon-delete" @click="deleteHandler()">批量删除</el-button>
          </el-form-item>
        </el-row>
      </el-form>
      <div class="table-content">
        <el-table class="tables" size="medium" :border="true" :stripe="true" v-if="isAuth('aikefuzhishiku','查看')" :data="dataList" v-loading="dataListLoading" @selection-change="selectionChangeHandler">
          <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
          <el-table-column label="索引" type="index" width="50" />
          <el-table-column prop="wentileixing" header-align="center" align="center" label="问题类型" width="100">
            <template slot-scope="scope">
              <el-tag>{{scope.row.wentileixing}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="guanjianci" header-align="center" align="center" label="关键词" width="150"></el-table-column>
          <el-table-column prop="wenti" header-align="center" align="left" label="问题" min-width="200" show-overflow-tooltip></el-table-column>
          <el-table-column prop="daan" header-align="center" align="left" label="答案" min-width="200" show-overflow-tooltip></el-table-column>
          <el-table-column prop="paixu" header-align="center" align="center" label="排序" width="80"></el-table-column>
          <el-table-column prop="clickcount" header-align="center" align="center" label="点击量" width="80"></el-table-column>
          <el-table-column prop="zhuangtai" header-align="center" align="center" label="状态" width="80">
            <template slot-scope="scope">
              <el-tag :type="scope.row.zhuangtai=='启用'?'success':'danger'">{{scope.row.zhuangtai}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column width="250" align="center" header-align="center" label="操作">
            <template slot-scope="scope">
              <el-button v-if="isAuth('aikefuzhishiku','查看')" type="success" icon="el-icon-tickets" size="mini" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
              <el-button v-if="isAuth('aikefuzhishiku','修改')" type="primary" icon="el-icon-edit" size="mini" @click="addOrUpdateHandler(scope.row.id)">修改</el-button>
              <el-button v-if="isAuth('aikefuzhishiku','删除')" type="danger" icon="el-icon-delete" size="mini" @click="deleteHandler(scope.row.id)">删除</el-button>
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
      searchForm: {},
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showFlag: true,
      addOrUpdateFlag: false,
      typeOptions: ['客房信息', '价格咨询', '服务咨询', '预订流程', '退订政策', '会员权益', '其他']
    };
  },
  created() { this.getDataList(); },
  components: { AddOrUpdate },
  methods: {
    contentStyleChange() {},
    search() { this.pageIndex = 1; this.getDataList(); },
    getDataList() {
      this.dataListLoading = true;
      let params = { page: this.pageIndex, limit: this.pageSize, sort: 'paixu' }
      if(this.searchForm.wentileixing) params['wentileixing'] = this.searchForm.wentileixing
      if(this.searchForm.guanjianci) params['guanjianci'] = '%' + this.searchForm.guanjianci + '%'
      this.$http({ url: "aikefuzhishiku/page", method: "get", params: params }).then(({ data }) => {
        if (data && data.code === 0) { this.dataList = data.data.list; this.totalPage = data.data.total; }
        else { this.dataList = []; this.totalPage = 0; }
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
        this.$http({ url: "aikefuzhishiku/delete", method: "post", data: ids }).then(({ data }) => {
          if (data && data.code === 0) { this.$message({ message: "操作成功", type: "success", duration: 1500, onClose: () => { this.search(); } }); }
          else { this.$message.error(data.msg); }
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
