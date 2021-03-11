<template>
    <div>
        <button v-if="!connected" @click="OpenAuth" id="NotConnectedButton"> Connect To GitHub </button>
        <div v-if="connected" class="GithubAction" >
            <h2>Github</h2>
            <h3> Action </h3>
            <div><input type="checkbox" id="ActionOne" v-model="actionOne">
            <label for="ActionOne"> Pull Request </label></div>

            <div v-if="actionOne" class="InputActionDiv">
                <div><select v-model="idOptionOne" style="width: 150px">
                    <option disabled value=""> Choose A Repo </option>
                    <option v-for="(item, index) in listRepos" :value="item.name" :key="index"> {{item.name}}</option>
                </select>

                <select v-model="head" :disabled="idOptionOne === ''">
                    <option disabled value=""> Choose Branch To Merge</option>
                    <option v-for="(item, index) in listBranchOptionOne" :value="item" :key="index"> {{item}}</option>
                </select>

                <select v-model="base" :disabled="idOptionOne === ''">
                    <option disabled value=""> Choose Branch Target </option>
                    <option v-for="(item, index) in listBranchOptionOne" :value="item" :key="index"> {{item}}</option>
                </select></div>

                <div style="display: flex; flex-direction: row; margin-top: 0">
                    <div style="display: flex; flex-direction: column">
                        <label for="pullTitle"> Title </label>
                        <input type="text" id="pullTitle" v-model="pullTitle" />
                    </div>
                    <div style="display: flex; flex-direction: column; width: 100%">
                        <label for="pullBody"> Body </label>
                        <textarea id="pullBody" v-model="pullBody"/>
                    </div>
                </div>
            </div>
            
            <div style="margin: 0"><input type="checkbox" id="ActionTwo" v-model="actionTwo">
            <label for="ActionTwo"> Create Branch </label></div>
            
            <div v-if="actionTwo" class="InputActionDivInline">
                <select v-model="idOptionTwo" style="width: 150px">
                    <option disabled value=""> Choose A Repo </option>
                    <option v-for="(item, index) in listRepos" :value="item.name" :key="index"> {{item.name}}</option>
                </select>

                <div style="padding-right: 0; margin: 0; width: 190px"><label for="branchName"> Branch Name </label>
                <input type="text" v-model="branchName" id="branchName" /></div>

                <select v-model="sha" :disabled="idOptionTwo === ''">
                    <option disabled value=""> Choose Base Branch</option>
                    <option v-for="(item, index) in listBranchOptionTwo" :value="item" :key="index"> {{item}}</option>
                </select>
            </div>

            <button type="button" @click="CreateService" :disabled="CheckCorrect() === false" id="Next"> Create </button>
        </div>

        <button @click="this.$emit('backstep')" id="Back">Back</button>
    </div>
</template>

<script>
import Axios from 'axios';
import { API_CONNEXION_URL, TOKEN, API_BASE_URL, API_SERVICE_URL } from '@/Constant.js';
import GetMe from "@/Utils/GetMe.js";


export default {
    props: {
        triggerData: Object
    },

    data () {
        return {
            connected: undefined,
            connexionUrl: "",
            intervalId: 0,
            wind: undefined,
            actionOne: false,
            actionTwo: false,
            listRepos: [],
            listBranchOptionOne: [],
            listBranchOptionTwo: [],
            pullTitle: "",
            pullBody: "",
            branchName: "",
            idOptionOne: "",
            idOptionTwo: "",
            head: "",
            base: "",
            sha: "",
        }
    },

    watch: {
        idOptionOne: async function (val) {
            if (val === "")
                return;
            const token = localStorage.getItem(TOKEN);
            await Axios.get(API_BASE_URL + "/api/github/get_branch/" + val, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
            .then((res) => {
                this.listBranchOptionOne = res.data;
            })
        },
        idOptionTwo: async function (val) {
            if (val === "")
                return;
            const token = localStorage.getItem(TOKEN);
            await Axios.get(API_BASE_URL + "/api/github/get_branch/" + val, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
            .then((res) => {
                this.listBranchOptionTwo = res.data;
            })
        },
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
                this.listRepos = res.data
            })
            .catch((err) => {console.log(err)})
        },

        CreateService: function (e) {
            e.preventDefault();
            const token = localStorage.getItem(TOKEN)
            var action = [];
            
            if (this.actionOne) {
                var dataOne = {}
                dataOne.id = this.idOptionOne;
                dataOne.head = this.head;
                dataOne.base = this.base;
                dataOne.title = this.pullTitle;
                dataOne.body = this.pullBody;
                dataOne = JSON.stringify(dataOne);
                action.push({
                    event: 1,
                    action: dataOne,
                })
            }
            if (this.actionTwo) {
                var dataTwo = {}
                dataTwo.id = this.idOptionTwo;
                dataTwo.name = this.branchName;
                dataTwo.sha = this.sha;
                dataTwo = JSON.stringify(dataTwo);
                action.push({
                    event: 2,
                    action: dataTwo,
                })
            }
            Axios.post(API_SERVICE_URL + '/create', {
                trigger: this.triggerData.trigger,
                conditions: this.triggerData.conditions,
                service: 4,
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

        CheckCorrect: function () {
            if (!this.actionOne && !this.actionTwo)
                return false;
            if (this.actionOne) {
                if (this.idOptionOne === '' || this.head === '' || this.base === '' || this.pullTitle === '' || this.pullBody === '')
                    return false;
            }
            if (this.actionTwo) {
                if (this.idOptionTwo === '' || this.branchName === '' || this.sha === '')
                    return false;
            }
            return true;
        },
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
}

</script>