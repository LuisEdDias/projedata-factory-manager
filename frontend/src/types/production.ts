export interface ProductionPlanItem {
  productCode: string
  productName: string
  quantityToProduce: number
  totalValue: number
  itemProfit: number
}

export interface ProductionPlanResponse {
  plan: ProductionPlanItem[]
  estimatedTotalRevenue: number
  estimatedTotalProfit: number
  remainingStock: Record<string, number>
}
