@echo off
rem Batch file, called from wrun.bat, 
rem that stops the HTTPD server

set JDK_HOME=%1
set JINI_HOME=%2

set PORT_ARG=-port %3

set STOP_HTTPD=%JDK_HOME%\bin\java -jar %JINI_HOME%\lib\tools.jar %PORT_ARG% -stop

echo %STOP_HTTPD%
echo.
%STOP_HTTPD%

