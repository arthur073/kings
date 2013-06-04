@echo off

rem **** Set the following items for your system ****
set JDK_HOME=%HOMEDRIVE%%HOMEPATH%j2sdk1.4.2_06
set JINI_HOME=%HOMEDRIVE%%HOMEPATH%jini2_1

rem *** set EXAMPLE_HOME=%HOMEDRIVE%%HOMEPATH%src
set EXAMPLE_HOME=%JINI_HOME%

set HOSTNAME=%COMPUTERNAME%

rem ***************************************************************************
rem ***                           *** NOTE ***
rem *** If the code contained in either phoenix-dl.jar or jsk-dl.jar is 
rem *** changed, then the message digest used to build the codebase for Phoenix
rem *** when running under secure Jini ERI/jsse must be re-computed, and
rem *** the (HTTPMD) codebase for Phoenix must be re-built using the values
rem *** of those new message digest strings.
rem ***
rem *** Note that because the ServiceStarter framework can be used to start
rem *** Phoenix, that message digest can be computed (on-the-fly) in the
rem *** ServiceStarter configuration file for Phoenix (start-phoenix.config).
rem *** In that case, it's not necessary to take any additional steps with
rem *** repsect to computing the message digest for Phoenix. But because
rem *** the ServiceStarter framework cannot currently be used to stop Phoenix,
rem *** the message digest of both Phoenix and jsk-dl.jar must be supplied
rem *** directly to the command line when stopping Phoenix. Thus, for the
rem *** case where this script is used to stop Phoenix, the message digests
rem *** are computed as shown below in the related if-block, and then passed
rem *** as an argument to the appropriate start script.
rem ***
rem *** Note that unlike unix, the command language of Windows does not
rem *** allow for the execution, or automatic substitution of the results of,
rem *** other commands and bat files or command scripts. Thus, unfortunately,
rem *** such a capability cannot be exploited below to automatically 
rem *** re-compute the message digest for Phoenix and jsk-dl.jar whenever 
rem *** needed. This means that if the value of those message digests need to 
rem *** be re-computed, you must re-compute them yourself, and then modify this 
rem *** file by replacing the value of the appropriate MESSAGE_DIGEST 
rem *** constant(s) below with the new value(s).
rem ***
rem *** If you need to re-compute the message digest value, you can execute
rem *** the tool com.sun.jini.tool.ComputeDigest (which computes just the
rem ***  message digest)
rem ***
rem *** Note also that both this bat file and the computeHttpmdCodebase.bat 
rem *** file assume that the 'sha' algorithm is used, rather than the 'md5' 
rem *** algorithm.

set PHOENIX_MESSAGE_DIGEST=552d870124df67bf3d1c00d04bb911d4dcf85459
set JSK_MESSAGE_DIGEST=b4599e8126605d8727a923635e7b23455dedce2b
rem ********************************************************

set RUN_PROGRAM_NAME=%0%

set USAGE=usage: %RUN_PROGRAM_NAME% scriptID [-usage] [-help] [-port port] [-dir dir] [-stop] [-debug]

set ARG_LIST0=(-port -dir)

set STOP=start
set START_TYPE=oneservice
set SCRIPT_BASE=notset
set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jrmp

set SERVICE_NAME=reggie
set INTERFACE_NAME=Registrar
set IMPL_NAME=TransientRegistrarImpl

set PORT=8080
set DIR_ARG=%JINI_HOME%\lib-dl

set DEBUG=off

rem Parse the argument list
:loop
if "%1"=="" goto endloop
set ARG=%1

for %%f in %ARG_LIST0% do if %ARG%==%%f shift

rem *** test for all expected single-item args

if -usage==%ARG% goto showUsage
if -help==%ARG% goto showHelp
if -stop==%ARG% set STOP=stop
if -debug==%ARG% set DEBUG=on

rem *** Httpd
if httpd==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts
if httpd==%ARG% set SCRIPT_BASE=httpd
if httpd==%ARG% set START_TYPE=httpd

rem *** The Browser
if jrmp-browser==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jrmp
if jrmp-browser==%ARG% set SCRIPT_BASE=browser
if jrmp-browser==%ARG% set START_TYPE=browser

if jeri-browser==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-browser==%ARG% set SCRIPT_BASE=browser
if jeri-browser==%ARG% set START_TYPE=browser

if jsse-browser==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-browser==%ARG% set SCRIPT_BASE=browser
if jsse-browser==%ARG% set START_TYPE=browser

rem *** Phoenix
if jrmp-phoenix==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jrmp
if jrmp-phoenix==%ARG% set SCRIPT_BASE=phoenix
if jrmp-phoenix==%ARG% set START_TYPE=phoenix

if jeri-phoenix==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-phoenix==%ARG% set SCRIPT_BASE=phoenix
if jeri-phoenix==%ARG% set START_TYPE=phoenix

if jsse-phoenix==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-phoenix==%ARG% set SCRIPT_BASE=phoenix
if jsse-phoenix==%ARG% set START_TYPE=phoenix

