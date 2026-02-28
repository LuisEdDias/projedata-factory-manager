import type { ProductionPlanResponse } from '@/types/production'
import api from './api'

const BASE = '/production'

export default {
  async calculatePlan(): Promise<ProductionPlanResponse> {
    const res = await api.get(`${BASE}/plan`)
    return res.data
  },
}
