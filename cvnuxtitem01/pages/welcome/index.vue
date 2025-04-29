<script setup lang="ts">

import {Lock, Message} from "@element-plus/icons-vue";
import type {FormRules} from "element-plus";
import {useAuthService} from "~/services/auth";
import {useUserService} from "~/services/user";
import {useUserStore} from "~/store/user";

interface LoginForm{
    username: string
    password: string
    remember_me: boolean
}
type ElFormInstance = InstanceType<typeof import('element-plus')['ElForm']>

const store = useUserStore()
const router = useRouter()
const form = reactive<LoginForm>({
    username: '',
    password: '',
    remember_me: false
})
const rule = reactive<FormRules<LoginForm>>({
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码在6-20个字符之间' },
    ],
    remember_me: []
})
const formRef = ref<ElFormInstance>()


const submit = () =>{
    formRef.value?.validate((valid:boolean)=>{
        if (valid){
            userLogin()
        }else {
            ElMessage.warning('请输入对应信息')
        }
    })
}

const userLogin = () =>{
    useAuthService()
        .login(
            form.username,
            form.password,
            form.remember_me,
        ).then((res)=>{
            useUserService()
                .apiUserInfo()
                    .then((res)=>{
                        // console.log(res.data)
                        // setCookie(
                        //     USER_INFO_PREFIX,
                        //     JSON.stringify(res.data),
                        //     form.remember_me ? { expireDays: 7 } : {}
                        // )
                        store.setUser(res.data, form.remember_me ? { expireDays: 7 } : {})
                    })
        })
}

</script>

<template>
    <div style="background-color: #fdecea;height: 100vh;width: 100vw;display: flex;justify-content: center;align-items: center">
        <div id="main" style="text-align: center">
            <h2 style="color: #ea3f19;padding-top: 60px;padding-bottom: 10px">
                Welcome Back
            </h2>
            <el-text type="info" size="large">请输入对应信息</el-text>
            <div style="display: flex;justify-content: center;align-items: center;margin-top: 20px;">
                <el-form :model="form" ref="formRef" :rules="rule">
                    <el-form-item prop="username">
                        <el-input style="width: 380px;height: 38px"
                                  placeholder="输入用户名" clearable
                                  v-model="form.username"
                        >
                            <template #prefix>
                                <el-button link :icon="Message" style="color: red"/>
                            </template>
                        </el-input>
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
                    <el-form-item>
                        <el-checkbox v-model="form.remember_me">自动登录</el-checkbox>
                    </el-form-item>
                    <el-button type="danger" plain style="width: 100%" @click="submit">
                        Login in
                    </el-button>
                </el-form>
            </div>
        </div>
    </div>
</template>

<style scoped>
#main{
    width: 500px;
    height: 400px;
    background-color: #fdfdfd;
    border-radius: 1rem;
}
</style>
