<template>
  <div class="addEdit-block">
    <el-form class="detail-form-content" ref="ruleForm" :model="ruleForm" :rules="rules" label-width="100px">
      <el-row>
        <el-col :span="12">
          <el-form-item v-if="type!='info'" label="问题类型" prop="wentileixing">
            <el-select v-model="ruleForm.wentileixing" placeholder="请选择类型">
              <el-option v-for="item in typeOptions" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item v-else label="问题类型"><el-input v-model="ruleForm.wentileixing" readonly></el-input></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item v-if="type!='info'" label="关键词" prop="guanjianci">
            <el-input v-model="ruleForm.guanjianci" placeholder="多个关键词用逗号分隔" clearable></el-input>
          </el-form-item>
          <el-form-item v-else label="关键词"><el-input v-model="ruleForm.guanjianci" readonly></el-input></el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item v-if="type!='info'" label="问题" prop="wenti">
            <el-input type="textarea" :rows="3" v-model="ruleForm.wenti" placeholder="请输入问题内容"></el-input>
          </el-form-item>
          <el-form-item v-else label="问题">
            <el-input type="textarea" :rows="3" v-model="ruleForm.wenti" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item v-if="type!='info'" label="答案" prop="daan">
            <el-input type="textarea" :rows="5" v-model="ruleForm.daan" placeholder="请输入答案内容"></el-input>
          </el-form-item>
          <el-form-item v-else label="答案">
            <el-input type="textarea" :rows="5" v-model="ruleForm.daan" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item v-if="type!='info'" label="排序" prop="paixu">
            <el-input-number v-model="ruleForm.paixu" :min="0" :max="999"></el-input-number>
          </el-form-item>
          <el-form-item v-else label="排序"><el-input v-model="ruleForm.paixu" readonly></el-input></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item v-if="type!='info'" label="状态" prop="zhuangtai">
            <el-radio-group v-model="ruleForm.zhuangtai">
              <el-radio label="启用">启用</el-radio>
              <el-radio label="禁用">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-else label="状态">
            <el-tag :type="ruleForm.zhuangtai=='启用'?'success':'danger'">{{ruleForm.zhuangtai}}</el-tag>
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="type=='info'">
          <el-form-item label="点击量"><el-input v-model="ruleForm.clickcount" readonly></el-input></el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="btn">
        <el-button v-if="type!='info'" type="primary" class="btn-success" @click="onSubmit">提交</el-button>
        <el-button v-if="type!='info'" class="btn-close" @click="back()">取消</el-button>
        <el-button v-if="type=='info'" class="btn-close" @click="back()">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
export default {
  data() {
    return {
      id: '',
      type: '',
      ruleForm: { wentileixing: '', guanjianci: '', wenti: '', daan: '', paixu: 0, zhuangtai: '启用', clickcount: 0 },
      typeOptions: ['客房信息', '价格咨询', '服务咨询', '预订流程', '退订政策', '会员权益', '其他'],
      rules: {
        wentileixing: [{ required: true, message: '问题类型不能为空', trigger: 'change' }],
        guanjianci: [{ required: true, message: '关键词不能为空', trigger: 'blur' }],
        wenti: [{ required: true, message: '问题不能为空', trigger: 'blur' }],
        daan: [{ required: true, message: '答案不能为空', trigger: 'blur' }]
      }
    };
  },
  props: ["parent"],
  methods: {
    init(id, type) {
      if (id) { this.id = id; this.type = type; }
      if(this.type=='info'||this.type=='else') this.info(id);
    },
    info(id) {
      this.$http({ url: `aikefuzhishiku/info/${id}`, method: "get" }).then(({ data }) => {
        if (data && data.code === 0) { this.ruleForm = data.data; }
        else { this.$message.error(data.msg); }
      });
    },
    onSubmit() {
      this.$refs["ruleForm"].validate(valid => {
        if (valid) {
          this.$http({ url: `aikefuzhishiku/${!this.ruleForm.id ? "save" : "update"}`, method: "post", data: this.ruleForm }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({ message: "操作成功", type: "success", duration: 1500, onClose: () => {
                this.parent.showFlag = true;
                this.parent.addOrUpdateFlag = false;
                this.parent.search();
              }});
            } else { this.$message.error(data.msg); }
          });
        }
      });
    },
    back() { this.parent.showFlag = true; this.parent.addOrUpdateFlag = false; this.parent.contentStyleChange(); }
  }
};
</script>
<style lang="scss">
.addEdit-block { margin: -10px; }
.detail-form-content { padding: 12px; }
.btn .el-button { padding: 0 20px; }
</style>
