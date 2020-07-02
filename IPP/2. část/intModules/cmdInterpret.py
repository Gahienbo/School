import re
from intModules.variable import variable
from intModules.dictionaries import dictionaries
from intModules.label import label
import sys


#trida commandInterpret provadi jednotlive instrukce
class commandInterpret():
    def __init__(self,instruction,dictionaries):
        self.dictionaries = dictionaries
        self.instruction = instruction

    #pomocna procedura kontroluje zda je promenna definovana, pokud ano vraci objekt variable
    def checkDefAndLoad(self,name):
        var = variable(name)
        if not self.dictionaries.isDef(var):
            sys.exit(54)
        var = self.dictionaries.getVar(var)
        return var

    #instrukce DEFVAR
    def defvar(self):
        varToDef = variable(self.instruction.args[0][1])
        if self.dictionaries.isDef(varToDef):
            sys.exit(52)
        self.dictionaries.varFrameAdd(varToDef)

    #instrukce READ
    def read(self):
        varToRead = self.checkDefAndLoad(self.instruction.args[0][1])

        input = self.dictionaries.getNextInput()
        type = self.instruction.args[1][1]
        value = None
        if type == "int":
            varToRead.type = "int"
            try:
                value = int(input)
            except:
                value = 0
            varToRead.value = value
        elif type == "bool":
            varToRead.type = "bool"
            value = input
            if value.lower() == "true":
                varToRead.value = "true"
            else:
                varToRead.value = "false"
        elif type == "string":
            varToRead.type = "string"
            varToRead.value = input
        self.dictionaries.varUpdate(varToRead)

    #instrukce WRITE
    def write(self):
        if self.instruction.args[0][0] == "var":
            varToPrint = self.checkDefAndLoad(self.instruction.args[0][1])
            if varToPrint.value == None:
                sys.exit(56)
            if varToPrint.type == "nil":
                print("",end='')
            else:                  
                print(varToPrint.value,end='')
        else:
            if self.instruction.args[0][0] == "nil":
                print("",end='') 
            else:
                print(self.instruction.args[0][1],end='')

    #aritmetické instrukce - {ADD,SUB,MUL,IDIV}
    def arithmetric(self):
        varToAdd = self.checkDefAndLoad(self.instruction.args[0][1])
        def loadOperands(operand):
            if operand[0] == "var":
                returnOperand = self.checkDefAndLoad(operand[1])
                if returnOperand.type != "int":
                    sys.exit(56)
            elif operand[0] == "int":
                returnOperand = variable("temp@temp")
                returnOperand.type = "int"
                returnOperand.value = operand[1]
            else:
                sys.exit(53)
            return returnOperand

        operand01 = loadOperands(self.instruction.args[1])
        operand02 = loadOperands(self.instruction.args[2])
        if self.instruction.opcode == "ADD":
            value = int(operand01.value) + int(operand02.value)
        elif self.instruction.opcode == "SUB":
            value = int(operand01.value) - int(operand02.value)
        elif self.instruction.opcode == "MUL":
            value = int(operand01.value) * int(operand02.value)
        elif self.instruction.opcode == "IDIV":
            if int(operand02.value) == 0:
                sys.exit(57)
            else:
                value = int(operand01.value) // int(operand02.value)
        varToAdd.value = value
        varToAdd.type = "int"
        self.dictionaries.varUpdate(varToAdd)

    #relacni instrukce - {EQ,LT,GT}
    def relational(self):
        relationalVar = self.checkDefAndLoad(self.instruction.args[0][1])
        def loadOperands(operand):
            if operand[0] == "var":
                returnOperand = self.checkDefAndLoad(operand[1])
                if returnOperand.value == None:
                    sys.exit(56)
            else:
                returnOperand = variable("temp@temp")
                if operand[0] == "int":
                    returnOperand.value = int(operand[1])
                    returnOperand.type = operand[0]
                else:
                    returnOperand.value = operand[1]
                    returnOperand.type = operand[0]
            return returnOperand        

        operand01 = loadOperands(self.instruction.args[1])
        operand02 = loadOperands(self.instruction.args[2])

        if operand01.type != operand02.type and operand01.type != "nil" and operand02.type != "nil":
            sys.exit(53)
        else:
            if operand01.type == "nil" or operand02.type == "nil":
                if self.instruction.opcode == "EQ":
                    if operand01.type == "nil" and operand02.value == "nil":
                        relationalVar.value = "true"
                    else:
                        relationalVar.value = "false"
                else:
                    sys.exit(53)
            elif operand01.type == "int":
                if self.instruction.opcode == "LT": 
                    if int(operand01.value) < int(operand02.value): 
                        relationalVar.value = "true"
                    else:
                        relationalVar.value = "false"
                elif self.instruction.opcode == "GT":
                    if int(operand01.value) > int(operand02.value):
                        relationalVar.value = "true"
                    else:
                        relationalVar.value = "false"
                elif self.instruction.opcode == "EQ":
                    if int(operand01.value) == int(operand02.value):
                        relationalVar.value = "true"
                    else:
                        relationalVar.value = "false"
            elif operand01.type == "bool":
                if self.instruction.opcode == "LT":
                    if operand01.value == "false" and operand02.value == "true":
                        relationalVar.value = "true"
                    else:
                        relationalVar.value = "false"
                elif self.instruction.opcode == "GT":
                    if operand01.value == "true" and operand02.value == "false":
                        relationalVar.value = "true"
                    else:
                        relationalVar.value = "false"
                elif self.instruction.opcode == "EQ":
                    if operand01.value == operand02.value:
                        relationalVar.value = "true"
                    else:
                        relationalVar.value = "false"
            elif operand01.type == "string":
                if self.instruction.opcode == "LT":
                    if operand01.value < operand02.value:
                        relationalVar.value = "true"
                    else:
                        relationalVar.value = "false"
                elif self.instruction.opcode == "GT":
                    if operand01.value > operand02.value:
                        relationalVar.value = "true"
                    else:
                        relationalVar.value = "false"
                elif self.instruction.opcode == "EQ":
                    if operand01.value == operand02.value:
                        relationalVar.value = "true"
                    else:
                        relationalVar.value = "false"
        relationalVar.type = "bool"
        self.dictionaries.varUpdate(relationalVar)

    #logické instrukce - {AND,OR,NOT}
    def logical(self):
        logicalVar = self.checkDefAndLoad(self.instruction.args[0][1])
        def loadOperands(operand):
            if operand[0] == "var":
                returnOperand = self.checkDefAndLoad(operand[1])
                if returnOperand.value == None:
                    sys.exit(56)
                if returnOperand.type != "bool":
                    sys.exit(53)
            elif operand[0] == "bool":
                returnOperand = variable("temp@temp")
                returnOperand.type = "bool"
                returnOperand.value = operand[1]
            else:
                sys.exit(53)
            return returnOperand

        operand01 = loadOperands(self.instruction.args[1])
        if self.instruction.opcode != "NOT":
            operand02 = loadOperands(self.instruction.args[2]) 

        if self.instruction.opcode == "AND":
            if operand01.value == "true" and operand02.value == "true":
                logicalVar.value = "true"
            else:
                logicalVar.value = "false"
        elif self.instruction.opcode == "OR":
            if operand01.value == "true" or operand02.value == "true":
                logicalVar.value = "true"
            else:
                logicalVar.value = "false"
        elif self.instruction.opcode == "NOT":
            if operand01.value == "true":
                logicalVar.value = "false"
            else:
                logicalVar.value = "true"
        logicalVar.type = "bool"
        self.dictionaries.varUpdate(logicalVar)

    #instrukce MOVE
    def move(self):
        movVar = self.checkDefAndLoad(self.instruction.args[0][1])
        if self.instruction.args[1][0] == "var":
            moveOperand = self.checkDefAndLoad(self.instruction.args[1][1])
            if moveOperand.value == None:
                sys.exit(56)     
            movOperandValue = moveOperand.value
            movOperandType = moveOperand.type
        else:
            movOperandValue = self.instruction.args[1][1]
            movOperandType = self.instruction.args[1][0]                                  
        movVar.value = movOperandValue
        movVar.type = movOperandType
        self.dictionaries.varUpdate(movVar)
    
    #instrukce LABEL
    def label(self):
        labelToAdd = label(self.instruction)
        self.dictionaries.labelIsDef(labelToAdd)
        self.dictionaries.addLabel(labelToAdd)

    #instrukce CALL       
    def call(self):
        labelToCall = label(self.instruction.args[0][1])
        if self.dictionaries.labelIsDef(labelToCall) == False:
            sys.exit(52)
        labelToCall = self.dictionaries.getLabel(labelToCall)
        return int(labelToCall.order)-1

    #instrukce JUMP
    def jump(self):
        labeltoJump = label(self.instruction.args[0][1])
        if  not self.dictionaries.labelIsDef(labeltoJump):
            sys.exit(52)
        labeltoJump = self.dictionaries.getLabel(labeltoJump)
        return int(labeltoJump.order) -1

    #instrukce zajistujici podminene skoky - {JUMPIFEQ,JUMPIFNEQ}
    def conditionalJump(self):
        labeltoJump = label(self.instruction.args[0][1])
        if not self.dictionaries.labelIsDef(labeltoJump):
            sys.exit(52)
        labeltoJump = self.dictionaries.getLabel(labeltoJump)
        def loadOperands(operand):
            if operand[0] == "var":
                returnOperand = self.checkDefAndLoad(operand[1])
            else:
                returnOperand = variable("temp@temp")
                returnOperand.type = operand[0]
                returnOperand.value = operand[1]
            return returnOperand

        operand01 = loadOperands(self.instruction.args[1])
        operand02 = loadOperands(self.instruction.args[2])

        if operand01.type == operand02.type or (operand01.type == "nil" or operand02.type == "nil"):
            if self.instruction.opcode == "JUMPIFEQ":
                if operand01.type == "nil" and operand02.type == "nil":
                    return int(labeltoJump.order)-1
                if operand01.type == "int": 
                    if operand01.type == "nil" or operand02.type == "nil":
                        int(self.instruction.order)-1  
                    elif int(operand01.value) == int(operand02.value):
                        return int(labeltoJump.order)-1
                else:
                    if operand01.type == "nil" or operand02.type == "nil":
                        int(self.instruction.order)-1  
                    elif operand01.value == operand02.value:
                        return int(labeltoJump.order)-1
            else:
                if operand01.type == "nil" and operand02.type == "nil":
                    return int(self.instruction.order)-1
                if operand01.type == "int":
                    if operand01.type == "nil" or operand02.type == "nil":
                        return int(labeltoJump.order)-1
                    elif int(operand01.value) != int(operand02.value):
                        return int(labeltoJump.order)-1
                else:
                    if operand01.value != operand02.value:
                        return int(labeltoJump.order)-1              
            return int(self.instruction.order)-1
        if operand01.type == None or operand02.type == None:
            sys.exit(56)              
        sys.exit(53)

    #instrukce EXIT
    def exit(self):
        if self.instruction.args[0][0] == "var":
            exitOperand = self.checkDefAndLoad(self.instruction.args[0][1])
            if exitOperand.value == None:
                sys.exit(56)
            if exitOperand.type == "int":
                if int(exitOperand.value)>=0 and int(exitOperand.value)<=49:
                    sys.exit(int(exitOperand.value))
                else:
                    sys.exit(57)
            sys.exit(53)
        elif self.instruction.args[0][0] == "int":
            exitOperandValue = self.instruction.args[0][1]
            if int(exitOperandValue)>=0 and int(exitOperandValue)<=49:
                sys.exit(int(exitOperandValue))
            else:
                sys.exit(57)
        sys.exit(53)

    #instrukce TYPE
    def type(self):
        typeVar = self.checkDefAndLoad(self.instruction.args[0][1])

        if self.instruction.args[1][0] == "var":
            operand = self.checkDefAndLoad(self.instruction.args[1][1])
            if operand.type == None:
                typeVar.value = ""
            else:
                typeVar.value = operand.type
        else:
            typeVar.value = self.instruction.args[1][0]
        typeVar.type = "string"
        self.dictionaries.varUpdate(typeVar)

    #instrukce pro prace s retezci - {CONCAT,STRLEN,GETCHAR,SETCHAR,INT2CHAR}
    def stringFunc(self):

        def loadOperands(operand):
            if operand[0] == "var":
                returnOperand = self.checkDefAndLoad(operand[1])
                if returnOperand.value == None:
                    sys.exit(56)
                if returnOperand.type != "string":
                    sys.exit(53)
            elif operand[0] == "string":
                returnOperand = variable("temp@temp")
                returnOperand.type = operand[0]
                returnOperand.value = operand[1]
            else:
                sys.exit(53)
                
            return returnOperand
        def concat():
            concatVar = self.checkDefAndLoad(self.instruction.args[0][1])

            operand01 = loadOperands(self.instruction.args[1])
            operand02 = loadOperands(self.instruction.args[2])

            concatVar.value = operand01.value + operand02.value
            concatVar.type = "string"
            self.dictionaries.varUpdate(concatVar)

        def strlen():
            strlenVar = self.checkDefAndLoad(self.instruction.args[0][1])

            operand01 = loadOperands(self.instruction.args[1])

            strlenVar.type = "int"
            if operand01.type == "nil":
                operand01.value = 0
            else:
                strlenVar.value = len(str(operand01.value))
            self.dictionaries.varUpdate(strlenVar)
        
        def getchar():
            varToGet = self.checkDefAndLoad(self.instruction.args[0][1])

            operand01 = loadOperands(self.instruction.args[1])

            if self.instruction.args[2][0] == "var":
                operand02 = self.checkDefAndLoad(self.instruction.args[2][1])
            elif self.instruction.args[2][0] == "int":
                operand02 = variable("temp@temp")
                operand02.type = self.instruction.args[2][0]
                operand02.value = self.instruction.args[2][1]
            else:
                sys.exit(53)
            if operand02.value == None:
                sys.exit(56)
            if int(operand02.value) < 0:
                sys.exit(58)

            try:
                varToGet.value = operand01.value[int(operand02.value)]
            except:
                sys.exit(58)
            if self.instruction.opcode == "STRI2INT":
                varToGet.type = "int"
                varToGet.value = ord(varToGet.value)
            else:
                varToGet.type = "string"
            self.dictionaries.varUpdate(varToGet)

        def setchar():
            varToSet = self.checkDefAndLoad(self.instruction.args[0][1])
            if varToSet.value == None:
                sys.exit(56)
            if varToSet.type != "string":
                sys.exit(53)
            if varToSet.value == None or varToSet.value == "":
                sys.exit(58)
            
            if self.instruction.args[1][0] == "var":
                operand01 = self.checkDefAndLoad(self.instruction.args[1][1])
            elif self.instruction.args[1][0] == "int":
                operand01 = variable("temp@temp")
                operand01.type = self.instruction.args[1][0]
                operand01.value = self.instruction.args[1][1]
            else:
                sys.exit(53)
            if operand01.value == None:
                sys.exit(56)
            if int(operand01.value) < 0:
                sys.exit(58)

            operand02 = loadOperands(self.instruction.args[2])
            if operand02.value == None:
                sys.exit(56)
            try:
                retype = list(varToSet.value)
                retype[int(operand01.value)] = operand02.value[0]
                varToSet.value = ''.join([str(elem) for elem in retype])
            except:
                sys.exit(58)
            self.dictionaries.varUpdate(varToSet)
        
        def int2char():
            var = self.checkDefAndLoad(self.instruction.args[0][1])
            
            if self.instruction.args[1][0] == "var":
                operand01 = self.checkDefAndLoad(self.instruction.args[1][1])
                if operand01.value == None:
                    sys.exit(56)
                if operand01.type != "int":
                    sys.exit(53)
            elif self.instruction.args[1][0] == "int":
                operand01 = variable("temp@temp")
                operand01.type = self.instruction.args[1][0]
                operand01.value = self.instruction.args[1][1]
            else:
                sys.exit(53)
            
            try:
                var.value = chr(int(operand01.value))
                var.type = "string"
            except:
                sys.exit(58)
            
            self.dictionaries.varUpdate(var)

        if self.instruction.opcode == "CONCAT":
            concat()
        elif self.instruction.opcode == "STRLEN":
            strlen()
        elif self.instruction.opcode == "GETCHAR" or self.instruction.opcode == "STRI2INT":
            getchar()
        elif self.instruction.opcode == "SETCHAR":
            setchar()
        elif self.instruction.opcode == "INT2CHAR":
            int2char()

    #instrukce PUSHS
    def pushs(self):
        if self.instruction.args[0][0] == "var":
            pushVar = self.checkDefAndLoad(self.instruction.args[0][1])
            if pushVar.value == None:
                sys.exit(56)
        else:
            pushVar = variable("temp@temp")
            pushVar.value = self.instruction.args[0][1]
            pushVar.type = self.instruction.args[0][0]
        self.dictionaries.pushs(pushVar)
    
    #instrukce POPS
    def pops(self):
        popVar = self.checkDefAndLoad(self.instruction.args[0][1])
        self.dictionaries.pops(popVar)


        


            