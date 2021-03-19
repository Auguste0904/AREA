<template>
    <div class="BgMod">
        <div class="AreaModale">
            <span class="fas fa-times" @click="CloseModale"/>
            <span v-if="step === 1"><h2>Action</h2></span>
            <div v-if="step === 1" class="ServiceList">
                <span @click="step += 1; service='Gmail'"><img class="ServiceImg" src="Gmail.png"/></span>
                <span @click="step += 1; service='Epitech'"><img class="ServiceImg" src="Epitech.png"/></span>
                <span @click="step += 1; service='Github'"><img class="ServiceImg" src="Github.png"/></span>
                <span @click="step += 1; service='Trello'"><img class="ServiceImg" src="Trello.png"/></span>
                <span @click="step += 1; service='GitLab'"><img class="ServiceImg" src="GitLab.png"/></span>
                <span @click="step += 1; service='Agenda'" class="fas fa-calendar-alt"></span>
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