<template>
	<xn-form-container :title="formData.id ? 'Edit Rental Records' : 'Add Rental Records'" :width="700"
		v-model:open="open" :destroy-on-close="true" @close="onClose">
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="USER_ID：" name="userId">
						<a-input v-model:value="formData.userId" placeholder="Input USER_ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="SCOOTER_ID：" name="scooterId">
						<a-input v-model:value="formData.scooterId" placeholder="Input SCOOTER_ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="START_TIME：" name="startTime">
						<a-date-picker v-model:value="formData.startTime" value-format="YYYY-MM-DD HH:mm:ss" show-time
							placeholder="Please select START_TIME" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="END_TIME：" name="endTime">
						<a-date-picker v-model:value="formData.endTime" value-format="YYYY-MM-DD HH:mm:ss" show-time
							placeholder="Please select END_TIME" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="START_LOCATION_LATITUDE：" name="startLocationLatitude">
						<a-input v-model:value="formData.startLocationLatitude" placeholder="Input START_LOCATION_LATITUDE"
							allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="END_LOCATION_LATITUDE：" name="endLocationLatitude">
						<a-input v-model:value="formData.endLocationLatitude" placeholder="Input END_LOCATION_LATITUDE"
							allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="START_LOCATION_LONGITUDE：" name="startLocationLongitude">
						<a-input v-model:value="formData.startLocationLongitude" placeholder="Input START_LOCATION_LONGITUDE"
							allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="END_LOCATION_LONGITUDE：" name="endLocationLongitude">
						<a-input v-model:value="formData.endLocationLongitude" placeholder="Input END_LOCATION_LONGITUDE"
							allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="TOTAL_COST：" name="totalCost">
						<a-input v-model:value="formData.totalCost" placeholder="Input TOTAL_COST" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="STATUS：" name="status">
						<a-radio-group v-model:value="formData.status" placeholder="Please select STATUS"
							:options="statusOptions" />
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

<script setup name="rentalForm">
import tool from '@/utils/tool'
import { cloneDeep } from 'lodash-es'
import { required } from '@/utils/formRules'
import rentalApi from '@/api/rental/rentalApi'
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
	statusOptions.value = tool.dictList('1')
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
			rentalApi
				.rentalSubmitForm(formDataParam, formDataParam.id)
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
