# dbswitch-admin-ui

## 一、介绍

基于Vue.js 2.0编写的dbswitch操作管理web端。

## 二、环境

 **node** : >= v14.17.3

## 二、构建

``` bash
# install dependencies
npm install

# serve with hot reload at localhost:8080
npm run dev

# build for production with minification
npm run build

# build for production and view the bundle analyzer report
npm run build --report
```

## 三、部署

执行`npm run build`命令后，将dbswitch-admin-ui\dist目录生成的所有文件拷贝（或替换）到dbswitch-admin\src\main\resources目录下。然后直接使用mvn对整个dbswitch项目打包即可。