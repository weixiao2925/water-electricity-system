<template>
  <NuxtLayout name="admin">
    <div class="users-page">
      <div class="page-header">
        <h1 class="page-title">用户管理</h1>
        <button class="add-user-btn" @click="openUserModal('add')">
          <span class="icon">+</span> 新增用户
        </button>
      </div>

      <!-- 搜索和筛选区域 -->
      <div class="filter-bar">
        <div class="search-box">
          <input
            type="text"
            v-model="searchQuery"
            placeholder="搜索用户名/邮箱"
            @input="handleSearch"
          />
          <button class="search-btn">搜索</button>
        </div>
        <div class="filters">
          <select v-model="roleFilter" @change="applyFilters">
            <option value="">所有角色</option>
            <option value="admin">管理员</option>
            <option value="user">普通用户</option>
            <option value="guest">访客</option>
          </select>
          <select v-model="statusFilter" @change="applyFilters">
            <option value="">所有状态</option>
            <option value="active">正常</option>
            <option value="disabled">已禁用</option>
          </select>
        </div>
      </div>

      <!-- 用户表格 -->
      <div class="users-table">
        <table>
          <thead>
            <tr>
              <th>用户ID</th>
              <th>用户名</th>
              <th>邮箱</th>
              <th>角色</th>
              <th>状态</th>
              <th>创建时间</th>
              <th>最后登录</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in displayedUsers" :key="user.id" :class="{ 'disabled-row': user.status === 'disabled' }">
              <td>{{ user.id }}</td>
              <td>{{ user.username }}</td>
              <td>{{ user.email }}</td>
              <td>
                <span class="role-tag" :class="getRoleClass(user.role)">
                  {{ getRoleLabel(user.role) }}
                </span>
              </td>
              <td>
                <span class="status-indicator" :class="user.status"></span>
                {{ user.status === 'active' ? '正常' : '已禁用' }}
              </td>
              <td>{{ formatDate(user.createdAt) }}</td>
              <td>{{ formatDate(user.lastLogin) }}</td>
              <td class="actions">
                <button class="edit-btn" @click="openUserModal('edit', user)">编辑</button>
                <button class="reset-btn" @click="resetPassword(user.id)">重置密码</button>
                <button
                  :class="user.status === 'active' ? 'disable-btn' : 'enable-btn'"
                  @click="toggleUserStatus(user.id, user.status)"
                >
                  {{ user.status === 'active' ? '禁用' : '启用' }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页控件 -->
      <div class="pagination">
        <button :disabled="currentPage === 1" @click="currentPage--">上一页</button>
        <span>{{ currentPage }} / {{ totalPages }}</span>
        <button :disabled="currentPage === totalPages" @click="currentPage++">下一页</button>
      </div>

      <!-- 用户编辑/新增弹窗 -->
      <div class="modal-backdrop" v-if="showUserModal" @click="closeUserModal"></div>
      <div class="user-modal" v-if="showUserModal" @click.stop>
        <div class="modal-header">
          <h2>{{ modalMode === 'add' ? '新增用户' : '编辑用户' }}</h2>
          <button class="close-btn" @click="closeUserModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveUser">
            <div class="form-group">
              <label for="username">用户名</label>
              <input type="text" id="username" v-model="userForm.username" required />
            </div>
            <div class="form-group">
              <label for="email">邮箱</label>
              <input type="email" id="email" v-model="userForm.email" required />
            </div>
            <div class="form-group" v-if="modalMode === 'add'">
              <label for="password">初始密码</label>
              <input type="password" id="password" v-model="userForm.password" required />
            </div>
            <div class="form-group">
              <label for="role">角色</label>
              <select id="role" v-model="userForm.role" required>
                <option value="admin">管理员</option>
                <option value="user">普通用户</option>
                <option value="guest">访客</option>
              </select>
            </div>
            <div class="form-actions">
              <button type="button" class="cancel-btn" @click="closeUserModal">取消</button>
              <button type="submit" class="save-btn">保存</button>
            </div>
          </form>
        </div>
      </div>

      <!-- 重置密码确认弹窗 -->
      <div class="modal-backdrop" v-if="showResetModal" @click="closeResetModal"></div>
      <div class="confirm-modal" v-if="showResetModal" @click.stop>
        <div class="modal-header">
          <h2>重置密码</h2>
          <button class="close-btn" @click="closeResetModal">×</button>
        </div>
        <div class="modal-body">
          <p>确定要重置该用户的密码吗？系统将生成随机密码并发送邮件通知。</p>
          <div class="form-actions">
            <button class="cancel-btn" @click="closeResetModal">取消</button>
            <button class="confirm-btn" @click="confirmResetPassword">确认重置</button>
          </div>
        </div>
      </div>
    </div>
  </NuxtLayout>
</template>

<script setup>

// 使用管理布局
definePageMeta({
  layout: 'admin'
});

// 用户数据相关
const users = ref([]);
const currentPage = ref(1);
const pageSize = 10;
const totalPages = ref(1);
const searchQuery = ref('');
const roleFilter = ref('');
const statusFilter = ref('');
const userToReset = ref(null);

// 弹窗相关
const showUserModal = ref(false);
const showResetModal = ref(false);
const modalMode = ref('add'); // 'add' 或 'edit'
const userForm = ref({
  id: '',
  username: '',
  email: '',
  password: '',
  role: 'user',
  status: 'active'
});

// 获取用户列表
const fetchUsers = async () => {
  try {
    // 实际项目中应调用API获取数据
    // const { data } = await api.getUsers({
    //   page: currentPage.value,
    //   pageSize,
    //   search: searchQuery.value,
    //   role: roleFilter.value,
    //   status: statusFilter.value
    // });

    // 模拟数据
      users.value = Array.from({length: 30}, (_, i) => ({
        id: `U${1000 + i}`,
        username: `user${i}`,
        email: `user${i}@example.com`,
        role: i % 5 === 0 ? 'admin' : i % 3 === 0 ? 'guest' : 'user',
        status: i % 7 === 0 ? 'disabled' : 'active',
        createdAt: new Date(Date.now() - Math.random() * 86400000 * 365),
        lastLogin: i % 9 === 0 ? null : new Date(Date.now() - Math.random() * 86400000 * 30)
    }));
    totalPages.value = Math.ceil(users.value.length / pageSize);
  } catch (error) {
    console.error('获取用户列表失败:', error);
  }
};

// 分页后的用户列表
const displayedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  const end = start + pageSize;
  return users.value.slice(start, end);
});

