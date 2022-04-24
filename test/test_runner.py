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