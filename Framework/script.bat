@echo off
set DESTINATION_DIR=D:\s4\Naina\sprint12\Test

if not exist "%DESTINATION_DIR%\lib" (
    mkdir "%DESTINATION_DIR%\lib"
)

javac -cp .\lib\* -d .\classes *.java

jar -cvf "%DESTINATION_DIR%\lib\framework.jar" -C .\classes .