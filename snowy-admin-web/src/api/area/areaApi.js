import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/area/area/` + url, ...arg)

/**
 * 区域管理Api接口管理器
 *
 * @author 胡勋
 * @date 2025/04/19 17:44
 **/
export default {
	// 获取区域管理分页
	areaPage(data) {
		return request('page', data, 'get')
	},
	// 提交区域管理表单 edit为true时为编辑，默认为新增
	areaSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除区域管理
	areaDelete(data) {
		return request('delete', data)
	},
	// 获取区域管理详情
	areaDetail(data) {
		return request('detail', data, 'get')
	},
	// 获取所有活跃区域
	getActiveAreas() {
		return baseRequest('/client/area/activeAreas', {}, 'get')
	},
	// 获取运营区域
	getOperationalAreas() {
		return baseRequest('/client/area/operationalArea', {}, 'get')
	},
	// 获取禁停区域
	getNoParkingAreas() {
		return baseRequest('/client/area/noParkingAreas', {}, 'get')
	},
	// 检查点是否在运营区域内
	isInOperationalArea(latitude, longitude) {
		return baseRequest('/client/area/isInOperationalArea', { lat: latitude, lng: longitude }, 'get')
	},
	// 检查点是否在禁停区域内
	isInNoParkingArea(latitude, longitude) {
		return baseRequest('/client/area/isInNoParkingArea', { lat: latitude, lng: longitude }, 'get')
	}
}
