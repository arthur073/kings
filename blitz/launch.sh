#!/bin/sh

echo -n "[Terminal #0] Compiling example ..."
cd examples/helloworld
javac -classpath ../../jini2_1/lib/jsk-lib.jar:../../jini2_1/lib/jsk-platform.jar:../../jini2_1/source/src *.java -Xlint:-unchecked
cd ../..
echo "OK"

echo -n "[Terminal #1] Launching Services..."
gnome-terminal -x sh blitz.sh &
echo "OK"

echo -n "[Terminal #0] Waiting for services to launch..."
sleep 5
echo "OK"

echo -n "[Terminal #2] Launching Taker..."
gnome-terminal -x java -Djava.security.policy=config/policy.all -classpath jini2_1/lib/jsk-lib.jar:jini2_1/lib/jsk-platform.jar:examples/:jini2_1/source/lib/reggie.jar helloworld.Taker 
echo "OK"

echo -n "[Terminal #3] Launching Writer..."
gnome-terminal -x java -Djava.security.policy=config/policy.all -classpath jini2_1/lib/jsk-lib.jar:jini2_1/lib/jsk-platform.jar:examples/:jini2_1/source/lib/reggie.jar helloworld.Writer
echo "OK"

echo -n "[Terminal #0] Running Dashboard..."
sh dashboard.sh 


