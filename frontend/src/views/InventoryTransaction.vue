<template>
  <div class="inventory-operation-log">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-checkbox-group v-model="searchForm.operationTypes" style="display: inline-block; margin-right: 20px;">
        <el-checkbox v-for="type in operationTypes" :key="type" :label="type">{{ type }}</el-checkbox>
      </el-checkbox-group>
      <el-date-picker v-model="searchForm.timeRange" type="datetimerange" range-separator="至" start-placeholder="开始时间"
        end-placeholder="结束时间" style="width: 400px; margin-right: 20px;"></el-date-picker>
      <el-button type="primary" @click="search">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <!-- 批量删除按钮 -->
    <div class="operation-buttons" style="margin: 20px 0;">
      <el-button type="danger" :disabled="multipleSelection.length === 0" @click="batchDelete">批量删除</el-button>
    </div>

    <!-- 操作记录表格 -->
    <el-table :data="tableData" border style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="transactionType" label="操作类型" width="120px"></el-table-column>
      <el-table-column prop="deviceName" label="医疗器械名称" width="120px"></el-table-column>
      <el-table-column prop="modelNumber" label="型号" width="120px"></el-table-column>
      <el-table-column prop="quantity" label="数量" width="120px">
        <template slot-scope="scope">
          {{ scope.row.quantity }}
          <span v-if="scope.row.beforeEdit !== null"  class="before-edit-text">
            （{{ scope.row.beforeEdit }}）
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="fromWarehouse" label="出库仓库"></el-table-column>
      <el-table-column prop="toWarehouse" label="入库仓库"></el-table-column>
      <el-table-column prop="transactionDate" label="操作时间"></el-table-column>
      <el-table-column prop="employee" label="操作员工"></el-table-column>
    </el-table>

    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
      :current-page="searchForm.pageNum" :page-sizes="[5, 10, 15, 20]" :page-size="searchForm.pageSize"
      layout="total, sizes, prev, pager, next, jumper" :total="total">
    </el-pagination>

  </div>
</template>

<script>
import request from '@/utils/request'
export default {
  name: 'InventoryOperationLog',
  data() {
    return {
      total: 0,
      // 搜索表单
      searchForm: {
        operationTypes: [],
        timeRange: [],
        pageNum: 1,
        pageSize: 5,
      },
      // 操作类型选项
      operationTypes: ['出库', '入库', '调拨', '修改'],
      // 表格数据
      tableData: [],
      // 选中的行
      selectedRows: [],
      multipleSelection: [],
    }
  },
  created() {
    this.load();
  },
  methods: {
    load() {
      this.loading = true;
      console.log(this.searchForm);
      request
        .get('/inventory/transaction', { params: this.searchForm })
        .then((res) => {
          if (res.code === 1 || res.code === '1') {
            this.tableData = res.data.list || [];
            this.total = res.data.total || 0;
          } else {
            this.$message.error(`加载数据失败: ${res.msg || '未知错误'}`);
          }
        })
        .catch((error) => {
          console.error('Request error:', error);
          this.$message.error('请求失败，请检查网络或后端服务');
        })
        .finally(() => {
          this.loading = false;
        });
    },
    // 搜索
    search() {
      this.loading = true;
      console.log('搜索条件', this.searchForm);
      request
        .post('/inventory/transaction/search', this.searchForm)
        .then((res) => {
          if (res.code === 1 || res.code === '1') {
            this.tableData = res.data.list || [];
            this.total = res.data.total || 0;
          } else {
            this.$message.error(`加载数据失败: ${res.msg || '未知错误'}`);
          }
        })
        .catch((error) => {
          console.error('Request error:', error);
          this.$message.error('请求失败，请检查网络或后端服务');
        })
        .finally(() => {
          this.loading = false;
        });
    },
    // 重置搜索
    resetSearch() {
      this.searchForm.operationTypes = [],
      this.searchForm.timeRange = [],
      this.search()
    },
    // 表格选择变化
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    // 批量删除
    batchDelete() {
      this.$confirm('确定要批量删除选中的器械吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                request.put(`/inventory/batch`,this.multipleSelection).then(res => {
                    if (res.code === 1 || res.code === '1') {
                        this.$message.success('批量删除成功');
                        this.search();
                    } else {
                        this.$message.error(`批量删除失败: ${res.msg || '未知错误'}`);
                    }
                }).catch(error => {
                    console.error('Request error:', error);
                    this.$message.error('请求失败，请检查网络或后端服务');
                });
            }).catch(() => {
                this.$message.info('已取消批量删除');
            });
    },
    handleSizeChange(newSize) {
      this.searchForm.pageSize = newSize;
      this.searchForm.pageNum = 1; // 切换每页大小时重置到第一页
      this.search();
    },
    handleCurrentChange(newPage) {
      this.searchForm.pageNum = newPage;
      this.search();
    },
  },
}
</script>

<style scoped>
.inventory-operation-log {
  padding: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.before-edit-text {
  color: #ff0000;
}
</style>