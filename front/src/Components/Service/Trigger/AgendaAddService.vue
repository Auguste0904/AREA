<template>
    <div>
        <div class="AgendaTrigger" v-if="!next">
            <h2>Agenda </h2>
            <h3> Trigger </h3>

            <div><input type="checkbox" id="TriggerOne" v-model="triggerOne" >
            <label for="TriggerOne"> Days Until </label></div>

            <div v-if="triggerOne" class="InputTriggerDiv"> 
                <label for="ActionDays"> Days </label>
                <input type="number" min="1" id="ActionDays" v-model="daysUntil">
                &nbsp;Before&nbsp;<input type="date" id="untilDate" v-model="untilDate" :min="limiteDate"/>
            </div>

            <div><input type="checkbox" id="TriggerTwo" v-model="triggerTwo" >
            <label for="TriggerTwo"> Every X Y </label></div>

            <div v-if="triggerTwo" class="InputTriggerDiv">
                <label for="recurrence"> Recurrence </label>
                <input type="number" min="1" id="recurrence" v-model="recurrence" >
                <select v-model="type" required>
                    <option value="day"> Day </option>
                    <option value="week"> Week </option>
                    <option value="month"> Month </option>
                </select>
            </div>
            <button @click="NextStep" type="button" id="Next" :disabled="CheckCorrect() === false"> Next </button>
            <button @click="this.$emit('backstep')" id="Back" >Back</button>
        </div>
        <SelectActionService v-if="next" :triggerData="triggerData" @return="next = false"/>
    </div>
</template>

<script>
import SelectActionService from '@/Components/Service/Actions/SelectActionService.vue';


export default {

    data () {
        return {
            next: false,
            limiteDate: "",
            triggerData: {},
            triggerOne: false,
            triggerTwo: false,
            daysUntil: undefined,
            recurrence: undefined,
            untilDate: "",
            type: "day",
        }
    },
    
    methods: {
        CheckCorrect: function () {
            if (!this.triggerOne && !this.triggerTwo)
                return false;
            if (this.triggerOne && (!this.daysUntil || this.daysUntil < 1 || this.untilDate === ""))
                return false;
            if (this.triggerTwo && (!this.recurrence || this.recurrence < 1 || this.type === ''))
                return false;
            return true;
        },

        NextStep: function () {
            var condition = []

            if (this.triggerOne) {
                condition.push({ event: 1, condition: `${this.daysUntil} ${Date.parse(this.untilDate)}` });
            } if (this.triggerTwo)
                condition.push({ event: 2, condition: `${this.recurrence} ${this.type}` });
            this.triggerData.trigger = 7;
            this.triggerData.conditions = condition;
            this.next = true;
        },
    },

    created () {
        const date = new Date();
        const year = date.getFullYear();
        const month = date.getMonth() + 1;
        const day = date.getDay();
        var result = `${year}-`;

        if (month < 10)
            result += '0';
        result += `${month}-`;
        if (day < 10)
            result += '0';
        result += `${day}`;
        this.limiteDate = result;
    },

    components: {
        SelectActionService,
    },

}

</script>