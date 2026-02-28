import api from './api'

const BASE = '/raw-materials'

export default {
  async list(page = 0, size = 20) {
    const res = await api.get(BASE, { params: { page, size } })
    return res.data
  },

  async get(code: string) {
    const res = await api.get(`${BASE}/${code}`)
    return res.data
  },

  async create(data: any) {
    const res = await api.post(BASE, data)
    return res.data
  },

  async updateName(code: string, data: any) {
    const res = await api.put(`${BASE}/${code}`, data)
    return res.data
  },

  async delete(code: string) {
    const res = await api.delete(`${BASE}/${code}`)
    return res.data
  },

  async addStock(code: string, quantity: number) {
    const res = await api.post(`${BASE}/${code}/stock/add`, { quantity })
    return res.data
  },

  async consumeStock(code: string, quantity: number) {
    const res = await api.post(`${BASE}/${code}/stock/consume`, { quantity })
    return res.data
  },
}
