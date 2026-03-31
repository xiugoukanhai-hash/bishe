<template>
  <div class="addEdit-block">
    <el-form class="detail-form-content" ref="ruleForm" :model="ruleForm" :rules="rules" label-width="100px" :style="{backgroundColor:addEditForm.addEditBoxColor}">
      <el-row>
        <el-col :span="12">
          <el-form-item class="input" v-if="type!='info'" label="前台账号" prop="qiantaizhanghao">
            <el-input v-model="ruleForm.qiantaizhanghao" placeholder="前台账号" clearable :disabled="!!ruleForm.id"></el-input>
            <div v-if="ruleForm.id" class="form-tip">账号创建后不可修改</div>
          </el-form-item>
          <div v-else>
            <el-form-item class="input" label="前台账号" prop="qiantaizhanghao">
              <el-input v-model="ruleForm.qiantaizhanghao" placeholder="前台账号" readonly></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="12">
          <el-form-item class="input" v-if="type!='info'" label="密码" prop="mima">
            <el-input v-model="ruleForm.mima" :type="showPassword?'text':'password'" placeholder="密码" clearable>
              <i slot="suffix" class="el-icon-view" @click="showPassword=!showPassword" style="cursor:pointer"></i>
            </el-input>
            <div v-if="!ruleForm.id" class="form-tip">默认密码：123456，建议登录后修改</div>
          </el-form-item>
          <div v-else>
            <el-form-item class="input" label="密码" prop="mima">
              <el-input value="******" placeholder="密码" readonly></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="12">
          <el-form-item class="input" v-if="type!='info'" label="前台姓名" prop="qiantaixingming">
            <el-input v-model="ruleForm.qiantaixingming" placeholder="前台姓名" clearable></el-input>
          </el-form-item>
          <div v-else>
            <el-form-item class="input" label="前台姓名" prop="qiantaixingming">
              <el-input v-model="ruleForm.qiantaixingming" placeholder="前台姓名" readonly></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="12">
          <el-form-item class="select" v-if="type!='info'" label="性别" prop="xingbie">
            <el-select v-model="ruleForm.xingbie" placeholder="请选择性别">
              <el-option v-for="(item,index) in xingbieOptions" :key="index" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <div v-else>
            <el-form-item class="input" label="性别" prop="xingbie">
              <el-input v-model="ruleForm.xingbie" placeholder="性别" readonly></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="12">
          <el-form-item class="input" v-if="type!='info'" label="手机" prop="shouji">
            <el-input v-model="ruleForm.shouji" placeholder="手机" clearable></el-input>
          </el-form-item>
          <div v-else>
            <el-form-item class="input" label="手机" prop="shouji">
              <el-input v-model="ruleForm.shouji" placeholder="手机" readonly></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="12">
          <el-form-item class="date" v-if="type!='info'" label="入职时间" prop="ruzhishijian">
            <el-date-picker v-model="ruleForm.ruzhishijian" value-format="yyyy-MM-dd" type="date" placeholder="选择入职时间"></el-date-picker>
          </el-form-item>
          <div v-else>
            <el-form-item class="input" label="入职时间" prop="ruzhishijian">
              <el-input v-model="ruleForm.ruzhishijian" placeholder="入职时间" readonly></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="12">
          <el-form-item class="select" v-if="type!='info'" label="状态" prop="zhuangtai">
            <el-select v-model="ruleForm.zhuangtai" placeholder="请选择状态">
              <el-option v-for="(item,index) in zhuangtaiOptions" :key="index" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <div v-else>
            <el-form-item class="input" label="状态" prop="zhuangtai">
              <el-input v-model="ruleForm.zhuangtai" placeholder="状态" readonly></el-input>
            </el-form-item>
          </div>
        </el-col>
        <el-col :span="24">
          <el-form-item class="upload" v-if="type!='info' && !ro.zhaopian" label="照片" prop="zhaopian">
            <file-upload tip="点击上传照片" action="file/upload" :limit="3" :multiple="true" :fileUrls="ruleForm.zhaopian?ruleForm.zhaopian:''" @change="zhaopianUploadChange"></file-upload>
          </el-form-item>
          <div v-else>
            <el-form-item v-if="ruleForm.zhaopian" label="照片" prop="zhaopian">
              <img style="margin-right:20px;" v-for="(item,index) in ruleForm.zhaopian.split(',')" :key="index" :src="item" width="100" height="100">
            </el-form-item>
          </div>
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
import { isMobile } from "@/utils/validate";
export default {
  data() {
    var validateMobile = (rule, value, callback) => {
      if(!value) callback();
      else if (!isMobile(value)) callback(new Error("请输入正确的手机号码"));
      else callback();
    };
    return {
      addEditForm: {"btnSaveFontColor":"#fff","selectFontSize":"14px","btnCancelBorderColor":"#DCDFE6","inputBorderRadius":"4px","inputFontSize":"14px","textareaBgColor":"#fff","btnSaveFontSize":"14px","textareaBorderRadius":"4px","uploadBgColor":"#fff","textareaBorderStyle":"solid","btnCancelWidth":"88px","textareaHeight":"120px","dateBgColor":"#fff","btnSaveBorderRadius":"4px","uploadLableFontSize":"14px","textareaBorderWidth":"1px","inputLableColor":"#606266","addEditBoxColor":"#fff","dateIconFontSize":"14px","btnSaveBgColor":"rgba(36, 194, 205, 1)","uploadIconFontColor":"#8c939d","textareaBorderColor":"#DCDFE6","btnCancelBgColor":"#ecf5ff","selectLableColor":"#606266","btnSaveBorderStyle":"solid","dateBorderWidth":"1px","dateLableFontSize":"14px","dateBorderRadius":"4px","btnCancelBorderStyle":"solid","selectLableFontSize":"14px","selectBorderStyle":"solid","selectIconFontColor":"#C0C4CC","btnCancelHeight":"44px","inputHeight":"40px","btnCancelFontColor":"#606266","dateBorderColor":"#DCDFE6","dateIconFontColor":"#C0C4CC","uploadBorderStyle":"solid","dateBorderStyle":"solid","dateLableColor":"#606266","dateFontSize":"14px","inputBorderWidth":"1px","uploadIconFontSize":"28px","selectHeight":"40px","inputFontColor":"#606266","uploadHeight":"148px","textareaLableColor":"#606266","textareaLableFontSize":"14px","btnCancelFontSize":"14px","inputBorderStyle":"solid","btnCancelBorderRadius":"4px","inputBgColor":"#fff","inputLableFontSize":"14px","uploadLableColor":"#606266","uploadBorderRadius":"4px","btnSaveHeight":"44px","selectBgColor":"#fff","btnSaveWidth":"88px","selectIconFontSize":"14px","dateHeight":"40px","selectBorderColor":"#DCDFE6","inputBorderColor":"#DCDFE6","uploadBorderColor":"#DCDFE6","textareaFontColor":"#606266","selectBorderWidth":"1px","dateFontColor":"#606266","btnCancelBorderWidth":"1px","uploadBorderWidth":"1px","textareaFontSize":"14px","selectBorderRadius":"4px","selectFontColor":"#606266","btnSaveBorderColor":"#409EFF","btnSaveBorderWidth":"0px"},
      id: '',
      type: '',
      showPassword: false,
      ro: { qiantaizhanghao: false, mima: false, zhaopian: false },
      ruleForm: {
        qiantaizhanghao: '',
        mima: '',
        qiantaixingming: '',
        xingbie: '',
        shouji: '',
        ruzhishijian: '',
        zhaopian: '',
        zhuangtai: '在职'
      },
      xingbieOptions: ['男', '女'],
      zhuangtaiOptions: ['在职', '请假', '离职'],
      rules: {
        qiantaizhanghao: [{ required: true, message: '前台账号不能为空', trigger: 'blur' }],
        mima: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
        qiantaixingming: [{ required: true, message: '前台姓名不能为空', trigger: 'blur' }],
        shouji: [{ validator: validateMobile, trigger: 'blur' }],
      }
    };
  },
  props: ["parent"],
  created() {},
  methods: {
    init(id, type) {
      if (id) {
        this.id = id;
        this.type = type;
      }
      if(this.type=='info'||this.type=='else') {
        this.info(id);
      }
    },
    info(id) {
      this.$http({
        url: `qiantairenyuan/info/${id}`,
        method: "get"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.ruleForm = data.data;
        } else {
          this.$message.error(data.msg);
        }
      });
    },
    onSubmit() {
      this.$refs["ruleForm"].validate(valid => {
        if (valid) {
          this.$http({
            url: `qiantairenyuan/${!this.ruleForm.id ? "save" : "update"}`,
            method: "post",
            data: this.ruleForm
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.parent.showFlag = true;
                  this.parent.addOrUpdateFlag = false;
                  this.parent.search();
                  this.parent.contentStyleChange();
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
      this.parent.contentStyleChange();
    },
    zhaopianUploadChange(fileUrls) {
      this.ruleForm.zhaopian = fileUrls;
    }
  }
};
</script>
<style lang="scss">
.addEdit-block { margin: -10px; }
.detail-form-content { padding: 12px; }
.btn .el-button { padding: 0; }
.form-tip { font-size: 12px; color: #909399; line-height: 1.5; margin-top: 4px; }
</style>
