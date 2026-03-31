<template>
  <div class="addEdit-block">
    <el-form class="detail-form-content" ref="ruleForm" :model="ruleForm" :rules="rules" label-width="100px">
      <el-row>
        <el-col :span="12">
          <el-form-item v-if="type!='info'" label="客房号" prop="kefanghao">
            <el-input v-model="ruleForm.kefanghao" placeholder="客房号" clearable></el-input>
          </el-form-item>
          <el-form-item v-else label="客房号"><el-input v-model="ruleForm.kefanghao" readonly></el-input></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item v-if="type!='info'" label="服务类型" prop="fuwuleixing">
            <el-select v-model="ruleForm.fuwuleixing" placeholder="请选择类型">
              <el-option v-for="item in typeOptions" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item v-else label="服务类型"><el-input v-model="ruleForm.fuwuleixing" readonly></el-input></el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item v-if="type!='info'" label="服务详情" prop="fuwuxiangqing">
            <el-input type="textarea" :rows="3" v-model="ruleForm.fuwuxiangqing" placeholder="请输入服务详情"></el-input>
          </el-form-item>
          <el-form-item v-else label="服务详情">
            <el-input type="textarea" :rows="3" v-model="ruleForm.fuwuxiangqing" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item v-if="type!='info'" label="服务费用" prop="fuwufeiyong">
            <el-input-number v-model="ruleForm.fuwufeiyong" :min="0" :precision="2"></el-input-number>
          </el-form-item>
          <el-form-item v-else label="服务费用"><el-input :value="ruleForm.fuwufeiyong?'￥'+ruleForm.fuwufeiyong:'免费'" readonly></el-input></el-form-item>
        </el-col>
        <el-col :span="12" v-if="type=='info'">
          <el-form-item label="状态">
            <el-tag :type="getStatusType(ruleForm.zhuangtai)">{{ruleForm.zhuangtai}}</el-tag>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item v-if="type!='info'" label="备注" prop="beizhu">
            <el-input type="textarea" :rows="2" v-model="ruleForm.beizhu" placeholder="备注信息"></el-input>
          </el-form-item>
          <el-form-item v-else-if="ruleForm.beizhu" label="备注">
            <el-input type="textarea" :rows="2" v-model="ruleForm.beizhu" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="type=='info'">
          <el-form-item label="登记人"><el-input v-model="ruleForm.dengjirenzhanghao" readonly></el-input></el-form-item>
        </el-col>
        <el-col :span="12" v-if="type=='info'">
          <el-form-item label="登记时间"><el-input v-model="ruleForm.dengjishijian" readonly></el-input></el-form-item>
        </el-col>
        <el-col :span="12" v-if="type=='info' && ruleForm.chulirenzhanghao">
          <el-form-item label="处理人"><el-input v-model="ruleForm.chulirenzhanghao" readonly></el-input></el-form-item>
        </el-col>
        <el-col :span="12" v-if="type=='info' && ruleForm.chulishijian">
          <el-form-item label="处理时间"><el-input v-model="ruleForm.chulishijian" readonly></el-input></el-form-item>
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
      ruleForm: { kefanghao: '', fuwuleixing: '', fuwuxiangqing: '', fuwufeiyong: 0, beizhu: '', zhuangtai: '待处理' },
      typeOptions: ['加床服务', '早餐预订', '客房清洁', '洗衣服务', '叫醒服务', '行李寄存', '维修服务', '其他'],
      rules: {
        kefanghao: [{ required: true, message: '客房号不能为空', trigger: 'blur' }],
        fuwuleixing: [{ required: true, message: '服务类型不能为空', trigger: 'change' }],
        fuwuxiangqing: [{ required: true, message: '服务详情不能为空', trigger: 'blur' }]
      }
    };
  },
  props: ["parent"],
  methods: {
    getStatusType(status) {
      const map = { '待处理': 'warning', '处理中': 'primary', '已完成': 'success', '已取消': 'info' };
      return map[status] || '';
    },
    init(id, type) {
      if (id) { this.id = id; this.type = type; }
      if(this.type=='info'||this.type=='else') this.info(id);
    },
    info(id) {
      this.$http({ url: `fuwujilu/info/${id}`, method: "get" }).then(({ data }) => {
        if (data && data.code === 0) { this.ruleForm = data.data; }
        else { this.$message.error(data.msg); }
      });
    },
    onSubmit() {
      this.$refs["ruleForm"].validate(valid => {
        if (valid) {
          this.$http({ url: `fuwujilu/${!this.ruleForm.id ? "save" : "update"}`, method: "post", data: this.ruleForm }).then(({ data }) => {
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