// 搜索和筛选
const handleSearch = () => {
  currentPage.value = 1;
  fetchUsers();
};

const applyFilters = () => {
  currentPage.value = 1;
  fetchUsers();
};

// 打开用户弹窗
const openUserModal = (mode, user = null) => {
  modalMode.value = mode;
  if (mode === 'edit' && user) {
    userForm.value = { ...user };
    delete userForm.value.password; // 编辑模式不需要密码字段
  } else {
    userForm.value = {
      id: '',
      username: '',
      email: '',
      password: '',
      role: 'user',
      status: 'active'
    };
  }
  showUserModal.value = true;
};

// 关闭用户弹窗
const closeUserModal = () => {
  showUserModal.value = false;
};

// 保存用户
const saveUser = async () => {
  try {
    if (modalMode.value === 'add') {
      // 实际项目中应调用API创建用户
      // await api.createUser(userForm.value);
      console.log('创建用户:', userForm.value);
    } else {
      // 实际项目中应调用API更新用户
      // await api.updateUser(userForm.value.id, userForm.value);
      console.log('更新用户:', userForm.value);
    }

    // 关闭弹窗并刷新用户列表
    closeUserModal();
    fetchUsers();
  } catch (error) {
    console.error('保存用户失败:', error);
  }
};

// 重置密码
const resetPassword = (userId) => {
  userToReset.value = userId;
  showResetModal.value = true;
};

// 关闭重置密码弹窗
const closeResetModal = () => {
  showResetModal.value = false;
  userToReset.value = null;
};

// 确认重置密码
const confirmResetPassword = async () => {
  try {
    if (userToReset.value) {
      // 实际项目中应调用API重置密码
      // await api.resetPassword(userToReset.value);
      console.log('重置密码:', userToReset.value);

      // 关闭弹窗
      closeResetModal();
    }
  } catch (error) {
    console.error('重置密码失败:', error);
  }
};

