<template>
  <div>
    <div class="container mt-5">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
          <h2 class="text-secondary mb-1">Detalhes do Produto</h2>
          <div class="text-muted">
            Código: <b>{{ product?.code }}</b>
          </div>
        </div>

        <div class="d-flex gap-2">
          <button class="btn btn-outline-secondary" @click="$router.back()">Voltar</button>
          <button
            class="btn btn-primary"
            @click="openEditProductModal"
            :disabled="loading || !product"
          >
            Editar Produto
          </button>
          <button class="btn btn-outline-danger" @click="deleteProduct()">Remover Produto</button>
        </div>
      </div>

      <div v-if="loading" class="text-center my-5">
        <div class="spinner-border text-primary" role="status"></div>
      </div>

      <div v-else>
        <div v-if="success" class="alert alert-success">{{ success }}</div>
        <div v-if="error" class="alert alert-danger">{{ error }}</div>

        <div class="card shadow-sm mb-3" v-if="product">
          <div class="card-body">
            <div class="row g-3">
              <div class="col-md-6">
                <div class="text-muted small">Nome</div>
                <div class="fw-bold">{{ product.name }}</div>
              </div>
              <div class="col-md-2">
                <div class="text-muted small">Preço</div>
                <div class="fw-bold">
                  R$
                  {{ Number(product.price).toLocaleString('pt-BR', { minimumFractionDigits: 2 }) }}
                </div>
              </div>
              <div class="col-md-2">
                <div class="text-muted small">Custo</div>
                <div class="fw-bold text-danger">
                  R$
                  {{
                    Number(product.totalMaterialCost).toLocaleString('pt-BR', {
                      minimumFractionDigits: 2,
                    })
                  }}
                </div>
              </div>
              <div class="col-md-2">
                <div class="text-muted small">Lucro Liquido</div>
                <div class="fw-bold text-success">
                  R$
                  {{
                    Number(product.unitProfit).toLocaleString('pt-BR', { minimumFractionDigits: 2 })
                  }}
                  ({{ margin() }})
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="card shadow-sm" v-if="product">
          <div class="card-header bg-light d-flex justify-content-between align-items-center">
            <div class="fw-bold">Composição do Produto</div>
            <button class="btn btn-sm btn-success" @click="openMaterialModal('ADD')">
              + Adicionar insumo
            </button>
          </div>

          <div class="card-body p-0">
            <div class="table-responsive">
              <table class="table table-hover table-striped mb-0">
                <thead class="table-dark">
                  <tr>
                    <th class="col-md-6">Material</th>
                    <th class="col-md-2 text-end">Quantidade</th>
                    <th class="col-md-1">Unidade</th>
                    <th class="col-auto text-center">Ações</th>
                  </tr>
                </thead>

                <tbody>
                  <tr v-if="product.materials.length === 0">
                    <td colspan="4" class="text-center text-muted py-4">
                      Nenhum material cadastrado.
                    </td>
                  </tr>

                  <tr v-for="material in product.materials" :key="material.rawMaterialCode">
                    <td class="align-middle">
                      <div class="fw-bold">{{ material.rawMaterialCode }}</div>
                      <div class="text-muted small">{{ material.rawMaterialName }}</div>
                    </td>

                    <td class="align-middle text-end">
                      {{ Number(material.quantityRequired).toLocaleString('pt-BR') }}
                    </td>

                    <td class="align-middle">{{ material.unit }}</td>

                    <td class="align-middle text-center">
                      <div class="d-flex gap-2 flex-wrap justify-content-center">
                        <button
                          class="btn btn-sm btn-outline-primary"
                          @click="openMaterialModal('EDIT', material)"
                        >
                          Editar
                        </button>
                        <button
                          class="btn btn-sm btn-outline-danger"
                          @click="removeMaterial(material.rawMaterialCode)"
                        >
                          Remover
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Edit Product Modal -->
    <div
      class="modal fade"
      ref="editProductModalEl"
      tabindex="-1"
      aria-hidden="true"
      data-bs-backdrop="static"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-light">
            <h5 class="modal-title fw-bold">Editar Produto</h5>
            <button type="button" class="btn-close" @click="closeEditProductModal"></button>
          </div>

          <div class="modal-body">
            <form @submit.prevent="updateProduct">
              <div class="mb-3">
                <label class="form-label">Nome</label>
                <input
                  v-model="editProductForm.name"
                  class="form-control"
                  :class="{ 'is-invalid': hasError('name') }"
                />
                <div class="invalid-feedback">{{ getError('name') }}</div>
              </div>
              <div class="mb-3">
                <label class="form-label">Preço</label>
                <input
                  v-model.number="editProductForm.price"
                  type="number"
                  step="0.01"
                  class="form-control"
                  :class="{ 'is-invalid': hasError('price') }"
                />
                <div class="invalid-feedback">{{ getError('price') }}</div>
              </div>
            </form>
          </div>

          <div class="modal-footer bg-light">
            <button class="btn btn-secondary" @click="closeEditProductModal">Cancelar</button>
            <button class="btn btn-primary" @click="updateProduct" :disabled="saving">
              <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
              Salvar
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Material Modal -->
    <div
      class="modal fade"
      ref="materialModalEl"
      tabindex="-1"
      aria-hidden="true"
      data-bs-backdrop="static"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-light">
            <h5 class="modal-title fw-bold">
              {{ materialMode === 'ADD' ? 'Adicionar material' : 'Editar material' }}
            </h5>
            <button type="button" class="btn-close" @click="closeMaterialModal"></button>
          </div>

          <div class="modal-body">
            <div v-if="materialError" class="alert alert-danger py-2">{{ materialError }}</div>

            <form @submit.prevent="confirmMaterial">
              <div class="mb-3">
                <label class="form-label">Matéria-prima</label>
                <select
                  v-model="materialForm.rawMaterialCode"
                  class="form-select"
                  :disabled="materialMode === 'EDIT'"
                >
                  <option value="" disabled>Selecione...</option>
                  <option v-for="rm in rawMaterials" :key="rm.code" :value="rm.code">
                    {{ rm.code }} - {{ rm.name }} ({{ rm.unit }})
                  </option>
                </select>
              </div>

              <div class="mb-3">
                <label class="form-label">Quantidade</label>
                <input
                  v-model.number="materialForm.quantityRequired"
                  type="number"
                  step="0.0001"
                  min="0"
                  class="form-control"
                />
              </div>
            </form>
          </div>

          <div class="modal-footer bg-light">
            <button class="btn btn-secondary" @click="closeMaterialModal">Cancelar</button>
            <button class="btn btn-primary" @click="confirmMaterial" :disabled="savingMaterial">
              <span v-if="savingMaterial" class="spinner-border spinner-border-sm me-2"></span>
              Confirmar
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Modal } from 'bootstrap'
import productService from '@/services/productService'
import rawMaterialService from '@/services/rawMaterialService'
import type { ProductResponse, ProductMaterialResponse } from '@/types/product'
import type { RawMaterial } from '@/types/raw-material'

