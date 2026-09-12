/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-07-09 10:33:16
 * @LastEditors: 张健，dhq 327650114@qq.com
 * @LastEditTime: 2022-09-26 13:49:39
 */
const { defineConfig } = require('@vue/cli-service')
const NodePolyfillPlugin = require('node-polyfill-webpack-plugin')
module.exports = defineConfig({
  publicPath: process.env.VUE_APP_BASE_PUBLICPATH,
  transpileDependencies: true,
  lintOnSave: true,
  configureWebpack: {
    externals: {
      AMap: 'AMap'
    },
    plugins: [new NodePolyfillPlugin()],
  },
  css: {
    loaderOptions: {
            scss: {
                additionalData: `@import "@/style.scss";`
            }
        }
    },
  devServer: {
    client: {
      overlay: false // 默认为true，即开启热更新功能
    },
    port: 8000,
    host: '0.0.0.0',
    proxy: {
      '/': {
        target: process.env.VUE_APP_API_BASE_URL,
        ws: false,
        changeOrigin: true,
        pathRewrite: {
          '^/': ''
        }
      }
    },
  },
})