rem *** All jrmp services
if jrmp-activatable==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jrmp
if jrmp-activatable==%ARG% set SCRIPT_BASE=all-activatable
if jrmp-activatable==%ARG% set START_TYPE=allservices

if jrmp-nonactivatable==%ARG% echo *** the non-activatable mode of the services should not be run using JRMP
if jrmp-nonactivatable==%ARG% goto exitScript

if jrmp-transient==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jrmp
if jrmp-transient==%ARG% set SCRIPT_BASE=all-transient
if jrmp-transient==%ARG% set START_TYPE=allservices

rem *** All jeri services
if jeri-activatable==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-activatable==%ARG% set SCRIPT_BASE=all-activatable
if jeri-activatable==%ARG% set START_TYPE=allservices

if jeri-nonactivatable==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-nonactivatable==%ARG% set SCRIPT_BASE=all-nonactivatable
if jeri-nonactivatable==%ARG% set START_TYPE=allservices

if jeri-transient==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-transient==%ARG% set SCRIPT_BASE=all-transient
if jeri-transient==%ARG% set START_TYPE=allservices

rem *** All jsse services
if jsse-activatable==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-activatable==%ARG% set SCRIPT_BASE=all-activatable
if jsse-activatable==%ARG% set START_TYPE=allservices

if jsse-nonactivatable==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-nonactivatable==%ARG% set SCRIPT_BASE=all-nonactivatable
if jsse-nonactivatable==%ARG% set START_TYPE=allservices

if jsse-transient==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-transient==%ARG% set SCRIPT_BASE=all-transient
if jsse-transient==%ARG% set START_TYPE=allservices

rem *** Reggie jrmp
if jrmp-activatable-reggie==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jrmp
if jrmp-activatable-reggie==%ARG% set SCRIPT_BASE=one-activatable
if jrmp-activatable-reggie==%ARG% set SERVICE_NAME=reggie
if jrmp-activatable-reggie==%ARG% set INTERFACE_NAME=Registrar
if jrmp-activatable-reggie==%ARG% set IMPL_NAME=PersistentRegistrarImpl

if jrmp-nonactivatable-reggie==%ARG% echo *** the non-activatable mode of this service should not be run using JRMP
if jrmp-nonactivatable-reggie==%ARG% goto exitScript

if jrmp-transient-reggie==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jrmp
if jrmp-transient-reggie==%ARG% set SCRIPT_BASE=one-transient
if jrmp-transient-reggie==%ARG% set SERVICE_NAME=reggie
if jrmp-transient-reggie==%ARG% set INTERFACE_NAME=Registrar
if jrmp-transient-reggie==%ARG% set IMPL_NAME=TransientRegistrarImpl

rem *** Reggie jeri
if jeri-activatable-reggie==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-activatable-reggie==%ARG% set SCRIPT_BASE=one-activatable
if jeri-activatable-reggie==%ARG% set SERVICE_NAME=reggie
if jeri-activatable-reggie==%ARG% set INTERFACE_NAME=Registrar
if jeri-activatable-reggie==%ARG% set IMPL_NAME=PersistentRegistrarImpl

if jeri-nonactivatable-reggie==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-nonactivatable-reggie==%ARG% set SCRIPT_BASE=one-nonactivatable
if jeri-nonactivatable-reggie==%ARG% set SERVICE_NAME=reggie
if jeri-nonactivatable-reggie==%ARG% set INTERFACE_NAME=Registrar
if jeri-nonactivatable-reggie==%ARG% set IMPL_NAME=PersistentRegistrarImpl

if jeri-transient-reggie==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-transient-reggie==%ARG% set SCRIPT_BASE=one-transient
if jeri-transient-reggie==%ARG% set SERVICE_NAME=reggie
if jeri-transient-reggie==%ARG% set INTERFACE_NAME=Registrar
if jeri-transient-reggie==%ARG% set IMPL_NAME=TransientRegistrarImpl

rem *** Reggie jsse
if jsse-activatable-reggie==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-activatable-reggie==%ARG% set SCRIPT_BASE=one-activatable
if jsse-activatable-reggie==%ARG% set SERVICE_NAME=reggie
if jsse-activatable-reggie==%ARG% set INTERFACE_NAME=Registrar
if jsse-activatable-reggie==%ARG% set IMPL_NAME=PersistentRegistrarImpl

if jsse-nonactivatable-reggie==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-nonactivatable-reggie==%ARG% set SCRIPT_BASE=one-nonactivatable
if jsse-nonactivatable-reggie==%ARG% set SERVICE_NAME=reggie
if jsse-nonactivatable-reggie==%ARG% set INTERFACE_NAME=Registrar
if jsse-nonactivatable-reggie==%ARG% set IMPL_NAME=PersistentRegistrarImpl

