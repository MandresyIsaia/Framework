@echo off
set DESTINATION_DIR=D:\s4\Naina\sprint9\test

if not exist "%DESTINATION_DIR%\lib" (
    mkdir "%DESTINATION_DIR%\lib"
)

javac -cp C:\xampp\tomcat\lib\* -d .\classes *.java

jar -cvf "%DESTINATION_DIR%\lib\framework.jar" -C .\classes .

rem copy "%DESTINATION_DIR%\lib\framework.jar" "%DESTINATION_DIR%\lib"
