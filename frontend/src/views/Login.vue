<template>
    <div class="login-container">
        <!-- 背景大标题 -->
        <div class="background-title">医疗器械公司信息管理系统</div>

        <div class="form-container">
            <h1 class="form-title">欢迎登录</h1>
            <el-form :model="loginForm" ref="loginFormRef" class="login-form">
                <el-form-item label="账号" prop="username">
                    <el-input v-model="loginForm.username" placeholder="请输入手机号或邮箱" clearable prefix-icon="el-icon-user"
                        @blur="validateUsername"></el-input>
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" clearable
                        prefix-icon="el-icon-lock"></el-input>
                </el-form-item>

                <el-form-item>
                    <div style="display: flex; justify-content: center;">
                        <el-input v-model="loginForm.verCode" prefix-icon="el-icon-key"
                            style="width: 50%; margin-right: 28px;" placeholder="请输入验证码"></el-input>
                        <img :src="captchaUrl" @click="clickImg()" width="140px" height="40px" />
                    </div>
                </el-form-item>

            </el-form>
            <div class="small-text" @click="showAuthCodeDialog = true">使用授权码登录</div>
            <div class="button-group">
                <el-button class="reset-button" @click="resetLoginForm">重置</el-button>
                <el-button type="primary" class="login-button" @click="handleLogin">登录</el-button>
            </div>
        </div>

        <el-dialog :visible.sync="showAuthCodeDialog" title="使用授权码登录" width="400px" custom-class="auth-dialog">
            <el-form :model="authCodeForm" ref="authCodeFormRef" class="auth-form">
                <el-form-item label="授权码" prop="authCode">
                    <el-input v-model="authCodeForm.authCode" placeholder="请输入授权码" clearable
                        prefix-icon="el-icon-key"></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button class="dialog-reset-button" @click="resetAuthCodeForm">重置</el-button>
                    <el-button type="primary" class="dialog-button" @click="handleAuthCodeLogin">登录</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 注册表单对话框 -->
        <el-dialog :visible.sync="showRegisterForm" title="完善个人信息" width="50%" custom-class="register-dialog">
            <el-alert title="当前账号为初始状态，请完成个人信息以登录" type="info" show-icon class="register-tip"></el-alert>

            <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" label-width="120px">
                <el-form-item label="姓名" prop="name">
                    <el-input v-model="registerForm.name" placeholder="请输入姓名"></el-input>
                </el-form-item>

                <el-form-item label="性别" prop="gender">
                    <el-radio-group v-model="registerForm.gender">
                        <el-radio label="男">男</el-radio>
                        <el-radio label="女">女</el-radio>
                    </el-radio-group>
                </el-form-item>

                <el-form-item label="年龄" prop="age">
                    <el-input v-model.number="registerForm.age" placeholder="请输入年龄" type="number" min="18"
                        max="60"></el-input>
                </el-form-item>

                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="registerForm.email" placeholder="请输入邮箱"></el-input>
                </el-form-item>

                <el-form-item label="手机号" prop="phone">
                    <el-input v-model="registerForm.phone" placeholder="请输入手机号" pattern="^1[3-9]\d{9}$"></el-input>
                </el-form-item>

                <el-form-item label="职位" prop="position">
                    <el-input v-model="registerForm.position" placeholder="请输入职位"></el-input>
                </el-form-item>

                <el-form-item label="部门" prop="department">
                    <el-input v-model="registerForm.department" placeholder="请输入部门"></el-input>
                </el-form-item>

                <el-form-item label="角色" prop="role">
                    <el-input v-model="registerForm.role" placeholder="角色" disabled :value="userRole"></el-input>
                </el-form-item>

                <el-form-item label="密码" prop="password">
                    <el-input v-model="registerForm.password" type="password" placeholder="请设置登录密码" minlength="6"
                        maxlength="20"></el-input>
                </el-form-item>
            </el-form>

            <template #footer>
                <el-button @click="showRegisterForm = false">取消</el-button>
                <el-button type="primary" @click="submitRegisterForm">提交注册</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script>
import request from '@/utils/request';

