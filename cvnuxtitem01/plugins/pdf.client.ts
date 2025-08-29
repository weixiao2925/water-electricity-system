export default defineNuxtPlugin(() => {
  if (process.client) {
    // Configure PDF.js worker
    import('pdfjs-dist').then((pdfjs) => {
      // @ts-ignore
      pdfjs.GlobalWorkerOptions.workerSrc = `https://cdnjs.cloudflare.com/ajax/libs/pdf.js/4.0.379/pdf.worker.min.js`
    })
  }
})