<template>
  <div class="addEdit-block">
    <el-form class="detail-form-content" ref="ruleForm" :model="ruleForm" label-width="100px">
      <el-row>
        <el-col :span="12">
          <el-form-item label="会员账号"><el-input v-model="ruleForm.zhanghao" readonly></el-input></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="积分类型">
            <el-tag :type="ruleForm.jifenshu>0?'success':'danger'">{{ruleForm.jifenleixing}}</el-tag>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="积分数">
            <span :style="{color: ruleForm.jifenshu>0?'#67c23a':'#f56c6c', fontWeight:'bold', fontSize:'18px'}">
              {{ruleForm.jifenshu>0?'+':''}}{{ruleForm.jifenshu}}
            </span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="积分余额">
            <span style="font-weight:bold;color:#409eff;fontSize:18px">{{ruleForm.yue}}</span>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="说明">
            <el-input type="textarea" :rows="2" v-model="ruleForm.shuoming" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="ruleForm.guanliandingdan">
          <el-form-item label="关联订单"><el-input v-model="ruleForm.guanliandingdan" readonly></el-input></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="操作人"><el-input v-model="ruleForm.caozuoren" readonly></el-input></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="操作时间"><el-input v-model="ruleForm.addtime" readonly></el-input></el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="btn">
        <el-button class="btn-close" @click="back()">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
export default {
  data() {
    return {
      ruleForm: { zhanghao: '', jifenleixing: '', jifenshu: 0, yue: 0, shuoming: '', guanliandingdan: '', caozuoren: '', addtime: '' }
    };
  },
  props: ["parent"],
  methods: {
    init(id, type) {
      if (id) this.info(id);
    },
    info(id) {
      this.$http({ url: `jifen/info/${id}`, method: "get" }).then(({ data }) => {
        if (data && data.code === 0) { this.ruleForm = data.data; }
        else { this.$message.error(data.msg); }
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
