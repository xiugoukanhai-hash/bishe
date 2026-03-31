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
          <el-form-item label="订单编号" prop="yuyuebianhao">
            <el-input v-model="ruleForm.yuyuebianhao" placeholder="订单编号" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客房号" prop="kefanghao">
            <el-input v-model="ruleForm.kefanghao" placeholder="客房号" :readonly="type=='info'"></el-input>
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
          <el-form-item label="入住天数" prop="tianshu">
            <el-input v-model="ruleForm.tianshu" placeholder="入住天数" :readonly="type=='info'"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="入住时间" prop="ruzhushijian">
            <el-date-picker v-if="type!='info'" v-model="ruleForm.ruzhushijian" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择入住时间"></el-date-picker>
            <el-input v-else v-model="ruleForm.ruzhushijian" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="单价" prop="jiage">
            <el-input v-model="ruleForm.jiage" placeholder="单价" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="总价" prop="zongjia">
            <el-input v-model="ruleForm.zongjia" placeholder="总价" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="预约时间" prop="yuyueshijian">
            <el-input v-model="ruleForm.yuyueshijian" placeholder="预约时间" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="预约状态" prop="yuyuezhuangtai">
            <el-input v-model="ruleForm.yuyuezhuangtai" placeholder="预约状态" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="支付状态" prop="ispay">
            <el-input v-model="ruleForm.ispay" placeholder="支付状态" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="审核状态" prop="sfsh">
            <el-input v-model="ruleForm.sfsh" placeholder="审核状态" readonly></el-input>
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
        yuyuebianhao: '',
        kefanghao: '',
        kefangleixing: '',
        suoshujiudian: '',
        zhanghao: '',
        xingming: '',
        shenfenzheng: '',
        shouji: '',
        tianshu: '',
        ruzhushijian: '',
        jiage: '',
        zongjia: '',
        yuyueshijian: '',
        yuyuezhuangtai: '',
        sfsh: '待审核',
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
      this.ruleForm.yuyuebianhao = 'YY' + new Date().getTime();
      let now = new Date();
      let y = now.getFullYear();
      let m = (now.getMonth() + 1).toString().padStart(2, '0');
      let d = now.getDate().toString().padStart(2, '0');
      let h = now.getHours().toString().padStart(2, '0');
      let min = now.getMinutes().toString().padStart(2, '0');
      let s = now.getSeconds().toString().padStart(2, '0');
      this.ruleForm.yuyueshijian = `${y}-${m}-${d} ${h}:${min}:${s}`;
      
      if (id) {
        this.$http({
          url: `huiyuanyuyue/info/${id}`,
          method: 'get'
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.ruleForm = data.data;
            // 通过客房号查询客房信息获取客房类型和所属酒店
            if (data.data.kefanghao) {
              this.$http({
                url: 'kefangxinxi/list',
                method: 'get',
                params: { kefanghao: data.data.kefanghao }
              }).then(({ data: kfData }) => {
                if (kfData && kfData.code === 0 && kfData.data.list && kfData.data.list.length > 0) {
                  this.$set(this.ruleForm, 'kefangleixing', kfData.data.list[0].kefangleixing);
                  this.$set(this.ruleForm, 'suoshujiudian', kfData.data.list[0].suoshujiudian);
                }
              });
            }
          }
        });
      }
      
      let crossObj = this.$storage.get('crossObj');
      let crossTable = this.$storage.get('crossTable');
      if (crossObj && crossTable === 'kefangxinxi') {
        this.ruleForm.kefanghao = crossObj.kefanghao;
        this.ruleForm.kefangleixing = crossObj.kefangleixing;
        this.ruleForm.suoshujiudian = crossObj.suoshujiudian;
        this.$storage.remove('crossObj');
        this.$storage.remove('crossTable');
      }
      
      let sessionTable = this.$storage.get('sessionTable');
      if (sessionTable === 'huiyuan') {
        this.$http({
          url: 'huiyuan/session',
          method: 'get'
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.ruleForm.zhanghao = data.data.zhanghao;
            this.ruleForm.xingming = data.data.xingming;
            this.ruleForm.shenfenzheng = data.data.shenfenzheng;
            this.ruleForm.shouji = data.data.shouji;
          }
        });
      }
    },
    onSubmit() {
      this.$refs['ruleForm'].validate(valid => {
        if (valid) {
          this.$http({
            url: `huiyuanyuyue/${!this.ruleForm.id ? 'save' : 'update'}`,
            method: 'post',
            data: this.ruleForm
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: '预约成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.parent.showFlag = true;
                  this.parent.addOrUpdateFlag = false;
                  if(this.parent.huiyuanyuyueCrossAddOrUpdateFlag !== undefined) {
                    this.parent.huiyuanyuyueCrossAddOrUpdateFlag = false;
                  }
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
      if(this.parent.huiyuanyuyueCrossAddOrUpdateFlag !== undefined) {
        this.parent.huiyuanyuyueCrossAddOrUpdateFlag = false;
      }
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
