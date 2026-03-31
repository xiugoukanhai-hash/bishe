<template>
  <div class="main-content">
    <div v-if="showFlag">
      <el-form :inline="true" :model="searchForm" class="form-content">
        <el-row :gutter="20" class="slt">
          <el-form-item label="客房号">
            <el-input prefix-icon="el-icon-search" v-model="searchForm.kefanghao" placeholder="客房号" clearable></el-input>
          </el-form-item>
          <el-form-item label="床型">
            <el-input prefix-icon="el-icon-search" v-model="searchForm.chuangxing" placeholder="床型" clearable></el-input>
          </el-form-item>
          <el-form-item label="客房状态">
            <el-select v-model="searchForm.kefangzhuangtai" placeholder="请选择" clearable>
              <el-option v-for="item in kefangzhuangtaiOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-search" type="success" @click="search()">查询</el-button>
          </el-form-item>
        </el-row>
        <el-row class="ad">
          <el-form-item>
            <el-button v-if="isAuth('kefangxinxi','新增')" type="success" icon="el-icon-plus" @click="addOrUpdateHandler()">新增</el-button>
            <el-button v-if="isAuth('kefangxinxi','删除')" :disabled="dataListSelections.length <= 0" type="danger" icon="el-icon-delete" @click="deleteHandler()">批量删除</el-button>
          </el-form-item>
        </el-row>
      </el-form>
      <div class="table-content">
        <el-table class="tables" size="medium" border stripe :data="dataList" v-loading="dataListLoading" @selection-change="selectionChangeHandler">
          <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
          <el-table-column label="序号" type="index" width="60" align="center"></el-table-column>
          <el-table-column prop="kefanghao" header-align="center" align="center" label="客房号"></el-table-column>
          <el-table-column prop="kefangleixing" header-align="center" align="center" label="客房类型"></el-table-column>
          <el-table-column prop="chuangxing" header-align="center" align="center" label="床型"></el-table-column>
          <el-table-column prop="kefangtupian" header-align="center" align="center" label="客房图片" width="120">
            <template slot-scope="scope">
              <div v-if="scope.row.kefangtupian">
                <img :src="getImageUrl(scope.row.kefangtupian)" width="80" height="60" style="object-fit: cover;">
              </div>
              <div v-else>暂无图片</div>
            </template>
          </el-table-column>
          <el-table-column prop="fangjianmianji" header-align="center" align="center" label="房间面积"></el-table-column>
          <el-table-column prop="jiage" header-align="center" align="center" label="价格">
            <template slot-scope="scope">
              <span style="color: #f56c6c;">￥{{ scope.row.jiage }}/晚</span>
            </template>
          </el-table-column>
          <el-table-column prop="kefangzhuangtai" header-align="center" align="center" label="客房状态">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.kefangzhuangtai)">{{ scope.row.kefangzhuangtai }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="keyueshijian" header-align="center" align="center" label="可预时间" width="160"></el-table-column>
          <el-table-column prop="weishengqingkuang" header-align="center" align="center" label="卫生情况"></el-table-column>
          <el-table-column prop="kefanghuanjing" header-align="center" align="center" label="客房环境" show-overflow-tooltip></el-table-column>
          <el-table-column prop="suoshujiudian" header-align="center" align="center" label="所属酒店"></el-table-column>
          <el-table-column header-align="center" align="center" label="操作" width="320">
            <template slot-scope="scope">
              <el-button v-if="isAuth('kefangxinxi','查看')" type="success" icon="el-icon-tickets" size="mini" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
              <el-button v-if="isAuth('kefangxinxi','用户预约') && canBook(scope.row)" type="warning" icon="el-icon-date" size="mini" @click="yonghuyuyueCrossAddOrUpdateHandler(scope.row,'cross')">用户预约</el-button>
              <el-button v-if="isAuth('kefangxinxi','会员预约') && canBook(scope.row)" type="warning" icon="el-icon-date" size="mini" @click="huiyuanyuyueCrossAddOrUpdateHandler(scope.row,'cross')">会员预约</el-button>
              <el-button v-if="isAuth('kefangxinxi','修改')" type="primary" icon="el-icon-edit" size="mini" @click="addOrUpdateHandler(scope.row.id)">修改</el-button>
              <el-button v-if="isAuth('kefangxinxi','删除')" type="danger" icon="el-icon-delete" size="mini" @click="deleteHandler(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination :layout="'total, sizes, prev, pager, next, jumper'" @size-change="sizeChangeHandle" @current-change="currentChangeHandle" :current-page="pageIndex" :page-sizes="[10, 20, 50, 100]" :page-size="pageSize" :total="totalPage" background style="text-align:right;margin-top:15px;"></el-pagination>
      </div>
    </div>
    <add-or-update v-if="addOrUpdateFlag" :parent="this" ref="addOrUpdate"></add-or-update>
    <yonghuyuyue-cross-add-or-update v-if="yonghuyuyueCrossAddOrUpdateFlag" :parent="this" ref="yonghuyuyueCrossaddOrUpdate"></yonghuyuyue-cross-add-or-update>
    <huiyuanyuyue-cross-add-or-update v-if="huiyuanyuyueCrossAddOrUpdateFlag" :parent="this" ref="huiyuanyuyueCrossaddOrUpdate"></huiyuanyuyue-cross-add-or-update>
  </div>
