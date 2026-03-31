<template>
  <div class="main-content">
    <div v-if="showFlag">
      <el-form :inline="true" :model="searchForm" class="form-content">
        <el-row :gutter="20" class="slt">
          <el-form-item label="用户昵称">
            <el-input prefix-icon="el-icon-search" v-model="searchForm.nickname" placeholder="用户昵称" clearable></el-input>
          </el-form-item>
          <el-form-item label="订单编号">
            <el-input v-model="searchForm.dingdanbianhao" placeholder="订单编号" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-search" type="success" @click="search()">查询</el-button>
          </el-form-item>
        </el-row>
        <el-row class="ad">
          <el-form-item>
            <el-button v-if="isAuth('pinglun','删除')" :disabled="dataListSelections.length <= 0" type="danger" icon="el-icon-delete" @click="deleteHandler()">批量删除</el-button>
          </el-form-item>
        </el-row>
      </el-form>
      <div class="table-content">
        <el-table class="tables" size="medium" border stripe v-if="isAuth('pinglun','查看')" :data="dataList" v-loading="dataListLoading" @selection-change="selectionChangeHandler">
          <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
          <el-table-column label="索引" type="index" width="50" align="center"></el-table-column>
          <el-table-column prop="nickname" header-align="center" align="center" label="用户" width="100"></el-table-column>
          <el-table-column prop="content" header-align="center" align="center" label="评价内容" :show-overflow-tooltip="true" min-width="150"></el-table-column>
          <el-table-column prop="pingfen" header-align="center" align="center" label="评分" width="120">
            <template slot-scope="scope">
              <el-rate :value="Number(scope.row.pingfen) || 5" disabled show-score text-color="#ff9900"></el-rate>
            </template>
          </el-table-column>
          <el-table-column prop="dingdanbianhao" header-align="center" align="center" label="订单编号" width="140"></el-table-column>
          <el-table-column prop="reply" header-align="center" align="center" label="回复" :show-overflow-tooltip="true" min-width="120"></el-table-column>
          <el-table-column prop="addtime" header-align="center" align="center" label="评价时间" width="160"></el-table-column>
          <el-table-column width="200" header-align="center" align="center" label="操作">
            <template slot-scope="scope">
              <el-button v-if="isAuth('pinglun','查看')" type="success" icon="el-icon-tickets" size="mini" @click="infoHandler(scope.row)">详情</el-button>
              <el-button v-if="isAuth('pinglun','回复')" type="primary" icon="el-icon-edit" size="mini" @click="replyHandler(scope.row)">回复</el-button>
              <el-button v-if="isAuth('pinglun','删除')" type="danger" icon="el-icon-delete" size="mini" @click="deleteHandler(scope.row.id)">删除</el-button>
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
    <el-dialog title="回复评价" :visible.sync="replyVisible" width="500px">
      <el-form :model="replyForm" label-width="100px">
        <el-form-item label="评价内容">
          <div>{{replyForm.content}}</div>
        </el-form-item>
        <el-form-item label="回复内容">
          <el-input type="textarea" v-model="replyForm.reply" placeholder="请输入回复内容" :rows="4"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" @click="replySubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
export default {
  data() {
    return {
      searchForm: { nickname: "", dingdanbianhao: "" },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showFlag: true,
      replyVisible: false,
      replyForm: {}
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
    getDataList() {
      this.dataListLoading = true;
      let params = { page: this.pageIndex, limit: this.pageSize, sort: 'addtime', order: 'desc' };
      if (this.searchForm.nickname) params['nickname'] = '%' + this.searchForm.nickname + '%';
      if (this.searchForm.dingdanbianhao) params['dingdanbianhao'] = '%' + this.searchForm.dingdanbianhao + '%';
      this.$http({ url: "pinglun/page", method: "get", params }).then(({ data }) => {
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
    infoHandler(row) {
      this.$alert('<p>用户：' + (row.nickname || '') + '</p><p>评分：' + (row.pingfen || 0) + '星</p><p>内容：' + (row.content || '') + '</p><p>回复：' + (row.reply || '暂无') + '</p>', '评价详情', { dangerouslyUseHTMLString: true });
    },
    replyHandler(row) {
      this.replyForm = { id: row.id, content: row.content, reply: row.reply || '' };
      this.replyVisible = true;
    },
    replySubmit() {
      this.$http({ url: "pinglun/reply", method: "post", data: this.replyForm }).then(({ data }) => {
        if (data && data.code === 0) {
          this.$message.success("回复成功");
          this.replyVisible = false;
          this.getDataList();
        } else {
          this.$message.error(data.msg || "回复失败");
        }
      });
    },
    deleteHandler(id) {
      let ids = id ? [id] : this.dataListSelections.map(item => item.id);
      if (ids.length === 0) {
        this.$message.warning("请选择要删除的记录");
        return;
      }
      this.$confirm("确定删除选中的评价？", "提示", { type: "warning" }).then(() => {
        this.$http({ url: "pinglun/delete", method: "post", data: ids }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message.success("删除成功");
            this.getDataList();
          } else {
            this.$message.error(data.msg || "删除失败");
          }
        });
      });
    }
  }
};
</script>