if jsse-transient-reggie==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-transient-reggie==%ARG% set SCRIPT_BASE=one-transient
if jsse-transient-reggie==%ARG% set SERVICE_NAME=reggie
if jsse-transient-reggie==%ARG% set INTERFACE_NAME=Registrar
if jsse-transient-reggie==%ARG% set IMPL_NAME=TransientRegistrarImpl

rem *** Mahalo jrmp
if jrmp-activatable-mahalo==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jrmp
if jrmp-activatable-mahalo==%ARG% set SCRIPT_BASE=one-activatable
if jrmp-activatable-mahalo==%ARG% set SERVICE_NAME=mahalo
if jrmp-activatable-mahalo==%ARG% set INTERFACE_NAME=TxnManager
if jrmp-activatable-mahalo==%ARG% set IMPL_NAME=ActivatableMahaloImpl

if jrmp-nonactivatable-mahalo==%ARG% echo *** the non-activatable mode of this service should not be run using JRMP
if jrmp-nonactivatable-mahalo==%ARG% goto exitScript

if jrmp-transient-mahalo==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jrmp
if jrmp-transient-mahalo==%ARG% set SCRIPT_BASE=one-transient
if jrmp-transient-mahalo==%ARG% set SERVICE_NAME=mahalo
if jrmp-transient-mahalo==%ARG% set INTERFACE_NAME=TxnManager
if jrmp-transient-mahalo==%ARG% set IMPL_NAME=TransientMahaloImpl

rem *** Mahalo jeri
if jeri-activatable-mahalo==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-activatable-mahalo==%ARG% set SCRIPT_BASE=one-activatable
if jeri-activatable-mahalo==%ARG% set SERVICE_NAME=mahalo
if jeri-activatable-mahalo==%ARG% set INTERFACE_NAME=TxnManager
if jeri-activatable-mahalo==%ARG% set IMPL_NAME=ActivatableMahaloImpl

if jeri-nonactivatable-mahalo==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-nonactivatable-mahalo==%ARG% set SCRIPT_BASE=one-nonactivatable
if jeri-nonactivatable-mahalo==%ARG% set SERVICE_NAME=mahalo
if jeri-nonactivatable-mahalo==%ARG% set INTERFACE_NAME=TxnManager
if jeri-nonactivatable-mahalo==%ARG% set IMPL_NAME=NonActivatableMahaloImpl

if jeri-transient-mahalo==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-transient-mahalo==%ARG% set SCRIPT_BASE=one-transient
if jeri-transient-mahalo==%ARG% set SERVICE_NAME=mahalo
if jeri-transient-mahalo==%ARG% set INTERFACE_NAME=TxnManager
if jeri-transient-mahalo==%ARG% set IMPL_NAME=TransientMahaloImpl

rem *** Mahalo jsse
if jsse-activatable-mahalo==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-activatable-mahalo==%ARG% set SCRIPT_BASE=one-activatable
if jsse-activatable-mahalo==%ARG% set SERVICE_NAME=mahalo
if jsse-activatable-mahalo==%ARG% set INTERFACE_NAME=TxnManager
if jsse-activatable-mahalo==%ARG% set IMPL_NAME=ActivatableMahaloImpl

if jsse-nonactivatable-mahalo==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-nonactivatable-mahalo==%ARG% set SCRIPT_BASE=one-nonactivatable
if jsse-nonactivatable-mahalo==%ARG% set SERVICE_NAME=mahalo
if jsse-nonactivatable-mahalo==%ARG% set INTERFACE_NAME=TxnManager
if jsse-nonactivatable-mahalo==%ARG% set IMPL_NAME=NonActivatableMahaloImpl

if jsse-transient-mahalo==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-transient-mahalo==%ARG% set SCRIPT_BASE=one-transient
if jsse-transient-mahalo==%ARG% set SERVICE_NAME=mahalo
if jsse-transient-mahalo==%ARG% set INTERFACE_NAME=TxnManager
if jsse-transient-mahalo==%ARG% set IMPL_NAME=TransientMahaloImpl

rem *** Fiddler jrmp
if jrmp-activatable-fiddler==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jrmp
if jrmp-activatable-fiddler==%ARG% set SCRIPT_BASE=one-activatable
if jrmp-activatable-fiddler==%ARG% set SERVICE_NAME=fiddler
if jrmp-activatable-fiddler==%ARG% set INTERFACE_NAME=Fiddler
if jrmp-activatable-fiddler==%ARG% set IMPL_NAME=ActivatableFiddlerImpl

if jrmp-nonactivatable-fiddler==%ARG% echo *** the non-activatable mode of this service should not be run using JRMP
if jrmp-nonactivatable-fiddler==%ARG% goto exitScript

if jrmp-transient-fiddler==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jrmp
if jrmp-transient-fiddler==%ARG% set SCRIPT_BASE=one-transient
if jrmp-transient-fiddler==%ARG% set SERVICE_NAME=fiddler
if jrmp-transient-fiddler==%ARG% set INTERFACE_NAME=Fiddler
if jrmp-transient-fiddler==%ARG% set IMPL_NAME=TransientFiddlerImpl

