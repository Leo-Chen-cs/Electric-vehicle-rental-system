import { http } from '@/utils/http'

export const getFeedbackList = (current = 1, size = 10) => {
  return http({
    url: `/client/feedback/page?current=${current}&size=${size}`,
    method: 'GET',
  })
}
