<template>
  <div class="container mt-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="text-secondary">Catálogo de Produtos</h2>
    </div>

    <div class="card shadow-sm mb-3">
      <div class="card-body py-3">
        <div class="row justify-content-between align-items-center">
          <div class="d-flex col-9 gap-2 flex-wrap align-items-end">
            <div class="col-sm-5 col-md-4">
              <label class="form-label mb-1">Buscar por código</label>
              <input
                v-model="searchCode"
                class="form-control text-uppercase"
                placeholder="Ex: PRD-01"
                @keyup.enter="searchByCode"
              />
            </div>

            <div class="col-auto">
              <button class="btn btn-outline-primary" @click="searchByCode" :disabled="loading">
                Buscar
              </button>
            </div>

            <div class="col-auto">
              <button class="btn btn-outline-secondary" @click="clearSearch" :disabled="loading">
                Limpar
              </button>
            </div>
          </div>
          <div class="col-3 text-end">
            <button class="btn btn-primary fw-bold" @click="openCreateModal">+ Novo Produto</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="loading" class="text-center my-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else>
      <div v-if="success" class="alert alert-success">{{ success }}</div>
      <div v-if="error" class="alert alert-danger">{{ error }}</div>

      <div class="card shadow-sm">
        <div class="card-body p-0">
          <div class="table-responsive">
            <table class="table table-hover table-striped mb-0">
              <thead class="table-dark">
                <tr>
                  <th>Código</th>
                  <th>Nome do Produto</th>
                  <th class="text-end">Preço de Venda</th>
                  <th class="text-center">Qtd. Insumos na Receita</th>
                  <th class="text-center">Ações</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="products.length === 0">
                  <td colspan="5" class="text-center text-muted py-4">
                    Nenhum produto encontrado.
                  </td>
                </tr>
                <tr v-for="product in products" :key="product.code">
                  <td class="align-middle fw-bold">{{ product.code }}</td>
                  <td class="align-middle">{{ product.name }}</td>
                  <td class="align-middle text-end text-success fw-bold">
                    $
                    {{
                      Number(product.price).toLocaleString('pt-BR', { minimumFractionDigits: 2 })
                    }}
                  </td>
                  <td class="align-middle text-center">
                    <span class="badge bg-info text-dark rounded-pill">
                      {{ product.materials.length }} itens
                    </span>
                  </td>
                  <td class="align-middle text-center">
                    <div class="d-flex gap-2 flex-wrap justify-content-center">
                      <RouterLink
                        class="btn btn-sm btn-outline-primary"
                        :to="`/products/${product.code}`"
                        >Detalhes</RouterLink
                      >

                      <button
                        class="btn btn-sm btn-outline-danger"
                        @click="deleteProduct(product.code, product.name)"
                      >
                        Excluir
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div
            v-if="!isSearching && products.length !== 0"
            class="d-flex justify-content-between align-items-center p-3 border-top"
          >
            <div class="text-muted small">
              Página {{ currentPage + 1 }} de {{ totalPages }} ({{ totalElements }} registros)
            </div>

            <div class="btn-group btn-group-sm">
              <button
                class="btn btn-outline-dark"
                :disabled="currentPage === 0"
                @click="previousPage"
              >
                Anterior
              </button>

              <button
                class="btn btn-outline-dark"
                :disabled="currentPage >= totalPages - 1"
                @click="nextPage"
              >
                Próxima
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Create Product Modal -->
    <div
      class="modal fade"
      ref="modalElement"
      tabindex="-1"
      aria-hidden="true"
      data-bs-backdrop="static"
    >
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-light">
            <h5 class="modal-title fw-bold">Cadastrar Novo Produto</h5>
            <button type="button" class="btn-close" @click="closeCreateModal"></button>
          </div>
          <div class="modal-body">
            <div v-if="formGeneralError" class="alert alert-danger py-2">
              {{ formGeneralError }}
            </div>

            <form @submit.prevent="saveProduct">
              <h6 class="text-muted border-bottom pb-2 mb-3">1. Dados Básicos</h6>
              <div class="row mb-3">
                <div class="col-md-4">
                  <label class="form-label">Código Único</label>
                  <input
                    v-model="form.code"
                    type="text"
                    class="form-control text-uppercase"
                    :class="{ 'is-invalid': hasError('code') }"
                    placeholder="Ex: MESA-01"
                  />
                  <div class="invalid-feedback">{{ getError('code') }}</div>
                </div>
                <div class="col-md-5">
                  <label class="form-label">Nome do Produto</label>
                  <input
                    v-model="form.name"
                    type="text"
                    class="form-control"
                    :class="{ 'is-invalid': hasError('name') }"
                    placeholder="Ex: Mesa de Jantar"
                  />
                  <div class="invalid-feedback">{{ getError('name') }}</div>
                </div>
                <div class="col-md-3">
                  <label class="form-label">Preço Venda ($)</label>
                  <input
                    v-model="form.price"
                    type="number"
                    step="0.01"
                    class="form-control"
                    :class="{ 'is-invalid': hasError('price') }"
                  />
                  <div class="invalid-feedback">{{ getError('price') }}</div>
                </div>
              </div>

              <h6 class="text-muted border-bottom pb-2 mb-3 mt-4">2. Materiais necessários</h6>
              <div v-if="formSuccess" class="alert alert-success py-2">
                {{ formSuccess }}
              </div>

              <div v-if="formAddMaterialError" class="alert alert-danger py-2">
                {{ formAddMaterialError }}
              </div>

              <div v-if="hasError('materials')" class="text-danger small mb-2">
                {{ getError('materials') }}
              </div>

              <div class="row mb-3 align-items-end bg-light p-3 rounded">
                <div class="col-md-6">
                  <label class="form-label small">Selecionar Matéria-Prima</label>
                  <select v-model="tempMaterial.code" class="form-select form-select-sm">
                    <option value="" disabled>Selecione um material...</option>
                    <option v-for="rm in availableRawMaterials" :key="rm.code" :value="rm.code">
                      {{ rm.code }} - {{ rm.name }} ({{ rm.unit }})
                    </option>
                  </select>
                </div>
                <div class="col-md-4">
                  <label class="form-label small">Quantidade Necessária</label>
                  <input
                    v-model="tempMaterial.quantity"
                    type="number"
                    step="0.001"
                    class="form-control form-control-sm"
                    placeholder="Qtd"
                  />
                </div>
                <div class="col-md-2 d-grid">
                  <button
                    type="button"
                    class="btn btn-sm btn-success"
                    @click="addMaterialToRecipe"
                    :disabled="!tempMaterial.code || tempMaterial.quantity <= 0"
                  >
                    Incluir
                  </button>
                </div>
              </div>

              <table class="table table-sm table-bordered mt-2" v-if="form.materials.length > 0">
                <thead class="table-light">
                  <tr>
                    <th>Insumo</th>
                    <th>Quantidade</th>
                    <th class="text-center">Remover</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(mat, index) in form.materials" :key="index">
                    <td>({{ mat.rawMaterialCode }}) - {{ mat.rawMaterialName }}</td>
                    <td>{{ mat.quantityRequired }} {{ mat.unit }}</td>
                    <td class="text-center">
                      <button
                        type="button"
                        class="btn btn-sm btn-text text-danger"
                        @click="removeMaterialFromRecipe(index)"
                      >
                        &times;
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
              <div v-else class="text-center text-muted small py-2 font-italic">
                Nenhum insumo adicionado à receita ainda.
              </div>
            </form>
          </div>
          <div class="modal-footer bg-light">
            <button type="button" class="btn btn-secondary" @click="closeCreateModal">
              Cancelar
            </button>
            <button type="button" class="btn btn-primary" @click="saveProduct" :disabled="saving">
              <span
                v-if="saving"
                class="spinner-border spinner-border-sm me-2"
                role="status"
              ></span>
              Salvar Produto
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import productService from '../services/productService'
import rawMaterialService from '../services/rawMaterialService'
import type { ProductResponse } from '@/types/product'
import type { RawMaterial, Unit } from '@/types/raw-material'