rem *** Fiddler jeri
if jeri-activatable-fiddler==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-activatable-fiddler==%ARG% set SCRIPT_BASE=one-activatable
if jeri-activatable-fiddler==%ARG% set SERVICE_NAME=fiddler
if jeri-activatable-fiddler==%ARG% set INTERFACE_NAME=Fiddler
if jeri-activatable-fiddler==%ARG% set IMPL_NAME=ActivatableFiddlerImpl

if jeri-nonactivatable-fiddler==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-nonactivatable-fiddler==%ARG% set SCRIPT_BASE=one-nonactivatable
if jeri-nonactivatable-fiddler==%ARG% set SERVICE_NAME=fiddler
if jeri-nonactivatable-fiddler==%ARG% set INTERFACE_NAME=Fiddler
if jeri-nonactivatable-fiddler==%ARG% set IMPL_NAME=NonActivatableFiddlerImpl

if jeri-transient-fiddler==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-transient-fiddler==%ARG% set SCRIPT_BASE=one-transient
if jeri-transient-fiddler==%ARG% set SERVICE_NAME=fiddler
if jeri-transient-fiddler==%ARG% set INTERFACE_NAME=Fiddler
if jeri-transient-fiddler==%ARG% set IMPL_NAME=TransientFiddlerImpl

rem *** Fiddler jsse
if jsse-activatable-fiddler==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-activatable-fiddler==%ARG% set SCRIPT_BASE=one-activatable
if jsse-activatable-fiddler==%ARG% set SERVICE_NAME=fiddler
if jsse-activatable-fiddler==%ARG% set INTERFACE_NAME=Fiddler
if jsse-activatable-fiddler==%ARG% set IMPL_NAME=ActivatableFiddlerImpl

if jsse-nonactivatable-fiddler==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-nonactivatable-fiddler==%ARG% set SCRIPT_BASE=one-nonactivatable
if jsse-nonactivatable-fiddler==%ARG% set SERVICE_NAME=fiddler
if jsse-nonactivatable-fiddler==%ARG% set INTERFACE_NAME=Fiddler
if jsse-nonactivatable-fiddler==%ARG% set IMPL_NAME=NonActivatableFiddlerImpl

if jsse-transient-fiddler==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-transient-fiddler==%ARG% set SCRIPT_BASE=one-transient
if jsse-transient-fiddler==%ARG% set SERVICE_NAME=fiddler
if jsse-transient-fiddler==%ARG% set INTERFACE_NAME=Fiddler
if jsse-transient-fiddler==%ARG% set IMPL_NAME=TransientFiddlerImpl

rem *** Norm jrmp
if jrmp-activatable-norm==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jrmp
if jrmp-activatable-norm==%ARG% set SCRIPT_BASE=one-activatable
if jrmp-activatable-norm==%ARG% set SERVICE_NAME=norm
if jrmp-activatable-norm==%ARG% set INTERFACE_NAME=Registrar
if jrmp-activatable-norm==%ARG% set IMPL_NAME=ActivatableNormServerImpl

if jrmp-nonactivatable-norm==%ARG% echo *** the non-activatable mode of this service should not be run using JRMP
if jrmp-nonactivatable-norm==%ARG% goto exitScript

if jrmp-transient-norm==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jrmp
if jrmp-transient-norm==%ARG% set SCRIPT_BASE=one-transient
if jrmp-transient-norm==%ARG% set SERVICE_NAME=norm
if jrmp-transient-norm==%ARG% set INTERFACE_NAME=NormServer
if jrmp-transient-norm==%ARG% set IMPL_NAME=TransientNormServerImpl

rem *** Norm jeri
if jeri-activatable-norm==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-activatable-norm==%ARG% set SCRIPT_BASE=one-activatable
if jeri-activatable-norm==%ARG% set SERVICE_NAME=norm
if jeri-activatable-norm==%ARG% set INTERFACE_NAME=NormServer
if jeri-activatable-norm==%ARG% set IMPL_NAME=ActivatableNormServerImpl

if jeri-nonactivatable-norm==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-nonactivatable-norm==%ARG% set SCRIPT_BASE=one-nonactivatable
if jeri-nonactivatable-norm==%ARG% set SERVICE_NAME=norm
if jeri-nonactivatable-norm==%ARG% set INTERFACE_NAME=NormServer
if jeri-nonactivatable-norm==%ARG% set IMPL_NAME=PersistentNormServerImpl

if jeri-transient-norm==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-transient-norm==%ARG% set SCRIPT_BASE=one-transient
if jeri-transient-norm==%ARG% set SERVICE_NAME=norm
if jeri-transient-norm==%ARG% set INTERFACE_NAME=NormServer
if jeri-transient-norm==%ARG% set IMPL_NAME=TransientNormServerImpl

