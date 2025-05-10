<script setup lang="ts">
import { Lock, Message, Key } from "@element-plus/icons-vue";
import type { FormRules } from "element-plus";
import { useAuthService } from "~/services/auth";
import type { ElFormInstance } from "~/types/welcome/type";

interface ResetForm {
    email: string;
    code: string;
    password: string;
}

const router = useRouter();
const form = reactive<ResetForm>({
    email: '',
    code: '',
    password: ''
});

const rule = reactive<FormRules<ResetForm>>({
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
    ],
    code: [
        { required: true, message: '请输入验证码', trigger: 'blur' },
        { min: 6, max: 6, message: '验证码必须是6位', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码在6-20个字符之间', trigger: 'blur' }
    ]
});

const formRef = ref<ElFormInstance>();
const isSending = ref(false);
const countdown = ref(0);
const timer = ref<NodeJS.Timeout | null>(null);

// 发送验证码
const sendCode = () => {
    formRef.value?.validateField('email', (valid) => {
        if (valid) {
            isSending.value = true;
            countdown.value = 60;
            
            useAuthService()
                .sendEmailCode(form.email)
                .then(() => {
                    ElMessage.success('验证码已发送到您的邮箱');
                    timer.value = setInterval(() => {
                        countdown.value--;
                        if (countdown.value <= 0) {
                            if (timer.value) clearInterval(timer.value);
                            isSending.value = false;
                        }
                    }, 1000);
                })
                .catch(() => {
                    isSending.value = false;
                    if (timer.value) clearInterval(timer.value);
                });
        }
    });
};

// 提交重置密码
const submit = () => {
    formRef.value?.validate((valid: boolean) => {
        if (valid) {
            resetPassword();
        } else {
            ElMessage.warning('请输入正确的信息');
        }
    });
};

const resetPassword = () => {
    useAuthService()
        .resetPassword(form.email, form.code, form.password)
        .then(() => {
            ElMessage.success('密码重置成功');
            router.push('/welcome');
        });
};

// 返回登录页
const goToLogin = () => {
    router.push('/welcome');
};

onBeforeUnmount(() => {
    if (timer.value) clearInterval(timer.value);
});
</script>

<template>
    <div style="background-color: #fdecea;height: 100vh;width: 100vw;display: flex;justify-content: center;align-items: center">
        <div id="main" style="text-align: center">
            <h2 style="color: #ea3f19;padding-top: 60px;padding-bottom: 10px">
                重置密码
            </h2>
            <el-text type="info" size="large">请填写以下信息重置您的密码</el-text>
            <div style="display: flex;justify-content: center;align-items: center;margin-top: 20px;">
                <el-form :model="form" ref="formRef" :rules="rule">
                    <el-form-item prop="email">
                        <el-input style="width: 380px;height: 38px"
                                placeholder="输入邮箱" clearable
                                v-model="form.email"
                        >
                            <template #prefix>
                                <el-button link :icon="Message" style="color: red"/>
                            </template>
                        </el-input>
                    </el-form-item>
                    <el-form-item prop="code">
                        <div style="display: flex">
                            <el-input style="width: 280px;height: 38px"
                                    placeholder="输入验证码" clearable
                                    v-model="form.code"
                            >
                                <template #prefix>
                                    <el-button link :icon="Key" style="color: red"/>
                                </template>
                            </el-input>
                            <el-button type="danger" plain style="margin-left: 10px" 
                                    :disabled="isSending" @click="sendCode">
                                {{ isSending ? `${countdown}秒后重试` : '获取验证码' }}
                            </el-button>
                        </div>
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input style="width: 380px;height: 38px"
                                placeholder="输入新密码" clearable show-password
                                v-model="form.password"
                        >
                            <template #prefix>
                                <el-button link :icon="Lock" style="color: red"/>
                            </template>
                        </el-input>
                    </el-form-item>
                    <el-form-item>
                        <div style="width: 100%;display: flex; justify-content: flex-end;">
                            <el-text type="info" @click="goToLogin" style="cursor: pointer;">
                                返回登录
                            </el-text>
                        </div>
                    </el-form-item>
                    <el-button type="danger" plain style="width: 100%" @click="submit">
                        重置密码
                    </el-button>
                </el-form>
            </div>
        </div>
    </div>
</template>

<style scoped>
#main{
    width: 500px;
    height: 450px;
    background-color: #fdfdfd;
    border-radius: 1rem;
}
</style>
