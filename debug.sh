export MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=n"
rm -rf $HUDSON_HOME/plugins/pmd*
mvn clean hpi:run
