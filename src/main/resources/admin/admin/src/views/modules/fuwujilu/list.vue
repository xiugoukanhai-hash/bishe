<template>
  <div class="main-content">
    <div v-if="showFlag">
      <el-form :inline="true" :model="searchForm" class="form-content">
        <el-row :gutter="20" class="slt">
          <el-form-item label="客房号">
            <el-input prefix-icon="el-icon-search" v-model="searchForm.kefanghao" placeholder="客房号" clearable></el-input>
          </el-form-item>
          <el-form-item label="服务类型">
            <el-select v-model="searchForm.fuwuleixing" placeholder="请选择类型" clearable>
              <el-option v-for="item in typeOptions" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.zhuangtai" placeholder="请选择状态" clearable>
              <el-option v-for="item in statusOptions" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-search" type="success" @click="search()">查询</el-button>
          </el-form-item>
        </el-row>
        <el-row class="ad">
          <el-form-item>
            <el-button v-if="isAuth('fuwujilu','新增')" type="success" icon="el-icon-plus" @click="addOrUpdateHandler()">新增</el-button>
            <el-button v-if="isAuth('fuwujilu','删除')" :disabled="dataListSelections.length <= 0" type="danger" icon="el-icon-delete" @click="deleteHandler()">批量删除</el-button>
          </el-form-item>
        </el-row>
      </el-form>
      <div class="table-content">
        <el-table class="tables" size="medium" :border="true" :stripe="true" v-if="isAuth('fuwujilu','查看')" :data="dataList" v-loading="dataListLoading" @selection-change="selectionChangeHandler">
          <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
          <el-table-column label="索引" type="index" width="50" />
          <el-table-column prop="dingdanbianhao" header-align="center" align="center" label="订单编号" width="180"></el-table-column>
          <el-table-column prop="kefanghao" header-align="center" align="center" label="客房号" width="100"></el-table-column>
          <el-table-column prop="fuwuleixing" header-align="center" align="center" label="服务类型" width="100">
            <template slot-scope="scope"><el-tag>{{scope.row.fuwuleixing}}</el-tag></template>
          </el-table-column>
          <el-table-column prop="fuwuxiangqing" header-align="center" align="left" label="服务详情" min-width="150" show-overflow-tooltip></el-table-column>
          <el-table-column prop="fuwufeiyong" header-align="center" align="center" label="费用" width="80">
            <template slot-scope="scope">{{scope.row.fuwufeiyong ? '￥'+scope.row.fuwufeiyong : '免费'}}</template>
          </el-table-column>
          <el-table-column prop="dengjirenzhanghao" header-align="center" align="center" label="登记人" width="100"></el-table-column>
          <el-table-column prop="zhuangtai" header-align="center" align="center" label="状态" width="80">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.zhuangtai)">{{scope.row.zhuangtai}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="addtime" header-align="center" align="center" label="登记时间" width="160"></el-table-column>
          <el-table-column width="300" align="center" header-align="center" label="操作">
            <template slot-scope="scope">
              <el-button v-if="isAuth('fuwujilu','查看')" type="success" icon="el-icon-tickets" size="mini" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
              <el-button v-if="isAuth('fuwujilu','修改') && scope.row.zhuangtai=='待处理'" type="primary" icon="el-icon-edit" size="mini" @click="addOrUpdateHandler(scope.row.id)">修改</el-button>
              <el-button v-if="isAuth('fuwujilu','处理') && scope.row.zhuangtai=='待处理'" type="warning" size="mini" @click="handleService(scope.row.id)">处理</el-button>
              <el-button v-if="scope.row.zhuangtai=='待处理'" type="info" size="mini" @click="cancelService(scope.row.id)">取消</el-button>
              <el-button v-if="isAuth('fuwujilu','删除')" type="danger" icon="el-icon-delete" size="mini" @click="deleteHandler(scope.row.id)">删除</el-button>
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
      typeOptions: ['加床服务', '早餐预订', '客房清洁', '洗衣服务', '叫醒服务', '行李寄存', '维修服务', '其他'],
      statusOptions: ['待处理', '处理中', '已完成', '已取消']
    };
  },
  created() { this.getDataList(); },
  components: { AddOrUpdate },
  methods: {
    contentStyleChange() {},
    getStatusType(status) {
      const map = { '待处理': 'warning', '处理中': 'primary', '已完成': 'success', '已取消': 'info' };
      return map[status] || '';
    },
    search() { this.pageIndex = 1; this.getDataList(); },
    getDataList() {
      this.dataListLoading = true;
      let params = { page: this.pageIndex, limit: this.pageSize, sort: 'addtime', order: 'desc' }
      if(this.searchForm.kefanghao) params['kefanghao'] = '%' + this.searchForm.kefanghao + '%'
      if(this.searchForm.fuwuleixing) params['fuwuleixing'] = this.searchForm.fuwuleixing
      if(this.searchForm.zhuangtai) params['zhuangtai'] = this.searchForm.zhuangtai
      this.$http({ url: "fuwujilu/page", method: "get", params: params }).then(({ data }) => {
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
    handleService(id) {
      this.$confirm('确定将此服务标记为已处理?', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }).then(() => {
        this.$http({ url: `fuwujilu/handle/${id}`, method: "post" }).then(({ data }) => {
          if (data && data.code === 0) { this.$message({ message: "处理成功", type: "success" }); this.search(); }
          else { this.$message.error(data.msg); }
        });
      });
    },
    cancelService(id) {
      this.$confirm('确定取消此服务请求?', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }).then(() => {
        this.$http({ url: `fuwujilu/cancel/${id}`, method: "post" }).then(({ data }) => {
          if (data && data.code === 0) { this.$message({ message: "已取消", type: "success" }); this.search(); }
          else { this.$message.error(data.msg); }
        });
      });
    },
    deleteHandler(id) {
      var ids = id ? [Number(id)] : this.dataListSelections.map(item => Number(item.id));
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", { confirmButtonText: "确定", cancelButtonText: "取消", type: "warning" }).then(() => {
        this.$http({ url: "fuwujilu/delete", method: "post", data: ids }).then(({ data }) => {
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
