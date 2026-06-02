import { useCallback, useEffect, useState } from 'react';
import { Button, Form, Input, Modal, Space, Table, Typography, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { Link, useParams } from 'react-router-dom';
import type { PersonItem } from '../../types/api';
import { createPerson, disablePerson, fetchPersonList } from '../../services/person';

export default function PersonListPage() {
  const { tenant = '', orgId = '' } = useParams();
  const orgIdNum = Number(orgId);
  const [list, setList] = useState<PersonItem[]>([]);
  const [total, setTotal] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [keyword, setKeyword] = useState('');
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [form] = Form.useForm();

  const load = useCallback(async () => {
    if (!orgIdNum) return;
    setLoading(true);
    try {
      const res = await fetchPersonList(tenant, {
        orgId: orgIdNum,
        realName: keyword || undefined,
        pageNum,
        pageSize,
      });
      if (res.success && res.result) {
        setList(res.result.records || []);
        setTotal(res.result.total);
      } else {
        message.error(res.message || '加载失败');
      }
    } catch {
      message.error('请求失败');
    } finally {
      setLoading(false);
    }
  }, [tenant, orgIdNum, keyword, pageNum, pageSize]);

  useEffect(() => {
    load();
  }, [load]);

  const handleCreate = async () => {
    const values = await form.validateFields();
    const res = await createPerson(tenant, { orgId: orgIdNum, ...values });
    if (res.success) {
      message.success('成员已创建');
      setModalOpen(false);
      form.resetFields();
      load();
    } else {
      message.error(res.message || '创建失败');
    }
  };

  const columns: ColumnsType<PersonItem> = [
    { title: 'ID', dataIndex: 'id', width: 80 },
    { title: '姓名', dataIndex: 'realName' },
    { title: '手机', dataIndex: 'mobile' },
    { title: '邮箱', dataIndex: 'email' },
    {
      title: '操作',
      render: (_, row) => (
        <Button type="link" danger onClick={() => handleDisable(row.id)}>
          停用
        </Button>
      ),
    },
  ];

  const handleDisable = (personId: number) => {
    Modal.confirm({
      title: '确认停用该成员？',
      onOk: async () => {
        const res = await disablePerson(tenant, personId);
        if (res.success) {
          message.success('已停用');
          load();
        } else {
          message.error(res.message || '操作失败');
        }
      },
    });
  };

  return (
    <>
      <Typography.Paragraph>
        <Link to={`/${tenant}/org`}>← 返回组织列表</Link>
        <span style={{ marginLeft: 8 }}>组织 ID: {orgId}</span>
      </Typography.Paragraph>
      <Space style={{ marginBottom: 16 }}>
        <Input
          placeholder="按姓名搜索"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          style={{ width: 200 }}
        />
        <Button onClick={() => { setPageNum(1); load(); }}>查询</Button>
        <Button type="primary" onClick={() => setModalOpen(true)}>
          新增成员
        </Button>
      </Space>
      <Table
        rowKey="id"
        loading={loading}
        columns={columns}
        dataSource={list}
        pagination={{
          current: pageNum,
          pageSize,
          total,
          onChange: (p, ps) => {
            setPageNum(p);
            setPageSize(ps);
          },
        }}
      />
      <Modal title="新增成员" open={modalOpen} onOk={handleCreate} onCancel={() => setModalOpen(false)}>
        <Form form={form} layout="vertical">
          <Form.Item name="realName" label="姓名" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="mobile" label="手机">
            <Input />
          </Form.Item>
          <Form.Item name="email" label="邮箱">
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}
