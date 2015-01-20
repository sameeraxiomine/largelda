set action=%1
set MAINDIR=..
set CLASSPATH=%MAINDIR%/config/;%MAINDIR%/mylib/*
java -classpath %CLASSPATH% -Xmx2G  org.bigtextml.client.ParallelTopicModelClient %action%



