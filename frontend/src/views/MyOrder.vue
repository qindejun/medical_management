<template>
    <div class="my-orders">
        <!-- 搜索栏 -->
        <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="客户" prop="customer">
                <el-select v-model="searchForm.customer" placeholder="请选择" clearable>
                    <el-option v-for="item in uniqueCustomers" :key="item" :label="item" :value="item"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="订单时间">
                <el-date-picker v-model="searchForm.dateRange" type="daterange" range-separator="至"
                    start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="searchOrders">查询</el-button>
                <el-button @click="resetSearch">清空</el-button>
            </el-form-item>
        </el-form>
        <!-- 操作按钮 -->
        <div class="operation-bar">
            <el-button type="primary" @click="showAddDialog">新增订单</el-button>
        </div>
        <!-- 订单表格 -->
        <el-table :data="tableData" border style="width: 100%;" @selection-change="handleSelectionChange"
            :span-method="arraySpanMethod">
            <el-table-column prop="customer" label="客户" width="100px"></el-table-column>
            <el-table-column prop="deviceName" label="医疗器械名称"></el-table-column>
            <el-table-column prop="modelNumber" label="型号"></el-table-column>
            <el-table-column prop="category" label="类型"></el-table-column>
            <el-table-column prop="manufacturer" label="生产商"></el-table-column>
            <el-table-column prop="quantity" label="数量"></el-table-column>
            <el-table-column prop="unitPrice" label="单价"></el-table-column>
            <el-table-column prop="totalAmount" label="订单总价"></el-table-column>
            <el-table-column prop="orderDate" label="订单创建时间" width="180px"></el-table-column>
            <el-table-column prop="status" label="订单状态"></el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button type="text" @click="showEditDialog(scope.row.remark)">完成</el-button>
                    <el-button type="text" @click="deleteOrder(scope.row.remark)">取消订单</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- 分页 -->
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
            :current-page="searchForm.pageNum" :page-sizes="[5, 10, 15, 20]" :page-size="searchForm.pageSize"
            layout="total, sizes, prev, pager, next, jumper" :total="total">
        </el-pagination>

        <!-- 新增订单对话框 -->
        <el-dialog title="新增订单" :visible.sync="addDialogVisible" width="700px" custom-class="add-order-dialog"
            :append-to-body="true">
            <el-form :model="addForm" ref="addFormRef" :rules="addRules">
                <el-form-item label="客户" prop="customer">
                    <div class="customer-header">
                        <span class="add-customer" @click="showNewCustomerDialog">新增客户</span>
                    </div>
                    <el-select v-model="addForm.customer" placeholder="请选择客户" class="short-select"
                        @change="handleCustomerChange">
                        <el-option v-for="customer in allcustomers" :key="customer" :label="customer"
                            :value="customer"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="产品">
                    <el-button type="primary" @click="openProductDialog">新增产品</el-button>
                    <div class="product-list" v-if="addForm.items.length">
                        <div class="product-item" v-for="(item, index) in addForm.items" :key="index">
                            <span>{{ item.deviceName }} ({{ item.modelNumber }})</span>
                            <i class="el-icon-close" @click="removeProduct(index)"></i>
                        </div>
                    </div>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="addDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitAddOrder">确定</el-button>
            </div>
        </el-dialog>

        <!-- 新增客户对话框 -->
        <el-dialog title="新增客户" :visible.sync="newCustomerDialogVisible" width="600px"
            custom-class="new-customer-dialog" :append-to-body="true">
            <el-form :model="newCustomer" :rules="customerRules" ref="newCustomerRef">
                <el-form-item label="公司名称" prop="companyName">
                    <el-input v-model="newCustomer.companyName"></el-input>
                </el-form-item>
                <el-form-item label="联系人" prop="contactName">
                    <el-input v-model="newCustomer.contactName"></el-input>
                </el-form-item>
                <el-form-item label="地址" prop="address">
                    <el-input v-model="newCustomer.address"></el-input>
                </el-form-item>
                <el-form-item label="电话" prop="phone">
                    <el-input v-model="newCustomer.phone"></el-input>
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="newCustomer.email"></el-input>
                </el-form-item>
                <el-form-item label="行业" prop="industry">
                    <el-select v-model="newCustomer.industry" placeholder="请选择行业" style="width: 100%">
                        <el-option label="医院" value="医院"></el-option>
                        <el-option label="诊所" value="诊所"></el-option>
                        <el-option label="药店" value="药店"></el-option>
                        <el-option label="经销商" value="经销商"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="newCustomerDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitNewCustomer">确定</el-button>
            </div>
        </el-dialog>

        <!-- 新增产品对话框 -->
        <el-dialog title="新增产品" :visible.sync="productDialogVisible" width="600px" custom-class="product-dialog"
            :append-to-body="true">
            <el-form :model="productForm" ref="productFormRef" :rules="productRules">
                <el-form-item label="产品名称" prop="deviceName">
                    <el-select v-model="productForm.deviceName" placeholder="请选择产品名称" style="width: 100%"
                        @change="fetchModels">
                        <el-option v-for="device in deviceNames" :key="device" :label="device"
                            :value="device"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="型号" prop="modelNumber">
                    <el-select v-model="productForm.modelNumber" placeholder="请选择型号" style="width: 100%"
                        @change="fetchManufacturers" :disabled="!productForm.deviceName">
                        <el-option v-for="model in models" :key="model" :label="model" :value="model"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="生产商" prop="manufacturer">
                    <el-select v-model="productForm.manufacturer" placeholder="请选择生产商" style="width: 100%"
                        @change="fetchWarehouses" :disabled="!productForm.modelNumber">
                        <el-option v-for="manufacturer in manufacturers" :key="manufacturer" :label="manufacturer"
                            :value="manufacturer"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="仓库" prop="warehouseId">
                    <el-select v-model="productForm.warehouse" placeholder="请选择仓库" style="width: 100%"
                        @change="fetchStock" :disabled="!productForm.manufacturer">
                        <el-option v-for="warehouse in warehouses" :key="warehouse" :label="warehouse"
                            :value="warehouse"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="单价" v-if="productForm.unitPrice !== null">
                    <span>{{ productForm.unitPrice ? `¥${productForm.unitPrice.toFixed(2)}` : '无单价' }}</span>
                </el-form-item>
                <el-form-item label="库存" v-if="productForm.stock !== null">
                    <span>{{ productForm.stock || '无库存' }}</span>
                </el-form-item>
                <el-form-item label="数量" prop="quantity">
                    <el-input-number v-model="productForm.quantity" :min="1" :max="productForm.stock || 9999"
                        :disabled="!productForm.warehouse"></el-input-number>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="productDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="addProductToOrder">新增</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import request from '@/utils/request';

