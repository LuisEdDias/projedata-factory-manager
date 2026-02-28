<template>
  <div class="container mt-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="text-secondary">Inventário de Matérias-Primas</h2>
    </div>

    <div class="card shadow-sm mb-3">
      <div class="card-body py-3">
        <div class="row justify-content-between align-items-center">
          <div class="d-flex gap-2 flex-wrap col-9 align-items-end">
            <div class="col-sm-5 col-md-4">
              <label class="form-label mb-1">Buscar por código</label>
              <input
                v-model="searchCode"
                class="form-control text-uppercase"
                placeholder="Ex: COD-01"
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
            <button class="btn btn-primary fw-bold" @click="openModal">+ Novo Insumo</button>
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
                  <th>Nome</th>
                  <th class="text-end">Estoque Atual</th>
                  <th>Unidade</th>
                  <th class="text-center">Ações</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="rawMaterials.length === 0">
                  <td colspan="5" class="text-center text-muted py-4">
                    Nenhuma matéria-prima cadastrada.
                  </td>
                </tr>
                <tr v-for="material in rawMaterials" :key="material.code">
                  <td class="align-middle fw-bold">{{ material.code }}</td>
                  <td class="align-middle">{{ material.name }}</td>
                  <td class="align-middle text-end">
                    <span class="badge bg-secondary fs-6">
                      {{ Number(material.stockQuantity).toLocaleString('pt-BR') }}
                    </span>
                  </td>
                  <td class="align-middle">{{ material.unit }}</td>
                  <td class="align-middle text-center">
                    <div class="d-flex gap-2 flex-wrap justify-content-center">
                      <button
                        class="btn btn-sm btn-success"
                        title="Adicionar Estoque"
                        @click="openStockModal(material, 'ADD')"
                      >
                        + Estoque
                      </button>

                      <button
                        class="btn btn-sm btn-secondary"
                        title="Consumir Estoque"
                        @click="openStockModal(material, 'CONSUME')"
                      >
                        - Estoque
                      </button>

                      <button
                        class="btn btn-sm btn-primary"
                        title="Editar"
                        @click="openEditModal(material)"
                      >
                        Editar
                      </button>

                      <button
                        class="btn btn-sm btn-danger"
                        title="Excluir"
                        @click="deleteMaterial(material.code)"
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
            v-if="rawMaterials.length != 0"
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

      <!-- Create Modal -->
      <div class="modal fade" ref="createModalElement" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header bg-light">
              <h5 class="modal-title fw-bold">Cadastrar Matéria-Prima</h5>
              <button
                type="button"
                class="btn-close"
                @click="closeModal"
                aria-label="Close"
              ></button>
            </div>
            <div class="modal-body">
              <div v-if="formSuccess" class="alert alert-success py-2">
                {{ formSuccess }}
              </div>

              <div v-if="formGeneralError" class="alert alert-danger py-2">
                {{ formGeneralError }}
              </div>

              <form @submit.prevent="saveMaterial">
                <div class="mb-3">
                  <label class="form-label">Código</label>
                  <input
                    v-model="createForm.code"
                    type="text"
                    class="form-control text-uppercase"
                    :class="{ 'is-invalid': hasError('code') }"
                    placeholder="Ex: COD-01"
                  />
                  <div class="invalid-feedback">{{ getError('code') }}</div>
                </div>

                <div class="mb-3">
                  <label class="form-label">Nome do Insumo</label>
                  <input
                    v-model="createForm.name"
                    type="text"
                    class="form-control"
                    :class="{ 'is-invalid': hasError('name') }"
                    placeholder="Ex: Prego 17x30mm"
                  />
                  <div class="invalid-feedback">{{ getError('name') }}</div>
                </div>

                <div class="row mb-3">
                  <div class="col-6">
                    <label class="form-label">Estoque Inicial</label>
                    <input
                      v-model="createForm.initialStock"
                      type="number"
                      step="0.0001"
                      class="form-control"
                      :class="{ 'is-invalid': hasError('initialStock') }"
                    />
                    <div class="invalid-feedback">{{ getError('initialStock') }}</div>
                  </div>
                  <div class="col-6">
                    <label class="form-label">Unidade de Medida</label>
                    <select
                      v-model="createForm.unit"
                      class="form-select"
                      :class="{ 'is-invalid': hasError('unit') }"
                    >
                      <option value="UN">Unidade (UN)</option>
                      <option value="KG">Quilograma (KG)</option>
                      <option value="L">Litro (L)</option>
                      <option value="M">Metro (M)</option>
                      <option value="HR">Hora trabalhada (HR)</option>
                    </select>
                    <div class="invalid-feedback">{{ getError('unit') }}</div>
                  </div>
                </div>
              </form>
            </div>
            <div class="modal-footer bg-light">
              <button type="button" class="btn btn-secondary" @click="closeModal">Cancelar</button>
              <button
                type="button"
                class="btn btn-primary"
                @click="saveMaterial"
                :disabled="saving"
              >
                <span
                  v-if="saving"
                  class="spinner-border spinner-border-sm me-2"
                  role="status"
                ></span>
                Salvar
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Edit Modal -->
  <div class="modal fade" ref="editModalElement" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header bg-light">
          <h5 class="modal-title fw-bold">Editar Matéria-Prima</h5>
          <button type="button" class="btn-close" @click="closeEditModal"></button>
        </div>
        <div class="modal-body">
          <div v-if="formGeneralError" class="alert alert-danger py-2">
            {{ formGeneralError }}
          </div>
          <form @submit.prevent="updateMaterial">
            <div class="mb-3">
              <label class="form-label">Código</label>
              <input v-model="editForm.code" type="text" class="form-control text-muted" readonly />
            </div>

            <div class="mb-3">
              <label class="form-label">Nome do Insumo</label>
              <input
                v-model="editForm.name"
                type="text"
                class="form-control"
                :class="{ 'is-invalid': hasError('name') }"
              />
              <div class="invalid-feedback">{{ getError('name') }}</div>
            </div>
          </form>
        </div>
        <div class="modal-footer bg-light">
          <button type="button" class="btn btn-secondary" @click="closeEditModal">Cancelar</button>
          <button type="button" class="btn btn-primary" @click="updateMaterial" :disabled="saving">
            <span v-if="saving" class="spinner-border spinner-border-sm me-2" role="status"></span>
            Atualizar
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- Stock Modal -->
  <div class="modal fade" ref="stockModalElement" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header bg-light">
          <h5 class="modal-title fw-bold">
            {{ stockAction === 'ADD' ? 'Adicionar Estoque' : 'Consumir Estoque' }}
          </h5>
          <button type="button" class="btn-close" @click="closeStockModal"></button>
        </div>

        <div class="modal-body">
          <div v-if="stockGeneralError" class="alert alert-danger py-2">
            {{ stockGeneralError }}
          </div>

          <div class="mb-2 text-muted small">
            Material: <b>{{ stockForm.code }}</b> — {{ stockForm.name }}
          </div>

          <form @submit.prevent="confirmStockAction">
            <div class="mb-3">
              <label class="form-label">Quantidade</label>
              <input
                v-model.number="stockForm.quantity"
                type="number"
                step="0.0001"
                min="0"
                class="form-control"
              />
            </div>
          </form>
        </div>

        <div class="modal-footer bg-light">
          <button type="button" class="btn btn-secondary" @click="closeStockModal">Cancelar</button>
          <button
            type="button"
            class="btn btn-primary"
            @click="confirmStockAction"
            :disabled="stockSaving"
          >
            <span v-if="stockSaving" class="spinner-border spinner-border-sm me-2"></span>
            Confirmar
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import rawMaterialService from '../services/rawMaterialService'

