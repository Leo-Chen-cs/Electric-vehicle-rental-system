<template>
	<div id="BingZhuangTu01"></div>
</template>

<script setup>
import { onMounted } from 'vue'
import { Pie, G2 } from '@antv/g2plot'

const G = G2.getEngine('canvas')

const data = [
	{ sex: 'Male', sold: 0.45 },
	{ sex: 'Female', sold: 0.55 }
]

onMounted(() => {
	const piePlot = new Pie('BingZhuangTu01', {
		appendPadding: 10,
		data,
		angleField: 'sold',
		colorField: 'sex',
		radius: 0.66,
		color: ['#1677FF', '#f04864'],
		label: {
			content: (obj) => {
				const group = new G.Group({})
				group.addShape({
					type: 'image',
					attrs: {
						x: 0,
						y: 0,
						width: 40,
						height: 50,
						img:
							obj.sex === 'Male'
								? 'https://gw.alipayobjects.com/zos/rmsportal/oeCxrAewtedMBYOETCln.png'
								: 'https://gw.alipayobjects.com/zos/rmsportal/mweUsJpBWucJRixSfWVP.png'
					}
				})

				group.addShape({
					type: 'text',
					attrs: {
						x: 20,
						y: 54,
						text: obj.sex,
						textAlign: 'center',
						textBaseline: 'top',
						fill: obj.sex === 'Male' ? '#1677FF' : '#f04864'
					}
				})
				return group
			}
		},
		interactions: [{ type: 'element-active' }]
	})

	piePlot.render()
})
</script>
