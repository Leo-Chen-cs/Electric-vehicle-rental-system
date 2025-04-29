import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/user/user/` + url, ...arg)

/**
 * 用户管理Api接口管理器
 *
 * @author 张一风
 * @date  2025/02/09 23:14
 **/
export default {
	// 获取用户管理分页
	clientUserPage(data) {
		return request('page', data, 'get')
	},
	// 提交用户管理表单 edit为true时为编辑，默认为新增
	clientUserSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除用户管理
	clientUserDelete(data) {
		return request('delete', data)
	},
	// 获取用户管理详情
	clientUserDetail(data) {
		return request('detail', data, 'get')
	}
}
