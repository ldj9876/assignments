from lexical import scanner
from parse import parser
from semantic import semantic

class IR():
    def __init__(self,tree):
        self.tree = tree

    def make_IR(self,node):
        cur = node.search_inorder()
        ir = []
        node.node_print()
        while cur:
            if cur.data == "stat":
                n = cur.children[0]
                if n.data in ["word", "RETURN"]:
                    print("roTlqkf" + n.id)
                    ir.append([self.word_ret(n),cur])
            cur =cur.search_inorder()
        return ir

    def word_ret(self,node):
        ir = node.id if node.id else node.data
        print(node.id)
        while node.data != ";" :
            node = node.get_next()
            if node.id:
                ir += " "  + node.id
            elif node.data:
                ir += " " + node.data
        return ir



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
    ir = IR(parsing.parse_tree)
    Ir = ir.make_IR(ir.tree)
    for i in Ir:
        print(i[0])