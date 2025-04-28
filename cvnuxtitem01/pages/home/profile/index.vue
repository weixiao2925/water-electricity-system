<script setup lang="ts">
import { ref, reactive } from 'vue';

// 模拟用户数据
const userData = reactive({
  id: '10001',
  username: 'user123',
  nickname: '张三',
  email: 'zhangsan@example.com',
  phone: '13800138000',
  address: '北京市海淀区',
  avatar: '/assets/avatar.png',
  registerTime: '2023-01-15'
});

// 表单数据
const userForm = reactive({
  nickname: userData.nickname,
  email: userData.email,
  phone: userData.phone,
  address: userData.address,
});

// 密码修改表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 状态标记
const isEditing = ref(false);
const isChangingPassword = ref(false);
const showSuccessMessage = ref(false);
const successMessage = ref('');

// 开始编辑
const startEdit = () => {
  isEditing.value = true;
};

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false;
  // 重置表单数据为原始值
  userForm.nickname = userData.nickname;
  userForm.email = userData.email;
  userForm.phone = userData.phone;
  userForm.address = userData.address;
};

// 保存个人信息
const saveUserInfo = () => {
  // 在实际应用中，这里应该调用API保存用户信息
  userData.nickname = userForm.nickname;
  userData.email = userForm.email;
  userData.phone = userForm.phone;
  userData.address = userForm.address;
  
  isEditing.value = false;
  showSuccessNotification('个人信息更新成功');
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
  // 表单验证
  if (!passwordForm.oldPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
    alert('请填写所有密码字段');
    return;
  }
  
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    alert('新密码与确认密码不匹配');
    return;
  }
  
  // 在实际应用中，这里应该调用API修改密码
  isChangingPassword.value = false;
  showSuccessNotification('密码修改成功');
  
  // 重置表单
  passwordForm.oldPassword = '';
  passwordForm.newPassword = '';
  passwordForm.confirmPassword = '';
};

// 显示成功通知
const showSuccessNotification = (message: string) => {
  successMessage.value = message;
  showSuccessMessage.value = true;
  
  // 3秒后自动关闭提示
  setTimeout(() => {
    showSuccessMessage.value = false;
  }, 3000);
};

// 上传头像
const uploadAvatar = (event: any) => {
  const file = event.target.files[0];
  if (file) {
    // 在实际应用中，这里应该上传文件到服务器
    // 临时本地预览
    const reader = new FileReader();
    reader.onload = (e: any) => {
      userData.avatar = e.target.result;
    };
    reader.readAsDataURL(file);
    showSuccessNotification('头像更新成功');
  }
};

definePageMeta({
  layout: "home"
});
</script>

<template>
  <NuxtLayout>
    <div class="profile-container">
      <h1 class="profile-title">个人资料</h1>
      
      <!-- 成功提示 -->
      <div v-if="showSuccessMessage" class="success-message">
        {{ successMessage }}
      </div>
      
      <div class="profile-content">
        <!-- 左侧头像区域 -->
        <div class="avatar-section">
          <div class="avatar-container">
            <img :src="userData.avatar" alt="用户头像" class="avatar-image">
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
          </div>
          <div class="user-id">
            <p>用户ID: {{ userData.id }}</p>
            <p>注册时间: {{ userData.registerTime }}</p>
          </div>
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
              <div class="form-group">
                <label>用户名</label>
                <input type="text" :value="userData.username" disabled class="form-control readonly">
                <span class="hint">用户名不可修改</span>
              </div>
              
              <div class="form-group">
                <label>昵称</label>
                <input 
                  type="text" 
                  v-model="userForm.nickname" 
                  :readonly="!isEditing" 
                  class="form-control"
                  :class="{ readonly: !isEditing }"
                >
              </div>
              
              <div class="form-group">
                <label>邮箱</label>
                <input 
                  type="email" 
                  v-model="userForm.email" 
                  :readonly="!isEditing" 
                  class="form-control"
                  :class="{ readonly: !isEditing }"
                >
              </div>
              
              <div class="form-group">
                <label>手机号</label>
                <input 
                  type="tel" 
                  v-model="userForm.phone" 
                  :readonly="!isEditing" 
                  class="form-control"
                  :class="{ readonly: !isEditing }"
                >
              </div>
              
              <div class="form-group">
                <label>地址</label>
                <input 
                  type="text" 
                  v-model="userForm.address" 
                  :readonly="!isEditing" 
                  class="form-control"
                  :class="{ readonly: !isEditing }"
                >
              </div>
            </div>
          </div>
          
          <!-- 密码修改卡片 -->
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
              <div class="form-group">
                <label>当前密码</label>
                <input type="password" v-model="passwordForm.oldPassword" class="form-control">
              </div>
              
              <div class="form-group">
                <label>新密码</label>
                <input type="password" v-model="passwordForm.newPassword" class="form-control">
              </div>
              
              <div class="form-group">
                <label>确认新密码</label>
                <input type="password" v-model="passwordForm.confirmPassword" class="form-control">
              </div>
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

.success-message {
  background-color: #4CAF50;
  color: white;
  padding: 12px;
  text-align: center;
  border-radius: 4px;
  margin-bottom: 20px;
  animation: fadeIn 0.3s ease;
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

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #666;
  font-weight: bold;
}

.form-control {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.form-control:focus {
  border-color: #1976D2;
  outline: none;
}

.form-control.readonly {
  background-color: #f9f9f9;
  cursor: not-allowed;
}

.hint {
  display: block;
  font-size: 12px;
  color: #999;
  margin-top: 5px;
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
