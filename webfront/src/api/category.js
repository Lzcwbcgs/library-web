import request from './request'

export function getCategoryList() {
  return request({
    url: '/categories',
    method: 'get'
  })
}

export function getCategoryTree() {
  return request({
    url: '/categories/tree',
    method: 'get'
  })
}

export function addCategory(data) {
  return request({
    url: '/categories',
    method: 'post',
    data
  })
}

export function updateCategory(id, data) {
  return request({
    url: `/categories/${id}`,
    method: 'put',
    data
  })
}

export function deleteCategory(id) {
  return request({
    url: `/categories/${id}`,
    method: 'delete'
  })
} 