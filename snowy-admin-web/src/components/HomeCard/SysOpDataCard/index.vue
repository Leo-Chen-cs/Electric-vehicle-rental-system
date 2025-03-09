<template>
	<a-card :title="title" :bordered="false" :loading="apiLoading">
		<a-row>
			<!-- 因租户内定时任务需解决，暂先注释 -->
			<!--
			<a-col :span="4">
				<a-statistic :value="dataSource.jobCount">
					<template #title>
						<field-time-outlined style="color: #ec2c09" />
						任务调度
					</template>
</a-statistic>
</a-col>
-->
			<a-col :span="5">
				<a-statistic :value="dataSource.sysDictCount">
					<template #title>
						<read-outlined style="color: #4b4b4b" />
						System Dict
					</template>
				</a-statistic>
			</a-col>
			<a-col :span="5">
				<a-statistic :value="dataSource.bizDictCount">
					<template #title>
						<read-outlined style="color: #353779" />
						Business Dict
					</template>
				</a-statistic>
			</a-col>
			<a-col :span="5">
				<a-statistic :value="dataSource.backUserSessionCount">
					<template #title>
						<usergroup-delete-outlined style="color: #3ceecd" />
						Back Online User
					</template>
				</a-statistic>
			</a-col>
			<a-col :span="5">
				<a-statistic :value="dataSource.clientUserSessionCount">
					<template #title>
						<UserSwitchOutlined style="color: rgba(229, 159, 18, 0.35)" />
						Client Online User
					</template>
				</a-statistic>
			</a-col>
			<a-col :span="4">
				<a-statistic :value="dataSource.thirdUserCount">
					<template #title>
						<team-outlined style="color: #1890ff" />
						Third Part User
					</template>
				</a-statistic>
			</a-col>
		</a-row>
	</a-card>
</template>

<script setup name="sysBizDataCard">
import indexApi from '@/api/sys/indexApi'
const title = ref('System Overview')
const apiLoading = ref(false)
const dataSource = ref({
	sysDictCount: 0,
	bizDictCount: 0,
	backUserSessionCount: 0,
	clientUserSessionCount: 0,
	thirdUserCount: 0
})
onMounted(() => {
	apiLoading.value = true
	indexApi
		.indexOpDataCount()
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
