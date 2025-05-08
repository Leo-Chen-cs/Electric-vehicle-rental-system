import { sm2 } from 'sm-crypto'

// 前后端约定的公钥
const cipherMode = 1 // 1 - C1C3C2，0 - C1C2C3，默认为1
const publicKey =
  '04298364ec840088475eae92a591e01284d1abefcda348b47eb324bb521bb03b0b2a5bc393f6b71dabb8f15c99a0050818b56b23f31743b93df9cf8948f15ddb54'

export const encryptPassword = (password) => {
  // sm2 加密的第三个参数为 1 时，加密结果都不一样
  return sm2.doEncrypt(password, publicKey, cipherMode)
}
