<template>
    <v-card flat tile class="d-flex flex-column">
        <v-subheader>SYMBOLS</v-subheader>
        <div class="overflow-y-auto">
            <v-list
                    class="component-root overflow-y-auto"
            >

                <v-list-item-group
                        v-model="selectedItem"
                        color="primary"
                >
                    <v-list-item
                            v-for="(item) in items"
                            :key="item.name"
                            :value="item"
                    >
                        <v-icon>mdi-circle-small</v-icon>
                        <v-list-item-content>
                            <v-list-item-title v-text="item.name"/>
                            <v-list-item-subtitle v-text="item.description"/>
                        </v-list-item-content>
                    </v-list-item>
                </v-list-item-group>
            </v-list>
        </div>

    </v-card>
</template>

<script>

    export default {
        name: "SymbolList",

        props: {
            items: Array,
        },

        data: () => ({
            selectedItem: null
        }),

        watch: {
            selectedItem: function (newValue) {
                this.$emit('selectedItemChanged', newValue);
            },

            items: function (newValue, oldValue) {
                if (oldValue.length === 0) {
                    this.selectedItem = newValue[0];
                }
            },
        }
    }


</script>

<style scoped>
    .component-root {
        min-width: 200px;
        max-height: 100%;
    }
</style>