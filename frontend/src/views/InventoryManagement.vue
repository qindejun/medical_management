<template>
  <div class="inventory-management">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchForm.deviceName" placeholder="请输入名称"
        style="width: 200px; margin-right: 10px;"></el-input>
      <el-select v-model="searchForm.category" placeholder="请选择类型" style="width: 150px; margin-right: 10px;">
        <el-option label="诊断设备" value="诊断设备" />
        <el-option label="治疗设备" value="治疗设备" />
        <el-option label="监护设备" value="监护设备" />
        <el-option label="手术器械" value="手术器械" />
        <el-option label="耗材" value="耗材" />
      </el-select>
      <el-input v-model="searchForm.manufacturer" placeholder="请输入生产商"
        style="width: 200px; margin-right: 10px;"></el-input>
      <el-button type="primary" @click="search">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <!-- 操作按钮 -->
    <div class="operation-buttons" style="margin-top: 20px;">
      <el-button type="primary" @click="showOutDialog">出库</el-button>
      <el-button type="success" @click="showInDialog">入库</el-button>
      <el-button type="warning" @click="showTransferDialog">调拨</el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" border style="width: 100%;">
      <el-table-column prop="deviceName" label="名称"></el-table-column>
      <el-table-column prop="modelNumber" label="型号"></el-table-column>
      <el-table-column prop="category" label="类型"></el-table-column>
      <el-table-column prop="manufacturer" label="生产商"></el-table-column>
      <el-table-column prop="quantity" label="数量"></el-table-column>
      <el-table-column prop="location" label="仓库"></el-table-column>
      <el-table-column prop="lastUpdated" label="最后操作时间"></el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button type="text" @click="showEditDialog(scope.row)">修改</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
      :current-page="searchForm.pageNum" :page-sizes="[5, 10, 15, 20]" :page-size="searchForm.pageSize"
      layout="total, sizes, prev, pager, next, jumper" :total="total">
    </el-pagination>

    <!-- 出库对话框 -->
    <el-dialog title="出库" :visible.sync="outDialogVisible" width="30%">
      <el-form :model="outForm" :rules="outRules" ref="outForm" label-width="100px">
        <el-form-item label="名称型号" prop="nameModel">
          <el-select v-model="outForm.nameModel" placeholder="请选择" @change="handleOutNameModelChange">
            <el-option v-for="item in nameModels" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="生产商" prop="manufacturer">
          <el-select v-model="outForm.manufacturer" placeholder="请选择" @change="handleOutManufacturerChange">
            <el-option v-for="item in outManufacturers" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="出库仓库" prop="warehouse">
          <el-select v-model="outForm.warehouse" placeholder="请选择" @change="handleOutWarehouseChange">
            <el-option v-for="item in outWarehouses" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input v-model.number="outForm.quantity" placeholder="请输入数量"></el-input>
          <span>总库存: {{ outForm.totalQuantity }}</span>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="outDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitOut">确定</el-button>
      </span>
    </el-dialog>

    <!-- 入库对话框 -->
    <el-dialog title="入库" :visible.sync="inDialogVisible" width="30%">
      <el-form :model="inForm" :rules="inRules" ref="inForm" label-width="100px">
        <el-form-item label="名称型号" prop="nameModel">
          <el-select v-model="inForm.nameModel" placeholder="请选择" @change="handleInNameModelChange">
            <el-option v-for="item in nameModels" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="生产商" prop="manufacturer">
          <el-select v-model="inForm.manufacturer" placeholder="请选择">
            <el-option v-for="item in inManufacturers" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="入库仓库" prop="warehouse">
          <el-select v-model="inForm.warehouse" placeholder="请选择">
            <el-option v-for="item in inWarehouses" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input v-model.number="inForm.quantity" placeholder="请输入数量"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="inDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitIn">确定</el-button>
      </span>
    </el-dialog>

    <!-- 调拨对话框 -->
    <el-dialog title="调拨" :visible.sync="transferDialogVisible" width="30%">
      <el-form :model="transferForm" :rules="transferRules" ref="transferForm" label-width="100px">
        <el-form-item label="名称型号" prop="nameModel">
          <el-select v-model="transferForm.nameModel" placeholder="请选择" @change="handleTransferNameModelChange">
            <el-option v-for="item in nameModels" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="生产商" prop="manufacturer">
          <el-select v-model="transferForm.manufacturer" placeholder="请选择" @change="handleTransferManufacturerChange">
            <el-option v-for="item in transferManufacturers" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="出库仓库" prop="outWarehouse">
          <el-select v-model="transferForm.outWarehouse" placeholder="请选择" @change="handleTransferOutWarehouseChange">
            <el-option v-for="item in transferOutWarehouses" :key="item" :label="item" :value="item"
              :disabled="item === transferForm.inWarehouse"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="入库仓库" prop="inWarehouse">
          <el-select v-model="transferForm.inWarehouse" placeholder="请选择">
            <el-option v-for="item in transferInWarehouses" :key="item" :label="item" :value="item"
              :disabled="item === transferForm.outWarehouse"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input v-model.number="transferForm.quantity" placeholder="请输入数量"></el-input>
          <span>总库存: {{ transferForm.totalQuantity }}</span>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="transferDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTransfer">确定</el-button>
      </span>
    </el-dialog>

    <!-- 修改对话框 -->
    <el-dialog title="修改库存" :visible.sync="editDialogVisible" width="30%">
      <el-form :model="editForm" :rules="editRules" ref="editForm" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="editForm.deviceName" disabled></el-input>
        </el-form-item>
        <el-form-item label="型号">
          <el-input v-model="editForm.modelNumber" disabled></el-input>
        </el-form-item>
        <el-form-item label="生产商">
          <el-input v-model="editForm.manufacturer" disabled></el-input>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input v-model.number="editForm.quantity"></el-input>
        </el-form-item>
        <el-form-item label="仓库">
          <el-input v-model="editForm.location" disabled></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request';
