import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/user/user/` + url, ...arg)

/**
 * 用户模块Api接口管理器
 *
 * @author 张一风
 * @date  2025/02/09 18:13
 **/
export default {
	// 获取用户模块分页
	sysUserPage(data) {
		return request('page', data, 'get')
	},
	// 提交用户模块表单 edit为true时为编辑，默认为新增
	sysUserSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除用户模块
	sysUserDelete(data) {
		return request('delete', data)
	},
	// 获取用户模块详情
	sysUserDetail(data) {
		return request('detail', data, 'get')
	}
}
