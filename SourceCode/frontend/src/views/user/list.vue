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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="用户名" prop="username">
                <el-input v-model="form.username" :disabled="isEdit" />
            </el-form-item>
            <el-form-item label="姓名" prop="name">
                <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
                <el-input v-model="form.phone" />
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit">确定</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, addUser, updateUser, deleteUser, toggleUserStatus } from '@/api/user'

const searchForm = reactive({ phone: '', name: '' })
const pagination = reactive({ current: 1, pageSize: 10, total: 0 })
const tableData = ref([])

onMounted(() => loadData())

const loadData = async () => {
    try {
        const res = await getUserList({
            page: pagination.current,
            pageSize: pagination.pageSize,
            ...searchForm
        })
        tableData.value = res.data.list
        pagination.total = res.data.total
    } catch(e) {
        console.error(e)
    }
}

const handleSearch = () => {
    pagination.current = 1
    loadData()
}
const handleAdd = () => {
    isEdit.value = false
    dialogTitle.value = '新增用户'
    Object.assign(form, { id: '', username: '', name: '', phone: '', status: '正常' })
    dialogVisible.value = true
}

const handleEdit = (row) => {
    isEdit.value = true
    dialogTitle.value = '编辑用户'
    Object.assign(form, row)
    dialogVisible.value = true
}

const handleSubmit = async () => {
    const valid = await formRef.value.validate().catch(() => false)
    if(!valid) return
    try {
        if(isEdit.value) {
            await updateUser(form.id, form)
            ElMessage.success('更新成功')
        } else {
            form.password = '123456' // Default
            await addUser(form)
            ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
    } catch(e) { console.error(e) }
}

const handleToggle = (row) => {
  const newStatus = row.status === '正常' ? '禁用' : '正常'
  ElMessageBox.confirm(`确定要${newStatus}该用户吗?`).then(async () => {
      await toggleUserStatus(row.id, {status: newStatus})
      ElMessage.success('操作成功')
      loadData()
  })
}
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除用户 ${row.name}？`).then(async () => {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      loadData()
  })
}
</script>

<style lang="scss" scoped>
.user-list {
  .card-header { display: flex; justify-content: space-between; align-items: center; }
  .search-form { margin-bottom: 20px; }
  .pagination { margin-top: 20px; justify-content: flex-end; }
}
</style>
