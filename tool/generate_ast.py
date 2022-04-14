from sys import argv, stderr
import os

def __parse_template():
    parsed_lines = {
        "imports": [],
        "visitor_interface": [],
        "subclass": []
    }
    #module dir
    with open(f"{os.path.dirname(__file__)}/template.groovyt", 'r', encoding="UTF-8") as file:
        lines = file.readlines()
        for index, line in enumerate(lines):
            if line.startswith("class <TemplateName>Visitor(ABC):"):
                parsed_lines["visitor_class"] = [line]
                parsed_lines["visitor_method"] = lines[index+1:index+4]
            elif line.startswith("from"):
                parsed_lines["imports"].append(line)
            elif line.startswith("class <TemplateName>(ABC):"):
                parsed_lines["main_class"] = lines[index:index+4]
            elif line.startswith("class <SubclassName>(<TemplateName>):"):
                parsed_lines["subclass"] = lines[index:index+5]
    return parsed_lines

def __fix_visitor_main_class(line_list, base_name):
    new_line = "\n"
    for line in line_list:
        new_line += line
    new_line = new_line.replace("<TemplateName>", base_name)
    return new_line

def __fix_visitor_method(line_list, base_name, types):
    template_line = ""
    result_line = ""
    for line in line_list:
        template_line += line
    for name in types:
        result_line += template_line
        result_line = result_line.replace("<subclass_name>", name.lower())
        result_line = result_line.replace("<template_name>", base_name.lower())
        result_line +='\n'
    return result_line[:-1]

def __fix_subclass(line_list, base_name, types):
    return_string = "\n"
    class_header = []
    init = []
    init_self = []
    accept = []
    for index, line in enumerate(line_list):
        if line.startswith("class <SubclassName>(<TemplateName>):"):
            class_header.append(line)
        elif line.startswith("    def __init__(self, <field>*):"):
            init.append(line)
            init_self.append(line_list[index+1])
        elif line.startswith("    def accept(self, visitor):"):
            accept.append(line)
            accept.append(line_list[index+1])
    for name, fields in types.items():
        return_string += class_header[0].replace("<SubclassName>", name)\
            .replace("<TemplateName>", base_name)
        return_string += init[0].replace("<field>*", ", ".join(fields))
        for field in fields:
            return_string += init_self[0].replace("<field>", field)
        return_string += accept[0]
        return_string += accept[1].replace("<subclass_name>", name.lower())\
            .replace("<template_name>", base_name.lower())
        return_string += "\n\n"
    return return_string[:-1]
        

def __define_ast(directory, base_name, full_name, types):
    #create path
    new_file = ""
    path = f"{directory}/{full_name}.py"
    template_dictionary = __parse_template()
    #create file
    for line in template_dictionary["imports"]:
        new_file += line
    new_file += __fix_visitor_main_class(template_dictionary["visitor_class"], base_name)
    new_file += __fix_visitor_method(template_dictionary["visitor_method"], base_name, types)
    new_file += __fix_visitor_main_class(template_dictionary["main_class"], base_name)
    new_file += __fix_subclass(template_dictionary["subclass"], base_name, types)
    #create/open file
    with open(path, 'w', encoding="UTF-8") as file:
        #write imports
        file.write(new_file)
        file.close()

if __name__ == "__main__":
    if len(argv) != 2:
        print("Usage: generate_ast <output_file>", file=stderr)
        exit(64)
    output_directory = argv[1]
    __define_ast(output_directory, "Expr", "expressions",  {
        "Assign": ["name", "value", "operator"],
        "Binary": ["left", "operator", "right"],
        "Grouping": ["expression"],
        "Literal": ["value"],
        "Logical": ["left", "operator", "right"],
        "Unary": ["operator", "right"],
        "Call": ["callee", "paren", "arguments"],
        "Get": ["object", "name"],
        "Set": ["object", "name", "value"],
        "Super": ["keyword", "method"],
        "This": ["keyword"],
        "Variable": ["name"],
    })
    __define_ast(output_directory, "Statement", "statements",  {
        "Block": ["statements"],
        "Class": ["name", "superclass", "methods"],
        "Expression": ["expression"],
        "Function": ["name", "params", "body"],
        "If": ["condition", "then_branch", "else_branch"],
        "Print": ["expression"],
        "Return": ["keyword", "value"],
        "Var": ["name", "initializer"],
        "While": ["condition", "body"],
    })