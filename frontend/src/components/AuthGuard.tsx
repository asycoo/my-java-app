import { useEffect, useState } from 'react';
import { Spin } from 'antd';
import { Navigate, Outlet, useParams } from 'react-router-dom';
import AppLayout from './AppLayout';
import { AuthProvider } from '../context/AuthContext';
import { getCurrentSession } from '../services/auth';

export default function AuthGuard() {
  const { tenant = '' } = useParams();
  const [loading, setLoading] = useState(true);
  const [ok, setOk] = useState(false);
  const [nickname, setNickname] = useState('');
  const [role, setRole] = useState('');

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const res = await getCurrentSession(tenant);
        if (!cancelled) {
          setOk(res.success && !!res.result);
          setNickname(res.result?.nickname || res.result?.username || '');
          setRole(res.result?.role || '');
        }
      } catch {
        if (!cancelled) setOk(false);
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [tenant]);

  if (loading) {
    return (
      <div style={{ padding: 80, textAlign: 'center' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!ok) {
    return <Navigate to={`/${tenant}/login`} replace />;
  }

  const authState = { nickname, role, isAdmin: role === 'ADMIN' };

  return (
    <AuthProvider value={authState}>
      <AppLayout nickname={nickname} role={role}>
        <Outlet />
      </AppLayout>
    </AuthProvider>
  );
}
