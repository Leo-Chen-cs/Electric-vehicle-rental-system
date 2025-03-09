<template>
	<xn-form-container :title="formData.id ? 'Edit Scooter' : 'Add Scooter'" :width="700" v-model:open="open"
		:destroy-on-close="true" @close="onClose">
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="Scooter Name：" name="scooterName">
						<a-input v-model:value="formData.scooterName" placeholder="Please enter scooter name" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Brand：" name="brand">
						<a-input v-model:value="formData.brand" placeholder="Please enter brand" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Model：" name="model">
						<a-input v-model:value="formData.model" placeholder="Please enter model" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Status：" name="status">
						<a-radio-group v-model:value="formData.status" placeholder="Please select status"
							:options="statusOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Battery Level：" name="batteryLevel">
						<a-slider v-model:value="formData.batteryLevel" :max="100" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Last Maintenance Date：" name="lastMaintenanceDate">
						<a-date-picker v-model:value="formData.lastMaintenanceDate" value-format="YYYY-MM-DD HH:mm:ss" show-time
							placeholder="Please select last maintenance date" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Longitude：" name="longitude">
						<a-input v-model:value="formData.longitude" placeholder="Please enter longitude" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Latitude：" name="latitude">
						<a-input v-model:value="formData.latitude" placeholder="Please enter latitude" allow-clear />
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

<script setup name="scooterForm">
import tool from '@/utils/tool'
import { cloneDeep } from 'lodash-es'
import { required } from '@/utils/formRules'
import scooterApi from '@/api/scooter/scooterApi'
// 抽屉状态
const open = ref(false)
const emit = defineEmits({ successful: null })
const formRef = ref()
// 表单数据
const formData = ref({})
const submitLoading = ref(false)
const statusOptions = ref([])

// 打开抽屉
const onOpen = (record) => {
	open.value = true
	if (record) {
		let recordData = cloneDeep(record)
		formData.value = Object.assign({}, recordData)
	}
	statusOptions.value = tool.dictList('0')
}
// 关闭抽屉
const onClose = () => {
	formRef.value.resetFields()
	formData.value = {}
	open.value = false
}
// 默认要校验的
const formRules = {
	scooterName: [required('Please enter scooter name')]
}
// 验证并提交数据
const onSubmit = () => {
	formRef.value
		.validate()
		.then(() => {
			submitLoading.value = true
			const formDataParam = cloneDeep(formData.value)
			scooterApi
				.scooterSubmitForm(formDataParam, formDataParam.id)
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
