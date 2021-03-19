<template>
    <div>
        <Header />
        <div class="dashboard">
            <span class="newarea fas fa-plus" type="button" @click="newAreamodale = !newAreamodale"></span>
            <AddAreaModale v-if="newAreamodale" @closemodale="newAreamodale = !newAreamodale" />
            <div id="services-list">
                <div class="itemlist" v-for="service in services" :key="service._id">
                    <div class="item">
                        <div class="action">
                            <span v-if="service.triggerService.icon === 'Agenda.png'" class="fas fa-calendar-alt"></span>
                            <img v-else v-bind:src="service.triggerService.icon" class="icone"/>
                            <ul>
                                <li v-for="(cond, index) in service.conditions" :key="index">
                                    {{ cond }}
                                </li>
                            </ul>
                        </div>
                        <span class="fas fa-arrow-right"/>
                        <div class="reaction">
                            <img v-bind:src="service.actionService.icon" class="icone"/>
                            <ul>
                                <li v-for="(action, index) in service.actions" :key="index">
                                    {{ action }}
                                </li>
                            </ul>
                        </div>
                        <div class="interaction">
                            <button><fa class="delete" icon="times" width="24" height="24" @click="deleteOne(service.id)"/></button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>


<script>

import CheckConnexion from '@/Utils/CheckConnexion.js';
import Axios from 'axios';
import router from '@/router.js';
import { stringifyService, stringifyTriggerData, stringifyActionData } from '@/Utils/StringifyServices.js';
import { HOMEPATH, API_AUTH_URL, TOKEN, API_SERVICE_URL } from '@/Constant.js';
import Header from '@/Components/Header/Header.vue';
import AddAreaModale from '@/Components/Service/AddAreaModale.vue';


export default {
    data() {
        return {
            newAreamodale: false,
            services: []
        };
    },

    methods: {
        deleteOne: async function (id) {
            const token = localStorage.getItem(TOKEN);
            await Axios.post(API_SERVICE_URL + "/delete", {
                id: id,
            }, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
            .then(() => {
                var i = 0;
                while (i < this.services.length) {
                    if (this.services[i].id === id) {
                        this.services.splice(i, 1);
                        return;
                    }
                    i = i + 1
                }
            })
            .catch((err) => {console.log(err)})
        }
    },

    async created () {
        const result = await CheckConnexion();

        if (result === false)
            router.push(HOMEPATH);

        const context = this;
        const token = localStorage.getItem(TOKEN)
        const headers = {
            headers: {
                Authorization: 'Bearer ' + token
            }
        };

        Axios.get(API_AUTH_URL + '/me', headers)
        .then(res => {
            if (res.status !== 200)
                return;
            const { data } = res;

            context.services = data.services.map(service => ({
                id: service._id,
                triggerService: stringifyService(service.serviceTrigger),
                conditions: service.conditions.map(cond => stringifyTriggerData(cond.event, service.serviceTrigger, cond.condition)),
                actionService: stringifyService(service.serviceAction),
                actions: service.actions.map(action => stringifyActionData(action.event, service.serviceAction, action.action))
            }));
        });
    },

    components: {
        Header,
        AddAreaModale,
    }
}

</script>

<style lang="scss">
    @import "./Dashboard.scss";
</style>