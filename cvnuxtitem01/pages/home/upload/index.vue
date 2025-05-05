<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import type { UploadFile } from 'element-plus'


// 上传状态
const uploadStatus = ref<'idle' | 'uploading' | 'success' | 'error'>('idle'); // idle, uploading, success, error
const uploadProgress = ref<number>(0);
const selectedFile = ref<File | null>(null);
const previewUrl = ref<string>('');
const uploadResults = ref<any[]>([]);

// 表计类型选择
const meterTypes = ['水表', '电表', '气表'];
const selectedMeterType = ref('水表');

// 拖拽区域状态
const isDragging = ref(false);

// 模拟识别结果
const demoResults = [
    { id: 1, type: '水表', reading: 123.5, confidence: 98.5, time: '2023-05-20 14:30:45', cost: 78.4 },
    { id: 2, type: '电表', reading: 568.7, confidence: 97.2, time: '2023-05-18 10:15:22', cost: 195.6 },
    { id: 3, type: '气表', reading: 89.2, confidence: 95.8, time: '2023-05-15 09:40:18', cost: 167.3 },
];

// 处理文件选择
const handleFileSelect = (uploadFile: UploadFile, uploadFiles: UploadFile[]) => {
    const rawFile = uploadFile.raw
    if (!rawFile || !rawFile.type.startsWith('image/')) {
        ElMessage.error('请上传图片文件')
        return
    }

    // 示例：预览图
    previewUrl.value = URL.createObjectURL(rawFile)
    selectedFile.value = rawFile
}

// 处理拖拽
const handleDragOver = (event: DragEvent) => {
    event.preventDefault();
    isDragging.value = true;
};

const handleDragLeave = () => {
    isDragging.value = false;
};

const handleDrop = (event: DragEvent) => {
    event.preventDefault();
    isDragging.value = false;

    if (event.dataTransfer?.files.length) {
        processSelectedFile(event.dataTransfer.files[0]);
    }
};

// 处理拍照
const handleCapture = () => {
    // 这里实际项目中需要调用摄像头API
    ElMessage.info('摄像头功能将在实际项目中实现');
};

// 处理选择的文件
const processSelectedFile = (file: File) => {
    selectedFile.value = file;

    // 创建预览URL
    if (previewUrl.value) {
        URL.revokeObjectURL(previewUrl.value);
    }
    previewUrl.value = URL.createObjectURL(file);
};

// 上传文件
const uploadFile = () => {
    if (!selectedFile.value) {
        ElMessage.warning('请先选择文件');
        return;
    }

    uploadStatus.value = 'uploading';
    uploadProgress.value = 0;

    // 模拟上传进度
    const interval = setInterval(() => {
        uploadProgress.value += 5;

        if (uploadProgress.value >= 100) {
            clearInterval(interval);
            uploadStatus.value = 'success';

            // 模拟获取结果
            setTimeout(() => {
                // 根据选择的表计类型过滤结果
                uploadResults.value = demoResults.filter(item => item.type === selectedMeterType.value);
            }, 500);
        }
    }, 200);
};

// 重置上传
const resetUpload = () => {
    uploadStatus.value = 'idle';
    uploadProgress.value = 0;
    selectedFile.value = null;

    if (previewUrl.value) {
        URL.revokeObjectURL(previewUrl.value);
        previewUrl.value = '';
    }

    uploadResults.value = [];
};

// 上传文件列表，用于Element Plus Upload组件
const fileList = ref([]);

definePageMeta({
    layout: "home"
})
</script>

