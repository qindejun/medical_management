<template>
  <div class="history-orders">
    <!-- 搜索栏 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="客户" prop="customer">
        <el-select v-model="searchForm.customer" placeholder="请选择" clearable>
          <el-option v-for="item in customers" :key="item" :label="item" :value="item"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="searchOrders">查询</el-button>
        <el-button @click="resetSearch">清空</el-button>
      </el-form-item>
    </el-form>

    <!-- 批量删除按钮 -->
    <div class="operation-buttons" style="margin: 20px 0;">
      <el-button type="danger" :disabled="multipleSelection.length === 0" @click="handleBatchDelete">批量删除</el-button>
    </div>

    <!-- 订单表格 -->
    <el-table v-if="tableData.length" :data="tableData" border style="width: 100%"
      @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column v-if="user.role === 1" prop="employee" label="销售员"></el-table-column>
      <el-table-column prop="customer" label="客户"></el-table-column>
      <el-table-column prop="deviceName" label="名称"></el-table-column>
      <el-table-column prop="category" label="类型"></el-table-column>
      <el-table-column prop="manufacturer" label="生产商"></el-table-column>
      <el-table-column prop="quantity" label="数量"></el-table-column>
      <el-table-column prop="unitPrice" label="单价"></el-table-column>
      <el-table-column prop="totalAmount" label="总价"></el-table-column>
      <el-table-column prop="orderDate" label="订单创建时间"></el-table-column>
      <el-table-column prop="status" label="订单状态"></el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button type="text" @click="deleteOrder(scope.row.remark)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div v-else class="no-data">暂无订单数据</div>

    <!-- 分页 -->
    <div class="pagination" style="margin-top: 20px; text-align: left;">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
        :current-page.sync="pagination.currentPage" :page-sizes="[5, 10, 15, 20]" :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper" :total="pagination.total"></el-pagination>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request';
export default {
  name: 'HistoryOrders',
  data() {
    return {
      user: localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : {},
      // 搜索表单
      searchForm: {
        customer: '',
        name: '',
        pageNum: 1,
        pageSize: 5,
      },
      // 表格数据
      tableData: [],
      // 选中的行
      selectedRows: [],
      // 分页信息
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      customers: [],
      // 是否为管理员
      isAdmin: true,
      // 合并单元格的索引
      spanArr: {
        customer: [],
        totalPrice: [],
        createdTime: []
      },
      multipleSelection: []
    }
  },
  created() {
    this.load();
    this.getAllCustomers();
  },
  methods: {
    async load() {
      try {
        this.loading = true;
        if (this.user.role === 2) {
          this.searchForm.name = this.user.name;
        }
        console.log(this.searchForm);
        const res = await request.post('/historyorder', this.searchForm);
        if (res.code === 1 || res.code === '1') {
          this.tableData = res.data.list;
          this.pagination.total = res.data.total || 0;
        } else {
          this.$message.error(`加载数据失败: ${res.msg || '未知错误'}`);
        }
      } catch (error) {
        console.error('加载订单异常:', error);
        this.$message.error('网络请求失败');
      } finally {
        this.loading = false;
      }
    },
    async getAllCustomers() {
      try {
        this.loading = true;
        if (this.user.role === 2) {
          this.searchForm.name = this.user.name;
        }
        const response = await request.post('/historyorder/customers', this.searchForm);
        if (response.code === 1 || response.code === '1') {
          this.customers = response.data;
        } else {
          this.$message.error(`加载客户失败: ${response.msg || '未知错误'}`);
        }
      } catch (error) {
        console.error('请求客户异常:', error);
        this.$message.error('网络请求失败');
      } finally {
        this.loading = false;
      }
    },
    async searchOrders() {
      try {
        this.loading = true;
        if (this.user.role === 2) {
          this.searchForm.name = this.user.name;
        }
        const res = await request.post('/historyorder', this.searchForm);
        if (res.code === 1 || res.code === '1') {
          this.tableData = res.data.list;
          this.total = res.data.total || 0;
        } else {
          this.$message.error(`搜索失败: ${res.msg || '未知错误'}`);
        }
      } catch (error) {
        console.error('搜索订单异常:', error);
        this.$message.error('网络请求失败');
      } finally {
        this.loading = false;
      }
    },
    // 重置搜索
    resetSearch() {
      this.searchForm = {
        customer: '',
      }
      this.searchOrders()
    },
    // 表格选择变化
    handleSelectionChange(val) {
      this.selectedRows = val
      console.log('Selected rows:', this.selectedRows)
    },
    // 批量删除方法
    handleBatchDelete() {
      this.$confirm('确定要批量删除选中的订单记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post("/historyorder/batch", this.multipleSelection).then(res => {
          if (res.code === 1 || res.code === '1') {
            this.$message.success('批量删除成功');
            this.load();
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
    // 单个删除
    deleteOrder(remark) {
      this.$confirm('确定要删除该条历史订单吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post("/historyorder/delete", remark).then(res => {
          if (res.code === 1 || res.code === '1') {
            this.$message.success('删除成功');
            this.load();
          } else {
            this.$message.error(`删除失败: ${res.msg || '未知错误'}`);
          }
        }).catch(error => {
          console.error('Request error:', error);
          this.$message.error('请求失败，请检查网络或后端服务');
        });
      }).catch(() => {
        this.$message.info('已取消删除');
      });
    },
    // 每页条数变化
    handleSizeChange(newSize) {
      this.searchForm.pageSize = newSize;
      this.searchForm.pageNum = 1; // 切换每页大小时重置到第一页
      this.load();
    },
    handleCurrentChange(newPage) {
      this.searchForm.pageNum = newPage;
      this.load();
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
  },
}
</script>

<style scoped>
.history-orders {
  padding: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.no-data {
  text-align: center;
  padding: 20px;
  color: #999;
}
</style>