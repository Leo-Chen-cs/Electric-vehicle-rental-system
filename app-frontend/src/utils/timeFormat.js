/**
 * 格式化时长显示（转换为周和天）
 * @param {number} hours - 小时数
 * @returns {string} 格式化后的时长字符串
 */
export const formatDuration = (hours) => {
  if (hours < 24) {
    return hours === 1 ? '1 hour' : `${hours} hours`
  } else if (hours < 168) { // 不到一周
    const days = Math.floor(hours / 24)
    const remainingHours = hours % 24

    let text = days === 1 ? '1 day' : `${days} days`
    if (remainingHours > 0) {
      text += remainingHours === 1 ? ' 1 hour' : ` ${remainingHours} hours`
    }
    return text
  } else { // 一周或更多
    const weeks = Math.floor(hours / 168)
    const remainingHours = hours % 168
    const days = Math.floor(remainingHours / 24)
    const finalHours = remainingHours % 24

    let text = weeks === 1 ? '1 week' : `${weeks} weeks`

    if (days > 0) {
      text += days === 1 ? ' 1 day' : ` ${days} days`
    }

    if (finalHours > 0) {
      text += finalHours === 1 ? ' 1 hour' : ` ${finalHours} hours`
    }

    return text
  }
}

export const formatDateTime = (timestamp) => {
  if (!timestamp) return ''

  return new Date(timestamp).toLocaleString('en-US', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}
