<template>
  <div class="main-content">
    <!-- 待清扫房间列表页 -->
    <div v-if="showFlag">
      <el-form :inline="true" :model="searchForm" class="form-content">
        <el-row :gutter="20" class="slt" :style="{justifyContent:'flex-start'}">
          <el-form-item label="客房号">
            <el-input prefix-icon="el-icon-search" v-model="searchForm.kefanghao" placeholder="客房号" clearable></el-input>
          </el-form-item>
          <el-form-item label="客房类型">
            <el-input prefix-icon="el-icon-search" v-model="searchForm.kefangleixing" placeholder="客房类型" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-search" type="success" @click="search()">查询</el-button>
            <el-button icon="el-icon-refresh" type="info" @click="resetSearch()">重置</el-button>
          </el-form-item>
        </el-row>
        <el-row class="ad">
          <el-form-item>
            <el-tag type="warning" size="medium" style="margin-right:10px;">
              <i class="el-icon-warning"></i> 待清扫房间数量: {{ totalPage }}
            </el-tag>
          </el-form-item>
        </el-row>
      </el-form>
      <div class="table-content">
        <el-table class="tables" size="medium" :show-header="true"
            border
            fit
            stripe
            :style="{width: '100%',fontSize:'14px'}"
            v-if="isAuth('daiqingsao','查看')"
            :data="dataList"
            v-loading="dataListLoading">
            <el-table-column label="序号" type="index" width="60" align="center" />
            <el-table-column sortable align="center"
                prop="kefanghao"
                header-align="center"
                label="客房号"
                width="120">
              <template slot-scope="scope">
                <el-tag type="primary">{{scope.row.kefanghao}}</el-tag>
              </template>
            </el-table-column>
            <el-table-column sortable align="center"
                prop="kefangleixing"
                header-align="center"
                label="客房类型"
                width="120">
              <template slot-scope="scope">
                {{scope.row.kefangleixing}}
              </template>
            </el-table-column>
            <el-table-column sortable align="center"
                prop="louceng"
                header-align="center"
                label="楼层"
                width="80">
              <template slot-scope="scope">
                {{scope.row.louceng || '-'}}
              </template>
            </el-table-column>
            <el-table-column sortable align="center"
                prop="chuangxing"
                header-align="center"
                label="床型"
                width="100">
              <template slot-scope="scope">
                {{scope.row.chuangxing}}
              </template>
            </el-table-column>
            <el-table-column sortable align="center"
                prop="suoshujiudian"
                header-align="center"
                label="所属酒店">
              <template slot-scope="scope">
                {{scope.row.suoshujiudian}}
              </template>
            </el-table-column>
            <el-table-column align="center"
                prop="kefangzhuangtai"
                header-align="center"
                label="房间状态"
                width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.kefangzhuangtai=='待清扫'?'warning':'info'">
                  {{scope.row.kefangzhuangtai}}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column align="center"
                prop="weishengqingkuang"
                header-align="center"
                label="卫生情况"
                width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.weishengqingkuang=='待清扫'?'danger':'success'">
                  {{scope.row.weishengqingkuang}}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column width="200" align="center"
                header-align="center"
                label="操作">
                <template slot-scope="scope">
                  <el-button v-if="isAuth('daiqingsao','查看')" type="info" icon="el-icon-view" size="mini" @click="viewDetail(scope.row)">详情</el-button>
                  <el-button v-if="isAuth('daiqingsao','清扫')" type="success" icon="el-icon-check" size="mini" @click="cleanRoom(scope.row)">清扫</el-button>
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
    <!-- 房间详情弹窗 -->
    <el-dialog title="房间详情" :visible.sync="detailVisible" width="600px">
      <el-descriptions :column="2" border v-if="currentRoom">
        <el-descriptions-item label="客房号">{{currentRoom.kefanghao}}</el-descriptions-item>
        <el-descriptions-item label="客房类型">{{currentRoom.kefangleixing}}</el-descriptions-item>
        <el-descriptions-item label="床型">{{currentRoom.chuangxing}}</el-descriptions-item>
        <el-descriptions-item label="楼层">{{currentRoom.louceng || '-'}}</el-descriptions-item>
        <el-descriptions-item label="房间面积">{{currentRoom.fangjianmianji}}</el-descriptions-item>
        <el-descriptions-item label="价格">{{currentRoom.jiage}}</el-descriptions-item>
        <el-descriptions-item label="房间状态">
          <el-tag :type="currentRoom.kefangzhuangtai=='待清扫'?'warning':'info'">{{currentRoom.kefangzhuangtai}}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="卫生情况">
          <el-tag :type="currentRoom.weishengqingkuang=='待清扫'?'danger':'success'">{{currentRoom.weishengqingkuang}}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="所属酒店" :span="2">{{currentRoom.suoshujiudian}}</el-descriptions-item>
        <el-descriptions-item label="客房环境" :span="2">{{currentRoom.kefanghuanjing || '-'}}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer">
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="success" icon="el-icon-check" @click="cleanRoom(currentRoom)">立即清扫</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
export default {
  data() {
    return {
      searchForm: {
        kefanghao: "",
        kefangleixing: ""
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      showFlag: true,
      detailVisible: false,
      currentRoom: null
    };
  },
  created() {
    this.getDataList();
  },
  methods: {
    search() {
      this.pageIndex = 1;
      this.getDataList();
    },
    resetSearch() {
      this.searchForm = {
        kefanghao: "",
        kefangleixing: ""
      };
      this.search();
    },
    getDataList() {
      this.dataListLoading = true;
      let params = {
        page: this.pageIndex,
        limit: this.pageSize,
        sort: 'id',
      }
      if(this.searchForm.kefanghao) {
        params['kefanghao'] = '%' + this.searchForm.kefanghao + '%'
      }
      if(this.searchForm.kefangleixing) {
        params['kefangleixing'] = '%' + this.searchForm.kefangleixing + '%'
      }
      this.$http({
        url: "daiqingsao/page",
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
    viewDetail(row) {
      this.currentRoom = row;
      this.detailVisible = true;
    },
    cleanRoom(row) {
      this.$confirm(`确定将房间 ${row.kefanghao} 标记为已清扫吗?`, "清扫确认", {
        confirmButtonText: "确定清扫",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$http({
          url: `daiqingsao/clean/${row.id}`,
          method: "post"
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({
              message: data.msg || "清扫完成",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.detailVisible = false;
                this.getDataList();
              }
            });
          } else {
            this.$message.error(data.msg || "操作失败");
          }
        });
      }).catch(() => {});
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
  & ::v-deep .el-button--success {
    background-color: #67c23a;
    border-color: #67c23a;
    color: #fff;
  }
  & ::v-deep .el-button--info {
    background-color: #909399;
    border-color: #909399;
    color: #fff;
  }
  & ::v-deep .el-button {
    margin: 4px;
  }
}
</style>
