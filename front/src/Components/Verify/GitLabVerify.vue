<template>
    <div class='Home'>
    </div>
</template>

<script>

import Axios from 'axios';
import { API_CONNEXION_URL, TOKEN } from '@/Constant.js';

export default {
    created() {
        const params = (new URL(window.location)).searchParams;
        const code = params.get('code');
        const token = localStorage.getItem(TOKEN);

        Axios.post(API_CONNEXION_URL + "/loginGitlab", {
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