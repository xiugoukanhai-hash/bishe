<template>
  <div>
    <div class="container">
      <div class="login-form" style="backgroundColor:rgba(255, 255, 255, 0.17);borderRadius:10px">
        <h1 class="h1" style="color:rgba(36, 194, 205, 1);fontSize:28px;">酒店客房管理系统</h1>
        <el-form ref="rgsForm" class="rgs-form" :model="ruleForm" label-width="120px">
          <el-form-item label="账号" class="input" v-if="tableName=='yonghu'">
            <el-input v-model="ruleForm.zhanghao" autocomplete="off" placeholder="账号" />
          </el-form-item>
          <el-form-item label="密码" class="input" v-if="tableName=='yonghu'">
            <el-input v-model="ruleForm.mima" autocomplete="off" placeholder="密码" type="password" />
          </el-form-item>
          <el-form-item label="姓名" class="input" v-if="tableName=='yonghu'">
            <el-input v-model="ruleForm.xingming" autocomplete="off" placeholder="姓名" />
          </el-form-item>
          <el-form-item label="年龄" class="input" v-if="tableName=='yonghu'">
            <el-input v-model="ruleForm.nianling" autocomplete="off" placeholder="年龄" />
          </el-form-item>
          <el-form-item label="手机" class="input" v-if="tableName=='yonghu'">
            <el-input v-model="ruleForm.shouji" autocomplete="off" placeholder="手机" />
          </el-form-item>
          <el-form-item label="身份证" class="input" v-if="tableName=='yonghu'">
            <el-input v-model="ruleForm.shenfenzheng" autocomplete="off" placeholder="身份证" />
          </el-form-item>
          <el-form-item label="账号" class="input" v-if="tableName=='huiyuan'">
            <el-input v-model="ruleForm.zhanghao" autocomplete="off" placeholder="账号" />
          </el-form-item>
          <el-form-item label="密码" class="input" v-if="tableName=='huiyuan'">
            <el-input v-model="ruleForm.mima" autocomplete="off" placeholder="密码" type="password" />
          </el-form-item>
          <el-form-item label="姓名" class="input" v-if="tableName=='huiyuan'">
            <el-input v-model="ruleForm.xingming" autocomplete="off" placeholder="姓名" />
          </el-form-item>
          <el-form-item label="年龄" class="input" v-if="tableName=='huiyuan'">
            <el-input v-model="ruleForm.nianling" autocomplete="off" placeholder="年龄" />
          </el-form-item>
          <el-form-item label="手机" class="input" v-if="tableName=='huiyuan'">
            <el-input v-model="ruleForm.shouji" autocomplete="off" placeholder="手机" />
          </el-form-item>
          <el-form-item label="身份证" class="input" v-if="tableName=='huiyuan'">
            <el-input v-model="ruleForm.shenfenzheng" autocomplete="off" placeholder="身份证" />
          </el-form-item>
          <el-form-item label="清洁账号" class="input" v-if="tableName=='qingjierenyuan'">
            <el-input v-model="ruleForm.qingjiezhanghao" autocomplete="off" placeholder="清洁账号" />
          </el-form-item>
          <el-form-item label="密码" class="input" v-if="tableName=='qingjierenyuan'">
            <el-input v-model="ruleForm.mima" autocomplete="off" placeholder="密码" type="password" />
          </el-form-item>
          <el-form-item label="清洁姓名" class="input" v-if="tableName=='qingjierenyuan'">
            <el-input v-model="ruleForm.qingjiexingming" autocomplete="off" placeholder="清洁姓名" />
          </el-form-item>
          <el-form-item label="年龄" class="input" v-if="tableName=='qingjierenyuan'">
            <el-input v-model="ruleForm.nianling" autocomplete="off" placeholder="年龄" />
          </el-form-item>
          <el-form-item label="手机" class="input" v-if="tableName=='qingjierenyuan'">
            <el-input v-model="ruleForm.shouji" autocomplete="off" placeholder="手机" />
          </el-form-item>
          <div style="display: flex;flex-wrap: wrap;width: 100%;justify-content: center;">
            <el-button class="btn" type="primary" @click="login()">注册</el-button>
            <el-button class="btn close" type="primary" @click="close()">返回</el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      ruleForm: {},
      tableName: "",
      rules: {}
    };
  },
  mounted() {
    let table = this.$storage.get("loginTable");
    this.tableName = table;
  },
  methods: {
    getUUID() {
      return new Date().getTime();
    },
    close() {
      this.$router.push({ path: "/login" });
    },
    login() {
      if ((!this.ruleForm.zhanghao) && `yonghu` == this.tableName) {
        this.$message.error(`请填写账号`);
        return;
      }
      if ((!this.ruleForm.mima) && `yonghu` == this.tableName) {
        this.$message.error(`请填写密码`);
        return;
      }
      if (`yonghu` == this.tableName && this.ruleForm.shouji && (!this.$validate.isMobile(this.ruleForm.shouji))) {
        this.$message.error(`手机格式不正确`);
        return;
      }
      if (`yonghu` == this.tableName && this.ruleForm.shenfenzheng && (!this.$validate.checkIdCard(this.ruleForm.shenfenzheng))) {
        this.$message.error(`身份证号格式不正确`);
        return;
      }
      if ((!this.ruleForm.zhanghao) && `huiyuan` == this.tableName) {
        this.$message.error(`请填写账号`);
        return;
      }
      if ((!this.ruleForm.mima) && `huiyuan` == this.tableName) {
        this.$message.error(`请填写密码`);
        return;
      }
      if (`huiyuan` == this.tableName && this.ruleForm.shouji && (!this.$validate.isMobile(this.ruleForm.shouji))) {
        this.$message.error(`手机格式不正确`);
        return;
      }
      if (`huiyuan` == this.tableName && this.ruleForm.shenfenzheng && (!this.$validate.checkIdCard(this.ruleForm.shenfenzheng))) {
        this.$message.error(`身份证号格式不正确`);
        return;
      }
      if ((!this.ruleForm.qingjiezhanghao) && `qingjierenyuan` == this.tableName) {
        this.$message.error(`请填写清洁账号`);
        return;
      }
      if ((!this.ruleForm.mima) && `qingjierenyuan` == this.tableName) {
        this.$message.error(`请填写密码`);
        return;
      }
      if (`qingjierenyuan` == this.tableName && this.ruleForm.shouji && (!this.$validate.isMobile(this.ruleForm.shouji))) {
        this.$message.error(`手机格式不正确`);
        return;
      }
      this.$http({
        url: `${this.tableName}/register`,
        method: "post",
        data: this.ruleForm
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.$message({
            message: "注册成功",
            type: "success",
            duration: 1500,
            onClose: () => {
              this.$router.replace({ path: "/login" });
            }
          });
        } else {
          this.$message.error(data.msg);
        }
      });
    }
  }
};
</script>
<style lang="scss" scoped>
.el-radio__input.is-checked .el-radio__inner {
  border-color: #00c292;
  background: #00c292;
}

