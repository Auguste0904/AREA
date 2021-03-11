<template>
    <div>
        <button v-if="!connected" @click="OpenAuth" id="NotConnectedButton"> Connect to Gmail</button>
        <div v-if="connected" class="GmailAction">
            <h2>Gmail</h2>
            <h3>Action</h3>

            <label for="Email"> Email </label>
            <input type="text" v-model="email" id="Email" />

            <label for="Object"> Object </label>
            <input type="text" v-model="object" id="Object" />

            <label for="Message"> Message </label>
            <textarea v-model="message" id="Message" />

            <button @click="CreateService" type="button" :disabled="CheckCorrect() === false" id="Next"> Create </button>
            <button @click="this.$emit('backstep')" id="Back">Back</button>
        </div>
    </div>
</template>

<script>
import Axios from 'axios';
import { API_AUTH_URL, API_SERVICE_URL, TOKEN } from '@/Constant.js';
import GetMe from "@/Utils/GetMe.js"

export default {
    props: {
        triggerData: Object,
    },

    data () {
        return {
            connected: false,
            connexionUrl: "",
            intervalId: "",
            wind: undefined,
            email: "",
            object: "",
            message: "",
        }
    },

    methods: {
        CheckCorrect: function () {
            if (this.email === '' || this.object === '' || this.message === '')
                return false;
            return true;
        },

        check: async function () {
            const result = await GetMe();
            this.connected = result?.data?.google_token ? true : false;
            if (this.connected) {
                clearInterval(this.intervalId);
                this.intervalId = undefined;
            }
        },

        OpenAuth: function () {
            this.wind = window.open(this.connexionUrl, "_blank");
            if (this.intervalId === undefined)
                this.intervalId = setInterval(this.check, 1000);
        },

        CreateService: function (e) {
            e.preventDefault();
            const token = localStorage.getItem(TOKEN)
            var action = [];

            const act = JSON.stringify({ email: this.email, object: this.object, body: this.message });
            action.push({event: 1, action: act});
            Axios.post(API_SERVICE_URL + '/create', {
                trigger: this.triggerData.trigger,
                conditions: this.triggerData.conditions,
                service: 1,
                actions: action,
            }, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }).then((res) => {
                console.log("OK", res)
                document.location.reload();
            })
            .catch((err) => {console.log("KO", err)})
        },
    },

    async created () {
        const token = localStorage.getItem(TOKEN);
        const result = await GetMe();
        this.connected = result?.data?.google_token ? true : false;

        if (this.connected === false) {
            Axios.get(API_AUTH_URL + '/getGoogle', {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
            .then((res) => {
                this.connexionUrl = res.data.url;
                this.wind = window.open(res.data, "_blank");
                this.intervalId = setInterval(this.check, 1000);
            })
        }
    },

}

</script>

