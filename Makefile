GROOVYJAR = ./lib/groovy-4.0.1.jar
ENTRYPOINT = com.mlang.Mlang
run:
	@java -cp $(GROOVYJAR):. $(ENTRYPOINT) $(ARGS)
runfull: build
	@java -cp $(GROOVYJAR):. $(ENTRYPOINT) $(ARGS)
build:
	@groovyc ./com/mlang/*.groovy
clean: 
	@rm ./com/mlang/*.class
tests: build
	@pytest -v test/test_runner.py
	@make clean
