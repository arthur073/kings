@echo off
rem *** Batch file, called from wrun.bat, 
rem *** that starts Phoenix running under 
rem *** secure JERI/jsse

set JDK_HOME=%1
set JINI_HOME=%2
set EXAMPLE_HOME=%3
set HOSTNAME=%4
set CODEBASE_PORT=%5
set DEBUG_ON=%6

set PERSIST_DIR=C:\temp\jsse-phoenix-log

if on==%DEBUG_ON% set DEBUG=access,failure

set START_SERVICE=%JDK_HOME%\bin\java ^
-Djava.security.manager= ^
-Djava.security.policy=%EXAMPLE_HOME%\example\common\jsse\policy\start-service.policy ^
-Djava.security.auth.login.config=%EXAMPLE_HOME%\example\common\jsse\login\setup.login ^
-Djavax.net.ssl.trustStore=%EXAMPLE_HOME%\example\common\jsse\truststore\jini-ca.truststore ^
-Djava.protocol.handler.pkgs=net.jini.url ^
-Djava.security.properties=%EXAMPLE_HOME%\example\common\jsse\props\dynamic-policy.properties ^
-Djava.util.logging.config.file=%EXAMPLE_HOME%\example\common\logging\phoenix.logging ^
-Dconfig=%EXAMPLE_HOME%\example\common\jsse\config\start-phoenix.config ^
-DgroupConfig=%EXAMPLE_HOME%\example\phoenix\jsse\config\phoenix-group.config ^
-DserverHost=%HOSTNAME% ^
-DphoenixPort=2000 ^
-DpersistDir=%PERSIST_DIR% ^
-DcodebaseHost=%HOSTNAME% ^
-DcodebasePort=%CODEBASE_PORT% ^
-DjiniHome=%JINI_HOME% ^
-DappHome=%EXAMPLE_HOME% ^
-Djava.security.debug=%DEBUG% ^
-jar %JINI_HOME%\lib\start.jar ^
%EXAMPLE_HOME%\example\common\jsse\config\start-phoenix.config

echo.
echo %START_SERVICE%
echo.

start %START_SERVICE%
