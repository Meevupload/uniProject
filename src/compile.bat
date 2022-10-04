@echo off
echo Compiling FarmerClient...
cd .\FarmerClient
javac FarmerClient.java
jar cfm FarmerClient.jar FarmerClient.mf *.class
echo FarmerClient compilation finished.
echo Compiling WeatherClient...
cd ..\WeatherClient
javac WeatherClient.java
jar cfm WeatherClient.jar WeatherClient.mf *.class
echo WeatherClient compilation finished.
echo Compiling Server...
cd ..\Server
javac Server.java
jar cfm Server.jar Server.mf *.class
echo Server compilation finished.
echo Compilation completed.
pause
