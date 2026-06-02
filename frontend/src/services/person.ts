import { request } from '../utils/request';
import type { PageResult, PersonItem } from '../types/api';

export function fetchPersonList(
  tenant: string,
  params: { orgId: number; realName?: string; pageNum?: number; pageSize?: number },
) {
  return request<PageResult<PersonItem>>(tenant, '/api/usr/person/r/list', {
    method: 'GET',
    params,
  });
}

export function createPerson(
  tenant: string,
  body: { orgId: number; realName: string; mobile?: string; email?: string },
) {
  return request<{ personId: number }>(tenant, '/api/usr/person/w/create', {
    method: 'POST',
    data: body,
  });
}

export function disablePerson(tenant: string, personId: number) {
  return request<null>(tenant, '/api/usr/person/w/disable', {
    method: 'POST',
    params: { personId },
  });
}
