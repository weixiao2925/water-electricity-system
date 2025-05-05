<script setup>
import { Plus, Edit, Search, Key, Lock, Unlock } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
//
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

// 表单校验规则
const userFormRules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
    ],
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能小于6个字符', trigger: 'blur' }
    ],
    role: [
        { required: true, message: '请选择角色', trigger: 'change' }
    ]
};

// 表单引用
const userFormRef = ref(null);

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
        ElMessage.error('获取用户列表失败');
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

// 处理分页变化
const handleCurrentChange = (val) => {
    currentPage.value = val;
    // fetchUsers(); // 如果是后端分页则需要重新获取数据
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
    if (!userFormRef.value) return;

    try {
        await userFormRef.value.validate(async (valid) => {
            if (valid) {
                if (modalMode.value === 'add') {
                    // 实际项目中应调用API创建用户
                    // await api.createUser(userForm.value);
                    console.log('创建用户:', userForm.value);
                    ElMessage.success('用户创建成功');
                } else {
                    // 实际项目中应调用API更新用户
                    // await api.updateUser(userForm.value.id, userForm.value);
                    console.log('更新用户:', userForm.value);
                    ElMessage.success('用户更新成功');
                }

                // 关闭弹窗并刷新用户列表
                closeUserModal();
                fetchUsers();
            }
        });
    } catch (error) {
        console.error('保存用户失败:', error);
        ElMessage.error('保存用户失败');
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
            ElMessage.success('密码重置成功，已向用户发送邮件');

            // 关闭弹窗
            closeResetModal();
        }
    } catch (error) {
        console.error('重置密码失败:', error);
        ElMessage.error('重置密码失败');
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

        ElMessage.success(`用户已${newStatus === 'active' ? '启用' : '禁用'}`);
    } catch (error) {
        console.error('切换用户状态失败:', error);
        ElMessage.error('切换用户状态失败');
    }
};

// 表格行样式
const tableRowClassName = ({ row }) => {
    if (row.status === 'disabled') {
        return 'disabled-row';
    }
    return '';
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

// 角色标签类型
const getRoleTagType = (role) => {
    return {
        admin: 'warning',
        user: 'success',
        guest: 'info'
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

// 组件挂载时获取数据
onMounted(() => {
    fetchUsers();
});
</script>

<template>
  <NuxtLayout name="admin">
    <div class="users-page">
      <div class="page-header">
        <h1 class="page-title">用户管理</h1>
        <el-button type="primary" @click="openUserModal('add')">
          <el-icon><Plus /></el-icon> 新增用户
        </el-button>
      </div>

      <!-- 搜索和筛选区域 -->
      <el-card class="filter-card">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-input
              v-model="searchQuery"
              placeholder="搜索用户名/邮箱"
              clearable
              @input="handleSearch"
            >
              <template #append>
                <el-button @click="handleSearch">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
          </el-col>
          <el-col :span="8">
            <el-select v-model="roleFilter" placeholder="所有角色" clearable @change="applyFilters" style="width: 100%">
              <el-option label="所有角色" value="" />
              <el-option label="管理员" value="admin" />
              <el-option label="普通用户" value="user" />
              <el-option label="访客" value="guest" />
            </el-select>
          </el-col>
          <el-col :span="8">
            <el-select v-model="statusFilter" placeholder="所有状态" clearable @change="applyFilters" style="width: 100%">
              <el-option label="所有状态" value="" />
              <el-option label="正常" value="active" />
              <el-option label="已禁用" value="disabled" />
            </el-select>
          </el-col>
        </el-row>
      </el-card>

      <!-- 用户表格 -->
      <el-card class="table-card">
        <el-table
          :data="displayedUsers"
          style="width: 100%"
          border
          row-key="id"
          :row-class-name="tableRowClassName"
        >
          <el-table-column prop="id" label="用户ID" width="100" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="email" label="邮箱" />
          <el-table-column prop="role" label="角色">
            <template #default="scope">
              <el-tag
                :type="getRoleTagType(scope.row.role)"
                effect="plain"
                size="small"
              >
                {{ getRoleLabel(scope.row.role) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag
                :type="scope.row.status === 'active' ? 'success' : 'danger'"
                size="small"
              >
                {{ scope.row.status === 'active' ? '正常' : '已禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间">
            <template #default="scope">
              {{ formatDate(scope.row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column prop="lastLogin" label="最后登录">
            <template #default="scope">
              {{ formatDate(scope.row.lastLogin) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220">
            <template #default="scope">
              <el-space wrap>
                <el-button type="primary" size="small" @click="openUserModal('edit', scope.row)">
                  <el-icon><Edit /></el-icon> 编辑
                </el-button>
                <el-button type="warning" size="small" @click="resetPassword(scope.row.id)">
                  <el-icon><Key /></el-icon> 重置密码
                </el-button>
                <el-button
                  :type="scope.row.status === 'active' ? 'danger' : 'success'"
                  size="small"
                  @click="toggleUserStatus(scope.row.id, scope.row.status)"
                >
                  <el-icon v-if="scope.row.status === 'active'"><Lock /></el-icon>
                  <el-icon v-else><Unlock /></el-icon>
                  {{ scope.row.status === 'active' ? '禁用' : '启用' }}
                </el-button>
              </el-space>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页控件 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="users.length"
            layout="prev, pager, next, jumper"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>

      <!-- 用户编辑/新增弹窗 -->
      <el-dialog
        v-model="showUserModal"
        :title="modalMode === 'add' ? '新增用户' : '编辑用户'"
        width="500px"
        destroy-on-close
      >
        <el-form
          ref="userFormRef"
          :model="userForm"
          :rules="userFormRules"
          label-width="80px"
          label-position="right"
        >
          <el-form-item label="用户名" prop="username">
            <el-input v-model="userForm.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="userForm.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item v-if="modalMode === 'add'" label="初始密码" prop="password">
            <el-input v-model="userForm.password" placeholder="请输入初始密码" show-password />
          </el-form-item>
          <el-form-item label="角色" prop="role">
            <el-select v-model="userForm.role" placeholder="请选择角色" style="width: 100%">
              <el-option label="管理员" value="admin" />
              <el-option label="普通用户" value="user" />
              <el-option label="访客" value="guest" />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="closeUserModal">取消</el-button>
          <el-button type="primary" @click="saveUser">保存</el-button>
        </template>
      </el-dialog>

      <!-- 重置密码确认弹窗 -->
      <el-dialog
        v-model="showResetModal"
        title="重置密码"
        width="400px"
      >
        <span>确定要重置该用户的密码吗？系统将生成随机密码并发送邮件通知。</span>
        <template #footer>
          <el-button @click="closeResetModal">取消</el-button>
          <el-button type="primary" @click="confirmResetPassword">确认重置</el-button>
        </template>
      </el-dialog>
    </div>
  </NuxtLayout>
</template>

<style scoped>
.users-page {
  max-width: 100%;
  padding: 16px;
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

.filter-card {
  margin-bottom: 24px;
}

.table-card {
  margin-bottom: 24px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 禁用行的样式 */
:deep(.disabled-row) {
  color: #999;
  background-color: #f5f5f5;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}
</style>