rem *** Norm jsse
if jsse-activatable-norm==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-activatable-norm==%ARG% set SCRIPT_BASE=one-activatable
if jsse-activatable-norm==%ARG% set SERVICE_NAME=norm
if jsse-activatable-norm==%ARG% set INTERFACE_NAME=NormServer
if jsse-activatable-norm==%ARG% set IMPL_NAME=ActivatableNormServerImpl

if jsse-nonactivatable-norm==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-nonactivatable-norm==%ARG% set SCRIPT_BASE=one-nonactivatable
if jsse-nonactivatable-norm==%ARG% set SERVICE_NAME=norm
if jsse-nonactivatable-norm==%ARG% set INTERFACE_NAME=NormServer
if jsse-nonactivatable-norm==%ARG% set IMPL_NAME=PersistentNormServerImpl

if jsse-transient-norm==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-transient-norm==%ARG% set SCRIPT_BASE=one-transient
if jsse-transient-norm==%ARG% set SERVICE_NAME=norm
if jsse-transient-norm==%ARG% set INTERFACE_NAME=NormServer
if jsse-transient-norm==%ARG% set IMPL_NAME=TransientNormServerImpl

rem *** Mercury jrmp
if jrmp-activatable-mercury==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jrmp
if jrmp-activatable-mercury==%ARG% set SCRIPT_BASE=one-activatable
if jrmp-activatable-mercury==%ARG% set SERVICE_NAME=mercury
if jrmp-activatable-mercury==%ARG% set INTERFACE_NAME=MailboxBackEnd
if jrmp-activatable-mercury==%ARG% set IMPL_NAME=ActivatableMercuryImpl
if jrmp-activatable-mercury==%ARG% set IMPL_NAME=ActivatableMercuryImpl

if jrmp-nonactivatable-mercury==%ARG% echo *** the non-activatable mode of this service should not be run using JRMP
if jrmp-nonactivatable-mercury==%ARG% goto exitScript

if jrmp-transient-mercury==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jrmp
if jrmp-transient-mercury==%ARG% set SCRIPT_BASE=one-transient
if jrmp-transient-mercury==%ARG% set SERVICE_NAME=mercury
if jrmp-transient-mercury==%ARG% set INTERFACE_NAME=MailboxBackEnd
if jrmp-transient-mercury==%ARG% set IMPL_NAME=TransientMercuryImpl

rem *** Mercury jeri
if jeri-activatable-mercury==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-activatable-mercury==%ARG% set SCRIPT_BASE=one-activatable
if jeri-activatable-mercury==%ARG% set SERVICE_NAME=mercury
if jeri-activatable-mercury==%ARG% set INTERFACE_NAME=MailboxBackEnd
if jeri-activatable-mercury==%ARG% set IMPL_NAME=ActivatableMercuryImpl

if jeri-nonactivatable-mercury==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-nonactivatable-mercury==%ARG% set SCRIPT_BASE=one-nonactivatable
if jeri-nonactivatable-mercury==%ARG% set SERVICE_NAME=mercury
if jeri-nonactivatable-mercury==%ARG% set INTERFACE_NAME=MailboxBackEnd
if jeri-nonactivatable-mercury==%ARG% set IMPL_NAME=NonActivatableMercuryImpl

if jeri-transient-mercury==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-transient-mercury==%ARG% set SCRIPT_BASE=one-transient
if jeri-transient-mercury==%ARG% set SERVICE_NAME=mercury
if jeri-transient-mercury==%ARG% set INTERFACE_NAME=MailboxBackEnd
if jeri-transient-mercury==%ARG% set IMPL_NAME=TransientMercuryImpl

rem *** Mercury jsse
if jsse-activatable-mercury==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-activatable-mercury==%ARG% set SCRIPT_BASE=one-activatable
if jsse-activatable-mercury==%ARG% set SERVICE_NAME=mercury
if jsse-activatable-mercury==%ARG% set INTERFACE_NAME=MailboxBackEnd
if jsse-activatable-mercury==%ARG% set IMPL_NAME=ActivatableMercuryImpl

if jsse-nonactivatable-mercury==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-nonactivatable-mercury==%ARG% set SCRIPT_BASE=one-nonactivatable
if jsse-nonactivatable-mercury==%ARG% set SERVICE_NAME=mercury
if jsse-nonactivatable-mercury==%ARG% set INTERFACE_NAME=MailboxBackEnd
if jsse-nonactivatable-mercury==%ARG% set IMPL_NAME=NonActivatableMercuryImpl

if jsse-transient-mercury==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-transient-mercury==%ARG% set SCRIPT_BASE=one-transient
if jsse-transient-mercury==%ARG% set SERVICE_NAME=mercury
if jsse-transient-mercury==%ARG% set INTERFACE_NAME=MailboxBackEnd
if jsse-transient-mercury==%ARG% set IMPL_NAME=TransientMercuryImpl

