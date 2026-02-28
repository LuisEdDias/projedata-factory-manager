<template>
  <div class="app-wrapper bg-light min-vh-100">
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
      <div class="container">
        <router-link class="navbar-brand fw-bold d-flex align-items-center" to="/">
          <i class="bi bi-box-seam me-2 text-primary"></i> Factory Optimizer
        </router-link>

        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
        >
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav ms-auto">
            <li class="nav-item">
              <router-link
                class="nav-link px-3"
                active-class="active fw-bold text-white"
                to="/raw-materials"
              >
                Matérias-Primas
              </router-link>
            </li>
          </ul>
        </div>
      </div>
    </nav>

    <main class="container-fluid pb-5">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<style>
body {
  font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
  background-color: #f8f9fa;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
