<template>
    <NuxtLayout>
        <div class="settings-page">
            <h1 class="page-title">系统设置</h1>

            <div class="tabs">
                <div
                    v-for="tab in tabs"
                    :key="tab.key"
                    class="tab-item"
                    :class="{ active: currentTab === tab.key }"
                    @click="currentTab = tab.key"
                >
                    {{ tab.label }}
                </div>
            </div>

            <div class="tab-content">
                <!-- S3 存储配置 -->
                <div v-if="currentTab === 's3'" class="tab-pane">
                    <form @submit.prevent="saveS3Config">
                        <div class="form-section">
                            <h2 class="section-title">S3 存储配置</h2>
                            <div class="form-group">
                                <label>访问密钥 ID</label>
                                <input v-model="s3Config.accessKeyId" type="text" placeholder="输入访问密钥 ID" />
                            </div>
                            <div class="form-group">
                                <label>访问密钥</label>
                                <input v-model="s3Config.secretAccessKey" type="password" placeholder="输入访问密钥" />
                            </div>
                            <div class="form-group">
                                <label>区域</label>
                                <input v-model="s3Config.region" type="text" placeholder="例如: us-east-1" />
                            </div>
                            <div class="form-group">
                                <label>存储桶名称</label>
                                <input v-model="s3Config.bucketName" type="text" placeholder="输入存储桶名称" />
                            </div>
                            <div class="form-group">
                                <label>端点 URL (可选)</label>
                                <input v-model="s3Config.endpoint" type="text" placeholder="自定义端点 URL" />
                            </div>
                            <div class="form-actions">
                                <button type="button" class="test-btn" @click="testS3Connection">测试连接</button>
                                <button type="submit" class="save-btn">保存配置</button>
                            </div>
                        </div>
                    </form>
                </div>

                <!-- SMTP 邮件配置 -->
                <div v-if="currentTab === 'smtp'" class="tab-pane">
                    <form @submit.prevent="saveSmtpConfig">
                        <div class="form-section">
                            <h2 class="section-title">SMTP 邮件配置</h2>
                            <div class="form-group">
                                <label>SMTP 服务器</label>
                                <input v-model="smtpConfig.host" type="text" placeholder="例如: smtp.gmail.com" />
                            </div>
                            <div class="form-group">
                                <label>端口</label>
                                <input v-model="smtpConfig.port" type="number" placeholder="例如: 587" />
                            </div>
                            <div class="form-group">
                                <label>用户名</label>
                                <input v-model="smtpConfig.username" type="text" placeholder="输入邮箱用户名" />
                            </div>
                            <div class="form-group">
                                <label>密码</label>
                                <input v-model="smtpConfig.password" type="password" placeholder="输入邮箱密码" />
                            </div>
                            <div class="form-group">
                                <label>发件人</label>
                                <input v-model="smtpConfig.sender" type="email" placeholder="例如: noreply@example.com" />
                            </div>
                            <div class="form-group">
                                <label>是否启用 SSL/TLS</label>
                                <select v-model="smtpConfig.secure">
                                    <option :value="true">是</option>
                                    <option :value="false">否</option>
                                </select>
                            </div>
                            <div class="form-actions">
                                <button type="button" class="test-btn" @click="testSmtpConnection">测试连接</button>
                                <button type="submit" class="save-btn">保存配置</button>
                            </div>
                        </div>
                    </form>
                </div>

                <!-- 系统阈值配置 -->
                <div v-if="currentTab === 'threshold'" class="tab-pane">
                    <form @submit.prevent="saveThresholdConfig">
                        <div class="form-section">
                            <h2 class="section-title">系统阈值配置</h2>
                            <div class="form-group">
                                <label>OCR 识别阈值 (%)</label>
                                <input
                                    v-model.number="thresholdConfig.ocrConfidence"
                                    type="number"
                                    min="0"
                                    max="100"
                                    placeholder="输入 OCR 识别置信度阈值"
                                />
                                <span class="helper-text">当 OCR 置信度低于此值时将标记为可疑结果</span>
                            </div>
                            <div class="form-group">
                                <label>异常用量告警阈值 (%)</label>
                                <input
                                    v-model.number="thresholdConfig.usageAlert"
                                    type="number"
                                    placeholder="输入异常用量百分比"
                                />
                                <span class="helper-text">当用量超过历史平均值的此百分比时触发告警</span>
                            </div>
                            <div class="form-group">
                                <label>文件存储最大容量 (MB)</label>
                                <input
                                    v-model.number="thresholdConfig.maxStorageSize"
                                    type="number"
                                    placeholder="输入最大存储容量"
                                />
                            </div>
                            <div class="form-group">
                                <label>任务队列最大长度</label>
                                <input
                                    v-model.number="thresholdConfig.maxQueueLength"
                                    type="number"
                                    placeholder="输入最大队列长度"
                                />
                            </div>
                            <div class="form-group">
                                <label>系统日志保留天数</label>
                                <input
                                    v-model.number="thresholdConfig.logRetentionDays"
                                    type="number"
                                    placeholder="输入日志保留天数"
                                />
                            </div>
                            <div class="form-actions">
                                <button type="submit" class="save-btn">保存配置</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </NuxtLayout>

