<template>
    <div class="AreaModale">
        <div v-if="step === 1" class="ServiceList">
            <button @click="step += 1; service='Gmail'"><img class="ServiceImg" src="Gmail.png"/>Gmail</button>
            <button @click="step += 1; service='Epitech'"><img class="ServiceImg" src="Epitech.png"/>Epitech</button>
            <button @click="step += 1; service='Github'"><img class="ServiceImg" src="Github.png"/>GitHub</button>
            <button @click="step += 1; service='Trello'"><img class="ServiceImg" src="Trello.png"/>Trello</button>
            <button @click="step += 1; service='GitLab'"><img class="ServiceImg" src="GitLab.png"/>GitLab</button>
            <button @click="step += 1; service='Agenda'"><img class="ServiceImg" src="Agenda.png"/>Agenda</button>
        </div>
        <div v-if="step === 2">
            <GmailAddService v-if="service === 'Gmail'" @closemodale="CloseModale" @backstep="step = 1; service = ''" />
            <EpitechAddService v-else-if="service === 'Epitech'" @closemodale="CloseModale" @backstep="step = 1; service = ''" />
            <GithubAddService v-else-if="service === 'Github'" @closemodale="CloseModale" @backstep="step = 1; service = ''" />
            <TrelloAddService v-else-if="service === 'Trello'" @closemodale="CloseModale" @backstep="step = 1; service = ''" />
            <GitLabAddService v-else-if="service === 'GitLab'" @closemodale="CloseModale" @backstep="step = 1; service = ''" />
            <AgendaAddService v-else @closemodale="CloseModale" @backstep="step = 1; service = ''" />
        </div>
    </div>
</template>


<script>
import GmailAddService from '@/Components/Service/Trigger/GmailAddService.vue';
import EpitechAddService from '@/Components/Service/Trigger/EpitechAddService.vue';
import GithubAddService from '@/Components/Service/Trigger/GithubAddService.vue';
import TrelloAddService from '@/Components/Service/Trigger/TrelloAddService.vue';
import GitLabAddService from '@/Components/Service/Trigger/GitLabAddService.vue';
import AgendaAddService from '@/Components/Service/Trigger/AgendaAddService.vue';

export default {
    data () {
        return {
            step: 1,
            service: "",
        }
    },

    methods: {
        CloseModale: function() {
            this.$emit("closemodale");
        }
    },

    components: {
        GmailAddService,
        EpitechAddService,
        GithubAddService,
        TrelloAddService,
        GitLabAddService,
        AgendaAddService,
    }

}

</script>



<style lang="scss">
    @import 'Style/AreaModale.scss';
    @import 'Style/serviceStyle.scss';
    @import 'Style/AgendaStyle.scss';
    @import 'Style/TrelloStyle.scss';
    @import 'Style/GmailStyle.scss';
    @import 'Style/DriveStyle.scss';
    @import 'Style/GithubStyle.scss';
    @import 'Style/GitLabStyle.scss';
    @import 'Style/EpitechStyle.scss';
</style>