<template>
    <nuxt-layout>
        <div class="upload-page">
            <el-card>
                <template #header>
                    <h2 class="page-title">表计识读上传</h2>
                </template>

                <div class="upload-container">
                    <!-- 上传区域 -->
                    <div class="upload-section">
                        <div class="meter-type-selector">
                            <h3>选择表计类型</h3>
                            <el-radio-group v-model="selectedMeterType" size="large">
                                <el-radio-button v-for="type in meterTypes" :key="type" :label="type">
                                    {{ type }}
                                </el-radio-button>
                            </el-radio-group>
                        </div>

                        <el-upload
                            class="upload-area"
                            :class="{ 'has-file': previewUrl }"
                            drag
                            action="#"
                            :auto-upload="false"
                            :show-file-list="false"
                            :file-list="fileList"
                            :on-change="handleFileSelect"
                        >
                            <div v-if="!previewUrl">
                                <el-icon class="el-icon--upload"><ElUpload/></el-icon>
                                <div class="el-upload__text">拖拽图片到此处或 <em>点击上传</em></div>
                                <div class="el-upload__tip">
                                    只能上传 jpg/png 格式图片，且不超过 10MB
                                </div>
                            </div>


                            <template v-else>
                                <div class="preview-container">
                                    <img :src="previewUrl" class="preview-image" alt="预览图">
                                    <el-button
                                        class="remove-btn"
                                        circle
                                        type="danger"
                                        size="small"
                                        @click.stop="resetUpload"
                                    >
                                        <ElIconDelete/>
                                    </el-button>
                                </div>
                            </template>
                        </el-upload>

                        <div class="upload-actions">
                            <el-button
                                type="primary"
                                @click="handleCapture"
                            >
                                拍照上传
                            </el-button>
                            <el-button
                                type="success"
                                :disabled="!selectedFile || uploadStatus === 'uploading'"
                                @click="uploadFile"
                            >
                                <ElUpload/>
                                开始识读
                            </el-button>
                        </div>
                    </div>

                    <!-- 进度和结果区域 -->
                    <div class="results-section">
                        <div v-if="uploadStatus === 'uploading'" class="progress-container">
                            <h3>正在处理中...</h3>
                            <el-progress
                                :percentage="uploadProgress"
                                :stroke-width="10"
                                status="success"
                            ></el-progress>
                        </div>

                        <div v-else-if="uploadStatus === 'success' && uploadResults.length > 0" class="results-table">
                            <h3>识读结果</h3>
                            <el-table :data="uploadResults" style="width: 100%" border>
                                <el-table-column prop="type" label="表计类型"></el-table-column>
                                <el-table-column prop="reading" label="读数"></el-table-column>
                                <el-table-column prop="confidence" label="置信度">
                                    <template #default="scope">
                                        {{ scope.row.confidence }}%
                                    </template>
                                </el-table-column>
                                <el-table-column prop="time" label="识读时间"></el-table-column>
                                <el-table-column prop="cost" label="预估费用">
                                    <template #default="scope">
                                        ¥{{ scope.row.cost.toFixed(2) }}
                                    </template>
                                </el-table-column>
                            </el-table>

                            <div class="result-actions">
                                <el-button @click="resetUpload">
                                    上传新图片
                                </el-button>
                                <el-button type="primary">
                                    保存结果
                                </el-button>
                            </div>
                        </div>

                        <div v-else-if="uploadStatus === 'error'" class="error-container">
                            <el-result
                                icon="error"
                                title="处理失败"
                                sub-title="图片处理过程中出现错误，请重试或联系管理员。"
                            >
                                <template #extra>
                                    <el-button type="primary" @click="resetUpload">重新上传</el-button>
                                </template>
                            </el-result>
                        </div>

                        <div v-else-if="uploadStatus === 'idle' && !selectedFile" class="instructions">
                            <el-alert
                                title="使用说明"
                                type="info"
                                :closable="false"
                                show-icon
                            ></el-alert>
                            <el-card class="instruction-card" shadow="never">
                                <el-descriptions direction="vertical" :column="1" border>
                                    <el-descriptions-item label="图片要求">
                                        <ul>
                                            <li>请选择清晰的表计照片</li>
                                            <li>确保表盘/数字区域在画面中居中</li>
                                            <li>避免强光反射和阴影</li>
                                            <li>支持的格式：JPG、PNG、JPEG</li>
                                            <li>单张图片大小不超过10MB</li>
                                        </ul>
                                    </el-descriptions-item>
                                </el-descriptions>
                            </el-card>

                            <div class="example-images">
                                <h4>示例图片</h4>
                                <el-row :gutter="20">
                                    <el-col :span="8">
                                        <el-card shadow="hover">
                                            <div class="example-img water">水表</div>
                                        </el-card>
                                    </el-col>
                                    <el-col :span="8">
                                        <el-card shadow="hover">
                                            <div class="example-img electric">电表</div>
                                        </el-card>
                                    </el-col>
                                    <el-col :span="8">
                                        <el-card shadow="hover">
                                            <div class="example-img gas">气表</div>
                                        </el-card>
                                    </el-col>
                                </el-row>
                            </div>
                        </div>
                    </div>
                </div>
            </el-card>
        </div>
    </nuxt-layout>
</template>

<style scoped>
.upload-page {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
}

.page-title {
    text-align: center;
    margin-bottom: 0;
    color: #333;
    font-weight: bold;
}

.upload-container {
    display: flex;
    gap: 30px;
}

.upload-section {
    flex: 1;
    min-width: 350px;
}

.results-section {
    flex: 1.5;
    padding: 20px 0;
}

/* 表计类型选择器 */
.meter-type-selector {
    margin-bottom: 20px;
}

.meter-type-selector h3 {
    margin-bottom: 15px;
    color: #333;
}

/* 上传区域 */
.upload-area {
    margin-bottom: 20px;
    width: 100%;
}

.upload-area.has-file :deep(.el-upload) {
    width: 100%;
}

.upload-area :deep(.el-upload) {
    width: 100%;
}

.upload-area :deep(.el-upload-dragger) {
    width: 100%;
    height: 250px;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

/* 预览区域 */
.preview-container {
    width: 100%;
    height: 100%;
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
}

.preview-image {
    max-width: 100%;
    max-height: 200px;
    border-radius: 5px;
}

.remove-btn {
    position: absolute;
    top: -10px;
    right: -10px;
}

/* 上传按钮 */
.upload-actions {
    display: flex;
    gap: 15px;
    justify-content: center;
}

/* 进度条 */
.progress-container {
    text-align: center;
    padding: 20px 0;
}

.progress-container h3 {
    margin-bottom: 20px;
    color: #333;
}

/* 结果表格 */
.results-table h3 {
    margin-bottom: 20px;
    color: #333;
}

.result-actions {
    display: flex;
    justify-content: flex-end;
    gap: 15px;
    margin-top: 20px;
}

/* 使用说明 */
.instructions {
    padding: 20px 0;
}

.instruction-card {
    margin: 15px 0;
}

.instructions ul {
    margin: 10px 0;
    padding-left: 20px;
}

.instructions li {
    margin-bottom: 8px;
    color: #555;
}

.example-images h4 {
    margin: 20px 0 15px;
    color: #333;
}

.example-img {
    height: 120px;
    border-radius: 5px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
}

.example-img.water {
    background-color: rgba(33, 150, 243, 0.1);
    color: #2196F3;
}

.example-img.electric {
    background-color: rgba(255, 152, 0, 0.1);
    color: #FF9800;
}

.example-img.gas {
    background-color: rgba(139, 195, 74, 0.1);
    color: #8BC34A;
}

@media (max-width: 768px) {
    .upload-container {
        flex-direction: column;
    }

    .upload-actions {
        flex-direction: column;
    }
}
</style>
