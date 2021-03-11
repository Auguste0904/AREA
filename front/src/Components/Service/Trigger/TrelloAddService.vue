<template>
    <div>
        <button v-if="!connected" @click="OpenAuth" id="NotConnectedButton"> <h3>Connect to Trello</h3> </button>
        <div v-if="connected && !next" class="TrelloTrigger">
            <h2>Trello</h2>
            <h3> Trigger </h3>

            <div>
                <input type="checkbox" v-model="triggerOne" id="TriggerOne" />
                <label for="TriggerOne"> Check Activities On List </label>
            </div>

            <div v-if="triggerOne" class="InputTriggerDiv">
                <select v-model="board">
                    <option disabled value=""> Choose A Board </option>
                    <option v-for="(item, index) in listBoard" :value="item.id" :key="index"> {{item.name}} </option>
                </select>
                <select v-if="board !== ''" v-model="list">
                    <option disabled value=""> Choose A List </option>
                    <option v-for="(item, index) in listList" :value="item.id" :key="index"> {{item.name}} </option>
                </select>
            </div>


            <button @click="NextStep" type="button" :disabled="CheckCorrect() === false" id="Next"> Next </button>
            <button @click="this.$emit('backstep')" id="Back">Back</button>
        </div>
        <SelectActionService v-if="next" :triggerData="triggerData" @return="next = false"/>
    </div>
</template>

<script>
import Axios from 'axios';
import { API_CONNEXION_URL, API_BASE_URL, TOKEN } from '@/Constant.js';
import SelectActionService from '@/Components/Service/Actions/SelectActionService.vue';
import GetMe from "@/Utils/GetMe.js"


export default {

    data () {
        return {
            connected: false,
            connexionUrl: "",
            wind: null,
            intervalId: undefined,
            next: false,
            triggerData: {},
            triggerOne: false,
            board: "",
            list: "",
            listBoard: [],
            listList: [],
        }
    },

    watch: {
        board: function (value) {
            const token = localStorage.getItem(TOKEN);

            Axios(API_BASE_URL + '/api/trello/get_list/' + value, {
                headers: {
                    Authorization: `Bearer ${token}`,
                }
            })
            .then(res => {
                this.listList = res.data;
            })
            .catch(err => console.log(err))
        }
    },

    methods: {
        CheckCorrect: function () {
            if (!this.triggerOne)
                return false;
            if (this.triggerOne && (this.board === '' || this.list === ''))
                return false;
            return true;
        },

        NextStep: function () {
            var condition = []
            if (this.triggerOne)
                condition.push({ event: 1, condition: `${this.board} ${this.list}` });
            this.triggerData.trigger = 5;
            this.triggerData.conditions = condition;
            this.next = true;
        },

        check: async function () {
            const result = await GetMe();
            this.connected = result?.data?.trello_token ? true : false;
            if (this.connected) {
                clearInterval(this.intervalId);
                this.intervalId = undefined;
                this.getBoardList();
            }
        },
        
        getBoardList: async function () {
            const token = localStorage.getItem(TOKEN);

            Axios.get(API_BASE_URL + '/api/trello/get_board', {
                headers: {
                    Authorization: `Bearer ${token}`,
                }
            })
            .then(res => {
                this.listBoard = res.data;
            })
            .catch(err => console.log(err))
        },

        OpenAuth: function () {
            this.wind = window.open(this.connexionUrl, "_blank");
            if (this.intervalId === undefined)
                this.intervalId = setInterval(this.check, 1000);
        },
    },

    async created () {
        const token = localStorage.getItem(TOKEN);
        const result = await GetMe();
        this.connected = result?.data?.trello_token ? true : false;

        if (!this.connected) {
            Axios.get(API_CONNEXION_URL + '/getTrello', {
                headers: {
                    Authorization: `Bearer ${token}`,
                }
            })
            .then((res) => {
                this.connexionUrl = res.data;
                this.wind = window.open(res.data, "_blank");
                this.intervalId = setInterval(this.check, 1000);
            });
        } else {
            this.getBoardList();
        }
    },

    components: {
        SelectActionService,
    }
}

</script>