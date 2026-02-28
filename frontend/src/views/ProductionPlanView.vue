<template>
  <div class="container mt-5 mb-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div class="col-md-9">
        <h2 class="text-secondary mb-0"><i class="bi bi-cpu"></i> Inteligência de Produção</h2>
        <p class="text-muted small mt-1">
          Otimização baseada em algoritmo Greedy (Maximização de Valor) para sugerir a melhor ordem
          de produção baseada valor de venda do produto, considerando composição, estoque atual e
          gargalos de insumos.
        </p>
      </div>
      <button class="btn btn-primary fw-bold px-4" @click="fetchPlan" :disabled="loading">
        <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
        Recalcular Plano
      </button>
    </div>

    <div v-if="error" class="alert alert-danger shadow-sm">{{ error }}</div>

    <div v-if="loading && !loaded" class="text-center my-5 py-5">
      <div
        class="spinner-border text-primary"
        style="width: 3rem; height: 3rem"
        role="status"
      ></div>
      <h5 class="mt-3 text-muted">Analisando receitas e processando gargalos de estoque...</h5>
    </div>

    <div v-if="loaded && !error" class="row g-4">
      <div class="col-12">
        <div class="card shadow-sm border-success border-2">
          <div class="card-body text-center py-4">
            <h5 class="text-muted text-uppercase fw-bold mb-2">Receita Máxima Estimada</h5>
            <h1 class="display-4 text-success fw-bold mb-0">
              $
              {{
                Number(planData.estimatedTotalRevenue).toLocaleString('pt-BR', {
                  minimumFractionDigits: 2,
                })
              }}
            </h1>
            <p class="text-muted small mt-2 mb-0">
              Caso o plano de produção abaixo seja executado integralmente.
            </p>
          </div>
        </div>
      </div>

      <div class="col-md-8">
        <div class="card shadow-sm h-100">
          <div class="card-header bg-dark text-white fw-bold">Ordem de Produção Recomendada</div>
          <div class="card-body p-0">
            <div class="table-responsive">
              <table class="table table-hover mb-0">
                <thead class="table-light">
                  <tr>
                    <th>Código do Produto</th>
                    <th>Produto</th>
                    <th class="text-center">Qtd. a Produzir</th>
                    <th class="text-end">Valor Gerado</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="planData.plan.length === 0">
                    <td colspan="4" class="text-center text-muted py-4">
                      O estoque atual não é suficiente para produzir nenhum item do catálogo.
                    </td>
                  </tr>
                  <tr v-for="item in planData.plan" :key="item.productCode">
                    <td class="align-middle fw-bold text-secondary">{{ item.productCode }}</td>
                    <td class="align-middle">{{ item.productName }}</td>
                    <td class="align-middle text-center">
                      <span class="badge bg-primary fs-6">{{ item.quantityToProduce }} un</span>
                    </td>
                    <td class="align-middle text-end fw-bold text-success">
                      $
                      {{
                        Number(item.totalValue).toLocaleString('pt-BR', {
                          minimumFractionDigits: 2,
                        })
                      }}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      <div class="col-md-4">
        <div class="card shadow-sm h-100">
          <div class="card-header bg-secondary text-white fw-bold">
            Previsão de Sobras (Pós-Produção)
          </div>
          <div class="card-body p-0">
            <div class="table-responsive">
              <table class="table table-sm table-striped mb-0">
                <thead class="table-light">
                  <tr>
                    <th>Insumo</th>
                    <th class="text-end">Estoque Final</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="Object.keys(planData.remainingStock).length === 0">
                    <td colspan="2" class="text-center text-muted py-3 small">
                      Nenhum insumo cadastrado.
                    </td>
                  </tr>
                  <tr v-for="(qty, code) in planData.remainingStock" :key="code">
                    <td class="align-middle small fw-bold">{{ code }}</td>
                    <td class="align-middle text-end small">
                      <span :class="{ 'text-danger fw-bold': qty === 0, 'text-muted': qty > 0 }">
                        {{ Number(qty).toLocaleString('pt-BR') }}
                      </span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import productionService from '../services/productionService'

const loading = ref(false)
const error = ref('')

const planData = ref({
  plan: [] as any[],
  estimatedTotalRevenue: 0,
  remainingStock: {} as Record<string, number>,
})

const loaded = ref(false)

const fetchPlan = async () => {
  loading.value = true
  error.value = ''
  try {
    planData.value = await productionService.calculatePlan()
  } catch (err: any) {
    error.value = err.detail || 'Ocorreu um erro ao calcular o plano de otimização.'
  } finally {
    loading.value = false
    loaded.value = true
  }
}

onMounted(() => {
  fetchPlan()
})
</script>

<style scoped>
.display-4 {
  font-size: 3rem;
  letter-spacing: -1px;
}
</style>
