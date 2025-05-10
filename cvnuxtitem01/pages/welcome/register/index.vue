<script setup lang="ts">

import {Lock, Message, User, Key} from "@element-plus/icons-vue";
import type {FormRules} from "element-plus";
import {useAuthService} from "~/services/auth";
import type {ElFormInstance} from "~/types/welcome/type";

interface RegisterForm {
  email: string;
  username: string;
  code: string;
  password: string;
  confirmPassword: string;
}

const router = useRouter()
const form = reactive<RegisterForm>({
  email: '',
  username: '',
  code: '',
  password: '',
  confirmPassword: ''
})

const validateEmail = (rule: any, value: string, callback: any) => {
  const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
  if (value && !emailRegex.test(value)) {
    callback(new Error('请输入有效的邮箱地址'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rule = reactive<FormRules>({
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { validator: validateEmail, trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 6, max: 6, message: '验证码必须为6位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
})

const formRef = ref<ElFormInstance>()
const isCodeSending = ref(false)
const countdown = ref(0)
const timer = ref<NodeJS.Timeout | null>(null)

const startCountdown = () => {
  countdown.value = 60
  isCodeSending.value = true

  timer.value = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearTimeout(timer.value as NodeJS.Timeout)
      isCodeSending.value = false
    }
  }, 1000)
}

const sendVerificationCode = () => {
  if (isCodeSending.value) return

  // 验证邮箱是否有效
  if (!form.email) {
    ElMessage.warning('请先输入邮箱地址')
    return
  }

  const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
  if (!emailRegex.test(form.email)) {
    ElMessage.warning('请输入有效的邮箱地址')
    return
  }

  // 发送验证码
  useAuthService()
    .sendEmailCode(form.email)
    .then(() => {
      ElMessage.success('验证码已发送，请查收邮件')
      startCountdown()
    })
    .catch(() => {
      ElMessage.error('验证码发送失败，请稍后重试')
    })
}

const submit = () => {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      userRegister()
    } else {
      ElMessage.warning('请正确填写注册信息')
    }
  })
}

const userRegister = () => {
  useAuthService()
    .register(
      form.username,
      form.password,
      form.email,
      form.code
    )
    .then(() => {
      ElMessage.success('注册成功')
      router.push('/welcome')
    })
}

const goToLogin = () => {
  router.push('/welcome')
}

onBeforeUnmount(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
})

</script>

<template>
  <div style="background-color: #fdecea;height: 100vh;width: 100vw;display: flex;justify-content: center;align-items: center">
    <div id="main" style="text-align: center">
      <h2 style="color: #ea3f19;padding-top: 40px;padding-bottom: 10px">
        创建账号
      </h2>
      <el-text type="info" size="large">请填写注册信息</el-text>
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
          <el-form-item prop="username">
            <el-input style="width: 380px;height: 38px"
                      placeholder="输入用户名" clearable
                      v-model="form.username"
            >
              <template #prefix>
                <el-button link :icon="User" style="color: red"/>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="code">
            <div style="display: flex;">
              <el-input style="width: 260px;height: 38px"
                        placeholder="输入验证码" clearable
                        v-model="form.code"
              >
                <template #prefix>
                  <el-button link :icon="Key" style="color: red"/>
                </template>
              </el-input>
              <el-button
                type="danger"
                plain
                style="width: 120px; margin-left: 5px"
                :disabled="isCodeSending"
                @click="sendVerificationCode"
              >
                {{ isCodeSending ? `${countdown}s` : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>
          <el-form-item prop="password">
            <el-input style="width: 380px;height: 38px"
                      placeholder="输入密码" clearable show-password
                      v-model="form.password"
            >
              <template #prefix>
                <el-button link :icon="Lock" style="color: red"/>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input style="width: 380px;height: 38px"
                      placeholder="确认密码" clearable show-password
                      v-model="form.confirmPassword"
            >
              <template #prefix>
                <el-button link :icon="Lock" style="color: red"/>
              </template>
            </el-input>
          </el-form-item>
          <el-button type="danger" plain style="width: 100%" @click="submit">
            注册
          </el-button>
          <div style="margin-top: 10px; text-align: right;">
            <el-text type="info" @click="goToLogin" style="cursor: pointer;">
              已有账号？去登录
            </el-text>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<style scoped>
#main {
  width: 500px;
  height: 520px;
  background-color: #fdfdfd;
  border-radius: 1rem;
}
</style>
