<template>
	<div id="SanDianTu03"></div>
</template>

<script setup>
	import { onMounted } from 'vue'
	import { Scatter } from '@antv/g2plot'

	onMounted(() => {
		fetch('https://gw.alipayobjects.com/os/bmw-prod/3e4db10a-9da1-4b44-80d8-c128f42764a8.json')
			.then((res) => res.json())
			.then((data) => {
				const scatterPlot = new Scatter('SanDianTu03', {
					appendPadding: 30,
					data,
					xField: 'xG conceded',
					yField: 'Shot conceded',
					colorField: 'Result',
					size: 5,
					shape: 'circle',
					pointStyle: {
						fillOpacity: 1
					},
					yAxis: {
						nice: true,
						line: {
							style: {
								stroke: '#aaa'
							}
						}
					},
					xAxis: {
						grid: {
							line: {
								style: {
									stroke: '#eee'
								}
							}
						},
						line: {
							style: {
								stroke: '#aaa'
							}
						}
					},
					label: {}
				})
				scatterPlot.render()
			})
	})
</script>
