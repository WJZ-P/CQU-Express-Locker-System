<template>
  <div class="express-list">
    <el-card shadow="hover">
      <template #header>
        <span>快递列表</span>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="快递单号">
          <el-input v-model="searchForm.trackingNo" placeholder="请输入快递单号" clearable />
        </el-form-item>
        <el-form-item label="收件人手机">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 200px">
            <el-option label="待取件" value="待取件" />
            <el-option label="已取件" value="已取件" />
            <el-option label="已超时" value="已超时" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">录入快递</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="trackingNo" label="快递单号" width="180" />
        <el-table-column prop="courierName" label="快递员" width="100" />
        <el-table-column prop="receiverName" label="收件人" width="100" />
        <el-table-column prop="receiverPhone" label="手机号" width="130" />
        <el-table-column prop="lockerId" label="快递柜" width="100" />
        <el-table-column prop="compartmentId" label="仓门号" width="80" />
        <el-table-column prop="pickupCode" label="取件码" width="100" />
        <el-table-column prop="inTime" label="入柜时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status === '待取件'" type="success" link @click="handlePickup(row)">确认取件</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog v-model="dialogVisible" title="录入快递" width="500px">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="快递单号" prop="trackingNo">
            <el-input v-model="form.trackingNo" />
          </el-form-item>
           <el-form-item label="收件人" prop="receiverName">
            <el-input v-model="form.receiverName" />
          </el-form-item>
           <el-form-item label="手机号" prop="receiverPhone">
            <el-input v-model="form.receiverPhone" />
          </el-form-item>
          <el-form-item label="快递员" prop="courierName">
            <el-input v-model="form.courierName" />
          </el-form-item>
           <el-form-item label="快递柜ID" prop="lockerId">
            <el-input v-model="form.lockerId" placeholder="L001" />
          </el-form-item>
           <el-form-item label="仓门ID" prop="compartmentId">
            <el-input v-model="form.compartmentId" placeholder="L001-C01" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </template>
      </el-dialog>

      <!-- 详情弹窗 -->
      <el-dialog v-model="detailVisible" title="快递详情" width="500px">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="快递单号">{{ detailData.trackingNo }}</el-descriptions-item>
          <el-descriptions-item label="收件人">{{ detailData.receiverName }}</el-descriptions-item>
           <el-descriptions-item label="手机号">{{ detailData.receiverPhone }}</el-descriptions-item>
          <el-descriptions-item label="快递员">{{ detailData.courierName }}</el-descriptions-item>
          <el-descriptions-item label="快递柜">{{ detailData.lockerId }}</el-descriptions-item>
          <el-descriptions-item label="仓门">{{ detailData.compartmentId }}</el-descriptions-item>
          <el-descriptions-item label="取件码">{{ detailData.pickupCode }}</el-descriptions-item>
          <el-descriptions-item label="入柜时间">{{ detailData.inTime }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ detailData.status }}</el-descriptions-item>
        </el-descriptions>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExpressList, createExpress, pickupExpress } from '@/api/express'

const searchForm = reactive({
  trackingNo: '',
  phone: '',
  status: ''
})

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const detailData = ref({})
const formRef = ref()
const form = reactive({
  trackingNo: '',
  receiverName: '',
  receiverPhone: '',
  courierName: '',
  lockerId: '',
  compartmentId: ''
})

const rules = {
  trackingNo: [{ required: true, message: '请输入快递单号', trigger: 'blur' }],
  receiverPhone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  lockerId: [{ required: true, message: '请输入柜号', trigger: 'blur' }]
}

const loadData = async () => {
  try {
    const res = await getExpressList({
      page: pagination.current,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    tableData.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadData()
})

const getStatusType = (status) => {
  const map = { '待取件': 'warning', '已取件': 'success', '已超时': 'danger' }
  return map[status] || 'info'
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  Object.assign(searchForm, { trackingNo: '', phone: '', status: '' })
  loadData()
}

const handleView = (row) => {
  detailData.value = row
  detailVisible.value = true
}

const handleAdd = () => {
  Object.assign(form, { trackingNo: '', receiverName: '', receiverPhone: '', courierName: '', lockerId: '', compartmentId: '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    // Construct the ExpressOrder object.
    const payload = {
      trackingNo: form.trackingNo,
      receiverName: form.receiverName,
      receiverPhone: form.receiverPhone,
      courierName: form.courierName,
      lockerId: form.lockerId,
      compartmentId: form.compartmentId 
    }
    await createExpress(payload)
    ElMessage.success('录入成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handlePickup = (row) => {
  ElMessageBox.prompt('请输入取件码(任意字符模拟)', '取件确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async ({ value }) => {
     try {
       await pickupExpress(row.trackingNo)
       ElMessage.success('取件成功')
       loadData()
     } catch(e) { console.error(e) }
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
.express-list {
  .search-form { margin-bottom: 20px; }
  .pagination { margin-top: 20px; justify-content: flex-end; }
}
</style>
