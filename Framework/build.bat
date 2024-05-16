@echo off
setlocal

rem Récupérer le nom de l'application en argument
set "nomApp=%1"

rem Chemin vers le répertoire web de l'application
set "web=.\%nomApp%\web\*"
set "webxml=.\%nomApp%\web.xml"
set "dispatcherxml=.\%nomApp%\dispatcher-servlet.xml"
set "classes=.\%nomApp%\classes"
set "lib=.\%nomApp%\lib\*"
set "src=.\%nomApp%\src"
rem Chemin vers le répertoire temporaire
set "temp=%TEMP%\%nomApp%"

rem Chemin vers le répertoire webapps de Tomcat
set "webapp=C:\Program Files\Java\jdk1.8.0_361\Tomcat9x\webapps"
rem Création du répertoire temporaire
if exist "%temp%" (
    rmdir /s /q "%temp%"
)


mkdir "%temp%"
mkdir "%temp%\WEB-INF"
mkdir "%temp%\WEB-INF\lib"
mkdir "%temp%\WEB-INF\classes"
javac -cp "%lib%" -d "%temp%\WEB-INF\classes" "%src%"\*.java

rem Copie des fichiers JSP et HTML nécessaires
xcopy "%web%" "%temp%" /s /i /y

rem Copie des fichiers web.xml et dispatcher-servlet.xml
copy "%webxml%" "%temp%\WEB-INF" /y
copy "%dispatcherxml%" "%temp%\WEB-INF" /y

rem Copie des classes nécessaires
rem xcopy "%classes%" "%temp%\WEB-INF\classes" /s /i /y

rem Copie des fichiers du répertoire lib dans WEB-INF\lib
xcopy "%lib%" "%temp%\WEB-INF\lib" /s /i /y

rem Création du fichier WAR à partir du contenu du répertoire temporaire
set "warFile=%TEMP%\%nomApp%.war"
pushd "%temp%"
jar -cvf "%warFile%" *

rem Copie du fichier WAR dans le répertoire webapps de Tomcat pour le déploiement
copy "%warFile%" "%webapp%" /y

rem Affichage d'un message de déploiement réussi
echo Déploiement de '%nomApp%' réussi.

:end
rem Nettoyage du répertoire temporaire
cd /
if exist "%temp%" (
    rmdir /s /q "%temp%"
)

endlocal