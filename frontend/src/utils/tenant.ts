/** 从路由参数取租户 code，例如 /tenant_a/org */
export function getTenantFromPath(pathname: string): string {
  const segments = pathname.split('/').filter(Boolean);
  return segments[0] || '';
}
