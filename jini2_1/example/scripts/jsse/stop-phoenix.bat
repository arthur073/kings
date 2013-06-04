@echo off
rem *** Batch file, called from wrun.bat, 
rem *** that stops Phoenix running under 
rem *** secure JERI/jsse, and removes all
rem *** of Phoenix's log files.

set JDK_HOME=%1
set JINI_HOME=%2
set EXAMPLE_HOME=%3
set HOSTNAME=%4
set CODEBASE_PORT=%5
set DEBUG_ON=%6
set PHOENIX_MESSAGE_DIGEST=%7
set JSK_MESSAGE_DIGEST=%8

set PERSIST_DIR=C:\temp\jsse-phoenix-log

if on==%DEBUG_ON% set DEBUG=access,failure

set START_SERVICE=%JDK_HOME%\bin\java ^
-Djava.security.manager= ^
-Djava.security.policy=%EXAMPLE_HOME%\example\phoenix\jsse\policy\phoenix.policy ^
-Djava.rmi.server.codebase="httpmd://%HOSTNAME%:%CODEBASE_PORT%/phoenix-dl.jar\;sha=%PHOENIX_MESSAGE_DIGEST% httpmd://%HOSTNAME%:%CODEBASE_PORT%/jsk-dl.jar\;sha=%JSK_MESSAGE_DIGEST%" ^
-Djava.security.auth.login.config=%EXAMPLE_HOME%\example\phoenix\jsse\login\phoenix.login ^
-Djavax.net.ssl.trustStore=%EXAMPLE_HOME%\example\common\jsse\truststore\jini-ca.truststore ^
-Djava.protocol.handler.pkgs=net.jini.url ^
-Djava.security.properties=%EXAMPLE_HOME%\example\common\jsse\props\dynamic-policy.properties ^
-Djava.util.logging.config.file=%EXAMPLE_HOME%\example\common\logging\phoenix.logging ^
-Dconfig=%EXAMPLE_HOME%\example\phoenix\jsse\config\phoenix.config ^
-DgroupConfig=%EXAMPLE_HOME%\example\phoenix\jsse\config\phoenix-group.config ^
-DserverHost=%HOSTNAME% ^
-DphoenixPort=2000 ^
-DpersistDir=%PERSIST_DIR% ^
-DjiniHome=%JINI_HOME% ^
-DappHome=%EXAMPLE_HOME% ^
-Djava.security.debug=%DEBUG% ^
-jar %JINI_HOME%\lib\phoenix.jar -stop ^
%EXAMPLE_HOME%\example\phoenix\jsse\config\phoenix.config

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
