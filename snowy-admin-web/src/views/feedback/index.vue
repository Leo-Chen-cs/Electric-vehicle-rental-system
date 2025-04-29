<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="RATING" name="rating">
						<a-input-number v-model:value="searchFormState.rating" placeholder="Please enter RATING"
							style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="COMMENT" name="comment">
						<a-input v-model:value="searchFormState.comment" placeholder="Please enter COMMENT" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="UPDATED_AT" name="updatedAt">
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
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('feedbackAdd')">
						<template #icon><plus-outlined /></template>
						New
					</a-button>
					<xn-batch-button v-if="hasPerm('feedbackBatchDelete')" buttonName="Batch Delete" icon="DeleteOutlined"
						:selectedRowKeys="selectedRowKeys" @batchCallBack="deleteBatchFeedback" />
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'rating'">
					<a-tag :color="getPriorityColor(record.rating)">
						{{ getPriorityLabel(record.rating) }}
					</a-tag>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('feedbackEdit')">Edit</a>
						<a-popconfirm title="This feedback will be deleted if marked handled" @confirm="handleComplete(record)">
							<a-button type="primary" ghost size="small">
								<template #icon><check-outlined /></template>
								Handled
							</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="feedback">
import { cloneDeep } from 'lodash-es'
import Form from './form.vue'
import feedbackApi from '@/api/feedback/feedbackApi'
import { CheckOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
const searchFormState = ref({})
const searchFormRef = ref()
const tableRef = ref()
const formRef = ref()
const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
const columns = [
	{
		title: 'USER_ID',
		dataIndex: 'userId'
	},
	{
		title: 'RENTAL_ID',
		dataIndex: 'rentalId'
	},
	{
		title: 'RATING',
		dataIndex: 'rating'
	},
	{
		title: 'COMMENT',
		dataIndex: 'comment'
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
if (hasPerm(['feedbackEdit', 'feedbackDelete'])) {
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
	// updatedAt范围Search条件重载
	if (searchFormParam.updatedAt) {
		searchFormParam.startUpdatedAt = searchFormParam.updatedAt[0]
		searchFormParam.endUpdatedAt = searchFormParam.updatedAt[1]
		delete searchFormParam.updatedAt
	}
	return feedbackApi.feedbackPage(Object.assign(parameter, searchFormParam)).then((data) => {
		return data
	})
}
// Reset
const reset = () => {
	searchFormRef.value.resetFields()
	tableRef.value.refresh(true)
}
// Batch Delete
const deleteBatchFeedback = (params) => {
	feedbackApi.feedbackDelete(params).then(() => {
		tableRef.value.clearRefreshSelected()
	})
}
// 添加优先级颜色和标签方法
const getPriorityColor = (rating) => {
	const colors = {
		3: '#f50', // 红色 - 严重故障
		2: '#fa8c16', // 橙色 - 损坏
		1: '#52c41a', // 绿色 - 轻微问题
		default: '#d9d9d9' // 灰色 - 默认
	}
	return colors[rating] || colors.default
}

const getPriorityLabel = (rating) => {
	const labels = {
		3: 'High',
		2: 'Medium',
		1: 'Low'
	}
	return labels[rating] || '未知问题'
}

// 修改为处理完成并删除的方法
const handleComplete = (record) => {
	let params = [
		{
			id: record.id
		}
	]
	feedbackApi.feedbackDelete(params).then(() => {
		message.success(`Feed #${record.id} Handled`)
		tableRef.value.refresh(true)
	})
}
</script>

<style scoped>
.ant-tag {
	min-width: 70px;
	text-align: center;
	font-weight: 500;
}

.ant-btn-primary.ant-btn-background-ghost {
	color: #52c41a;
	border-color: #52c41a;
}

.ant-btn-primary.ant-btn-background-ghost:hover {
	color: #73d13d;
	border-color: #73d13d;
}
</style>
