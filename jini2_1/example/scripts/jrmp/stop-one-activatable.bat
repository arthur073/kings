@echo off
rem *** Batch file, called from wrun.bat, 
rem *** that destroys the shared VM in which one
rem *** activatable service is running under JRMP,
rem *** and removes the service's log files.

set JDK_HOME=%1
set JINI_HOME=%2
set EXAMPLE_HOME=%3
set HOSTNAME=%4
set CODEBASE_PORT=%5
set SERVICE_NAME=%6
set INTERFACE_NAME=%7
set IMPL_NAME=%8

set PERSIST_DIR=C:\temp\jrmp-%SERVICE_NAME%-log

set START_SERVICE=%JDK_HOME%\bin\java ^
-Djava.security.manager= ^
-Djava.security.policy=%EXAMPLE_HOME%\example\common\jrmp\policy\start-service.policy ^
-Djava.util.logging.manager=com.sun.jini.logging.LogManager ^
-Djava.util.logging.config.file=%EXAMPLE_HOME%\example\common\logging\jini.logging ^
-Dconfig=%EXAMPLE_HOME%\example\common\jrmp\config\start-one-activatable.config ^
-DserverHost=%HOSTNAME% ^
-DsharedVMLog=C:\temp\jrmp-%SERVICE_NAME%-sharedvm ^
-DserviceName=%SERVICE_NAME% ^
-DinterfaceName=%INTERFACE_NAME% ^
-DimplName=%IMPL_NAME% ^
-DpersistDir=%PERSIST_DIR% ^
-DcodebaseHost=%HOSTNAME% ^
-DcodebasePort=%CODEBASE_PORT% ^
-DjiniHome=%JINI_HOME% ^
-DappHome=%EXAMPLE_HOME% ^
-jar %JINI_HOME%\lib\destroy.jar ^
%EXAMPLE_HOME%\example\common\jrmp\config\start-one-activatable.config

echo.
echo %START_SERVICE%
echo.

%START_SERVICE%

echo del %PERSIST_DIR% /s /q
del %PERSIST_DIR% /s /q
echo.
echo rmdir %PERSIST_DIR% /s /q
rmdir %PERSIST_DIR% /s /q
echo.
