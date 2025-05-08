<template>
  <div ref="mapContainer" class="map-container" :style="{ height: height }"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, defineExpose } from 'vue';
import AMapLoader from '@amap/amap-jsapi-loader';

const props = defineProps({
  height: {
    type: String,
    default: '100%'
  },
  center: {
    type: Array,
    default: () => [116.397428, 39.90923] // 默认北京中心
  },
  zoom: {
    type: Number,
    default: 13
  },
  markers: {
    type: Array,
    default: () => []
  },
  areas: {
    type: Array,
    default: () => []
  },
  operationalZoomThreshold: {
    type: Number,
    default: 12  // 可运行区域显示的缩放阈值
  }
});

const emits = defineEmits(['map-loaded', 'marker-click', 'map-click']);

const mapContainer = ref(null);
let map = null;
let AMap = null;
let markers = [];
let polygons = [];  // 存储所有多边形
let noParkingMarkers = [];  // 存储所有禁停标记
let parkingMarkers = [];  // 存储所有停车区域标记

onMounted(() => {
  initMap();
});

onBeforeUnmount(() => {
  removeAllMarkers();
  if (map) {
    map.destroy();
    map = null;
    AMap = null;
  }
});

// 监听中心点变化
watch(() => props.center, (newCenter) => {
  if (map) {
    map.setCenter(newCenter);
  }
}, { deep: true });

// 监听标记变化
watch(() => props.markers, (newMarkers) => {
  if (map && AMap) {
    updateMarkers(newMarkers);
  }
}, { deep: true });

// 监听缩放级别变化
watch(() => props.zoom, (newZoom) => {
  if (map) {
    map.setZoom(newZoom);
    updateAreasVisibility(newZoom);
  }
}, { deep: true });

// 监听区域数据变化
watch(() => props.areas, (newAreas) => {
  if (map && AMap) {
    renderAreas(newAreas);
  }
}, { deep: true });

const initMap = async () => {
  try {
    AMap = await AMapLoader.load({
      key: import.meta.env.VITE_AMAP_KEY,
      version: '2.0',
      plugins: ['AMap.Scale', 'AMap.ToolBar', 'AMap.ControlBar', 'AMap.Geolocation']
    });

    // 创建地图实例
    map = new AMap.Map(mapContainer.value, {
      center: props.center,
      zoom: props.zoom,
      resizeEnable: true
    });

    // 添加基础控件
    map.addControl(new AMap.Scale());
    map.addControl(new AMap.ToolBar({
      position: 'RT',  // 设置为右上角位置
    }));;
    map.addControl(new AMap.ControlBar());

    // 添加定位控件 - 这会显示官方的用户位置标记
    const geolocation = new AMap.Geolocation({
      enableHighAccuracy: true,
      timeout: 10000,
      buttonPosition: 'RB',
      buttonOffset: new AMap.Pixel(10, 20),
      zoomToAccuracy: true,
      // showButton: true,  // 显示定位按钮
      showMarker: true,  // 显示定位标记
      showCircle: true,  // 显示精度圈
    });

    map.addControl(geolocation);

    // 立即开始定位
    geolocation.getCurrentPosition();

    // 初始化地图后加载标记
    if (props.markers.length > 0) {
      updateMarkers(props.markers);
    }

    // 初始化地图后加载区域
    if (props.areas.length > 0) {
      renderAreas(props.areas);
    }

    // 添加地图点击事件
    map.on('click', (e) => {
      emits('map-click', e);
    });

    // 添加地图缩放事件
    map.on('zoomchange', () => {
      const currentZoom = map.getZoom();
      updateAreasVisibility(currentZoom);
    });

    // 通知父组件地图加载完成
    emits('map-loaded', map);
  } catch (e) {
    console.error('高德地图加载失败', e);
  }
};

// 更新所有标记
const updateMarkers = (markerData) => {
  removeAllMarkers();

  markerData.forEach(item => {
    // 使用自定义内容创建标记，包含alt标签
    const markerContent = document.createElement('div');
    markerContent.className = 'custom-marker';
    
    const img = document.createElement('img');
    img.src = '/scooter.png';
    img.alt = `Scooter ${item.title || ''}`; // 添加alt标签
    img.width = 35;
    img.height = 35;
    
    markerContent.appendChild(img);
    
    const marker = new AMap.Marker({
      position: item.position,
      title: item.title,
      content: markerContent,  // 使用自定义内容
      anchor: 'bottom-center'
    });

    // 保存原始数据到marker的extData属性
    marker.setExtData(item.data);

    // 添加点击事件
    marker.on('click', () => {
      emits('marker-click', item);
    });

    marker.setMap(map);
    markers.push(marker);
  });
};

