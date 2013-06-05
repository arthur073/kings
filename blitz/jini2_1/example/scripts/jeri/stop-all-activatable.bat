@echo off
rem *** Batch file, called from wrun.bat, 
rem *** thatdestroys the shared VM in which all the
rem *** activatable services are running under JERI,
rem *** and removes the service's log files.

set JDK_HOME=%1
set JINI_HOME=%2
set EXAMPLE_HOME=%3
set HOSTNAME=%4
set CODEBASE_PORT=%5

set REGGIE_PERSIST_DIR=C:\temp\jeri-reggie-log
set MAHALO_PERSIST_DIR=C:\temp\jeri-mahalo-log
set FIDDLER_PERSIST_DIR=C:\temp\jeri-fiddler-log
set NORM_PERSIST_DIR=C:\temp\jeri-norm-log
set NORM_JOINSTATE_DIR=%NORM_PERSIST_DIR%\JoinState
set MERCURY_PERSIST_DIR=C:\temp\jeri-mercury-log
set OUTRIGGER_PERSIST_DIR=C:\temp\jeri-outrigger-log

set START_SERVICE=%JDK_HOME%\bin\java ^
-Djava.security.manager= ^
-Djava.security.policy=%EXAMPLE_HOME%\example\common\jeri\policy\start-service.policy ^
-Djava.util.logging.manager=com.sun.jini.logging.LogManager ^
-Djava.util.logging.config.file=%EXAMPLE_HOME%\example\common\logging\jini.logging ^
-Dconfig=%EXAMPLE_HOME%\example\common\jeri\config\start-all-activatable.config ^
-DserverHost=%HOSTNAME% ^
-DsharedVMLog=C:\temp\jeri-all-sharedvm ^
-DpersistDir=%PERSIST_DIR% ^
-DcodebaseHost=%HOSTNAME% ^
-DcodebasePort=%CODEBASE_PORT% ^
-DjiniHome=%JINI_HOME% ^
-DappHome=%EXAMPLE_HOME% ^
-jar %JINI_HOME%\lib\destroy.jar ^
%EXAMPLE_HOME%\example\common\jeri\config\start-all-activatable.config

echo.
echo %START_SERVICE%
echo.

%START_SERVICE%

echo del %REGGIE_PERSIST_DIR% /s /q
del %REGGIE_PERSIST_DIR% /s /q
echo rmdir %REGGIE_PERSIST_DIR% /s /q
rmdir %REGGIE_PERSIST_DIR% /s /q
echo.
echo del %MAHALO_PERSIST_DIR% /s /q
del %MAHALO_PERSIST_DIR% /s /q
echo rmdir %MAHALO_PERSIST_DIR% /s /q
rmdir %MAHALO_PERSIST_DIR% /s /q
echo.
echo del %FIDDLER_PERSIST_DIR% /s /q
del %FIDDLER_PERSIST_DIR% /s /q
echo rmdir %FIDDLER_PERSIST_DIR% /s /q
rmdir %FIDDLER_PERSIST_DIR% /s /q
echo.
echo del %NORM_JOINSTATE_DIR% /s /q
del %NORM_JOINSTATE_DIR% /s /q
echo rmdir %NORM_JOINSTATE_DIR% /s /q
rmdir %NORM_JOINSTATE_DIR% /s /q
echo.
echo del %NORM_PERSIST_DIR% /s /q
del %NORM_PERSIST_DIR% /s /q
echo rmdir %NORM_PERSIST_DIR% /s /q
rmdir %NORM_PERSIST_DIR% /s /q
echo.
echo del %MERCURY_PERSIST_DIR% /s /q
del %MERCURY_PERSIST_DIR% /s /q
echo rmdir %MERCURY_PERSIST_DIR% /s /q
rmdir %MERCURY_PERSIST_DIR% /s /q
echo.
echo del %OUTRIGGER_PERSIST_DIR% /s /q
del %OUTRIGGER_PERSIST_DIR% /s /q
echo rmdir %OUTRIGGER_PERSIST_DIR% /s /q
rmdir %OUTRIGGER_PERSIST_DIR% /s /q
echo.
