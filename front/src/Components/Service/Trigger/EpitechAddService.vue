<template>
    <div>
        <div class="ConnexionDiv" v-if="!connected && !next">
            <label for="email"> Your epitech Email </label>
            <input type="text" v-model="email" id="email" />

            <label for="email"> Your epitech Autologin Link </label>
            <input type="text" v-model="autologinlink" id="autologin" />
            <button v-if="!connected" @click="Auth" id="NotConnectedButton"> Connect to Epitech</button>
        </div>
        <div v-if="connected && !next" class="EpitechTrigger">
            <h2>Epitech</h2>
            <h3> Trigger </h3>
            <div><input type="checkbox" id="one" v-model="triggerOne" >
            <label for="one"> New Project </label></div>

            <div><input type="checkbox" id="two" v-model="triggerTwo" >
            <label for="two"> New Alert </label></div>

            <div><input type="checkbox" id="three" v-model="triggerThree" >
            <label for="three"> New Missing </label></div>

            <button @click="NextStep" type="button" :disabled="CheckCorrect() === false" id="Next"> Next </button>
        </div>
        <button v-if="!next" @click="this.$emit('backstep')" id="Back">Précédent</button>
        <SelectActionService v-if="next" :triggerData="triggerData" @return="next = false"/>
    </div>
</template>

<script>
import Axios from 'axios';
import { API_CONNEXION_URL, TOKEN } from '@/Constant.js';
import SelectActionService from '@/Components/Service/Actions/SelectActionService.vue';
import GetMe from "@/Utils/GetMe.js"

export default {

    data () {
        return {
            connected: false,
            next: false,
            email: "",
            autologinlink: "",
            triggerOne: false,
            triggerTwo: false,
            triggerThree: false,
            triggerData: {}
        }
    },

    methods: {
        CheckCorrect: function () {
            if (!this.triggerOne && !this.triggerTwo && !this.triggerThree)
                return false;
            return true;
        },

        Auth: function () {
            if (this.email === '' || this.autologinlink === '')
                return;
            const token = localStorage.getItem(TOKEN);
            Axios.post(API_CONNEXION_URL + "/loginEpitech", {
                email: this.email,
                autolog: this.autologinlink
            }, {
                headers: {
                    Authorization: `Bearer ${token}`,
                }
            })
            .then(() => { this.connected = true });

        },

        NextStep: function () {
            var condition = []
            if (this.triggerOne)
                condition.push({ event: 1 });
            if (this.triggerTwo)
                condition.push({ event: 2 });
            if (this.triggerThree)
                condition.push({ event: 3 });
            this.triggerData.trigger = 3;
            this.triggerData.conditions = condition;
            this.next = true;
        },
    },

    async created() {
        const result = await GetMe();
        this.connected = result?.data?.epitech ? true : false;
    },

    components: {
        SelectActionService,
    }
}

</script>