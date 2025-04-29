<template>
	<a-card :title="title" :bordered="false" :loading="apiLoading">
		<a-row>
			<a-col :span="6">
				<a-statistic :value="dataSource.userCount">
					<template #title>
						<user-outlined style="color: #1890ff" />
						Number of Users
					</template>
				</a-statistic>
			</a-col>
			<a-col :span="6">
				<a-statistic :value="dataSource.orgCount">
					<template #title>
						<cluster-outlined style="color: rgba(229, 159, 18, 0.35)" />
						Number of Organizations
					</template>
				</a-statistic>
			</a-col>
			<a-col :span="6">
				<a-statistic :value="dataSource.positionCount">
					<template #title>
						<apartment-outlined style="color: rgba(245, 6, 6, 0.2)" />
						Number of Positions
					</template>
				</a-statistic>
			</a-col>
			<a-col :span="6">
				<a-statistic :value="dataSource.roleCount">
					<template #title>
						<deployment-unit-outlined style="color: #09c755" />
						Number of Roles
					</template>
				</a-statistic>
			</a-col>
		</a-row>
	</a-card>
</template>

<script setup name="sysBizDataCard">
	import indexApi from '@/api/sys/indexApi'
	const title = ref('Business Data')
	const apiLoading = ref(false)
	const dataSource = ref({
		userCount: 0,
		roleCount: 0,
		orgCount: 0,
		positionCount: 0
	})
	onMounted(() => {
		apiLoading.value = true
		indexApi
			.indexBizDataCount()
			.then((data) => {
				dataSource.value = data
			})
			.catch(() => {})
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
