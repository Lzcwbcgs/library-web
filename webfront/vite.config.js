import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端服务器地址
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/api'), // 保留/api前缀
        configure: (proxy, options) => {
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('Original Request Method:', req.method)
            console.log('Original Request Path:', req.url)
            console.log('Proxy Request Method:', proxyReq.method)
            console.log('Proxy Request Path:', proxyReq.path)
          })
          proxy.on('proxyRes', (proxyRes, req, res) => {
            console.log('Response Status:', proxyRes.statusCode)
          })
        }
      }
    }
  }
})