// --- Page State ---
const products = ref<ProductResponse[]>([])
const availableRawMaterials = ref<RawMaterial[]>([])
const loading = ref(true)
const success = ref('')
const error = ref('')

// --- Form State ---
const saving = ref(false)
const formErrors = ref<any[]>([])
const formSuccess = ref('')
const formGeneralError = ref('')

const fetchInitialData = async () => {
  loading.value = true
  try {
    const [productsData, materialsData] = await Promise.all([
      productService.list(0, 10),
      rawMaterialService.list(0, 10000),
    ])
    products.value = productsData.content
    totalPages.value = productsData.totalPages
    totalElements.value = productsData.totalElements
    availableRawMaterials.value = materialsData.content
  } catch (err) {
    error.value = 'Falha ao carregar dados. Tente novamente.'
  } finally {
    loading.value = false
  }
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const data = await productService.list(currentPage.value, pageSize.value)
    products.value = data.content
    totalPages.value = data.totalPages
    totalElements.value = data.totalElements
  } catch (err) {
    error.value = 'Erro ao carregar produtos. Tente novamente.'
  } finally {
    loading.value = false
  }
}

// --- Pagination ---
const currentPage = ref(0)
const pageSize = ref(10)
const totalPages = ref(0)
const totalElements = ref(0)

const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++
    fetchProducts()
  }
}

const previousPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
    fetchProducts()
  }
}

