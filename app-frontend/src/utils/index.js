import pagesJson from '@/pages.json'
import { translate as t } from '@/locale/index'

console.log(pagesJson)

/** 判断当前页面是否是tabbar页  */
export const getIsTabbar = () => {
  if (!Object.keys(pagesJson).includes('tabBar')) {
    return false
  }
  const pages = getCurrentPages()
  const lastPage = getLastItem(pages)
  const currPath = lastPage.route
  return !!pagesJson.tabBar.list.find((e) => e.pagePath === currPath)
}

/**
 * test i18n in not .vue file
 */
export const testI18n = () => {
  console.log(t('app.name'))
  // 下面替换为普通的alert
  alert(`i18n 测试: ${t('app.name')}`)
}

/**
 * 获取当前页面路由的 path 路劲和 redirectPath 路径
 * path 如 '/pages/login/index'
 * redirectPath 如 '/pages/demo/base/route-interceptor'
 */
export const currRoute = () => {
  // 获取当前URL
  const fullPath = window.location.pathname + window.location.search
  console.log(fullPath)
  // eg: /pages/login/index?redirect=%2Fpages%2Fdemo%2Fbase%2Froute-interceptor
  return getUrlObj(fullPath)
}

const ensureDecodeURIComponent = (url) => {
  if (url.startsWith('%')) {
    return ensureDecodeURIComponent(decodeURIComponent(url))
  }
  return url
}

/**
 * 解析 url 得到 path 和 query
 * 比如输入url: /pages/login/index?redirect=%2Fpages%2Fdemo%2Fbase%2Froute-interceptor
 * 输出: {path: /pages/login/index, query: {redirect: /pages/demo/base/route-interceptor}}
 */
export const getUrlObj = (url) => {
  const [path, queryStr] = url.split('?')
  console.log(path, queryStr)

  const query = {}
  if (queryStr) {
    queryStr.split('&').forEach((item) => {
      const [key, value] = item.split('=')
      console.log(key, value)
      query[key] = ensureDecodeURIComponent(value)
    })
  }
  return { path, query }
}

/**
 * 得到所有的需要登录的pages，包括主包和分包的
 * 这里设计得通用一点，可以传递key作为判断依据，默认是 needLogin, 与 route-block 配对使用
 * 如果没有传 key，则表示所有的pages，如果传递了 key, 则表示通过 key 过滤
 */
export const getAllPages = (key = 'needLogin') => {
  // 这里处理主包
  const pages = [
    ...pagesJson.pages
      .filter((page) => !key || page[key])
      .map((page) => ({
        ...page,
        path: `/${page.path}`,
      })),
  ]
  // 这里处理分包
  const subPages = []
  pagesJson.subPackages.forEach((subPageObj) => {
    // console.log(subPageObj)
    const { root } = subPageObj

    subPageObj.pages
      .filter((page) => !key || page[key])
      .forEach((page) => {
        subPages.push({
          ...page,
          path: `/${root}/${page.path}`,
        })
      })
  })
  const result = [...pages, ...subPages]
  console.log(`getAllPages by ${key} result: `, result)
  return result
}

/**
 * 得到所有的需要登录的pages，包括主包和分包的
 * 只得到 path 数组
 */
export const getNeedLoginPages = () => getAllPages('needLogin').map((page) => page.path)

/**
 * 得到所有的需要登录的pages，包括主包和分包的
 * 只得到 path 数组
 */
export const needLoginPages = getAllPages('needLogin').map((page) => page.path)

/** 主要是处理 arr.at(-1) 在安卓机上运行报错的 兼容性问题 */
export const getArrElementByIdx = (arr, index) => {
  if (index < 0) return arr[arr.length + index]
  if (index >= arr.length) return undefined
  return arr[index]
}

export const getLastItem = (arr) => getArrElementByIdx(arr, -1)