rem *** Outrigger jrmp
if jrmp-activatable-outrigger==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jrmp
if jrmp-activatable-outrigger==%ARG% set SCRIPT_BASE=one-activatable
if jrmp-activatable-outrigger==%ARG% set SERVICE_NAME=outrigger
if jrmp-activatable-outrigger==%ARG% set INTERFACE_NAME=OutriggerServer
if jrmp-activatable-outrigger==%ARG% set IMPL_NAME=PersistentOutriggerImpl

if jrmp-nonactivatable-outrigger==%ARG% echo *** the non-activatable mode of this service should not be run using JRMP
if jrmp-nonactivatable-outrigger==%ARG% goto exitScript

if jrmp-transient-outrigger==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jrmp
if jrmp-transient-outrigger==%ARG% set SCRIPT_BASE=one-transient
if jrmp-transient-outrigger==%ARG% set SERVICE_NAME=outrigger
if jrmp-transient-outrigger==%ARG% set INTERFACE_NAME=OutriggerServer
if jrmp-transient-outrigger==%ARG% set IMPL_NAME=TransientOutriggerImpl

rem *** Outrigger jeri
if jeri-activatable-outrigger==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-activatable-outrigger==%ARG% set SCRIPT_BASE=one-activatable
if jeri-activatable-outrigger==%ARG% set SERVICE_NAME=outrigger
if jeri-activatable-outrigger==%ARG% set INTERFACE_NAME=OutriggerServer
if jeri-activatable-outrigger==%ARG% set IMPL_NAME=PersistentOutriggerImpl

if jeri-nonactivatable-outrigger==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-nonactivatable-outrigger==%ARG% set SCRIPT_BASE=one-nonactivatable
if jeri-nonactivatable-outrigger==%ARG% set SERVICE_NAME=outrigger
if jeri-nonactivatable-outrigger==%ARG% set INTERFACE_NAME=OutriggerServer
if jeri-nonactivatable-outrigger==%ARG% set IMPL_NAME=PersistentOutriggerImpl

if jeri-transient-outrigger==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jeri
if jeri-transient-outrigger==%ARG% set SCRIPT_BASE=one-transient
if jeri-transient-outrigger==%ARG% set SERVICE_NAME=outrigger
if jeri-transient-outrigger==%ARG% set INTERFACE_NAME=OutriggerServer
if jeri-transient-outrigger==%ARG% set IMPL_NAME=TransientOutriggerImpl

rem *** Outrigger jsse
if jsse-activatable-outrigger==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-activatable-outrigger==%ARG% set SCRIPT_BASE=one-activatable
if jsse-activatable-outrigger==%ARG% set SERVICE_NAME=outrigger
if jsse-activatable-outrigger==%ARG% set INTERFACE_NAME=OutriggerServer
if jsse-activatable-outrigger==%ARG% set IMPL_NAME=PersistentOutriggerImpl

if jsse-nonactivatable-outrigger==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-nonactivatable-outrigger==%ARG% set SCRIPT_BASE=one-nonactivatable
if jsse-nonactivatable-outrigger==%ARG% set SERVICE_NAME=outrigger
if jsse-nonactivatable-outrigger==%ARG% set INTERFACE_NAME=OutriggerServer
if jsse-nonactivatable-outrigger==%ARG% set IMPL_NAME=PersistentOutriggerImpl

if jsse-transient-outrigger==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if jsse-transient-outrigger==%ARG% set SCRIPT_BASE=one-transient
if jsse-transient-outrigger==%ARG% set SERVICE_NAME=outrigger
if jsse-transient-outrigger==%ARG% set INTERFACE_NAME=OutriggerServer
if jsse-transient-outrigger==%ARG% set IMPL_NAME=TransientOutriggerImpl

rem *** For computing HTTPMD ***
if browser-httpmd==%ARG% set STOP=compute
if browser-httpmd==%ARG% set START_TYPE=httpmd
if browser-httpmd==%ARG% set SCRIPT_BASE=httpmd
if browser-httpmd==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if browser-httpmd==%ARG% set SERVICE_NAME=browser

if phoenix-httpmd==%ARG% set STOP=compute
if phoenix-httpmd==%ARG% set START_TYPE=httpmd
if phoenix-httpmd==%ARG% set SCRIPT_BASE=httpmd
if phoenix-httpmd==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if phoenix-httpmd==%ARG% set SERVICE_NAME=phoenix

if reggie-httpmd==%ARG% set STOP=compute
if reggie-httpmd==%ARG% set START_TYPE=httpmd
if reggie-httpmd==%ARG% set SCRIPT_BASE=httpmd
if reggie-httpmd==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if reggie-httpmd==%ARG% set SERVICE_NAME=reggie

if mahalo-httpmd==%ARG% set STOP=compute
if mahalo-httpmd==%ARG% set START_TYPE=httpmd
if mahalo-httpmd==%ARG% set SCRIPT_BASE=httpmd
if mahalo-httpmd==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if mahalo-httpmd==%ARG% set SERVICE_NAME=mahalo

