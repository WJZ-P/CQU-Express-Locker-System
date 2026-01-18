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
          <el-select v-model="searchForm.company" placeholder="请选择" clearable style="width: 200px">
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
        <el-table-column prop="username" label="工号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="company" label="所属公司" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '正常' ? 'success' : 'danger'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="工号" prop="username">
                <el-input v-model="form.username" :disabled="isEdit" />
            </el-form-item>
            <el-form-item label="姓名" prop="name">
                <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
                <el-input v-model="form.phone" />
            </el-form-item>
            <el-form-item label="公司" prop="company">
                <el-select v-model="form.company" style="width: 100%">
                    <el-option label="顺丰" value="顺丰" />
                    <el-option label="圆通" value="圆通" />
                    <el-option label="中通" value="中通" />
                    <el-option label="韵达" value="韵达" />
                </el-select>
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
import { getCourierList, addCourier, updateCourier, deleteCourier } from '@/api/user'

const searchForm = reactive({ workNo: '', name: '', company: '' })
const pagination = reactive({ current: 1, pageSize: 10, total: 0 })
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增快递员')
const formRef = ref()
const isEdit = ref(false)

const form = reactive({
  id: '',
  username: '', // 工号
  name: '',
  phone: '',
  company: '',
  status: '正常'
})

const rules = {
  username: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  company: [{ required: true, message: '请选择公司', trigger: 'change' }]
}

onMounted(() => loadData())

const loadData = async () => {
    try {
        const res = await getCourierList({
            page: pagination.current,
            pageSize: pagination.pageSize,
            ...searchForm
        })
        tableData.value = res.data.list
        pagination.total = res.data.total
    } catch(e) { console.error(e) }
}

const handleSearch = () => { pagination.current = 1; loadData() }

const handleAdd = () => {
    isEdit.value = false
    dialogTitle.value = '新增快递员'
    Object.assign(form, { id: '', username: '', name: '', phone: '', company: '', status: '正常' })
    dialogVisible.value = true
}

const handleEdit = (row) => {
    isEdit.value = true
    dialogTitle.value = '编辑快递员'
    Object.assign(form, row)
    dialogVisible.value = true
}

const handleDelete = (row) => {
    ElMessageBox.confirm(`确定删除快递员 ${row.name}?`).then(async () => {
        await deleteCourier(row.id)
        ElMessage.success('删除成功')
        loadData()
    })
}

const handleSubmit = async () => {
    const valid = await formRef.value.validate().catch(() => false)
    if (!valid) return
    try {
        if(isEdit.value) {
            await updateCourier(form.id, form)
            ElMessage.success('更新成功')
        } else {
            await addCourier(form)
            ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
    } catch(e) { console.error(e) }
}
</script>

<style lang="scss" scoped>
.courier-list {
  .card-header { display: flex; justify-content: space-between; align-items: center; }
  .search-form { margin-bottom: 20px; }
  .pagination { margin-top: 20px; justify-content: flex-end; }
}
</style>
