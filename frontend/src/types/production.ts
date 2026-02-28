export interface ProductionPlanItem {
  productCode: string
  productName: string
  quantityToProduce: number
  totalValue: number
}

export interface ProductionPlanResponse {
  plan: ProductionPlanItem[]
  estimatedTotalRevenue: number
  remainingStock: Record<string, number>
}
