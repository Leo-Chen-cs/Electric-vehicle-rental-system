<template>
	<xn-form-container title="Info" :width="1000" v-model:open="open" :destroy-on-close="true" @close="onClose">
		<a-descriptions bordered>
			<a-descriptions-item label="Title">{{ formData.title }}</a-descriptions-item>
			<a-descriptions-item label="Type">
				<a-tag :bordered="false" color="success" v-if="formData.type === 'NOTICE'">
					{{ $TOOL.dictTypeData('BIZ_NOTICE_TYPE', formData.type) }}
				</a-tag>
				<a-tag :bordered="false" color="processing" v-else-if="formData.type === 'ANNOUNCEMENT'">
					{{ $TOOL.dictTypeData('BIZ_NOTICE_TYPE', formData.type) }}
				</a-tag>
				<a-tag :bordered="false" color="warning" v-else-if="formData.type === 'WARNING'">
					{{ $TOOL.dictTypeData('BIZ_NOTICE_TYPE', formData.type) }}
				</a-tag>
			</a-descriptions-item>
			<a-descriptions-item label="Cover Image">
				<div v-if="formData.image">
					<a-image :src="formData.image" style="width: 100px; height: 50px; margin-bottom: -10px; margin-top: -10px" />
				</div>
				<span v-else>Unupload</span>
			</a-descriptions-item>
			<a-descriptions-item label="Content">
				<div v-html="formData.content"></div>
			</a-descriptions-item>
		</a-descriptions>
		<a-descriptions bordered :column="2" class="mt-2">
			<a-descriptions-item label="Digest">{{ formData.digest }}</a-descriptions-item>
			<a-descriptions-item label="Remark"><span>{{ formData.remark }}</span></a-descriptions-item>
			<a-descriptions-item label="Sort"><span>{{ formData.sortCode }}</span></a-descriptions-item>
			<a-descriptions-item label="Publish Position">
				<div v-if="formData.place">
					<a-tag v-for="textValue in JSON.parse(formData.place)" :key="textValue" color="processing">
						{{ $TOOL.dictTypeData('BIZ_NOTICE_PLACE', textValue) }}
					</a-tag>
				</div>
			</a-descriptions-item>
			<a-descriptions-item label="Create Person"><span>{{ formData.createUserName }}</span></a-descriptions-item>
			<a-descriptions-item label="Create Time"><span>{{ formData.createTime }}</span></a-descriptions-item>
			<a-descriptions-item label="Update Person"><span>{{ formData.updateUserName }}</span></a-descriptions-item>
			<a-descriptions-item label="Update Time"><span>{{ formData.updateTime }}</span></a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">Close</a-button>
			<a-button type="primary" @click="onClose" :loading="submitLoading">确定</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="bizNoticeDetail">
import bizIndexApi from '@/api/biz/bizIndexApi'
import { message } from 'ant-design-vue'
// 抽屉状态
const open = ref(false)
const emit = defineEmits({ successful: null })
// 表单数据
const formData = ref({})
const submitLoading = ref(false)
// 打开抽屉
const onOpen = (id) => {
	open.value = true
	if (id) {
		const param = {
			id: id
		}
		bizIndexApi.bizIndexNoticeDetail(param).then((data) => {
			formData.value = Object.assign({}, data)
		})
	} else {
		message.warning('None message found')
	}
}
// 关闭抽屉
const onClose = () => {
	formData.value = {}
	open.value = false
}
// 抛出函数
defineExpose({
	onOpen
})
</script>
