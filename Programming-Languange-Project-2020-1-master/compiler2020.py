#!/usr/bin/env python
from lexical import scanner
from parse import parser
from semantic import semantic
from code_generator import code_generator
import sys

asdf = sys.argv
chk = False if len(asdf)==1 else True

if __name__ == "__main__":
    with open("testfiles/testfile_1.txt", 'r') as test:
        code = test.read()

    scan = scanner(code)
    scan.lexical()

    tokens = scan.tokens
    if chk:
        print("Tokens")
        for token in tokens:
            print(token)
        print()

    parsing = parser(tokens, "grammar2.txt")

    if chk:
        print("LL Grammar")
        for i in parsing.grammar:
            for j in parsing.grammar[i]:
                if j[0]=='':
                    print(i, '->', "''")
                else:
                    print(i,'->',' '.join(j))
    parsing.get_FIRST()
    parsing.get_FOLLOW()
    if chk:
        print("\nFIRST")
        for i in parsing.first:
            print(i, parsing.first[i])

    if chk:
        print("\nFOLLOW")
        for i in parsing.follow:
            print(i, parsing.follow[i])

    parsing.get_Table()
    if chk:
        print("\n terminals")
        print(parsing.terminal)
        print(parsing.non_terminal)
    if chk:
        print("\n terminals")
        print("\n non terminals")

        print("\nTable")
        print(parsing.non_terminal + ['$'])
        for i in range(len(parsing.table)):
            print(parsing.terminal[i], parsing.table[i])
    if chk:
        print()
    input_list =parsing.tokens_to_input(tokens)
    asdf = parsing.parsing(input_list)
    if chk:
        if asdf:
            parsing.parse_tree.node_print()
        else:
            print("input not accecpted")

        print("\n")
    parsing.get_symbol_table()
    if chk:
        print("symbol Table\nsymbol, type, scope")
        for s in parsing.symbol_table:
            print(s)

    asdf = semantic(parsing)
    asdf.type_check()
    ir = asdf.ir()
    if chk:
        print("\nIntermediate Representation")
        for i,r in enumerate(ir):
            print(str(i) + ". " + r[0])


    qwert = code_generator(ir)

    code = qwert.generate()
    if chk:
        print("\ncode")
        for c in code:
            print(c)
        print("Number of Used Register (except Rax) :", qwert.count)

    with open("compiler2020.py.symbol","w") as sb:
        for s in parsing.symbol_table:
            sb.write("name : "+s[0])
            sb.write("\n{\ttype : "+s[1])
            sb.write("\n\tscope : "+s[2][0])
            for s2 in s[2][1:]:
                sb.write(" -> "+s2)
            sb.write(" }\n\n")
    with open("compiler2020.py.code","w") as cd:
        for c in code:
            cd.write(c+"\n")