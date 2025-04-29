<template>
	<xn-form-container :title="formData.id ? 'Edit Pricing' : 'Add Pricing'" :width="700" v-model:open="open"
		:destroy-on-close="true" @close="onClose">
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="Rental Duration (hours)：" name="hour">
						<a-input v-model:value="formData.hour" placeholder="Please enter rental duration (hours)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Price ($)：" name="price">
						<a-input v-model:value="formData.price" placeholder="Please enter price ($)" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="CREATED_AT：" name="createdAt">
						<a-date-picker v-model:value="formData.createdAt" value-format="YYYY-MM-DD HH:mm:ss" show-time
							placeholder="Please select CREATED_AT" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="UPDATED_AT：" name="updatedAt">
						<a-date-picker v-model:value="formData.updatedAt" value-format="YYYY-MM-DD HH:mm:ss" show-time
							placeholder="Please select UPDATED_AT" style="width: 100%" />
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">Close</a-button>
			<a-button type="primary" @click="onSubmit" :loading="submitLoading">Save</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="pricingForm">
import { cloneDeep } from 'lodash-es'
import { required } from '@/utils/formRules'
import pricingApi from '@/api/pricing/pricingApi'
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
			pricingApi
				.pricingSubmitForm(formDataParam, formDataParam.id)
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
