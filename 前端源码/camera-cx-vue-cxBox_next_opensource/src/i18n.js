// src/i18n.js
import Vue from 'vue'
import VueI18n from 'vue-i18n'
import store from "./store";
import enLocale from 'element-ui/lib/locale/lang/en'
import zhLocale from 'element-ui/lib/locale/lang/zh-CN'

Vue.use(VueI18n)

function loadLocaleMessages() {
  const locales = require.context(
    './locales',
    true,
    /[A-Za-z0-9-_,\s]+\.json$/i
  )
  const messages = {
    'en': enLocale,
    'zh-CN': zhLocale
  }
  
  locales.keys().forEach(key => {
    // 提取语言和路径信息
    const matched = key.match(/([A-Za-z-]+)\/(.*)\.json$/i)
    if (matched && matched.length > 2) {
      const locale = matched[1]
      const path = matched[2].split('/')
      
      if (!messages[locale]) {
        messages[locale] = {}
      }
      
      // 构建嵌套的消息对象
      let current = messages[locale]
      for (let i = 0; i < path.length - 1; i++) {
        const segment = path[i]
        if (!current[segment]) {
          current[segment] = {}
        }
        current = current[segment]
      }
      
      // 合并内容
      const lastSegment = path[path.length - 1]
      current[lastSegment] = locales(key)
    }
  })
  console.log('messages', messages);
  
  return messages
}

export default new VueI18n({
  locale: store.state.locale,
  fallbackLocale: store.state.locale,
  messages: loadLocaleMessages(),
  silentTranslationWarn: true
})