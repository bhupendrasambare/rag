import api from '../api/axios';
import API_ENDPOINTS from '../api/endpoints';

export interface DocumentResponse {
  id: string;
  fileName: string;
  originalFileName: string;
  contentType: string;
  fileSize: number;
  status: string;
  createdAt: string;
  updatedAt: string;
}

export interface PageResponse<T> {
  content: T[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface FetchDocumentsRequest {
  page: number;
  size: number;
  sort?: string[];
}

export const documentService = {

  async getDocuments(
    request: FetchDocumentsRequest,
  ): Promise<PageResponse<DocumentResponse>> {

    const response =
      await api.post<
        ApiResponse<PageResponse<DocumentResponse>>
      >(
        API_ENDPOINTS.DOCUMENT.LIST,
        request,
      );

    return response.data.data;
  },

  async uploadDocument(
    file: File,
    fileName: string,
  ): Promise<DocumentResponse> {

    const formData = new FormData();

    formData.append(
      'file',
      file,
    );

    formData.append(
      'fileName',
      fileName,
    );

    const response =
      await api.post<
        ApiResponse<DocumentResponse>
      >(
        API_ENDPOINTS.DOCUMENT.UPLOAD,
        formData,
        {
          headers: {
            'Content-Type':
              'multipart/form-data',
          },
        },
      );

    return response.data.data;
  },

  async getDocument(
    id: string,
  ): Promise<DocumentResponse> {

    const response =
      await api.get<
        ApiResponse<DocumentResponse>
      >(
        API_ENDPOINTS.DOCUMENT.BY_ID(id),
      );

    return response.data.data;
  },

  async getDocumentStatus(
    id: string,
  ) {

    const response =
      await api.get(
        API_ENDPOINTS.DOCUMENT.STATUS(id),
      );

    return response.data.data;
  },

  async downloadDocument(
    id: string,
  ): Promise<Blob> {

    const response =
      await api.get(
        API_ENDPOINTS.DOCUMENT.DOWNLOAD(id),
        {
          responseType: 'blob',
        },
      );

    return response.data;
  },

  async deleteDocument(
    id: string,
  ): Promise<void> {

    await api.delete(
      API_ENDPOINTS.DOCUMENT.DELETE(id),
    );
  },
};

export default documentService;