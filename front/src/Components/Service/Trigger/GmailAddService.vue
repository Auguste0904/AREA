<template>
    <div>
        <button v-if="!connected" @click="OpenAuth" id="NotConnectedButton"> Connect to Gmail</button>
        <div v-if="connected && !next" class="GmailTrigger">
            <h2>Gmail</h2>
            <h3>Trigger</h3>
            <div><input type="checkbox" id="one" v-model="triggerOne" >
            <label for="one"> Contains Key Word In Object </label></div>
            
            <div class="InputTriggerDiv" v-if="triggerOne">
                <input type="text" v-model="keyWord" placeholder="Key Word" />
            </div>
            
            <div><input type="checkbox" id="two" v-model="triggerTwo" >
            <label for="two"> Reveive From </label></div>
            
            <div class="InputTriggerDiv" v-if="triggerTwo">
                <input type="text" v-model="email" placeholder="Email" />
            </div>

            <div><input type="checkbox" id="three" v-model="triggerThree" >
            <label for="three"> Receive In Time </label></div>

            <div class="InputTriggerDiv" v-if="triggerThree">
                <select v-model="timeKeyWord" >
                    <option value="Before"> Before </option>
                    <option value="After"> After </option>
                </select>
                <input type="time" id="TriggerTime" min="00:00" max="24:00" v-model="triggerTime" />
            </div>

            <button @click="NextStep" type="button" :disabled="CheckCorrect() === false" id="Next"> Next </button>
        </div>
        <button v-if="!next" @click="this.$emit('backstep')" id="Back">Back</button>
        <SelectActionService v-if="next" :triggerData="triggerData" @return="next = false"/>
    </div>
</template>

<script>
import Axios from 'axios';
import { API_AUTH_URL, TOKEN } from '@/Constant.js';
import SelectActionService from '@/Components/Service/Actions/SelectActionService.vue';
import GetMe from "@/Utils/GetMe.js"

export default {
    data () {
        return {
            connected: false,
            next: false,
            connexionUrl: "",
            intervalId: "",
            wind: undefined,
            triggerOne: false,
            triggerTwo: false,
            triggerThree: false,
            triggerData: {},
            keyWord: "",
            email: "",
            timeKeyWord: "Before",
            triggerTime: "",
        }
    },

    methods: {
        CheckCorrect: function () {
            if (!this.triggerOne && !this.triggerTwo && !this.triggerThree)
                return false;
            if (this.triggerOne && this.keyWord === '')
                return false;
            if (this.triggerTwo && this.email === '')
                return false;
            if (this.triggerThree && (this.timeKeyWord === "" || this.triggerTime === ""))
                return false;
            return true;
        },

        check: async function () {
            const result = await GetMe();
            this.connected = result?.data?.google_token ? true : false;
            if (this.connected) {
                clearInterval(this.intervalId);
            }
        },

        OpenAuth: function () {
            this.wind = window.open(this.connexionUrl, "_blank");
            this.intervalId = setInterval(this.check, 1000);
        },

        NextStep: function () {
            var condition = []
            if (this.triggerOne)
                condition.push({ event: 1, condition: this.keyWord });
            if (this.triggerTwo)
                condition.push({ event: 2, condition: this.email });
            if (this.triggerThree)
                condition.push({ event: 3, condition: `${this.timeKeyWord} ${this.triggerTime}` });
            this.triggerData.trigger = 1;
            this.triggerData.conditions = condition;
            this.next = true;
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
                this.wind = window.open(res.data.url, "_blank");
                this.intervalId = setInterval(this.check, 1000);
            })
        }
    },

    components: {
        SelectActionService,
    }
}

</script>