export default {
    data() {
        return {
            captchaUrl: '',
            loginForm: {
                username: '',
                phone: '',
                email: '',
                password: '',
                verCode: '',
            },
            authCodeForm: {
                authCode: ''
            },
            showAuthCodeDialog: false,
            showRegisterForm: false, // 控制注册表单显示
            registerForm: {
                employeeId: '',
                name: '',
                gender: '',
                age: '',
                email: '',
                phone: '',
                position: '',
                department: '',
                role: '', // 注意：这里初始值为空，由后端返回后赋值
                password: ''
            },
            userRole: '', // 从后端获取的角色信息
            registerRules: { // 表单验证规则
                name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
                gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
                age: [
                    { required: true, message: '请输入年龄', trigger: 'blur' },
                    { type: 'number', message: '请输入有效年龄', trigger: 'blur' }
                ],
                email: [
                    { required: true, message: '请输入邮箱', trigger: 'blur' },
                    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
                ],
                phone: [
                    { required: true, message: '请输入手机号', trigger: 'blur' },
                    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
                ],
                position: [{ required: true, message: '请输入职位', trigger: 'blur' }],
                department: [{ required: true, message: '请输入部门', trigger: 'blur' }],
                role: [{ required: true, message: '角色信息异常', trigger: 'blur' }], // 实际应由后端保证存在
                password: [
                    { required: true, message: '请设置密码', trigger: 'blur' },
                    { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
                ]
            }
        };
    },
    mounted() {
        this.key = Math.random();
        this.captchaUrl = 'http://localhost:9090/api/captcha?key=' + this.key;
    },
    methods: {
        clickImg() {
            this.key = Math.random();
            this.captchaUrl = 'http://localhost:9090/api/captcha?key=' + this.key;
            this.loginForm.verCode = '';
        },
        validateUsername() {
            const username = this.loginForm.username;
            const phoneReg = /^1[3-9]\d{9}$/;
            const emailReg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

            if (phoneReg.test(username)) {
                this.loginForm.phone = username;
                this.loginForm.email = '';
            } else if (emailReg.test(username)) {
                this.loginForm.email = username;
                this.loginForm.phone = '';
            } else {
                this.$message.error('请输入有效的手机号或邮箱');
                this.loginForm.phone = '';
                this.loginForm.email = '';
            }
        },
        handleLogin() {
            if (!this.loginForm.phone && !this.loginForm.email) {
                this.$message.error('请输入有效的手机号或邮箱');
                return;
            }
            console.log('普通登录:', {
                phone: this.loginForm.phone,
                email: this.loginForm.email,
                password: this.loginForm.password,
                verCode: this.loginForm.verCode
            });
            request.post("/login?key=" + this.key, this.loginForm).then(res => {
                if (res.code === 1 || res.code === '1') {
                    localStorage.setItem("user", JSON.stringify(res.data));
                    this.$router.push("/");
                } else {
                    this.$message.error(res.msg);
                    this.key = Math.random();
                    this.captchaUrl = 'http://localhost:9090/api/captcha?key=' + this.key;
                    this.loginForm.verCode = '';
                }
            })

        },
        resetLoginForm() {
            this.loginForm.username = '';
            this.loginForm.phone = '';
            this.loginForm.email = '';
            this.loginForm.password = '';
            this.loginForm.verCode = '';
            this.$refs.loginFormRef.resetFields();
        },
        resetAuthCodeForm() {
            this.authCodeForm.authCode = '';
            this.$refs.authCodeFormRef.resetFields();
        },
        handleAuthCodeLogin() {
            // 授权码验证
            request.post("/login/authcode", { authCode: this.authCodeForm.authCode })
                .then(res => {
                    if (res.code === 1 || res.code === '1') {
                        if (res.data.role === 1) {
                            this.registerForm.role = "管理员"
                        } else if (res.data.role === 2) {
                            this.registerForm.role = "销售员"
                        } else if (res.data.role === 3) {
                            this.registerForm.role = "仓管员"
                        }
                        this.registerForm.employeeId = res.data.usedBy;
                        console.log("员工ID", this.registerForm.employeeId);
                        this.showRegisterForm = true
                    } else {
                        this.$message({
                            message: res.msg,
                            type: "error"
                        })
                    }
                });
        },
        submitRegisterForm() {
            if (this.registerForm.role === "管理员") {
                this.registerForm.role = 1
            } else if (this.registerForm.role === "销售员") {
                this.registerForm.role = 2
            } else if (this.registerForm.role === "仓管员") {
                this.registerForm.role = 3
            }
            console.log("注册表单数据", this.registerForm),
                request.post("/login/register", this.registerForm)
                    .then(res => {
                        if (res.code === 1 || res.code === '1') {
                            localStorage.setItem("user", JSON.stringify(res.data));
                            this.$router.push("/");
                        } else {
                            this.$message({
                                message: res.msg,
                                type: "error"
                            })
                        }
                    });
        }
    }
}
</script>

<style scoped>
/* 注册表单样式 */

/* 页面整体背景 */
.login-container {
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
    padding: 20px;
    overflow: hidden;
}

/* 背景大标题 */
.background-title {
    position: absolute;
    top: 10%;
    left: 50%;
    transform: translateX(-50%);
    font-size: 48px;
    font-weight: bold;
    color: rgba(88, 88, 88, 0.242);
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    white-space: nowrap;
    animation: fadeIn 2s ease-in-out;
}

/* 淡入动画 */
@keyframes fadeIn {
    0% {
        opacity: 0;
        transform: translate(-50%, -20px);
    }

    100% {
        opacity: 1;
        transform: translate(-50%, 0);
    }
}

/* 表单容器 - 毛玻璃效果 */
.form-container {
    width: 100%;
    max-width: 400px;
    padding: 30px;
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(10px);
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.3);
}