export default {
  name: 'InventoryManagement',
  data() {
    // 自定义验证规则：数量必须为正数
    const validatePositiveNumber = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入数量'));
      } else if (!Number.isInteger(value) || value <= 0) {
        callback(new Error('数量必须为正整数'));
      } else {
        callback();
      }
    };

    return {
      user: localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : {},
      total: 0,
      // 搜索表单
      searchForm: {
        deviceName: '',
        category: '',
        manufacturer: '',
        pageNum: 1,
        pageSize: 5,
      },
      // 表格数据
      tableData: [],
      // 下拉选项
      categorys: [],
      nameModels: [],
      manufacturers: [],
      warehouses: [],
      // 出库对话框
      outDialogVisible: false,
      outForm: {
        nameModel: '',
        manufacturer: '',
        warehouse: '',
        quantity: '',
        totalQuantity: 0,
      },
      outManufacturers: [],
      outWarehouses: [],
      outRules: {
        nameModel: [{ required: true, message: '请选择名称型号', trigger: 'change' }],
        manufacturer: [{ required: true, message: '请选择生产商', trigger: 'change' }],
        warehouse: [{ required: true, message: '请选择出库仓库', trigger: 'change' }],
        quantity: [
          { required: true, validator: validatePositiveNumber, trigger: 'blur' },
        ],
      },
      // 入库对话框
      inDialogVisible: false,
      inForm: {
        deviceName: '',
        nameModel: '',
        manufacturer: '',
        location: '',
        quantity: '',
      },
      inManufacturers: [],
      inWarehouses: [],
      inRules: {
        nameModel: [{ required: true, message: '请选择名称型号', trigger: 'change' }],
        manufacturer: [{ required: true, message: '请选择生产商', trigger: 'change' }],
        warehouse: [{ required: true, message: '请选择入库仓库', trigger: 'change' }],
        quantity: [
          { required: true, validator: validatePositiveNumber, trigger: 'blur' },
        ],
      },
      // 调拨对话框
      transferDialogVisible: false,
      transferForm: {
        nameModel: '',
        manufacturer: '',
        outWarehouse: '',
        inWarehouse: '',
        quantity: '',
        totalQuantity: 0,
      },
      transferManufacturers: [],
      transferOutWarehouses: [],
      transferInWarehouses: [],
      transferRules: {
        nameModel: [{ required: true, message: '请选择名称型号', trigger: 'change' }],
        manufacturer: [{ required: true, message: '请选择生产商', trigger: 'change' }],
        outWarehouse: [{ required: true, message: '请选择出库仓库', trigger: 'change' }],
        inWarehouse: [{ required: true, message: '请选择入库仓库', trigger: 'change' }],
        quantity: [
          { required: true, validator: validatePositiveNumber, trigger: 'blur' },
        ],
      },
      // 修改对话框
      originalEditQuantity: null,
      editDialogVisible: false,
      editForm: {
        inventoryID: null,
        name: '',
        model: '',
        manufacturer: '',
        quantity: '',
        warehouse: '',
      },
      editRules: {
        quantity: [
          { required: true, validator: validatePositiveNumber, trigger: 'blur' },
        ],
        warehouse: [{ required: true, message: '请选择仓库', trigger: 'change' }],
      },
      params: {
        name: '',
        nameModel: '',
        manufacturer: '',
        warehouse: '',
        quantity: '',
        totalQuantity: 0,
        outWarehouse: '',
        inWarehouse: '',
      },
    };
  },
  created() {
    this.load();
  },
  methods: {
    load() {
      this.loading = true;
      console.log(this.searchForm);
      request
        .get('/inventory', { params: this.searchForm })
        .then((res) => {
          console.log('Load response:', res);
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
    search() {
      console.log(this.searchForm);
      request
        .get('/inventory/search', { params: this.searchForm })
        .then((res) => {
          console.log('Load response:', res);
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
    searchAllOut() {
      console.log(this.searchForm);
      const params = { ...this.searchForm };
      params.pageNum = '';
      params.pageSize = '';
      request
        .get('/inventory/searchall', { params })
        .then((res) => {
          console.log('Load response:', res);
          if (res.code === 1 || res.code === '1') {
            if (Array.isArray(res.data)) {
              this.nameModels = [...new Set(res.data.map((item) => `${item.deviceName}-${item.modelNumber}`))];
            } else {
              this.nameModels = [];
              console.error('res.data不是一个有效的数组');
            }
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
    searchAllIn() {
      console.log(this.searchForm);
      const params = { ...this.searchForm };
      params.pageNum = '';
      params.pageSize = '';
      request
        .get('/inventory/searchallin', { params })
        .then((res) => {
          console.log('Load response:', res);
          if (res.code === 1 || res.code === '1') {
            if (Array.isArray(res.data)) {
              this.nameModels = [...new Set(res.data.map((item) => `${item.deviceName}-${item.modelNumber}`))];
            } else {
              this.nameModels = [];
              console.error('res.data不是一个有效的数组');
            }
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
      this.findAllWarehouse();
    },
    async findAllWarehouse() {
      try {
        const data = await this.fetchWarehouses();
        this.inWarehouses = data;
        console.log('已更新仓库列表:', this.inWarehouses);
      } catch (error) {
        this.inWarehouses = [];
        console.error('处理失败:', error);
      }
    },
    resetSearch() {
      this.searchForm = {
        deviceName: '',
        category: '',
        manufacturer: '',
      };
    },
    async fetchManufacturers(nameModel) {
      try {
        this.loading = true;
        this.params.nameModel = nameModel;
        const response = await request.get('/inventory/manufacturer', {
          params: this.params,
        });
        console.log('API Response:', response);
        if (response.code === 1 || response.code === '1') {
          return response.data;
        } else {
          this.$message.error(`加载失败: ${response.msg || '未知错误'}`);
          return [];
        }
      } catch (error) {
        console.error('请求异常:', error);
        this.$message.error('网络请求失败');
        return [];
      } finally {
        this.loading = false;
      }
    },
    async fetchWarehouses(nameModel, manufacturer) {
      try {
        this.loading = true;
        this.params.nameModel = nameModel;
        this.params.manufacturer = manufacturer;
        const response = await request.get('/inventory/warehouse', {
          params: this.params,
        });
        console.log('API Response:', response);
        if (response.code === 1 || response.code === '1') {
          return response.data;
        } else {
          this.$message.error(`加载失败: ${response.msg || '未知错误'}`);
          return [];
        }
      } catch (error) {
        console.error('请求异常:', error);
        this.$message.error('网络请求失败');
        return [];
      } finally {
        this.loading = false;
      }
    },
    async fetchTotalQuantity(nameModel, manufacturer, warehouse) {
      try {
        this.loading = true;
        this.params.nameModel = nameModel;
        this.params.manufacturer = manufacturer;
        this.params.warehouse = warehouse;
        console.log(this.params);
        const response = await request.get('/inventory/quantity', {
          params: this.params,
        });
        console.log('API Response:', response);
        if (response.code === 1 || response.code === '1') {
          return response.data;
        } else {
          this.$message.error(`加载失败: ${response.msg || '未知错误'}`);
          return [];
        }
      } catch (error) {
        console.error('请求异常:', error);
        this.$message.error('网络请求失败');
        return [];
      } finally {
        this.loading = false;
      }
    },
    showOutDialog() {
      this.outForm = { nameModel: '', manufacturer: '', warehouse: '', quantity: '', totalQuantity: 0 };
      this.searchAllOut();
      this.outManufacturers = [];
      this.outWarehouses = [];
      this.outDialogVisible = true;
      this.$nextTick(() => {
        this.$refs.outForm.clearValidate();
      });
    },
    async handleOutNameModelChange() {
      try {
        const data = await this.fetchManufacturers(this.outForm.nameModel);
        this.outManufacturers = data;
        console.log('已更新制造商列表:', this.outManufacturers);
        this.outForm.manufacturer = '';
        this.outForm.warehouse = '';
        this.outForm.quantity = '';
        this.outForm.totalQuantity = 0;
      } catch (error) {
        this.outManufacturers = [];
        console.error('处理失败:', error);
      }
    },
    async handleOutManufacturerChange() {
      try {
        const data = await this.fetchWarehouses(this.outForm.nameModel, this.outForm.manufacturer);
        this.outWarehouses = data;
        console.log('已更新仓库列表:', this.outWarehouses);
      } catch (error) {
        this.outWarehouses = [];
        console.error('处理失败:', error);
      }
    },
    async handleOutWarehouseChange() {
      try {
        const data = await this.fetchTotalQuantity(this.outForm.nameModel, this.outForm.manufacturer, this.outForm.warehouse);
        this.outForm.totalQuantity = data;
        console.log('已更新总库存:', this.outForm.totalQuantity);
      } catch (error) {
        this.outForm.totalQuantity = 0;
        console.error('处理失败:', error);
      }
    },
    submitOut() {
      this.$refs.outForm.validate((valid) => {
        if (valid) {
          this.params = { ...this.outForm };
          this.params.name = this.user.name;
          console.log(this.params);
          request
            .put('/inventory/out', this.params)
            .then((res) => {
              if (res.code === 1 || res.code === '1') {
                this.$message.success('出库成功');
                this.outDialogVisible = false;
                this.search();
              } else {
                this.$message.error(`操作失败: ${res.msg || '未知错误'}`);
              }
            })
            .catch((error) => {
              console.error('Request error:', error);
              this.$message.error('请求失败，请检查网络或后端服务');
            });
        } else {
          console.log('出库表单验证失败');
          return false;
        }
      });
    },
    showInDialog() {
      this.inForm = { nameModel: '', manufacturer: '', warehouse: '', quantity: '' };
      this.searchAllIn();
      this.inManufacturers = [];
      this.inWarehouses = [];
      this.inDialogVisible = true;
      this.$nextTick(() => {
        this.$refs.inForm.clearValidate();
      });
    },
    async handleInNameModelChange() {
      try {
        const data = await this.fetchManufacturers(this.inForm.nameModel);
        this.inManufacturers = data;
        console.log('已更新制造商列表:', this.inManufacturers);
        this.inForm.manufacturer = '';
        this.inForm.warehouse = '';
        this.inForm.quantity = '';
      } catch (error) {
        this.inManufacturers = [];
        console.error('处理失败:', error);
      }
    },
    submitIn() {
      this.$refs.inForm.validate((valid) => {
        if (valid) {
          this.params = { ...this.inForm };
          this.params.name = this.user.name;
          console.log(this.params);
          request
            .put('/inventory/in', this.params)
            .then((res) => {
              if (res.code === 1 || res.code === '1') {
                this.$message.success('入库成功');
                this.inDialogVisible = false;
                this.search();
              } else {
                this.$message.error(`操作失败: ${res.msg || '未知错误'}`);
              }
            })
            .catch((error) => {
              console.error('Request error:', error);
              this.$message.error('请求失败，请检查网络或后端服务');
            });
        } else {
          console.log('入库表单验证失败');
          return false;
        }
      });
    },
    async showTransferDialog() {
      this.transferForm = {
        nameModel: '',
        manufacturer: '',
        outWarehouse: '',
        inWarehouse: '',
        quantity: '',
        totalQuantity: 0,
      };
      await this.searchAllOut();
      await this.findAllWarehouse();
      this.transferManufacturers = [];
      this.transferOutWarehouses = [];
      this.transferInWarehouses = [...this.inWarehouses];
      this.transferDialogVisible = true;
      this.$nextTick(() => {
        this.$refs.transferForm.clearValidate();
      });
    },
    async handleTransferNameModelChange() {
      try {
        const data = await this.fetchManufacturers(this.transferForm.nameModel);
        this.transferManufacturers = data;
        console.log('已更新制造商列表:', this.transferManufacturers);
        this.transferForm.manufacturer = '';
        this.transferForm.outWarehouse = '';
        this.transferForm.inWarehouse = '';
        this.transferForm.quantity = '';
        this.transferForm.totalQuantity = 0;
      } catch (error) {
        this.transferManufacturers = [];
        console.error('处理失败:', error);
      }
    },
    async handleTransferManufacturerChange() {
      try {
        const data = await this.fetchWarehouses(this.transferForm.nameModel, this.transferForm.manufacturer);
        this.transferOutWarehouses = data;
        console.log('已更新出库仓库列表:', this.transferOutWarehouses);
      } catch (error) {
        this.transferOutWarehouses = [];
        console.error('处理失败:', error);
      }
    },
    async handleTransferOutWarehouseChange() {
      try {
        const data = await this.fetchTotalQuantity(
          this.transferForm.nameModel,
          this.transferForm.manufacturer,
          this.transferForm.outWarehouse
        );
        this.transferForm.totalQuantity = data;
        console.log('已更新总库存:', this.transferForm.totalQuantity);
      } catch (error) {
        this.transferForm.totalQuantity = 0;
        console.error('处理失败:', error);
      }
    },
    submitTransfer() {
      this.$refs.transferForm.validate((valid) => {
        if (valid) {
          this.params = { ...this.transferForm };
          this.params.name = this.user.name;
          console.log(this.params);
          request
            .put('/inventory/transfer', this.params)
            .then((res) => {
              if (res.code === 1 || res.code === '1') {
                this.$message.success('调拨成功');
                this.transferDialogVisible = false;
                this.search();
              } else {
                this.$message.error(`操作失败: ${res.msg || '未知错误'}`);
              }
            })
            .catch((error) => {
              console.error('Request error:', error);
              this.$message.error('请求失败，请检查网络或后端服务');
            });
        } else {
          console.log('调拨表单验证失败');
          return false;
        }
      });
    },
    showEditDialog(row) {
      this.editForm = { ...row };
      this.editForm.deviceName = row.deviceName;
      this.editForm.modelNumber = row.modelNumber;
      this.editForm.warehouse = row.location;
      this.originalEditQuantity = row.quantity;
      this.editDialogVisible = true;
    },
    submitEdit() {
      this.$refs.editForm.validate((valid) => {
        if (valid) {
          if (this.editForm.quantity === this.originalEditQuantity) {
            this.$message.warning('数量未发生变化，无需提交');
            return; // 阻止提交
          }
          this.params = { ...this.editForm };
          this.params.name = this.user.name;
          console.log(this.params);
          request
            .put('/inventory/edit', this.params)
            .then((res) => {
              if (res.code === 1 || res.code === '1') {
                this.$message.success('修改成功');
                this.editDialogVisible = false;
                this.search();
              } else {
                this.$message.error(`操作失败: ${res.msg || '未知错误'}`);
              }
            })
            .catch((error) => {
              console.error('Request error:', error);
              this.$message.error('请求失败，请检查网络或后端服务');
            });
        } else {
          console.log('修改表单验证失败');
          return false;
        }
      });
    },
    handleSizeChange(newSize) {
      this.searchForm.pageSize = newSize;
      this.searchForm.pageNum = 1;
      this.search();
    },
    handleCurrentChange(newPage) {
      this.searchForm.pageNum = newPage;
      this.search();
    },
  },
};
</script>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.el-table {
  width: 100%;
  max-height: 689px;
  overflow-y: auto;
  margin-top: 20px;
  box-sizing: border-box;
}

.el-table__header {
  background-color: #fff;
}

.el-pagination {
  margin-top: 20px;
  text-align: left;
}
</style>