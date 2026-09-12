import i18n from '@/i18n';

// 用户性别
export function getSex(val){
    if(val){
        const list = JSON.parse(localStorage.getItem('user_sex'))
        const value = list.find(e=>e.val == val)
        return value.name
    } else {
        return i18n.t('utils.filters.2w5643')
    }
}
// 用户婚姻
export function getMarriage(val){
    if(val){
        const list = JSON.parse(localStorage.getItem('user_marriage'))
        const value = list.find(e=>e.val == val)
        return value.name
    } else {
        return i18n.t('utils.filters.2w5643')
    }
}
// 用户学历
export function getSchool(val){
    if(val){
        const list = JSON.parse(localStorage.getItem('user_school'))
        const value = list.find(e=>e.val == val)
        return value.name
    } else {
        return i18n.t('utils.filters.2w5643')
    }
}
// 4色人群 类型
export function getColor(val){
    if(val){
        const list = JSON.parse(localStorage.getItem('user_color'))
        const value = list.find(e=>e.val == val)
        return value.name
    } else {
        return i18n.t('utils.filters.2w5643')
    }
}
