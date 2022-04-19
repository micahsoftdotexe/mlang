GROOVYJAR = ./lib/groovy-4.0.1.jar
ENTRYPOINT = com.mlang.Mlang
run:
	@java -cp $(GROOVYJAR):. $(ENTRYPOINT) $(FILE)
runfull: build
	@java -cp $(GROOVYJAR):. $(ENTRYPOINT) $(FILE)
build:
	@groovyc ./com/mlang/*.groovy
clean: 
	@rm ./com/mlang/*.class
