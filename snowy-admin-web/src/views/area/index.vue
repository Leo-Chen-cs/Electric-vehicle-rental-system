<template>
  <div class="area-management-page">
    <!-- Map component -->
    <gaode-map ref="map" :api-key="apiKey" :height="mapHeight" :center="[103.98646533804845, 30.76435879673457]" :zoom="16"
      @complete="handleComplete" />
    <!-- Area settings sidebar card -->
    <div class="area-settings-card">
      <div class="card-header">
        <h3>Area Management</h3>
      </div>
      
      <a-tabs v-model:activeKey="activeTab">
        <!-- Area list tab -->
        <a-tab-pane key="list" tab="Area List">
          <div class="area-list">
            <a-list :data-source="areaList" :loading="loading">
              <template #renderItem="{ item }">
                <a-list-item>
                  <a-list-item-meta :title="item.areaName">
                    <template #description>
                      <div>
                        <a-tag :color="item.areaType === 'OPERATIONAL' ? 'green' : item.areaType === 'NO_PARKING' ? 'red' : 'yellow'">
                          {{ item.areaType === 'OPERATIONAL' ? 'Operational Area' : item.areaType === 'NO_PARKING' ? 'No Parking Zone' : 'Parking Area' }}
                        </a-tag>
                        <a-tag :color="item.isActive ? 'blue' : 'gray'">
                          {{ item.isActive ? 'Active' : 'Inactive' }}
                        </a-tag>
                      </div>
                    </template>
                  </a-list-item-meta>
                  <template #actions>
                    <a-button type="link" @click="editArea(item)">Edit</a-button>
                    <a-popconfirm
                      title="Are you sure you want to delete this area?"
                      @confirm="deleteArea(item.id)"
                      ok-text="Yes"
                      cancel-text="No"
                    >
                      <a-button type="link" danger>Delete</a-button>
                    </a-popconfirm>
                  </template>
                </a-list-item>
              </template>
            </a-list>
            <div class="add-button">
              <a-button type="primary" @click="createNewArea">
                <template #icon><plus-outlined /></template>
                Add New Area
              </a-button>
            </div>
          </div>
        </a-tab-pane>
        
        <!-- Area edit tab -->
        <a-tab-pane key="edit" tab="Edit Area" :force-render="true">
          <a-form :model="areaForm" layout="vertical">
            <a-form-item label="Area Name" name="areaName" 
                          :rules="[{ required: true, message: 'Please enter area name' }]">
              <a-input v-model:value="areaForm.areaName" placeholder="Please enter area name" />
            </a-form-item>
            
            <a-form-item label="Area Type" name="areaType" 
                          :rules="[{ required: true, message: 'Please select area type' }]">
              <a-radio-group v-model:value="areaForm.areaType">
                <a-radio value="OPERATIONAL">Operational Area</a-radio>
                <a-radio value="NO_PARKING">No Parking Zone</a-radio>
                <a-radio value="PARKING">Parking Area</a-radio>
              </a-radio-group>
            </a-form-item>
            
            <a-form-item label="Active" name="isActive">
              <a-switch v-model:checked="areaForm.isActive" />
            </a-form-item>
            
            <a-form-item name="color">
              <template #label>
                <div class="color-label">
                  <span>Area Color</span>
                  <div class="color-preview" :style="{ backgroundColor: areaForm.color || '#1890ff' }"></div>
                </div>
              </template>
              <a-input v-model:value="areaForm.color" placeholder="e.g.: #FF5500" />
            </a-form-item>
            
            <a-form-item label="Area Boundary">
              <div class="drawing-instructions">
                <p>Draw area on the map with these steps:</p>
                <ol>
                  <li>Click "Start Drawing" button</li>
                  <li>Click on the map to draw area boundary</li>
                  <li>Double-click to finish drawing</li>
                </ol>
              </div>
              <div class="drawing-controls">
                <a-button type="primary" @click="startDrawing" :disabled="isDrawing">
                  Start Drawing
                </a-button>
                <a-button @click="clearDrawing" :disabled="!hasDrawing">
                  Clear Drawing
                </a-button>
              </div>
            </a-form-item>
            
            <a-divider />
            
            <div class="form-actions">
              <a-button @click="cancelEdit">Cancel</a-button>
              <a-button type="primary" @click="saveArea" :loading="saving">
                Save Area
              </a-button>
            </div>
          </a-form>
        </a-tab-pane>
      </a-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import GaodeMap from '@/components/Map/gaodeMap/index.vue'
