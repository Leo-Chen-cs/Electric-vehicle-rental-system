<template>
	<a-row :gutter="10">
		<a-col :span="16" :xs="24" :sm="24" :md="24" :lg="16" :xl="16">
			<!-- User Info -->
			<!-- <sys-user-info-card class="mb-2" /> -->
			<!-- Business Data -->
			<sys-biz-data-card class="mb-2" />
			<!-- Operation Overview -->
			<sys-op-data-card class="mb-2" />
		</a-col>
		<a-col :span="8" :xs="24" :sm="24" :md="24" :lg="8" :xl="8">
			<!-- Slideshow -->
			<biz-slideshow-card :config="slideshowConfig" class="mb-2" />
			<!-- Basic Tools -->
			<sys-tool-data-card class="mb-2" />
			<!-- Weekly Visits -->
			<!-- <sys-vis-chart-data-card /> -->
		</a-col>
	</a-row>
</template>

<script setup name="indexHome">
// Slideshow configuration
const slideshowConfig = {
	options: {
		place: 'BACK_SYS_INDEX'
	}
}
</script>
