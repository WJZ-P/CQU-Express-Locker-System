<template>
  <div class="storage-list">
    <el-card shadow="hover">
      <template #header>
        <span>寄存记录</span>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="寄存人">
          <el-input v-model="searchForm.depositor" placeholder="请输入寄存人" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="寄存中" value="寄存中" />
            <el-option label="已取出" value="已取出" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="记录ID" width="100" />
        <el-table-column prop="depositor" label="寄存人" width="100" />
        <el-table-column prop="locker" label="快递柜" width="100" />
        <el-table-column prop="compartment" label="仓门号" width="80" />
        <el-table-column prop="pickupCode" label="取件码" width="100" />
        <el-table-column prop="depositTime" label="寄存时间" width="160" />
        <el-table-column prop="pickupTime" label="取出时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '寄存中' ? 'warning' : 'success'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        layout="total, prev, pager, next"
        class="pagination"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const searchForm = reactive({ depositor: '', status: '' })
const pagination = reactive({ current: 1, pageSize: 10, total: 50 })

const tableData = ref([
  { id: 'S001', depositor: '张明', locker: 'L001', compartment: 'C02', pickupCode: '998877', depositTime: '2026-01-18 11:00', pickupTime: '-', status: '寄存中' },
  { id: 'S002', depositor: '王丽', locker: 'L002', compartment: 'C04', pickupCode: '556677', depositTime: '2026-01-17 15:30', pickupTime: '2026-01-17 18:00', status: '已取出' }
])

const handleSearch = () => ElMessage.success('查询成功')
</script>

<style lang="scss" scoped>
.storage-list {
  .search-form { margin-bottom: 20px; }
  .pagination { margin-top: 20px; justify-content: flex-end; }
}
</style>