import areaApi from '@/api/area/areaApi'
import AMapLoader from '@amap/amap-jsapi-loader'

const map = ref(null)
const mapHeight = ref(window.innerHeight)
const apiKey = '18e2e5a437f47d5b1bb3f04315c2bf6b'
const activeTab = ref('list')
const loading = ref(false)
const saving = ref(false)
const isDrawing = ref(false)
const hasDrawing = ref(false)
const areaList = ref([])
const currentPolygon = ref(null)

// Form data
const areaForm = reactive({
  id: null,
  areaName: '',
  areaType: 'OPERATIONAL',
  isActive: true,
  color: '#1890ff',
  coordinates: null
})

// Fetch all areas
const fetchAreas = async () => {
  loading.value = true
  try {
    const response = await areaApi.areaPage({})
    // 处理从后端接收到的数据，将坐标字符串转换为数组
    areaList.value = (response.records || []).map(area => {
      // 确保坐标数据是数组格式
      let coordinates = null
      if (area.coordinates) {
        try {
          if (typeof area.coordinates === 'string') {
            coordinates = JSON.parse(area.coordinates)
          } else {
            coordinates = area.coordinates
          }
        } catch (e) {
          console.error('Failed to parse coordinates for area:', area.id, e)
          coordinates = null
        }
      }
      
      return {
        ...area,
        coordinates: coordinates
      }
    })
    renderAreaPolygons()
  } catch (error) {
    console.error('Failed to fetch area data:', error)
  } finally {
    loading.value = false
  }
}

// Edit area
const editArea = (item) => {
  activeTab.value = 'edit'
  areaForm.id = item.id
  areaForm.areaName = item.areaName
  areaForm.areaType = item.areaType
  areaForm.isActive = item.isActive
  
  // 根据区域类型设置颜色，优先使用已有颜色
  if (item.color) {
    areaForm.color = item.color
  } else {
    // 如果没有已设置的颜色，根据类型自动设置
    areaForm.color = item.areaType === 'NO_PARKING' ? '#ff4d4f' : item.areaType === 'OPERATIONAL' ? '#1890ff' : '#52c41a'
  }
  
  areaForm.coordinates = item.coordinates
  renderAreaPolygons()
}

// Delete area
const deleteArea = async (id) => {
  try {
    // 根据后端delete方法的定义，需要发送一个包含AreaIdParam对象的数组
    await areaApi.areaDelete([{ id: id }])
    message.success('Area deleted successfully')
    await fetchAreas()
  } catch (error) {
    console.error('Failed to delete area:', error)
    message.error('Failed to delete area')
  }
}

// Create new area
const createNewArea = () => {
  activeTab.value = 'edit'
  areaForm.id = null
  areaForm.areaName = ''
  areaForm.areaType = 'OPERATIONAL'
  areaForm.isActive = true
  areaForm.color = '#1890ff'
  areaForm.coordinates = null
}

// Cancel edit
const cancelEdit = () => {
  activeTab.value = 'list'
  clearDrawing()
}

