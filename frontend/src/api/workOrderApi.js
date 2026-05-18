import request from './axios-config.js'

export const workOrderApi = {
  getList(params) {
    return request.get('/workorders', { params })
  },

  getDetail(id, params) {
    return request.get(`/workorders/${id}`, { params })
  },

  create(data, params) {
    return request.post('/workorders', data, { params })
  },

  assign(id, data, params) {
    return request.put(`/workorders/${id}/assign`, data, { params })
  },

  accept(id, params) {
    return request.put(`/workorders/${id}/accept`, null, { params })
  },

  submitResult(id, data, params) {
    return request.put(`/workorders/${id}/submit-result`, data, { params })
  },

  confirm(id, params) {
    return request.put(`/workorders/${id}/confirm`, null, { params })
  },

  return(id, data, params) {
    return request.put(`/workorders/${id}/return`, data, { params })
  },

  close(id, data, params) {
    return request.put(`/workorders/${id}/close`, data, { params })
  },

  getLogs(id) {
    return request.get(`/workorders/${id}/logs`)
  },

  getStatistics() {
    return request.get('/workorders/statistics')
  },

  getEnums() {
    return request.get('/workorders/enums')
  }
}
