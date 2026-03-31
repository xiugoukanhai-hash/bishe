<template>
  <div class="addEdit-block">
    <el-form class="detail-form-content" ref="ruleForm" :model="ruleForm" :rules="rules" label-width="100px">
      <el-row>
        <el-col :span="12">
          <el-form-item v-if="type!='info'" label="通知标题" prop="title">
            <el-input v-model="ruleForm.title" placeholder="请输入通知标题" clearable></el-input>
          </el-form-item>
          <el-form-item v-else label="通知标题"><el-input v-model="ruleForm.title" readonly></el-input></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item v-if="type!='info'" label="通知类型" prop="type">
            <el-select v-model="ruleForm.type" placeholder="请选择通知类型">
              <el-option v-for="item in typeOptions" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item v-else label="通知类型"><el-input v-model="ruleForm.type" readonly></el-input></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item v-if="type!='info'" label="接收用户" prop="tablename">
            <el-select v-model="ruleForm.tablename" placeholder="请选择接收用户类型">
              <el-option label="全部用户" value="all"></el-option>
              <el-option label="普通用户" value="yonghu"></el-option>
              <el-option label="会员" value="huiyuan"></el-option>
              <el-option label="前台人员" value="qiantairenyuan"></el-option>
              <el-option label="清洁人员" value="qingjierenyuan"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item v-else label="用户类型">
            <el-input :value="getUserTypeName(ruleForm.tablename)" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="type=='info'">
          <el-form-item label="阅读状态">
            <el-tag :type="ruleForm.isread==1?'success':'info'">{{ruleForm.isread==1?'已读':'未读'}}</el-tag>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item v-if="type!='info'" label="通知内容" prop="content">
            <el-input type="textarea" :rows="4" v-model="ruleForm.content" placeholder="请输入通知内容"></el-input>
          </el-form-item>
          <el-form-item v-else label="通知内容">
            <el-input type="textarea" :rows="4" v-model="ruleForm.content" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="type=='info'">
          <el-form-item label="发送时间"><el-input v-model="ruleForm.addtime" readonly></el-input></el-form-item>
        </el-col>
        <el-col :span="12" v-if="type=='info' && ruleForm.readtime">
          <el-form-item label="阅读时间"><el-input v-model="ruleForm.readtime" readonly></el-input></el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="btn">
        <el-button v-if="type!='info'" type="primary" class="btn-success" @click="onSubmit">发送通知</el-button>
        <el-button class="btn-close" @click="back()">{{type=='info'?'返回':'取消'}}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
export default {
  data() {
    return {
      type: '',
      ruleForm: { title: '', type: '系统通知', tablename: 'all', content: '', isread: 0, addtime: '', readtime: '' },
      typeOptions: ['预约审核', '入住通知', '退房通知', '清扫通知', '系统通知', '积分变动'],
      rules: {
        title: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
        type: [{ required: true, message: '请选择通知类型', trigger: 'change' }],
        tablename: [{ required: true, message: '请选择接收用户', trigger: 'change' }],
        content: [{ required: true, message: '请输入通知内容', trigger: 'blur' }]
      }
    };
  },
  props: ["parent"],
  methods: {
    init(id, type) {
      this.type = type;
      if (id) this.info(id);
      else this.ruleForm = { title: '', type: '系统通知', tablename: 'all', content: '', isread: 0 };
    },
    info(id) {
      this.$http({ url: `tongzhi/info/${id}`, method: "get" }).then(({ data }) => {
        if (data && data.code === 0) { this.ruleForm = data.data; }
        else { this.$message.error(data.msg); }
      });
    },
    getUserTypeName(tablename) {
      const map = { 'yonghu': '普通用户', 'huiyuan': '会员', 'qiantairenyuan': '前台人员', 'qingjierenyuan': '清洁人员', 'users': '管理员', 'all': '全部用户' };
      return map[tablename] || tablename;
    },
    onSubmit() {
      this.$refs["ruleForm"].validate(valid => {
        if (valid) {
          this.$http({ url: "tongzhi/save", method: "post", data: this.ruleForm }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({ message: "发送成功", type: "success", duration: 1500, onClose: () => {
                this.parent.showFlag = true;
                this.parent.addOrUpdateFlag = false;
                this.parent.search();
              }});
            } else { this.$message.error(data.msg); }
          });
        }
      });
    },
    back() {
      this.parent.showFlag = true;
      this.parent.addOrUpdateFlag = false;
      this.parent.contentStyleChange();
    }
  }
};
</script>
<style lang="scss">
.addEdit-block { margin: -10px; }
.detail-form-content { padding: 12px; }
.btn .el-button { padding: 0 20px; margin-right: 10px; }
.btn-success { background: #3498db !important; border-color: #3498db !important; color: #fff !important; }
</style>
