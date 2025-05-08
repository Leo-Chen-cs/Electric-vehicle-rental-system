import { http } from '@/utils/http'

export const rent = (
  id,
  startTime,
  period,
  totalCost,
  startLocationLongitude,
  startLocationLatitude,
) => {
  return http({
    url: `/client/rental/rent`,
    method: 'POST',
    data: {
      id,
      startTime,
      period,
      totalCost,
      startLocationLongitude,
      startLocationLatitude,
    },
    timeout: 60000,
  })
}

export const getRentalRecords = () => {
  return http({
    url: '/client/rental/record',
    method: 'GET',
    query: {},
    timeout: 15000, // 减少超时时间至15秒
  })
}

export const cancelRental = (id) => {
  return http({
    url: `/client/rental/cancel?id=${id}`,
    method: 'GET',
  })
}

export const extendRental = (id, hours, totalCost) => {
  return http({
    url: `/client/rental/extend`,
    method: 'POST',
    data: {
      id,
      hours,
      totalCost
    },
  })
}
