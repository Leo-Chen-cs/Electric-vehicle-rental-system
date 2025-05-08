import { http } from '@/utils/http'

export const getPriceForHours = () => {
  return http({
    url: `/client/pricing/page`,
    method: 'GET',
  })
}

export const getPrice = getPriceForHours
