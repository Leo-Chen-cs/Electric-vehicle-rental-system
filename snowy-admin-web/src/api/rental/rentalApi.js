import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/rental/rental/` + url, ...arg)

/**
 * 租赁记录Api接口管理器
 *
 * @author 张一风
 * @date  2025/02/10 15:04
 **/
export default {
	// 获取租赁记录分页
	rentalPage(data) {
		return request('page', data, 'get')
	},
	// 提交租赁记录表单 edit为true时为编辑，默认为新增
	rentalSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除租赁记录
	rentalDelete(data) {
		return request('delete', data)
	},
	// 获取租赁记录详情
	rentalDetail(data) {
		return request('detail', data, 'get')
	},
	// 替用户租赁
	rentalForUser(id, userId, startTime, period, totalCost, startLocationLongitude, startLocationLatitude) {
		return baseRequest(
			'/rental/rental/rent',
			{
				id,
				userId,
				startTime,
				period,
				totalCost,
				startLocationLongitude,
				startLocationLatitude
			},
			'post'
		)
	}
}