if fiddler-httpmd==%ARG% set STOP=compute
if fiddler-httpmd==%ARG% set START_TYPE=httpmd
if fiddler-httpmd==%ARG% set SCRIPT_BASE=httpmd
if fiddler-httpmd==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if fiddler-httpmd==%ARG% set SERVICE_NAME=fiddler

if norm-httpmd==%ARG% set STOP=compute
if norm-httpmd==%ARG% set START_TYPE=httpmd
if norm-httpmd==%ARG% set SCRIPT_BASE=httpmd
if norm-httpmd==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if norm-httpmd==%ARG% set SERVICE_NAME=norm

if mercury-httpmd==%ARG% set STOP=compute
if mercury-httpmd==%ARG% set START_TYPE=httpmd
if mercury-httpmd==%ARG% set SCRIPT_BASE=httpmd
if mercury-httpmd==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if mercury-httpmd==%ARG% set SERVICE_NAME=mercury

if outrigger-httpmd==%ARG% set STOP=compute
if outrigger-httpmd==%ARG% set START_TYPE=httpmd
if outrigger-httpmd==%ARG% set SCRIPT_BASE=httpmd
if outrigger-httpmd==%ARG% set SCRIPT_PATH=%EXAMPLE_HOME%\example\scripts\jsse
if outrigger-httpmd==%ARG% set SERVICE_NAME=outrigger

rem ********************************************************************************************
rem *** if there are no args left, then exit loop
if "%1"=="" goto endloop

rem *** test for all expected double-item args
if -port==%ARG% set PORT=%1
if -dir==%ARG% set DIR_ARG=%1

shift
goto loop
:endloop

if %SCRIPT_BASE%==notset echo %USAGE%
if %SCRIPT_BASE%==notset echo *** a scriptID must be entered, check the argument list
if %SCRIPT_BASE%==notset goto exitScript

if %START_TYPE%==httpd set EXE_ARGS=%JDK_HOME% %JINI_HOME% %PORT% %DIR_ARG%
if %START_TYPE%==browser set EXE_ARGS=%JDK_HOME% %JINI_HOME% %EXAMPLE_HOME% %HOSTNAME% %PORT% %DEBUG%
if %START_TYPE%==phoenix set EXE_ARGS=%JDK_HOME% %JINI_HOME% %EXAMPLE_HOME% %HOSTNAME% %PORT% %DEBUG% %PHOENIX_MESSAGE_DIGEST% %JSK_MESSAGE_DIGEST%
if %START_TYPE%==allservices set EXE_ARGS=%JDK_HOME% %JINI_HOME% %EXAMPLE_HOME% %HOSTNAME% %PORT% %DEBUG%
if %START_TYPE%==oneservice set EXE_ARGS=%JDK_HOME% %JINI_HOME% %EXAMPLE_HOME% %HOSTNAME% %PORT% %SERVICE_NAME% %INTERFACE_NAME% %IMPL_NAME% %DEBUG%
if %START_TYPE%==httpmd set EXE_ARGS=%JDK_HOME% %JINI_HOME% %EXAMPLE_HOME% %HOSTNAME% %PORT% %SERVICE_NAME% %DEBUG%

set SCRIPT=%SCRIPT_PATH%\%STOP%-%SCRIPT_BASE%.bat %EXE_ARGS%
echo.
%SCRIPT%

rem *** END of Script ***
goto endScript

rem *** USAGE ***
:showUsage
echo.
echo %USAGE%
echo.
goto exitScript

set USAGE=usage: %RUN_PROGRAM_NAME% script [-usage] [-help] [-port port] [-dir dir] [-stop]

