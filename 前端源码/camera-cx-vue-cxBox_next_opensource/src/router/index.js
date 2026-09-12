/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-07-09 10:33:16
 * @LastEditors: 张健，dhq
 * @LastEditTime: 2022-09-11 13:40:58
 */
import Vue from 'vue'
import VueRouter from 'vue-router'
import allRoutes from './data'
import Cookies from 'js-cookie'

Vue.use(VueRouter)

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location, onResolve, onReject) {
if (onResolve || onReject) return originalPush.call(this, location, onResolve, onReject)
return originalPush.call(this, location).catch(err => err)
}
const router = new VueRouter({
  mode: 'hash',
  scrollBehavior: () => ({ y: 0 }),
  routes: allRoutes,
});


export default router