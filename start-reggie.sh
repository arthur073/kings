JAVA_HOME=/etc/java-config-2/current-system-vm
JINI_HOME=/home/12/k1218492/blitz/jini2_1/lib/
START_CONFIG=config/start-reggie.config

$JAVA_HOME/bin/java -Djava.security.policy=policy/policy.all -jar $JINI_HOME/start.jar $START_CONFIG
