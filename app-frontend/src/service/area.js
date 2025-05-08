import { http } from '@/utils/http'

export const getAreaList = () => {
    return http({
      url: `/client/area/activeAreas`,
      method: 'GET',
    })
  }