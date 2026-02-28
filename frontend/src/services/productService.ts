import api from './api'
import type { Page } from '@/types/pagination'
import type {
  ProductCreateRequest,
  ProductMaterialRequest,
  ProductResponse,
  ProductUpdateRequest,
} from '@/types/product'

const BASE = '/products'

export default {
  async list(page = 0, size = 20, sort = 'name,asc'): Promise<Page<ProductResponse>> {
    const res = await api.get(BASE, { params: { page, size, sort } })
    return res.data
  },

  async get(code: string): Promise<ProductResponse> {
    const res = await api.get(`${BASE}/${code}`)
    return res.data
  },

  async create(data: ProductCreateRequest): Promise<ProductResponse> {
    const res = await api.post(BASE, data)
    return res.data
  },

  async update(code: string, data: ProductUpdateRequest): Promise<ProductResponse> {
    const res = await api.put(`${BASE}/${code}`, data)
    return res.data
  },

  async delete(code: string): Promise<void> {
    await api.delete(`${BASE}/${code}`)
  },

  async addMaterial(code: string, data: ProductMaterialRequest): Promise<ProductResponse> {
    const res = await api.post(`${BASE}/${code}/materials`, data)
    return res.data
  },

  async updateMaterial(code: string, data: ProductMaterialRequest): Promise<ProductResponse> {
    const res = await api.put(`${BASE}/${code}/materials`, data)
    return res.data
  },

  async removeMaterial(code: string, materialCode: string): Promise<void> {
    await api.delete(`${BASE}/${code}/materials/${materialCode}`)
  },
}
