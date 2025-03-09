<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="Scooter Name" name="scooterName">
						<a-input v-model:value="searchFormState.scooterName" placeholder="Please enter scooter name" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="Brand" name="brand">
						<a-input v-model:value="searchFormState.brand" placeholder="Please enter brand" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="Status" name="status">
						<a-select v-model:value="searchFormState.status" placeholder="Please select status"
							:options="statusOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="6" v-show="advanced">
					<a-form-item label="Battery Level" name="batteryLevel">
						<a-input-number v-model:value="searchFormState.batteryLevel" placeholder="Please enter battery level"
							style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="6" v-show="advanced">
					<a-form-item label="Last Maintenance Date" name="lastMaintenanceDate">
						<a-range-picker v-model:value="searchFormState.lastMaintenanceDate" show-time />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-button type="primary" @click="tableRef.refresh()">Search</a-button>
					<a-button style="margin: 0 8px" @click="reset">Reset</a-button>
					<a @click="toggleAdvanced" style="margin-left: 8px">
						{{ advanced ? 'Collapse' : 'Expand' }}
						<component :is="advanced ? 'up-outlined' : 'down-outlined'" />
					</a>
				</a-col>
			</a-row>
		</a-form>
		<s-table ref="tableRef" :columns="columns" :data="loadData" :alert="options.alert.show" bordered
			:row-key="(record) => record.id" :tool-config="toolConfig" :row-selection="options.rowSelection">
			<template #operator class="table-operator">
				<a-space>
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('scooterAdd')">
						<template #icon><plus-outlined /></template>
						New
					</a-button>
					<xn-batch-button v-if="hasPerm('scooterBatchDelete')" buttonName="Batch Delete" icon="DeleteOutlined"
						:selectedRowKeys="selectedRowKeys" @batchCallBack="deleteBatchScooter" />
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'status'">
					{{ $TOOL.dictTypeData('0', record.status) }}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('scooterEdit')">Edit</a>
						<a-divider type="vertical" v-if="hasPerm(['scooterEdit', 'scooterDelete'], 'and')" />
						<a-popconfirm title="Are you sure to delete?" @confirm="deleteScooter(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('scooterDelete')">Delete</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="scooter">
import tool from '@/utils/tool'
import { cloneDeep } from 'lodash-es'
import Form from './form.vue'
import scooterApi from '@/api/scooter/scooterApi'
const searchFormState = ref({})
const searchFormRef = ref()
const tableRef = ref()
const formRef = ref()
const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
// Search区域显示更多控制
const advanced = ref(false)
const toggleAdvanced = () => {
	advanced.value = !advanced.value
}
const columns = [
	{
		title: 'Scooter Name',
		dataIndex: 'scooterName'
	},
	{
		title: 'Brand',
		dataIndex: 'brand'
	},
	{
		title: 'Model',
		dataIndex: 'model'
	},
	{
		title: 'Status',
		dataIndex: 'status'
	},
	{
		title: 'Battery Level (%)',
		dataIndex: 'batteryLevel'
	},
	{
		title: 'Last Maintenance Date',
		dataIndex: 'lastMaintenanceDate'
	},
	{
		title: 'Longitude',
		dataIndex: 'longitude'
	},
	{
		title: 'Latitude',
		dataIndex: 'latitude'
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
if (hasPerm(['scooterEdit', 'scooterDelete'])) {
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
	// lastMaintenanceDate范围Search条件重载
	if (searchFormParam.lastMaintenanceDate) {
		searchFormParam.startLastMaintenanceDate = searchFormParam.lastMaintenanceDate[0]
		searchFormParam.endLastMaintenanceDate = searchFormParam.lastMaintenanceDate[1]
		delete searchFormParam.lastMaintenanceDate
	}
	return scooterApi.scooterPage(Object.assign(parameter, searchFormParam)).then((data) => {
		return data
	})
}
// Reset
const reset = () => {
	searchFormRef.value.resetFields()
	tableRef.value.refresh(true)
}
// 删除
const deleteScooter = (record) => {
	let params = [
		{
			id: record.id
		}
	]
	scooterApi.scooterDelete(params).then(() => {
		tableRef.value.refresh(true)
	})
}
// Batch Delete
const deleteBatchScooter = (params) => {
	scooterApi.scooterDelete(params).then(() => {
		tableRef.value.clearRefreshSelected()
	})
}
const statusOptions = tool.dictList('0')
</script>
