@echo off
set DESTINATION_DIR=D:\s4\Naina\sprint13\Test

if not exist "%DESTINATION_DIR%\lib" (
    mkdir "%DESTINATION_DIR%\lib"
)

javac -cp .\lib\* -d .\classes *.java
jar -cf .\framework.jar -C .\classes .

copy .\framework.jar "%DESTINATION_DIR%\lib"