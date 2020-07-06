#!/usr/bin/env python
from lexical import scanner
from parse import parser
from semantic import semantic

class code_generator():
    def __init__(self,IR):
        self.IR = IR
        self.operator = ["=", "+","*",">"]
        self.count = 1

    def generate(self):
        self.code = []
        for ir in self.IR:
            if ir[1]:
                self.num = 1
                bt = self.get_bt(ir[1])
                bt.id = 1
                self.numbering(bt)
                self.write_code(ir[0],bt)
            else:
                if ir[1] == None:
                    if ir[0][:4] == "goto":
                        self.code.append("  JUMP         " + ir[0][5:])
                    else:
                        self.code.append(ir[0])
        return self.code

    def write_code(self,ir,node):
        nid = "REG#" + str(node.id)

        if node.children:
            left = node.children[0]
            right = node.children[1]
            lid = "REG#" + str(left.id)
            rid = "REG#" + str(right.id)
            if not left.children:
                if node.data != "=":
                    self.write_code("",left)
                self.write_code("",right)
                if node.data == "+":
                    self.code.append("  ADD   " + (nid + ",").ljust(7) + (lid + ",").ljust(7) + rid)
                elif node.data == "*":
                    self.code.append("  MUL   " + (nid + ",").ljust(7) + (lid + ",").ljust(7) + rid)
                elif node.data == "=":
                    self.code.append("  ST    " + (rid + ",").ljust(7) + left.data)

                else:  # >
                    self.code.append("  LT    " + (nid + ",").ljust(7) + (rid + ",").ljust(7) + lid)
                    self.code.append("  JUMPT " + (nid + ",").ljust(7) + ir.split("goto")[-1][1:])
            elif not right.children:
                self.write_code("",left)
                if node.data == "+":
                    self.code.append("  ADD   " + (nid + ",").ljust(7) + (lid + ",").ljust(7) + rid)
                elif node.data == "*":
                    self.code.append("  MUL   " + (nid + ",").ljust(7) + (lid + ",").ljust(7) + rid)
                elif node.data == "=":
                    self.code.append("  ST    " + (rid + ",").ljust(7) + left.data)

                else:  # >
                    self.code.append("  LT    " + (nid + ",").ljust(7) + (rid + ",").ljust(7) + lid)
                    self.code.append("  JUMPT " + (nid + ",").ljust(7) + ir.split("goto")[-1][1:])

            else:
                self.write_code("",left)
                self.write_code("",right)
                if node.data == "+":
                    self.code.append("  ADD   " + (nid + ",").ljust(7) + (lid + ",").ljust(7) + rid)
                elif node.data == "*":
                    self.code.append("  MUL   " + (nid + ",").ljust(7) + (lid + ",").ljust(7) + rid)
                elif node.data == "=":
                    self.code.append("  ST    " + (rid + ",").ljust(7) + left.data)

                else:  # >
                    self.code.append("  LT    " + (nid + ",").ljust(7) + (rid + ",").ljust(7) + lid)
                    self.code.append("  JUMPT " + (nid + ",").ljust(7) + ir.split("goto")[-1][1:])
        elif ir[:6] == "RETURN":
            self.code.append("  LD    " + ("rax,").ljust(7) + "0")
            self.code.append("  ADD   " + ("rax,").ljust(7) + ("rax,").ljust(7) + nid)
        elif str(node.id).isdigit():
            self.code.append("  LD    " + ("REG#"+str(node.id)+",").ljust(7)+node.data)






    def get_bt(self,node):
        return node.get_binarySyntaxTree()

    def numbering(self,node):
        if node.children:
            left = node.children[0]
            right = node.children[1]
            if node.data == "=":
                right.id = node.id
                self.numbering(right)
            elif not left.children:
                left.id = node.id + 1
                right.id = node.id
                self.count = max(self.count, left.id)
                self.numbering(right)
            elif not right.children:
                left.id = node.id
                right.id = node.id+1
                self.count = max(self.count, right.id)
                self.numbering(left)
            else:
                left.id = node.id
                right.id = node.id + 1
                self.count = max(self.count, right.id)
                self.numbering(left)
                self.numbering(right)






if __name__ == "__main__":
    with open("testfiles/testfile_1.txt", 'r') as test:
        code = test.read()

    scan = scanner(code)
    scan.lexical()
    tokens = scan.tokens
    for token in tokens:
        print(token)
    print()

    parsing = parser(tokens, "grammar2.txt")

    print("LL Grammar")
    for i in parsing.grammar:
        for j in parsing.grammar[i]:
            if j[0]=='':
                print(i, '->', "''")
            else:
                print(i,'->',' '.join(j))
    parsing.get_FIRST()
    parsing.get_FOLLOW()

    print("\nFIRST")
    for i in parsing.first:
        print(i, parsing.first[i])

    print("\nFOLLOW")
    for i in parsing.follow:
        print(i, parsing.follow[i])

    parsing.get_Table()
    print("\n terminals")
    print(parsing.terminal)
    print("\n non terminals")
    print(parsing.non_terminal)

    print("\nTable")
    print(parsing.non_terminal + ['$'])
    for i in range(len(parsing.table)):
        print(parsing.terminal[i], parsing.table[i])

    print()
    input_list =parsing.tokens_to_input(tokens)
    asdf = parsing.parsing(input_list)
    if asdf:
        asdf.parse_tree.node_print()
    else:
        print("input not accecpted")

    print("\n")
    parsing.get_symbol_table()
    print("symbol Table\nsymbol, type")
    for s in parsing.symbol_table:
        print(s)

    asdf = semantic(parsing)
    asdf.type_check()
    ir = asdf.ir()
    print("\nIntermediate Representation")
    for i,r in enumerate(ir):
        print(str(i) + ". " + r[0])


    qwert = code_generator(ir)

    code = qwert.generate()
    print("\ncode")
    for c in code:
        print(c)