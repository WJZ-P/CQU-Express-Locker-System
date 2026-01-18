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
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 200px">
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
        <el-table-column prop="depositorName" label="寄存人" width="100" />
        <el-table-column prop="depositorPhone" label="手机号" width="130" />
        <el-table-column prop="lockerId" label="快递柜" width="100" />
        <el-table-column prop="compartmentId" label="仓门号" width="100" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStorageList } from '@/api/express'

const searchForm = reactive({ depositor: '', status: '' })
const pagination = reactive({ current: 1, pageSize: 10, total: 0 })
const tableData = ref([])

onMounted(() => loadData())

const loadData = async () => {
  try {
    const res = await getStorageList({
      page: pagination.current,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    tableData.value = res.data.list
    pagination.total = res.data.total
  } catch(e) { console.error(e) }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}
</script>

<style lang="scss" scoped>
.storage-list {
  .search-form { margin-bottom: 20px; }
  .pagination { margin-top: 20px; justify-content: flex-end; }
}
</style>
