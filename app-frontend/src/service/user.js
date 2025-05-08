import { http } from '@/utils/http'

export const login = (account, password, device, validCode, validCodeReqNo) => {
  return http({
    url: `/auth/c/doLogin`,
    method: 'POST',
    data: {
      account,
      password,
      device,
      validCode,
      validCodeReqNo,
    },
  })
}

export const getPicCaptcha = () => {
  return http({
    url: `/auth/c/getPicCaptcha`,
    method: 'GET',
  })
}

export const register = (account, password, email, device, validCode, validCodeReqNo) => {
  return http({
    url: `/client/user/register`,
    method: 'POST',
    data: {
      account,
      password,
      email,
      device,
      validCode,
      validCodeReqNo,
    },
  })
}

export const userInfo = (account) => {
  return http({
    url: `/client/user/info2`,
    method: 'GET',
    query: { account },
  })
}

export const getUserInfo = () => {
  return http({
    url: `/client/user/info`,
    method: 'GET',
  })
}

export const saveBankCard = (
  account,
  bankCardId,
  bankCardExpiryMonth,
  bankCardExpiryYear,
  bankCardCvv,
) => {
  return http({
    url: `/client/user/bankcard/save`,
    method: 'POST',
    data: {
      account,
      bankCardId,
      bankCardExpiryMonth,
      bankCardExpiryYear,
      bankCardCvv,
    },
  })
}

export const feedback = (rentalId, rating, comment) => {
  return http({
    url: `/client/user/feedback`,
    method: 'POST',
    data: {
      rentalId,
      rating,
      comment,
    },
  })
}

export const canOfferDiscount = () => {
  return http({
    url: `/client/user/canOfferDiscount`,
    method: 'GET',
  })
}
