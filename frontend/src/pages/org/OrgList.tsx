import { useCallback, useEffect, useState } from 'react';
import { Button, Form, Input, Modal, Space, Table, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { Link, useParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import type { OrgItem } from '../../types/api';
import { createOrg, disableOrg, fetchOrgList } from '../../services/org';

export default function OrgListPage() {
  const { tenant = '' } = useParams();
  const { isAdmin } = useAuth();
  const [list, setList] = useState<OrgItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [keyword, setKeyword] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [form] = Form.useForm();

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await fetchOrgList(tenant, keyword || undefined);
      if (res.success) {
        setList(res.result || []);
      } else {
        message.error(res.message || '加载失败');
      }
    } catch {
      message.error('请求失败');
    } finally {
      setLoading(false);
    }
  }, [tenant, keyword]);

  useEffect(() => {
    load();
  }, [load]);

  const handleCreate = async () => {
    const values = await form.validateFields();
    const res = await createOrg(tenant, values);
    if (res.success) {
      message.success('创建成功');
      setModalOpen(false);
      form.resetFields();
      load();
    } else {
      message.error(res.message || '创建失败');
    }
  };

  const handleDisable = (orgId: number) => {
    Modal.confirm({
      title: '确认停用该组织？',
      onOk: async () => {
        const res = await disableOrg(tenant, orgId);
        if (res.success) {
          message.success('已停用');
          load();
        } else {
          message.error(res.message || '操作失败');
        }
      },
    });
  };

  const columns: ColumnsType<OrgItem> = [
    { title: 'ID', dataIndex: 'id', width: 80 },
    { title: '组织名称', dataIndex: 'orgName' },
    { title: '简称', dataIndex: 'orgShortName' },
    { title: '备注', dataIndex: 'orgRemark', ellipsis: true },
    {
      title: '操作',
      key: 'action',
      render: (_, row) => (
        <Space>
          <Link to={`/${tenant}/org/${row.id}/person`}>成员</Link>
          {isAdmin && (
            <Button type="link" danger onClick={() => handleDisable(row.id)}>
              停用
            </Button>
          )}
        </Space>
      ),
    },
  ];

  return (
    <>
      <Space style={{ marginBottom: 16 }}>
        <Input
          placeholder="按组织名搜索"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          style={{ width: 200 }}
          onPressEnter={load}
        />
        <Button onClick={load}>查询</Button>
        {isAdmin && (
          <Button type="primary" onClick={() => setModalOpen(true)}>
            新建组织
          </Button>
        )}
      </Space>
      <Table rowKey="id" loading={loading} columns={columns} dataSource={list} />

      <Modal title="新建组织" open={modalOpen} onOk={handleCreate} onCancel={() => setModalOpen(false)}>
        <Form form={form} layout="vertical">
          <Form.Item name="orgName" label="组织名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="orgShortName" label="简称">
            <Input />
          </Form.Item>
          <Form.Item name="orgRemark" label="备注">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}
