@echo off
rem *** Batch file, called from wrun.bat, 
rem *** that starts Phoenix running under JERI

set JDK_HOME=%1
set JINI_HOME=%2
set EXAMPLE_HOME=%3
set HOSTNAME=%4
set CODEBASE_PORT=%5
set DEBUG_ON=%6

set PERSIST_DIR=C:\temp\jeri-phoenix-log

if on==%DEBUG_ON% set DEBUG=access,failure

set START_SERVICE=%JDK_HOME%\bin\java ^
-Djava.security.manager= ^
-Djava.security.policy=%EXAMPLE_HOME%\example\common\jeri\policy\start-service.policy ^
-Djava.util.logging.manager=com.sun.jini.logging.LogManager ^
-Djava.util.logging.config.file=%EXAMPLE_HOME%\example\common\logging\phoenix.logging ^
-Dconfig=%EXAMPLE_HOME%\example\common\jeri\config\start-phoenix.config ^
-DgroupConfig=%EXAMPLE_HOME%\example\phoenix\jeri\config\phoenix-group.config ^
-DserverHost=%HOSTNAME% ^
-DpersistDir=%PERSIST_DIR% ^
-DcodebaseHost=%HOSTNAME% ^
-DcodebasePort=%CODEBASE_PORT% ^
-DjiniHome=%JINI_HOME% ^
-DappHome=%EXAMPLE_HOME% ^
-Djava.security.debug=%DEBUG% ^
-jar %JINI_HOME%\lib\start.jar ^
%EXAMPLE_HOME%\example\common\jeri\config\start-phoenix.config

echo.
echo %START_SERVICE%
echo.

start %START_SERVICE%
