rebuild:
	mvn package
	cp target/berry-pim-1.0-SNAPSHOT.war ~/software/jetty-distribution-9.3.6.v20151106/berry-base/webapps/berrypim.war
	(cd ~/software/jetty-distribution-9.3.6.v20151106/berry-base && java -jar ../start.jar)
