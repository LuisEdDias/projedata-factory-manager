import type { ProductionPlanResponse } from '@/types/production'
import api from './api'

const BASE = '/production'

export default {
  async calculateByRevenue(): Promise<ProductionPlanResponse> {
    const res = await api.get(`${BASE}/plan/revenue`)
    return res.data
  },
  async calculateByProfit(): Promise<ProductionPlanResponse> {
    const res = await api.get(`${BASE}/plan/profit`)
    return res.data
  },
}