const route = useRoute()
const code = String(route.params.code || '').toUpperCase()

const loading = ref(true)
const saving = ref(false)
const savingMaterial = ref(false)

const success = ref('')
const error = ref('')
const formErrors = ref<any[]>([])

const product = ref<ProductResponse | null>(null)
const rawMaterials = ref<RawMaterial[]>([])

const fetchProduct = async () => {
  loading.value = true
  error.value = ''
  try {
    product.value = await productService.get(code)
  } catch (err: any) {
    setError(err.detail || 'Falha ao carregar o produto.')
  } finally {
    loading.value = false
  }
}

// --- Material Modal State
const materialModalEl = ref<HTMLElement | null>(null)
let bsMaterialModal: Modal | null = null
type MaterialMode = 'ADD' | 'EDIT'
const closeMaterialModal = () => bsMaterialModal?.hide()
const materialError = ref('')

const materialMode = ref<MaterialMode>('ADD')
const materialForm = ref<{ rawMaterialCode: string; quantityRequired: number }>({
  rawMaterialCode: '',
  quantityRequired: 0,
})

const openMaterialModal = (mode: MaterialMode, mat?: ProductMaterialResponse) => {
  materialMode.value = mode
  materialError.value = ''
  if (mode === 'ADD') {
    materialForm.value = { rawMaterialCode: '', quantityRequired: 0 }
  } else if (mat) {
    materialForm.value = {
      rawMaterialCode: mat.rawMaterialCode,
      quantityRequired: Number(mat.quantityRequired),
    }
  }
  bsMaterialModal?.show()
}

