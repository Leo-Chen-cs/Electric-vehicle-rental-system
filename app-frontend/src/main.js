import { createApp } from "vue";
import App from "./App.vue";

import TDesign from 'tdesign-mobile-vue';
import router from "./route/index.js";
import pinia from "./store/index.js";
import 'tdesign-mobile-vue/es/style/index.css';
const app = createApp(App)
app.use(TDesign);
app.use(router);
app.use(pinia);
app.mount("#app");
