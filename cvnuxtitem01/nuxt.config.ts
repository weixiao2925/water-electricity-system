// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  ssr:true,
  nitro:{
    preset: 'node-server'
  },
  compatibilityDate: '2024-11-01',
  devtools: { enabled: true },
  app:{
    // pageTransition:{name: 'page', mode: 'out-in'},
    head: {
      title: 'CVNUXT',
      meta: [
        { name: 'description', content: 'CVNUXT' },
        { name: 'viewport', content: 'width=device-width, initial-scale=1' },
        { charset: 'utf-8' },
        { name: 'theme-color', content: '#ffffff' },
      ],
      link: [
        { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' },
        { rel: 'stylesheet', href: 'https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap' }
      ],
    }
  },
  modules: [
      '@element-plus/nuxt',
    [
      '@pinia/nuxt',
      {
        autoImports: [
          // 自动引入 `defineStore()`
          'defineStore',
          // 自动引入 `defineStore()` 并重命名为 `definePiniaStore()`
          ['defineStore', 'definePiniaStore'],
        ],
      },
    ],
  ],
  css: [
    '~/assets/css/main.css',
    'element-plus/dist/index.css',
    'element-plus/theme-chalk/display.css'
  ],
  runtimeConfig:{
    public:{
        apiBase: 'http://localhost:8848'
    }
  }
})
