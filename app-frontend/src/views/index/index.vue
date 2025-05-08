<template>
  <div class="home-container">
    <!-- 地图容器 -->
    <div class="map-wrapper">
      <AMap :center="mapCenter" :zoom="mapZoom" :markers="scooterMarkers" :areas="areaList" :operationalZoomThreshold="15" @map-loaded="onMapLoaded"
        @marker-click="onMarkerClick" @map-click="onMapClick" />
    </div>

    <!-- 底部操作区 - 调整位置和样式，避免覆盖底部导航栏 -->
    <div class="operation-panel">
      <t-button theme="primary" block size="large" @click="refreshScooters">Refresh Nearby Scooters</t-button>
    </div>

    <!-- 租赁弹窗 -->

    <scooter-popup 
      :visible="showScooterDialog" 
      :scooter="selectedScooter || {}" 
      @close="closeScooterDialog" 
      @rent="rentSelectedScooter"
    />


  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import AMap from '@/components/AMap.vue';
import { getNearbyScooters } from '@/service/scooter';
import { getAreaList } from '@/service/area'; // 导入获取区域的方法
import ScooterPopup from './components/ScooterPopup.vue';

const router = useRouter();
const mapCenter = ref([103.98646533804845, 30.76435879673457]); // Default center: SWJTU
const mapZoom = ref(14);
const scooterMarkers = ref([]);
const areaList = ref([]); // 存储区域数据
const selectedScooter = ref(null);
const mapInstance = ref(null);
const showScooterDialog = ref(false);

// Map loaded
const onMapLoaded = (map) => {
  mapInstance.value = map;
  getCurrentLocation();
  fetchAreas(); // 获取区域数据
};

// Get current location
const getCurrentLocation = () => {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const { latitude, longitude } = position.coords;
        mapCenter.value = [longitude, latitude];
        fetchNearbyScooters(longitude, latitude);
      },
      (error) => {
        console.error('Failed to get location:', error);
        import('tdesign-mobile-vue').then(({ MessagePlugin }) => {
          MessagePlugin.error('Failed to get location. Please check location permissions.');
        });
        // Use default location to load scooters
        fetchNearbyScooters(mapCenter.value[0], mapCenter.value[1]);
      }
    );
  } else {
    import('tdesign-mobile-vue').then(({ MessagePlugin }) => {
      MessagePlugin.error('Your browser does not support geolocation.');
    });
    // Use default location to load scooters
    fetchNearbyScooters(mapCenter.value[0], mapCenter.value[1]);
  }
};

// Fetch nearby scooters
const fetchNearbyScooters = async (longitude, latitude) => {
  try {
    import('tdesign-mobile-vue').then(({ Toast }) => {
      Toast.loading({
        message: 'Loading nearby scooters...',
        duration: 0,
      });
    });

    const { data } = await getNearbyScooters(longitude, latitude);

    // Convert scooter data to map markers
    console.log('Nearby scooters data:', data);

    scooterMarkers.value = data.map(scooter => ({
      position: [scooter.longitude, scooter.latitude],
      title: `Scooter #${scooter.id}`,
      icon: scooter.status === 'available' ?
        'https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png' :
        'https://webapi.amap.com/theme/v1.3/markers/n/mark_r.png',
      data: scooter
    }));

    import('tdesign-mobile-vue').then(({ Toast }) => {
      Toast.clear();
    });
  } catch (error) {
    console.error('Failed to fetch nearby scooters:', error);
    import('tdesign-mobile-vue').then(({ Toast }) => {
      Toast.clear();
      Toast.error('Failed to fetch nearby scooters');
    });
  }
};

// Handle refresh scooters
const refreshScooters = () => {
  fetchNearbyScooters(mapCenter.value[0], mapCenter.value[1]);
};

// Handle marker click
const onMarkerClick = (markerData) => {
  selectedScooter.value = markerData.data;
  showScooterDialog.value = true;
};

// Handle map click
const onMapClick = () => {
  // selectedScooter.value = null; // Clear selection when clicking on empty area
};

// Close scooter dialog
const closeScooterDialog = () => {
  showScooterDialog.value = false;
};

// Rent selected scooter
const rentSelectedScooter = () => {
  if (selectedScooter.value) {
    router.push({
      path: `/rent/${selectedScooter.value.id}`,
      query: {
        brand: selectedScooter.value.brand // 传递brand参数
      }
    });
  }
};

// Rent scooter
const rentScooter = (scooterId) => {
  router.push(`/rent/${scooterId}`);
};

// 获取区域数据
const fetchAreas = async () => {
  try {
    const { data } = await getAreaList();
    
    // 处理坐标数据，确保为正确的格式
    areaList.value = data.map(area => {
      // 如果coordinates是字符串，需要转为JSON对象
      if (typeof area.coordinates === 'string' && area.coordinates) {
        try {
          // 确保坐标是已经是JSON格式
          JSON.parse(area.coordinates);
        } catch (e) {
          console.error(`区域 ${area.id} 坐标格式错误:`, e);
          area.coordinates = '[]'; // 如果解析失败，设置为空数组
        }
      }
      return area;
    });
  } catch (error) {
    console.error('获取区域数据失败:', error);
    import('tdesign-mobile-vue').then(({ Toast }) => {
      Toast.error('获取区域数据失败');
    });
  }
};

onMounted(() => {
  // Get position immediately when component is mounted to load nearby scooters
  getCurrentLocation();
});
</script>

<style lang="scss" scoped>
.home-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 50px);
  /* 减去底部导航栏的高度 */
  position: relative;
}

.page-title {
  text-align: center;
  padding: 10px 0;
  margin: 0;
}

.map-wrapper {
  flex: 1;
  position: relative;
  /* 确保地图不会超出容器 */
  min-height: 0;
}

.operation-panel {
  padding: 16px;
  background: white;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 10;
  /* 确保按钮不会覆盖底部导航栏 */
  margin-bottom: 8px;
}

// .scooter-dialog-content {
//   padding: 8px;

//   .info-row {
//     display: flex;
//     justify-content: space-between;
//     margin-bottom: 12px;

//     .label {
//       color: #666;
//       font-weight: normal;
//     }

//     .value {
//       font-weight: bold;
//     }
//   }
// }

.scooter-popup {
  width: 300px;
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  
  &-header {
    text-align: center;
    padding: 16px;
    border-bottom: 1px solid #eee;
    
    h3 {
      margin: 0;
      font-size: 20px;
      font-weight: 500;
    }
  }
  
  &-content {
    padding: 16px;
    
    .scooter-id {
      font-size: 18px;
      font-weight: bold;
      text-align: center;
      margin-bottom: 16px;
    }
    
    .scooter-info {
      .info-item {
        font-size: 16px;
        margin-bottom: 8px;
      }
    }
  }
  
  &-footer {
    display: flex;
    gap: 12px;
    padding: 16px;
    border-top: 1px solid #eee;
    
    .t-button {
      flex: 1;
    }
  }
}

/* 添加禁停图标样式 */
.no-parking-icon {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

</style>