export default {
    data() {
        return {
            user: localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : {},
            tableData: [],
            searchForm: {
                customer: '',
                dateRange: [],
                name: '',
                pageNum: 1,
                pageSize: 5,
            },
            allcustomers: [],
            customers: [],
            uniqueCustomers: [],
            devices: [],
            deviceNames: [],
            models: [],
            manufacturers: [],
            warehouses: [],
            total: 0,
            selectedOrders: [],
            addDialogVisible: false,
            addForm: {
                customer: '',
                items: [],
            },
            newCustomer: {
                companyName: '',
                contactName: '',
                address: '',
                phone: '',
                email: '',
                industry: '',
            },
            newCustomerDialogVisible: false,
            editDialogVisible: false,
            editForm: {
                remark: '',
            },
            productDialogVisible: false,
            productForm: {
                deviceName: '',
                modelNumber: '',
                manufacturer: '',
                warehouse: '',
                unitPrice: null,
                stock: null,
                quantity: 1,
            },
            addRules: {
                customer: [
                    { required: true, message: '请选择客户', trigger: 'change' },
                ],
            },
            customerRules: {
                companyName: [
                    { required: true, message: '请输入公司名称', trigger: 'blur' },
                ],
                contactName: [
                    { required: true, message: '请输入联系人', trigger: 'blur' },
                ],
                address: [
                    { required: true, message: '请输入地址', trigger: 'blur' },
                ],
                phone: [
                    { required: true, message: '请输入电话', trigger: 'blur' },
                ],
                email: [
                    { required: true, message: '请输入邮箱', trigger: 'blur' },
                    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' },
                ],
                industry: [
                    { required: true, message: '请选择行业', trigger: 'change' },
                ],
            },
            productRules: {
                deviceName: [
                    { required: true, message: '请选择产品名称', trigger: 'change' },
                ],
                modelNumber: [
                    { required: true, message: '请选择型号', trigger: 'change' },
                ],
                manufacturer: [
                    { required: true, message: '请选择生产商', trigger: 'change' },
                ],
                warehouse: [
                    { required: true, message: '请选择仓库', trigger: 'change' },
                ],
                quantity: [
                    { required: true, message: '请输入数量', trigger: 'change' },
                    {
                        validator: (rule, value, callback) => {
                            if (value > this.productForm.stock) {
                                callback(new Error('数量不能超过库存'));
                            } else {
                                callback();
                            }
                        },
                        trigger: 'change',
                    },
                ],
            },
            isEditMode: false,
        };
    },
    computed: {
        // 计算需要合并的单元格
        spanData() {
            const spans = {
                customer: [],
                totalAmount: [],
                orderDate: [],
                status: [],
                operation: []
            };

            let currentRemark = null;
            let startRow = 0;
            let mergeLength = 1;

            this.tableData.forEach((row, index) => {
                if (row.remark !== currentRemark) {
                    // 处理前一个分组
                    if (currentRemark !== null) {
                        this.setSpan(spans, startRow, mergeLength);
                    }
                    // 开始新分组
                    currentRemark = row.remark;
                    startRow = index;
                    mergeLength = 1;
                } else {
                    mergeLength++;
                }

                // 处理最后一个分组
                if (index === this.tableData.length - 1) {
                    this.setSpan(spans, startRow, mergeLength);
                }
            });
            return spans;
        }
    },
    created() {
        this.getCustomers();
        this.load();
        this.fetchDeviceNames();
    },
    methods: {
        showEditDialog(remark){
            this.$confirm('确定订单已完成吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                console.log(remark);
                request.put(`/order/edit`,remark).then(res => {
                    if (res.code === 1 || res.code === '1') {
                        this.$message.success('修改状态成功');
                        this.load();
                    } else {
                        this.$message.error(`修改状态失败: ${res.msg || '未知错误'}`);
                    }
                }).catch(error => {
                    console.error('Request error:', error);
                    this.$message.error('请求失败，请检查网络或后端服务');
                });
            }).catch(() => {
                this.$message.info('已取消修改');
            });
        },
        setSpan(spans, startRow, mergeLength) {
            Object.keys(spans).forEach(col => {
                spans[col].push({ row: startRow, rowspan: mergeLength, colspan: 1 });
                for (let i = 1; i < mergeLength; i++) {
                    spans[col].push({ row: startRow + i, rowspan: 0, colspan: 0 });
                }
            });
        },
        // 获取列索引
        getColumnIndex(column) {
            const columnMap = {
                customer: 0,
                totalAmount: 7,
                orderDate: 8,
                status: 9,
                operation: 10
            };
            return columnMap[column];
        },
        // 单元格合并方法
        arraySpanMethod({ rowIndex, columnIndex }) {
            const columns = ['customer', 'totalAmount', 'orderDate', 'status', 'operation'];
            const colName = columns.find((_, idx) =>
                this.getColumnIndex(columns[idx]) === columnIndex
            );

            if (colName && this.spanData[colName][rowIndex]) {
                return [this.spanData[colName][rowIndex].rowspan, this.spanData[colName][rowIndex].colspan];
            }
            return [1, 1];
        },
        async getCustomers() {
            try {
                this.loading = true;
                this.searchForm.name = this.user.name;
                const response = await request.post('/order/customers', this.searchForm);
                if (response.code === 1 || response.code === '1') {
                    this.customers = response.data;
                    // 去重客户
                    this.uniqueCustomers = [...new Set(this.customers)];
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
        async getAllCustomers() {
            try {
                this.loading = true;
                const response = await request.get('/customer');
                if (response.code === 1 || response.code === '1') {
                    this.allcustomers = response.data;
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
        async load() {
            try {
                this.loading = true;
                this.searchForm.name = this.user.name;
                const res = await request.post('/order', this.searchForm);
                if (res.code === 1 || res.code === '1') {
                    this.tableData = res.data.list.sort((a, b) => a.remark.localeCompare(b.remark));
                    this.total = res.data.total || 0;
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
        async searchOrders() {
            try {
                this.loading = true;
                this.searchForm.name = this.user.name;
                console.log(this.searchForm);
                const res = await request.post('/order/search', this.searchForm);
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
        resetSearch() {
            this.searchForm = {
                customer: '',
                dateRange: [],
                name: this.user.name,
                pageNum: 1,
                pageSize: 5,
            };
            this.searchOrders();
        },
        handleSizeChange(newSize) {
            this.searchForm.pageSize = newSize;
            this.searchForm.pageNum = 1;
            this.searchOrders();
        },
        handleCurrentChange(newPage) {
            this.searchForm.pageNum = newPage;
            this.searchOrders();
        },
        handleSelectionChange(selection) {
            this.selectedOrders = selection;
        },
        showAddDialog() {
            this.getAllCustomers();
            this.addDialogVisible = true;
            this.addForm = {
                customer: '',
                items: [],
            };
            this.newCustomer = {
                companyName: '',
                contactName: '',
                address: '',
                phone: '',
                email: '',
                industry: '',
            };
            this.$nextTick(() => {
                this.$refs.addFormRef && this.$refs.addFormRef.resetFields();
                this.$refs.newCustomerRef && this.$refs.newCustomerRef.resetFields();
            });
        },
        handleCustomerChange() {
            this.$nextTick(() => {
                this.$refs.addFormRef && this.$refs.addFormRef.clearValidate('customer');
            });
        },
        showNewCustomerDialog() {
            this.newCustomerDialogVisible = true;
            this.newCustomer = {
                companyName: '',
                contactName: '',
                address: '',
                phone: '',
                email: '',
                industry: '',
            };
            this.$nextTick(() => {
                this.$refs.newCustomerRef && this.$refs.newCustomerRef.resetFields();
            });
        },
        async submitNewCustomer() {
            try {
                await this.$refs.newCustomerRef.validate();
                const customerRes = await request.post('/customer/add', this.newCustomer);
                if (customerRes.code === 1 || customerRes.code === '1') {
                    this.$message.success('新增客户成功');
                    this.newCustomerDialogVisible = false;
                    await this.getAllCustomers();
                    this.addForm.customer = customerRes.data;
                    this.$nextTick(() => {
                        this.$refs.addFormRef && this.$refs.addFormRef.clearValidate('customer');
                    });
                } else {
                    this.$message.error(`新增客户失败: ${customerRes.msg || '未知错误'}`);
                }
            } catch (error) {
                console.error('新增客户异常:', error);
                this.$message.error('网络请求失败');
            }
        },
        async submitAddOrder() {
            try {
                await this.$refs.addFormRef.validate();
                const orderData = {
                    customer: this.addForm.customer,
                    employee: this.user.name,
                    items: this.addForm.items.map(item => ({
                        deviceName: item.deviceName,
                        modelNumber: item.modelNumber,
                        manufacturer: item.manufacturer,
                        quantity: item.quantity,
                        unitPrice: item.unitPrice,
                    })),
                };
                console.log('添加的订单数据', orderData);
                const res = await request.post('/order/add', orderData);
                if (res.code === 1 || res.code === '1') {
                    this.$message.success('新增订单成功');
                    this.addDialogVisible = false;
                    this.load();
                } else {
                    this.$message.error(`新增订单失败: ${res.msg || '未知错误'}`);
                }
            } catch (error) {
                console.error('新增订单异常:', error);
                this.$message.error('网络请求失败');
            }
        },
        async deleteOrder(remark) {
            try {
                await this.$confirm('确定要取消订单吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                });
                console.log(remark);
                const res = await request.post('/order/delete', remark);
                if (res.code === 1 || res.code === '1') {
                    this.$message.success('取消订单成功');
                    this.load();
                } else {
                    this.$message.error(`取消订单失败: ${res.msg || '未知错误'}`);
                }
            } catch (error) {
                console.error('取消订单异常:', error);
                this.$message.error('网络请求失败');
            }
        },
        openProductDialog(isEdit = false) {
            this.isEditMode = isEdit;
            this.productForm = {
                deviceName: '',
                modelNumber: '',
                manufacturer: '',
                warehouse: '',
                unitPrice: null,
                stock: null,
                quantity: 1,
            };
            this.models = [];
            this.manufacturers = [];
            this.warehouses = [];
            this.productDialogVisible = true;
            this.$nextTick(() => {
                this.$refs.productFormRef && this.$refs.productFormRef.clearValidate();
            });
        },
        async fetchDeviceNames() {
            try {
                const res = await request.get('/inventory/devicenames');
                if (res.code === 1 || res.code === '1') {
                    this.deviceNames = res.data;
                } else {
                    this.$message.error(`加载产品名称失败: ${res.msg || '未知错误'}`);
                }
            } catch (error) {
                console.error('加载产品名称异常:', error);
                this.$message.error('网络请求失败');
            }
        },
        async fetchModels() {
            this.productForm.modelNumber = '';
            this.productForm.manufacturer = '';
            this.productForm.warehouse = '';
            this.productForm.unitPrice = null;
            this.productForm.stock = null;
            this.models = [];
            this.manufacturers = [];
            this.warehouses = [];
            if (!this.productForm.deviceName) return;
            try {
                const res = await request.get('/devices/models', {
                    params: { deviceName: this.productForm.deviceName },
                });
                if (res.code === 1 || res.code === '1') {
                    this.models = res.data;
                } else {
                    this.$message.error(`加载型号失败: ${res.msg || '未知错误'}`);
                }
            } catch (error) {
                console.error('加载型号异常:', error);
                this.$message.error('网络请求失败');
            }
        },
        async fetchManufacturers() {
            this.productForm.manufacturer = '';
            this.productForm.warehouse = '';
            this.productForm.unitPrice = null;
            this.productForm.stock = null;
            this.manufacturers = [];
            this.warehouses = [];
            if (!this.productForm.modelNumber) return;
            try {
                const res = await request.get('/devices/manufacturers', {
                    params: {
                        deviceName: this.productForm.deviceName,
                        modelNumber: this.productForm.modelNumber,
                    },
                });
                if (res.code === 1 || res.code === '1') {
                    this.manufacturers = res.data;
                } else {
                    this.$message.error(`加载生产商失败: ${res.msg || '未知错误'}`);
                }
            } catch (error) {
                console.error('加载生产商异常:', error);
                this.$message.error('网络请求失败');
            }
        },
        async fetchWarehouses() {
            this.productForm.warehouse = '';
            this.productForm.unitPrice = null;
            this.productForm.stock = null;
            this.warehouses = [];
            if (!this.productForm.manufacturer) return;
            try {
                const res = await request.get('/inventory/warehouses', { params: this.productForm });
                if (res.code === 1 || res.code === '1') {
                    this.warehouses = res.data;
                } else {
                    this.$message.error(`加载仓库失败: ${res.msg || '未知错误'}`);
                }
            } catch (error) {
                console.error('加载仓库异常:', error);
                this.$message.error('网络请求失败');
            }
        },
        async fetchStock() {
            this.productForm.unitPrice = null;
            this.productForm.stock = null;
            if (!this.productForm.warehouse) return;
            try {
                const res = await request.get('/inventory/stock', {
                    params: this.productForm,
                });
                if (res.code === 1 || res.code === '1') {
                    this.productForm.stock = res.data.stock;
                    this.productForm.unitPrice = res.data.unitPrice;
                    this.productForm.quantity = Math.min(this.productForm.quantity, this.productForm.stock || 9999);
                } else {
                    this.$message.error(`加载库存和单价失败: ${res.msg || '未知错误'}`);
                }
            } catch (error) {
                console.error('加载库存和单价异常:', error);
                this.$message.error('网络请求失败');
            }
        },
        async addProductToOrder() {
            try {
                await this.$refs.productFormRef.validate();
                const device = await this.getDeviceInfo();
                const item = {
                    deviceName: this.productForm.deviceName,
                    modelNumber: this.productForm.modelNumber,
                    manufacturer: this.productForm.manufacturer,
                    unitPrice: this.productForm.unitPrice,
                    quantity: this.productForm.quantity,
                };
                this.addForm.items.push(item);
                this.productDialogVisible = false;
                this.$message.success('产品添加成功');
            } catch (error) {
                console.error('添加产品异常:', error);
                this.$message.error('添加产品失败');
            }
        },
        async getDeviceInfo() {
            try {
                const res = await request.get('/devices/info', { params: this.productForm });
                if (res.code === 1 || res.code === '1') {
                    return res.data;
                } else {
                    throw new Error(`获取设备信息失败: ${res.msg || '未知错误'}`);
                }
            } catch (error) {
                console.error('获取设备信息异常:', error);
                throw error;
            }
        },
        removeProduct(index, isEdit = false) {
            const form = isEdit ? this.editForm : this.addForm;
            form.items.splice(index, 1);
        },
    },
};
</script>

<style scoped>
.my-orders {
    padding: 20px;
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    min-height: 100vh;
}

.search-form {
    background: #ffffff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
}

.operation-bar {
    margin-bottom: 20px;
}

.el-table {
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
}

.el-table__header {
    background-color: #fafafa;
}

.el-pagination {
    text-align: left;
    padding: 10px 0;
}

.add-order-dialog,
.product-dialog,
.new-customer-dialog {
    border-radius: 12px;
    overflow: hidden;
    animation: fadeIn 0.3s ease;
}

.add-order-dialog .el-form,
.product-dialog .el-form,
.new-customer-dialog .el-form {
    padding: 0 20px;
}

.customer-header {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
}

.customer-label {
    font-size: 14px;
    color: #606266;
}

.add-customer {
    font-size: 14px;
    color: #409EFF;
    cursor: pointer;
    margin-left: 20px;
}

.add-customer:hover {
    color: #66b1ff;
}

.short-select {
    width: 80%;
}

.product-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-top: 10px;
}

.product-item {
    display: flex;
    align-items: center;
    background: #e6f7ff;
    border: 1px solid #91d5ff;
    border-radius: 6px;
    padding: 8px 12px;
    font-size: 14px;
    color: #303133;
    transition: all 0.3s ease;
}

.product-item:hover {
    background: #bae7ff;
    border-color: #40a9ff;
}

.product-item .el-icon-close {
    margin-left: 8px;
    cursor: pointer;
    color: #f56c6c;
    font-size: 16px;
    transition: color 0.3s ease;
}

.product-item .el-icon-close:hover {
    color: #cf1322;
}

.product-dialog .el-form-item__content span {
    font-size: 14px;
    color: #606266;
    font-weight: 500;
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: scale(0.95);
    }

    to {
        opacity: 1;
        transform: scale(1);
    }
}
</style>