<template>
  <NuxtLayout>
    <div class="pdf-editor-page">
      <h1 class="page-title">PDF编辑器</h1>
      
      <!-- 文件上传区域 -->
      <div class="upload-section" v-if="!currentPdf">
        <el-upload
          ref="uploadRef"
          class="upload-dragger"
          drag
          :auto-upload="false"
          :on-change="handleFileChange"
          accept=".pdf"
          :show-file-list="false"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将PDF文件拖到此处，或<em>点击上传</em>
          </div>
          <div class="el-upload__tip">
            只能上传PDF文件，文件大小不超过50MB
          </div>
        </el-upload>
      </div>

      <!-- PDF编辑器主界面 -->
      <div class="editor-container" v-if="currentPdf">
        <!-- 工具栏 -->
        <div class="toolbar">
          <div class="toolbar-section">
            <el-button-group>
              <el-button 
                :type="currentMode === 'select' ? 'primary' : 'default'"
                @click="setMode('select')"
                :icon="Pointer"
              >
                选择
              </el-button>
              <el-button 
                :type="currentMode === 'redact' ? 'primary' : 'default'"
                @click="setMode('redact')"
                :icon="Delete"
              >
                编辑
              </el-button>
              <el-button 
                :type="currentMode === 'pattern' ? 'primary' : 'default'"
                @click="setMode('pattern')"
                :icon="Search"
              >
                模式识别
              </el-button>
            </el-button-group>
          </div>

          <div class="toolbar-section">
            <el-button @click="undo" :disabled="!canUndo" :icon="RefreshLeft">撤销</el-button>
            <el-button @click="redo" :disabled="!canRedo" :icon="RefreshRight">重做</el-button>
          </div>

          <div class="toolbar-section">
            <el-button @click="clearRedactions" :icon="Clear">清除所有</el-button>
            <el-button @click="showPatternDialog = true" :icon="Setting">高级设置</el-button>
          </div>

          <div class="toolbar-section">
            <el-button type="success" @click="downloadRedactedPdf" :icon="Download">下载</el-button>
            <el-button @click="closePdf" :icon="Close">关闭</el-button>
          </div>
        </div>

        <!-- 页面导航 -->
        <div class="page-navigation">
          <el-button 
            @click="previousPage" 
            :disabled="currentPage <= 1"
            :icon="ArrowLeft"
            size="small"
          >
            上一页
          </el-button>
          
          <span class="page-info">
            第 {{ currentPage }} 页，共 {{ totalPages }} 页
          </span>
          
          <el-button 
            @click="nextPage" 
            :disabled="currentPage >= totalPages"
            :icon="ArrowRight"
            size="small"
          >
            下一页
          </el-button>

          <div class="zoom-controls">
            <el-button @click="zoomOut" :icon="ZoomOut" size="small">缩小</el-button>
            <span class="zoom-info">{{ Math.round(zoomLevel * 100) }}%</span>
            <el-button @click="zoomIn" :icon="ZoomIn" size="small">放大</el-button>
          </div>
        </div>

        <!-- PDF查看器 -->
        <div class="pdf-viewer-container">
          <div 
            ref="pdfViewerRef" 
            class="pdf-viewer"
            @mousedown="startSelection"
            @mousemove="updateSelection"
            @mouseup="endSelection"
          >
            <canvas 
              ref="pdfCanvasRef"
              :style="{ 
                transform: `scale(${zoomLevel})`,
                transformOrigin: 'top left'
              }"
            ></canvas>
            
            <!-- 选择区域叠加层 -->
            <div class="selection-overlay">
              <!-- 当前选择框 -->
              <div 
                v-if="currentSelection"
                class="selection-box current-selection"
                :style="getSelectionStyle(currentSelection)"
              ></div>
              
              <!-- 已确认的编辑区域 -->
              <div 
                v-for="(redaction, index) in redactions"
                :key="index"
                class="selection-box confirmed-redaction"
                :style="getSelectionStyle(redaction)"
                @click="selectRedaction(index)"
              >
                <div class="redaction-controls">
                  <el-button size="small" type="danger" @click="removeRedaction(index)" :icon="Delete"></el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 进度指示器 -->
        <div v-if="processing" class="processing-overlay">
          <div class="processing-content">
            <el-progress :percentage="processingProgress" :show-text="true"></el-progress>
            <p>{{ processingMessage }}</p>
          </div>
        </div>
      </div>

      <!-- 模式识别对话框 -->
      <el-dialog v-model="showPatternDialog" title="高级编辑设置" width="500px">
        <el-form :model="patternSettings" label-width="120px">
          <el-form-item label="文本模式">
            <el-checkbox-group v-model="patternSettings.textPatterns">
              <el-checkbox label="email">邮箱地址</el-checkbox>
              <el-checkbox label="phone">电话号码</el-checkbox>
              <el-checkbox label="id">身份证号</el-checkbox>
              <el-checkbox label="bank">银行卡号</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          
          <el-form-item label="自定义模式">
            <el-input
              v-model="patternSettings.customPattern"
              placeholder="输入正则表达式"
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item label="批量处理">
            <el-switch v-model="patternSettings.batchMode" active-text="启用"></el-switch>
          </el-form-item>
        </el-form>
        
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="showPatternDialog = false">取消</el-button>
            <el-button type="primary" @click="applyPatternRedaction">应用</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { 
  UploadFilled, 
  Pointer, 
  Delete, 
  Search, 
  RefreshLeft, 
  RefreshRight, 
  Clear, 
  Setting, 
  Download, 
  Close,
  ArrowLeft,
  ArrowRight,
  ZoomIn,
  ZoomOut
} from '@element-plus/icons-vue'

