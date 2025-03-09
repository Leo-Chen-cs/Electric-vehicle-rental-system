<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="USER_ID" name="userId">
						<a-input v-model:value="searchFormState.userId" placeholder="Please enter USER_ID" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="SCOOTER_ID" name="scooterId">
						<a-input v-model:value="searchFormState.scooterId" placeholder="Please enter SCOOTER_ID" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="START_TIME" name="startTime">
						<a-range-picker v-model:value="searchFormState.startTime" value-format="YYYY-MM-DD HH:mm:ss" show-time />
					</a-form-item>
				</a-col>
				<a-col :span="6" v-show="advanced">
					<a-form-item label="END_TIME" name="endTime">
						<a-range-picker v-model:value="searchFormState.endTime" show-time />
					</a-form-item>
				</a-col>
				<a-col :span="6" v-show="advanced">
					<a-form-item label="TOTAL_COST" name="totalCost">
						<a-input v-model:value="searchFormState.totalCost" placeholder="Please enter TOTAL_COST" />
					</a-form-item>
				</a-col>
				<a-col :span="6" v-show="advanced">
					<a-form-item label="STATUS" name="status">
						<a-select v-model:value="searchFormState.status" placeholder="Please select STATUS"
							:options="statusOptions" />
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
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('rentalAdd')">
						<template #icon><plus-outlined /></template>
						New
					</a-button>
					<xn-batch-button v-if="hasPerm('rentalBatchDelete')" buttonName="Batch Delete" icon="DeleteOutlined"
						:selectedRowKeys="selectedRowKeys" @batchCallBack="deleteBatchRental" />
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'status'">
					{{ $TOOL.dictTypeData('1', record.status) }}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('rentalEdit')">Edit</a>
						<a-divider type="vertical" v-if="hasPerm(['rentalEdit', 'rentalDelete'], 'and')" />
						<a-popconfirm title="Are you sure to delete?" @confirm="deleteRental(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('rentalDelete')">Delete</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="rental">
import tool from '@/utils/tool'
import { cloneDeep } from 'lodash-es'
import Form from './form.vue'
import rentalApi from '@/api/rental/rentalApi'
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
		title: 'USER_ID',
		dataIndex: 'userId'
	},
	{
		title: 'SCOOTER_ID',
		dataIndex: 'scooterId'
	},
	{
		title: 'START_TIME',
		dataIndex: 'startTime'
	},
	{
		title: 'END_TIME',
		dataIndex: 'endTime'
	},
	{
		title: 'START_LOCATION_LATITUDE',
		dataIndex: 'startLocationLatitude'
	},
	{
		title: 'END_LOCATION_LATITUDE',
		dataIndex: 'endLocationLatitude'
	},
	{
		title: 'START_LOCATION_LONGITUDE',
		dataIndex: 'startLocationLongitude'
	},
	{
		title: 'END_LOCATION_LONGITUDE',
		dataIndex: 'endLocationLongitude'
	},
	{
		title: 'TOTAL_COST',
		dataIndex: 'totalCost'
	},
	{
		title: 'STATUS',
		dataIndex: 'status'
	},
	{
		title: 'CREATED_AT',
		dataIndex: 'createdAt'
	},
	{
		title: 'UPDATED_AT',
		dataIndex: 'updatedAt'
	}
]
// Operation栏通过权限判断是否显示
if (hasPerm(['rentalEdit', 'rentalDelete'])) {
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
	// startTime范围Search条件重载
	if (searchFormParam.startTime) {
		searchFormParam.startStartTime = searchFormParam.startTime[0]
		searchFormParam.endStartTime = searchFormParam.startTime[1]
		delete searchFormParam.startTime
	}
	// endTime范围Search条件重载
	if (searchFormParam.endTime) {
		searchFormParam.startEndTime = searchFormParam.endTime[0]
		searchFormParam.endEndTime = searchFormParam.endTime[1]
		delete searchFormParam.endTime
	}
	return rentalApi.rentalPage(Object.assign(parameter, searchFormParam)).then((data) => {
		return data
	})
}
// Reset
const reset = () => {
	searchFormRef.value.resetFields()
	tableRef.value.refresh(true)
}
// 删除
const deleteRental = (record) => {
	let params = [
		{
			id: record.id
		}
	]
	rentalApi.rentalDelete(params).then(() => {
		tableRef.value.refresh(true)
	})
}
// Batch Delete
const deleteBatchRental = (params) => {
	rentalApi.rentalDelete(params).then(() => {
		tableRef.value.clearRefreshSelected()
	})
}
const statusOptions = tool.dictList('1')
</script>
