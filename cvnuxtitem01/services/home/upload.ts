import type {Reading, UploadParams} from "~/types/home/upload/type";

export function useUploadService() {
  const { $api } = useNuxtApp();

  return {
    apiUpload: async (data: UploadParams) => {
      const formData = new FormData();
      formData.append('file', data.file);
      formData.append('type', data.type);

      return await $api.post('/api/home/upload/image', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
    },
    apiUploadSave: async (type: string,data: Reading) => {
        return await $api.post(`/api/home/upload/image/save?type=${type}`, data);
    }
  };
}
