import { Layout, Button, Typography, Space } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import { logout } from '../services/auth';
import type { ReactNode } from 'react';

const { Header, Content } = Layout;

interface AppLayoutProps {
  nickname?: string;
  role?: string;
  children?: ReactNode;
}

export default function AppLayout({ nickname, role, children }: AppLayoutProps) {
  const { tenant = '' } = useParams();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout(tenant);
    navigate(`/${tenant}/login`);
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <Space>
          <Typography.Text style={{ color: '#fff', fontWeight: 600 }}>
            租户学习项目
          </Typography.Text>
          <Typography.Text style={{ color: 'rgba(255,255,255,0.85)' }}>
            租户: {tenant}
          </Typography.Text>
        </Space>
        <Space>
          {nickname && (
            <Typography.Text style={{ color: '#fff' }}>
              {nickname}
              {role ? ` (${role})` : ''}
            </Typography.Text>
          )}
          <Button type="link" style={{ color: '#fff' }} onClick={handleLogout}>
            退出
          </Button>
        </Space>
      </Header>
      <Content style={{ padding: 24 }}>{children}</Content>
    </Layout>
  );
}
