import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/scooter/scooter/` + url, ...arg)

/**
 * 滑板管理Api接口管理器
 *
 * @author 张一风
 * @date  2025/02/10 12:59
 **/
export default {
	// 获取滑板管理分页
	scooterPage(data) {
		return request('page', data, 'get')
	},
	// 提交滑板管理表单 edit为true时为编辑，默认为新增
	scooterSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除滑板管理
	scooterDelete(data) {
		return request('delete', data)
	},
	// 获取滑板管理详情
	scooterDetail(data) {
		return request('detail', data, 'get')
	},
	showScooter() {
		return baseRequest(`/scooter/scooter/show`, { num: 5 }, 'get')
	},
	// 获取可用滑板车列表
	getAvailableScooters() {
		return baseRequest('/scooter/scooter/available', {}, 'get')
	}
}
