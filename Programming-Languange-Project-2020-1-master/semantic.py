#!/usr/bin/env python
from lexical import scanner
from parse import parser
import sys
class semantic():
    def __init__(self,ps):
        self.ps = ps
        self.g=1
        self.fname = ps.parse_tree.children[0].children[0].id

    def type_check(self):
        symbol = [s[0] for s in self.ps.symbol_table]
        types = [s[1] for s in self.ps.symbol_table]
        if len(symbol) != len(set(symbol)):
            print("\nsame variable declared more than twice")
            exit()

        st2 = {s[0]: s[1] for s in self.ps.symbol_table}
        st = dict()
        node = self.ps.parse_tree
        while len(node.children) != 0:
            node = node.children[0]
        node = node.get_next()
        ck = True
        while node.parent != None:
            if node.data in ["int","char"]:
                while node.get_next().data != ";":
                    if node.data == "[a-zA-Z]*":
                        st[node.id] = st2[node.id]

                    node = node.get_next()
            if node.data == "[a-zA-Z]*":
                if ck:
                    try :
                        cur_type = st[node.id]
                    except KeyError :
                        print("\n",node.id,"is not decalred variable")
                        sys.exit()
                    ck = False
                elif cur_type != st[node.id]:
                    print("Type error occured")
                    return 0
            elif node.data in [";", "THEN", "{"]:
                ck = True
            node = node.get_next()
    def ir(self):
        ir = [["BEGIN " + self.fname,None]]
        ir += self.make_IR(self.ps.parse_tree)
        ir += [["END " + self.fname,None]]
        return ir
    def make_IR(self,node):
        self.cur = node
        ir = []
        end = node
        while end.children:
            end = end.children[-1]

        while self.cur != end:
            if self.cur.data == "stat":
                n = self.cur.children[0]
                if n.data in ["word", "RETURN"] :
                    ir.append([self.word_ret(n),self.cur])
                if n.data == "WHILE" :
                    if self.cur.children[2].children:
                        ir += self.wh()
                if n.data == "IF" :
                    ir += self.if_ir()
            self.cur = self.cur.search_inorder()

        return ir
    def if_ir(self):
        ir = []
        l1 = str(self.g)
        l2 = str(self.g + 1)

        cond = self.cur.children[1]
        then_b = self.cur.children[3]
        else_b = self.cur.children[5]

        then_ir = self.make_IR(then_b)
        else_ir = self.make_IR(else_b)
        c = cond
        cond_txt = ""
        while c.children:
            c = c.children[0]
        while c.data != "{":
            if c.id:
                cond_txt += " " + c.id
            elif c.data:
                cond_txt += " " + c.data
            c = c.get_next()
        ir.append(["if" + cond_txt + " goto L" + l1, cond])
        ir = ir + else_ir
        ir.append(["goto L" + l2, None])
        ir.append(["L" + l1, None])
        ir = ir + then_ir
        ir.append(["L"+l2,None])

        return ir

    def wh(self):
        ir = []
        l1 = str(self.g)
        l2 = str(self.g+1)
        ir.append(["goto L"+l2,None])
        ir.append(["L"+l1,None])

        self.g += 2
        cond = self.cur.children[1]
        block = self.cur.children[2]

        ir = ir + self.make_IR(block)

        c = cond
        cond_txt = ""
        while c.children:
            c = c.children[0]
        while c.data != "{":
            if c.id:
                cond_txt += " " + c.id
            elif c.data:
                cond_txt += " " + c.data
            c = c.get_next()
        ir.append(["L" + l2, None])
        ir.append(["if" + cond_txt +" goto L"+l1,cond])


        return ir



    def make_IR2(self,node):
        cur = node
        ir = []
        self.g = 1
        while cur:
            if cur.data == "stat":
                n = cur.children[0]
                if n.data in ["word", "RETURN"] and n.parent.data !="block":
                    ir.append([self.word_ret(n), cur])
                elif n.data == "WHILE":
                    if cur.children[2].children: #Code optimizing block 없는 while 생략
                        t, cur = self.wh(cur)
                        ir = ir + t
                        self.g += 2
            cur = cur.search_inorder()
        return ir
    def wh2(self,cur):
        ir = []
        ir.append(["goto L"+str(self.g+1),None])
        ir.append(["L"+str(self.g),None])
        cond = cur.children[1]
        block = cur.children[2]
        c = cond
        cond_txt = ""
        block_txt = ""
        while c.children:
            c = c.children[0]

        while c.data != "{":
            if c.id:
                cond_txt += " " + c.id
            elif c.data:
                cond_txt += " " + c.data
            c = c.get_next()
        print("roTlqkf",cond_txt) # , cond

        while block.children:
            block = block.children[0]
        while block.data != "}":

            if block.id:
                block_txt += " " + block.id
            elif block.data:
                block_txt += " " + block.data
            block = block.get_next()
        block_txt = block_txt[2:]
        block_txt = block_txt.split(";")
        print("Tlqkf", block_txt)
        return ir,block.search_inorder()

    def word_ret(self,node):
        ir = node.children[0].id if node.children else node.data
        while not node.data in [";","THEN","{"] :
            node = node.get_next()
            if node.id:
                ir += " "  + node.id
            elif node.data:
                ir += " " + node.data
        return ir[:-2]


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
        parsing.parse_tree.node_print()
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
    for i, r in enumerate(ir):
        print(str(i) + ". " + r[0])
    qwert = code_generator(ir)
    qwert.generate()