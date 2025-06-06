<template>
	<a-row :gutter="10">
		<a-col :xs="24" :sm="24" :md="24" :lg="5" :xl="5">
			<a-card :bordered="false" :loading="cardLoading" class="left-tree-container">
				<a-tree
					v-if="treeData.length > 0"
					v-model:expandedKeys="defaultExpandedKeys"
					:tree-data="treeData"
					:field-names="treeFieldNames"
					@select="treeSelect"
				/>
				<a-empty v-else :image="Empty.PRESENTED_IMAGE_SIMPLE" />
			</a-card>
		</a-col>
		<a-col :xs="24" :sm="24" :md="24" :lg="19" :xl="19">
			<a-card :bordered="false" class="xn-mb10">
				<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form" :model="searchFormState">
					<a-row :gutter="24">
						<a-col :span="8">
							<a-form-item name="searchKey" label="名称关键词">
								<a-input v-model:value="searchFormState.searchKey" placeholder="请输入职位名称关键词" />
							</a-form-item>
						</a-col>
						<a-col :span="8">
							<a-button type="primary" @click="tableRef.refresh(true)">
								<template #icon><SearchOutlined /></template>
								查询
							</a-button>
							<a-button class="snowy-button-left" @click="reset">
								<template #icon><redo-outlined /></template>
								重置
							</a-button>
						</a-col>
					</a-row>
				</a-form>
			</a-card>
			<a-card :bordered="false">
				<s-table
					ref="tableRef"
					:columns="columns"
					:data="loadData"
					:expand-row-by-click="true"
					:alert="options.alert.show"
					bordered
					:row-key="(record) => record.id"
					:row-selection="options.rowSelection"
				>
					<template #operator class="table-operator">
						<a-space>
							<a-button type="primary" @click="formRef.onOpen(undefined, searchFormState.orgId)">
								<template #icon><plus-outlined /></template>
								新增
							</a-button>
							<xn-batch-button
								buttonName="批量删除"
								icon="DeleteOutlined"
								buttonDanger
								:selectedRowKeys="selectedRowKeys"
								@batchCallBack="deleteBatchPosition"
							/>
						</a-space>
					</template>
					<template #bodyCell="{ column, record }">
						<template v-if="column.dataIndex === 'category'">
							{{ $TOOL.dictTypeData('POSITION_CATEGORY', record.category) }}
						</template>
						<template v-if="column.dataIndex === 'action'">
							<a @click="formRef.onOpen(record)">编辑</a>
							<a-divider type="vertical" />
							<a-popconfirm title="确定删除此职位？" @confirm="removeOrg(record)">
								<a-button type="link" danger size="small">删除</a-button>
							</a-popconfirm>
						</template>
					</template>
				</s-table>
			</a-card>
		</a-col>
	</a-row>
	<Form ref="formRef" @successful="tableRef.refresh(true)" />
</template>

<script setup name="sysPosition">
	import { Empty } from 'ant-design-vue'
	import { isEmpty } from 'lodash-es'
	import positionApi from '@/api/sys/positionApi'
	import orgApi from '@/api/sys/orgApi'
	import Form from './form.vue'

	const columns = [
		{
			title: '职位名称',
			dataIndex: 'name'
		},
		{
			title: '分类',
			dataIndex: 'category'
		},
		{
			title: '排序',
			dataIndex: 'sortCode',
			width: 100
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '150px'
		}
	]
	const selectedRowKeys = ref([])
	// 列表选择配置
	const options = {
		alert: {
			show: false,
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
	// 定义tableDOM
	const tableRef = ref(null)
	const formRef = ref()
	const searchFormRef = ref()
	const searchFormState = ref({})
	// 默认展开的节点
	const defaultExpandedKeys = ref([])
	const treeData = ref([])
	// 替换treeNode 中 title,key,children
	const treeFieldNames = { children: 'children', title: 'name', key: 'id' }
	const cardLoading = ref(true)

	// 表格查询 返回 Promise 对象
	const loadData = (parameter) => {
		return positionApi.positionPage(Object.assign(parameter, searchFormState.value)).then((res) => {
			return res
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 加载左侧的树
	orgApi.orgTree().then((res) => {
		cardLoading.value = false
		if (res !== null) {
			treeData.value = res
			if (isEmpty(defaultExpandedKeys.value)) {
				// 默认展开2级
				treeData.value.forEach((item) => {
					// 因为0的顶级
					if (item.parentId === '0') {
						defaultExpandedKeys.value.push(item.id)
						// 取到下级ID
						if (item.children) {
							item.children.forEach((items) => {
								defaultExpandedKeys.value.push(items.id)
							})
						}
					}
				})
			}
		}
	})
	// 点击树查询
	const treeSelect = (selectedKeys) => {
		if (selectedKeys.length > 0) {
			searchFormState.value.orgId = selectedKeys.toString()
		} else {
			delete searchFormState.value.orgId
		}
		tableRef.value.refresh(true)
	}
	// 删除
	const removeOrg = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		positionApi.positionDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchPosition = (params) => {
		positionApi.positionDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>

<style scoped>
	.ant-form-item {
		margin-bottom: 0 !important;
	}
	.snowy-button-left {
		margin-left: 8px;
	}
</style>
