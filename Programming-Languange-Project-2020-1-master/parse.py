#!/usr/bin/env python
from lexical import scanner
from parse_tree import Node
import sys
class parser():
    def __init__(self, tokens, grammar_path):
        self.tokens = tokens
        self.grammar_path = grammar_path
        self.grammar = self.grammar_to_LL()
        self.classify_symbol()



    def grammar_to_LL(self):
        grammar = self.set_grammar()
        Recursion_Removed_grammar = self.Remove_Left_Recursion(grammar)
        Factoring_Removed_grammar = self.Remove_Left_factoring(Recursion_Removed_grammar)

        return grammar

    def get_FIRST(self):
        self.first = dict()
        keys = list(self.grammar.keys())
        for key in keys:
            self.first[key] = self.FIRST(key)

    def get_FOLLOW(self):
        self.follow = dict()
        keys = list(self.grammar.keys())
        for key in keys:
            self.follow[key] = set([])
        self.follow[keys[0]] = self.follow[keys[0]].union({"$"})

        self.done = False

        while not self.done:
            self.done = True
            for key in keys:
                self.FOLLOW(key)



    def get_Table(self):
        self.table = []

        for i, terminal in enumerate(self.terminal):
            self.table.append([])
            for non_terminal in (self.non_terminal + ["$"]):
                self.table[i].append(0)

        for left in list(self.grammar.keys()):
            index = self.terminal.index(left)
            first = self.first[left]
            follow = self.follow[left]
            for right in self.grammar[left]:
                fst = self.First(right)
                for f in fst :
                    if f != '':
                        self.table[index][self.non_terminal.index(f)] = right
                if '' in fst:
                    for flw in follow:
                        if flw == "$":
                            self.table[index][-1] = right
                        else:

                            self.table[index][self.non_terminal.index(flw)] = right


    def parsing(self,input_txt):
        terminal = {key : word for word, key in enumerate(self.terminal)}
        non_terminal = { key : word for word, key in enumerate(self.non_terminal)}
        non_terminal['$'] = len(non_terminal)
        input_txt.append("$")

        stack = [self.terminal[0],'$']
        self.parse_tree = Node(stack[0],None,0)
        check = False
        while len(stack) != 1:

            top = stack[0]
            stack = stack[1:]
            symbol = input_txt[0]
            if top in non_terminal:
                if symbol == top:
                    input_txt = input_txt[1:]
                    if symbol == '[a-zA-Z]*':
                        self.parse_tree.id = self.word_tokens[0]
                        self.word_tokens.remove(self.word_tokens[0])
                    elif symbol == '[0-9]*':
                        self.parse_tree.id = self.num_tokens[0]
                        self.num_tokens.remove(self.num_tokens[0])
                    self.parse_tree = self.parse_tree.get_next()
                else:

                    check = True
                    break
            else:
                push = self.table[terminal[top]][non_terminal[symbol]]
                if push != 0 :
                    if push != ['']:
                        stack = push + stack
                        self.parse_tree.set_child(push)
                        self.parse_tree = self.parse_tree.children[0]
                    else:
                        self.parse_tree.set_child([''])
                        self.parse_tree = self.parse_tree.get_next()
                else:
                    check = True
                    break
        if check:
            print("input code not accepted")
            sys.exit()
            return None
        else:
            self.parse_tree = self.parse_tree.get_root()
            return self.parse_tree

    def tokens_to_input(self,tokens):
        input_list = []
        self.word_tokens = []
        self.num_tokens = []
        for token_type, token in tokens:
            if token_type=="Word token :":
                input_list.append("[a-zA-Z]*")
                self.word_tokens.append(token)
            elif token_type == "Num token":
                input_list.append("[0-9]*")
                self.num_tokens.append(token)
            else:
                input_list.append(token)
        return input_list

    def get_symbol_table(self):
        # self.symbol_table = [[self.tokens[0][1],"function, void","global","0"]]
        # self.symbol_table +=  self.parse_tree.set_symbol_table();
        self.symbol_table = self.parse_tree.set_symbol_table();


    def set_grammar(self):
        with open(self.grammar_path, 'r', encoding="utf-8") as g:
            grammar_txt = g.read()
        grammar_list = grammar_txt.strip().split(";\n")
        grammar = dict()
        for trans in grammar_list:
            key, value = trans.split("->")

            key = key.strip()
            grammar[key] = []
            value = value.replace('"','').split("|")
            for val in value:
                val = val.split()
                if len(val):
                    grammar[key].append(val)
                else:
                    grammar[key].append([""])

        return grammar

    def Remove_Left_Recursion(self, grammar):
        keys = list(grammar.keys())

        for key in keys:
            values = list(grammar[key])
            recursion = []
            non_recursion = []

            for value in values:
                if key == value[0]:
                    recursion.append(value[1:])
                    grammar[key].remove(value)
                else:
                    non_recursion.append(value)

            if len(recursion):
                grammar[key] = list()
                prime = key + "'"
                grammar[prime] = list()
                for val in non_recursion:
                    if val != ['']:
                        grammar[key].append(val+[prime])
                    else:
                        grammar[key].append([prime])

                for val in recursion:
                    grammar[prime].append(val + [prime])
                grammar[prime].append([""])

        return grammar


    def Remove_Left_factoring(self, grammar):
        keys = list(grammar.keys())
        length = len(keys)
        index = 0
        while ((index + 1) < len(list(grammar.keys()))):
            key = list(grammar.keys())[index]
            values = list(grammar[key])
            prime = key + '"'
            len_values = len(values)

            for i in range(len_values-1):
                check = False
                for j in range(i+1,len_values):
                    if not (values[i] and values[j]):
                        continue
                    if values[i][0] == values[j][0]:

                        value = values[i][0]
                        if not prime in list(grammar.keys()):
                            grammar[prime] = []

                        if not ([value, prime]) in grammar[key]:
                            grammar[key].append([value,prime])

                        if not (values[i][1:]) in grammar[prime]:
                            if values[i][1:]:
                                grammar[prime].append(values[i][1:])
                            elif not [""] in grammar[prime]:
                                grammar[prime].append([""])

                        if (values[i]) in grammar[key]:
                            grammar[key].remove(values[i])
                        if not (values[j][1:]) in grammar[prime]:
                            if values[j][1:]:
                                grammar[prime].append(values[j][1:])
                            elif not [""] in grammar[prime]:
                                grammar[prime].append([""])

                        if (values[j]) in grammar[key]:
                            grammar[key].remove(values[j])
            index += 1
        return grammar

    def FIRST(self,key):
        keys = list(self.grammar.keys()) # LHS of the Grammar
        values = self.grammar[key] # RHS of the Grammar
        first = set()
        for value in values:
            # first is terminal
            if value[0] in keys:
                for i, symbol in enumerate(value):
                    # epsilon case handle. e.g. decls -> epsilon decls
                    if(symbol == key):
                        break
                    fst = self.FIRST(symbol)
                    # if epsilon is in FIRST(symbol), next symbol is investigated
                    if '' in fst:
                        first = first.union(fst - {''})
                        if i == len(value)-1:
                            first.add('')
                        continue
                    if len(fst) != 0:
                        first = first.union(fst)
                        break
            # first is non terminal
            else:
                first = first.union({value[0]})
        return first

    def First(self, key):
        symbol = key[0]
        result = set()
        if symbol in self.grammar.keys():
            result = result.union(self.first[symbol])
            if '' in self.first[symbol]:
                if len(key) == 1:
                    result = result.union({''})
                else:
                    result = result.union(self.First(key[1:]))
        else:
            result = result.union({symbol})
        return result



    def FOLLOW(self,symbol):
        keys = list(self.grammar.keys())
        check = dict()
        for key in self.follow:
            check[key] = set()
            for val in self.follow[key]:
                check[key].add(val)
        for key in keys:
            for values in self.grammar[key]:
                for i in range(len(values)):
                    if values[i] == symbol:
                        if i < (len(values) - 1):
                            if values[i+1] in keys:
                                next_first = self.first[values[i+1]]
                            else:
                                next_first = {values[i+1]}
                            if '' in next_first and key != symbol:
                                if not key in list(self.follow.keys()):
                                    self.FOLLOW(key)
                                self.follow[symbol] = self.follow[symbol].union(self.follow[key])
                            self.follow[symbol] = self.follow[symbol].union(next_first-{''})

                        else:
                            if key!=symbol:
                                if not key in list(self.follow.keys()):
                                    self.FOLLOW(key)
                                self.follow[symbol] = self.follow[symbol].union(self.follow[key])
        if self.follow != check:
            self.done = False

    def classify_symbol(self):
        self.terminal = list(self.grammar.keys())
        self.non_terminal = set()
        for terminal in self.terminal:
            for values in self.grammar[terminal]:
                for value in values:
                    if not value in (self.terminal + ['']):

                        self.non_terminal = self.non_terminal.union({value})
        self.non_terminal = list(self.non_terminal)
        self.non_terminal.sort()


