import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import selfsigned from 'selfsigned';

const createHttpsConfig = () => {
  const shouldUseSelfSigned = process.env.NODE_ENV !== 'production';
  if (!shouldUseSelfSigned) {
    return true;
  }
  const attrs = [{ name: 'commonName', value: 'localhost' }];
  const { cert, private: key } = selfsigned.generate(attrs, {
    days: 30,
    algorithm: 'sha256',
    keySize: 2048
  });
  return { cert, key };
};

const httpsConfig = createHttpsConfig();

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    https: httpsConfig,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  preview: {
    port: 4173,
    https: httpsConfig
  }
});
