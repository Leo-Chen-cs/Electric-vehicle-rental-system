import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/pricing/pricing/` + url, ...arg)

/**
 * PricingApi接口管理器
 *
 * @author 张一风
 * @date  2025/02/10 20:58
 **/
export default {
	// 获取Pricing分页
	pricingPage(data) {
		return request('page', data, 'get')
	},
	// 提交Pricing表单 edit为true时为编辑，默认为新增
	pricingSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除Pricing
	pricingDelete(data) {
		return request('delete', data)
	},
	// 获取Pricing详情
	pricingDetail(data) {
		return request('detail', data, 'get')
	}
}
