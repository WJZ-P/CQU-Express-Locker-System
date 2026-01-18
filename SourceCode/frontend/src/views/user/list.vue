<template>
  <div class="user-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>普通用户管理</span>
          <el-button type="primary" @click="handleAdd">新增用户</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="faceRegistered" label="人脸录入" width="100">
          <template #default="{ row }">
            <el-tag :type="row.faceRegistered ? 'success' : 'info'">
              {{ row.faceRegistered ? '已录入' : '未录入' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="160" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === '正常' ? 'success' : 'danger'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleToggle(row)">
              {{ row.status === '正常' ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        :total="pagination.total"
        layout="total, prev, pager, next"
        class="pagination"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchForm = reactive({ phone: '', name: '' })
const pagination = reactive({ current: 1, pageSize: 10, total: 100 })

const tableData = ref([
  { id: 1, name: '李明', phone: '13812341234', email: 'liming@qq.com', faceRegistered: true, createTime: '2025-12-01 10:00', status: '正常' },
  { id: 2, name: '王芳', phone: '13956785678', email: 'wangfang@163.com', faceRegistered: false, createTime: '2025-12-05 14:30', status: '正常' },
  { id: 3, name: '刘伟', phone: '13790129012', email: 'liuwei@gmail.com', faceRegistered: true, createTime: '2025-12-10 09:15', status: '禁用' }
])

const handleSearch = () => ElMessage.success('查询成功')
const handleAdd = () => ElMessage.info('打开新增用户弹窗')
const handleEdit = (row) => ElMessage.info(`编辑用户 ${row.name}`)
const handleToggle = (row) => {
  row.status = row.status === '正常' ? '禁用' : '正常'
  ElMessage.success('操作成功')
}
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除用户 ${row.name}？`).then(() => ElMessage.success('删除成功'))
}
</script>

<style lang="scss" scoped>
.user-list {
  .card-header { display: flex; justify-content: space-between; align-items: center; }
  .search-form { margin-bottom: 20px; }
  .pagination { margin-top: 20px; justify-content: flex-end; }
}
</style>
