import { http } from '@/utils/http'

export const showScooter = () => {
  return http({
    url: `/client/scooter/show?num=15`,
    method: 'GET',
  })
}


export const getNearbyScooters = showScooter