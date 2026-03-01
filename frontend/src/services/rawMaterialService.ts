import api from './api'
import type { Page } from '@/types/pagination'
import type {
  CreateRawMaterialRequest,
  RawMaterial,
  StockChangeRequest,
  UpdateRawMaterialRequest,
} from '@/types/raw-material'

const BASE = '/raw-materials'

export default {
  async list(page = 0, size = 20, sort = 'name,asc'): Promise<Page<RawMaterial>> {
    const res = await api.get(BASE, {
      params: { page, size, sort },
    })
    return res.data
  },

  async get(code: string): Promise<RawMaterial> {
    const res = await api.get(`${BASE}/${code}`)
    return res.data
  },

  async create(data: CreateRawMaterialRequest): Promise<RawMaterial> {
    const res = await api.post(BASE, data)
    return res.data
  },

  async update(code: string, data: UpdateRawMaterialRequest): Promise<RawMaterial> {
    const res = await api.put(`${BASE}/${code}`, data)
    return res.data
  },

  async delete(code: string): Promise<void> {
    await api.delete(`${BASE}/${code}`)
  },

  async addStock(code: string, data: StockChangeRequest): Promise<RawMaterial> {
    const res = await api.post(`${BASE}/${code}/stock/add`, data)
    return res.data
  },

  async consumeStock(code: string, data: StockChangeRequest): Promise<RawMaterial> {
    const res = await api.post(`${BASE}/${code}/stock/consume`, data)
    return res.data
  },
}
