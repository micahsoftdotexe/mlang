# run using pytest
import subprocess
import json
from os import listdir
from os.path import isfile, join
import glob
import time


def run(command):
    process = subprocess.run(f"./mylox", shell=True, stdout=subprocess.PIPE, stdin=subprocess.PIPE, stderr=subprocess.PIPE)
    stdout, error = process.communicate(input=bytes(command, "utf-8"))
    process.kill()
    return [stdout, error]


def load_test_answer(file_path, module_name):
    data = json.load(open(file_path))[module_name]
    return data


def run_make(command, file_path=None):
    process = subprocess.run(f"make {command} FILE={file_path}", shell=True)
    #stdout, error = process.communicate(input=bytes(command, "utf-8"))
    #process.kill()
    #return [stdout, error]




# def load_questions(file_path):
#     file = open(file_path)
#     return [line.rstrip('\n') for line in file.readlines()]


def run_file(file_path):
    #process = subprocess.Popen(f"make build", shell=True)
    process = subprocess.Popen(f"java -cp ./lib/groovy-4.0.1.jar:. com.mlang.Mlang {file_path}", shell=True, stdout=subprocess.PIPE, stdin=subprocess.PIPE, stderr=subprocess.PIPE)
    stdout, error = process.communicate()
    process.kill()
    return [stdout, error]

def test_block():
    out = run_file("test/empty.mlang")
    answers = load_test_answer("test/answers.json", "empty")

    assert out[0] == bytes(answers["stdout"], "utf-8")
    assert out[1] == bytes(answers["stderr"], "utf-8")

def test_block_scope():
    out = run_file("test/block_scope.mlang")
    answers = load_test_answer("test/answers.json", "block_scope")

    assert out[0] == bytes(answers["stdout"], "utf-8")
    assert out[1] == bytes(answers["stderr"], "utf-8")

def test_assignment_grouping():
    out = run_file("test/assignment_grouping.mlang")
    answers = load_test_answer("test/answers.json", "assignment_grouping")

    assert out[0] == bytes(answers["stdout"], "utf-8")
    assert out[1] == bytes(answers["stderr"], "utf-8")

def test_nil_1():
    out = run_file("test/nil_1.mlang")
    answers = load_test_answer("test/answers.json", "nil_1")

    assert out[0] == bytes(answers["stdout"], "utf-8")
    assert out[1] == bytes(answers["stderr"], "utf-8")

def test_nil_2():
    out = run_file("test/nil_2.mlang")
    answers = load_test_answer("test/answers.json", "nil_2")

    assert out[0] == bytes(answers["stdout"], "utf-8")
    assert out[1] == bytes(answers["stderr"], "utf-8")

def test_type_checking_1():
    out = run_file("test/type_checking_1.mlang")
    answers = load_test_answer("test/answers.json", "type_checking_1")

    assert out[0] == bytes(answers["stdout"], "utf-8")
    assert out[1] == bytes(answers["stderr"], "utf-8")

def test_type_checking_2():
    out = run_file("test/type_checking_2.mlang")
    answers = load_test_answer("test/answers.json", "type_checking_2")

    assert out[0] == bytes(answers["stdout"], "utf-8")
    assert out[1] == bytes(answers["stderr"], "utf-8")

def test_return_stmt():
    out = run_file("test/return.mlang")
    answers = load_test_answer("test/answers.json", "return")

    assert out[0] == bytes(answers["stdout"], "utf-8")
    assert out[1] == bytes(answers["stderr"], "utf-8")

def test_function_argument_1():
    out = run_file("test/function_argument_checking_1.mlang")
    answers = load_test_answer("test/answers.json", "function_argument_checking_1")

    assert out[0] == bytes(answers["stdout"], "utf-8")
    assert out[1] == bytes(answers["stderr"], "utf-8")

def test_function_argument_2():
    out = run_file("test/function_argument_checking_2.mlang")
    answers = load_test_answer("test/answers.json", "function_argument_checking_2")

    assert out[0] == bytes(answers["stdout"], "utf-8")
    assert out[1] == bytes(answers["stderr"], "utf-8")

def test_or_and_eval():
    out = run_file("test/or_and_eval.mlang")
    answers = load_test_answer("test/answers.json", "or_and_eval")

    assert out[0] == bytes(answers["stdout"], "utf-8")
    assert out[1] == bytes(answers["stderr"], "utf-8")

def test_bool_equality():
    out = run_file("test/bool_equality.mlang")
    answers = load_test_answer("test/answers.json", "bool_equality")

    assert out[0] == bytes(answers["stdout"], "utf-8")
    assert out[1] == bytes(answers["stderr"], "utf-8")

def test_not():
    out = run_file("test/not.mlang")
    answers = load_test_answer("test/answers.json", "not")

    assert out[0] == bytes(answers["stdout"], "utf-8")
    assert out[1] == bytes(answers["stderr"], "utf-8")

def test_if():
    out = run_file("test/if.mlang")
    answers = load_test_answer("test/answers.json", "if")

    assert out[0] == bytes(answers["stdout"], "utf-8")
    assert out[1] == bytes(answers["stderr"], "utf-8")

def test_for():
    out = run_file("test/for.mlang")
    answers = load_test_answer("test/answers.json", "for")

    assert out[0] == bytes(answers["stdout"], "utf-8")
    assert out[1] == bytes(answers["stderr"], "utf-8")

def test_in():
    for i in range(0, 10):
        process = subprocess.Popen(f"java -cp ./lib/groovy-4.0.1.jar:. com.mlang.Mlang test/in.mlang", shell=True, stdout=subprocess.PIPE, stdin=subprocess.PIPE, stderr=subprocess.PIPE)
        stdout, error = process.communicate(input=bytes(f"{i}\n", "utf-8"))
        process.kill()
        answers = load_test_answer("test/answers.json", "in")

        assert stdout == bytes(answers["stdout"][i], "utf-8")
        assert error == bytes(answers["stderr"][i], "utf-8")

def test_argument():
    process = subprocess.Popen(f"java -cp ./lib/groovy-4.0.1.jar:. com.mlang.Mlang test/argument.mlang hello", shell=True, stdout=subprocess.PIPE, stdin=subprocess.PIPE, stderr=subprocess.PIPE)
    stdout, error = process.communicate()
    process.kill()
    answers = load_test_answer("test/answers.json", "argument")

    assert stdout == bytes(answers["stdout"][0], "utf-8")
    assert error == bytes(answers["stderr"][0], "utf-8")

    process = subprocess.Popen(f"java -cp ./lib/groovy-4.0.1.jar:. com.mlang.Mlang test/argument.mlang", shell=True, stdout=subprocess.PIPE, stdin=subprocess.PIPE, stderr=subprocess.PIPE)
    stdout, error = process.communicate()
    process.kill()
    answers = load_test_answer("test/answers.json", "argument")

    assert stdout == bytes(answers["stdout"][1], "utf-8")
    assert error == bytes(answers["stderr"][1], "utf-8")

def test_multiple_scrnout():
    out = run_file("test/multiple_scrnout.mlang")
    answers = load_test_answer("test/answers.json", "multiple_scrnout")

    assert out[0] == bytes(answers["stdout"], "utf-8")
    assert out[1] == bytes(answers["stderr"], "utf-8")