// Save area
const saveArea = async () => {
  // Validate required fields
  if (!areaForm.areaName || !areaForm.areaType) {
    message.error('Please enter area name and select area type')
    return
  }
  
  // Validate if area has been drawn
  if (!areaForm.coordinates) {
    message.error('Please draw area boundary on the map')
    return
  }
  
  saving.value = true
  
  try {
    // 创建提交的数据对象，将坐标数组转换为JSON字符串
    const submitData = {
      id: areaForm.id,
      areaName: areaForm.areaName,
      areaType: areaForm.areaType,
      isActive: areaForm.isActive,
      color: areaForm.color,
      coordinates: JSON.stringify(areaForm.coordinates) // 将数组转换为JSON字符串
    }
    
    if (areaForm.id) {
      // Update existing area
      await areaApi.areaSubmitForm(submitData, true)
      message.success('Area updated successfully')
    } else {
      // Create new area
      await areaApi.areaSubmitForm(submitData)
      message.success('Area created successfully')
    }
    
    // Refresh area list and display
    await fetchAreas()
    activeTab.value = 'list'
  
  } catch (error) {
    console.error('Failed to save area:', error)
    message.error('Failed to save area')
  } finally {
    saving.value = false
  }
}

// Map load complete callback
const handleComplete = () => {
  fetchAreas()
}

// Watch color changes to update polygon color on map
watch(() => areaForm.color, (newColor) => {
  if (currentPolygon.value && newColor) {
    currentPolygon.value.setOptions({
      strokeColor: newColor,
      fillColor: newColor
    })
  }
})

// 添加新的watch函数，监听areaType变化自动设置颜色
watch(() => areaForm.areaType, (newType) => {
  // 根据区域类型自动设置颜色
  if (newType === 'NO_PARKING') {
    areaForm.color = '#ff4d4f'
  } else if (newType === 'OPERATIONAL') {
    areaForm.color = '#1890ff'
  } else if (newType === 'PARKING') {
    areaForm.color = '#ffb300'  // 停车区域使用绿色
  }
})

// Modify startDrawing method, use imported AMapLoader instead of window.AMapLoader
const startDrawing = async () => {
  if (!map.value) return
  
  
  isDrawing.value = true
  message.info('Click on the map to add area boundary points, double-click to finish drawing')
  
  try {
    // Directly get map instance
    const mapInstance = map.value.getMapInstance()
    if (!mapInstance) {
      message.error('Map instance not initialized')
      return
    }
    
    // Load AMap API
    const AMap = await AMapLoader.load({
      key: apiKey,
      version: '2.0',
      plugins: ['AMap.MouseTool']
    })
    
    // Create drawing tool
    const mouseTool = new AMap.MouseTool(mapInstance)
    
    // Enable polygon drawing
    mouseTool.polygon({
      strokeColor: areaForm.color,
      fillColor: areaForm.color,
      fillOpacity: 0.5,
      strokeOpacity: 0.8,
      strokeWeight: 2
    })
    
    // Drawing completion event
    mouseTool.on('draw', function(e) {
      // Get drawn polygon
      const polygon = e.obj
      
      // Get polygon path coordinates
      const path = polygon.getPath()
      
      // Save coordinates to form
      const coordinates = path.map(item => [item.lng, item.lat])
      areaForm.coordinates = coordinates
      
      // Save current polygon reference
      currentPolygon.value = polygon
      
      // Set drawing state
      isDrawing.value = false
      hasDrawing.value = true
      
      // Close drawing tool
      mouseTool.close()
    })
  } catch (error) {
    console.error('Failed to initialize drawing tool:', error)
    message.error('Failed to initialize drawing tool')
    isDrawing.value = false
  }
}

// 清除当前绘制的区域
const clearDrawing = () => {
  if (currentPolygon.value) {
    // 获取地图实例
    const mapInstance = map.value.getMapInstance()
    if (mapInstance) {
      // 从地图上移除当前多边形
      mapInstance.remove(currentPolygon.value)
      // 重置当前多边形引用
      currentPolygon.value = null
      // 重置绘图状态
      isDrawing.value = false
      hasDrawing.value = false
      // 清除表单中的坐标数据
      areaForm.coordinates = null
      
      // 重新渲染已保存的区域
      renderAreaPolygons()
    }
  }
}

