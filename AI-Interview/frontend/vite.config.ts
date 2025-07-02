import { UserConfigFn } from 'vite';
import { overrideVaadinConfig } from './vite.generated';

const customConfig: UserConfigFn = (env) => ({
  // Here you can add custom Vite parameters
  // https://vitejs.dev/config/
  server: {
    port: 8082
  },
  resolve: {
    alias: [
      { find: 'frontend', replacement: '/Users/lqy0584/Downloads/project/employment/AI-Interview/frontend/frontend' },
      { find: '@', replacement: '/Users/lqy0584/Downloads/project/employment/AI-Interview/frontend/frontend' },
      { find: 'assets', replacement: '/Users/lqy0584/Downloads/project/employment/AI-Interview/frontend/frontend/assets' }
    ]
  },
  assetsInclude: ['**/*.json'],
  json: {
    stringify: true,
    parse: false
  },
  build: {
    commonjsOptions: {
      transformMixedEsModules: true
    }
  }
});

export default overrideVaadinConfig(customConfig);