export enum Unit {
  UN = 'UN',
  KG = 'KG',
  L = 'L',
  M = 'M',
  HR = 'HR',
}

export interface RawMaterial {
  code: string
  name: string
  stockQuantity: number
  unit: Unit
}

export interface CreateRawMaterialRequest {
  code: string
  name: string
  initialStock: number
  unit: Unit
}

export interface UpdateRawMaterialRequest {
  name: string
}

export interface StockChangeRequest {
  quantity: number
}