.el-radio__input.is-checked + .el-radio__label {
  color: #00c292;
}

.h1 {
  margin-top: 10px;
}

body {
  padding: 0;
  margin: 0;
}

.nk-navigation {
  margin-top: 15px;

  a {
    display: inline-block;
    color: #fff;
    background: rgba(255, 255, 255, 0.2);
    width: 100px;
    height: 50px;
    border-radius: 30px;
    text-align: center;
    display: flex;
    align-items: center;
    margin: 0 auto;
    justify-content: center;
    padding: 0 20px;
  }

  .icon {
    margin-left: 10px;
    width: 30px;
    height: 30px;
  }
}

.register-container {
  margin-top: 10px;

  a {
    display: inline-block;
    color: #fff;
    max-width: 500px;
    height: 50px;
    border-radius: 30px;
    text-align: center;
    display: flex;
    align-items: center;
    margin: 0 auto;
    justify-content: center;
    padding: 0 20px;

    div {
      margin-left: 10px;
    }
  }
}

.container {
  background-image: url("http://codegen.caihongy.cn/20210417/19f1c259feef4fbd9854f8ed3c50a2ef.jpg");
  height: 100vh;
  background-position: center center;
  background-size: cover;
  background-repeat: no-repeat;

  .login-form {
    right: 50%;
    top: 50%;
    height: auto;
    transform: translate3d(50%, -50%, 0);
    border-radius: 10px;
    background-color: rgba(255, 255, 255, 0.5);
    width: 420px;
    padding: 30px 30px 40px 30px;
    font-size: 14px;
    font-weight: 500;

    .h1 {
      margin: 0;
      text-align: center;
      line-height: 54px;
      font-size: 24px;
      color: #000;
    }

    .rgs-form {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;

      .input {
        width: 100%;

        &::v-deep .el-form-item__label {
          line-height: 40px;
          color: rgba(0, 0, 0, 1);
          font-size: 14px;
        }

        &::v-deep .el-input__inner {
          height: 40px;
          color: #606266;
          font-size: 14px;
          border-width: 1px;
          border-style: solid;
          border-color: #606266;
          border-radius: 4px;
          background-color: #fff;
        }
      }

      .btn {
        margin: 0 10px;
        width: 88px;
        height: 44px;
        color: #fff;
        font-size: 14px;
        border-width: 1px;
        border-style: solid;
        border-color: rgba(36, 194, 205, 1);
        border-radius: 4px;
        background-color: rgba(36, 194, 205, 1);
      }

      .close {
        margin: 0 10px;
        width: 88px;
        height: 44px;
        color: #409eff;
        font-size: 14px;
        border-width: 1px;
        border-style: solid;
        border-color: #409eff;
        border-radius: 5px;
        background-color: #fff;
      }
    }
  }
}
</style>