// --- Page State ---
const rawMaterials = ref<any[]>([])
const loading = ref(true)
const success = ref('')
const error = ref('')

// --- Form State ---
const saving = ref(false)
const formErrors = ref<any[]>([])
const formSuccess = ref('')
const formGeneralError = ref('')

const fetchMaterials = async () => {
  loading.value = true
  error.value = ''

  try {
    const pageData = await rawMaterialService.list(currentPage.value, pageSize.value)

    rawMaterials.value = pageData.content
    totalPages.value = pageData.totalPages
    totalElements.value = pageData.totalElements
  } catch (err: any) {
    error.value = 'Falha ao carregar materiais.'
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
    fetchMaterials()
  }
}

const previousPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
    fetchMaterials()
  }
}

// --- Delete Material ---
const deleteMaterial = async (code: string) => {
  if (!confirm(`Excluir permanentemente o material ${code}?`)) return

  try {
    await rawMaterialService.delete(code)

    if (rawMaterials.value.length === 1 && currentPage.value > 0) {
      currentPage.value--
    }

    await fetchMaterials()
    setPageSuccess(`Material ${code} excluído com sucesso.`)
  } catch (err: any) {
    setPageError(err.detail || 'Não é possível excluir este mater.')
  }
}

// --- RAW MATERIAL CREATE MODAL ---
const createModalElement = ref<HTMLElement | null>(null)
let bsCreateModal: Modal | null = null
const createForm = ref({ code: '', name: '', initialStock: 0, unit: 'UN' })

const openModal = () => {
  createForm.value = { code: '', name: '', initialStock: 0, unit: 'UN' }
  formErrors.value = []
  formGeneralError.value = ''
  bsCreateModal?.show()
}

const closeModal = () => {
  bsCreateModal?.hide()
}

