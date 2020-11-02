<template>
    <v-card flat tile class="d-flex flex-column" width="250" max-width="250" min-width="250">
        <v-subheader>ORDER BOOK</v-subheader>

        <div class="list-wrapper d-flex flex-column overflow-y-auto">
            <div class="sell-list d-flex flex-column justify-end">
                <v-list>
                    <template v-for="(item, index) in sellOrders">
                        <div :key="index">
                            <v-divider v-if="index === 0"/>
                            <div class="price-wrapper">
                                <v-progress-linear color="light red lighten-3"
                                                   background-color="white"
                                                   height="30"
                                                   :value="item.percents"
                                                   reverse
                                                   class="progress-bar-sell">
                                    <template>
                                        <div class="d-flex grow justify-end pr-1 text-body-2">{{item.quantity}}</div>
                                    </template>
                                </v-progress-linear>
                                <div class="d-flex justify-center price pt-1">
                                    <div>{{item.price}}</div>
                                </div>
                            </div>
                            <v-divider/>
                        </div>
                    </template>
                </v-list>
            </div>

            <div class="buy-list d-flex flex-column justify-start">
                <v-list>
                    <template v-for="(item, index) in buyOrders">
                        <div :key="index">
                            <v-divider v-if="index === 0"/>
                            <div class="price-wrapper">
                                <v-progress-linear color="blue lighten-3"
                                                   background-color="white"
                                                   height="25"
                                                   :value="item.percents"
                                                   class="progress-bar-buy">
                                    <template>
                                        <div class="d-flex grow justify-start pl-1 text-body-2">{{item.quantity}}</div>
                                    </template>
                                </v-progress-linear>
                                <div class="d-flex justify-center price"
                                     style="">
                                    <div>{{item.price}}</div>
                                </div>
                            </div>
                            <v-divider/>
                        </div>
                    </template>

                </v-list>
            </div>
        </div>
    </v-card>

</template>

<script>
    import _ from "lodash";

    export default {
        name: "OrderBook",

        props: {
            items: Array,
        },

        data: () => ({}),

        computed: {
            buyOrders() {
                return this.convertItems(this.items, 'BUY', 'desc');
            },

            sellOrders() {
                return this.convertItems(this.items, 'SELL', 'desc');
            }
        },

        methods: {
            convertItems(items, orderType, orderBy){
                let filteredItems = _.filter(items, order => {
                    return order.type === orderType;
                })
                filteredItems = _.orderBy(filteredItems, 'price', orderBy)
                if (_.isEmpty(filteredItems)) {
                    return [];
                }

                let maxQuantity = _.max(filteredItems.map(o => o.quantity));

                return filteredItems.map((order) => {
                    return {
                        price: order.price,
                        percents: Math.round(order.quantity * 100 / maxQuantity),
                        quantity: order.quantity
                    }
                });
            }
        }

    }


</script>

<style scoped>
    .progress-bar-sell {
        grid-row-start: 1;
        grid-column-start: 2;
    }

    .progress-bar-buy {
        grid-row-start: 1;
        grid-column-start: 1;
    }


    .price {
        grid-row-start: 1;
        grid-column-start: 1;
        grid-column-end: span 2;
        z-index: 100
    }

    .price-wrapper {
        display: grid;
        grid-template-columns: 1fr 1fr;
    }

    .list-wrapper {
        height: 100%;
        display: grid;
        grid-template-columns: 2fr;
    }

    .buy-list {
        height: 100%;
        grid-row-start: 2;
        grid-column-start: 1;
    }

    .sell-list {
        height: 100%;
        grid-row-start: 1;
        grid-column-start: 1;
    }

</style>