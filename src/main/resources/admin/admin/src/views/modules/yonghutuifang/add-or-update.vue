<template>
  <div class="addEdit-block">
    <el-form
      class="detail-form-content"
      ref="ruleForm"
      :model="ruleForm"
      :rules="rules"
      label-width="100px"
    >
      <el-row>
        <el-col :span="12">
          <el-form-item label="订单编号" prop="dingdanbianhao">
            <el-input v-model="ruleForm.dingdanbianhao" placeholder="订单编号" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客房号" prop="kefanghao">
            <el-input v-model="ruleForm.kefanghao" placeholder="客房号" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客房类型" prop="kefangleixing">
            <el-input v-model="ruleForm.kefangleixing" placeholder="客房类型" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所属酒店" prop="suoshujiudian">
            <el-input v-model="ruleForm.suoshujiudian" placeholder="所属酒店" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="账号" prop="zhanghao">
            <el-input v-model="ruleForm.zhanghao" placeholder="账号" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="姓名" prop="xingming">
            <el-input v-model="ruleForm.xingming" placeholder="姓名" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="身份证" prop="shenfenzheng">
            <el-input v-model="ruleForm.shenfenzheng" placeholder="身份证" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="手机" prop="shouji">
            <el-input v-model="ruleForm.shouji" placeholder="手机" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="入住押金" prop="ruzhuyajin">
            <el-input v-model="ruleForm.ruzhuyajin" placeholder="入住押金" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="退房时间" prop="tuifangshijian">
            <el-date-picker v-if="type!='info'" v-model="ruleForm.tuifangshijian" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择退房时间"></el-date-picker>
            <el-input v-else v-model="ruleForm.tuifangshijian" readonly></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="btn">
        <el-button v-if="type!='info'" type="primary" @click="onSubmit">提交</el-button>
        <el-button @click="back()">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
export default {
  data() {
    return {
      ruleForm: {
        dingdanbianhao: '',
        kefanghao: '',
        kefangleixing: '',
        suoshujiudian: '',
        zhanghao: '',
        xingming: '',
        shenfenzheng: '',
        shouji: '',
        ruzhuyajin: '',
        tuifangshijian: '',
        ispay: '未支付'
      },
      rules: {},
      type: ''
    };
  },
  props: ['parent'],
  methods: {
    init(id, type) {
      this.type = type;
      if (id) {
        this.$http({
          url: `yonghutuifang/info/${id}`,
          method: 'get'
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.ruleForm = data.data;
          }
        });
      }
      let crossObj = this.$storage.get('crossObj');
      let crossTable = this.$storage.get('crossTable');
      if (crossObj && (crossTable === 'yonghuruzhu' || type === 'cross')) {
        this.ruleForm.dingdanbianhao = crossObj.dingdanbianhao;
        this.ruleForm.kefanghao = crossObj.kefanghao;
        this.ruleForm.kefangleixing = crossObj.kefangleixing;
        this.ruleForm.suoshujiudian = crossObj.suoshujiudian;
        this.ruleForm.zhanghao = crossObj.zhanghao;
        this.ruleForm.xingming = crossObj.xingming;
        this.ruleForm.shenfenzheng = crossObj.shenfenzheng;
        this.ruleForm.shouji = crossObj.shouji;
        this.ruleForm.ruzhuyajin = crossObj.ruzhuyajin;
        let now = new Date();
        let y = now.getFullYear();
        let m = (now.getMonth() + 1).toString().padStart(2, '0');
        let d = now.getDate().toString().padStart(2, '0');
        let h = now.getHours().toString().padStart(2, '0');
        let min = now.getMinutes().toString().padStart(2, '0');
        let s = now.getSeconds().toString().padStart(2, '0');
        this.ruleForm.tuifangshijian = `${y}-${m}-${d} ${h}:${min}:${s}`;
        this.$storage.remove('crossObj');
        this.$storage.remove('crossTable');
      }
    },
    onSubmit() {
      this.$refs['ruleForm'].validate(valid => {
        if (valid) {
          this.$http({
            url: `yonghutuifang/${!this.ruleForm.id ? 'save' : 'update'}`,
            method: 'post',
            data: this.ruleForm
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: '退房成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.parent.showFlag = true;
                  this.parent.addOrUpdateFlag = false;
                  this.parent.yonghutuifangCrossAddOrUpdateFlag = false;
                  this.parent.search();
                }
              });
            } else {
              this.$message.error(data.msg);
            }
          });
        }
      });
    },
    back() {
      this.parent.showFlag = true;
      this.parent.addOrUpdateFlag = false;
      this.parent.yonghutuifangCrossAddOrUpdateFlag = false;
    }
  }
};
</script>
<style lang="scss" scoped>
.addEdit-block {
  padding: 20px;
}
.el-select {
  width: 100%;
}
.btn {
  text-align: center;
  margin-top: 20px;
}
</style>
