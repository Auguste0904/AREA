import { createApp } from 'vue'
import App from '@/Components/App.vue'
import router from '@/router.js'

import { FontAwesomeIcon } from "@/font-awesome";
import VueScrollingTable from "vue-scrolling-table"
import "vue-scrolling-table/dist/style.css"

createApp(App)
    .use(router)
    .component('fa', FontAwesomeIcon)
    .component(VueScrollingTable.name, VueScrollingTable)
    .mount('#app');