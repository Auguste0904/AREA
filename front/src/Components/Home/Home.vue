<template>
    <div class="NotConnected">
        <form v-if="signIn" @submit="SignIn">
            <input type="text" v-model="email" placeholder="E-mail" required/>
            <input type="password" v-model="password" placeholder="Mot de Passe" required/>
            <button type="submit">Se Connecter</button>
            <span></span>
            <button class="GoogleButton" @click="googleSignIn" ><img src="googleConnexion.svg"/></button>
            <span></span>
            <h4>Vous n'avez pas de compte ?</h4>
            <button class="InUpButton" @click="signIn = false, email = '', password = ''">Inscrivez vous</button>
        </form>
        <form v-else @submit="SignUp">
            <input type="text" v-model="firstname" placeholder="Ex: Bruce" required/>
            <input type="text" v-model="lastname" placeholder="Ex: Wayne" required/>
            <input type="text" v-model="email" placeholder="Ex: batman@wayneenterprise.com" required/>
            <input type="password" v-model="password" placeholder="IAmBatman" required/>
            <button type="submit"> S'inscrire </button>
            <span></span>
            <h4> Vous avez déjà un compte ?</h4>
            <button class="InUpButton" @click="signIn = true, email = '', password= ''">Connectez vous</button>
        </form>
        <h4 v-if="isMobile() === true"> Vous êtes sur smartphone ? téléchargez notre <a href="/client.apk" download>Application</a></h4>
    </div>
</template>

<script>
import './Style/NotConnected.css'
import { API_AUTH_URL, TOKEN, DASHBOARDPATH} from '@/Constant.js'
import CheckConnexion from '@/Utils/CheckConnexion.js'
import router from '@/router.js'
import Axios from 'axios'


export default {
    data () {
        return {
            email: "",
            password: "",
            signIn: true,
            firstname: "",
            lastname: "",
        }
    },

    methods: {
        isMobile() {
            if(/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
                return true
            } else {
                return false
            }
        },
        googleSignIn: function () {
            Axios.get(API_AUTH_URL + "/getGoogle")
            .then((res) => {
                window.location.href = res.data.url;
            })
            .catch((err) => {
                console.log(err);
                return;
            })
        },

        SignIn: async function (e) {
            e.preventDefault();
            await Axios.post(API_AUTH_URL + '/login', {
                email: this.email,
                password: this.password,
            })
            .then (result => {
                localStorage.setItem(TOKEN, result.data.token);
                router.push(DASHBOARDPATH);
            })
            .catch (() => {
                this.email = "";
                this.password = "";
            })
        },

        SignUp: async function (e) {
            e.preventDefault();
            await Axios.post(API_AUTH_URL + '/signup', {
                firstname: this.firstname,
                lastname: this.lastname,
                email: this.email,
                password: this.password,
            })
            .then (result => {
                localStorage.setItem(TOKEN, result.data.token);
                router.push(DASHBOARDPATH);
            })
            .catch (() => {
                this.email = "";
                this.password = "";
            })
        }
    },

    async created () {
        const result = await CheckConnexion();
        if (result === true)
            router.push(DASHBOARDPATH);
    }
}

</script>