import Vuex from "vuex";
import Vue from 'vue';
import main from './modules/MainModule'

Vue.use(Vuex);

const debug = process.env.NODE_ENV !== 'production';

export default new Vuex.Store({
    modules: {
        main
    },
    strict: debug,
})