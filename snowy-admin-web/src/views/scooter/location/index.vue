<template>
	<div class="map-page">
		<gaode-map ref="map" :api-key="apiKey" :height="mapHeight" :center="[116.39, 39.9]" :zoom="11"
			@complete="handleComplete" @marker-click="handleMarkerClick" />
	</div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import GaodeMap from '@/components/Map/gaodeMap/index.vue'
import scooterApi from '@/api/scooter/scooterApi'

const map = ref(null)
const mapHeight = ref(window.innerHeight)
const apiKey = '18e2e5a437f47d5b1bb3f04315c2bf6b'

const fetchScooterData = async () => {
	try {
		const data = await scooterApi.showScooter()
		if (data) {
			renderScooters(data)
		}
	} catch (error) {
		console.error('获取滑板车数据失败:', error)
	}
}

const renderScooters = (data) => {
	const markers = data.map((scooter) => {
		return {
			position: [scooter.longitude, scooter.latitude],
			title: `Scooter #${scooter.id}`,
			content: [
				`<div class="scooter-info">`,
				`<h4>Scooter Info</h4>`,
				`<p>ID: ${scooter.id}</p>`,
				`<p>Battery: ${scooter.batteryLevel}%</p>`,
				`<p>Status: ${scooter.status}</p>`,
				`</div>`
			]
		}
	})
	map.value?.renderMarker(markers)
}

const handleComplete = () => {
	fetchScooterData()
}

const handleMarkerClick = (position) => {
	map.value?.openInfoWindow(position)
}

onMounted(() => {
	window.addEventListener('resize', () => {
		mapHeight.value = window.innerHeight
	})
})
</script>

<style scoped>
.map-page {
	width: 100%;
	height: 100vh;
}

:deep(.scooter-info) {
	padding: 10px;
	min-width: 200px;
}

:deep(.scooter-info h4) {
	margin: 0 0 8px;
	color: #333;
}

:deep(.scooter-info p) {
	margin: 4px 0;
	font-size: 14px;
	color: #666;
}
</style>