</template>
<script>
import AddOrUpdate from "./add-or-update";
import yonghuyuyueCrossAddOrUpdate from "../yonghuyuyue/add-or-update";
import huiyuanyuyueCrossAddOrUpdate from "../huiyuanyuyue/add-or-update";
export default {
  data() {
    return {
      searchForm: {
        kefanghao: "",
        chuangxing: "",
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
      yonghuyuyueCrossAddOrUpdateFlag: false,
      huiyuanyuyueCrossAddOrUpdateFlag: false,
      kefangzhuangtaiOptions: [
        {label: "空闲", value: "空闲"},
        {label: "已预约", value: "已预约"},
        {label: "已入住", value: "已入住"},
        {label: "待清扫", value: "待清扫"}
      ]
    };
  },
  created() {
    this.getDataList();
  },
  components: {
    AddOrUpdate,
    yonghuyuyueCrossAddOrUpdate,
    huiyuanyuyueCrossAddOrUpdate
  },
  methods: {
    getImageUrl(path) {
      if (!path) return '';
      const filename = path.split(',')[0];
      if (filename.startsWith('http')) return filename;
      const cleanName = filename.replace(/^upload\/+/, '');
      return '/springboot6alf1/upload/' + cleanName;
    },
    getStatusType(status) {
      if (status === "空闲") return "success";
      if (status === "已预约") return "warning";
      if (status === "已入住") return "danger";
      if (status === "待清扫") return "info";
      return "";
    },
    search() {
      this.pageIndex = 1;
      this.getDataList();
    },
    getDataList() {
      this.dataListLoading = true;
      let params = {
        page: this.pageIndex,
        limit: this.pageSize,
        sort: "id"
      };
      if(this.searchForm.kefanghao) {
        params["kefanghao"] = "%" + this.searchForm.kefanghao + "%";
      }
      if(this.searchForm.chuangxing) {
        params["chuangxing"] = "%" + this.searchForm.chuangxing + "%";
      }
      if(this.searchForm.kefangzhuangtai) {
        params["kefangzhuangtai"] = this.searchForm.kefangzhuangtai;
      }
      this.$http({
        url: "kefangxinxi/page",
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
      this.yonghuyuyueCrossAddOrUpdateFlag = false;
      this.huiyuanyuyueCrossAddOrUpdateFlag = false;
      if(type != "info") {
        type = "else";
      }
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id, type);
      });
    },
    yonghuyuyueCrossAddOrUpdateHandler(row, type) {
      this.showFlag = false;
      this.addOrUpdateFlag = false;
      this.yonghuyuyueCrossAddOrUpdateFlag = true;
      this.huiyuanyuyueCrossAddOrUpdateFlag = false;
      this.$storage.set("crossObj", row);
      this.$storage.set("crossTable", "kefangxinxi");
      this.$nextTick(() => {
        this.$refs.yonghuyuyueCrossaddOrUpdate.init(null, type);
      });
    },
    huiyuanyuyueCrossAddOrUpdateHandler(row, type) {
      this.showFlag = false;
      this.addOrUpdateFlag = false;
      this.yonghuyuyueCrossAddOrUpdateFlag = false;
      this.huiyuanyuyueCrossAddOrUpdateFlag = true;
      this.$storage.set("crossObj", row);
      this.$storage.set("crossTable", "kefangxinxi");
      this.$nextTick(() => {
        this.$refs.huiyuanyuyueCrossaddOrUpdate.init(null, type);
      });
    },
    canBook(row) {
      const status = row.kefangzhuangtai;
      const cleanStatus = row.weishengqingkuang;
      const statusOk = status === "空闲" || status === "未入住";
      const cleanOk = cleanStatus === "已清扫" || !cleanStatus;
      return statusOk && cleanOk;
    },
    deleteHandler(id) {
      var ids = id ? [Number(id)] : this.dataListSelections.map(item => Number(item.id));
      this.$confirm("确定删除选中的记录吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$http({
          url: "kefangxinxi/delete",
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
  &::v-deep .el-button {
    margin: 4px;
  }
}
</style>
