// src/utils/storage.js

/**
 * 本地存储工具 - 基础读写功能
 * 管理 id_token, id, role 三个数据
 */

// ==================== 写入功能 ====================

/**
 * 保存认证数据
 */
export const saveAuthData = (data) => {
  localStorage.setItem('id_token', data.id_token || '')
  localStorage.setItem('id', data.id || '')
  localStorage.setItem('role', data.role || '')
}

/**
 * 保存 id_token
 */
export const saveIdToken = (id_token) => {
  localStorage.setItem('id_token', id_token)
}

/**
 * 保存用户ID
 */
export const saveId = (id) => {
  localStorage.setItem('id', id)
}

/**
 * 保存用户角色
 */
export const saveRole = (role) => {
  localStorage.setItem('role', role)
}

// ==================== 读取功能 ====================

/**
 * 读取所有认证数据
 */
export const getAuthData = () => {
  return {
    id_token: localStorage.getItem('id_token'),
    id: localStorage.getItem('id'),
    role: localStorage.getItem('role')
  }
}

/**
 * 读取 id_token
 */
export const getIdToken = () => {
  return localStorage.getItem('id_token')
}

/**
 * 读取用户ID
 */
export const getId = () => {
  return localStorage.getItem('id')
}

/**
 * 读取用户角色
 */
export const getRole = () => {
  return localStorage.getItem('role')
}

// ==================== 清除功能 ====================

/**
 * 清除所有认证数据
 */
export const clearAuthData = () => {
  localStorage.removeItem('id_token')
  localStorage.removeItem('id')
  localStorage.removeItem('role')
}