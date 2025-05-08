import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import AutoImport from "unplugin-auto-import/vite";
import { resolve } from "path";
import Components from "unplugin-vue-components/vite";
import { TDesignResolver } from "unplugin-vue-components/resolvers";
import vueDevTools from 'vite-plugin-vue-devtools';

const host = process.env.TAURI_DEV_HOST;


// https://vitejs.dev/config/
export default defineConfig(async () => ({
  resolve: {
    alias: {
      "@": resolve(__dirname, 'src'),
    },
  },
  plugins: [
    vue(),
    vueDevTools({}),
    AutoImport({
      resolvers: [TDesignResolver({
        library: 'mobile-vue'
      })],
    }),
    Components({
      resolvers: [TDesignResolver({
        library: 'mobile-vue'
      })],
    }),
  ],

  // Vite options tailored for Tauri development and only applied in `tauri dev` or `tauri build`
  //
  // 1. prevent vite from obscuring rust errors
  clearScreen: false,
  // 2. tauri expects a fixed port, fail if that port is not available
  server: {
    port: 3000,
    strictPort: true,
    host: host || false,
    hmr: host
      ? {
        protocol: "ws",
        host,
        port: 3000,
      }
      : undefined,
    watch: {
      // 3. tell vite to ignore watching `src-tauri`
      ignored: ["**/src-tauri/**"],
    },
  },
}));
