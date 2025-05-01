<script setup lang="ts">
import {useUserService} from "~/services/user";
import type {FormRules} from "element-plus";
import {useUserStore} from "~/store/user";
import type {InfoForm, PasswordForm, UserInfo, ElFormInstance} from "~/types/home/profile/type";



const useStore = useUserStore()
const userData: UserInfo = reactive({
    id: -1,
    username: '',
    email: '',
    avatar: '',
    registerTime: '',
    details: {
        gender: -1,
        qq: '',
        wx: '',
        phone: '',
        address: '',
        desc: '',
    }
});
const passwordForm: PasswordForm = reactive({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
});
const infoFormRef = ref<ElFormInstance>();
const passwordFormRef = ref<ElFormInstance>();
// 状态标记
const isEditing = ref(false);
const isChangingPassword = ref(false);

const validatePassword = (_: any, value: string, callback: (error ?: Error)=>void ) => {
    // console.log(value,':',data.password)
    if (value===''){
        callback(new Error("请再次输入密码"))
    }else if (value !== passwordForm.newPassword){
        callback(new Error("两次密码输入不一致"))
    }
    else {
        callback()
    }
}
const infoRule = reactive<FormRules<InfoForm>>({
    email: [
        { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
    ],
})
const pawRule = reactive<FormRules<PasswordForm>>({
    oldPassword:[
        {required: true, message: '请输入原来的密码', trigger: 'blur'}
    ],
    newPassword:[
        {required: true, message: '请输入新密码', trigger:'blur'},
        {min: 6,max: 20, message: '密码长度在6-20个字符之间', trigger: ['blur']},
    ],
    confirmPassword:[
        {required: true, message: '请再次输入新密码', trigger: 'blur'},
        {validator: validatePassword, trigger: ['blur','change']},
    ]
})

const refetchUserInfo = () => {
    useUserService()
        .apiUserInfoDetail()
        .then(res => {
            Object.assign(userData, res.data);
        })
}

// 开始编辑
const startEdit = () => {
    isEditing.value = true;
};

// 取消编辑
const cancelEdit = () => {
    isEditing.value = false;
    refetchUserInfo()
};

// 保存个人信息
const saveUserInfo = () => {
    infoFormRef.value?.validate((valid: boolean) => {
        if (valid){
            ElMessage.success('个人信息更新成功');
            isEditing.value = false;
        }else {
            ElMessage.error('请填有效的信息');
        }
    })
};

// 开始修改密码
const startChangePassword = () => {
    isChangingPassword.value = true;
};

// 取消修改密码
const cancelChangePassword = () => {
    isChangingPassword.value = false;
    passwordForm.oldPassword = '';
    passwordForm.newPassword = '';
    passwordForm.confirmPassword = '';
};

// 提交密码修改
const submitPasswordChange = () => {
    passwordFormRef.value?.validate((valid: boolean) => {
        if (valid){
            isChangingPassword.value = false;
            ElMessage.success('密码修改成功');

            // 重置表单
            passwordForm.oldPassword = '';
            passwordForm.newPassword = '';
            passwordForm.confirmPassword = '';
        }else {
            ElMessage.error('请填写完整信息');
        }
    })
};

// 上传头像
const uploadAvatar = (event: any) => {
    const file = event.target.files[0];
    if (file) {
        // 临时本地预览
        const reader = new FileReader();
        reader.onload = (e: any) => {
            userData.avatar = e.target.result;
        };
        reader.readAsDataURL(file);
        ElMessage.success('头像更新成功');
    }
};


definePageMeta({
  layout: "home"
});
onMounted(() => {
    refetchUserInfo()
})
</script>

<template>
  <NuxtLayout>
    <div class="profile-container">
      <h1 class="profile-title">个人资料</h1>
      <div class="profile-content">
        <!-- 左侧头像区域 -->
        <div class="avatar-section">
          <el-card class="avatar-container" shadow="hover">
              <el-avatar :src="useStore.avatarUrl" :size="70"/>
            <div class="avatar-upload">
              <label for="avatar-input" class="upload-btn">更换头像</label>
              <input
                type="file"
                id="avatar-input"
                class="avatar-input"
                accept="image/*"
                @change="uploadAvatar"
              >
            </div>
          </el-card>
          <el-card class="user-id" shadow="hover">
            <p>用户名: {{ userData.username }}</p>
            <p>个人签名: {{ userData.details.desc }}</p>
            <p>性别: {{ userData.details.gender === 1 ? '男' : '女' }}</p>
            <p style="font-size: 14px;color: gray">注册时间: {{ userData.registerTime }}</p>
          </el-card>
        </div>

        <!-- 右侧信息区域 -->
        <div class="info-section">
          <!-- 基本信息卡片 -->
          <div class="info-card">
            <div class="card-header">
              <h2>基本信息</h2>
              <template v-if="!isEditing">
                <button class="btn edit-btn" @click="startEdit">编辑</button>
              </template>
              <template v-else>
                <div class="btn-group">
                  <button class="btn save-btn" @click="saveUserInfo">保存</button>
                  <button class="btn cancel-btn" @click="cancelEdit">取消</button>
                </div>
              </template>
            </div>

            <div class="form-content">
                <el-form :model="userData" ref="infoFormRef" :rules="infoRule"
                         label-width="80px" label-position="top" >
                    <el-form-item label="用户名">
                        <el-input
                            v-model="userData.username"
                            disabled
                            size="large"
                            placeholder="请输入用户名"
                        />
                    </el-form-item>
                    <el-form-item label="邮箱" prop="email">
                        <el-input
                            v-model="userData.email"
                            disabled
                            size="large"
                            placeholder="请输入邮箱"
                        />
                    </el-form-item>
                    <el-form-item label="手机号">
                        <el-input
                            v-model="userData.details.phone"
                            :disabled="!isEditing"
                            size="large"
                            placeholder="请输入手机号"
                        />
                    </el-form-item>
                    <el-form-item label="QQ号">
                        <el-input
                            v-model="userData.details.qq"
                            :disabled="!isEditing"
                            size="large"
                            placeholder="请输入QQ号"
                        />
                    </el-form-item>
                    <el-form-item label="微信号">
                        <el-input
                            v-model="userData.details.wx"
                            :disabled="!isEditing"
                            size="large"
                            placeholder="请输入微信号"
                        />
                    </el-form-item>
                    <el-form-item label="地址">
                        <el-input
                            v-model="userData.details.address"
                            :disabled="!isEditing"
                            size="large"
                            placeholder="请输入地址"
                        />
                    </el-form-item>
                </el-form>
            </div>
          </div>

            <div class="info-card">
                <div class="card-header">
                    <h2>密码修改</h2>
                    <template v-if="!isChangingPassword">
                        <button class="btn edit-btn" @click="startChangePassword">修改密码</button>
                    </template>
                    <template v-else>
                        <div class="btn-group">
                            <button class="btn save-btn" @click="submitPasswordChange">保存</button>
                            <button class="btn cancel-btn" @click="cancelChangePassword">取消</button>
                        </div>
                    </template>
                </div>
                <div v-if="isChangingPassword" class="form-content">
                    <el-form :model="passwordForm" ref="passwordFormRef" :rules="pawRule"
                             label-width="80px" label-position="top">
                        <el-form-item label="当前密码" prop="oldPassword">
                            <el-input
                                type="password"
                                show-password
                                clearable
                                v-model="passwordForm.oldPassword"
                                placeholder="请输入当前用户的密码"
                                :disabled="!isChangingPassword"
                                size="large"
                            />
                        </el-form-item>
                        <el-form-item label="新密码" prop="newPassword">
                            <el-input
                                type="password"
                                show-password
                                clearable
                                v-model="passwordForm.newPassword"
                                placeholder="请输入新密码"
                                :disabled="!isChangingPassword"
                                size="large"
                            />
                        </el-form-item>
                        <el-form-item label="确认新密码" prop="confirmPassword">
                            <el-input
                                type="password"
                                show-password
                                clearable
                                v-model="passwordForm.confirmPassword"
                                placeholder="请再次输入新密码"
                                :disabled="!isChangingPassword"
                                size="large"
                            />
                        </el-form-item>
                    </el-form>
                </div>

                <div v-else class="password-placeholder">
                    <p>出于安全考虑，密码不会显示。如需修改，请点击"修改密码"按钮。</p>
                </div>
            </div>
        </div>
      </div>
    </div>
  </NuxtLayout>
</template>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.profile-title {
  color: #333;
  margin-bottom: 30px;
  text-align: center;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}

.profile-content {
  display: flex;
  gap: 30px;
  flex-wrap: wrap;
}

.avatar-section {
  flex: 1;
  min-width: 250px;
  max-width: 300px;
}

.info-section {
  flex: 2;
  min-width: 500px;
}

.avatar-container {
  background-color: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
  margin-bottom: 20px;
}

.avatar-image {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 15px;
}

.avatar-upload {
  margin-top: 10px;
}

.upload-btn {
  display: inline-block;
  padding: 8px 16px;
  background-color: #1976D2;
  color: white;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 14px;
}

.upload-btn:hover {
  background-color: #1565C0;
}

.avatar-input {
  display: none;
}

.user-id {
  background-color: white;
  padding: 15px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-id p {
  margin: 8px 0;
  color: #666;
}

.info-card {
  background-color: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.card-header h2 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.btn {
  padding: 8px 16px;
  font-size: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s;
}

.edit-btn {
  background-color: #1976D2;
  color: white;
}

.edit-btn:hover {
  background-color: #1565C0;
}

.save-btn {
  background-color: #4CAF50;
  color: white;
}

.save-btn:hover {
  background-color: #43A047;
}

.cancel-btn {
  background-color: #F44336;
  color: white;
  margin-left: 10px;
}

.cancel-btn:hover {
  background-color: #E53935;
}

.btn-group {
  display: flex;
}

.form-content {
  padding: 10px 0;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #666;
  font-weight: bold;
}

.password-placeholder {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 4px;
  text-align: center;
  color: #666;
}

@media (max-width: 900px) {
  .profile-content {
    flex-direction: column;
  }

  .avatar-section {
    max-width: 100%;
  }

  .info-section {
    min-width: 100%;
  }
}
</style>
