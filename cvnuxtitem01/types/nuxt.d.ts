// types/nuxt.d.ts
import type { apiHelpers } from '~/plugins/api' // 这里指向你真正写 apiHelpers 的地方

declare module '#app' {
    interface NuxtApp {
        $api: typeof apiHelpers
        $apiRaw: typeof api
    }
}

declare module '@vue/runtime-core' {
    interface ComponentCustomProperties {
        $api: typeof apiHelpers
    }
}
