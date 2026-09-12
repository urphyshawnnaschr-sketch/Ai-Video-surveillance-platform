<template>
    <div class="weather">
        <div id="he-plugin-simple"></div>
    </div>
</template>
<script>
    export default {
        name: 'weather',
        components: {},
        data() {
            return {}
        },
        created(){
            window.WIDGET = {
                "CONFIG": {
                    "modules": "01234",
                    "background": "5",
                    "tmpColor": "3A72BB",
                    "tmpSize": "16",
                    "cityColor": "3A72BB",
                    "citySize": "16",
                    "aqiColor": "3A72BB",
                    "aqiSize": "16",
                    "weatherIconSize": "24",
                    "alertIconSize": "18",
                    "padding": "10px 10px 10px 10px",
                    "shadow": "0",
                    "language": "auto",
                    "fixed": "false",
                    "vertical": "top",
                    "horizontal": "left",
                    "key": "9ff26224aa3c4d02b917e3d81b02f133"
                }
            };
            (function (d) {
                var c = d.createElement('link')
                c.rel = 'stylesheet'
                c.href = 'https://widget.qweather.net/simple/static/css/he-simple.css?v=1.4.0'
                var s = d.createElement('script')
                s.src = 'https://widget.qweather.net/simple/static/js/he-simple.js?v=1.4.0'
                var sn = d.getElementsByTagName('script')[0]
                sn.parentNode.insertBefore(c, sn)
                sn.parentNode.insertBefore(s, sn)
            })(document)
        },
        methods: {}
    }
</script>
<style lang="scss" scoped>

</style>