// 页面元数据
definePageMeta({
  layout: 'default'
})

// 使用PDF编辑功能
const pdfRedaction = usePdfRedaction()

// 响应式数据
const currentPdf = ref<any>(null)
const currentPage = ref(1)
const totalPages = ref(0)
const zoomLevel = ref(1)
const currentMode = ref<'select' | 'redact' | 'pattern'>('select')

// 选择和编辑状态
const currentSelection = ref<any>(null)
const redactions = ref<any[]>([])
const isSelecting = ref(false)
const selectionStart = ref<{ x: number, y: number } | null>(null)

// 历史记录用于撤销/重做
const history = ref<any[]>([])
const historyIndex = ref(-1)

// UI状态
const processing = ref(false)
const processingProgress = ref(0)
const processingMessage = ref('')
const showPatternDialog = ref(false)

// 模式设置
const patternSettings = ref({
  textPatterns: [],
  customPattern: '',
  batchMode: false
})

// 计算属性
const canUndo = computed(() => historyIndex.value > 0)
const canRedo = computed(() => historyIndex.value < history.value.length - 1)

// DOM引用
const uploadRef = ref()
const pdfViewerRef = ref()
const pdfCanvasRef = ref()

// 文件上传处理
const handleFileChange = async (file: any) => {
  if (!file.raw) return
  
  // 检查文件大小 (50MB)
  const maxSize = 50 * 1024 * 1024
  if (file.raw.size > maxSize) {
    ElMessage.error('文件大小不能超过50MB')
    return
  }
  
  try {
    processing.value = true
    processingMessage.value = '正在加载PDF文件...'
    processingProgress.value = 20
    
    const arrayBuffer = await file.raw.arrayBuffer()
    await loadPdf(arrayBuffer)
    
    processing.value = false
    ElMessage.success('PDF文件加载成功')
  } catch (error) {
    console.error('加载PDF失败:', error)
    ElMessage.error('加载PDF文件失败: ' + (error as Error).message)
    processing.value = false
  }
}

// 加载PDF文档
const loadPdf = async (arrayBuffer: ArrayBuffer) => {
  try {
    processingProgress.value = 40
    await pdfRedaction.loadPdf(arrayBuffer)
    
    processingProgress.value = 60
    currentPdf.value = pdfRedaction.state.document
    totalPages.value = pdfRedaction.state.totalPages
    currentPage.value = 1
    
    processingProgress.value = 80
    // 渲染第一页
    await nextTick()
    await renderCurrentPage()
    
    processingProgress.value = 100
    
    // 初始化历史记录
    history.value = [[]]
    historyIndex.value = 0
    redactions.value = []
    
  } catch (error) {
    console.error('PDF加载失败:', error)
    throw error
  }
}

// 渲染当前页面
const renderCurrentPage = async () => {
  if (!pdfCanvasRef.value || !currentPdf.value) return
  
  try {
    await pdfRedaction.renderPage(pdfCanvasRef.value, currentPage.value, zoomLevel.value)
  } catch (error) {
    console.error('渲染页面失败:', error)
    ElMessage.error('渲染PDF页面失败')
  }
}

// 设置当前模式
const setMode = (mode: string) => {
  currentMode.value = mode as any
  // 清除当前选择
  currentSelection.value = null
  isSelecting.value = false
  selectionStart.value = null
}

