import type { Unit } from '@/types/raw-material'

export interface ProductMaterialResponse {
  rawMaterialCode: string
  rawMaterialName: string
  quantityRequired: number
  unit: Unit
}

export interface ProductResponse {
  code: string
  name: string
  price: number
  materials: ProductMaterialResponse[]
}

export interface ProductMaterialRequest {
  rawMaterialCode: string
  quantityRequired: number
}

export interface ProductCreateRequest {
  code: string
  name: string
  price: number
  materials: ProductMaterialRequest[]
}

export interface ProductUpdateRequest {
  name?: string
  price?: number
}
