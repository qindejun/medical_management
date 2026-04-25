<template>
  <div class="inventory-stats">
    <div class="text-container">
      <div class="title-text">订单信息统计</div>
    </div>
    <div class="chart-container">
      <div id="bie" style="width: 100%; height: 500px"></div>
    </div>
  </div>
</template>
  
  <script>
import * as echarts from "echarts";
import request from '@/utils/request';

export default {
  mounted() {
    this.initEcharts();
  },
  methods: {
    initEcharts() {
      request.get("/order/echarts/bie").then((res) => {
        if (res.code === 1 || res.code === "1") {
          this.initBie(res.data);
        } else {
          this.$message.error(`加载数据失败: ${res.msg || "未知错误"}`);
        }
      });
    },
    initBie(data) {
      let chartDom = document.getElementById("bie");
      let myChart = echarts.init(chartDom);
      let option;

      // 转换数据格式
      const chartData = data.map((item) => ({
        name: item.quarter,
        value: item.totalAmount,
        orderCount: item.orderCount,
      }));

      option = {
        tooltip: {
          trigger: "item",
          formatter: function (params) {
            return `
                ${params.name}<br/>
                订单量: ${params.data.orderCount} 单<br/>
                订单金额: ￥${params.value.toLocaleString()}
              `;
          },
        },
        legend: {
          top: "5%",
          left: "center",
        },
        series: [
          {
            name: "季度订单统计",
            type: "pie",
            radius: ["40%", "70%"],
            itemStyle: {
              borderRadius: 10,
              borderColor: "#fff",
              borderWidth: 2,
            },
            label: {
              show: true,
              formatter: "{b}: {d}%", // 显示季度名称和百分比
            },
            emphasis: {
              // 修正配置项名称
              label: {
                show: true,
                fontSize: 16,
                fontWeight: "bold",
              },
            },
            data: chartData,
          },
        ],
      };

      option && myChart.setOption(option);
    },
  },
};
</script>
  
  <style scoped>
.inventory-stats {
  background: linear-gradient(
    to bottom right,
    #ffffff,
    #4a90e2
  ); /* 左上白色到右下蓝色的渐变 */
  min-height: 100vh; /* 确保背景覆盖整个视口 */
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 5vh 20px; /* 顶部内边距和水平内边距 */
}

.text-container {
  text-align: center;
  margin-bottom: 20px; /* 与图表间距 */
}

.title-text {
  font-size: 3.5rem; /* 更大字体 */
  font-weight: 800; /* 更粗 */
  color: #1a3c73; /* 深蓝色文字 */
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif; /* 现代字体 */
  letter-spacing: 1.5px; /* 字间距 */
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1); /* 轻微文字阴影 */
  padding: 10px;
}

.chart-container {
  width: 100%;
  max-width: 800px; /* 增大最大宽度以放大图表 */
  background: rgba(255, 255, 255, 0.9); /* 半透明白色背景 */
  padding: 20px;
  border-radius: 15px; /* 圆角 */
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2); /* 阴影效果 */
}
</style>