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
            <el-input v-model="ruleForm.dingdanbianhao" placeholder="订单编号" :readonly="type=='info'"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客房号" prop="kefanghao">
            <el-select v-if="type!='info'" v-model="ruleForm.kefanghao" @change="kefanghaoChange" placeholder="请选择客房号">
              <el-option v-for="item in kefanghaoOptions" :key="item" :label="item" :value="item"></el-option>
            </el-select>
            <el-input v-else v-model="ruleForm.kefanghao" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客房类型" prop="kefangleixing">
            <el-input v-model="ruleForm.kefangleixing" placeholder="客房类型" :readonly="true"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所属酒店" prop="suoshujiudian">
            <el-input v-model="ruleForm.suoshujiudian" placeholder="所属酒店" :readonly="true"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="账号" prop="zhanghao">
            <el-select v-if="type!='info'" v-model="ruleForm.zhanghao" @change="zhanghaoChange" placeholder="请选择账号">
              <el-option v-for="item in zhanghaoOptions" :key="item" :label="item" :value="item"></el-option>
            </el-select>
            <el-input v-else v-model="ruleForm.zhanghao" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="姓名" prop="xingming">
            <el-input v-model="ruleForm.xingming" placeholder="姓名" :readonly="true"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="身份证" prop="shenfenzheng">
            <el-input v-model="ruleForm.shenfenzheng" placeholder="身份证" :readonly="true"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="手机" prop="shouji">
            <el-input v-model="ruleForm.shouji" placeholder="手机" :readonly="true"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客房状态" prop="kefangzhuangtai">
            <el-select v-if="type!='info'" v-model="ruleForm.kefangzhuangtai" placeholder="请选择客房状态">
              <el-option label="已入住" value="已入住"></el-option>
            </el-select>
            <el-input v-else v-model="ruleForm.kefangzhuangtai" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="入住押金" prop="ruzhuyajin">
            <el-input v-model="ruleForm.ruzhuyajin" placeholder="入住押金" :readonly="type=='info'"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="支付方式" prop="zhifufangshi">
            <el-select v-if="type!='info'" v-model="ruleForm.zhifufangshi" placeholder="请选择支付方式">
              <el-option label="微信" value="微信"></el-option>
              <el-option label="支付宝" value="支付宝"></el-option>
              <el-option label="现金" value="现金"></el-option>
              <el-option label="银行卡" value="银行卡"></el-option>
            </el-select>
            <el-input v-else v-model="ruleForm.zhifufangshi" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="入住时间" prop="ruzhushijian">
            <el-date-picker v-if="type!='info'" v-model="ruleForm.ruzhushijian" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择入住时间"></el-date-picker>
            <el-input v-else v-model="ruleForm.ruzhushijian" readonly></el-input>
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
        kefangzhuangtai: '已入住',
        ruzhuyajin: '',
        zhifufangshi: '',
        ruzhushijian: '',
        ispay: '未支付'
      },
      rules: {
        kefanghao: [{ required: true, message: '请选择客房号', trigger: 'change' }],
        zhanghao: [{ required: true, message: '请选择账号', trigger: 'change' }]
      },
      type: '',
      kefanghaoOptions: [],
      zhanghaoOptions: [],
      kefangList: [],
      yonghuList: []
    };
  },
  props: ['parent'],
  methods: {
    init(id, type) {
      this.type = type;
      this.ruleForm.dingdanbianhao = 'DD' + new Date().getTime();
      this.getKefangList();
      this.getYonghuList();
      if (id) {
        this.$http({
          url: `yonghuruzhu/info/${id}`,
          method: 'get'
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.ruleForm = data.data;
          }
        });
      }
      let crossObj = this.$storage.get('crossObj');
      let crossTable = this.$storage.get('crossTable');
      if (crossObj && crossTable) {
        if (crossTable === 'kefangxinxi') {
          this.ruleForm.kefanghao = crossObj.kefanghao;
          this.ruleForm.kefangleixing = crossObj.kefangleixing;
          this.ruleForm.suoshujiudian = crossObj.suoshujiudian;
        }
        this.$storage.remove('crossObj');
        this.$storage.remove('crossTable');
      }
    },
    getKefangList() {
      this.$http({
        url: 'kefangxinxi/page',
        method: 'get',
        params: { page: 1, limit: 999, kefangzhuangtai: '空闲' }
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.kefangList = data.data.list;
          this.kefanghaoOptions = data.data.list.map(item => item.kefanghao);
        }
      });
    },
    getYonghuList() {
      this.$http({
        url: 'yonghu/page',
        method: 'get',
        params: { page: 1, limit: 999 }
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.yonghuList = data.data.list;
          this.zhanghaoOptions = data.data.list.map(item => item.zhanghao);
        }
      });
    },
    kefanghaoChange(val) {
      let kefang = this.kefangList.find(item => item.kefanghao === val);
      if (kefang) {
        this.ruleForm.kefangleixing = kefang.kefangleixing;
        this.ruleForm.suoshujiudian = kefang.suoshujiudian;
      }
    },
    zhanghaoChange(val) {
      let yonghu = this.yonghuList.find(item => item.zhanghao === val);
      if (yonghu) {
        this.ruleForm.xingming = yonghu.xingming;
        this.ruleForm.shenfenzheng = yonghu.shenfenzheng;
        this.ruleForm.shouji = yonghu.shouji;
      }
    },
    onSubmit() {
      this.$refs['ruleForm'].validate(valid => {
        if (valid) {
          this.$http({
            url: `yonghuruzhu/${!this.ruleForm.id ? 'save' : 'update'}`,
            method: 'post',
            data: this.ruleForm
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.parent.showFlag = true;
                  this.parent.addOrUpdateFlag = false;
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
