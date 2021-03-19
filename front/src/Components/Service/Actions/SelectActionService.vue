<template>
    <div class="AreaModale">
        <h2 v-if="step === 1">Reaction</h2>
        <div v-if="step === 1" class="ServiceList">
            <span @click="step += 1; service='Github'"><img class="ServiceImg" src="Github.png"/></span>
            <span @click="step += 1; service='Drive'"><img class="ServiceImg" src="drive.png"/></span>
            <span @click="step += 1; service='Gmail'"><img class="ServiceImg" src="Gmail.png"/></span>
            <span @click="step += 1; service='GitLab'"><img class="ServiceImg" src="GitLab.png"/></span>
            <span @click="step += 1; service='Trello'"><img class="ServiceImg" src="Trello.png"/></span>
        </div>
        <button v-if="step === 1" @click="this.$emit('return')" id="Back"> Back </button>

        <div v-if="step === 2">
            <GithubAddServiceAction v-if="service === 'Github'" @closemodale="CloseModale" @backstep="step = 1; service = ''" :triggerData="triggerData"/>
            <TrelloAddServiceAction v-if="service === 'Trello'" @backstep="step = 1; service = ''" :triggerData="triggerData"/>
            <GmailAddServiceAction v-if="service === 'Gmail'" @closemodale="CloseModale" @backstep="step = 1; service = ''" :triggerData="triggerData"/>
            <DriveAddServiceAction v-else-if="service === 'Drive' " @closemodale="CloseModale" @backstep="step = 1; service = ''" :triggerData="triggerData" />
            <GitLabAddServiceAction v-else-if="service === 'GitLab' " @closemodale="CloseModale" @backstep="step = 1; service = ''" :triggerData="triggerData" />
        </div>
    </div>
</template>


<script>
import GithubAddServiceAction from '@/Components/Service/Actions/GithubAddServiceAction.vue';
import TrelloAddServiceAction from '@/Components/Service/Actions/TrelloAddServiceAction.vue';
import GmailAddServiceAction from '@/Components/Service/Actions/GmailAddServiceAction.vue';
import DriveAddServiceAction from '@/Components/Service/Actions/DriveAddServiceAction.vue';
import GitLabAddServiceAction from '@/Components/Service/Actions/GitLabAddServiceAction.vue';

export default {
    props: {
        triggerData: Object,
    },

    data () {
        return {
            step: 1,
            service: "",
        }
    },

    methods: {
        CloseModale: function() {
            this.$emit("closemodale");
        },
    },

    components: {
        GithubAddServiceAction,
        TrelloAddServiceAction,
        GmailAddServiceAction,
        DriveAddServiceAction,
        GitLabAddServiceAction,
    }

}

</script>