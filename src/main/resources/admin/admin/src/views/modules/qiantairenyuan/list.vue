<template>
  <div class="main-content">
    <div v-if="showFlag">
      <el-form :inline="true" :model="searchForm" class="form-content">
        <el-row :gutter="20" class="slt" :style="{justifyContent:contents.searchBoxPosition=='1'?'flex-start':contents.searchBoxPosition=='2'?'center':'flex-end'}">
          <el-form-item :label="contents.inputTitle == 1 ? '前台账号' : ''">
            <el-input v-if="contents.inputIcon == 1" prefix-icon="el-icon-search" v-model="searchForm.qiantaizhanghao" placeholder="前台账号" clearable></el-input>
            <el-input v-else v-model="searchForm.qiantaizhanghao" placeholder="前台账号" clearable></el-input>
          </el-form-item>
          <el-form-item :label="contents.inputTitle == 1 ? '前台姓名' : ''">
            <el-input v-if="contents.inputIcon == 1" prefix-icon="el-icon-search" v-model="searchForm.qiantaixingming" placeholder="前台姓名" clearable></el-input>
            <el-input v-else v-model="searchForm.qiantaixingming" placeholder="前台姓名" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-search" type="success" @click="search()">查询</el-button>
          </el-form-item>
        </el-row>
        <el-row class="ad" :style="{justifyContent:contents.btnAdAllBoxPosition=='1'?'flex-start':contents.btnAdAllBoxPosition=='2'?'center':'flex-end'}">
          <el-form-item>
            <el-button v-if="isAuth('qiantairenyuan','新增')" type="success" icon="el-icon-plus" @click="addOrUpdateHandler()">新增</el-button>
            <el-button v-if="isAuth('qiantairenyuan','删除') && contents.tableSelection" :disabled="dataListSelections.length <= 0" type="danger" icon="el-icon-delete" @click="deleteHandler()">删除</el-button>
          </el-form-item>
        </el-row>
      </el-form>
      <div class="table-content">
        <el-table class="tables" :size="contents.tableSize" :show-header="contents.tableShowHeader"
            :header-row-style="headerRowStyle" :header-cell-style="headerCellStyle"
            :border="contents.tableBorder" :fit="contents.tableFit" :stripe="contents.tableStripe"
            :row-style="rowStyle" :cell-style="cellStyle"
            :style="{width: '100%',fontSize:contents.tableContentFontSize,color:contents.tableContentFontColor}"
            v-if="isAuth('qiantairenyuan','查看')" :data="dataList" v-loading="dataListLoading"
            @selection-change="selectionChangeHandler">
            <el-table-column v-if="contents.tableSelection" type="selection" header-align="center" align="center" width="50"></el-table-column>
            <el-table-column label="索引" v-if="contents.tableIndex" type="index" width="50" />
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign" prop="qiantaizhanghao" header-align="center" label="前台账号">
              <template slot-scope="scope">{{scope.row.qiantaizhanghao}}</template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign" prop="qiantaixingming" header-align="center" label="前台姓名">
              <template slot-scope="scope">{{scope.row.qiantaixingming}}</template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign" prop="xingbie" header-align="center" label="性别">
              <template slot-scope="scope">{{scope.row.xingbie}}</template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign" prop="shouji" header-align="center" label="手机">
              <template slot-scope="scope">{{scope.row.shouji}}</template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign" prop="ruzhishijian" header-align="center" label="入职时间">
              <template slot-scope="scope">{{scope.row.ruzhishijian}}</template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign" prop="zhuangtai" header-align="center" label="状态">
              <template slot-scope="scope">
                <el-tag :type="scope.row.zhuangtai=='在职'?'success':scope.row.zhuangtai=='请假'?'warning':'danger'">{{scope.row.zhuangtai}}</el-tag>
              </template>
            </el-table-column>
            <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign" prop="zhaopian" header-align="center" width="120" label="照片">
              <template slot-scope="scope">
                <div v-if="scope.row.zhaopian"><img :src="getImageUrl(scope.row.zhaopian)" width="80" height="80"></div>
                <div v-else>无图片</div>
              </template>
            </el-table-column>
            <el-table-column width="320" :align="contents.tableAlign" header-align="center" label="操作">
              <template slot-scope="scope">
                <el-button v-if="isAuth('qiantairenyuan','查看')" type="success" icon="el-icon-tickets" size="mini" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
                <el-button v-if="isAuth('qiantairenyuan','修改')" type="primary" icon="el-icon-edit" size="mini" @click="addOrUpdateHandler(scope.row.id)">修改</el-button>
                <el-button v-if="isAuth('qiantairenyuan','重置密码')" type="warning" size="mini" @click="resetPassword(scope.row.qiantaizhanghao)">重置密码</el-button>
                <el-button v-if="isAuth('qiantairenyuan','删除')" type="danger" icon="el-icon-delete" size="mini" @click="deleteHandler(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
        </el-table>
        <el-pagination
          :layout="layouts" @size-change="sizeChangeHandle" @current-change="currentChangeHandle"
          :current-page="pageIndex" :page-sizes="[10, 20, 50, 100]" :page-size="Number(contents.pageEachNum)"
          :total="totalPage" :small="contents.pageStyle" class="pagination-content" :background="contents.pageBtnBG"
          :style="{textAlign:contents.pagePosition==1?'left':contents.pagePosition==2?'center':'right'}"
        ></el-pagination>
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
      form:{},
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showFlag: true,
      addOrUpdateFlag: false,
      contents:{"searchBtnFontColor":"rgba(0, 0, 0, 1)","pagePosition":"1","inputFontSize":"14px","inputBorderRadius":"4px","tableBtnDelFontColor":"rgba(0, 0, 0, 1)","tableBtnIconPosition":"1","searchBtnHeight":"40px","inputIconColor":"#C0C4CC","searchBtnBorderRadius":"4px","tableStripe":true,"btnAdAllWarnFontColor":"rgba(0, 0, 0, 1)","tableBtnDelBgColor":"rgba(255, 255, 204, 1)","searchBtnIcon":"1","tableSize":"medium","searchBtnBorderStyle":"solid","tableSelection":true,"searchBtnBorderWidth":"1px","tableContentFontSize":"14px","searchBtnBgColor":"rgba(36, 194, 205, 1)","inputTitleSize":"14px","btnAdAllBorderColor":"#DCDFE6","pageJumper":true,"btnAdAllIconPosition":"1","searchBoxPosition":"1","tableBtnDetailFontColor":"rgba(0, 0, 0, 1)","tableBtnHeight":"40px","pagePager":true,"searchBtnBorderColor":"#DCDFE6","tableHeaderFontColor":"#909399","inputTitle":"1","tableBtnBorderRadius":"4px","btnAdAllFont":"0","btnAdAllDelFontColor":"rgba(0, 0, 0, 1)","tableBtnIcon":"1","btnAdAllHeight":"40px","btnAdAllWarnBgColor":"rgba(102, 102, 153, 1)","btnAdAllBorderWidth":"1px","tableStripeFontColor":"#606266","tableBtnBorderStyle":"solid","inputHeight":"40px","btnAdAllBorderRadius":"4px","btnAdAllDelBgColor":"rgba(255, 255, 204, 1)","pagePrevNext":true,"btnAdAllAddBgColor":"rgba(36, 194, 205, 1)","searchBtnFont":"0","tableIndex":true,"btnAdAllIcon":"1","tableSortable":true,"pageSizes":true,"tableFit":true,"pageBtnBG":false,"searchBtnFontSize":"14px","tableBtnEditBgColor":"rgba(102, 102, 153, 1)","inputBorderWidth":"1px","inputFontPosition":"1","inputFontColor":"#333","pageEachNum":10,"tableHeaderBgColor":"#fff","inputTitleColor":"#333","btnAdAllBoxPosition":"1","tableBtnDetailBgColor":"rgba(36, 194, 205, 1)","inputIcon":"1","searchBtnIconPosition":"2","btnAdAllFontSize":"10px","inputBorderStyle":"solid","inputBgColor":"#fff","pageStyle":false,"pageTotal":true,"btnAdAllAddFontColor":"rgba(0, 0, 0, 1)","tableBtnFont":"1","tableContentFontColor":"#606266","inputBorderColor":"#DCDFE6","tableShowHeader":true,"tableBtnFontSize":"10px","tableBtnBorderColor":"#DCDFE6","inputIconPosition":"2","tableBorder":true,"btnAdAllBorderStyle":"solid","tableBtnBorderWidth":"0px","tableStripeBgColor":"#F5F7FA","tableBtnEditFontColor":"rgba(0, 0, 0, 1)","tableAlign":"center"},
      layouts: ''
    };
  },
  created() {
    this.init();
    this.getDataList();
    this.contentStyleChange();
  },
  components: { AddOrUpdate },
  methods: {
    getImageUrl(path) {
      if (!path) return '';
      const filename = path.split(',')[0];
      if (filename.startsWith('http')) return filename;
      const cleanName = filename.replace(/^upload\/+/, '');
      return '/springboot6alf1/upload/' + cleanName;
    },
    contentStyleChange() {
      this.contentPageStyleChange();
    },
    rowStyle({ row, rowIndex}) {
      if (rowIndex % 2 == 1 && this.contents.tableStripe) {
        return {color:this.contents.tableStripeFontColor}
      }
      return ''
    },
    cellStyle({ row, rowIndex}){
      if (rowIndex % 2 == 1 && this.contents.tableStripe) {
        return {backgroundColor:this.contents.tableStripeBgColor}
      }
      return ''
    },
    headerRowStyle({ row, rowIndex}){
      return {color: this.contents.tableHeaderFontColor}
    },
    headerCellStyle({ row, rowIndex}){
      return {backgroundColor: this.contents.tableHeaderBgColor}
    },
    contentPageStyleChange(){
      let arr = []
      if(this.contents.pageTotal) arr.push('total')
      if(this.contents.pageSizes) arr.push('sizes')
      if(this.contents.pagePrevNext){
        arr.push('prev')
        if(this.contents.pagePager) arr.push('pager')
        arr.push('next')
      }
      if(this.contents.pageJumper) arr.push('jumper')
      this.layouts = arr.join()
      this.contents.pageEachNum = 10
    },
    init () {},
    search() {
      this.pageIndex = 1;
      this.getDataList();
    },
    getDataList() {
      this.dataListLoading = true;
      let params = { page: this.pageIndex, limit: this.pageSize, sort: 'id' }
      if(this.searchForm.qiantaizhanghao) {
        params['qiantaizhanghao'] = '%' + this.searchForm.qiantaizhanghao + '%'
      }
      if(this.searchForm.qiantaixingming) {
        params['qiantaixingming'] = '%' + this.searchForm.qiantaixingming + '%'
      }
      this.$http({
        url: "qiantairenyuan/page",
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
    addOrUpdateHandler(id,type) {
      this.showFlag = false;
      this.addOrUpdateFlag = true;
      if(type!='info') type = 'else';
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id,type);
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
          url: "qiantairenyuan/delete",
          method: "post",
          data: ids
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({ message: "操作成功", type: "success", duration: 1500, onClose: () => { this.search(); } });
          } else {
            this.$message.error(data.msg);
          }
        });
      });
    },
    resetPassword(username) {
      this.$confirm(`确定重置账号 ${username} 的密码为 123456 ?`, "重置密码", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$http({
          url: "qiantairenyuan/resetPass",
          method: "get",
          params: { username: username }
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({ message: data.msg || "密码重置成功", type: "success" });
          } else {
            this.$message.error(data.msg);
          }
        });
      });
    },
  }
};
</script>
<style lang="scss" scoped>
.slt { margin: 0 !important; display: flex; }
.ad { margin: 0 !important; display: flex; }
.el-button+.el-button { margin:0; }
.tables {
  & ::v-deep .el-button--success { height: 40px; color: rgba(0, 0, 0, 1); font-size: 10px; border-width: 0px; border-radius: 4px; background-color: rgba(36, 194, 205, 1); }
  & ::v-deep .el-button--primary { height: 40px; color: rgba(0, 0, 0, 1); font-size: 10px; border-width: 0px; border-radius: 4px; background-color: rgba(102, 102, 153, 1); }
  & ::v-deep .el-button--danger { height: 40px; color: rgba(0, 0, 0, 1); font-size: 10px; border-width: 0px; border-radius: 4px; background-color: rgba(255, 255, 204, 1); }
  & ::v-deep .el-button { margin: 4px; }
}
</style>
