import { createApp } from 'vue';
import App from './App.vue';
import './assets/main.css';

// Force production traffic to use HTTPS except on localhost/dev tunnels.
if (typeof window !== 'undefined') {
  const devHosts = ['localhost', '127.0.0.1', '::1'];
  const isLocalhost = devHosts.includes(window.location.hostname);
  if (window.location.protocol === 'http:' && !isLocalhost) {
    window.location.replace(`https://${window.location.host}${window.location.pathname}${window.location.search}${window.location.hash}`);
  }
}

createApp(App).mount('#app');
