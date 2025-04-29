<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="Account" name="account">
						<a-input v-model:value="searchFormState.account" placeholder="Please enter account" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="Name" name="name">
						<a-input v-model:value="searchFormState.name" placeholder="Please enter name" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="Gender" name="gender">
						<a-select v-model:value="searchFormState.gender" placeholder="Please select gender"
							:options="genderOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-button type="primary" @click="tableRef.refresh()">Search</a-button>
					<a-button style="margin: 0 8px" @click="reset">Reset</a-button>
				</a-col>
			</a-row>
		</a-form>
		<s-table ref="tableRef" :columns="columns" :data="loadData" :alert="options.alert.show" bordered
			:row-key="(record) => record.id" :tool-config="toolConfig" :row-selection="options.rowSelection"
			:scroll="{ x: 3500 }">
			<template #operator class="table-operator">
				<a-space>
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('clientUserAdd')">
						<template #icon><plus-outlined /></template>
						New
					</a-button>
					<xn-batch-button v-if="hasPerm('clientUserBatchDelete')" buttonName="Batch Delete" icon="DeleteOutlined"
						:selectedRowKeys="selectedRowKeys" @batchCallBack="deleteBatchClientUser" />
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'gender'">
					{{ $TOOL.dictTypeData('GENDER', record.gender) }}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('clientUserEdit')">Edit</a>
						<a-divider type="vertical" v-if="hasPerm(['clientUserEdit', 'clientUserDelete'], 'and')" />
						<a-button type="link" @click="rentDialogRef.onOpen(record)" size="small">Book</a-button>
						<a-popconfirm title="Are you sure to delete?" @confirm="deleteClientUser(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('clientUserDelete')">Delete</a-button>
						</a-popconfirm>
						<a-button type="primary" ghost size="small" v-if="hasPerm('clientUserDelete')"
							@click="markAsCompleted(record)">
							<check-outlined />Mark as Completed
						</a-button>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="formRef" @successful="tableRef.refresh()" />
	<RentDialog ref="rentDialogRef" @successful="tableRef.refresh()" />
</template>

<script setup name="user">
import tool from '@/utils/tool'
import { cloneDeep } from 'lodash-es'
import Form from './form.vue'
import clientUserApi from '@/api/user/clientUserApi'
import RentDialog from './rentDialog.vue'
import rentalApi from '@/api/rental/rentalApi'
import { CheckOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
const searchFormState = ref({})
const searchFormRef = ref()
const tableRef = ref()
const formRef = ref()
const rentDialogRef = ref()
const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
const columns = [
	{
		title: 'Avatar',
		dataIndex: 'avatar',
		width: 100
	},
	{
		title: 'Signature',
		dataIndex: 'signature',
		width: 120
	},
	{
		title: 'Account',
		dataIndex: 'account',
		width: 120
	},
	{
		title: 'Name',
		dataIndex: 'name',
		width: 100
	},
	{
		title: 'Nickname',
		dataIndex: 'nickname',
		width: 120
	},
	{
		title: 'Gender',
		dataIndex: 'gender',
		width: 80
	},
	{
		title: 'Age',
		dataIndex: 'age',
		width: 70
	},
	{
		title: 'Birthday',
		dataIndex: 'birthday',
		width: 120
	},
	{
		title: 'Nation',
		dataIndex: 'nation',
		width: 100
	},
	{
		title: 'Native Place',
		dataIndex: 'nativePlace',
		width: 120
	},
	{
		title: 'Home Address',
		dataIndex: 'homeAddress',
		width: 150
	},
	{
		title: 'Mailing Address',
		dataIndex: 'mailingAddress',
		width: 150
	},
	{
		title: 'ID Card Type',
		dataIndex: 'idCardType',
		width: 120
	},
	{
		title: 'ID Card Number',
		dataIndex: 'idCardNumber',
		width: 150
	},
	{
		title: 'Culture Level',
		dataIndex: 'cultureLevel',
		width: 120
	},
	{
		title: 'Political Outlook',
		dataIndex: 'politicalOutlook',
		width: 150
	},
	{
		title: 'College',
		dataIndex: 'college',
		width: 120
	},
	{
		title: 'Education',
		dataIndex: 'education',
		width: 120
	},
	{
		title: 'Edu Length',
		dataIndex: 'eduLength',
		width: 100
	},
	{
		title: 'Degree',
		dataIndex: 'degree',
		width: 100
	},
	{
		title: 'Phone',
		dataIndex: 'phone',
		width: 120
	},
	{
		title: 'Email',
		dataIndex: 'email',
		width: 180
	},
	{
		title: 'Home Tel',
		dataIndex: 'homeTel',
		width: 120
	},
	{
		title: 'Office Tel',
		dataIndex: 'officeTel',
		width: 120
	},
	{
		title: 'Emergency Contact',
		dataIndex: 'emergencyContact',
		width: 150
	},
	{
		title: 'Emergency Phone',
		dataIndex: 'emergencyPhone',
		width: 150
	}
]
// Operation栏通过权限判断是否显示
if (hasPerm(['clientUserEdit', 'clientUserDelete'])) {
	columns.push({
		title: 'Operation',
		dataIndex: 'action',
		align: 'center',
		width: 350,
		fixed: 'right'
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
	return clientUserApi.clientUserPage(Object.assign(parameter, searchFormParam)).then((data) => {
		return data
	})
}
// Reset
const reset = () => {
	searchFormRef.value.resetFields()
	tableRef.value.refresh(true)
}
// 删除
const deleteClientUser = (record) => {
	let params = [
		{
			id: record.id
		}
	]
	clientUserApi.clientUserDelete(params).then(() => {
		tableRef.value.refresh(true)
	})
}
// Batch Delete
const deleteBatchClientUser = (params) => {
	clientUserApi.clientUserDelete(params).then(() => {
		tableRef.value.clearRefreshSelected()
	})
}
// 添加标记完成方法
const markAsCompleted = (record) => {
	uni.showModal({
		title: '确认',
		content: '确定要将此预订标记为已完成吗？',
		success: (res) => {
			if (res.confirm) {
				message.success('已标记为完成')
				tableRef.value.refresh(true)
			}
		}
	})
}
const genderOptions = tool.dictList('GENDER')
</script>

<style scoped>
.ant-btn-primary.ant-btn-background-ghost {
	color: #52c41a;
	border-color: #52c41a;
}

.ant-btn-primary.ant-btn-background-ghost:hover {
	color: #73d13d;
	border-color: #73d13d;
}
</style>
