javac -d ./test/classes/ -cp ./test/src/:./../anthill.jar:./../lib/jsch-0.1.50.jar ./test/src/*.java 
jar cvf ./test/test.jar -C ./test/classes/ .
javac -d ./fileRW/classes/ -cp ./fileRW/src/:./../anthill.jar:./../lib/jsch-0.1.50.jar ./fileRW/src/*.java 
jar cvf ./fileRW/fileRW.jar -C ./fileRW/classes/ .
