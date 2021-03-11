
<template>
    <div class="home">
    </div>
</template>

<script>

import router from "@/router.js";
import Axios from 'axios';
import { API_BASE_URL, HOMEPATH, DASHBOARDPATH } from '@/Constant.js';

export default {
    name: 'MailVerify',

    created() {
        const params = (new URL(window.location));
        Axios.get(API_BASE_URL + params.pathname)
        .then(() => {
            router.push(DASHBOARDPATH);
        })
        .catch(() => {
            router.push({ path: HOMEPATH })
        });
    }
}
</script>