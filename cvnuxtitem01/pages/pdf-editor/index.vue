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
        
        <div class="demo-section">
          <el-divider>或者</el-divider>
          <el-button 
            type="info" 
            @click="generateSamplePdf"
            :loading="generatingSample"
          >
            📄 下载示例PDF文件
          </el-button>
          <p class="demo-tip">
            下载包含敏感信息的示例PDF文件，用于测试编辑功能
          </p>
        </div>
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
            <el-button @click="showHelpDialog = true" :icon="QuestionFilled">帮助</el-button>
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
      <el-dialog v-model="showPatternDialog" title="高级编辑设置" width="600px">
        <el-form :model="patternSettings" label-width="120px">
          <el-form-item label="内置模式">
            <el-checkbox-group v-model="patternSettings.textPatterns">
              <div class="pattern-grid">
                <el-checkbox label="email">📧 邮箱地址</el-checkbox>
                <el-checkbox label="phone">📱 电话号码</el-checkbox>
                <el-checkbox label="id">🆔 身份证号</el-checkbox>
                <el-checkbox label="bank">💳 银行卡号</el-checkbox>
                <el-checkbox label="creditCard">💰 信用卡号</el-checkbox>
                <el-checkbox label="ipAddress">🌐 IP地址</el-checkbox>
                <el-checkbox label="url">🔗 网址链接</el-checkbox>
                <el-checkbox label="address">📍 地址信息</el-checkbox>
              </div>
            </el-checkbox-group>
          </el-form-item>
          
          <el-form-item label="自定义模式">
            <el-input
              v-model="patternSettings.customPattern"
              placeholder="输入正则表达式，例如: \\d{4}-\\d{4}-\\d{4}-\\d{4}"
              clearable
            >
              <template #append>
                <el-button @click="validatePattern">验证</el-button>
              </template>
            </el-input>
            <div class="pattern-help">
              <small>支持标准正则表达式。例如匹配手机号: 1[3-9]\\d{9}</small>
            </div>
          </el-form-item>

          <el-form-item label="批量处理">
            <el-switch 
              v-model="patternSettings.batchMode" 
              active-text="处理所有页面"
              inactive-text="仅当前页面"
            ></el-switch>
          </el-form-item>

          <el-form-item label="预览">
            <div class="pattern-preview">
              <div v-if="patternSettings.textPatterns.length > 0">
                <strong>将查找以下类型的敏感信息:</strong>
                <ul>
                  <li v-for="pattern in patternSettings.textPatterns" :key="pattern">
                    {{ getPatternDescription(pattern) }}
                  </li>
                </ul>
              </div>
              <div v-if="patternSettings.customPattern">
                <strong>自定义模式:</strong> {{ patternSettings.customPattern }}
              </div>
            </div>
          </el-form-item>
        </el-form>
        
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="showPatternDialog = false">取消</el-button>
            <el-button 
              type="primary" 
              @click="applyPatternRedaction"
              :disabled="patternSettings.textPatterns.length === 0 && !patternSettings.customPattern"
            >
              应用模式识别
            </el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 帮助对话框 -->
      <el-dialog v-model="showHelpDialog" title="使用帮助" width="90%" fullscreen>
        <PDFEditorHelp />
        <template #footer>
          <span class="dialog-footer">
            <el-button type="primary" @click="showHelpDialog = false">关闭</el-button>
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
  ZoomOut,
  QuestionFilled
} from '@element-plus/icons-vue'
import { 
  getBuiltInPatterns, 
  validateCustomPattern,
  formatFileSize,
  isValidPdfFile,
  downloadFile,
  createRedactionStats,
  handleKeyboardShortcut,
  PDFEditorShortcuts
} from '~/utils/pdfUtils'
import { downloadSamplePDF } from '~/utils/samplePdf'

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
const showHelpDialog = ref(false)
const generatingSample = ref(false)

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
  
  // 验证文件
  const validation = isValidPdfFile(file.raw)
  if (!validation.valid) {
    ElMessage.error(validation.error!)
    return
  }
  
  try {
    processing.value = true
    processingMessage.value = '正在加载PDF文件...'
    processingProgress.value = 20
    
    const arrayBuffer = await file.raw.arrayBuffer()
    await loadPdf(arrayBuffer)
    
    processing.value = false
    ElMessage.success(`PDF文件加载成功 (${formatFileSize(file.raw.size)})`)
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
    
    // 验证自定义模式
    if (customPattern) {
      const validation = validateCustomPattern(customPattern)
      if (!validation.valid) {
        ElMessage.error(validation.error!)
        processing.value = false
        return
      }
    }
    
    // 应用模式编辑
    await pdfRedaction.applyPatternRedaction(textPatterns, customPattern)
    
    processingProgress.value = 70
    
    // 更新UI中的编辑列表
    redactions.value = [...pdfRedaction.state.redactions]
    saveToHistory()
    
    processingProgress.value = 100
    processing.value = false
    showPatternDialog.value = false
    
    const stats = createRedactionStats(redactions.value)
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
    
    // 使用工具函数下载文件
    const blob = new Blob([redactedBytes], { type: 'application/pdf' })
    const filename = `redacted_document_${Date.now()}.pdf`
    downloadFile(blob, filename)
    
    processingProgress.value = 100
    processing.value = false
    
    ElMessage.success('编辑后的PDF文件已下载')
    
  } catch (error) {
    console.error('下载失败:', error)
    ElMessage.error('生成编辑后的PDF失败: ' + (error as Error).message)
    processing.value = false
  }
}

