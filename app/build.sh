javac -server -d ./test/classes/ -cp ./test/src/:./../anthill.jar:./../lib/jsch-0.1.50.jar ./test/src/*.java 
jar cvf ./test/test.jar -C ./test/classes/ .
rm ./test/src/*~ ./test/*~

javac -server -d ./fileRW/classes/ -cp ./fileRW/src/:./../anthill.jar:./../lib/jsch-0.1.50.jar ./fileRW/src/*.java 
jar cvf ./fileRW/fileRW.jar -C ./fileRW/classes/ .
rm ./fileRW/src/*~ ./fileRW/*~

javac -server -d ./hdfsFileRW/classes/ -cp ./hdfsFileRW/src/:./../anthill.jar:./../lib/jsch-0.1.50.jar ./hdfsFileRW/src/*.java 
jar cvf ./hdfsFileRW/hdfsFileRW.jar -C ./hdfsFileRW/classes/ .
rm ./hdfsFileRW/src/*~ ./hdfsFileRW/*~

javac -server -d ./hdfsKeyValueWrite/classes/ -cp ./hdfsKeyValueWrite/src/:./../anthill.jar:./../lib/jsch-0.1.50.jar ./hdfsKeyValueWrite/src/*.java 
jar cvf ./hdfsKeyValueWrite/hdfsKeyValueWrite.jar -C ./hdfsKeyValueWrite/classes/ .
rm ./hdfsKeyValueWrite/src/*~ ./hdfsKeyValueWrite/*~

javac -server -d ./hdfsKeyValueRW/classes/ -cp ./hdfsKeyValueRW/src/:./../anthill.jar:./../lib/jsch-0.1.50.jar ./hdfsKeyValueRW/src/*.java 
jar cvf ./hdfsKeyValueRW/hdfsKeyValueRW.jar -C ./hdfsKeyValueRW/classes/ .
rm ./hdfsKeyValueRW/src/*~ ./hdfsKeyValueRW/*~

javac -server -d ./wordCounter/classes/ -cp ./wordCounter/src/:./../anthill.jar:./../lib/jsch-0.1.50.jar ./wordCounter/src/*.java 
jar cvf ./wordCounter/wordCounter.jar -C ./wordCounter/classes/ .
rm ./wordCounter/src/*~ ./wordCounter/*~

javac -server -d ./netBCRW/classes/ -cp ./netBCRW/src/:./../anthill.jar:./../lib/jsch-0.1.50.jar ./netBCRW/src/*.java 
jar cvf ./netBCRW/netBCRW.jar -C ./netBCRW/classes/ .
rm ./netBCRW/src/*~ ./netBCRW/*~

javac -server -d ./netKVRW/classes/ -cp ./netKVRW/src/:./../anthill.jar:./../lib/jsch-0.1.50.jar ./netKVRW/src/*.java 
jar cvf ./netKVRW/netKVRW.jar -C ./netKVRW/classes/ .
rm ./netKVRW/src/*~ ./netKVRW/*~

javac -server -d ./netWordCounter/classes/ -cp ./netWordCounter/src/:./../anthill.jar:./../lib/jsch-0.1.50.jar ./netWordCounter/src/*.java 
jar cvf ./netWordCounter/netWordCounter.jar -C ./netWordCounter/classes/ .
rm ./netWordCounter/src/*~ ./netWordCounter/*~

javac -server -d ./fastWordCounter/classes/ -cp ./fastWordCounter/src/:./../anthill.jar:./../lib/jsch-0.1.50.jar ./fastWordCounter/src/*.java 
jar cvf ./fastWordCounter/fastWordCounter.jar -C ./fastWordCounter/classes/ .
rm ./fastWordCounter/src/*~ ./fastWordCounter/*~

rm *~