// 添加电动车标记
const addScooterMarker = (position, options = {}) => {
  if (!map || !AMap) return null;
  
  // 使用自定义内容创建标记，包含alt标签
  const markerContent = document.createElement('div');
  markerContent.className = 'custom-marker';
  
  const img = document.createElement('img');
  img.src = import.meta.env.VITE_SERVER_BASEURL + '/scooter.png';
  img.alt = options.title || 'Electric Scooter'; // 添加alt标签
  img.width = 35;
  img.height = 35;
  
  markerContent.appendChild(img);

  // 创建标记
  const marker = new AMap.Marker({
    position: position,
    content: markerContent,  // 使用自定义内容
    anchor: 'bottom-center',
    offset: new AMap.Pixel(0, 0),
    title: options.title || 'Scooter'
  });

  // 如果有提供文本标签
  if (options.label) {
    marker.setLabel({
      direction: 'bottom',
      offset: new AMap.Pixel(0, 5),
      content: `<div style="padding: 2px 5px; background-color: rgba(0,0,0,0.7); color:white; font-size:12px; border-radius:4px;">${options.label}</div>`
    });
  }

  // 添加到地图
  marker.setMap(map);
  markers.push(marker);

  return marker;
};

// 移除所有标记
const removeAllMarkers = () => {
  if (map && markers.length) {
    map.remove(markers);
    markers = [];
  }
};

// 渲染所有区域
const renderAreas = (areas) => {
  // 清除旧的多边形和标记
  clearAllPolygons();
  clearAllNoParkingMarkers();
  clearAllParkingMarkers();
  
  areas.forEach(area => {
    try {
      // 解析坐标字符串为坐标数组
      const path = JSON.parse(area.coordinates);
      
      // 创建多边形
      const polygon = createAreaPolygon(area, path);
      
      // 如果是禁停区域，添加禁停标记
      if (area.areaType === 'NO_PARKING' && area.isActive) {
        addNoParkingMarker(path, area);
      }
      
      // 如果是停车区域，添加停车标记
      if (area.areaType === 'PARKING' && area.isActive) {
        addParkingMarker(path, area);
      }
      
      // 根据当前缩放级别设置可运行区域可见性
      if (area.areaType === 'OPERATIONAL') {
        const currentZoom = map.getZoom();
        const isVisible = currentZoom <= props.operationalZoomThreshold;
        
        polygon.setOptions({
          visible: isVisible
        });
      }
      
      polygons.push(polygon);
    } catch (error) {
      console.error(`解析区域坐标失败: ${area.id}`, error);
    }
  });
  
  // 确保第一次渲染后就应用可见性规则
  updateAreasVisibility(map.getZoom());
};

// 创建区域多边形
const createAreaPolygon = (area, path) => {
  // 根据当前缩放级别决定可运行区域的初始可见性
  let visible = true;
  if (area.areaType === 'OPERATIONAL') {
    const currentZoom = map.getZoom();
    visible = currentZoom <= props.operationalZoomThreshold;
  }
  
  // 设置特定区域类型的颜色
  let strokeColor = area.color || '#0088ff';
  let fillColor = area.color || '#0088ff';
  let fillOpacity = 0.2;
  
  if (area.areaType === 'NO_PARKING') {
    fillOpacity = 0.4;
  } else if (area.areaType === 'PARKING') {
    fillColor = area.color || '#ffcc00'; // 停车区域默认黄色
    strokeColor = area.color || '#ffcc00';
    fillOpacity = 0.4; // 与禁停区域相同的透明度
  }
  
  const polygon = new AMap.Polygon({
    path: path,
    strokeColor: strokeColor,
    strokeWeight: 2,
    strokeOpacity: 0.8,
    fillColor: fillColor,
    fillOpacity: fillOpacity,
    zIndex: area.areaType === 'NO_PARKING' ? 100 : (area.areaType === 'PARKING' ? 90 : 50),
    cursor: 'pointer',
    visible: visible // 设置初始可见性
  });
  
  // 添加点击事件
  polygon.on('click', () => {
    emits('map-click', { type: 'area', data: area });
  });
  
  // 重要: 设置区域数据到多边形的extData属性
  polygon.setExtData(area);
  
  polygon.setMap(map);
  return polygon;
};

