@echo off
set DESTINATION_DIR=D:\s6\Naina\Booking\src\main\webapp\WEB-INF\

if not exist "%DESTINATION_DIR%\lib" (
    mkdir "%DESTINATION_DIR%\lib"
)

javac -cp .\lib\* -d .\classes *.java


jar -cvf "%DESTINATION_DIR%\lib\framework.jar" -C .\classes .