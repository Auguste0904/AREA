<template>
    <div>
        <Header />
        <button type="button" @click="newAreamodale = !newAreamodale"> Ajouter Un AREA </button>

        <AddAreaModale v-if="newAreamodale" @closemodale="newAreamodale = !newAreamodale" />
        <div id="services-list">
            <table>
                <thead>
                    <tr>
                        <th>Trigger service</th>
                        <th>Condition(s)</th>
                        <th><fa icon="arrow-right" width="32" height="32" color="#0acc8b"/></th>
                        <th>Action service</th>
                        <th>Action(s)</th>
                        <th>Edit</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="service in services" :key="service._id">
                        <td>
                            <div>
                                <img v-bind:src="service.triggerService.icon" class="service-icon"/>
                                <p>{{ service.triggerService.name }}</p>
                            </div>
                        </td>
                        <td>
                            <ul>
                                <li v-for="(cond, index) in service.conditions" :key="index">
                                    <p>{{ cond }}</p>
                                </li>
                            </ul>
                        </td>
                        <td></td>
                        <td>
                            <div>
                                <img v-bind:src="service.actionService.icon" class="service-icon"/>
                                <p>{{ service.actionService.name }}</p>
                            </div>
                        </td>
                        <td>
                            <ul>
                                <li v-for="(action, index) in service.actions" :key="index">
                                    <p>{{ action }}</p>
                                </li>
                            </ul>
                        </td>
                        <td>
                            <button><fa icon="edit" width="24" height="24" /></button>
                            <button><fa icon="trash-alt" width="24" height="24" @click="deleteOne(service.id)"/></button>
                        </td>
                    </tr>
                </tbody>
            </table>
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