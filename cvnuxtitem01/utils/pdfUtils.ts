// Utility functions for PDF redaction

export interface RedactionPattern {
  name: string
  pattern: RegExp
  description: string
  category: 'pii' | 'financial' | 'contact' | 'custom'
}

export const getBuiltInPatterns = (): Record<string, RedactionPattern> => {
  return {
    email: {
      name: 'email',
      pattern: /\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}\b/g,
      description: '邮箱地址',
      category: 'contact'
    },
    phone: {
      name: 'phone', 
      pattern: /(?:\+?86)?[-\s]?1[3-9]\d{9}|\b\d{3,4}[-\s]?\d{3,4}[-\s]?\d{3,4}\b/g,
      description: '电话号码',
      category: 'contact'
    },
    chineseId: {
      name: 'id',
      pattern: /\b\d{18}|\d{17}[Xx]\b/g,
      description: '身份证号码',
      category: 'pii'
    },
    bankCard: {
      name: 'bank',
      pattern: /\b\d{16,19}\b/g,
      description: '银行卡号',
      category: 'financial'
    },
    creditCard: {
      name: 'creditCard',
      pattern: /\b(?:\d{4}[-\s]?){3}\d{4}\b/g,
      description: '信用卡号',
      category: 'financial'
    },
    ssn: {
      name: 'ssn',
      pattern: /\b\d{3}-\d{2}-\d{4}\b/g,
      description: '社会保障号',
      category: 'pii'
    },
    ipAddress: {
      name: 'ipAddress',
      pattern: /\b(?:\d{1,3}\.){3}\d{1,3}\b/g,
      description: 'IP地址',
      category: 'pii'
    },
    url: {
      name: 'url',
      pattern: /https?:\/\/[^\s]+/g,
      description: '网址链接',
      category: 'pii'
    },
    address: {
      name: 'address',
      pattern: /\b\d+\s+[A-Za-z\u4e00-\u9fa5]+\s+(路|街|巷|弄|号|楼|室|区|市|省|县)\b/g,
      description: '地址信息',
      category: 'pii'
    }
  }
}

export const validateCustomPattern = (pattern: string): { valid: boolean; error?: string } => {
  try {
    new RegExp(pattern)
    return { valid: true }
  } catch (error) {
    return { 
      valid: false, 
      error: '无效的正则表达式: ' + (error as Error).message 
    }
  }
}

export const escapeRegExp = (string: string): string => {
  return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
}

export const createLiteralPattern = (text: string): string => {
  return escapeRegExp(text)
}

export const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 Bytes'
  
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

export const generateRedactionId = (): string => {
  return 'redaction_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

export const getRedactionColor = (type: string): string => {
  const colors = {
    manual: '#f56c6c',      // 手动选择 - 红色
    pattern: '#409eff',     // 模式识别 - 蓝色
    text: '#67c23a',        // 文本搜索 - 绿色
    image: '#e6a23c',       // 图像遮盖 - 橙色
    custom: '#909399'       // 自定义 - 灰色
  }
  return colors[type as keyof typeof colors] || colors.custom
}

export const downloadFile = (blob: Blob, filename: string): void => {
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}

export const readFileAsArrayBuffer = (file: File): Promise<ArrayBuffer> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(reader.result as ArrayBuffer)
    reader.onerror = () => reject(reader.error)
    reader.readAsArrayBuffer(file)
  })
}

export const isValidPdfFile = (file: File): { valid: boolean; error?: string } => {
  // Check file extension
  if (!file.name.toLowerCase().endsWith('.pdf')) {
    return { valid: false, error: '只支持PDF文件' }
  }
  
  // Check file size (50MB limit)
  const maxSize = 50 * 1024 * 1024
  if (file.size > maxSize) {
    return { 
      valid: false, 
      error: `文件大小不能超过50MB，当前文件大小: ${formatFileSize(file.size)}` 
    }
  }
  
  // Check MIME type
  if (file.type && file.type !== 'application/pdf') {
    return { valid: false, error: '无效的PDF文件类型' }
  }
  
  return { valid: true }
}

export const createRedactionStats = (redactions: any[]) => {
  const stats = {
    total: redactions.length,
    byPage: new Map<number, number>(),
    byType: new Map<string, number>(),
    categories: new Map<string, number>()
  }
  
  redactions.forEach(redaction => {
    // Count by page
    const pageCount = stats.byPage.get(redaction.page) || 0
    stats.byPage.set(redaction.page, pageCount + 1)
    
    // Count by type
    const typeCount = stats.byType.get(redaction.type) || 0
    stats.byType.set(redaction.type, typeCount + 1)
    
    // Count by category if available
    if (redaction.category) {
      const categoryCount = stats.categories.get(redaction.category) || 0
      stats.categories.set(redaction.category, categoryCount + 1)
    }
  })
  
  return stats
}

// Keyboard shortcuts for the PDF editor
export const PDFEditorShortcuts = {
  UNDO: 'Ctrl+Z',
  REDO: 'Ctrl+Y',
  DELETE_SELECTED: 'Delete',
  SELECT_ALL: 'Ctrl+A',
  ZOOM_IN: 'Ctrl+=',
  ZOOM_OUT: 'Ctrl+-',
  RESET_ZOOM: 'Ctrl+0',
  NEXT_PAGE: 'PageDown',
  PREV_PAGE: 'PageUp',
  SAVE: 'Ctrl+S'
}

export const handleKeyboardShortcut = (
  event: KeyboardEvent,
  handlers: Record<string, () => void>
): boolean => {
  const { ctrlKey, metaKey, key, code } = event
  const modifier = ctrlKey || metaKey
  
  // Build shortcut string
  let shortcut = ''
  if (modifier) shortcut += 'Ctrl+'
  if (event.shiftKey) shortcut += 'Shift+'
  if (event.altKey) shortcut += 'Alt+'
  
  // Add the main key
  if (key === '=' && modifier) {
    shortcut += '='
  } else if (key === '-' && modifier) {
    shortcut += '-'
  } else if (key === '0' && modifier) {
    shortcut += '0'
  } else if (code === 'KeyZ' && modifier) {
    shortcut += 'Z'
  } else if (code === 'KeyY' && modifier) {
    shortcut += 'Y'
  } else if (code === 'KeyA' && modifier) {
    shortcut += 'A'
  } else if (code === 'KeyS' && modifier) {
    shortcut += 'S'
  } else if (code === 'Delete') {
    shortcut = 'Delete'
  } else if (code === 'PageDown') {
    shortcut = 'PageDown'
  } else if (code === 'PageUp') {
    shortcut = 'PageUp'
  } else {
    return false
  }
  
  // Execute handler if found
  const handler = handlers[shortcut]
  if (handler) {
    event.preventDefault()
    handler()
    return true
  }
  
  return false
}