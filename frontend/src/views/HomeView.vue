<template>
  <div class="container py-5">
    <div class="row align-items-center g-5">
      <div class="col-lg-7">
        <span class="badge bg-primary mb-2 px-3 py-2">Projeto Full-Stack Industrial</span>
        <h1 class="display-4 fw-bold text-dark mb-3">Factory Manager</h1>
        <p class="lead text-muted mb-4">
          Uma solução inteligente para gestão de manufatura que transforma inventário em lucro
          otimizado através de algoritmos de alta performance.
        </p>
        <div class="d-flex gap-3 flex-wrap">
          <RouterLink to="/production-plan" class="btn btn-primary btn-lg fw-bold shadow-sm">
            <i class="bi bi-cpu-fill me-2"></i>Ver Plano de Produção
          </RouterLink>
          <RouterLink to="/products" class="btn btn-outline-dark btn-lg fw-bold">
            Gerenciar Catálogo
          </RouterLink>
        </div>
      </div>
      <div class="col-lg-5">
        <div class="card shadow border-0 bg-dark text-white">
          <div class="card-body p-4">
            <h5 class="fw-bold border-bottom border-secondary pb-2 mb-3">Stack Tecnológica</h5>
            <ul class="list-unstyled mb-0">
              <li class="mb-2">
                <i class="bi bi-check2-circle text-primary me-2"></i><strong>Backend:</strong> Java
                17, Spring Boot 3.4
              </li>
              <li class="mb-2">
                <i class="bi bi-check2-circle text-primary me-2"></i><strong>Frontend:</strong> Vue
                3 (Composition API), Vite, TS
              </li>
              <li class="mb-2">
                <i class="bi bi-check2-circle text-primary me-2"></i
                ><strong>Banco:</strong> PostgreSQL 16 (Dockerizado)
              </li>
              <li class="mb-2">
                <i class="bi bi-check2-circle text-primary me-2"></i
                ><strong>UI/UX:</strong> Bootstrap 5 + Mobile Friendly
              </li>
              <li>
                <i class="bi bi-check2-circle text-primary me-2"></i><strong>Infra:</strong> Docker
                Compose com Healthchecks
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <hr class="my-5 opacity-25" />

    <div class="row g-4">
      <div class="col-md-6">
        <div class="card h-100 border-0 shadow-sm bg-light">
          <div class="card-body p-4">
            <h4 class="fw-bold mb-3">
              <i class="bi bi-diagram-3 me-2 text-primary"></i>O Algoritmo Guloso
            </h4>
            <p class="text-secondary">
              Para maximizar a receita, implementamos um
              <strong>Greedy Algorithm (Algoritmo Guloso)</strong>. Diferente de uma busca
              exaustiva, ele toma decisões rápidas e eficientes:
            </p>

            <ol class="small text-secondary">
              <li>Ordena os produtos pelo <strong>maior valor unitário</strong>.</li>
              <li>
                Identifica o <strong>gargalo</strong> de cada receita comparando com o estoque
                atual.
              </li>
              <li>Consome o estoque virtualmente e avança para o próximo item mais rentável.</li>
              <li>Garante complexidade de tempo <strong>O(n log n)</strong> devido à ordenação.</li>
            </ol>
          </div>
        </div>
      </div>

      <div class="col-md-6">
        <div class="card h-100 border-0 shadow-sm bg-light">
          <div class="card-body p-4">
            <h4 class="fw-bold mb-3">
              <i class="bi bi-shield-check me-2 text-primary"></i>Regras de Domínio
            </h4>
            <ul class="text-secondary mb-0">
              <li class="mb-2">
                <strong>Integridade de Código:</strong> Todos os IDs são sanitizados para maiúsculas
                e validados via Regex no Backend.
              </li>
              <li class="mb-2">
                <strong>Precisão Matemática:</strong> Uso de <code>BigDecimal</code> com
                arredondamento para baixo em divisões de estoque.
              </li>
              <li class="mb-2">
                <strong>Consistência:</strong> Deleção proibida de insumos que possuam vínculos
                ativos em receitas (Integridade Referencial).
              </li>
              <li>
                <strong>Simulação Imutável:</strong> O cálculo de plano de produção não altera o
                banco, operando em uma projeção de estoque em memória.
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <div class="mt-5">
      <h3 class="fw-bold mb-4 text-center">Decisões de Arquitetura & Qualidade</h3>
      <div class="row g-4">
        <div class="col-md-4">
          <div class="p-3 text-center">
            <div class="fs-1 text-primary mb-2"><i class="bi bi-bug"></i></div>
            <h5 class="fw-bold">Erro Padronizado</h5>
            <p class="small text-muted">
              Implementação da <strong>RFC 7807 (Problem Details)</strong>. Erros de validação do
              Spring chegam ao Vue com mapeamento de campos exatos.
            </p>
          </div>
        </div>
        <div class="col-md-4">
          <div class="p-3 text-center">
            <div class="fs-1 text-primary mb-2"><i class="bi bi-journal-check"></i></div>
            <h5 class="fw-bold">Test Coverage</h5>
            <p class="small text-muted">
              As classes de serviço possuem alta cobertura de testes unitários (JUnit 5), garantindo
              que o sistema seja confiável e robusto.
            </p>
          </div>
        </div>
        <div class="col-md-4">
          <div class="p-3 text-center">
            <div class="fs-1 text-primary mb-2"><i class="bi bi-translate"></i></div>
            <h5 class="fw-bold">i18n Nativo na API</h5>
            <p class="small text-muted">
              API preparada para internacionalização via <code>MessageSource</code>, resolvendo
              mensagens de erro conforme o Locale do navegador.
            </p>
          </div>
        </div>
      </div>
    </div>

    <div class="alert alert-info mt-5 border-0 shadow-sm d-flex align-items-center">
      <i class="bi bi-info-circle-fill fs-4 me-3"></i>
      <div>
        <strong>Dica de Execução:</strong> Utilize <code>docker-compose up --build</code> na raiz
        para subir o ecossistema completo (API + DB + Front). O Healthcheck do PostgreSQL garantirá
        que a API só inicie após o banco estar pronto para conexões.
      </div>
    </div>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped>
.card {
  transition: transform 0.2s;
}
.card:hover {
  transform: translateY(-5px);
}
.display-4 {
  letter-spacing: -2px;
}
</style>