</template>

<script setup>

definePageMeta({
    layout: 'admin'
});
// 定义标签页
const tabs = [
  { key: 's3', label: 'S3 存储配置' },
  { key: 'smtp', label: 'SMTP 邮件配置' },
  { key: 'threshold', label: '系统阈值配置' }
];

// 当前激活的标签页
const currentTab = ref('s3');

// S3 配置
const s3Config = ref({
  accessKeyId: '',
  secretAccessKey: '',
  region: '',
  bucketName: '',
  endpoint: ''
});

// SMTP 配置
const smtpConfig = ref({
  host: '',
  port: 587,
  username: '',
  password: '',
  sender: '',
  secure: true
});

// 系统阈值配置
const thresholdConfig = ref({
  ocrConfidence: 85,
  usageAlert: 150,
  maxStorageSize: 10240, // 10GB
  maxQueueLength: 1000,
  logRetentionDays: 90
});

// 页面加载时获取当前配置
onMounted(async () => {
  try {
    // 这里应该从 API 获取配置
    // 模拟获取的数据
    setTimeout(() => {
      s3Config.value = {
        accessKeyId: 'AKIAIOSFODNN7EXAMPLE',
        secretAccessKey: '********',
        region: 'us-west-2',
        bucketName: 'my-bucket',
        endpoint: ''
      };

      smtpConfig.value = {
        host: 'smtp.example.com',
        port: 587,
        username: 'user@example.com',
        password: '********',
        sender: 'noreply@example.com',
        secure: true
      };

      thresholdConfig.value = {
        ocrConfidence: 85,
        usageAlert: 150,
        maxStorageSize: 10240,
        maxQueueLength: 1000,
        logRetentionDays: 90
      };
    }, 500);
  } catch (error) {
    console.error('获取配置失败', error);
    // 这里应该添加错误提示
  }
});

// 保存 S3 配置
const saveS3Config = async () => {
  try {
    // 这里应该调用 API 保存配置
    console.log('保存 S3 配置', s3Config.value);
    alert('S3 配置保存成功！');
  } catch (error) {
    console.error('保存 S3 配置失败', error);
    alert('保存失败，请重试！');
  }
};

// 测试 S3 连接
const testS3Connection = async () => {
  try {
    // 这里应该调用 API 测试连接
    console.log('测试 S3 连接', s3Config.value);

    // 模拟测试结果
    setTimeout(() => {
      alert('S3 连接测试成功！');
    }, 1000);
  } catch (error) {
    console.error('S3 连接测试失败', error);
    alert('连接测试失败，请检查配置！');
  }
};

// 保存 SMTP 配置
const saveSmtpConfig = async () => {
  try {
    // 这里应该调用 API 保存配置
    console.log('保存 SMTP 配置', smtpConfig.value);
    alert('SMTP 配置保存成功！');
  } catch (error) {
    console.error('保存 SMTP 配置失败', error);
    alert('保存失败，请重试！');
  }
};

// 测试 SMTP 连接
const testSmtpConnection = async () => {
  try {
    // 这里应该调用 API 测试连接
    console.log('测试 SMTP 连接', smtpConfig.value);

    // 模拟测试结果
    setTimeout(() => {
      alert('SMTP 连接测试成功！已发送测试邮件。');
    }, 1000);
  } catch (error) {
    console.error('SMTP 连接测试失败', error);
    alert('连接测试失败，请检查配置！');
  }
};

// 保存阈值配置
const saveThresholdConfig = async () => {
  try {
    // 这里应该调用 API 保存配置
    console.log('保存阈值配置', thresholdConfig.value);
    alert('系统阈值配置保存成功！');
  } catch (error) {
    console.error('保存阈值配置失败', error);
    alert('保存失败，请重试！');
  }
};
</script>

<style scoped>
.settings-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  margin-bottom: 24px;
  font-size: 24px;
  font-weight: 500;
}

.tabs {
  display: flex;
  border-bottom: 1px solid #e8e8e8;
  margin-bottom: 24px;
}

.tab-item {
  padding: 12px 24px;
  cursor: pointer;
  transition: all 0.3s;
  border-bottom: 2px solid transparent;
}

.tab-item:hover {
  color: #1890ff;
}

.tab-item.active {
  color: #1890ff;
  border-bottom-color: #1890ff;
  font-weight: 500;
}

.tab-content {
  background-color: #fff;
  padding: 24px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 18px;
  margin-bottom: 24px;
  font-weight: 500;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}

.form-group .helper-text {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: #8c8c8c;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 32px;
}

.test-btn {
  padding: 10px 20px;
  background-color: #fff;
  border: 1px solid #1890ff;
  color: #1890ff;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.test-btn:hover {
  background-color: rgba(24, 144, 255, 0.1);
}

.save-btn {
  padding: 10px 20px;
  background-color: #1890ff;
  border: 1px solid #1890ff;
  color: white;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.save-btn:hover {
  background-color: #40a9ff;
  border-color: #40a9ff;
}
</style>
