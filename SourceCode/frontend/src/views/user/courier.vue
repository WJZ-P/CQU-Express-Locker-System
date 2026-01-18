<template>
  <div class="courier-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>快递员管理</span>
          <el-button type="primary" @click="handleAdd">新增快递员</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="工号">
          <el-input v-model="searchForm.workNo" placeholder="请输入工号" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="快递公司">
          <el-select v-model="searchForm.company" placeholder="请选择" clearable>
            <el-option label="顺丰" value="顺丰" />
            <el-option label="圆通" value="圆通" />
            <el-option label="中通" value="中通" />
            <el-option label="韵达" value="韵达" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="workNo" label="工号" width="100" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="company" label="快递公司" width="120" />
        <el-table-column prop="todayCount" label="今日投递" width="100" />
        <el-table-column prop="totalCount" label="累计投递" width="100" />
        <el-table-column prop="createTime" label="入职时间" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === '在职' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleToggle(row)">
              {{ row.status === '在职' ? '离职' : '复职' }}
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

const searchForm = reactive({ workNo: '', name: '', company: '' })
const pagination = reactive({ current: 1, pageSize: 10, total: 50 })

const tableData = ref([
  { workNo: 'C001', name: '张三', phone: '13811111111', company: '顺丰', todayCount: 25, totalCount: 3560, createTime: '2024-06-01', status: '在职' },
  { workNo: 'C002', name: '李四', phone: '13822222222', company: '圆通', todayCount: 18, totalCount: 2890, createTime: '2024-07-15', status: '在职' },
  { workNo: 'C003', name: '王五', phone: '13833333333', company: '中通', todayCount: 0, totalCount: 1560, createTime: '2024-08-20', status: '离职' }
])

const handleSearch = () => ElMessage.success('查询成功')
const handleAdd = () => ElMessage.info('打开新增快递员弹窗')
const handleEdit = (row) => ElMessage.info(`编辑快递员 ${row.name}`)
const handleToggle = (row) => {
  row.status = row.status === '在职' ? '离职' : '在职'
  ElMessage.success('操作成功')
}
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除快递员 ${row.name}？`).then(() => ElMessage.success('删除成功'))
}
</script>

<style lang="scss" scoped>
.courier-list {
  .card-header { display: flex; justify-content: space-between; align-items: center; }
  .search-form { margin-bottom: 20px; }
  .pagination { margin-top: 20px; justify-content: flex-end; }
}
</style>