// 在地图上渲染所有区域多边形
const renderAreaPolygons = () => {
  if (!map.value) return
  
  // 清除地图上所有覆盖物
  map.value.clearOverlay()
  
  // 准备所有区域的多边形数据
  const polygons = []
  
  // 准备禁停区域的标记点数据
  const noParkingMarkers = []
  
  // 准备可运行区域的标记点数据
  const operationalMarkers = []
  
  // 准备停车区域的标记点数据
  const parkingMarkers = []
  
  // 添加已保存的区域
  areaList.value.forEach(area => {
    if (area.coordinates && area.coordinates.length > 0) {
      // 添加区域多边形
      polygons.push({
        points: area.coordinates.map(coord => ({
          position: coord
        })),
        options: {
          strokeColor: area.color || '#1890ff',
          fillColor: area.color || '#1890ff',
          fillOpacity: 0.3,
          strokeOpacity: 0.8,
          strokeWeight: 2
        }
      })
      
      // 如果是禁停区域，在区域中心添加禁停图标
      if (area.areaType === 'NO_PARKING' && area.coordinates.length >= 3) {
        // 计算多边形中心点
        const center = calculatePolygonCenter(area.coordinates)
        
        noParkingMarkers.push({
          position: center,
          title: area.areaName,
          content: `<div class="no-parking-info"><p>No Parking Zone: <span class="area-name">${area.areaName}</span></p></div>`
        })
      }
      
      // 如果是可运行区域，在区域中心添加可运行图标
      if (area.areaType === 'OPERATIONAL' && area.coordinates.length >= 3) {
        // 计算多边形中心点
        const center = calculatePolygonCenter(area.coordinates)
        
        operationalMarkers.push({
          position: center,
          title: area.areaName,
          content: `<div class="operational-info"><p>Operational Area: <span class="area-name">${area.areaName}</span></p></div>`
        })
      }
      
      // 如果是停车区域，在区域中心添加停车图标
      if (area.areaType === 'PARKING' && area.coordinates.length >= 3) {
        // 计算多边形中心点
        const center = calculatePolygonCenter(area.coordinates)
        
        parkingMarkers.push({
          position: center,
          title: area.areaName,
          content: `<div class="parking-info"><p>Parking Area: <span class="area-name">${area.areaName}</span></p></div>`
        })
      }
    }
  })
  
  // 如果有编辑中的区域，也添加到渲染列表
  if (activeTab.value === 'edit' && areaForm.coordinates && areaForm.coordinates.length > 0) {
    polygons.push({
      points: areaForm.coordinates.map(coord => ({
        position: coord
      })),
      options: {
        strokeColor: areaForm.color || '#1890ff',
        fillColor: areaForm.color || '#1890ff',
        fillOpacity: 0.5,
        strokeOpacity: 0.8,
        strokeWeight: 2
      }
    })
    
    // 如果编辑中的是禁停区域，也添加禁停图标
    if (areaForm.areaType === 'NO_PARKING' && areaForm.coordinates.length >= 3) {
      const center = calculatePolygonCenter(areaForm.coordinates)
      
      noParkingMarkers.push({
        position: center,
        title: areaForm.areaName || '新禁停区域',
        content: `<div class="no-parking-info"><p>No Parking Zone: <span class="area-name">${areaForm.areaName || '新禁停区域'}</span></p></div>`
      })
    }
    
    // 如果编辑中的是可运行区域，也添加可运行图标
    if (areaForm.areaType === 'OPERATIONAL' && areaForm.coordinates.length >= 3) {
      const center = calculatePolygonCenter(areaForm.coordinates)
      
      operationalMarkers.push({
        position: center,
        title: areaForm.areaName || '新可运行区域',
        content: `<div class="operational-info"><p>Operational Area: <span class="area-name">${areaForm.areaName || '新可运行区域'}</span></p></div>`
      })
    }
    
    // 如果编辑中的是停车区域，也添加停车图标
    if (areaForm.areaType === 'PARKING' && areaForm.coordinates.length >= 3) {
      const center = calculatePolygonCenter(areaForm.coordinates)
      
      parkingMarkers.push({
        position: center,
        title: areaForm.areaName || '新停车区域',
        content: `<div class="parking-info"><p>Parking Area: <span class="area-name">${areaForm.areaName || '新停车区域'}</span></p></div>`
      })
    }
  }
  
  // 渲染所有多边形
  polygons.forEach(polygon => {
    map.value.renderPolygon(polygon.points, polygon.options)
  })
  
  // 渲染所有禁停标记
  if (noParkingMarkers.length > 0) {
    map.value.renderNoParkingMarker(noParkingMarkers)
  }
  
  // 渲染所有可运行区域标记
  if (operationalMarkers.length > 0) {
    map.value.renderOperationalMarker(operationalMarkers)
  }
  
  // 渲染所有停车区域标记
  if (parkingMarkers.length > 0) {
    map.value.renderParkingMarker(parkingMarkers)
  }
}

