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
          <el-form-item label="客房号" prop="kefanghao">
            <el-input v-model="ruleForm.kefanghao" placeholder="客房号" :readonly="type=='info'"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客房类型" prop="kefangleixing">
            <el-select v-if="type!='info'" v-model="ruleForm.kefangleixing" placeholder="请选择客房类型">
              <el-option label="标准间" value="标准间"></el-option>
              <el-option label="大床房" value="大床房"></el-option>
              <el-option label="豪华套房" value="豪华套房"></el-option>
              <el-option label="商务房" value="商务房"></el-option>
              <el-option label="总统套房" value="总统套房"></el-option>
            </el-select>
            <el-input v-else v-model="ruleForm.kefangleixing" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="床型" prop="chuangxing">
            <el-select v-if="type!='info'" v-model="ruleForm.chuangxing" placeholder="请选择床型">
              <el-option label="单人床" value="单人床"></el-option>
              <el-option label="双人床" value="双人床"></el-option>
              <el-option label="大床" value="大床"></el-option>
              <el-option label="双床" value="双床"></el-option>
            </el-select>
            <el-input v-else v-model="ruleForm.chuangxing" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="楼层" prop="louceng">
            <el-input v-model="ruleForm.louceng" placeholder="楼层" :readonly="type=='info'"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="客房图片" prop="kefangtupian">
            <file-upload v-if="type!='info'"
              tip="点击上传客房图片"
              action="file/upload"
              :limit="3"
              :multiple="true"
              :fileUrls="ruleForm.kefangtupian?ruleForm.kefangtupian:''"
              @change="kefangtupianUploadSuccess"
            ></file-upload>
            <div v-else>
              <img v-if="ruleForm.kefangtupian" v-for="(item,index) in ruleForm.kefangtupian.split(',')" :key="index" :src="item" style="width:100px;height:100px;margin-right:10px;">
              <span v-else>暂无图片</span>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="房间面积" prop="fangjianmianji">
            <el-input v-model="ruleForm.fangjianmianji" placeholder="房间面积(平方米)" :readonly="type=='info'"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="价格" prop="jiage">
            <el-input v-model="ruleForm.jiage" placeholder="价格(元/天)" :readonly="type=='info'"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客房状态" prop="kefangzhuangtai">
            <el-select v-if="type!='info'" v-model="ruleForm.kefangzhuangtai" placeholder="请选择客房状态">
              <el-option label="空闲" value="空闲"></el-option>
              <el-option label="已预约" value="已预约"></el-option>
              <el-option label="已入住" value="已入住"></el-option>
              <el-option label="待清扫" value="待清扫"></el-option>
            </el-select>
            <el-input v-else v-model="ruleForm.kefangzhuangtai" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="卫生情况" prop="weishengqingkuang">
            <el-select v-if="type!='info'" v-model="ruleForm.weishengqingkuang" placeholder="请选择卫生情况">
              <el-option label="已清扫" value="已清扫"></el-option>
              <el-option label="待清扫" value="待清扫"></el-option>
            </el-select>
            <el-input v-else v-model="ruleForm.weishengqingkuang" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="可预约时间" prop="keyueshijian">
            <el-date-picker v-if="type!='info'" v-model="ruleForm.keyueshijian" type="date" value-format="yyyy-MM-dd" placeholder="选择可预约时间"></el-date-picker>
            <el-input v-else v-model="ruleForm.keyueshijian" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所属酒店" prop="suoshujiudian">
            <el-input v-model="ruleForm.suoshujiudian" placeholder="所属酒店" :readonly="type=='info'"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="客房环境" prop="kefanghuanjing">
            <el-input v-if="type!='info'" type="textarea" v-model="ruleForm.kefanghuanjing" placeholder="客房环境描述" :rows="3"></el-input>
            <div v-else>{{ ruleForm.kefanghuanjing }}</div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="客房详情" prop="kefangxiangqing">
            <el-input v-if="type!='info'" type="textarea" v-model="ruleForm.kefangxiangqing" placeholder="客房详情描述" :rows="4"></el-input>
            <div v-else>{{ ruleForm.kefangxiangqing }}</div>
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
import FileUpload from "@/components/common/FileUpload";
export default {
  data() {
    return {
      ruleForm: {
        kefanghao: '',
        kefangleixing: '',
        chuangxing: '',
        louceng: '',
        kefangtupian: '',
        fangjianmianji: '',
        jiage: '',
        kefangzhuangtai: '空闲',
        weishengqingkuang: '已清扫',
        keyueshijian: '',
        suoshujiudian: '',
        kefanghuanjing: '',
        kefangxiangqing: ''
      },
      rules: {
        kefanghao: [{ required: true, message: '请输入客房号', trigger: 'blur' }],
        kefangleixing: [{ required: true, message: '请选择客房类型', trigger: 'change' }],
        jiage: [{ required: true, message: '请输入价格', trigger: 'blur' }]
      },
      type: ''
    };
  },
  props: ['parent'],
  components: {
    FileUpload
  },
  methods: {
    init(id, type) {
      this.type = type;
      if (id) {
        this.$http({
          url: `kefangxinxi/info/${id}`,
          method: 'get'
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.ruleForm = data.data;
          }
        });
      } else {
        this.ruleForm = {
          kefanghao: '',
          kefangleixing: '',
          chuangxing: '',
          louceng: '',
          kefangtupian: '',
          fangjianmianji: '',
          jiage: '',
          kefangzhuangtai: '空闲',
          weishengqingkuang: '已清扫',
          keyueshijian: '',
          suoshujiudian: '',
          kefanghuanjing: '',
          kefangxiangqing: ''
        };
      }
    },
    kefangtupianUploadSuccess(fileUrls) {
      this.ruleForm.kefangtupian = fileUrls;
    },
    onSubmit() {
      this.$refs['ruleForm'].validate(valid => {
        if (valid) {
          this.$http({
            url: `kefangxinxi/${!this.ruleForm.id ? 'save' : 'update'}`,
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
