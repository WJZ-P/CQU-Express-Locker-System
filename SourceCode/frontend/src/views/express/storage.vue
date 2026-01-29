<template>
  <div class="storage-list">
    <el-card shadow="hover">
      <template #header>
        <span>寄存记录</span>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="收件人手机">
          <el-input v-model="searchForm.receiverPhone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="快递柜">
          <el-select v-model="searchForm.lockerId" placeholder="请选择" clearable>
            <el-option
              v-for="locker in lockerList"
              :key="locker.id"
              :label="`${locker.name || locker.serialNo} - ${locker.location}`"
              :value="locker.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="寄存中" :value="0" />
            <el-option label="已取出" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="expressNo" label="快递单号" width="160" />
        <el-table-column prop="receiverPhone" label="收件人手机" width="130" />
        <el-table-column prop="lockerName" label="快递柜" width="150" />
        <el-table-column prop="boxNo" label="格口号" width="80" />
        <el-table-column prop="pickupCode" label="取件码" width="100" />
        <el-table-column prop="depositTime" label="寄存时间" width="170" />
        <el-table-column prop="pickupTime" label="取出时间" width="170">
          <template #default="{ row }">
            {{ row.pickupTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="寄存时长" width="120">
          <template #default="{ row }">
            {{ calculateDuration(row) }}
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        class="pagination"
        @current-change="loadData"
        @size-change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOrderList } from '@/api/express'
import { getLockerList } from '@/api/locker'

const loading = ref(false)
const lockerList = ref([])
const searchForm = reactive({ receiverPhone: '', lockerId: null, status: null })
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const tableData = ref([])

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger', 3: 'info' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '寄存中', 1: '已取出', 2: '已超时', 3: '已退回' }
  return map[status] || '未知'
}

const calculateDuration = (row) => {
  if (!row.depositTime) return '-'
  const start = new Date(row.depositTime)
  const end = row.pickupTime ? new Date(row.pickupTime) : new Date()
  const diff = end - start
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  
  if (hours > 24) {
    const days = Math.floor(hours / 24)
    return `${days}天${hours % 24}小时`
  }
  return `${hours}小时${minutes}分钟`
}

const loadLockers = async () => {
  try {
    const res = await getLockerList({ page: 1, pageSize: 100 })
    lockerList.value = res.data.list || []
  } catch (error) {
    console.error('加载快递柜列表失败:', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      receiverPhone: searchForm.receiverPhone || undefined,
      lockerId: searchForm.lockerId || undefined,
      status: searchForm.status != null ? searchForm.status : undefined
    }
    const res = await getOrderList(params)
    tableData.value = res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载寄存记录失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadLockers()
  loadData()
})

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  Object.assign(searchForm, { receiverPhone: '', lockerId: null, status: null })
  pagination.page = 1
  loadData()
}
</script>

<style lang="scss" scoped>
.storage-list {
  .search-form { margin-bottom: 20px; }
  .pagination { margin-top: 20px; justify-content: flex-end; }
}
</style>
