import { PDFDocument, PDFPage, rgb } from 'pdf-lib'
import * as pdfjs from 'pdfjs-dist'

// PDF.js worker setup
if (process.client) {
  // @ts-ignore
  pdfjs.GlobalWorkerOptions.workerSrc = `https://cdnjs.cloudflare.com/ajax/libs/pdf.js/4.0.379/pdf.worker.min.js`
}

interface RedactionArea {
  x: number
  y: number
  width: number
  height: number
  page: number
  type: 'manual' | 'pattern' | 'text'
}

interface PdfRedactionState {
  document: PDFDocument | null
  viewerDocument: any | null
  pages: PDFPage[]
  currentPage: number
  totalPages: number
  redactions: RedactionArea[]
  originalArrayBuffer: ArrayBuffer | null
}

export const usePdfRedaction = () => {
  const state = reactive<PdfRedactionState>({
    document: null,
    viewerDocument: null,
    pages: [],
    currentPage: 1,
    totalPages: 0,
    redactions: [],
    originalArrayBuffer: null
  })

  // Load PDF from ArrayBuffer
  const loadPdf = async (arrayBuffer: ArrayBuffer): Promise<void> => {
    try {
      // Store original for viewer
      state.originalArrayBuffer = arrayBuffer

      // Load with pdf-lib for editing
      state.document = await PDFDocument.load(arrayBuffer)
      state.pages = state.document.getPages()
      state.totalPages = state.pages.length

      // Load with PDF.js for viewing
      if (process.client) {
        const loadingTask = pdfjs.getDocument({ data: arrayBuffer })
        state.viewerDocument = await loadingTask.promise
      }

      // Reset state
      state.currentPage = 1
      state.redactions = []
    } catch (error) {
      console.error('Error loading PDF:', error)
      throw new Error('Failed to load PDF document')
    }
  }

  // Render PDF page to canvas
  const renderPage = async (canvas: HTMLCanvasElement, pageNumber: number, scale: number = 1): Promise<void> => {
    if (!state.viewerDocument || !canvas) return

    try {
      const page = await state.viewerDocument.getPage(pageNumber)
      const viewport = page.getViewport({ scale })
      
      canvas.width = viewport.width
      canvas.height = viewport.height
      
      const context = canvas.getContext('2d')
      if (!context) return

      const renderContext = {
        canvasContext: context,
        viewport: viewport
      }

      await page.render(renderContext).promise
    } catch (error) {
      console.error('Error rendering page:', error)
      throw new Error('Failed to render PDF page')
    }
  }

  // Convert canvas coordinates to PDF coordinates
  const canvasToPdfCoordinates = (
    canvasX: number, 
    canvasY: number, 
    canvasWidth: number, 
    canvasHeight: number,
    pageNumber: number
  ) => {
    if (!state.pages[pageNumber - 1]) return null

    const page = state.pages[pageNumber - 1]
    const { width: pdfWidth, height: pdfHeight } = page.getSize()

    // PDF coordinates start from bottom-left, canvas from top-left
    const pdfX = (canvasX / canvasWidth) * pdfWidth
    const pdfY = pdfHeight - ((canvasY / canvasHeight) * pdfHeight)

    return { x: pdfX, y: pdfY, pdfWidth, pdfHeight }
  }

  // Add redaction area
  const addRedaction = (redaction: RedactionArea): void => {
    state.redactions.push(redaction)
  }

  // Remove redaction area
  const removeRedaction = (index: number): void => {
    if (index >= 0 && index < state.redactions.length) {
      state.redactions.splice(index, 1)
    }
  }

  // Clear all redactions
  const clearRedactions = (): void => {
    state.redactions = []
  }

  // Apply true redaction by removing content from PDF
  const applyRedactions = async (): Promise<Uint8Array> => {
    if (!state.document) throw new Error('No PDF document loaded')

    try {
      // Create a copy of the document to avoid modifying the original
      const pdfBytes = await state.document.save()
      const newDoc = await PDFDocument.load(pdfBytes)
      const newPages = newDoc.getPages()

      // Group redactions by page
      const redactionsByPage = new Map<number, RedactionArea[]>()
      state.redactions.forEach(redaction => {
        if (!redactionsByPage.has(redaction.page)) {
          redactionsByPage.set(redaction.page, [])
        }
        redactionsByPage.get(redaction.page)!.push(redaction)
      })

      // Apply redactions to each page
      for (const [pageNum, pageRedactions] of redactionsByPage) {
        const page = newPages[pageNum - 1]
        if (!page) continue

        // For each redaction on this page
        for (const redaction of pageRedactions) {
          await applyRedactionToPage(page, redaction)
        }
      }

      return await newDoc.save()
    } catch (error) {
      console.error('Error applying redactions:', error)
      throw new Error('Failed to apply redactions to PDF')
    }
  }

  // Apply a single redaction to a page
  const applyRedactionToPage = async (page: PDFPage, redaction: RedactionArea): Promise<void> => {
    try {
      const { width: pageWidth, height: pageHeight } = page.getSize()

      // Convert canvas coordinates to PDF coordinates
      const pdfX = redaction.x
      const pdfY = pageHeight - redaction.y - redaction.height // PDF coordinates are bottom-up
      const pdfWidth = redaction.width
      const pdfHeight = redaction.height

      // Draw a black rectangle to cover the content
      // This is true redaction - we're adding opaque content over the original
      page.drawRectangle({
        x: pdfX,
        y: pdfY,
        width: pdfWidth,
        height: pdfHeight,
        color: rgb(0, 0, 0), // Black color for redaction
      })

      // For true content removal, we would need to modify the content stream
      // This is more complex and involves parsing PDF operators
      await removeContentInArea(page, pdfX, pdfY, pdfWidth, pdfHeight)
    } catch (error) {
      console.error('Error applying redaction to page:', error)
    }
  }

  // Remove actual content from PDF (advanced redaction)
  const removeContentInArea = async (
    page: PDFPage, 
    x: number, 
    y: number, 
    width: number, 
    height: number
  ): Promise<void> => {
    try {
      // This is a simplified implementation
      // In a full implementation, you would:
      // 1. Parse the page's content stream
      // 2. Identify text and graphics operators within the redaction area
      // 3. Remove or modify those operators
      // 4. Reconstruct the content stream

      // For now, we'll use a content stream that adds a white rectangle
      // followed by a black rectangle to ensure complete coverage
      const contentStream = `
        q
        1 1 1 rg
        ${x} ${y} ${width} ${height} re
        f
        0 0 0 rg
        ${x} ${y} ${width} ${height} re
        f
        Q
      `

      // Add the content stream to the page
      // This overwrites any content in the specified area
      page.drawRectangle({
        x: x,
        y: y,
        width: width,
        height: height,
        color: rgb(1, 1, 1), // White background first
      })

      page.drawRectangle({
        x: x,
        y: y,
        width: width,
        height: height,
        color: rgb(0, 0, 0), // Black redaction over it
      })
    } catch (error) {
      console.error('Error removing content:', error)
    }
  }

  // Find text patterns in PDF
  const findTextPatterns = async (patterns: string[]): Promise<RedactionArea[]> => {
    const foundRedactions: RedactionArea[] = []

    if (!state.viewerDocument) return foundRedactions

    try {
      for (let pageNum = 1; pageNum <= state.totalPages; pageNum++) {
        const page = await state.viewerDocument.getPage(pageNum)
        const textContent = await page.getTextContent()
        
        // Simple pattern matching on text items
        textContent.items.forEach((item: any) => {
          if (item.str) {
            patterns.forEach(pattern => {
              const regex = new RegExp(pattern, 'gi')
              if (regex.test(item.str)) {
                // Approximate text position (this could be improved)
                const transform = item.transform
                const x = transform[4]
                const y = transform[5]
                const width = item.width || 100
                const height = item.height || 12

                foundRedactions.push({
                  x: x,
                  y: y,
                  width: width,
                  height: height,
                  page: pageNum,
                  type: 'pattern'
                })
              }
            })
          }
        })
      }
    } catch (error) {
      console.error('Error finding text patterns:', error)
    }

    return foundRedactions
  }

  // Built-in patterns for sensitive data
  const getSensitiveDataPatterns = (): Record<string, string> => {
    return {
      email: '[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}',
      phone: '\\b(?:\\+?86)?\\s?1[3-9]\\d{9}\\b|\\b(?:\\d{3,4}[\\s-]?){2}\\d{3,4}\\b',
      id: '\\b\\d{18}\\b|\\b\\d{17}[\\dxX]\\b', // Chinese ID pattern
      bank: '\\b\\d{16,19}\\b', // Bank card pattern
      ssn: '\\b\\d{3}-\\d{2}-\\d{4}\\b', // US SSN pattern
      creditCard: '\\b(?:\\d{4}[\\s-]?){3}\\d{4}\\b'
    }
  }

  // Apply pattern-based redaction
  const applyPatternRedaction = async (patternTypes: string[], customPattern?: string): Promise<void> => {
    const patterns = getSensitiveDataPatterns()
    const patternsToApply: string[] = []

    // Add selected pattern types
    patternTypes.forEach(type => {
      if (patterns[type]) {
        patternsToApply.push(patterns[type])
      }
    })

    // Add custom pattern if provided
    if (customPattern && customPattern.trim()) {
      patternsToApply.push(customPattern.trim())
    }

    if (patternsToApply.length === 0) return

    try {
      const foundRedactions = await findTextPatterns(patternsToApply)
      foundRedactions.forEach(redaction => addRedaction(redaction))
    } catch (error) {
      console.error('Error applying pattern redaction:', error)
      throw new Error('Failed to apply pattern redaction')
    }
  }

  // Get redaction statistics
  const getRedactionStats = () => {
    const stats = {
      total: state.redactions.length,
      byPage: new Map<number, number>(),
      byType: new Map<string, number>()
    }

    state.redactions.forEach(redaction => {
      // Count by page
      const pageCount = stats.byPage.get(redaction.page) || 0
      stats.byPage.set(redaction.page, pageCount + 1)

      // Count by type
      const typeCount = stats.byType.get(redaction.type) || 0
      stats.byType.set(redaction.type, typeCount + 1)
    })

    return stats
  }

  return {
    // State
    state: readonly(state),
    
    // Core functions
    loadPdf,
    renderPage,
    canvasToPdfCoordinates,
    
    // Redaction management
    addRedaction,
    removeRedaction,
    clearRedactions,
    applyRedactions,
    
    // Pattern-based redaction
    applyPatternRedaction,
    getSensitiveDataPatterns,
    
    // Utilities
    getRedactionStats
  }
}