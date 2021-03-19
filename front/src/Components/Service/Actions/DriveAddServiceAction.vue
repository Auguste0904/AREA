<template>
    <div>
        <button v-if="!connected" @click="OpenAuth" id="NotConnectedButton"> Connect to Google Drive</button>
        <div v-if="connected && !next" class="DriveAction">
            <h2>Google Drive</h2>
            <h3></h3>

            <div v-if="step === 0">
                <h4>Choose file(s) to share</h4>
                <vue-scrolling-table id="table">
                    <template #thead>
                        <tr>
                            <th></th>
                            <th>Name</th>
                            <th>Extension</th>
                            <th>Size</th>
                        </tr>
                    </template>
                    <template #tbody>
                        <tr v-for="file in fileList" :key="file.id">
                            <td>
                                <input type="checkbox" v-on:click="e => handleFileCheck(e, file.id)" />
                            </td>
                            <td>
                                <p>{{ file.name }}</p>
                            </td>
                            <td>
                                <p>{{ file.extension }}</p>
                            </td>
                            <td>
                                <p>{{ file.size.value }}</p>
                            </td>
                            <td>
                                <p>{{ file.size.unit }}</p>
                            </td>
                        </tr>
                    </template>
                </vue-scrolling-table>
                <button @click="step = 1" type="button" id="Next" :disabled="checkedFile.length === 0"> Next </button>
            </div>
            <div v-else-if="step === 1">
                <h4>Choose people to share your file(s)</h4>
                <div v-for="(user, i) in shareUsers" v-bind:key="i">
                    <input v-if="user.length === 0" type="text" placeholder="chucknorris@godmail.com" @change="(e) => handleUserFieldChange(e,i)" class="add-user-input" />
                    <div v-else-if="user.length > 0" class="user-item">
                        {{ user }}
                    </div>
                </div>
                <img src="add-user.png" alt="Add user" v-on:click="handleAddUser" id="add-user-button"/>
                <button type="button" @click="CreateService" id="Next" :disabled="CheckUserCorrect() === false"> Create </button>
            </div>
        </div>
        <button v-if="step === 0" @click="this.$emit('backstep')" id="Back">Back</button>
        <button v-else-if="step === 1" @click="step = 0; checkedFile = []" id="Back">Back</button>
    </div>
</template>

<script>
import Axios from 'axios';
import { API_BASE_URL, API_AUTH_URL, TOKEN, API_SERVICE_URL } from '@/Constant.js';
import GetMe from "@/Utils/GetMe.js"

export default {
    props: {
        triggerData: Object,
    },

    data () {
        return {
            connected: false,
            next: false,
            connexionUrl: "",
            wind: undefined,
            intervalId: undefined,
            fileList: [],
            checkedFile: [],
            shareUsers: [],
            step: 0
        }
    },

    methods: {
        CheckUserCorrect: function () {
            if (this.shareUsers.length === 0)
                return false;
            if (this.shareUsers.length === 1 && this.shareUsers[0] === "")
                return false;
            return true;
        },

        handleFileCheck: function (e, id) {
            const alreadyIn = this.checkedFile.includes(id);

            if (e.target.checked && !alreadyIn)
                this.checkedFile.push(id);
            if (!e.target.checked && alreadyIn)
                this.checkedFile.splice(this.checkedFile.indexOf(id), 1);
        },

        handleUserFieldChange: function(e, id) {
            this.shareUsers[id] = e.target.value;
        },

        handleAddUser: function() {
            this.shareUsers.push("");
        },

        CheckCorrect: function () {
            return false;
        },

        CreateArea: function () {

        },
        
        check: async function () {
            const result = await GetMe();
            if (result)
                await this.getList();
            this.connected = result?.data?.google_token && this.relationsList ? true : false;
            if (this.connected) {
                    clearInterval(this.intervalId);
            }
        },

        OpenAuth: function () {
            this.wind = window.open(this.connexionUrl, "_blank");
            this.intervalId = setInterval(this.check, 1000);
        },

        CreateService: function (e) {
            e.preventDefault();
            const token = localStorage.getItem(TOKEN);
            var path = "";
            var users = "";

            this.checkedFile.forEach((elem, index) => {
                if (index != 0)
                    path += ';';
                path += elem;
            })
            this.shareUsers.forEach((elem, index) => {
                if (index != 0)
                    users += ';';
                users += elem;
            })
            var action = [];
            action.push({ event: 1, action: JSON.stringify({email: users, Files: path})});
            Axios.post(API_SERVICE_URL + '/create', {
                trigger: this.triggerData.trigger,
                conditions: this.triggerData.conditions,
                service: 2,
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

        getList: async function () {
            const token = localStorage.getItem(TOKEN);

            await Axios(API_BASE_URL + '/api/drive/getFiles', {
                headers: {
                    Authorization: `Bearer ${token}`,
                }
            }).then((res) => {
                if (!res.data.error) {
                    this.relationsList = res.data;
                    this.fileList = res.data.items
                        .map(item => ({
                            id: item.id,
                            name: item.originalFilename,
                            extension: item.fileExtension ? item.fileExtension : "Not found",
                            size: item.fileSize ?
                                (() => {
                                    const digitsNb = item.fileSize.length;
                                    const format = nb => (parseFloat(nb) / 1000).toFixed(2);

                                    if (digitsNb >= 8)
                                        return {value: format(item.fileSize), unit: 'GB'};
                                    if (digitsNb >= 5)
                                        return {value: format(item.fileSize), unit: 'MB'};
                                    if (digitsNb >= 2)
                                        return {value: format(item.fileSize), unit: 'KB'};
                                    return {value: format(item.fileSize), unit: 'B'};
                                })() 
                                : "Not found"
                        }))
                        .filter(item => item.name);
                }
            }).catch ((err) => {
                console.log(err);
            });
        }

    },

    async created () {
        const token = localStorage.getItem(TOKEN);
        const result = await GetMe();
        if (result)
            await this.getList();
        this.connected = result?.data?.google_token && this.relationsList ? true : false;

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
}

</script>