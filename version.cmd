@echo off

set APP_VERSION=1.7.0

echo "Clean Project ..."
call mvn clean -f pom.xml

echo "Update version ..."
call mvn versions:set -DnewVersion=%APP_VERSION% -DprocessAllModules=true -DallowSnapshots=true -DgenerateBackupPoms=false
call mvn -N versions:update-child-modules 
call mvn versions:commit

:exit
pause