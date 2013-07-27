export CLASSPATH=./src/:./lib/jsch-0.1.50.jar
javac -d ./classes/ -cp ./src/:./lib/jsch-0.1.50.jar ./src/dcc/ufmg/anthill/*.java ./src/dcc/ufmg/anthill/info/*.java ./src/dcc/ufmg/anthill/net/*.java ./src/dcc/ufmg/anthill/stream/*.java ./src/dcc/ufmg/anthill/scheduler/*.java ./src/dcc/ufmg/anthill/util/*.java 
jar cvf anthill.jar -C classes/ .
javadoc -d ./doc/ ./src/dcc/ufmg/anthill/*.java ./src/dcc/ufmg/anthill/info/*.java ./src/dcc/ufmg/anthill/net/*.java ./src/dcc/ufmg/anthill/stream/*.java ./src/dcc/ufmg/anthill/scheduler/*.java ./src/dcc/ufmg/anthill/util/*.java
rm *~ ./src/dcc/ufmg/anthill/*~ ./src/dcc/ufmg/anthill/info/*~ ./src/dcc/ufmg/anthill/net/*~ ./src/dcc/ufmg/anthill/stream/*~ ./src/dcc/ufmg/anthill/scheduler/*~ ./src/dcc/ufmg/anthill/util/*~ 
