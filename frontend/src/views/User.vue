<template>
  <div class="personal-center">
    <!-- 用户信息 -->
    <el-card class="user-info-card" shadow="hover">
      <div slot="header" class="card-header">
        <span>个人信息</span>
      </div>
      <el-form class="user-form" label-width="120px">
        <div class="form-grid">
          <el-form-item label="姓名: ">
            <span>{{ user.name }}</span>
          </el-form-item>
          <el-form-item label="性别: ">
            <span>{{ user.gender }}</span>
          </el-form-item>
          <el-form-item label="年龄: ">
            <span>{{ user.age }}</span>
          </el-form-item>
          <el-form-item label="职位: ">
            <span>{{ user.position }}</span>
          </el-form-item>
          <el-form-item label="部门: ">
            <span>{{ user.department }}</span>
          </el-form-item>
          <el-form-item label="联系电话: ">
            <span>{{ user.phone }}</span>
          </el-form-item>
          <el-form-item label="角色: ">
            <span>{{ roleText }}</span>
          </el-form-item>
        </div>
      </el-form>
      <div class="operation-buttons">
        <el-button class="custom-button primary" @click="showChangePasswordDialog">修改密码</el-button>
        <el-button class="custom-button danger" @click="logout">退出登录</el-button>
      </div>
    </el-card>

    <!-- 修改密码对话框 -->
    <el-dialog title="修改密码" :visible.sync="passwordDialogVisible" width="30%" custom-class="password-dialog"
      :append-to-body="true">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="120px" class="dialog-form">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input type="password" v-model="passwordForm.oldPassword" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="passwordForm.newPassword" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="passwordForm.confirmPassword" show-password></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button class="custom-button cancel" @click="passwordDialogVisible = false">取消</el-button>
        <el-button class="custom-button primary" @click="submitChangePassword">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'
export default {
  name: 'PersonalCenter',
  data() {
    // 确认密码校验
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      // 用户信息
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      // 修改密码对话框
      passwordDialogVisible: false,
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    roleText() {
      // 定义角色映射对象
      const roleMap = {
        1: '管理员',
        2: '销售员',
        3: '仓管员'
      };
      // 根据 user.role 获取对应的中文名称，默认返回“未知角色”
      return roleMap[this.user.role] || '未知角色';
    }
  },
  methods: {
    // 显示修改密码对话框
    showChangePasswordDialog() {
      this.passwordForm = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      this.passwordDialogVisible = true
    },
    // 提交修改密码
    submitChangePassword() {
      this.$refs.passwordForm.validate((valid) => {
        if (valid) {
          // 构建提交数据（包含employeeId和密码）
          const formData = {
            employeeId: this.user.employeeId, // 从用户信息获取ID
            oldPassword: this.passwordForm.oldPassword,
            newPassword: this.passwordForm.newPassword
          };

          // 校验employeeId是否存在（防止本地存储异常）
          if (!formData.employeeId) {
            return this.$message.error('用户信息异常，请重新登录');
          }

          // 调用后端接口
          request.post('/employee/changePassword', formData) // 假设接口地址
            .then(res => {
              if (res.code === 1) {
                this.$message.success('密码修改成功');
                this.passwordDialogVisible = false;
                this.$refs.passwordForm.resetFields();
              } else {
                this.$message.error(res.msg || '密码修改失败');
              }
            })
            .catch(error => {
              console.error('修改密码请求失败:', error);
              this.$message.error('网络请求失败，请重试');
            });
        }
      });
    },
    // 退出登录
    logout() {
      this.$confirm('确定要退出登录吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 删除登录token
        localStorage.removeItem('user');
        // 跳转到登录页面
        this.$router.push('/login');
        this.$message.success('退出登录成功');
      }).catch(() => {
        // 处理取消操作，这里可以留空，也可以添加提示信息
        console.log('用户取消了退出登录操作');
      });
    }
  }
}
</script>

<style scoped>
.personal-center {
  padding: 30px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

/* 用户信息卡片 */
.user-info-card {
  max-width: 800px;
  width: 100%;
  background: #ffffff;
  border-radius: 12px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.user-info-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.card-header {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  padding: 10px 20px;
  border-bottom: 1px solid #e6e6e6;
}

/* 表单样式 */
.user-form {
  padding: 20px;
  font-size: 16px;
  color: #606266;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-form-item__label {
  font-weight: 500;
  color: #303133;
}

.el-form-item__content span {
  color: #606266;
  font-weight: 400;
}

/* 操作按钮 */
.operation-buttons {
  text-align: center;
  padding: 20px 0;
}

.custom-button {
  padding: 12px 24px;
  font-size: 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
  border: none;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.custom-button.primary {
  background: linear-gradient(45deg, #409eff, #66b1ff);
  color: #ffffff;
}

.custom-button.primary:hover {
  background: linear-gradient(45deg, #66b1ff, #409eff);
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(64, 158, 255, 0.3);
}

.custom-button.danger {
  background: linear-gradient(45deg, #f56c6c, #ff8787);
  color: #ffffff;
}

.custom-button.danger:hover {
  background: linear-gradient(45deg, #ff8787, #f56c6c);
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(245, 108, 108, 0.3);
}

.custom-button.cancel {
  background: linear-gradient(45deg, #dcdcdc, #f5f5f5);
  color: #606266;
}

.custom-button.cancel:hover {
  background: linear-gradient(45deg, #f5f5f5, #dcdcdc);
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
}

/* 修改密码对话框 */
.password-dialog {
  border-radius: 12px;
  overflow: hidden;
  animation: fadeIn 0.3s ease;
}

.dialog-form {
  padding: 0 20px;
}

.password-dialog .el-input__inner {
  border-radius: 8px;
  font-size: 14px;
}

/* 淡入动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }

  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>