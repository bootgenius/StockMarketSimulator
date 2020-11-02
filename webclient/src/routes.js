import VueRouter from 'vue-router'
import MainPage from "./pages/MainPage";
import AboutPage from "./pages/AboutPage";

export default new VueRouter({
    routes: [
        {
            path: '',
            alias: '/main',
            component: MainPage
        },
        {
            path: '/about',
            component: AboutPage
        }
    ],
    mode: 'history'
})