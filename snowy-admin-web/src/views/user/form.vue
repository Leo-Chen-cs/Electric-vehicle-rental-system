<template>
	<xn-form-container :title="formData.id ? 'Edit User' : 'Add User'" :width="700" v-model:open="open"
		:destroy-on-close="true" @close="onClose">
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="Avatar：" name="avatar">
						<a-input v-model:value="formData.avatar" placeholder="Input Avatar" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Signature：" name="signature">
						<a-input v-model:value="formData.signature" placeholder="Input Signature" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Account：" name="account">
						<a-input v-model:value="formData.account" placeholder="Input Account" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Password：" name="password">
						<a-input disabled v-model:value="formData.password" placeholder="Input Password" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Name：" name="name">
						<a-input v-model:value="formData.name" placeholder="Input Name" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Nickname：" name="nickname">
						<a-input v-model:value="formData.nickname" placeholder="Input Nickname" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Gender：" name="gender">
						<a-radio-group v-model:value="formData.gender" placeholder="Please select gender"
							:options="genderOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Age：" name="age">
						<a-input v-model:value="formData.age" placeholder="Input Age" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Birthday：" name="birthday">
						<a-input v-model:value="formData.birthday" placeholder="Input Birthday" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Nation：" name="nation">
						<a-input v-model:value="formData.nation" placeholder="Input Nation" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Native Place：" name="nativePlace">
						<a-input v-model:value="formData.nativePlace" placeholder="Input Native Place" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Home Address：" name="homeAddress">
						<a-input v-model:value="formData.homeAddress" placeholder="Input Home Address" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Mailing Address：" name="mailingAddress">
						<a-input v-model:value="formData.mailingAddress" placeholder="Input Mailing Address" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="ID Card Type：" name="idCardType">
						<a-input v-model:value="formData.idCardType" placeholder="Input ID Card Type" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="ID Card Number：" name="idCardNumber">
						<a-input v-model:value="formData.idCardNumber" placeholder="Input ID Card Number" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Culture Level：" name="cultureLevel">
						<a-input v-model:value="formData.cultureLevel" placeholder="Input Culture Level" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Political Outlook：" name="politicalOutlook">
						<a-input v-model:value="formData.politicalOutlook" placeholder="Input Political Outlook" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="College：" name="college">
						<a-input v-model:value="formData.college" placeholder="Input College" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Education：" name="education">
						<a-input v-model:value="formData.education" placeholder="Input Education" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Edu Length：" name="eduLength">
						<a-input v-model:value="formData.eduLength" placeholder="Input Edu Length" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Degree：" name="degree">
						<a-input v-model:value="formData.degree" placeholder="Input Degree" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Phone：" name="phone">
						<a-input v-model:value="formData.phone" placeholder="Input Phone" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Email：" name="email">
						<a-input v-model:value="formData.email" placeholder="Input Email" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Home Tel：" name="homeTel">
						<a-input v-model:value="formData.homeTel" placeholder="Input Home Tel" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Office Tel：" name="officeTel">
						<a-input v-model:value="formData.officeTel" placeholder="Input Office Tel" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Emergency Contact：" name="emergencyContact">
						<a-input v-model:value="formData.emergencyContact" placeholder="Input Emergency Contact" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Emergency Phone：" name="emergencyPhone">
						<a-input v-model:value="formData.emergencyPhone" placeholder="Input Emergency Phone" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Emergency Address：" name="emergencyAddress">
						<a-input v-model:value="formData.emergencyAddress" placeholder="Input Emergency Address" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Last Login IP：" name="lastLoginIp">
						<a-input v-model:value="formData.lastLoginIp" placeholder="Input Last Login IP" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Last Login Address：" name="lastLoginAddress">
						<a-input v-model:value="formData.lastLoginAddress" placeholder="Input Last Login Address" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Last Login Time：" name="lastLoginTime">
						<a-date-picker v-model:value="formData.lastLoginTime" value-format="YYYY-MM-DD HH:mm:ss" show-time
							placeholder="Please select Last Login Time" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Last Login Device：" name="lastLoginDevice">
						<a-input v-model:value="formData.lastLoginDevice" placeholder="Input Last Login Device" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Latest Login IP：" name="latestLoginIp">
						<a-input v-model:value="formData.latestLoginIp" placeholder="Input Latest Login IP" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Latest Login Address：" name="latestLoginAddress">
						<a-input v-model:value="formData.latestLoginAddress" placeholder="Input Latest Login Address" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Latest Login Time：" name="latestLoginTime">
						<a-date-picker v-model:value="formData.latestLoginTime" value-format="YYYY-MM-DD HH:mm:ss" show-time
							placeholder="Please select Latest Login Time" style="width: 100%" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Latest Login Device：" name="latestLoginDevice">
						<a-input v-model:value="formData.latestLoginDevice" placeholder="Input Latest Login Device" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="User Status：" name="userStatus">
						<a-input v-model:value="formData.userStatus" placeholder="Input User Status" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Sort Code：" name="sortCode">
						<a-input v-model:value="formData.sortCode" placeholder="Input Sort Code" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="Extended Information：" name="extJson">
						<a-input v-model:value="formData.extJson" placeholder="Input Extended Information" allow-clear />
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

<script setup name="clientUserForm">
import tool from '@/utils/tool'
import { cloneDeep } from 'lodash-es'
import { required } from '@/utils/formRules'
import clientUserApi from '@/api/user/clientUserApi'
// 抽屉状态
const open = ref(false)
const emit = defineEmits({ successful: null })
const formRef = ref()
// 表单数据
const formData = ref({})
const submitLoading = ref(false)
const genderOptions = ref([])

// 打开抽屉
const onOpen = (record) => {
	open.value = true
	if (record) {
		let recordData = cloneDeep(record)
		formData.value = Object.assign({}, recordData)
	}
	genderOptions.value = tool.dictList('GENDER')
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
			clientUserApi
				.clientUserSubmitForm(formDataParam, formDataParam.id)
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
