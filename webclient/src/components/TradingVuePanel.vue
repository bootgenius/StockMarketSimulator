<template>
    <v-card flat tile class="d-flex flex-column pt-1">
        <v-subheader>Chart</v-subheader>
        <trading-vue ref="tradingVue" :data="historyData"
                     :title-txt="symbol ? symbol.name : 'Please select the symbol'"
                     :color-back="colors.back"
                     :color-grid="colors.grid"
                     :color-text="colors.text"
                     :color-cross="colors.cross"
                     :color-candle-dw="colors.candle_dw"
                     :color-wick-dw="colors.wick_dw"
                     :color-title="colors.tvTitle"
        ></trading-vue>
    </v-card>
</template>

<script>
    import TradingVue from 'trading-vue-js'
    import _ from 'lodash'

    export default {
        name: "TradingVuePanel",

        components: {
            TradingVue
        },

        props: {
            symbol: Object,
            history: Array
        },

        computed: {

            name: function() {
                return this.symbol ? this.symbol.name : 'Please select the symbol';
            },

            colors: function(){
                return {
                    back: '#fff',
                    grid: '#eee',
                    text: '#333',
                    candle_dw: 'black',
                    wick_dw: 'black'
                }
            },

            historyData: function() {
                let items = _.map(this.history, (item) => {
                    return [new Date(item.timeStamp).getTime(), item.open, item.high, item.low, item.close, item.volume]
                });

                return {
                    ohlcv: items
                }
            }

        },

        data: () => ({
        }),

        mounted() {
            setInterval(() => {
                if (!_.isEmpty(this.$refs.tradingVue)){
                    this.$refs.tradingVue.goto(new Date().getTime());
                }
            }, 1000);
        }

    }
</script>

<style scoped>

</style>