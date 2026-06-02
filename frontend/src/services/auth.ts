import { request } from '../utils/request';
import type { LoginResult, UserSession } from '../types/api';

export function login(tenant: string, username: string, password: string) {
  return request<LoginResult>(tenant, '/api/usr/login/w/signin', {
    method: 'POST',
    data: { username, password },
  });
}

export function logout(tenant: string) {
  return request<null>(tenant, '/api/usr/login/w/signout', { method: 'POST' });
}

export function getCurrentSession(tenant: string) {
  return request<UserSession>(tenant, '/api/usr/login/r/current', { method: 'GET' });
}
