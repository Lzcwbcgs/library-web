import request from './request'

// 获取用户个人信息
export function getProfile() {
  return request({
    url: '/user/profile',
    method: 'get'
  })
}

// 更新用户信息
export function updateProfile(data) {
  return request({
    url: '/user/profile',
    method: 'put',
    data
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/user/password',
    method: 'put',
    data
  })
}

// .el-pagination .el-input__inner {
//     -moz-appearance: textfield;
//     appearance: textfield;
// }