// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2024-11-01',
  devtools: { enabled: true },
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
