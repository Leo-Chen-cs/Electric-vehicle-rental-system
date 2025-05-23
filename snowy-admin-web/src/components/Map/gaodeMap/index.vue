<template>
	<div class="gaode-map" :style="{ height: `${height}px` }">
		<div :id="`container-${mid}`" class="map-container">Map loading...</div>
	</div>
</template>

<script setup>
import { ref, shallowRef, onMounted, onUnmounted } from 'vue'
import AMapLoader from '@amap/amap-jsapi-loader'

const props = defineProps({
	mid: { type: Number, default: new Date().getTime() },
	height: { type: Number, default: 800 },
	apiKey: { type: String, required: true },
	center: { type: Array },
	plugins: {
		type: Array,
		default: () => ['AMap.ToolBar', 'AMap.Scale']
	},
	zoom: { type: Number, default: 12 }
})

const emit = defineEmits(['complete', 'markerClick'])
const gaodeMap = shallowRef(null)
const gaodeMapInfoWindowObj = ref({})

const initMap = async () => {
	try {
		const AMap = await AMapLoader.load({
			key: props.apiKey,
			version: '2.0',
			plugins: props.plugins
		})

		gaodeMap.value = new AMap.Map(`container-${props.mid}`, {
			viewMode: '2D',
			zoom: props.zoom,
			center: props.center,
			pitch: 0,
			mapStyle: 'amap://styles/normal',
			features: ['bg', 'road'],
			expandZoomRange: false,
			animateEnable: false,
			dragEnable: true,
			zoomEnable: true,
			resizeEnable: true
		})

		if (props.plugins.includes('AMap.ToolBar')) {
			gaodeMap.value.addControl(new AMap.ToolBar({ position: 'RB', liteStyle: true }))
		}
		if (props.plugins.includes('AMap.Scale')) {
			gaodeMap.value.addControl(new AMap.Scale())
		}

		gaodeMap.value.on('complete', () => emit('complete'))
	} catch (error) {
		console.error('地图初始化失败:', error)
	}
}

const renderMarker = (dataArr) => {
	if (!gaodeMap.value || !dataArr.length) return

	const icon = new AMap.Icon({
		image: '/src/assets/icons/scooter.png', // 确保这个路径存在
		size: new AMap.Size(32, 32),
		imageSize: new AMap.Size(32, 32)
	})

	dataArr.forEach((point) => {
		const marker = new AMap.Marker({
			map: gaodeMap.value,
			position: point.position,
			title: point.title,
			icon,
			offset: new AMap.Pixel(-16, -16)
		})

		const infoWindow = new AMap.InfoWindow({
			content: Array.isArray(point.content) ? point.content.join('') : point.content,
			offset: new AMap.Pixel(0, -30)
		})

		marker.on('click', () => {
			infoWindow.open(gaodeMap.value, point.position)
			emit('markerClick', point)
		})
	})

	gaodeMap.value.setFitView()
}

const renderNoParkingMarker = (dataArr) => {
	if (!gaodeMap.value || !dataArr.length) return

	const icon = new AMap.Icon({
		image: '/src/assets/icons/noParking.svg', // 确保这个路径存在
		size: new AMap.Size(32, 32),
		imageSize: new AMap.Size(32, 32)
	})

	dataArr.forEach((point) => {
		const marker = new AMap.Marker({
			map: gaodeMap.value,
			position: point.position,
			title: point.title,
			icon,
			offset: new AMap.Pixel(-16, -16)
		})

		const infoWindow = new AMap.InfoWindow({
			content: Array.isArray(point.content) ? point.content.join('') : point.content,
			offset: new AMap.Pixel(0, -30)
		})

		marker.on('click', () => {
			infoWindow.open(gaodeMap.value, point.position)
			emit('markerClick', point)
		})
	})

	gaodeMap.value.setFitView()
}