rem *** HELP ***
:showHelp
echo.
echo %USAGE%
echo.
echo where options include:
echo.
echo scriptID takes one of the following values
echo.
echo httpd -- start the HTTPD daemon
echo.
echo start the Browser under JRMP, Jini ERI, or secure Jini ERI/jsse
echo jrmp-browser
echo jeri-browser
echo jsse-browser
echo.
echo start Phoenix under JRMP, Jini ERI, or secure Jini ERI/jsse
echo jrmp-phoenix
echo jeri-phoenix
echo jsse-phoenix
echo.
echo start ALL services in transient or activatable mode under JRMP
echo jrmp-transient
echo jrmp-activatable
echo.
echo start ALL services in transient, nonactivatable, or activatable mode under Jini ERI
echo jeri-transient
echo jeri-nonactivatable
echo jeri-activatable
echo.
echo start ALL services in transient, nonactivatable, or activatable mode under secure Jini ERI/jsse
echo jsse-transient
echo jsse-nonactivatable
echo jsse-activatable
echo.
echo ******* For Reggie *******
echo.
echo start ONE Reggie in transient or activatable mode under JRMP
echo jrmp-transient-reggie
echo jrmp-activatable-reggie
echo.
echo start ONE Reggie in transient, nonactivatable, or activatable mode under Jini ERI
echo jeri-transient-reggie
echo jeri-nonactivatable-reggie
echo jeri-activatable-reggie
echo.
echo start ONE Reggie in transient, nonactivatable, or activatable mode under secure Jini ERI/jsse
echo jsse-transient-reggie
echo jsse-nonactivatable-reggie
echo jsse-activatable-reggie
echo.
echo ******* For Mahalo *******
echo.
echo start ONE Mahalo in transient or activatable mode under JRMP
echo jrmp-transient-mahalo
echo jrmp-activatable-mahalo
echo.
echo start ONE Mahalo in transient, nonactivatable, or activatable mode under Jini ERI
echo jeri-transient-mahalo
echo jeri-nonactivatable-mahalo
echo jeri-activatable-mahalo
echo.
echo start ONE Mahalo in transient, nonactivatable, or activatable mode under secure Jini ERI/jsse
echo jsse-transient-mahalo
echo jsse-nonactivatable-mahalo
echo jsse-activatable-mahalo
echo.
echo ******* For Fiddler *******
echo.
echo start ONE Fiddler in transient or activatable mode under JRMP
echo jrmp-transient-fiddler
echo jrmp-activatable-fiddler
echo.
echo start ONE Fiddler in transient, nonactivatable, or activatable mode under Jini ERI
echo jeri-transient-fiddler
echo jeri-nonactivatable-fiddler
echo jeri-activatable-fiddler
echo.
echo start ONE Fiddler in transient, nonactivatable, or activatable mode under secure Jini ERI/jsse
echo jsse-transient-fiddler
echo jsse-nonactivatable-fiddler
echo jsse-activatable-fiddler
echo.
echo ******* For Norm *******
echo.
echo start ONE Norm in transient or activatable mode under JRMP
echo jrmp-transient-norm
echo jrmp-activatable-norm
echo.
echo start ONE Norm in transient, nonactivatable, or activatable mode under Jini ERI
echo jeri-transient-norm
echo jeri-nonactivatable-norm
echo jeri-activatable-norm
echo.
echo start ONE Norm in transient, nonactivatable, or activatable mode under secure Jini ERI/jsse
echo jsse-transient-norm
echo jsse-nonactivatable-norm
echo jsse-activatable-norm
echo.
echo ******* For Mercury *******
echo.
echo start ONE Mercury in transient or activatable mode under JRMP
echo jrmp-transient-mercury
echo jrmp-activatable-mercury
echo.
echo start ONE Mercury in transient, nonactivatable, or activatable mode under Jini ERI
echo jeri-transient-mercury
echo jeri-nonactivatable-mercury
echo jeri-activatable-mercury
echo.
echo start ONE Mercury in transient, nonactivatable, or activatable mode under secure Jini ERI/jsse
echo jsse-transient-mercury
echo jsse-nonactivatable-mercury
echo jsse-activatable-mercury
echo.
echo ******* For Outrigger *******
echo.
echo start ONE Outrigger in transient or activatable mode under JRMP
echo jrmp-transient-outrigger
echo jrmp-activatable-outrigger
echo.
echo start ONE Outrigger in transient, nonactivatable, or activatable mode under Jini ERI
echo jeri-transient-outrigger
echo jeri-nonactivatable-outrigger
echo jeri-activatable-outrigger
echo.
echo start ONE Outrigger in transient, nonactivatable, or activatable mode under secure Jini ERI/jsse
echo jsse-transient-outrigger
echo jsse-nonactivatable-outrigger
echo jsse-activatable-outrigger
echo.
echo browser-httpmd   -- compute the httpmd codebase for the Browser
echo phoenix-httpmd   -- compute the httpmd codebase for Phoenix
echo reggie-httpmd    -- compute the httpmd codebase for Reggie
echo mahalo-httpmd    -- compute the httpmd codebase for Mahalo
echo fiddler-httpmd   -- compute the httpmd codebase for Fiddler
echo norm-httpmd      -- compute the httpmd codebase for Norm
echo mercury-httpmd   -- compute the httpmd codebase for Mercury
echo outrigger-httpmd -- compute the httpmd codebase for Outrigger
echo.
echo ******* Additional Arguments *******
echo.
echo  -port   for httpd        - the port to listen on
echo          for the Browser  - the port to use in the codebase
echo          for Phoenix      - the port to use in the codebase
echo          for the services - the port to use in the codebase
echo                             (Default = %PORT%).
echo  -dir    for httpd        - the base directory from which to serve jar files
echo                             (Default = %JINI_HOME%\lib).
echo  -stop   for httpd        - stops the server (must also supply the -port option
echo                             if the server is listening to a port other than
echo                             the default)
echo          for Phoenix      - stops the server and deletes its persisted state
echo          for the services - destroys the shared VM in which the activatable
echo                             versions of the services are running
echo  -debug  for the Browser  - enables security debugging in the Browser's VM
echo          for Phoenix      - enables security debugging in the Phoenix VM
echo          for the services - enables security debugging in the VM in which the
echo                             service runs and in the ServiceStarter setup VM
echo                             (which may or may not be the same VM)
echo.
goto exitScript

:endScript
goto exitScript

:exitScript
