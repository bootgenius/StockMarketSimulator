<template>
    <v-card flat tile min-width="250">
        <loading-panel :is-loading="isLoading">
            <v-subheader>BUY/SELL</v-subheader>

            <div class="pa-5 pb-0 pt-0">
                <v-card-title>{{symbol ? symbol.name : 'Please select the symbol'}}</v-card-title>
                <v-card-subtitle>{{symbol ? symbol.description : 'Please select the symbol'}}</v-card-subtitle>
            </div>

            <v-container class="pa-5 pb-0 pt-0">
                <v-text-field
                        label="Quantity"
                        type="number"
                        outlined
                        v-model="quantity"
                />
                <v-text-field
                        label="Price"
                        type="number"
                        outlined
                        v-model="price"
                />
            </v-container>

            <v-container class="pa-3 pt-0">
                <v-row align="center" justify="space-around">
                    <v-btn
                            color="primary"
                            class="pa-5 pl-5 pr-5"
                            @click="onAddOrderButtonClick('BUY')"
                            :disabled="!canExecuteBuySellButton">Buy
                    </v-btn>
                    <v-btn
                            color="error"
                            class="pa-5 pl-5 pr-5"
                            @click="onAddOrderButtonClick('SELL')"
                            :disabled="!canExecuteBuySellButton">Sell
                    </v-btn>
                </v-row>
            </v-container>
        </loading-panel>
    </v-card>
</template>

<script>
    import {mapActions} from "vuex";
    import LoadingPanel from "./LoadingPanel";
    import _ from 'lodash';

    export default {
        name: "OperationPanel",

        components: {
            LoadingPanel
        },

        props: {
            symbol: {
                type: Object,
                default: null
            }
        },

        data: () => ({
            isLoading: false,
            quantity: 100,
            price: 50,
        }),

        computed: {
            canExecuteBuySellButton: function () {
                return !_.isEmpty(this.symbol)
                    && this.quantity !== null
                    && this.price !== null
                    && this.quantity > 0
                    && this.price > 0;
            },
        },

        methods: {
            ...mapActions({
                addOrder: 'main/addOrder'
            }),

            onAddOrderButtonClick: async function (orderType) {
                this.isLoading = true;

                try {
                    await this.addOrder({
                        symbol: this.symbol.name,
                        type: orderType,
                        price: this.price,
                        quantity: this.quantity
                    });
                } catch (error) {
                    console.log(error);
                }
                this.isLoading = false;
            },
        }
    }


</script>

<style scoped>
    .component-root {
    }
</style>