const confirmMaterial = async () => {
  if (!product.value) return
  const rmCode = materialForm.value.rawMaterialCode
  const qty = Number(materialForm.value.quantityRequired)

  if (!rmCode || !Number.isFinite(qty) || qty <= 0) {
    materialError.value = 'Informe material e quantidade válida (> 0).'
    return
  }

  savingMaterial.value = true
  materialError.value = ''

  try {
    if (materialMode.value === 'ADD') {
      product.value = await productService.addMaterial(product.value.code, {
        rawMaterialCode: rmCode,
        quantityRequired: qty,
      })
      setSuccess('Material adicionado à receita.')
    } else {
      product.value = await productService.updateMaterial(product.value.code, {
        rawMaterialCode: rmCode,
        quantityRequired: qty,
      })
      setSuccess('Material atualizado.')
    }

    closeMaterialModal()
  } catch (err: any) {
    materialError.value = err.detail || 'Erro ao salvar material.'
  } finally {
    savingMaterial.value = false
  }
}

const fetchRawMaterials = async () => {
  const page = await rawMaterialService.list(0, 10000)
  rawMaterials.value = page.content
}

// --- Edit Product
const editProductModalEl = ref<HTMLElement | null>(null)
let bsEditProductModal: Modal | null = null
const editProductForm = ref<{ name: string; price: number }>({ name: '', price: 0 })

const openEditProductModal = () => {
  if (!product.value) return
  formErrors.value = []
  editProductForm.value = {
    name: product.value.name,
    price: Number(product.value.price),
  }
  bsEditProductModal?.show()
}

const closeEditProductModal = () => bsEditProductModal?.hide()

const updateProduct = async () => {
  if (!product.value) return
  saving.value = true
  try {
    const updated = await productService.update(product.value.code, {
      name: editProductForm.value.name.trim(),
      price: Number(editProductForm.value.price),
    })
    product.value = updated
    closeEditProductModal()
    setSuccess('Produto atualizado com sucesso.')
  } catch (err: any) {
    if (err.invalid_params) {
      formErrors.value = err.invalid_params
    } else if (err.detail) {
      setError(err.detail)
    } else {
      setError('Ocorreu um erro ao atualizar.')
    }
  } finally {
    saving.value = false
  }
}

// Remove Material
const removeMaterial = async (materialCode: string) => {
  if (!product.value) return
  if (!confirm(`Remover o material ${materialCode} da receita?`)) return

  try {
    await productService.removeMaterial(product.value.code, materialCode)
    await fetchProduct()
    setSuccess('Material removido.')
  } catch (err: any) {
    setError(err.detail || 'Erro ao remover material.')
  }
}

// Delete Product
const router = useRouter()
const deleteProduct = async () => {
  if (confirm(`Excluir o produto ${code} (${product.value?.name})?`)) {
    try {
      await productService.delete(code)
      alert(`Produto ${code} excluído com sucesso.`)
      await router.replace('/products')
    } catch (err: any) {
      setError(err.detail || 'Não é possível excluir este produto.')
    }
  }
}

const hasError = (field: string) => {
  return formErrors.value.some((e) => e.field === field)
}

const getError = (field: string) => {
  const error = formErrors.value.find((e) => e.field === field)
  return error ? error.reason : ''
}

const setSuccess = (msg: string) => {
  success.value = msg
  setTimeout(() => (success.value = ''), 3000)
}
const setError = (msg: string) => {
  error.value = msg
  setTimeout(() => (error.value = ''), 5000)
}

const margin = (): string => {
  if (!product.value) return '0%'
  const cost = Number(product.value.totalMaterialCost)
  const price = Number(product.value.price)
  if (price === 0) return '0%'
  return (
    (((price - cost) / price) * 100).toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }) + '%'
  )
}

onMounted(async () => {
  if (editProductModalEl.value) bsEditProductModal = new Modal(editProductModalEl.value)
  if (materialModalEl.value) bsMaterialModal = new Modal(materialModalEl.value)

  await Promise.all([fetchProduct(), fetchRawMaterials()])
})
</script>
