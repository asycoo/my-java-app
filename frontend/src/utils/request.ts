import axios, { type AxiosRequestConfig } from 'axios';
import type { ApiResponse } from '../types/api';

const APP_ROOT = '/api/tenant-demo';

/**
 * 拼接租户前缀，对齐 pubfund-zcb-front request.ts：
 * /api/usr/org/r/list → /api/tenant-demo/{tenant}/api/usr/org/r/list
 */
export function buildTenantUrl(tenant: string, path: string): string {
  const normalized = path.startsWith('/') ? path : `/${path}`;
  return tenant ? `${APP_ROOT}/${tenant}${normalized}` : `${APP_ROOT}${normalized}`;
}

const client = axios.create({
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' },
});

client.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      const tenant = err.config?.headers?.['X-Tenant-Code'] as string | undefined;
      if (tenant && !window.location.pathname.includes('/login')) {
        window.location.href = `/${tenant}/login`;
      }
    }
    return Promise.reject(err);
  },
);

export async function request<T>(
  tenant: string,
  path: string,
  config?: AxiosRequestConfig,
): Promise<ApiResponse<T>> {
  const url = buildTenantUrl(tenant, path);
  const { data } = await client.request<ApiResponse<T>>({
    url,
    ...config,
    headers: {
      ...config?.headers,
      'X-Tenant-Code': tenant,
    },
  });
  return data;
}
