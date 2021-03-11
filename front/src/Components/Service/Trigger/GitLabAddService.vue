<template>
    <div>
        <button v-if="!connected" @click="OpenAuth" id="NotConnectedButton"> Connect To GitLab </button>
        <div v-if="connected && !next" class="GitLabTrigger">
            <h2>GitLab</h2>
            <h3> Trigger </h3>
            <div class="InputTriggerDiv">
                <label for="triggerOne"> New Project </label>
                <input type="checkbox" v-model="triggerOne" id="triggerOne"/>
            </div>
            <button @click="NextStep" type="button" :disabled="CheckCorrect() === false" id="Next"> Next </button>
            <button @click="this.$emit('backstep')" id="Back">Back</button>
        </div>
        <SelectActionService v-if="next" :triggerData="triggerData" @return="next = false"/>
    </div>
</template>

<script>
import Axios from 'axios';
import { TOKEN, API_CONNEXION_URL } from '@/Constant.js';
import SelectActionService from '@/Components/Service/Actions/SelectActionService.vue';
import GetMe from "@/Utils/GetMe.js"


export default {

    data () {
        return {
            connected: false,
            next: false,
            connexionUrl: "",
            wind: undefined,
            intervalId: undefined,
            triggerOne: false,
            triggerData: {},
        }
    },

    methods: {
        check: async function () {
            const result = await GetMe();
            this.connected = result?.data?.gitlab_token ? true : false;
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

        NextStep: function () {
            var condition = []
            if (this.triggerOne)
                condition.push({ event: 1 });
            this.triggerData.trigger = 6;
            this.triggerData.conditions = condition;
            this.next = true;
        },

        CheckCorrect: function () {
            if (!this.triggerOne)
                return false;
            return true;
        },
    },

    async created () {
        const token = localStorage.getItem(TOKEN);
        const result = await GetMe();
        this.connected = result?.data?.gitlab_token ? true : false;
        
        if (this.connected === false) {
            Axios.get(API_CONNEXION_URL + '/getGitlab', {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
            .then((res) => {
                this.connexionUrl = res.data;
                this.wind = window.open(res.data, "_blank");
                this.intervalId = setInterval(this.check, 1000);
            })
        }
    },

    components: {
        SelectActionService,
    }
}

</script>