// 切换用户状态（启用/禁用）
const toggleUserStatus = async (userId, currentStatus) => {
  try {
    const newStatus = currentStatus === 'active' ? 'disabled' : 'active';
    // 实际项目中应调用API更新用户状态
    // await api.updateUserStatus(userId, newStatus);
    console.log('切换用户状态:', userId, newStatus);

    // 更新本地用户列表
    users.value = users.value.map(user => {
      if (user.id === userId) {
        return { ...user, status: newStatus };
      }
      return user;
    });
  } catch (error) {
    console.error('切换用户状态失败:', error);
  }
};

// 角色显示工具函数
const getRoleLabel = (role) => {
  const roleMap = {
    admin: '管理员',
    user: '普通用户',
    guest: '访客'
  };
  return roleMap[role] || role;
};

// 根据角色获取样式类
const getRoleClass = (role) => {
  return {
    admin: 'role-admin',
    user: 'role-user',
    guest: 'role-guest'
  }[role] || '';
};

// 日期格式化
const formatDate = (date) => {
  if (!date) return '未登录';
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 监听分页变化
watch(currentPage, () => {
  fetchUsers();
});

// 组件挂载时获取数据
onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
.users-page {
  max-width: 100%;
  padding: 0 16px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 500;
}

.add-user-btn {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.add-user-btn .icon {
  margin-right: 6px;
  font-size: 16px;
}

/* 搜索和筛选区域 */
.filter-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.search-box {
  display: flex;
  width: 350px;
}

.search-box input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-right: none;
  border-radius: 4px 0 0 4px;
}

.search-btn {
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
}

.filters {
  display: flex;
  gap: 12px;
}

.filters select {
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  min-width: 120px;
}

/* 用户表格 */
.users-table {
  background-color: white;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  text-align: left;
  padding: 12px 16px;
  border-bottom: 1px solid #e8e8e8;
}

th {
  background-color: #fafafa;
  font-weight: 500;
}

.disabled-row {
  background-color: #f5f5f5;
  color: #999;
}

.role-tag {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.role-admin {
  background-color: #faad14;
  color: white;
}

.role-user {
  background-color: #52c41a;
  color: white;
}

.role-guest {
  background-color: #d9d9d9;
  color: #595959;
}

.status-indicator {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 6px;
}

.status-indicator.active {
  background-color: #52c41a;
}

.status-indicator.disabled {
  background-color: #ff4d4f;
}

.actions {
  display: flex;
  gap: 8px;
}

.edit-btn, .reset-btn, .disable-btn, .enable-btn {
  padding: 4px 8px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  white-space: nowrap;
}

.edit-btn {
  background-color: #1890ff;
  color: white;
}

.reset-btn {
  background-color: #faad14;
  color: white;
}

.disable-btn {
  background-color: #ff4d4f;
  color: white;
}

.enable-btn {
  background-color: #52c41a;
  color: white;
}

/* 分页控件 */
.pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 16px;
  gap: 12px;
}

.pagination button {
  padding: 6px 12px;
  background-color: white;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  color: #d9d9d9;
  cursor: not-allowed;
}

/* 弹窗样式 */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 100;
}

.user-modal, .confirm-modal {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 101;
  width: 500px;
  max-width: 90vw;
}

.confirm-modal {
  width: 400px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #e8e8e8;
}

.modal-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.modal-body {
  padding: 24px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
}

.form-group input, .form-group select {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.cancel-btn, .save-btn, .confirm-btn {
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn {
  background-color: white;
  border: 1px solid #d9d9d9;
}

.save-btn, .confirm-btn {
  background-color: #1890ff;
  color: white;
  border: none;
}

@media (max-width: 1024px) {
  .filter-bar {
    flex-direction: column;
    gap: 12px;
  }

  .search-box {
    width: 100%;
  }

  .actions {
    flex-direction: column;
  }
}

@media (max-width: 768px) {
  .users-table {
    overflow-x: auto;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .add-user-btn {
    align-self: flex-start;
  }
}
</style>