/* 标题 */
.form-title {
    font-size: 24px;
    font-weight: bold;
    color: #6a6969;
    text-align: center;
    margin-bottom: 20px;
}

/* 表单样式 */
.login-form {
    margin-bottom: 20px;
}

.login-form :deep(.el-form-item) {
    margin-bottom: 20px;
}

.login-form :deep(.el-form-item__label) {
    width: auto !important;
    padding-right: 10px;
    color: #333;
    font-weight: 500;
    text-align: left;
}

.login-form :deep(.el-form-item__content) {
    margin-left: 0 !important;
    flex: 1;
}

.login-form :deep(.el-input__inner) {
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.8);
    border: 1px solid #dcdcdc;
    transition: all 0.3s;
}

.login-form :deep(.el-input__inner:focus) {
    border-color: #409eff;
    box-shadow: 0 0 5px rgba(64, 158, 255, 0.5);
}

/* 使用授权码登录链接 */
.small-text {
    margin: 10px 0;
    font-size: 14px;
    color: #666;
    text-align: left;
    cursor: pointer;
    transition: color 0.3s;
}

.small-text:hover {
    color: #409eff;
}

/* 按钮组 */
.button-group {
    display: flex;
    gap: 10px;
}

/* 重置按钮 */
.reset-button {
    width: 100%;
    border-radius: 8px;
    font-size: 16px;
    padding: 12px;
    background: #f5f7fa;
    border: 1px solid #dcdcdc;
    color: #333;
    transition: all 0.3s;
}

.reset-button:hover {
    background: #e6e9ed;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
    transform: translateY(-2px);
}

/* 登录按钮 */
.login-button {
    width: 100%;
    border-radius: 8px;
    font-size: 16px;
    padding: 12px;
    transition: all 0.3s;
}

.login-button:hover {
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
    transform: translateY(-2px);
}

/* 授权码对话框 */
:deep(.auth-dialog .el-dialog) {
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(10px);
    border-radius: 12px;
    border: 1px solid rgba(255, 255, 255, 0.3);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

:deep(.auth-dialog .el-dialog__header) {
    border-bottom: none;
    padding-bottom: 10px;
}

:deep(.auth-dialog .el-dialog__title) {
    color: #333;
    font-weight: bold;
}

.auth-form :deep(.el-form-item__label) {
    width: auto !important;
    padding-right: 10px;
    color: #333;
    font-weight: 500;
    text-align: left;
}

.auth-form :deep(.el-form-item__content) {
    margin-left: 0 !important;
    flex: 1;
}

.auth-form :deep(.el-input__inner) {
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.8);
    border: 1px solid #dcdcdc;
    transition: all 0.3s;
}

.auth-form :deep(.el-input__inner:focus) {
    border-color: #409eff;
    box-shadow: 0 0 5px rgba(64, 158, 255, 0.5);
}

/* 对话框按钮 */
.dialog-footer {
    display: flex;
    justify-content: space-between;
    gap: 10px;
    padding: 10px 20px;
}

.dialog-reset-button,
.dialog-button {
    flex: 1;
    border-radius: 8px;
    font-size: 16px;
    padding: 12px;
    transition: all 0.3s;
}

.dialog-reset-button {
    background: #f5f7fa;
    border: 1px solid #dcdcdc;
    color: #333;
}

.dialog-reset-button:hover {
    background: #e6e9ed;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
    transform: translateY(-2px);
}

.dialog-button {
    background: #409eff;
    border: none;
}

.dialog-button:hover {
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
    transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 480px) {
    .background-title {
        font-size: 28px;
        top: 5%;
    }

    .form-container {
        padding: 20px;
    }

    .form-title {
        font-size: 20px;
    }

    .button-group {
        flex-direction: column;
        gap: 10px;
    }

    .reset-button,
    .login-button {
        font-size: 14px;
        padding: 10px;
    }

    :deep(.auth-dialog .el-dialog) {
        width: 90%;
    }

    .dialog-footer {
        flex-direction: column;
        gap: 10px;
    }

    .dialog-reset-button,
    .dialog-button {
        width: 100%;
    }
}
</style>