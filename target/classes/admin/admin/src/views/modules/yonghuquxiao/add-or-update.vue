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
          <el-form-item label="订单编号">
            <el-input v-model="ruleForm.dingdanbianhao" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客房号">
            <el-input v-model="ruleForm.kefanghao" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客房类型">
            <el-input v-model="ruleForm.kefangleixing" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所属酒店">
            <el-input v-model="ruleForm.suoshujiudian" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="账号">
            <el-input v-model="ruleForm.zhanghao" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="姓名">
            <el-input v-model="ruleForm.xingming" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="取消原因" prop="quxiaoyuanyin">
            <el-input v-model="ruleForm.quxiaoyuanyin" :readonly="type=='info'" placeholder="请输入取消原因"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="取消时间">
            <el-input v-model="ruleForm.quxiaoshijian" readonly></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="btn">
        <el-button @click="back()">返回</el-button>
        <el-button v-if="type!='info'" type="primary" @click="submitForm()">确认取消预约</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
export default {
  data() {
    return {
      ruleForm: {},
      type: '',
      crossTable: '',
      rules: {
        quxiaoyuanyin: [
          { required: true, message: '请输入取消原因', trigger: 'blur' }
        ]
      }
    };
  },
  props: ['parent'],
  methods: {
    init(id, type) {
      this.type = type;
      this.crossTable = this.$storage.get('crossTable') || '';
      
      if (type === 'cross') {
        // 跨表操作：从预约记录创建取消记录
        let crossObj = this.$storage.getObj('crossObj');
        if (crossObj) {
          // 获取当前时间
          let now = new Date();
          let quxiaoshijian = now.getFullYear() + '-' + 
            String(now.getMonth() + 1).padStart(2, '0') + '-' + 
            String(now.getDate()).padStart(2, '0') + ' ' + 
            String(now.getHours()).padStart(2, '0') + ':' + 
            String(now.getMinutes()).padStart(2, '0') + ':' + 
            String(now.getSeconds()).padStart(2, '0');
          
          this.ruleForm = {
            dingdanbianhao: crossObj.yuyuebianhao || '',
            yuyuebianhao: crossObj.yuyuebianhao || '',
            kefanghao: crossObj.kefanghao || '',
            kefangleixing: '',
            suoshujiudian: '',
            zhanghao: crossObj.zhanghao || '',
            xingming: crossObj.xingming || '',
            quxiaoyuanyin: '',
            quxiaoshijian: quxiaoshijian
          };
          
          // 通过客房号查询客房信息获取客房类型和所属酒店
          if (crossObj.kefanghao) {
            this.$http({
              url: 'kefangxinxi/list',
              method: 'get',
              params: { kefanghao: crossObj.kefanghao }
            }).then(({ data }) => {
              if (data && data.code === 0 && data.data.list && data.data.list.length > 0) {
                this.ruleForm.kefangleixing = data.data.list[0].kefangleixing;
                this.ruleForm.suoshujiudian = data.data.list[0].suoshujiudian;
              }
            });
          }
        }
      } else if (id) {
        // 查看已有的取消记录
        this.$http({
          url: `yonghuquxiao/info/${id}`,
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
                  this.ruleForm.kefangleixing = kfData.data.list[0].kefangleixing;
                  this.ruleForm.suoshujiudian = kfData.data.list[0].suoshujiudian;
                }
              });
            }
          }
        });
      }
    },
    submitForm() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.$confirm('确定要取消该预约吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.$http({
              url: 'yonghuquxiao/save',
              method: 'post',
              data: this.ruleForm
            }).then(({ data }) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '取消预约成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.back();
                    this.parent.getDataList();
                  }
                });
              } else {
                this.$message.error(data.msg || '操作失败');
              }
            });
          });
        }
      });
    },
    back() {
      this.parent.showFlag = true;
      this.parent.yonghuquxiaoCrossAddOrUpdateFlag = false;
      this.parent.addOrUpdateFlag = false;
    }
  }
};
</script>
<style lang="scss" scoped>
.addEdit-block {
  padding: 20px;
}
.btn {
  text-align: center;
  margin-top: 20px;
}
</style>
