@echo off
rem Batch file, called from wrun.bat, 
rem that starts the HTTPD server

set JDK_HOME=%1
set JINI_HOME=%2

set PORT_ARG=-port %3
set DIR_ARG=-dir %4

set START_HTTPD=%JDK_HOME%\bin\java -jar %JINI_HOME%\lib\tools.jar -verbose %PORT_ARG% %DIR_ARG%

echo %START_HTTPD%
echo.
start %START_HTTPD%