// 开始选择区域
const startSelection = (event: MouseEvent) => {
  if (currentMode.value !== 'redact') return
  
  event.preventDefault()
  isSelecting.value = true
  
  const canvas = pdfCanvasRef.value
  if (!canvas) return
  
  const rect = canvas.getBoundingClientRect()
  selectionStart.value = {
    x: (event.clientX - rect.left) / zoomLevel.value,
    y: (event.clientY - rect.top) / zoomLevel.value
  }
}

// 更新选择区域
const updateSelection = (event: MouseEvent) => {
  if (!isSelecting.value || !selectionStart.value) return
  
  const canvas = pdfCanvasRef.value
  if (!canvas) return
  
  const rect = canvas.getBoundingClientRect()
  const currentPos = {
    x: (event.clientX - rect.left) / zoomLevel.value,
    y: (event.clientY - rect.top) / zoomLevel.value
  }
  
  currentSelection.value = {
    x: Math.min(selectionStart.value.x, currentPos.x),
    y: Math.min(selectionStart.value.y, currentPos.y),
    width: Math.abs(currentPos.x - selectionStart.value.x),
    height: Math.abs(currentPos.y - selectionStart.value.y)
  }
}

// 结束选择区域
const endSelection = () => {
  if (!isSelecting.value || !currentSelection.value) return
  
  // 只有足够大的选择区域才添加
  if (currentSelection.value.width > 5 && currentSelection.value.height > 5) {
    const newRedaction = {
      ...currentSelection.value,
      page: currentPage.value,
      type: 'manual'
    }
    
    redactions.value.push(newRedaction)
    pdfRedaction.addRedaction(newRedaction)
    
    // 保存到历史记录
    saveToHistory()
  }
  
  currentSelection.value = null
  isSelecting.value = false
  selectionStart.value = null
}

// 获取选择框样式
const getSelectionStyle = (selection: any) => {
  return {
    left: `${selection.x * zoomLevel.value}px`,
    top: `${selection.y * zoomLevel.value}px`,
    width: `${selection.width * zoomLevel.value}px`,
    height: `${selection.height * zoomLevel.value}px`
  }
}

// 移除编辑区域
const removeRedaction = (index: number) => {
  if (index >= 0 && index < redactions.value.length) {
    redactions.value.splice(index, 1)
    pdfRedaction.removeRedaction(index)
    saveToHistory()
  }
}

// 清除所有编辑
const clearRedactions = () => {
  redactions.value = []
  pdfRedaction.clearRedactions()
  saveToHistory()
}

// 保存到历史记录
const saveToHistory = () => {
  // 截断历史记录到当前位置
  history.value = history.value.slice(0, historyIndex.value + 1)
  
  // 添加新状态
  history.value.push(JSON.parse(JSON.stringify(redactions.value)))
  historyIndex.value = history.value.length - 1
}

// 撤销操作
const undo = () => {
  if (!canUndo.value) return
  
  historyIndex.value--
  redactions.value = JSON.parse(JSON.stringify(history.value[historyIndex.value]))
  
  // 同步到PDF编辑器状态
  pdfRedaction.clearRedactions()
  redactions.value.forEach(redaction => pdfRedaction.addRedaction(redaction))
}

// 重做操作
const redo = () => {
  if (!canRedo.value) return
  
  historyIndex.value++
  redactions.value = JSON.parse(JSON.stringify(history.value[historyIndex.value]))
  
  // 同步到PDF编辑器状态
  pdfRedaction.clearRedactions()
  redactions.value.forEach(redaction => pdfRedaction.addRedaction(redaction))
}

// 上一页
const previousPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    renderCurrentPage()
  }
}

// 下一页
const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    renderCurrentPage()
  }
}

// 放大
const zoomIn = () => {
  zoomLevel.value = Math.min(zoomLevel.value * 1.2, 3)
  nextTick(() => renderCurrentPage())
}

// 缩小
const zoomOut = () => {
  zoomLevel.value = Math.max(zoomLevel.value / 1.2, 0.3)
  nextTick(() => renderCurrentPage())
}

