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
  unitCost: number
}

export interface CreateRawMaterialRequest {
  code: string
  name: string
  initialStock: number
  unit: Unit
  unitCost: number
}

export interface UpdateRawMaterialRequest {
  name: string
  unitCost: number
}

export interface StockChangeRequest {
  quantity: number
}