const renderParkingMarker = (dataArr) => {
	if (!gaodeMap.value || !dataArr.length) return

	const icon = new AMap.Icon({
		image: '/src/assets/icons/parking.svg', // 确保这个路径存在
		size: new AMap.Size(32, 32),
		imageSize: new AMap.Size(32, 32)
	})

	dataArr.forEach((point) => {
		const marker = new AMap.Marker({
			map: gaodeMap.value,
			position: point.position,
			title: point.title,
			icon,
			offset: new AMap.Pixel(-16, -16)
		})

		const infoWindow = new AMap.InfoWindow({
			content: Array.isArray(point.content) ? point.content.join('') : point.content,
			offset: new AMap.Pixel(0, -30)
		})

		marker.on('click', () => {
			infoWindow.open(gaodeMap.value, point.position)
			emit('markerClick', point)
		})
	})

	gaodeMap.value.setFitView()
}

const renderOperationalMarker = (dataArr) => {
	if (!gaodeMap.value || !dataArr.length) return

	const icon = new AMap.Icon({
		image: '/src/assets/icons/operational.svg', // 确保这个路径存在
		size: new AMap.Size(32, 32),
		imageSize: new AMap.Size(32, 32)
	})

	dataArr.forEach((point) => {
		const marker = new AMap.Marker({
			map: gaodeMap.value,
			position: point.position,
			title: point.title,
			icon,
			offset: new AMap.Pixel(-16, -16)
		})

		const infoWindow = new AMap.InfoWindow({
			content: Array.isArray(point.content) ? point.content.join('') : point.content,
			offset: new AMap.Pixel(0, -30)
		})

		marker.on('click', () => {
			infoWindow.open(gaodeMap.value, point.position)
			emit('markerClick', point)
		})
	})

	gaodeMap.value.setFitView()
}

// 渲染 面
const renderPolygon = (dataArr, option = {}) => {
	const path = []
	dataArr.forEach((d) => {
		path.push(new AMap.LngLat(d.position[0], d.position[1]))
	})

	const polygon = new AMap.Polygon({
		path: path,
		strokeColor: option.strokeColor || 'blue',
		strokeWeight: option.strokeWeight || 2,
		strokeOpacity: option.strokeOpacity || 0.2,
		fillColor: option.fillColor || 'blue',
		fillOpacity: option.fillOpacity || 0.2,
		strokeStyle: 'solid'
	})
	gaodeMap.value.add(polygon)

	gaodeMap.value.setFitView([polygon])
}

// 添加 renderInfoWindow 方法
const renderInfoWindow = (dataArr) => {
	if (!gaodeMap.value || !dataArr.length) return

	dataArr.forEach((point) => {
		gaodeMapInfoWindowObj.value[point.position] = new AMap.InfoWindow({
			content: Array.isArray(point.content) ? point.content.join('') : point.content,
			offset: new AMap.Pixel(0, -30),
			closeWhenClickMap: true
		})
	})
}

// 添加 openInfoWindow 方法
const openInfoWindow = (position) => {
	const infoWindow = gaodeMapInfoWindowObj.value[position]
	if (infoWindow && gaodeMap.value) {
		infoWindow.open(gaodeMap.value, position)
	}
}

// 清理 覆盖物
const clearOverlay = () => {
	gaodeMap.value.clearMap()
}

// 获取地图实例
const getMapInstance = () => {
	return gaodeMap.value
}

onMounted(() => {
	initMap()
})

onUnmounted(() => {
	if (gaodeMap.value) {
		gaodeMap.value.destroy()
		gaodeMap.value = null
	}
})

defineExpose({
	renderMarker,
	renderNoParkingMarker,
	renderParkingMarker,
	renderOperationalMarker,
	renderInfoWindow,
	openInfoWindow,
	clearOverlay,
	renderPolygon,
	getMapInstance
})
</script>

<style scoped>
.gaode-map {
	width: 100%;
	position: relative;
}

.map-container {
	width: 100%;
	height: 100%;
}
</style>
