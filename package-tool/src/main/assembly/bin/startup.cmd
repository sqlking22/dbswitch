::	
:: Author: tang	
:: Date: 2020-03-06
::	
@echo off
title dbswitch
setlocal enabledelayedexpansion
cls

::需要启动的Java类
set APP_MAINCLASS=com.gitee.dbswitch.admin.AdminApplication

::Java应用根目录
set APP_HOME=%~dp0
set APP_HOME=%APP_HOME%\..\
cd %APP_HOME%
set APP_HOME=%cd%

set APP_BIN_PATH=%APP_HOME%\bin
set APP_LIB_PATH=%APP_HOME%\lib
set APP_CONF_PATH=%APP_HOME%\conf
set APP_DRIVERS_PATH=%APP_HOME%\drivers

::java虚拟机启动参数
set JAVA_OPTS=-server -Xms4096m -Xmx4096m -Xmn2048m -XX:+DisableExplicitGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Doracle.jdbc.J2EE13Compliant=true

::打印环境信息
echo System Information:
echo ********************************************************
echo COMPUTERNAME=%COMPUTERNAME%
echo OS=%OS%
echo.
echo APP_HOME=%APP_HOME%
echo APP_MAINCLASS=%APP_MAINCLASS%
echo CLASSPATH=%APP_CONF_PATH%;%APP_LIB_PATH%\*
echo CURRENT_DATE=%date% %time%:~0,8%
echo ********************************************************

::执行java
echo Starting %APP_MAINCLASS% ...
echo java -classpath %APP_CONF_PATH%;%APP_LIB_PATH%\* %JAVA_OPTS% %APP_MAINCLASS%
echo .
java -classpath %APP_CONF_PATH%;%APP_LIB_PATH%\* %JAVA_OPTS% %APP_MAINCLASS%

:exit
pause
