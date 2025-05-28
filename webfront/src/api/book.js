import request from './request'

export function getBookList(params) {
  return request({
    url: '/books',
    method: 'get',
    params
  })
}

export function getBookDetail(id) {
  return request({
    url: `/books/${id}`,
    method: 'get'
  })
}

export function addBook(data) {
  return request({
    url: '/books',
    method: 'post',
    data
  })
}

export function updateBook(id, data) {
  return request({
    url: `/books/${id}`,
    method: 'put',
    data
  })
}

export function deleteBook(id) {
  return request({
    url: `/books/${id}`,
    method: 'delete'
  })
}

export function updateBookStatus(id, status) {
  return request({
    url: `/books/${id}/status`,
    method: 'put',
    params: { status }
  })
} 