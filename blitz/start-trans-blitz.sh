JAVA_HOME=/etc/java-config-2/current-system-vm
JINI_HOME=/home/12/k1218492/TS/blitz/jini2_1/lib/
START_CONFIG=config/start-trans-blitz.config

$JAVA_HOME/bin/java -Djava.security.policy=policy/policy.all -jar $JINI_HOME/start.jar $START_CONFIG
