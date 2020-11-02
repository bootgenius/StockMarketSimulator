<template>
    <div class="loading-panel-root">
        <loading :active="isLoading"
                 loader="bars"
                 color="blue"
                 :is-full-page="false">
            <div>
                <v-card class="pa-5">
                    <div>
                        <v-progress-circular
                                :value="value"
                                :indeterminate="indeterminate"
                                color="primary"
                        >
                            <div v-if="!indeterminate"
                                    class="progress-circular-text"
                            >
                                {{value}}
                            </div>
                        </v-progress-circular>
                        <div style="display:inline; margin-left: 10px;">{{message ? message : 'Please wait'}}</div>
                    </div>
                </v-card>
            </div>
        </loading>
        <slot>
        </slot>
    </div>
</template>

<script>
    import Loading from "vue-loading-overlay";

    export default {
        name: "LoadingPanel",

        components: {
            Loading
        },

        props: {
            isLoading: {
                type: Boolean,
                default: false
            },
            message: {
                type: String,
                default: null
            },
            value: {
                type: Number,
                default: 0
            },
            indeterminate: {
                type: Boolean,
                default: true
            }
        },

        data: () => ({})

    }
</script>

<style scoped>
    .loading-panel-root {
        display: flex;
        flex-direction: column;
        flex-grow: 1;
        overflow-y: hidden;
    }

    .progress-circular-text {
        font-size: 12px;
    }
</style>