<template>
    <div class='Home'>
        Ok
    </div>
</template>

<script>

import Axios from 'axios';
import { API_CONNEXION_URL, TOKEN } from '@/Constant.js';

export default {
    created() {
        const params = new URLSearchParams(window.location.hash.substr(1));
        const code = params.get('token');
        const token = localStorage.getItem(TOKEN);

        Axios.post(API_CONNEXION_URL + "/loginTrello", {
            code: code,
        }, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
        .then(() => {
            window.close();
        })
        .catch((err) => {
            console.log(err);
            window.close();

        });
    }
}

</script>