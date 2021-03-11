<template>
    <div>
        <button v-if="!connected" @click="OpenAuth" id="NotConnectedButton"> Connect To GitLab </button>
        <div v-if="connected && !next" class="GitLabAction">
            <h2>GitLab</h2>
            <h3> Actions </h3>
            <div style="margin: 0">
                <input type="checkbox" id="ActionOne" v-model="actionOne">
                <label for="ActionOne"> Create Branch </label>
            </div>
            
            <div v-if="actionOne" class="InputActionDiv">
                <select v-model="id" style="width: 150px">
                    <option disabled value=""> Choose A Repo </option>
                    <option v-for="(item, index) in listRepos" :value="item.id" :key="index"> {{item.name}}</option>
                </select>

                <div style="padding-right: 0; margin-block: 1em; width: 190px"><label for="branchName"> Branch Name </label>
                <input type="text" v-model="branchName" id="branchName" /></div>

                <select v-model="sha" :disabled="id === ''">
                    <option disabled value=""> Choose Base Branch</option>
                    <option v-for="(item, index) in listBranch" :value="item.name" :key="index"> {{item.name}}</option>
                </select>
            </div>

            <button @click="CreateService" type="button" :disabled="CheckCorrect() === false" id="Next"> Create </button>
            <button @click="this.$emit('backstep')" id="Back">Back</button>
        </div>
    </div>
</template>

<script>
import Axios from 'axios';
import { TOKEN, API_CONNEXION_URL, API_BASE_URL, API_SERVICE_URL } from '@/Constant.js';
import GetMe from "@/Utils/GetMe.js"


export default {
    props: {
        triggerData: Object
    },

    data () {
        return {
            connected: false,
            next: false,
            connexionUrl: "",
            wind: undefined,
            intervalId: undefined,
            actionOne: false,
            id: "",
            branchName: "",
            sha: "",
            listRepos: [],
            listBranch: [],
        }
    },

    watch: {
        id: async function (val) {
            if (val === "")
                return;
            const token = localStorage.getItem(TOKEN);

            await Axios.get(API_BASE_URL + "/api/gitlab/get_branch/" + val, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
            .then((res) => {
                this.listBranch = res.data;
            })
            .catch(err => {console.log(err)})
        },
    },

    methods: {
        check: async function () {
            const result = await GetMe();
            this.connected = result?.data?.gitlab_token ? true : false;
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

        CreateService: function (e) {
            e.preventDefault();
            const token = localStorage.getItem(TOKEN);
            var action = [];

            if(this.actionOne) {
                action.push({event: 1, action: JSON.stringify({id: this.id, name: this.branchName, sha: this.sha})})
            }
            Axios.post(API_SERVICE_URL + '/create', {
                trigger: this.triggerData.trigger,
                conditions: this.triggerData.conditions,
                service: 6,
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

        getList: async function () {
            const token = localStorage.getItem(TOKEN);

            await Axios(API_BASE_URL + "/api/gitlab/get_projects", {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }).then((res) => {
                console.log(res)
                this.listRepos = res.data
            })
            .catch((err) => {console.log(err)})
        },

        CheckCorrect: function () {
            if (!this.actionOne)
                return false;
            if (this.actionOne && (this.id === "" || this.branchName === "" || this.sha === ""))
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
        } else {
            this.getList();
        }
    },
}

</script>