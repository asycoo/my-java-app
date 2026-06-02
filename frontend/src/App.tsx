import { Navigate, Route, Routes } from 'react-router-dom';
import AuthGuard from './components/AuthGuard';
import LoginPage from './pages/Login';
import OrgListPage from './pages/org/OrgList';
import PersonListPage from './pages/person/PersonList';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/tenant_a/login" replace />} />
      <Route path="/:tenant/login" element={<LoginPage />} />
      <Route path="/:tenant" element={<AuthGuard />}>
        <Route path="org" element={<OrgListPage />} />
        <Route path="org/:orgId/person" element={<PersonListPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/tenant_a/login" replace />} />
    </Routes>
  );
}
