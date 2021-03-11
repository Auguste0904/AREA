<template>
    <div class="Header">
        <h2>My Area</h2>
        <span class="HeaderInfos" @click="DisplayInfos = !DisplayInfos; firstname = afirstname; lastname = alastname" > {{afirstname}} {{alastname}} </span>
        <span @click="LogOut" class="fas fa-sign-out-alt"/>
    </div>
    <div class="UserInfos" v-if="DisplayInfos">
        <form v-if="!modalePassword" @submit="Update">
            <input type="text" v-model="firstname" placeholder="Ex: Bruce" required/>
            <input type="text" v-model="lastname" placeholder="Ex: Wayne" required/>
            <button type="submit">Sauvegarder</button>
        </form>
        <form v-else @submit="ChangePwd">
            <input type="password" v-model="lastPassword" placeholder="Ancien Mot De Passe" />
            <input type="password" v-model="newPassword" placeholder="Nouveau Mot De Passe" />
            <button type="submit"> Modifier </button>
        </form>
        <div class="ModButtonDiv">
            <button type="button" v-if="!modalePassword" @click="modalePassword = !modalePassword">Changer le mot de passe</button>
            <button type="button" v-else @click="modalePassword = !modalePassword; lastPassword = ''; newPassword = ''"> Annuler </button>
            <button type="button" @click="DeleteAccount" > Supprimer Le Compte </button>
        </div>
    </div>
</template>

<script>

import Axios from 'axios';
import { API_AUTH_URL, HOMEPATH, TOKEN } from '@/Constant.js';
import router from '@/router.js';

export default {
    data () {
        return {
            email: "",
            firstname: "",
            afirstname: "",
            lastname: "",
            alastname: "",
            DisplayInfos: false,
            modalePassword: false,
            lastPassword: "",
            newPassword: "",
        }
    },

    methods: {
        LogOut: function () {
            localStorage.removeItem(TOKEN);
            router.push(HOMEPATH);
        },

        Update: function (e) {
            e.preventDefault();
            const token = localStorage.getItem(TOKEN)

            if (token === null)
                router.push(HOMEPATH);

            Axios.post(API_AUTH_URL + "/update", {
                firstname: this.firstname,
                lastname: this.lastname,
            }, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
            .then(() => { 
                this.modalePassword = false;
                this.DisplayInfos = false;
                this.lastPassword = "";
                this.newPassword = "";
                this.afirstname = this.firstname;
                this.alastname = this.lastname;
            })
            .catch((err) => {console.log(err)});
        },

        ChangePwd: function (e) {
            e.preventDefault();
            const token = localStorage.getItem(TOKEN)

            if (token === null)
                router.push(HOMEPATH);

            Axios.post(API_AUTH_URL + "/change", {
                last: this.lastPassword,
                new: this.newPassword,
            }, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
            .then(() => {
                this.modalePassword = false;
                this.DisplayInfos = false;
                this.lastPassword = "";
                this.newPassword = "";
            })
            .catch((err) => {console.log(err)})
        },

        DeleteAccount: function () {
            const token = localStorage.getItem(TOKEN);

            if (token === null)
                router.push(HOMEPATH);

            if (confirm("Voulez vous vraiment supprimer votre compte ?")) {
                Axios.post(API_AUTH_URL + "/delete", {}, {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }).then(() => {
                    localStorage.removeItem(TOKEN)
                    router.push(HOMEPATH)
                })
                .catch(error => console.log(error));
            }
        },

    },

    async created () {
        const token = localStorage.getItem(TOKEN);
        const result = await Axios.get(API_AUTH_URL + '/me', {
            headers: {
                Authorization: `Bearer ${token}`
            }
        }).then((result) => { return (result) })
        .catch((err) => {
            console.log(err);
            router.push(HOMEPATH);
        });
        this.email = result.data.email;
        this.firstname = result.data.firstname;
        this.afirstname = result.data.firstname;
        this.lastname = result.data.lastname;
        this.alastname = result.data.lastname;
    }
}


</script>

<style lang="scss">
    @import "./Header.scss";
</style>