// 键盘快捷键处理
const handleKeydown = (event: KeyboardEvent) => {
  const shortcuts = {
    [PDFEditorShortcuts.UNDO]: undo,
    [PDFEditorShortcuts.REDO]: redo,
    [PDFEditorShortcuts.DELETE_SELECTED]: () => {
      // 如果有选中的编辑区域，删除它
      // 这里可以添加选中编辑区域的逻辑
    },
    [PDFEditorShortcuts.ZOOM_IN]: zoomIn,
    [PDFEditorShortcuts.ZOOM_OUT]: zoomOut,
    [PDFEditorShortcuts.RESET_ZOOM]: () => {
      zoomLevel.value = 1
      nextTick(() => renderCurrentPage())
    },
    [PDFEditorShortcuts.NEXT_PAGE]: nextPage,
    [PDFEditorShortcuts.PREV_PAGE]: previousPage,
    [PDFEditorShortcuts.SAVE]: downloadRedactedPdf
  }
  
  handleKeyboardShortcut(event, shortcuts)
}

// 获取模式描述
const getPatternDescription = (patternName: string): string => {
  const patterns = getBuiltInPatterns()
  return patterns[patternName]?.description || patternName
}

// 验证自定义模式
const validatePattern = () => {
  if (!patternSettings.value.customPattern) {
    ElMessage.warning('请输入自定义模式')
    return
  }
  
  const validation = validateCustomPattern(patternSettings.value.customPattern)
  if (validation.valid) {
    ElMessage.success('模式验证成功')
  } else {
    ElMessage.error(validation.error!)
  }
}

// 生成示例PDF
const generateSamplePdf = async () => {
  try {
    generatingSample.value = true
    await downloadSamplePDF()
    ElMessage.success('示例PDF文件已下载，请将其拖拽到上传区域进行测试')
  } catch (error) {
    console.error('生成示例PDF失败:', error)
    ElMessage.error('生成示例PDF失败')
  } finally {
    generatingSample.value = false
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
  
  // 添加键盘事件监听
  document.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  // 清理键盘事件监听
  document.removeEventListener('keydown', handleKeydown)
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

.demo-section {
  text-align: center;
  margin-top: 20px;
}

.demo-tip {
  margin-top: 10px;
  color: #909399;
  font-size: 14px;
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

.pattern-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 10px;
}

.pattern-help {
  margin-top: 8px;
  color: #909399;
}

.pattern-preview {
  background: #f9f9f9;
  padding: 15px;
  border-radius: 4px;
  border: 1px solid #e6e6e6;
}

.pattern-preview ul {
  margin: 8px 0 0 20px;
  color: #666;
}

.pattern-preview ul li {
  margin: 4px 0;
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