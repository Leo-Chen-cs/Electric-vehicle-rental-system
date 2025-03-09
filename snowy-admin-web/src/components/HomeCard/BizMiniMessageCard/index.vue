<template>
	<a-card :title="title" :bordered="false" :loading="miniMessageLoading">
		<template #extra><a @click="leaveFor('/usercenter')">更多</a></template>
		<div class="index-message-list">
			<a-list :data-source="messageList" size="small" :loading="miniMessageLoading">
				<template #renderItem="{ item }">
					<a-list-item>
						<a-list-item-meta :description="item.createTime">
							<template #title>
								<a @click="messageDetail(item)">{{ item.subject }}</a>
							</template>
						</a-list-item-meta>
					</a-list-item>
				</template>
			</a-list>
		</div>
		<xn-form-container title="Info" :width="700" :visible="visible" :destroy-on-close="true" @close="onClose">
			<a-form ref="formRef" :model="formData" layout="vertical">
				<a-form-item label="subject：" name="subject">
					<span>{{ formData.subject }}</span>
				</a-form-item>
				<a-form-item label="send time：" name="createTime">
					<span>{{ formData.createTime }}</span>
				</a-form-item>
				<a-form-item label="content：" name="content">
					<span>{{ formData.content }}</span>
				</a-form-item>
				<a-form-item label="receive info：" name="receiveInfoList">
					<s-table ref="tableRef" :columns="columns" :data="loadData" :alert="false" :showPagination="false" bordered
						:row-key="(record) => record.id">
						<template #bodyCell="{ column, record }">
							<template v-if="column.dataIndex === 'read'">
								<span v-if="record.read" class="xn-color-d9d9d9">已读</span>
								<span v-else class="xn-color-ff4d4f">Unread</span>
							</template>
						</template>
					</s-table>
				</a-form-item>
			</a-form>
		</xn-form-container>
	</a-card>
</template>

<script setup name="miniMessage">
import indexApi from '@/api/sys/indexApi'
import { onMounted } from 'vue'
import router from '@/router'
const miniMessageLoading = ref(false)
const messageList = ref([])
const title = ref('Site message')
onMounted(() => {
	// 进来后执行查询
	getMessageList()
})
// 获取站内信列表
const getMessageList = () => {
	miniMessageLoading.value = true
	indexApi
		.indexMessageList()
		.then((data) => {
			messageList.value = data
		})
		.catch(() => { })
		.finally(() => {
			miniMessageLoading.value = false
		})
}
// 跳转，用于点击更多按钮
const leaveFor = (url = '/') => {
	router.replace({ path: url, query: { tab: 'userMessage' } })
}
// 点击详情
const messageDetail = (message) => {
	visible.value = true
	const param = {
		id: message.id
	}
	indexApi.indexMessageDetail(param).then((data) => {
		Object.assign(message, data)
		formData.value = message
		receiveInfoList.value = data.receiveInfoList
		tableRef.value.refresh(true)
	})
}

const loadData = () => {
	return new Promise((resolve) => {
		resolve(receiveInfoList.value)
	})
}
// 以下部分是抽屉的
const visible = ref(false)
const formRef = ref()
const receiveInfoList = ref([])
const formData = ref({})
const tableRef = ref()
const columns = [
	{
		title: 'name',
		dataIndex: 'receiveUserName'
	},
	{
		title: 'Whether read',
		dataIndex: 'read',
		width: 120
	}
]
// 关闭抽屉
const onClose = () => {
	visible.value = false
	formData.value = {}
	receiveInfoList.value = []
}
</script>

<style scoped>
.index-message-list {
	overflow: auto;
}

:deep(.ant-card-body) {
	padding-top: 0 !important;
}
</style>
