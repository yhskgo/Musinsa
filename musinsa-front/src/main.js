import { createApp } from 'vue'
import App from './App.vue'
import axios from 'axios';
import MusinsaApi from './components/MusinsaApi.vue';

const app = createApp(App);

app.component('musinsa-api', MusinsaApi); // 컴포넌트 등록
app.config.globalProperties.$axios = axios;

app.mount('#app')