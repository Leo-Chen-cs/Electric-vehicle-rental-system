<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="Created At" name="createdAt">
						<a-range-picker v-model:value="searchFormState.createdAt" value-format="YYYY-MM-DD HH:mm:ss" show-time />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="Updated At" name="updatedAt">
						<a-range-picker v-model:value="searchFormState.updatedAt" value-format="YYYY-MM-DD HH:mm:ss" show-time />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-button type="primary" @click="tableRef.refresh()">Search</a-button>
					<a-button style="margin: 0 8px" @click="reset">Reset</a-button>
				</a-col>
			</a-row>
		</a-form>
		<s-table ref="tableRef" :columns="columns" :data="loadData" :alert="options.alert.show" bordered
			:row-key="(record) => record.id" :tool-config="toolConfig" :row-selection="options.rowSelection">
			<template #operator class="table-operator">
				<a-space>
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('pricingAdd')">
						<template #icon><plus-outlined /></template>
						New
					</a-button>
					<xn-batch-button v-if="hasPerm('pricingBatchDelete')" buttonName="Batch Delete" icon="DeleteOutlined"
						:selectedRowKeys="selectedRowKeys" @batchCallBack="deleteBatchPricing" />
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('pricingEdit')">Edit</a>
						<a-divider type="vertical" v-if="hasPerm(['pricingEdit', 'pricingDelete'], 'and')" />
						<a-popconfirm title="Are you sure to delete?" @confirm="deletePricing(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('pricingDelete')">Delete</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="pricing">
import { cloneDeep } from 'lodash-es'
import Form from './form.vue'
import pricingApi from '@/api/pricing/pricingApi'
const searchFormState = ref({})
const searchFormRef = ref()
const tableRef = ref()
const formRef = ref()
const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
const columns = [
	{
		title: 'Rental Duration (hours)',
		dataIndex: 'hour'
	},
	{
		title: 'Price ($)',
		dataIndex: 'price'
	},
	{
		title: 'Created At',
		dataIndex: 'createdAt'
	},
	{
		title: 'Updated At',
		dataIndex: 'updatedAt'
	}
]
// Operation栏通过权限判断是否显示
if (hasPerm(['pricingEdit', 'pricingDelete'])) {
	columns.push({
		title: 'Operation',
		dataIndex: 'action',
		align: 'center',
		width: 150
	})
}
const selectedRowKeys = ref([])
// 列表选择配置
const options = {
	// columns数字类型字段加入 needTotal: true 可以勾选自动算账
	alert: {
		show: true,
		clear: () => {
			selectedRowKeys.value = ref([])
		}
	},
	rowSelection: {
		onChange: (selectedRowKey, selectedRows) => {
			selectedRowKeys.value = selectedRowKey
		}
	}
}
const loadData = (parameter) => {
	const searchFormParam = cloneDeep(searchFormState.value)
	// createdAt范围Search条件重载
	if (searchFormParam.createdAt) {
		searchFormParam.startCreatedAt = searchFormParam.createdAt[0]
		searchFormParam.endCreatedAt = searchFormParam.createdAt[1]
		delete searchFormParam.createdAt
	}
	// updatedAt范围Search条件重载
	if (searchFormParam.updatedAt) {
		searchFormParam.startUpdatedAt = searchFormParam.updatedAt[0]
		searchFormParam.endUpdatedAt = searchFormParam.updatedAt[1]
		delete searchFormParam.updatedAt
	}
	return pricingApi.pricingPage(Object.assign(parameter, searchFormParam)).then((data) => {
		return data
	})
}
// Reset
const reset = () => {
	searchFormRef.value.resetFields()
	tableRef.value.refresh(true)
}
// 删除
const deletePricing = (record) => {
	let params = [
		{
			id: record.id
		}
	]
	pricingApi.pricingDelete(params).then(() => {
		tableRef.value.refresh(true)
	})
}
// Batch Delete
const deleteBatchPricing = (params) => {
	pricingApi.pricingDelete(params).then(() => {
		tableRef.value.clearRefreshSelected()
	})
}
</script>
