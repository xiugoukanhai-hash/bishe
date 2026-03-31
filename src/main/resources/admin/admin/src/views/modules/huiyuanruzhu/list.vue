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
          <el-form-item label="客房状态">
            <el-select v-model="searchForm.kefangzhuangtai" placeholder="请选择" clearable>
              <el-option v-for="item in kefangzhuangtaiOptions" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-search" type="success" @click="search()">查询</el-button>
          </el-form-item>
        </el-row>
        <el-row class="ad">
          <el-form-item>
            <el-button v-if="isAuth('huiyuanruzhu','新增')" type="success" icon="el-icon-plus" @click="addOrUpdateHandler()">新增</el-button>
            <el-button v-if="isAuth('huiyuanruzhu','删除')" :disabled="dataListSelections.length <= 0" type="danger" icon="el-icon-delete" @click="deleteHandler()">批量删除</el-button>
          </el-form-item>
        </el-row>
      </el-form>
      <div class="table-content">
        <el-table class="tables" size="medium" border stripe v-if="isAuth('huiyuanruzhu','查看')" :data="dataList" v-loading="dataListLoading" @selection-change="selectionChangeHandler">
          <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
          <el-table-column label="索引" type="index" width="50" align="center"></el-table-column>
          <el-table-column prop="dingdanbianhao" header-align="center" align="center" label="订单编号"></el-table-column>
          <el-table-column prop="kefanghao" header-align="center" align="center" label="客房号"></el-table-column>
          <el-table-column prop="kefangleixing" header-align="center" align="center" label="客房类型"></el-table-column>
          <el-table-column prop="suoshujiudian" header-align="center" align="center" label="所属酒店"></el-table-column>
          <el-table-column prop="zhanghao" header-align="center" align="center" label="账号"></el-table-column>
          <el-table-column prop="xingming" header-align="center" align="center" label="姓名"></el-table-column>
          <el-table-column prop="shenfenzheng" header-align="center" align="center" label="身份证"></el-table-column>
          <el-table-column prop="shouji" header-align="center" align="center" label="手机"></el-table-column>
          <el-table-column prop="kefangzhuangtai" header-align="center" align="center" label="客房状态"></el-table-column>
          <el-table-column prop="ruzhuyajin" header-align="center" align="center" label="入住押金"></el-table-column>
          <el-table-column prop="zhifufangshi" header-align="center" align="center" label="支付方式"></el-table-column>
          <el-table-column prop="ruzhushijian" header-align="center" align="center" label="入住时间" width="160"></el-table-column>
          <el-table-column prop="ispay" header-align="center" align="center" label="是否支付">
            <template slot-scope="scope">
              <span style="margin-right:10px">{{scope.row.ispay=='已支付'?'已支付':'未支付'}}</span>
              <el-button v-if="scope.row.ispay!='已支付' && isAuth('huiyuanruzhu','支付')" type="text" icon="el-icon-edit" size="small" @click="payHandler(scope.row)">支付</el-button>
            </template>
          </el-table-column>
          <el-table-column width="300" header-align="center" align="center" label="操作">
            <template slot-scope="scope">
              <el-button v-if="isAuth('huiyuanruzhu','查看')" type="success" icon="el-icon-tickets" size="mini" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
              <el-button v-if="isAuth('huiyuanruzhu','修改') && scope.row.kefangzhuangtai == '已入住'" type="primary" icon="el-icon-edit" size="mini" @click="addOrUpdateHandler(scope.row.id)">修改</el-button>
              <el-button v-if="isAuth('huiyuanruzhu','会员退房') && scope.row.kefangzhuangtai == '已入住'" type="warning" icon="el-icon-s-home" size="mini" @click="tuifangHandler(scope.row)">退房</el-button>
              <span v-if="scope.row.kefangzhuangtai == '已退房'" style="color: #909399; margin-right: 10px;">已退房</span>
              <el-button v-if="isAuth('huiyuanruzhu','删除')" type="danger" icon="el-icon-delete" size="mini" @click="deleteHandler(scope.row.id)">删除</el-button>
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
    <huiyuantuifang-cross-add-or-update v-if="huiyuantuifangCrossAddOrUpdateFlag" :parent="this" ref="huiyuantuifangCrossaddOrUpdate"></huiyuantuifang-cross-add-or-update>
  </div>
</template>
<script>
import AddOrUpdate from "./add-or-update";
import huiyuantuifangCrossAddOrUpdate from "../huiyuantuifang/add-or-update";
export default {
  data() {
    return {
      searchForm: {
        kefanghao: "",
        xingming: "",
        kefangzhuangtai: ""
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showFlag: true,
      addOrUpdateFlag: false,
      huiyuantuifangCrossAddOrUpdateFlag: false,
      kefangzhuangtaiOptions: ["已入住", "待清扫"]
    };
  },
  created() {
    this.getDataList();
  },
  components: {
    AddOrUpdate,
    huiyuantuifangCrossAddOrUpdate
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
      if(this.searchForm.kefangzhuangtai) {
        params['kefangzhuangtai'] = this.searchForm.kefangzhuangtai;
      }
      this.$http({
        url: "huiyuanruzhu/page",
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
      this.huiyuantuifangCrossAddOrUpdateFlag = false;
      if(type != 'info') {
        type = 'else';
      }
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id, type);
      });
    },
    tuifangHandler(row) {
      this.$http({
        url: `huiyuantuifang/calcFee/${row.id}`,
        method: "get"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          const feeInfo = data.data;
          let message = `房间号：${row.kefanghao}\n`;
          message += `入住时间：${row.ruzhushijian || '未知'}\n`;
          message += `基础价格：￥${feeInfo.baseFee || 0}\n`;
          message += `超时费用：￥${feeInfo.overTimeFee || 0}`;
          if (feeInfo.overTimeHours > 0) {
            message += `（已享9折会员优惠）`;
          }
          message += `\n总计费用：￥${feeInfo.totalFee || 0}`;
          if (feeInfo.overTimeHours > 0) {
            message += `\n（超时${feeInfo.overTimeHours}小时）`;
          }
          this.$confirm(message, "退房结算确认（会员）", {
            confirmButtonText: "确定退房",
            cancelButtonText: "取消",
            type: "info",
            customClass: 'checkout-confirm'
          }).then(() => {
            this.$http({
              url: `huiyuantuifang/checkOut/${row.id}`,
              method: "post"
            }).then(({ data }) => {
              if (data && data.code === 0) {
                this.$message({
                  message: data.msg || "退房办理成功",
                  type: "success",
                  duration: 2000,
                  onClose: () => {
                    this.getDataList();
                  }
                });
              } else {
                this.$message.error(data.msg || "退房失败");
              }
            });
          }).catch(() => {});
        } else {
          this.$message.error(data.msg || "获取费用信息失败");
        }
      });
    },
    payHandler(row) {
      this.$confirm(`确定支付订单 ${row.dingdanbianhao} 的费用?`, "支付确认", {
        confirmButtonText: "确定支付",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$http({
          url: `huiyuanruzhu/pay/${row.id}`,
          method: "post"
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({
              message: "支付成功",
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
          url: "huiyuanruzhu/delete",
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
