<template>
  <div class="main-content">
    <div v-if="showFlag">
      <el-form :inline="true" :model="searchForm" class="form-content">
        <el-row :gutter="20" class="slt">
          <el-form-item label="会员账号">
            <el-input prefix-icon="el-icon-search" v-model="searchForm.zhanghao" placeholder="会员账号" clearable></el-input>
          </el-form-item>
          <el-form-item label="积分类型">
            <el-select v-model="searchForm.jifenleixing" placeholder="请选择类型" clearable>
              <el-option v-for="item in typeOptions" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-search" type="success" @click="search()">查询</el-button>
          </el-form-item>
        </el-row>
        <el-row class="ad">
          <el-form-item>
            <el-button v-if="isAuth('jifen','删除')" :disabled="dataListSelections.length <= 0" type="danger" icon="el-icon-delete" @click="deleteHandler()">批量删除</el-button>
          </el-form-item>
        </el-row>
      </el-form>
      <div class="table-content">
        <el-table class="tables" size="medium" :border="true" :stripe="true" v-if="isAuth('jifen','查看')" :data="dataList" v-loading="dataListLoading" @selection-change="selectionChangeHandler">
          <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
          <el-table-column label="索引" type="index" width="50" />
          <el-table-column prop="zhanghao" header-align="center" align="center" label="会员账号" width="120"></el-table-column>
          <el-table-column prop="jifenleixing" header-align="center" align="center" label="积分类型" width="100">
            <template slot-scope="scope">
              <el-tag :type="scope.row.jifenshu>0?'success':'danger'">{{scope.row.jifenleixing}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="jifenshu" header-align="center" align="center" label="积分数" width="100">
            <template slot-scope="scope">
              <span :style="{color: scope.row.jifenshu>0?'#67c23a':'#f56c6c', fontWeight:'bold'}">
                {{scope.row.jifenshu>0?'+':''}}{{scope.row.jifenshu}}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="yue" header-align="center" align="center" label="积分余额" width="100">
            <template slot-scope="scope">
              <span style="font-weight:bold;color:#409eff">{{scope.row.yue}}</span>
            </template>
          </el-table-column>
          <el-table-column prop="shuoming" header-align="center" align="left" label="说明" min-width="200" show-overflow-tooltip></el-table-column>
          <el-table-column prop="guanliandingdan" header-align="center" align="center" label="关联订单" width="180"></el-table-column>
          <el-table-column prop="caozuoren" header-align="center" align="center" label="操作人" width="100"></el-table-column>
          <el-table-column prop="addtime" header-align="center" align="center" label="操作时间" width="160"></el-table-column>
          <el-table-column width="150" align="center" header-align="center" label="操作">
            <template slot-scope="scope">
              <el-button v-if="isAuth('jifen','查看')" type="success" icon="el-icon-tickets" size="mini" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
              <el-button v-if="isAuth('jifen','删除')" type="danger" icon="el-icon-delete" size="mini" @click="deleteHandler(scope.row.id)">删除</el-button>
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
      typeOptions: ['预订积分', '消费积分', '积分抵扣', '签到积分', '活动赠送', '系统调整', '积分过期']
    };
  },
  created() { this.getDataList(); },
  components: { AddOrUpdate },
  methods: {
    contentStyleChange() {},
    search() { this.pageIndex = 1; this.getDataList(); },
    getDataList() {
      this.dataListLoading = true;
      let params = { page: this.pageIndex, limit: this.pageSize, sort: 'addtime', order: 'desc' }
      if(this.searchForm.zhanghao) params['zhanghao'] = '%' + this.searchForm.zhanghao + '%'
      if(this.searchForm.jifenleixing) params['jifenleixing'] = this.searchForm.jifenleixing
      this.$http({ url: "jifen/page", method: "get", params: params }).then(({ data }) => {
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
      this.$nextTick(() => { this.$refs.addOrUpdate.init(id, 'info'); });
    },
    deleteHandler(id) {
      var ids = id ? [Number(id)] : this.dataListSelections.map(item => Number(item.id));
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", { confirmButtonText: "确定", cancelButtonText: "取消", type: "warning" }).then(() => {
        this.$http({ url: "jifen/delete", method: "post", data: ids }).then(({ data }) => {
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