// --- Delete Product ---
const deleteProduct = async (code: string, name: string) => {
  if (confirm(`Excluir o produto ${code} (${name})?`)) {
    try {
      await productService.delete(code)

      if (products.value.length === 1 && currentPage.value > 0) {
        currentPage.value--
      }

      await fetchProducts()
      setPageSuccess(`Produto ${code} excluído com sucesso.`)
    } catch (err: any) {
      setPageError(err.detail || 'Não é possível excluir este produto.')
    }
  }
}

// --- Product Create ---
const modalElement = ref<HTMLElement | null>(null)
let bsCreateModal: Modal | null = null
const form = ref<CreateProductForm>({ code: '', name: '', price: 0, materials: [] })

const tempMaterial = ref({ code: '', quantity: 0 })
const formAddMaterialError = ref('')

const addMaterialToRecipe = () => {
  const code = tempMaterial.value.code
  const qty = tempMaterial.value.quantity

  const alreadyExists = form.value.materials.some((m) => m.rawMaterialCode === code)
  if (alreadyExists) {
    setFormAddMaterialError('Este material já foi adicionado.')
    return
  }

  const rm = availableRawMaterials.value.find((r) => r.code === code)
  if (!rm) {
    setFormAddMaterialError('Matéria-prima não encontrada.')
    return
  }

  form.value.materials.push({
    rawMaterialCode: code,
    rawMaterialName: rm.name,
    unit: rm.unit,
    quantityRequired: qty,
  })

  tempMaterial.value = { code: '', quantity: 0 }
}

const removeMaterialFromRecipe = (index: number) => {
  form.value.materials.splice(index, 1)
}

const openCreateModal = () => {
  form.value = { code: '', name: '', price: 0, materials: [] }
  tempMaterial.value = { code: '', quantity: 0 }
  formErrors.value = []
  formGeneralError.value = ''
  formAddMaterialError.value = ''
  bsCreateModal?.show()
}

const closeCreateModal = () => {
  bsCreateModal?.hide()
}

const saveProduct = async () => {
  saving.value = true
  formErrors.value = []
  formGeneralError.value = ''
  formAddMaterialError.value = ''

  try {
    const payload = { ...form.value }
    await productService.create(payload)

    closeCreateModal()
    fetchProducts()
    setPageSuccess(`Produto ${payload.code.trim().toUpperCase()} criado com sucesso.`)
  } catch (err: any) {
    if (err.invalid_params) {
      formErrors.value = err.invalid_params
    } else if (err.detail) {
      setFormGeneralError(err.detail)
    } else {
      setFormGeneralError('Erro ao salvar o produto. Tente novamente.')
    }
  } finally {
    saving.value = false
  }
}

// --- Search by Code ---
const searchCode = ref('')
const isSearching = ref(false)

const searchByCode = async () => {
  const code = searchCode.value.trim().toUpperCase()

  if (!code) {
    setPageError('Informe um código válido.')
    return
  }

  isSearching.value = true
  loading.value = true
  error.value = ''

  try {
    const product = await productService.get(code)
    products.value = product ? [product] : []
    totalElements.value = products.value.length
    searchCode.value = ''
  } catch (err: any) {
    setPageError(err.detail || `Nenhum produto encontrado para o código ${code}.`)
    isSearching.value = false
  } finally {
    loading.value = false
  }
}

const clearSearch = async () => {
  searchCode.value = ''
  isSearching.value = false
  currentPage.value = 0
  await fetchProducts()
}

const hasError = (field: string) => {
  return formErrors.value.some((e) => e.field === field)
}

const getError = (field: string) => {
  const error = formErrors.value.find((e) => e.field === field)
  return error ? error.reason : ''
}

const setPageError = (message: string) => {
  error.value = message
  setTimeout(() => (error.value = ''), 5000)
}

const setPageSuccess = (message: string) => {
  success.value = message
  setTimeout(() => (success.value = ''), 3000)
}

const setFormGeneralError = (message: string) => {
  formGeneralError.value = message
  setTimeout(() => (formGeneralError.value = ''), 5000)
}

const setFormAddMaterialError = (message: string) => {
  formAddMaterialError.value = message
  setTimeout(() => (formAddMaterialError.value = ''), 5000)
}

const setFormSuccess = (message: string) => {
  formSuccess.value = message
  setTimeout(() => (formSuccess.value = ''), 3000)
}

interface ProductMaterialForm {
  rawMaterialCode: string
  rawMaterialName?: string
  quantityRequired: number
  unit?: Unit
}

interface CreateProductForm {
  code: string
  name: string
  price: number
  materials: ProductMaterialForm[]
}

interface UpdateProductForm {
  name: string
  price: number
}

onMounted(() => {
  if (modalElement.value) {
    bsCreateModal = new Modal(modalElement.value)
  }
  fetchInitialData()
})
</script>
