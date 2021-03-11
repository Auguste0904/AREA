<template>
    <div>
        <button v-if="!connected" @click="OpenAuth" id="NotConnectedButton"> <h3>Connect to Trello</h3> </button>
        <div v-if="connected" class="TrelloAction">
            <h2>Trello</h2>
            <h3>Action</h3>
            
            <div>
                <input type="checkbox" v-model="actionOne" id="ActionOne"/>
                <label for="ActionOne"> Create A List </label>
            </div>

            <div v-if="actionOne" class="InputTriggerDiv">
                <select v-model="board">
                    <option disabled value=""> Choose A Board </option>
                    <option v-for="(item, index) in listBoard" :value="item.id" :key="index"> {{item.name}} </option>
                </select>
                <div id="InputLabel">
                    <label for="Name"> Name : </label>
                    <input type="text" v-model="name" placeholder="Name Of Your New List" id="Name"/>
                </div>
            </div>

            <button @click="CreateArea" :disabled="CheckCorrect() === false" id="Next"> Create </button>
        </div>
        <button @click="this.$emit('backstep')" id="Back">Back</button>
    </div>
</template>

<script>
import Axios from 'axios';
import { API_CONNEXION_URL, TOKEN, API_BASE_URL, API_SERVICE_URL } from '@/Constant.js';
import GetMe from "@/Utils/GetMe.js"

export default {
    props: {
        triggerData: Object
    },
    
    data () {
        return {
            connected: false,
            connexionUrl: '',
            intervalId: undefined,
            wind: null,
            actionOne: false,
            board: "",
            name: "",
            listBoard: [],
        }
    },

    methods: {
        CheckCorrect: function () {
            if (this.board === '' || this.name === '')
                return false;
            return true;
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
        
        OpenAuth: function () {
            this.wind = window.open(this.connexionUrl, "_blank");
            if (this.intervalId === undefined)
                this.intervalId = setInterval(this.check, 1000);
        },


        getBoardList: async function () {
            const token = localStorage.getItem(TOKEN);

            await Axios(API_BASE_URL + '/api/trello/get_board', {
                headers: {
                    Authorization: `Bearer ${token}`,
                }
            }).then((res) => {
                console.log(res);
                this.listBoard = res.data;
            }).catch ((err) => {
                console.log(err);
            });
        },

        CreateArea: function (e) {
            e.preventDefault();
            const token = localStorage.getItem(TOKEN);
            var action = [];
            action.push({ event: 1, action: `${this.board} ${this.name}` });
            Axios.post(API_SERVICE_URL + '/create', {
                trigger: this.triggerData.trigger,
                conditions: this.triggerData.conditions,
                service: 5,
                actions: action,
            }, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }).then((res) => {
                console.log("OK", res);
                document.location.reload();
            })
            .catch ((err) => {console.log("KO", err)})
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
}

</script>