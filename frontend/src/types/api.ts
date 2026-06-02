/** 对齐后端 Response<T> */
export interface ApiResponse<T> {
  success: boolean;
  code: string | null;
  message: string | null;
  result: T;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  pageNum: number;
  pageSize: number;
}

export interface LoginResult {
  sessionId: string;
  username: string;
  nickname: string;
  role: string;
  expiresAt: number;
}

export interface UserSession {
  sessionId: string;
  tenantCode: string;
  userId: number;
  username: string;
  nickname: string;
  role: string;
}

export interface OrgItem {
  id: number;
  orgName: string;
  orgShortName: string;
  orgRemark: string;
  orgStatus: number;
  rAddTime?: string;
}

export interface PersonItem {
  id: number;
  orgId: number;
  realName: string;
  mobile: string;
  email: string;
  personStatus: number;
}
