from intModules.argCheck import checkArg
from intModules.xmlParser import xmlParser
from intModules.cmdInterpret import commandInterpret
from intModules.dictionaries import dictionaries
import sys

def Main():
    #kontrola argumentu
    argChecker = checkArg()
    argChecker.check()
    #otevreni souboru se vstupy
    inputArray = []
    if argChecker.inputFileName != sys.stdin:
        try:
            inputFile = open(argChecker.inputFileName, "r")
            inputArray = inputFile.readlines()
            inputFile.close()
        except:
            sys.exit(11)

    dictionaryHolder = dictionaries(inputArray)

    #precteni xml vstupu
    xmlR = xmlParser(argChecker.sourceFileName,argChecker.inputFileName,dictionaryHolder)
    xmlR.checkXml()
    #serazeni instrukci podle vzrustajiciho insOrder
    xmlR.sortInstructionList()
    #pridani ENDMARKER na konec seznamu instrukci pro cyklus while
    xmlR.instructionList.append("ENDMARKER")
    #inicializace zasobniku insOrder jednotlivých instrukci pri provadeni ruznych skoku
    instructionOrderStack = []

    #nacteni prvni instrukce
    currIns = 0;
    ins = xmlR.instructionList[currIns]

    #hlavní cyklus interpretu, cte instrukce ze seznamu instrukci a podle operacniho kodu posila zpravy objektu commandInterpret
    while ins != "ENDMARKER":
        opCode = xmlR.instructionList[currIns].opcode
        cmdInt = commandInterpret(xmlR.instructionList[currIns],dictionaryHolder)
        if opCode == "DEFVAR": 
            cmdInt.defvar()
        elif opCode == "READ": 
            cmdInt.read()
        elif opCode == "WRITE": 
            cmdInt.write()
        elif opCode == "ADD" or opCode == "SUB" or opCode == "MUL" or opCode == "IDIV": 
            cmdInt.arithmetric()
        elif opCode == "LT" or opCode == "GT" or opCode == "EQ": 
            cmdInt.relational()
        elif opCode == "AND" or opCode == "OR" or opCode == "NOT": 
            cmdInt.logical()
        elif opCode == "MOVE": 
            cmdInt.move()
        elif opCode == "CALL": 
            instructionOrderStack.append(currIns)
            currIns = cmdInt.call()
        elif opCode == "RETURN": 
            if len(instructionOrderStack) == 0:
                sys.exit(56)
            else:
                currIns = instructionOrderStack.pop()
        elif opCode == "JUMP": 
            currIns = cmdInt.jump()
        elif opCode == "JUMPIFEQ": 
            currIns = cmdInt.conditionalJump()
        elif opCode == "JUMPIFNEQ": 
            currIns = cmdInt.conditionalJump()
        elif opCode == "EXIT": 
            cmdInt.exit()
        elif opCode == "TYPE": 
            cmdInt.type()
        elif opCode == "CREATEFRAME":
            dictionaryHolder.createFrame()
        elif opCode == "PUSHFRAME":
            dictionaryHolder.pushFrame()
        elif opCode == "POPFRAME":
            dictionaryHolder.popFrame()
        elif opCode == "LABEL":
            pass
        elif opCode in ["CONCAT","STRLEN","GETCHAR","SETCHAR","STRI2INT","INT2CHAR"]:
            cmdInt.stringFunc()
        elif opCode == "PUSHS":
            cmdInt.pushs()
        elif opCode == "POPS":
            cmdInt.pops()
        #v pripade ze ma instrukce neznamy opecacni kod se program ukonci s navratovou hodnotou 32,
        #  tento pripad by nemel nastat provadi se kontrola jiz v xmlParseru
        else:
            sys.exit(32)
        #nacteni dalsi instrukce
        currIns += 1
        ins = xmlR.instructionList[currIns]
if __name__ == "__main__":
    Main()
