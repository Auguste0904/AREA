<template>
    <div>
        <button v-if="!connected" @click="OpenAuth" id="NotConnectedButton"> Connect To GitHub </button>
        <div v-if="connected && !next" class="GithubTrigger">
            <h2>Github</h2>
            <h3>Trigger</h3>
            <div><input type="checkbox" id="TriggerOne" name="optionTrigger" v-model="triggerOne">
            <label for="TriggerOne"> New Project </label></div>

            <div><input type="checkbox" id="TriggerTwo" name="optionTrigger" v-model="triggerTwo">
            <label for="TriggerTwo"> New Commit </label></div>

            <div class="InputTriggerDiv" v-if="triggerTwo"><select v-model="repoOption2" >
                <option disabled value=""> Choose A Repo </option>
                <option v-for="(item, index) in listRepos" :value="item.name" :key="index"> {{item.name}}</option>
            </select></div>

            <div><input type="checkbox" id="TriggerThree" name="optionTrigger" v-model="triggerThree">
            <label for="TriggerThree"> New Pull Request </label></div>

            <div class="InputTriggerDiv"><select v-model="repoOption3" v-if="triggerThree">
                <option disabled value=""> Choose A Repo </option>
                <option v-for="(item, index) in listRepos" :value="item.name" :key="index"> {{item.name}}</option>
            </select></div>

            <button @click="NextStep" type="button" :disabled="CheckCorrect() === false" id="Next"> Next </button>
        </div>
        <button v-if="!next" @click="this.$emit('backstep')" id="Back">Back</button>
        <SelectActionService v-if="next" :triggerData="triggerData" @return="next = false"/>
    </div>
</template>

<script>
import Axios from 'axios';
import { API_CONNEXION_URL, TOKEN, API_BASE_URL } from '@/Constant.js';
import SelectActionService from '@/Components/Service/Actions/SelectActionService.vue';
import GetMe from "@/Utils/GetMe.js";


export default {

    data () {
        return {
            connected: undefined,
            next: false,
            connexionUrl: "",
            intervalId: 0,
            wind: undefined,
            triggerOne: false,
            triggerTwo: false,
            triggerThree: false,
            triggerData: {},
            listRepos: [],
            repoOption2: "",
            repoOption3: "",
        }
    },

    methods: {
        check: async function () {
            const result = await GetMe();
            this.connected = result?.data?.github_token ? true : false;
            if (this.connected) {
                clearInterval(this.intervalId);
                this.intervalId = undefined;
                this.getList();
            }
        },

        OpenAuth: function () {
            this.wind = window.open(this.connexionUrl, "_blank");
            if (this.intervalId === undefined)
                this.intervalId = setInterval(this.check, 1000);
        },

        getList: async function () {
            const token = localStorage.getItem(TOKEN);

            await Axios(API_BASE_URL + "/api/github/get_projects", {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }).then((res) => {
                console.log(res);
                this.listRepos = res.data
            })
            .catch((err) => {console.log(err)})
        },

        NextStep: function () {
            var condition = []
            if (this.triggerOne)
                condition.push({ event: 1 });
            if (this.triggerTwo)
                condition.push({ event: 2, condition: this.repoOption2 });
            if (this.triggerThree)
                condition.push({ event: 3, condition: this.repoOption3 });
            this.triggerData.trigger = 4;
            this.triggerData.conditions = condition;
            this.next = true;
        },

        CheckCorrect: function () {
            if (!this.triggerOne && !this.triggerTwo && !this.triggerThree)
                return false;
            if (this.triggerTwo && this.repoOption2 === '')
                return false;
            if (this.triggerThree && this.repoOption3 === '')
                return false;
            return true;
        }
    },

    async created () {
        const token = localStorage.getItem(TOKEN);
        const result = await GetMe();
        this.connected = result?.data?.github_token ? true : false;

        if (this.connected === false) {
            Axios.get(API_CONNEXION_URL + '/getGithub', {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
            .then((res) => {
                this.connexionUrl = res.data;
                this.wind = window.open(res.data, "_blank");
                this.intervalId = setInterval(this.check, 1000);
            })
        } else {
            this.getList();
        }
    },

    components: {
        SelectActionService,
    }
}

</script>