// 计算多边形中心点
const calculatePolygonCenter = (coordinates) => {
  // 简单方法：求所有顶点的平均值
  const sumX = coordinates.reduce((sum, coord) => sum + coord[0], 0)
  const sumY = coordinates.reduce((sum, coord) => sum + coord[1], 0)
  
  return [sumX / coordinates.length, sumY / coordinates.length]
}

onMounted(() => {
  window.addEventListener('resize', () => {
    mapHeight.value = window.innerHeight
  })
})
</script>

<style scoped>
.area-management-page {
  width: 100%;
  height: 100vh;
  position: relative;
}

.area-settings-card {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 360px;
  max-height: calc(100vh - 220px);
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-header h3 {
  margin: 0;
}

.area-list-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  position: relative;
}

.area-list {
  flex: 1;
  overflow-y: auto;
  max-height: calc(100vh - 280px);
  padding-bottom: 60px;
}

.add-button {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: white;
  padding: 12px 0;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
  z-index: 1;
}

.color-label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.color-preview {
  width: 20px;
  height: 20px;
  border-radius: 4px;
  border: 1px solid #d9d9d9;
  display: inline-block;
  vertical-align: middle;
}

.drawing-instructions {
  margin-bottom: 12px;
  background-color: #f5f5f5;
  padding: 12px;
  border-radius: 4px;
}

/* 优化绘制说明部分的样式 */
.drawing-instructions p {
  margin-top: 0;
  margin-bottom: 8px;
  font-weight: 500;
}

.drawing-instructions ol {
  margin: 0;
  padding-left: 20px;
}

.drawing-instructions li {
  margin-bottom: 4px;
  line-height: 1.5;
}

.drawing-controls {
  display: flex;
  gap: 8px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

:deep(.no-parking-info) {
  padding: 10px;
  min-width: 150px;
}

:deep(.no-parking-info p) {
  margin: 4px 0;
  font-size: 14px;
  color: #ff4d4f;
  font-weight: bold;
}

:deep(.no-parking-info .area-name) {
  color: #333;
  font-weight: normal;
}

:deep(.operational-info) {
  padding: 10px;
  min-width: 150px;
}

:deep(.operational-info p) {
  margin: 4px 0;
  font-size: 14px;
  color: #1890ff;
  font-weight: bold;
}

:deep(.operational-info .area-name) {
  color: #333;
  font-weight: normal;
}

:deep(.parking-info) {
  padding: 10px;
  min-width: 150px;
}

:deep(.parking-info p) {
  margin: 4px 0;
  font-size: 14px;
  color: #52c41a;
  font-weight: bold;
}

:deep(.parking-info .area-name) {
  color: #333;
  font-weight: normal;
}
</style>