// 添加禁停区域标记
const addNoParkingMarker = (path, area) => {
  try {
    // 使用高德地图API创建临时多边形来获取中心点
    const tempPolygon = new AMap.Polygon({
      path: path
    });
    
    // 获取中心点
    const bounds = tempPolygon.getBounds();
    const center = bounds.getCenter();
    
    // 使用自定义内容创建标记，包含alt标签
    const markerContent = document.createElement('div');
    markerContent.className = 'custom-marker';
    
    const img = document.createElement('img');
    img.src = '/noParking.svg';
    img.alt = area.areaName || 'No Parking Zone'; // 添加alt标签
    img.width = 32;
    img.height = 32;
    
    markerContent.appendChild(img);
    
    // 创建标记
    const marker = new AMap.Marker({
      map: map,
      position: center,
      title: area.areaName || 'No Parking Zone',
      content: markerContent,  // 使用自定义内容
      offset: new AMap.Pixel(-16, -16),
      zIndex: 110
    });
    
    marker.setMap(map);
    noParkingMarkers.push(marker);
    
    // 删除临时多边形
    tempPolygon.setMap(null);
    
    return marker;
  } catch (error) {
    console.error('Creating no parking marker failed:', error, 'Area:', area.id);
    return null;
  }
};

// 添加停车区域标记
const addParkingMarker = (path, area) => {
  try {
    // 使用高德地图API创建临时多边形来获取中心点
    const tempPolygon = new AMap.Polygon({
      path: path
    });
    
    // 获取中心点
    const bounds = tempPolygon.getBounds();
    const center = bounds.getCenter();
    
    // 使用自定义内容创建标记，包含alt标签
    const markerContent = document.createElement('div');
    markerContent.className = 'custom-marker';
    
    const img = document.createElement('img');
    img.src = '/parking.svg';
    img.alt = area.areaName || 'Parking Zone'; // 添加alt标签
    img.width = 32;
    img.height = 32;
    
    markerContent.appendChild(img);
    
    // 创建标记
    const marker = new AMap.Marker({
      map: map,
      position: center,
      title: area.areaName || 'Parking Zone',
      content: markerContent,  // 使用自定义内容
      offset: new AMap.Pixel(-16, -16),
      zIndex: 105
    });
    
    marker.setMap(map);
    parkingMarkers.push(marker);
    
    // 删除临时多边形
    tempPolygon.setMap(null);
    
    return marker;
  } catch (error) {
    console.error('Creating parking marker failed:', error, 'Area:', area.id);
    return null;
  }
};

// 根据缩放级别更新区域显示
const updateAreasVisibility = (zoomLevel) => {
  
  polygons.forEach(polygon => {
    const areaData = polygon.getExtData();
    if (areaData && areaData.areaType === 'OPERATIONAL') {
      // 当缩放级别大于阈值时隐藏可运行区域，小于等于阈值时显示
      const isVisible = zoomLevel <= props.operationalZoomThreshold;
      
      // 强制更新可见性设置
      polygon.hide();
      if (isVisible) {
        polygon.show();
      }
    }
  });
};

// 清除所有多边形
const clearAllPolygons = () => {
  if (map && polygons.length) {
    polygons.forEach(polygon => {
      polygon.setMap(null);
    });
    polygons = [];
  }
};

// 清除所有禁停标记
const clearAllNoParkingMarkers = () => {
  if (map && noParkingMarkers.length) {
    noParkingMarkers.forEach(marker => {
      marker.setMap(null);
    });
    noParkingMarkers = [];
  }
};

// 清除所有停车标记
const clearAllParkingMarkers = () => {
  if (map && parkingMarkers.length) {
    parkingMarkers.forEach(marker => {
      marker.setMap(null);
    });
    parkingMarkers = [];
  }
};

// 向外部暴露更多方法
defineExpose({
  addScooterMarker,
  removeAllMarkers,
  getMapInstance: () => map,
  getAMapInstance: () => AMap,
  renderAreas,
  clearAllPolygons,
  clearAllNoParkingMarkers,
  clearAllParkingMarkers
});
</script>

<style scoped>
.map-container {
  width: 100%;
  min-height: 300px;
  position: relative;
  overflow: hidden;
}

/* 添加自定义标记样式 */
:deep(.custom-marker) {
  display: flex;
  justify-content: center;
  align-items: center;
}

:deep(.custom-marker img) {
  display: block;
}
</style>
