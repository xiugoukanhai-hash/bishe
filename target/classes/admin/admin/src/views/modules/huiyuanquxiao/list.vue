<template>
  <div class="main-content">
    <!-- 列表页 -->
    <div v-if="showFlag">
      <el-form :inline="true" :model="searchForm" class="form-content">
        <el-row :gutter="20" class="slt">
          <el-form-item label="客房号">
            <el-input prefix-icon="el-icon-search" v-model="searchForm.kefanghao" placeholder="客房号" clearable></el-input>
          </el-form-item>
          <el-form-item label="姓名">
            <el-input prefix-icon="el-icon-search" v-model="searchForm.xingming" placeholder="姓名" clearable></el-input>
          </el-form-item>
          <el-form-item label="审核状态">
            <el-select v-model="searchForm.sfsh" placeholder="请选择" clearable>
              <el-option v-for="item in sfshOptions" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-search" type="success" @click="search()">查询</el-button>
          </el-form-item>
        </el-row>
        <el-row class="ad">
          <el-form-item>
            <el-button v-if="isAuth('huiyuanquxiao','删除')" :disabled="dataListSelections.length <= 0" type="danger" icon="el-icon-delete" @click="deleteHandler()">批量删除</el-button>
          </el-form-item>
        </el-row>
      </el-form>
      <div class="table-content">
        <el-table class="tables" size="medium" border stripe v-if="isAuth('huiyuanquxiao','查看')" :data="dataList" v-loading="dataListLoading" @selection-change="selectionChangeHandler">
          <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
          <el-table-column label="索引" type="index" width="50" align="center"></el-table-column>
          <el-table-column prop="dingdanbianhao" header-align="center" align="center" label="订单编号"></el-table-column>
          <el-table-column prop="kefanghao" header-align="center" align="center" label="客房号"></el-table-column>
          <el-table-column prop="kefangleixing" header-align="center" align="center" label="客房类型"></el-table-column>
          <el-table-column prop="suoshujiudian" header-align="center" align="center" label="所属酒店"></el-table-column>
          <el-table-column prop="zhanghao" header-align="center" align="center" label="账号"></el-table-column>
          <el-table-column prop="xingming" header-align="center" align="center" label="姓名"></el-table-column>
          <el-table-column prop="quxiaoyuanyin" header-align="center" align="center" label="取消原因"></el-table-column>
          <el-table-column prop="quxiaoshijian" header-align="center" align="center" label="取消时间" width="160"></el-table-column>
          <el-table-column prop="sfsh" header-align="center" align="center" label="审核状态">
            <template slot-scope="scope">
              <el-tag :type="scope.row.sfsh=='已通过'?'success':scope.row.sfsh=='已拒绝'?'danger':'warning'">{{scope.row.sfsh}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="shhf" header-align="center" align="center" label="审核回复"></el-table-column>
          <el-table-column prop="ispay" header-align="center" align="center" label="退款状态">
            <template slot-scope="scope">
              <span style="margin-right:10px">{{scope.row.ispay=='已退款'?'已退款':'未退款'}}</span>
              <el-button v-if="scope.row.sfsh=='已通过' && scope.row.ispay!='已退款' && isAuth('huiyuanquxiao','支付')" type="text" icon="el-icon-edit" size="small" @click="refundHandler(scope.row)">退款</el-button>
            </template>
          </el-table-column>
          <el-table-column width="200" header-align="center" align="center" label="操作">
            <template slot-scope="scope">
              <el-button v-if="isAuth('huiyuanquxiao','查看')" type="success" icon="el-icon-tickets" size="mini" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
              <el-button v-if="isAuth('huiyuanquxiao','审核') && scope.row.sfsh=='待审核'" type="primary" icon="el-icon-edit" size="mini" @click="shHandler(scope.row)">审核</el-button>
              <el-button v-if="isAuth('huiyuanquxiao','删除')" type="danger" icon="el-icon-delete" size="mini" @click="deleteHandler(scope.row.id)">删除</el-button>
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
    <!-- 审核弹窗 -->
    <el-dialog title="审核" :visible.sync="shVisible" width="500px">
      <el-form :model="shForm" label-width="100px">
        <el-form-item label="审核状态">
          <el-radio-group v-model="shForm.sfsh">
            <el-radio label="已通过">通过</el-radio>
            <el-radio label="已拒绝">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核回复">
          <el-input type="textarea" v-model="shForm.shhf" placeholder="请输入审核回复"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="shVisible = false">取消</el-button>
        <el-button type="primary" @click="shSubmit">确定</el-button>
      </div>
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
        xingming: "",
        sfsh: ""
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showFlag: true,
      addOrUpdateFlag: false,
      shVisible: false,
      shForm: {},
      sfshOptions: ["待审核", "已通过", "已拒绝"]
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
      if(this.searchForm.xingming) {
        params['xingming'] = '%' + this.searchForm.xingming + '%';
      }
      if(this.searchForm.sfsh) {
        params['sfsh'] = this.searchForm.sfsh;
      }
      this.$http({
        url: "huiyuanquxiao/page",
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
    shHandler(row) {
      this.shForm = {
        id: row.id,
        sfsh: '已通过',
        shhf: ''
      };
      this.shVisible = true;
    },
    shSubmit() {
      this.$http({
        url: "huiyuanquxiao/update",
        method: "post",
        data: this.shForm
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.$message({
            message: "审核成功",
            type: "success",
            duration: 1500,
            onClose: () => {
              this.shVisible = false;
              this.getDataList();
            }
          });
        } else {
          this.$message.error(data.msg);
        }
      });
    },
    refundHandler(row) {
      this.$confirm(`确定对订单 ${row.dingdanbianhao} 进行退款操作?`, "退款确认", {
        confirmButtonText: "确定退款",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$http({
          url: `huiyuanquxiao/refund/${row.id}`,
          method: "post"
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({
              message: "退款成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.getDataList();
              }
            });
          } else {
            this.$message.error(data.msg);
          }
        });
      }).catch(() => {});
    },
    deleteHandler(id) {
      var ids = id ? [Number(id)] : this.dataListSelections.map(item => Number(item.id));
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$http({
          url: "huiyuanquxiao/delete",
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
</style>
