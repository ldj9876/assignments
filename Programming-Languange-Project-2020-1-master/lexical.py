#!/usr/bin/env python
import sys
class scanner():
    def __init__(self, code: str):
        self.code = code
        self.length = len(code)
        self.Bracket = ['(', ')', '{', '}']
        self.special_character = [';',',']
        self.types = ['int', 'char']
        self.statement = ['IF', 'THEN', 'ELSE', "WHILE" ]
        self.Return = ["RETURN"]
        self.operator = ['>', '==', '+', '*', '=']
        self.tokens = []

    def lexical(self):
        i = 0
        while i < self.length:
            if self.code[i].isdigit(): # num
                token = self.get_number(i)
                self.tokens.append(['Num token', token])
                i += len(token)

            elif self.code[i].isalpha(): #word or type or statement
                token = self.get_alphabet(i)
                if token in self.types:
                    self.tokens.append(['Type token :', token])
                elif token in self.statement:
                    self.tokens.append(['Statement token :', token])
                elif token in self.Return:
                    self.tokens.append(['RETURN token :', token])
                else:
                    self.tokens.append(['Word token :', token])
                i += len(token)

            elif self.code[i].isspace(): #space, \n, \t
                i += 1

            else: #Bracket Operator others or error
                if self.code[i] in self.Bracket:
                    self.tokens.append(['Bracket token : ',self.code[i]])
                    i += 1
                elif self.code[i] in self.operator:
                    if self.check_equal(i):
                        self.tokens.append(['Operator token : ', '=='])
                        i += 2
                    else :
                        self.tokens.append(['Operator token : ', self.code[i]])
                        i += 1
                elif self.code[i] in self.special_character:
                    self.tokens.append(['Other token : ',self.code[i]])
                    i +=1

                else:
                    self.tokens.append(0)
                    print("Error occurr-ed in lexical analysis")
                    sys.exit()

    def check_equal(self, i):
        if self.code[i] == '=' and i < self.length -1 :
            if self.code[i+1] == '=':
                return True

    def get_alphabet(self, i):
        j=1
        while i+j < self.length :
            if self.code[i+j].isalpha():
                j += 1
            else:
                break
        return self.code[i:i+j]
    
    def get_number(self,i):
        j = 1
        while i + j < self.length:
            if self.code[i + j].isdigit():
                j += 1
            else:
                break
        return self.code[i:i + j]
