import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/feedback/feedback/` + url, ...arg)

/**
 * 反馈管理Api接口管理器
 *
 * @author 张一风
 * @date  2025/02/10 11:07
 **/
export default {
	// 获取反馈管理分页
	feedbackPage(data) {
		return request('page', data, 'get')
	},
	// 提交反馈管理表单 edit为true时为编辑，默认为新增
	feedbackSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除反馈管理
	feedbackDelete(data) {
		return request('delete', data)
	},
	// 获取反馈管理详情
	feedbackDetail(data) {
		return request('detail', data, 'get')
	}
}
