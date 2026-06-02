import { createContext, useContext, type ReactNode } from 'react';

export interface AuthState {
  nickname: string;
  role: string;
  isAdmin: boolean;
}

const AuthContext = createContext<AuthState>({
  nickname: '',
  role: '',
  isAdmin: false,
});

export function AuthProvider({ value, children }: { value: AuthState; children: ReactNode }) {
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
