<template>
    <div class='Home'>
    </div>
</template>

<script>

import router from "@/router.js";
import Axios from 'axios';
import { API_AUTH_URL, TOKEN, HOMEPATH, DASHBOARDPATH } from '@/Constant.js';
import GetMe from "@/Utils/GetMe.js"

export default {
    name: 'Verify',

    methods: {
        Auth: function (code) {
            Axios.post(API_AUTH_URL + '/authgoogle', {
                code: code,
            })
            .then(result => {
                localStorage.setItem(TOKEN, result.data.token);
                router.push(DASHBOARDPATH);
            })
            .catch(() => {
                router.push({ path: HOMEPATH })
            });
        },

        Login: function (code) {
            const token = localStorage.getItem(TOKEN);
            Axios.post(API_AUTH_URL + '/loginGoogle', {
                code: code,
            }, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
            .then(() => {
                window.close();
            })
        }
    },

    async created() {
        const params = (new URL(window.location)).searchParams;
        const code = params.get('code');
        const token = localStorage.getItem(TOKEN);
        
        if (token !== null && (await GetMe()) !== null) {
            this.Login(code);
        } else
            this.Auth(code);
    }
}

</script>