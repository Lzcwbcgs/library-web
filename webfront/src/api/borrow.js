import request from './request'

export function getBorrowList(params) {
  return request({
    url: '/borrows',
    method: 'get',
    params
  })
}

export function getBorrowDetail(id) {
  return request({
    url: `/borrows/${id}`,
    method: 'get'
  })
}

export function createBorrow(data) {
  return request({
    url: '/borrows',
    method: 'post',
    data
  })
}

export function renewBorrow(id) {
  return request({
    url: `/borrows/${id}/renew`,
    method: 'post'
  })
}

export function returnBook(id) {
  return request({
    url: `/borrows/${id}/return`,
    method: 'post',
    headers: {
      'Content-Type': 'application/json',
      'X-Requested-With': 'XMLHttpRequest'
    },
    data: {} // 显式添加空的请求体，确保请求方法不会被改变
  })
}

export function handleOverdueRecords() {
  return request({
    url: '/borrows/handle-overdue',
    method: 'post'
  })
}

export function getBorrowStats() {
  return request({
    url: '/borrows/stats',
    method: 'get'
  })
}

export function getMyBorrowStats() {
  return request({
    url: '/borrows/stats/my',
    method: 'get'
  })
}