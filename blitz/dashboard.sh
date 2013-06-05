SPACE_NAME=Blitz_JavaSpace

JAVA_HOME=/etc/java-config-2/current-system-vm
JINI_LIB=/home/12/k1218492/TS/blitz/jini2_1/lib/

CP=$JINI_LIB/jsk-lib.jar:lib/dashboard.jar:lib/stats.jar:thirdpartylib/backport-util-concurrent50.jar:$JINI_LIB/jsk-platform.jar:$JINI_LIB/sun-util.jar
POLICY=-Djava.security.policy=policy/policy.all

$JAVA_HOME/bin/java $POLICY -cp $CP org.dancres.blitz.tools.dash.StartDashBoard $SPACE_NAME
