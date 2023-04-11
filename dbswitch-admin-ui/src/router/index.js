import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);

///////////////////////////////////////////////////////////////////////////
// 路由配置
// 参考教程：https://blog.csdn.net/weixin_38404899/article/details/90229805
//
///////////////////////////////////////////////////////////////////////////
const constantRouter = new Router({
  routes: [
    {
      path: '/',
      name: '首页',
      component: () => import('@/views/layout'),
      redirect: '/dashboard',
      children: [
        {
          path: '/dashboard',
          name: '概览',
          icon: "el-icon-menu",
          component: () => import('@/views/dashboard/index')
        },
        {
          path: '/connection',
          name: '连接配置',
          icon: "el-icon-s-order",
          component: () => import('@/views/connection/index'),
          children: [
            {
              path: '/connection/driver',
              name: '驱动配置',
              icon: "el-icon-help",
              component: () => import('@/views/connection/driver'),
            },
            {
              path: '/connection/list',
              name: '连接管理',
              icon: "el-icon-bank-card",
              component: () => import('@/views/connection/list')
            }
          ]
        },
        {
          path: '/metadata',
          name: '数据目录',
          icon: "el-icon-coin",
          component: () => import('@/views/metadata/index')
        },
        {
          path: '/task',
          name: '任务管理',
          icon: "el-icon-s-tools",
          component: () => import('@/views/task/index'),
          children: [
            {
              path: '/task/assignment',
              name: '任务安排',
              icon: "el-icon-eleme",
              component: () => import('@/views/task/assignment'),
            },
            {
              path: '/task/schedule',
              name: '调度记录',
              icon: "el-icon-pie-chart",
              component: () => import('@/views/task/schedule')
            }
          ]
        },
        {
          path: '/log',
          name: '审计日志',
          icon: "el-icon-platform-eleme",
          //redirect: '/log/access',
          component: () => import('@/views/log/index'),
          children: [
            {
              path: '/log/access',
              name: '登录日志',
              icon: "el-icon-place",
              component: () => import('@/views/log/access')
            },
            {
              path: '/log/action',
              name: '操作日志',
              icon: "el-icon-s-check",
              component: () => import('@/views/log/action')
            }
          ]
        },
        {
          path: '/about',
          name: '关于系统',
          icon: "el-icon-s-custom",
          component: () => import('@/views/about/me')
        },
        {
          path: '/user/personal',
          name: '个人中心',
          hidden: true,
          component: () => import('@/views/personal/index')
        },
        {
          path: '/task/create',
          name: '创建任务',
          hidden: true,
          component: () => import('@/views/task/create')
        },
        {
          path: '/task/update',
          name: '修改任务',
          hidden: true,
          component: () => import('@/views/task/update')
        }
      ],
    },

    {
      path: '/login',
      name: '登录',
      component: () => import('@/views/login')
    }
  ]
});

export default constantRouter;