if __name__ == "__main__":
    # open test file
    with open("testfiles/testfile_1.txt", 'r') as test:
        code = test.read()

    # scanner : print tokens
    scan = scanner(code)
    scan.lexical()
    tokens = scan.tokens
    for token in tokens:
        print(token)
    print()

    # LL parser
    parsing = parser(tokens, "grammar2.txt")

    # LL Grammar
    print("LL Grammar")
    for i in parsing.grammar:
        for j in parsing.grammar[i]:
            if j[0]=='':
                print(i, '->', "''")
            else:
                print(i,'->',' '.join(j))
    parsing.get_FIRST()
    parsing.get_FOLLOW()

    # Get First Set
    print("\nFIRST")
    for i in parsing.first:
        print(i, parsing.first[i])

    # Get Follow Set
    print("\nFOLLOW")
    for i in parsing.follow:
        print(i, parsing.follow[i])

    # Based on First & Follow, get Parsing Table
    parsing.get_Table()

    # Terminals
    print("\n terminals")
    print(parsing.terminal)

    # Non-Terminals
    print("\n non terminals")
    print(parsing.non_terminal)

    # Parsing Table
    print("\nTable")
    print(parsing.non_terminal + ['$'])
    for i in range(len(parsing.table)):
        print(parsing.terminal[i], parsing.table[i])
    print()

    # Abstract Syntax Tree
    input_list = parsing.tokens_to_input(tokens)
    asdf = parsing.parsing(input_list)
    if asdf:
        print(asdf)
    else:
        print("input not accepted")

    print("\n")
    parsing.get_symbol_table()
    print("Symbol Table\nsymbol, type, scope, size")
    for s in parsing.symbol_table:
        print(s)