// 应用模式编辑
const applyPatternRedaction = async () => {
  try {
    processing.value = true
    processingMessage.value = '正在应用模式识别...'
    processingProgress.value = 0
    
    const { textPatterns, customPattern, batchMode } = patternSettings.value
    
    processingProgress.value = 30
    
    // 应用模式编辑
    await pdfRedaction.applyPatternRedaction(textPatterns, customPattern)
    
    processingProgress.value = 70
    
    // 更新UI中的编辑列表
    redactions.value = [...pdfRedaction.state.redactions]
    saveToHistory()
    
    processingProgress.value = 100
    processing.value = false
    showPatternDialog.value = false
    
    const stats = pdfRedaction.getRedactionStats()
    ElMessage.success(`成功识别并标记了 ${stats.total} 个敏感信息区域`)
    
  } catch (error) {
    console.error('模式编辑失败:', error)
    ElMessage.error('模式识别失败: ' + (error as Error).message)
    processing.value = false
  }
}

// 下载编辑后的PDF
const downloadRedactedPdf = async () => {
  if (redactions.value.length === 0) {
    ElMessage.warning('没有需要编辑的区域')
    return
  }
  
  try {
    processing.value = true
    processingMessage.value = '正在生成编辑后的PDF...'
    processingProgress.value = 0
    
    processingProgress.value = 20
    
    // 应用编辑并生成新PDF
    const redactedBytes = await pdfRedaction.applyRedactions()
    
    processingProgress.value = 80
    
    // 创建下载链接
    const blob = new Blob([redactedBytes], { type: 'application/pdf' })
    const url = URL.createObjectURL(blob)
    
    const link = document.createElement('a')
    link.href = url
    link.download = `redacted_document_${Date.now()}.pdf`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    URL.revokeObjectURL(url)
    
    processingProgress.value = 100
    processing.value = false
    
    ElMessage.success('编辑后的PDF文件已下载')
    
  } catch (error) {
    console.error('下载失败:', error)
    ElMessage.error('生成编辑后的PDF失败: ' + (error as Error).message)
    processing.value = false
  }
}

// 关闭PDF
const closePdf = () => {
  currentPdf.value = null
  redactions.value = []
  history.value = []
  historyIndex.value = -1
  currentPage.value = 1
  totalPages.value = 0
  zoomLevel.value = 1
  
  // 重置PDF编辑器状态
  pdfRedaction.clearRedactions()
}

// 选择编辑区域
const selectRedaction = (index: number) => {
  console.log('Selected redaction:', index)
  // 可以在此处添加选中编辑区域的逻辑，比如高亮显示
}

// 监听缩放级别变化
watch(zoomLevel, () => {
  nextTick(() => renderCurrentPage())
})

// 监听当前页面变化
watch(currentPage, () => {
  renderCurrentPage()
})

// 初始化
onMounted(() => {
  history.value = [[]]
  historyIndex.value = 0
})
</script>

<style scoped>
.pdf-editor-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px;
}

.page-title {
  text-align: center;
  color: #333;
  font-size: 32px;
  margin-bottom: 30px;
}

.upload-section {
  max-width: 600px;
  margin: 0 auto;
}

.upload-dragger {
  width: 100%;
}

.editor-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #fafafa;
  border-bottom: 1px solid #e6e6e6;
  flex-wrap: wrap;
  gap: 10px;
}

.toolbar-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-navigation {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 10px 20px;
  background: #f9f9f9;
  border-bottom: 1px solid #e6e6e6;
  gap: 15px;
}

.page-info {
  color: #666;
  font-size: 14px;
}

.zoom-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.zoom-info {
  color: #666;
  font-size: 14px;
  min-width: 50px;
  text-align: center;
}

.pdf-viewer-container {
  height: calc(100vh - 300px);
  overflow: auto;
  position: relative;
  background: #e9e9e9;
}

.pdf-viewer {
  position: relative;
  display: flex;
  justify-content: center;
  padding: 20px;
  min-height: 100%;
}

.selection-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.selection-box {
  position: absolute;
  border: 2px solid;
  pointer-events: auto;
}

.current-selection {
  border-color: #409eff;
  background: rgba(64, 158, 255, 0.1);
}

.confirmed-redaction {
  border-color: #f56c6c;
  background: rgba(245, 108, 108, 0.3);
  cursor: pointer;
}

.confirmed-redaction:hover {
  background: rgba(245, 108, 108, 0.5);
}

.redaction-controls {
  position: absolute;
  top: -30px;
  right: 0;
  opacity: 0;
  transition: opacity 0.2s;
}

.confirmed-redaction:hover .redaction-controls {
  opacity: 1;
}

.processing-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.processing-content {
  background: white;
  padding: 30px;
  border-radius: 8px;
  min-width: 300px;
  text-align: center;
}

.processing-content p {
  margin-top: 15px;
  color: #666;
}

@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .toolbar-section {
    justify-content: center;
  }
  
  .page-navigation {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>