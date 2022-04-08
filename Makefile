GROOVYJAR = ./lib/groovy-4.0.1.jar
ENTRYPOINT = com.mlang.Mlang
run: build
	@java -cp $(GROOVYJAR):. $(ENTRYPOINT)
build:
	@groovyc ./com/mlang/*.groovy
clean: 
	@rm ./com/mlang/*.class
