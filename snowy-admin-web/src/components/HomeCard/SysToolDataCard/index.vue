<template>
	<a-card :title="title" :bordered="false" :loading="apiLoading">
		<a-row>
			<a-col :span="12">
				<a-statistic :value="dataSource.fileCount">
					<template #title>
						<FileFilled style="color: #1890ff" />
						File
					</template>
				</a-statistic>
			</a-col>
			<a-col :span="12">
				<a-statistic :value="dataSource.smsCount">
					<template #title>
						<MessageFilled style="color: #07913e" />
						SMS
					</template>
				</a-statistic>
			</a-col>
			<a-col :span="12">
				<a-statistic :value="dataSource.emailCount">
					<template #title>
						<MailFilled style="color: #fada00" />
						Email
					</template>
				</a-statistic>
			</a-col>
			<a-col :span="12">
				<a-statistic :value="dataSource.messageCount">
					<template #title>
						<NotificationFilled style="color: #fada00" />
						Site Message
					</template>
				</a-statistic>
			</a-col>
		</a-row>
	</a-card>
</template>

<script setup name="sysToolDataCard">
import indexApi from '@/api/sys/indexApi'
const title = ref('Basic Tool')
const apiLoading = ref(false)
const dataSource = ref({
	fileCount: 0,
	smsCount: 0,
	emailCount: 0,
	messageCount: 0
})
onMounted(() => {
	apiLoading.value = true
	indexApi
		.indexToolDataCount()
		.then((data) => {
			dataSource.value = data
		})
		.catch(() => { })
		.finally(() => {
			apiLoading.value = false
		})
})
</script>

<style scoped>
:deep(.ant-card-body) {
	padding-top: 0 !important;
}
</style>