const saveMaterial = async () => {
  saving.value = true
  formErrors.value = []
  formGeneralError.value = ''

  try {
    const payload = { ...createForm.value, code: createForm.value.code.toUpperCase() }
    const created = await rawMaterialService.create(payload)
    if (rawMaterials.value.length < pageSize.value) {
      rawMaterials.value.push(created)
    }
    createForm.value = { code: '', name: '', initialStock: 0, unit: 'UN' }
    setFormSuccess('Matéria-prima cadastrada com sucesso!')
  } catch (err: any) {
    if (err.invalid_params) {
      formErrors.value = err.invalid_params
    } else if (err.detail) {
      setFormGeneralError(err.detail)
    } else {
      setFormGeneralError('Ocorreu um erro inesperado ao salvar.')
    }
  } finally {
    saving.value = false
  }
}

// --- RAW MATERIAL EDIT MODAL ---
const editModalElement = ref<HTMLElement | null>(null)
let bsEditModal: Modal | null = null
const editForm = ref({ code: '', name: '' })

const openEditModal = (material: any) => {
  editForm.value = { code: material.code, name: material.name }
  formErrors.value = []
  formGeneralError.value = ''
  bsEditModal?.show()
}

const closeEditModal = () => {
  bsEditModal?.hide()
}

const updateMaterial = async () => {
  saving.value = true
  formErrors.value = []
  formGeneralError.value = ''

  try {
    await rawMaterialService.updateName(editForm.value.code, { name: editForm.value.name })

    const index = rawMaterials.value.findIndex((m) => m.code === editForm.value.code)
    if (index !== -1) {
      rawMaterials.value[index].name = editForm.value.name
    }

    closeEditModal()
    setPageSuccess('Matéria-prima atualizada com sucesso!')
  } catch (err: any) {
    if (err.invalid_params) {
      formErrors.value = err.invalid_params
    } else if (err.detail) {
      setFormGeneralError(err.detail)
    } else {
      setFormGeneralError('Ocorreu um erro ao atualizar.')
    }
  } finally {
    saving.value = false
  }
}

// --- STOCK MODAL ---
type StockAction = 'ADD' | 'CONSUME'

const stockModalElement = ref<HTMLElement | null>(null)
let bsStockModal: Modal | null = null

const stockAction = ref<StockAction>('ADD')
const stockSaving = ref(false)
const stockGeneralError = ref('')

const stockForm = ref({
  code: '',
  name: '',
  quantity: 0,
})

const openStockModal = (material: any, action: StockAction) => {
  stockAction.value = action
  stockGeneralError.value = ''
  stockForm.value = { code: material.code, name: material.name, quantity: 0 }
  bsStockModal?.show()
}

const closeStockModal = () => {
  bsStockModal?.hide()
}

const confirmStockAction = async () => {
  const qty = Number(stockForm.value.quantity)

  if (!Number.isFinite(qty) || qty <= 0) {
    stockGeneralError.value = 'Informe uma quantidade válida (> 0).'
    return
  }

  stockSaving.value = true
  stockGeneralError.value = ''

  try {
    if (stockAction.value === 'ADD') {
      await rawMaterialService.addStock(stockForm.value.code, qty)
      setPageSuccess(`Estoque adicionado em ${stockForm.value.code}.`)
    } else {
      await rawMaterialService.consumeStock(stockForm.value.code, qty)
      setPageSuccess(`Estoque consumido em ${stockForm.value.code}.`)
    }

    closeStockModal()
    await fetchMaterials()
  } catch (err: any) {
    stockGeneralError.value =
      err.detail || 'Não foi possível atualizar o estoque. Verifique com o suporte.'
  } finally {
    stockSaving.value = false
  }
}

// --- Search by Code ---
const searchCode = ref('')

const searchByCode = async () => {
  const code = searchCode.value.trim().toUpperCase()
  if (!code) {
    setPageError('Informe um código válido.')
    return
  }

  loading.value = true
  error.value = ''

  try {
    const material = await rawMaterialService.get(code)
    rawMaterials.value = material ? [material] : []
    totalPages.value = 1
    totalElements.value = rawMaterials.value.length
    searchCode.value = ''
  } catch (err: any) {
    setPageError(err.detail || `Nenhum material encontrado para o código ${code}.`)
  } finally {
    loading.value = false
  }
}

const clearSearch = async () => {
  searchCode.value = ''
  currentPage.value = 0
  await fetchMaterials()
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

const setFormSuccess = (message: string) => {
  formSuccess.value = message
  setTimeout(() => (formSuccess.value = ''), 3000)
}

onMounted(() => {
  if (createModalElement.value) {
    bsCreateModal = new Modal(createModalElement.value)
  }
  if (editModalElement.value) {
    bsEditModal = new Modal(editModalElement.value)
  }
  if (stockModalElement.value) {
    bsStockModal = new Modal(stockModalElement.value)
  }
  fetchMaterials()
})
</script>
