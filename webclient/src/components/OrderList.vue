<template>
    <v-card flat tile class="d-flex flex-column" width="250" max-width="250" min-width="250">
        <v-subheader>ORDERS</v-subheader>
        <div class="overflow-y-auto mt-3">
            <v-list class="component-root pa-0"
            >

                <template v-for="(item, index) in items"

                >
                    <div :key="index">
                        <v-divider
                                v-if="index === 0"
                        ></v-divider>
                        <v-list-item :value="item">
                            <v-list-item-content>
                                <v-list-item-title>{{item.type}} {{item.quantity}} {{item.price}}</v-list-item-title>
                                <v-list-item-subtitle>{{ convertDate(item.created)}}</v-list-item-subtitle>
                            </v-list-item-content>
                            <v-list-item-action>
                                <v-hover v-slot="{ hover }">
                                    <v-icon :color="!hover ? 'grey lighten-1' : ''" class="item-delete-button" @click="onDeleteItemClick(item)">
                                        mdi-close-circle
                                    </v-icon>
                                </v-hover>
                            </v-list-item-action>
                        </v-list-item>
                        <v-divider/>
                    </div>
                </template>
            </v-list>
        </div>
    </v-card>
</template>

<script>
    import {dateFormat} from "@vuejs-community/vue-filter-date-format";

    export default {
        name: "OrderList",

        props: {
            items: Array,
        },

        methods: {
            convertDate(date) {
                return dateFormat(new Date(date), 'DD.MM.YYYY HH:mm:ss')
            },

            onDeleteItemClick(item) {
                this.$emit('cancelOrder', item);
            }

        },

        data: () => ({})
    }


</script>

<style scoped>
    .component-root {
    }

    .item-delete-button {
        cursor: pointer;
    }

</style>