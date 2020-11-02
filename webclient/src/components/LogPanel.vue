<template>
    <v-card flat tile min-height="200" max-height="200" class="d-flex flex-column pt-1">
        <v-subheader>LOGS</v-subheader>

        <virtual-list class="overflow-y-auto mt-3 pa-3"
                      ref="logList"
                      :data-key="'id'"
                      :data-sources="logItems"
                      :data-component="itemComponent"
        />
    </v-card>
</template>

<script>

    import _ from 'lodash'
    import VirtualList from 'vue-virtual-scroll-list'
    import LogItem from "./LogItem"; /* eslint-disable vue/no-unused-components */

    export default {
        name: "LogPanel",

        components: {
            LogItem,
            VirtualList
        },

        props: {
            items: Array,
        },

        watch: {
            items() {
                if (this.$refs.logList) {
                    this.$refs.logList.scrollToBottom();
                }
            }
        },

        computed: {
            logItems() {
                return _.map(this.items, (value, index) => {
                    return {
                        id: index,
                        value: value
                    }
                });
            }
        },

        data: () => ({
            itemComponent: LogItem,
        })
    }


</script>

<style scoped>
    .component-root {
    }
</style>