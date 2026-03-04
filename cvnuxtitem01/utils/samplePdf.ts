import { PDFDocument, StandardFonts, rgb } from 'pdf-lib'

export const createSamplePDF = async (): Promise<Uint8Array> => {
  const pdfDoc = await PDFDocument.create()
  
  // Add first page
  const page1 = pdfDoc.addPage([595, 842]) // A4 size
  const font = await pdfDoc.embedFont(StandardFonts.Helvetica)
  const boldFont = await pdfDoc.embedFont(StandardFonts.HelveticaBold)
  
  // Title
  page1.drawText('敏感信息测试文档', {
    x: 50,
    y: 800,
    size: 24,
    font: boldFont,
    color: rgb(0, 0, 0),
  })
  
  // Sample sensitive data
  const sensitiveData = [
    { label: '姓名:', value: '张三', y: 750 },
    { label: '邮箱:', value: 'zhangsan@example.com', y: 720 },
    { label: '电话:', value: '13812345678', y: 690 },
    { label: '身份证:', value: '110101199001011234', y: 660 },
    { label: '银行卡:', value: '6222021234567890123', y: 630 },
    { label: '地址:', value: '北京市朝阳区某某路123号', y: 600 },
    { label: 'IP地址:', value: '192.168.1.100', y: 570 },
    { label: '网址:', value: 'https://example.com/sensitive', y: 540 },
  ]
  
  sensitiveData.forEach(item => {
    page1.drawText(item.label, {
      x: 50,
      y: item.y,
      size: 12,
      font: boldFont,
      color: rgb(0, 0, 0),
    })
    
    page1.drawText(item.value, {
      x: 120,
      y: item.y,
      size: 12,
      font: font,
      color: rgb(0, 0, 0),
    })
  })
  
  // Add some regular content
  page1.drawText('这是一些普通文本内容，不包含敏感信息。', {
    x: 50,
    y: 500,
    size: 12,
    font: font,
    color: rgb(0, 0, 0),
  })
  
  page1.drawText('PDF编辑器可以识别并编辑上述敏感信息。', {
    x: 50,
    y: 480,
    size: 12,
    font: font,
    color: rgb(0, 0, 0),
  })
  
  // Add second page with more test data
  const page2 = pdfDoc.addPage([595, 842])
  
  page2.drawText('第二页测试内容', {
    x: 50,
    y: 800,
    size: 20,
    font: boldFont,
    color: rgb(0, 0, 0),
  })
  
  const moreData = [
    '信用卡号: 4111-1111-1111-1111',
    '另一个邮箱: test@company.org', 
    '社会保障号: 123-45-6789',
    '另一个电话: 021-12345678',
    '国外地址: 123 Main St, New York, NY 10001'
  ]
  
  moreData.forEach((text, index) => {
    page2.drawText(text, {
      x: 50,
      y: 750 - (index * 30),
      size: 12,
      font: font,
      color: rgb(0, 0, 0),
    })
  })
  
  // Add instructions
  page2.drawText('使用说明:', {
    x: 50,
    y: 600,
    size: 14,
    font: boldFont,
    color: rgb(0, 0, 0),
  })
  
  const instructions = [
    '1. 选择"编辑"模式，然后拖动选择要编辑的区域',
    '2. 使用"模式识别"自动检测敏感信息',
    '3. 使用"高级设置"配置自定义识别模式',
    '4. 点击"下载"生成真正编辑后的PDF文件',
    '5. 支持撤销/重做操作 (Ctrl+Z / Ctrl+Y)',
    '6. 支持缩放和页面导航'
  ]
  
  instructions.forEach((text, index) => {
    page2.drawText(text, {
      x: 50,
      y: 570 - (index * 25),
      size: 10,
      font: font,
      color: rgb(0.2, 0.2, 0.2),
    })
  })
  
  return await pdfDoc.save()
}

export const downloadSamplePDF = async (): Promise<void> => {
  try {
    const pdfBytes = await createSamplePDF()
    const blob = new Blob([pdfBytes], { type: 'application/pdf' })
    
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = 'sample_sensitive_document.pdf'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
  } catch (error) {
    console.error('Error creating sample PDF:', error)
    throw error
  }
}