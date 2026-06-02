import { request } from '../utils/request';
import type { OrgItem } from '../types/api';

export function fetchOrgList(tenant: string, orgName?: string) {
  return request<OrgItem[]>(tenant, '/api/usr/org/r/list', {
    method: 'GET',
    params: orgName ? { orgName } : undefined,
  });
}

export function createOrg(
  tenant: string,
  body: { orgName: string; orgShortName?: string; orgRemark?: string },
) {
  return request<{ orgId: number }>(tenant, '/api/usr/org/w/create', {
    method: 'POST',
    data: body,
  });
}

export function disableOrg(tenant: string, orgId: number) {
  return request<null>(tenant, '/api/usr/org/w/disable', {
    method: 'POST',
    params: { orgId },
  });
}
