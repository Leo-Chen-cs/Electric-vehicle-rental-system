<template>
	<div id="LouDouTu01"></div>
</template>

<script setup>
	import { onMounted } from 'vue'
	import { Funnel } from '@antv/g2plot'

	const data = [
		{ stage: '简历筛选', number: 253 },
		{ stage: '初试人数', number: 151 },
		{ stage: '复试人数', number: 113 },
		{ stage: '录取人数', number: 87 },
		{ stage: '入职人数', number: 59 }
	]

	onMounted(() => {
		const funnelPlot = new Funnel('LouDouTu01', {
			data: data,
			xField: 'stage',
			yField: 'number',
			legend: false
		})

		funnelPlot.render()
	})
</script>
