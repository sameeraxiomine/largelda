export action=$1
export MAINDIR=..
export CLASSPATH=%MAINDIR%/config/:%MAINDIR%/lib/*

java -classpath %CLASSPATH% -Xmx2G  org.bigtextml.client.ParallelTopicModelClient %action%



