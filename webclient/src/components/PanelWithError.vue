<template>
    <div class="panel-with-error-root">
        <slot v-if="!errorMessage"/>
        <v-card
                v-if="errorMessage"
                flat outlined tile
                class="panel-with-error-error-container"
        >
            <div v-if="$slots['error-template']">
                <slot name="error-template"/>
            </div>

            <div class="panel-with-error-error-content-wrapper">
                <div v-if="!$slots['error-template']" class="panel-with-error-error-content">
                    <img src="../assets/scull.svg" height="80"/>
                    <div class="panel-with-error-error-message" v-html="errorMessage"></div>
                </div>
                <div v-if="$slots['bottom-template']">
                    <slot name="bottom-template"/>
                </div>
            </div>
        </v-card>
    </div>
</template>

<script>

    export default {
        name: "PanelWithError",

        components: {
        },

        props: {
            errorMessage: {
                type: String,
                default: null
            }
        },
    }
</script>

<style scoped>
    .panel-with-error-root {
        display: flex;
        flex-direction: column;
        flex-grow:1;
        overflow-y: hidden;
    }

    .panel-with-error-error-container {
        display: flex;
        flex-direction: row;
        overflow: hidden;
        flex-grow: 1;
        justify-content: center;
        align-items: center;
    }

    .panel-with-error-error-content {
        display: flex;
        flex-direction: row;
        align-items: center;
    }

    .panel-with-error-error-content-wrapper {
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .panel-with-error-error-message {
        padding-left: 15px;
        white-space: nowrap;
    }


</style>