javac -d ./test/classes/ -cp ./test/src/:./../anthill.jar:./../lib/jsch-0.1.50.jar ./test/src/*.java 
jar cvf ./test/test.jar -C ./test/classes/ .
rm ./test/src/*~ ./test/*~

javac -d ./fileRW/classes/ -cp ./fileRW/src/:./../anthill.jar:./../lib/jsch-0.1.50.jar ./fileRW/src/*.java 
jar cvf ./fileRW/fileRW.jar -C ./fileRW/classes/ .
rm ./fileRW/src/*~ ./fileRW/*~

javac -d ./hdfsFileRW/classes/ -cp ./hdfsFileRW/src/:./../anthill.jar:./../lib/jsch-0.1.50.jar ./hdfsFileRW/src/*.java 
jar cvf ./hdfsFileRW/hdfsFileRW.jar -C ./hdfsFileRW/classes/ .
rm ./hdfsFileRW/src/*~ ./hdfsFileRW/*~

javac -d ./hdfsKeyValueWrite/classes/ -cp ./hdfsKeyValueWrite/src/:./../anthill.jar:./../lib/jsch-0.1.50.jar ./hdfsKeyValueWrite/src/*.java 
jar cvf ./hdfsKeyValueWrite/hdfsKeyValueWrite.jar -C ./hdfsKeyValueWrite/classes/ .
rm ./hdfsKeyValueWrite/src/*~ ./hdfsKeyValueWrite/*~

javac -d ./hdfsKeyValueRW/classes/ -cp ./hdfsKeyValueRW/src/:./../anthill.jar:./../lib/jsch-0.1.50.jar ./hdfsKeyValueRW/src/*.java 
jar cvf ./hdfsKeyValueRW/hdfsKeyValueRW.jar -C ./hdfsKeyValueRW/classes/ .
rm ./hdfsKeyValueRW/src/*~ ./hdfsKeyValueRW/*~

javac -d ./wordCounter/classes/ -cp ./wordCounter/src/:./../anthill.jar:./../lib/jsch-0.1.50.jar ./wordCounter/src/*.java 
jar cvf ./wordCounter/wordCounter.jar -C ./wordCounter/classes/ .
rm ./wordCounter/src/*~ ./wordCounter/*~

rm *~
