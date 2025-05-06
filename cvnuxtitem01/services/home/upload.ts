import type {UploadParams} from "~/types/home/upload/type";

export function useUploadService() {
  const { $api } = useNuxtApp();

  return {
    apiUpload: async (data: UploadParams) => {
      // 创建FormData对象来发送文件
      const formData = new FormData();
      formData.append('file', data.file);
      formData.append('type', data.type);

      return await $api.post('/api/home/upload/image', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
    }
  };
}
