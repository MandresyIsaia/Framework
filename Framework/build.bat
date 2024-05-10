@echo off
set DESTINATION_DIR=D:\s4\Naina\sprint0\test

if not exist "%DESTINATION_DIR%\lib" (
    mkdir "%DESTINATION_DIR%\lib"
)

javac -cp C:\xampp\tomcat\lib\servlet-api.jar -d .\classes *.java

jar -cvf "%DESTINATION_DIR%\lib\framework.jar" -C .\classes .

copy "%DESTINATION_DIR%\lib\framework.jar" "%DESTINATION_DIR%\lib"
