import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  headers: {
    'Content-Type': 'application/json',
    'Accept-Language': navigator.language,
  },
  timeout: 10000,
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.data) {
      const problemDetail = error.response.data

      console.error(`[API Error] ${problemDetail.title}: ${problemDetail.detail}`)

      return Promise.reject({
        message: problemDetail.detail || 'Unexpected error',
        status: error.response.status,
        ...problemDetail,
      })
    }

    return Promise.reject({
      message: 'Communication failure with the server. Please try again later.',
      status: 0,
    })
  },
)

export default api
