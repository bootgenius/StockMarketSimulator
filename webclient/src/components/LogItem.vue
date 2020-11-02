<template>
    <div>{{ source.value }}</div>
</template>

<script>
    export default {
        name: 'item-component',
        props: {
            index: { // index of current item
                type: Number
            },
            source: { // here is: {uid: 'unique_1', text: 'abc'}
                type: Object,
                default () {
                    return {}
                }
            }
        }
    }
</script>