<script setup lang="ts">
import { ElMessage } from 'element-plus';
import type { UploadFile } from 'element-plus'
import {useUploadService} from "~/services/home/upload";
import type {UploadParams} from "~/types/home/upload/type";

const uploadService = useUploadService();

// 上传状态
const uploadStatus = ref<'idle' | 'uploading' | 'success' | 'error'>('idle'); // idle, uploading, success, error
const uploadProgress = ref<number>(0);
const selectedFile = ref<File | null>(null);
const previewUrl = ref<string>('');
const uploadResults = ref<any>();
const dialogVisible = ref(false)
const locationForm = reactive({
    location: ''
})
const rules = {
    location: [
        { required: true, message: '请输入位置', trigger: 'blur' }
    ]
}
const formRef = ref()
// 表计类型选择
const meterTypes = ['水表', '电表', '气表'];
const selectedMeterType = ref('水表');
const lock = ref(false)

// 处理文件选择
const handleFileSelect = (uploadFile: UploadFile, _: UploadFile[]) => {
    const rawFile = uploadFile.raw
    if (!rawFile || !rawFile.type.startsWith('image/')) {
        ElMessage.error('请上传图片文件')
        return
    }

    // 示例：预览图
    previewUrl.value = URL.createObjectURL(rawFile)
    selectedFile.value = rawFile
}

const openDialog = () => {
    dialogVisible.value = true
}
const confirmSave = () => {
    formRef.value.validate((valid: boolean) => {
        if (valid) {
            // 你的保存逻辑放这里
            ElMessage.success(`保存成功，位置是：${locationForm.location}`)
            locationForm.location = ''
            formRef.value?.resetFields?.()
            dialogVisible.value = false
            lock.value = true
        }
    })
}

// 处理拍照
const handleCapture = () => {
    // 这里实际项目中需要调用摄像头API
    ElMessage.info('摄像头功能将在实际项目中实现');
};

// 上传文件
const uploadFile = async () => {
    if (!selectedFile.value) {
        ElMessage.warning('请先选择文件');
        return;
    }

    uploadStatus.value = 'uploading';
    uploadProgress.value = 0;

    try {
        // 准备上传参数
        const uploadParams: UploadParams = {
            file: selectedFile.value,
            type: selectedMeterType.value === '水表' ? 'watter' :
                selectedMeterType.value === '电表' ? 'electricity' :  'electricity'
        };

        // 模拟上传进度（实际项目中可能需要使用 axios 的上传进度事件）
        const progressInterval = setInterval(() => {
            if (uploadProgress.value < 90) {
                uploadProgress.value += 5;
            }
        }, 200);

        // 调用上传服务
        const response = await uploadService.apiUpload(uploadParams);
        console.log(response.data);
        // 清除进度模拟
        clearInterval(progressInterval);
        uploadProgress.value = 100;
        uploadStatus.value = 'success';

        // 使用返回的结果替代模拟结果
        if (response && response.data) {
            // 检查响应数据的格式并统一处理
            lock.value = false
            if (Array.isArray(response.data)) {
                uploadResults.value = response.data;
            } else if (typeof response.data === 'object') {
                uploadResults.value = [response.data];
            } else {
                // 如果后端返回的数据格式不正确，显示错误
                ElMessage.error('返回数据格式错误');
                uploadStatus.value = 'error';
            }
        } else {
            // 如果后端没有返回有效数据，显示错误
            ElMessage.error('未获取到有效数据');
            uploadStatus.value = 'error';
        }

    } catch (error) {
        console.error('上传失败:', error);
        uploadStatus.value = 'error';
        ElMessage.error('上传处理失败，请重试');
    }
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
                                <el-radio-button v-for="type in meterTypes" :key="type" :value="type">
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
                                        <el-icon><ElIconDelete/></el-icon>
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

                        <div v-else-if="uploadStatus === 'success' && uploadResults" class="results-table">
                            <h3>识读结果</h3>
                            <el-table :data="Array.isArray(uploadResults) ? uploadResults : [uploadResults]" style="width: 100%" border>
                                <el-table-column prop="type" label="表计类型">
                                    <template #default="{ row }">
                                        {{selectedMeterType}}
                                    </template>
                                </el-table-column>
                                <el-table-column prop="value" label="读数"></el-table-column>
                                <el-table-column prop="confidence" label="置信度">
                                    <template #default="scope">
                                        90%
                                    </template>
                                </el-table-column>
                                <el-table-column prop="shotTime" label="识读时间">
                                    <template #default="scope">
                                        {{ formatDateTime(scope.row.shotTime) }}
                                    </template>
                                </el-table-column>
                                <el-table-column prop="cost" label="预估费用">
                                    <template #default="scope">
                                        ¥{{ scope.row.cost ? scope.row.cost.toFixed(2) : '0.00' }}
                                    </template>
                                </el-table-column>
                            </el-table>

                            <div class="result-actions">
                                <el-button @click="resetUpload">
                                    上传新图片
                                </el-button>
                                <!-- 保存识别结果按钮 -->
                                <el-button
                                    type="primary"
                                    @click="openDialog"
                                    :disabled="lock"
                                >
                                    保存识别结果
                                </el-button>

                                <!-- 填写位置的弹窗表单 -->
                                <el-dialog v-model="dialogVisible" title="保存识别结果">
                                    <el-form :model="locationForm" :rules="rules" ref="formRef" label-width="80px">
                                        <el-form-item label="位置" prop="location">
                                            <el-input v-model="locationForm.location" placeholder="请输入表计所在位置" />
                                        </el-form-item>
                                    </el-form>

                                    <template #footer>
                                        <el-button @click="dialogVisible = false">取消</el-button>
                                        <el-button type="primary" @click="confirmSave">确认保存</el-button>
                                    </template>
                                </el-dialog>
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
