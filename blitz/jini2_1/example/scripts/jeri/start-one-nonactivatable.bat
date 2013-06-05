@echo off
rem *** Batch file, called from wrun.bat, 
rem *** that starts one nonactivatable service
rem *** running under JERI

set JDK_HOME=%1
set JINI_HOME=%2
set EXAMPLE_HOME=%3
set HOSTNAME=%4
set CODEBASE_PORT=%5
set SERVICE_NAME=%6
set INTERFACE_NAME=%7
set IMPL_NAME=%8
set DEBUG_ON=%9

if on==%DEBUG_ON% set DEBUG=access,failure

set PERSIST_DIR=C:\temp\jeri-%SERVICE_NAME%-log

set START_SERVICE=%JDK_HOME%\bin\java ^
-Djava.security.manager= ^
-Djava.security.policy=%EXAMPLE_HOME%\example\common\jeri\policy\start-service.policy ^
-Djava.util.logging.manager=com.sun.jini.logging.LogManager ^
-Djava.util.logging.config.file=%EXAMPLE_HOME%\example\common\logging\jini.logging ^
-Dconfig=%EXAMPLE_HOME%\example\common\jeri\config\start-one-nonactivatable.config ^
-DserverHost=%HOSTNAME% ^
-DserviceName=%SERVICE_NAME% ^
-DinterfaceName=%INTERFACE_NAME% ^
-DimplName=%IMPL_NAME% ^
-DpersistDir=%PERSIST_DIR% ^
-DcodebaseHost=%HOSTNAME% ^
-DcodebasePort=%CODEBASE_PORT% ^
-DjiniHome=%JINI_HOME% ^
-DappHome=%EXAMPLE_HOME% ^
-Djava.security.debug=%DEBUG% ^
-jar %JINI_HOME%\lib\start.jar ^
%EXAMPLE_HOME%\example\common\jeri\config\start-one-nonactivatable.config

echo.
echo %START_SERVICE%
echo.

%START_SERVICE%
