import type { Unit } from '@/types/raw-material'

export interface ProductMaterialResponse {
  rawMaterialCode: string
  rawMaterialName: string
  quantityRequired: Number
  unit: Unit
}

export interface ProductResponse {
  code: string
  name: string
  price: Number
  materials: ProductMaterialResponse[]
}

export interface ProductMaterialRequest {
  rawMaterialCode: string
  quantityRequired: Number
}

export interface ProductCreateRequest {
  code: string
  name: string
  price: Number
  materials: ProductMaterialRequest[]
}

export interface ProductUpdateRequest {
  name?: string
  price?: Number
}
