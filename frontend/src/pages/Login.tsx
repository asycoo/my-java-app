import { useState } from 'react';
import { Button, Card, Form, Input, Typography, message } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import { login } from '../services/auth';

export default function LoginPage() {
  const { tenant = 'tenant_a' } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const onFinish = async (values: { username: string; password: string }) => {
    setLoading(true);
    try {
      const res = await login(tenant, values.username, values.password);
      if (res.success) {
        message.success('登录成功');
        navigate(`/${tenant}/org`);
      } else {
        message.error(res.message || '登录失败');
      }
    } catch {
      message.error('登录请求失败，请确认后端已启动');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: '#f0f2f5',
      }}
    >
      <Card title={`登录 · ${tenant}`} style={{ width: 400 }}>
        <Typography.Paragraph type="secondary">
          种子账号：admin / 123456。也可访问 /tenant_b/login 切换租户。
        </Typography.Paragraph>
        <Form layout="vertical" onFinish={onFinish} initialValues={{ username: 'admin', password: '123456' }}>
          <Form.Item name="username" label="用户名" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="password" label="密码" rules={[{ required: true }]}>
            <Input.Password />
          </Form.Item>
          <Button type="primary" htmlType="submit" block loading={loading}>
            登录
          </Button>
        </Form>
      </Card>
    </div>
  );
}
