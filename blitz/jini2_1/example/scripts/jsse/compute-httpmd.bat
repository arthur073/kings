@echo off
rem *** Batch file, called from run.bat, 
rem *** that computes the HTTPMD codebase
rem *** for the given service (or Phoenix).
rem *** Defaults to the sha algorithm, 
rem *** rather than md5.

set JDK_HOME=%1
set JINI_HOME=%2
set EXAMPLE_HOME=%3
set HOSTNAME=%4
set CODEBASE_PORT=%5
set SERVICE_NAME=%6
set DEBUG=%7

set ALGORITHM=sha
set SOURCE_DIR=%JINI_HOME%\lib-dl
set HTTPMD_CODEBASE=httpmd://%HOSTNAME%:%CODEBASE_PORT%/%SERVICE_NAME%-dl.jar;%ALGORITHM%=0 httpmd://%HOSTNAME%:%CODEBASE_PORT%/jsk-dl.jar;%ALGORITHM%=0

set COMPUTE_CODEBASE=%JDK_HOME%\bin\java ^
-cp %JINI_HOME%\lib\tools.jar;%SOURCE_DIR\jsk-platform.jar ^
-Djava.security.debug=%DEBUG% ^
com.sun.jini.tool.ComputeHttpmdCodebase ^
%SOURCE_DIR% ^
%HTTPMD_CODEBASE%

echo.
echo %COMPUTE_CODEBASE%
echo.

%COMPUTE_CODEBASE%
