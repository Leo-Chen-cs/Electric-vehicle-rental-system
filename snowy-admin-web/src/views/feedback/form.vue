<template>
	<xn-form-container :title="formData.id ? 'Edit Feedback' : 'New Feedback'" :width="700" v-model:open="open"
		:destroy-on-close="true" @close="onClose">
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="USER_ID：" name="userId">
						<a-input v-model:value="formData.userId" placeholder="请输入USER_ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="RENTAL_ID：" name="rentalId">
						<a-input v-model:value="formData.rentalId" placeholder="请输入RENTAL_ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="RATING：" name="rating">
						<a-slider :min="1" v-model:value="formData.rating" :max="3" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="COMMENT：" name="comment">
						<a-input v-model:value="formData.comment" placeholder="请输入COMMENT" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="CREATED_AT：" name="createdAt">
						<a-date-picker v-model:value="formData.createdAt" value-format="YYYY-MM-DD HH:mm:ss" show-time
							placeholder="请选择CREATED_AT" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="UPDATED_AT：" name="updatedAt">
						<a-date-picker v-model:value="formData.updatedAt" value-format="YYYY-MM-DD HH:mm:ss" show-time
							placeholder="请选择UPDATED_AT" style="width: 100%" />
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit" :loading="submitLoading">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="feedbackForm">
import { cloneDeep } from 'lodash-es'
import { required } from '@/utils/formRules'
import feedbackApi from '@/api/feedback/feedbackApi'
// 抽屉状态
const open = ref(false)
const emit = defineEmits({ successful: null })
const formRef = ref()
// 表单数据
const formData = ref({})
const submitLoading = ref(false)

// 打开抽屉
const onOpen = (record) => {
	open.value = true
	if (record) {
		let recordData = cloneDeep(record)
		formData.value = Object.assign({}, recordData)
	}
}
// 关闭抽屉
const onClose = () => {
	formRef.value.resetFields()
	formData.value = {}
	open.value = false
}
// 默认要校验的
const formRules = {}
// 验证并提交数据
const onSubmit = () => {
	formRef.value
		.validate()
		.then(() => {
			submitLoading.value = true
			const formDataParam = cloneDeep(formData.value)
			feedbackApi
				.feedbackSubmitForm(formDataParam, formDataParam.id)
				.then(() => {
					onClose()
					emit('successful')
				})
				.finally(() => {
					submitLoading.value = false
				})
		})
		.catch(() => { })
}
// 抛出函数
defineExpose({
	onOpen
})
</script>
