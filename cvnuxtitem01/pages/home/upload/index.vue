<script setup lang="ts">
import { ref } from 'vue';

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
const handleFileSelect = (event: Event) => {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
        processSelectedFile(input.files[0]);
    }
};

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
    alert('摄像头功能将在实际项目中实现');
};

// 处理选择的文件
const processSelectedFile = (file: File) => {
    // 检查文件类型
    if (!file.type.startsWith('image/')) {
        alert('请上传图片文件');
        return;
    }

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
        alert('请先选择文件');
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


definePageMeta({
    layout: "home"
})
</script>

<template>
    <nuxt-layout>
        <div class="upload-page">
            <h1 class="page-title">表计识读上传</h1>

            <div class="upload-container">
                <!-- 上传区域 -->
                <div class="upload-section">
                    <div class="meter-type-selector">
                        <h3>选择表计类型</h3>
                        <div class="meter-buttons">
                            <button
                                v-for="type in meterTypes"
                                :key="type"
                                :class="['meter-button', { active: selectedMeterType === type }]"
                                @click="selectedMeterType = type"
                            >
                                {{ type }}
                            </button>
                        </div>
                    </div>

                    <div
                        class="upload-area"
                        :class="{ 'dragging': isDragging, 'has-file': previewUrl }"
                        @dragover="handleDragOver"
                        @dragleave="handleDragLeave"
                        @drop="handleDrop"
                    >
                        <div v-if="!previewUrl" class="upload-placeholder">
                            <div class="upload-icon">📷</div>
                            <p>拖拽图片到此处或点击上传</p>
                            <input
                                type="file"
                                class="file-input"
                                accept="image/*"
                                @change="handleFileSelect"
                            >
                        </div>

                        <div v-else class="preview-container">
                            <img :src="previewUrl" class="preview-image" alt="预览图">
                            <button class="remove-btn" @click="resetUpload">×</button>
                        </div>
                    </div>

                    <div class="upload-actions">
                        <button class="upload-btn capture" @click="handleCapture">
                            <span class="btn-icon">📷</span>
                            拍照上传
                        </button>
                        <button
                            class="upload-btn submit"
                            :disabled="!selectedFile || uploadStatus === 'uploading'"
                            @click="uploadFile"
                        >
                            <span class="btn-icon">📤</span>
                            开始识读
                        </button>
                    </div>
                </div>

                <!-- 进度和结果区域 -->
                <div class="results-section">
                    <div v-if="uploadStatus === 'uploading'" class="progress-container">
                        <h3>正在处理中...</h3>
                        <div class="progress-bar">
                            <div class="progress-fill" :style="{ width: `${uploadProgress}%` }"></div>
                        </div>
                        <p class="progress-text">{{ uploadProgress }}%</p>
                    </div>

                    <div v-else-if="uploadStatus === 'success' && uploadResults.length > 0" class="results-table">
                        <h3>识读结果</h3>
                        <table>
                            <thead>
                            <tr>
                                <th>表计类型</th>
                                <th>读数</th>
                                <th>置信度</th>
                                <th>识读时间</th>
                                <th>预估费用</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr v-for="result in uploadResults" :key="result.id">
                                <td>{{ result.type }}</td>
                                <td>{{ result.reading }}</td>
                                <td>{{ result.confidence }}%</td>
                                <td>{{ result.time }}</td>
                                <td>¥{{ result.cost.toFixed(2) }}</td>
                            </tr>
                            </tbody>
                        </table>

                        <div class="result-actions">
                            <button class="action-btn" @click="resetUpload">
                                上传新图片
                            </button>
                            <button class="action-btn primary">
                                保存结果
                            </button>
                        </div>
                    </div>

                    <div v-else-if="uploadStatus === 'error'" class="error-container">
                        <h3>处理失败</h3>
                        <p>图片处理过程中出现错误，请重试或联系管理员。</p>
                        <button class="action-btn" @click="resetUpload">重新上传</button>
                    </div>

                    <div v-else-if="uploadStatus === 'idle' && !selectedFile" class="instructions">
                        <h3>使用说明</h3>
                        <ul>
                            <li>请选择清晰的表计照片</li>
                            <li>确保表盘/数字区域在画面中居中</li>
                            <li>避免强光反射和阴影</li>
                            <li>支持的格式：JPG、PNG、JPEG</li>
                            <li>单张图片大小不超过10MB</li>
                        </ul>

                        <div class="example-images">
                            <h4>示例图片</h4>
                            <div class="image-examples">
                                <div class="example">
                                    <div class="example-img water">水表</div>
                                </div>
                                <div class="example">
                                    <div class="example-img electric">电表</div>
                                </div>
                                <div class="example">
                                    <div class="example-img gas">气表</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
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
    margin-bottom: 30px;
    color: #333;
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
    background-color: white;
    border-radius: 10px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 表计类型选择器 */
.meter-type-selector {
    margin-bottom: 20px;
}

.meter-type-selector h3 {
    margin-bottom: 15px;
    color: #333;
}

.meter-buttons {
    display: flex;
    gap: 10px;
}

.meter-button {
    padding: 10px 20px;
    border: 1px solid #ddd;
    background: white;
    border-radius: 5px;
    cursor: pointer;
    flex: 1;
    font-size: 16px;
    transition: all 0.3s ease;
}

.meter-button.active {
    background-color: #1976D2;
    color: white;
    border-color: #1976D2;
}

/* 上传区域 */
.upload-area {
    border: 2px dashed #ddd;
    border-radius: 10px;
    padding: 30px;
    text-align: center;
    background-color: #f9f9f9;
    position: relative;
    min-height: 250px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 20px;
    transition: all 0.3s ease;
}

.upload-area.dragging {
    border-color: #1976D2;
    background-color: rgba(25, 118, 210, 0.05);
}

.upload-area.has-file {
    border-style: solid;
    border-color: #4CAF50;
    background-color: white;
}

.upload-placeholder {
    width: 100%;
}

.upload-icon {
    font-size: 48px;
    margin-bottom: 15px;
    color: #666;
}

.upload-placeholder p {
    margin-bottom: 15px;
    color: #666;
}

.file-input {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    opacity: 0;
    cursor: pointer;
}

/* 预览区域 */
.preview-container {
    width: 100%;
    height: 100%;
    position: relative;
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
    width: 25px;
    height: 25px;
    border-radius: 50%;
    background-color: #F44336;
    color: white;
    border: none;
    font-size: 16px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
}

/* 上传按钮 */
.upload-actions {
    display: flex;
    gap: 15px;
}

.upload-btn {
    flex: 1;
    padding: 12px;
    border-radius: 5px;
    border: none;
    font-size: 16px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;
}

.upload-btn.capture {
    background-color: #673AB7;
    color: white;
}

.upload-btn.submit {
    background-color: #2196F3;
    color: white;
}

.upload-btn:disabled {
    background-color: #ddd;
    color: #999;
    cursor: not-allowed;
}

.btn-icon {
    margin-right: 8px;
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

.progress-bar {
    height: 10px;
    background-color: #f0f0f0;
    border-radius: 5px;
    overflow: hidden;
    margin-bottom: 10px;
}

.progress-fill {
    height: 100%;
    background-color: #4CAF50;
    border-radius: 5px;
    transition: width 0.3s ease;
}

.progress-text {
    font-size: 14px;
    color: #666;
}

/* 结果表格 */
.results-table h3 {
    margin-bottom: 20px;
    color: #333;
}

table {
    width: 100%;
    border-collapse: collapse;
    margin-bottom: 20px;
}

th, td {
    padding: 12px 15px;
    text-align: left;
    border-bottom: 1px solid #eee;
}

th {
    background-color: #f5f5f5;
    font-weight: bold;
    color: #333;
}

tr:hover {
    background-color: #f9f9f9;
}

.result-actions {
    display: flex;
    justify-content: flex-end;
    gap: 15px;
    margin-top: 20px;
}

.action-btn {
    padding: 10px 20px;
    border-radius: 5px;
    border: 1px solid #ddd;
    background-color: white;
    cursor: pointer;
    font-size: 14px;
    transition: all 0.3s ease;
}

.action-btn.primary {
    background-color: #4CAF50;
    color: white;
    border-color: #4CAF50;
}

/* 错误提示 */
.error-container {
    text-align: center;
    padding: 30px;
    color: #F44336;
}

.error-container h3 {
    margin-bottom: 15px;
}

.error-container p {
    margin-bottom: 20px;
}

/* 使用说明 */
.instructions {
    padding: 20px 0;
}

.instructions h3 {
    margin-bottom: 20px;
    color: #333;
}

.instructions ul {
    margin-bottom: 30px;
    padding-left: 20px;
}

.instructions li {
    margin-bottom: 10px;
    color: #555;
}

.example-images h4 {
    margin-bottom: 15px;
    color: #333;
}

.image-examples {
    display: flex;
    gap: 20px;
}

.example {
    flex: 1;
    text-align: center;
}

.example-img {
    height: 120px;
    background-color: #f0f0f0;
    border-radius: 5px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
    color: #666;
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

    .meter-buttons {
        flex-direction: column;
    }

    .upload-actions {
        flex-direction: column;
    }

    .image-examples {
        flex-direction: column;
    }

    .example-img {
        margin-bottom: 10px;
    }
}
</style>
