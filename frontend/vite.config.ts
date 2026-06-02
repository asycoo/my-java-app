import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api/tenant-demo': {
        target: 'http://localhost:8061',
        changeOrigin: true,
      },
    },
  },
});
