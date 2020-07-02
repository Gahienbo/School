from xml.dom import minidom
import sys
import re
import xml.etree.ElementTree as elementTree
from intModules.instructionConverter import instructionConverter
from intModules.label import label


#třída xmlParser převádí vstupní xml soubor do seznamu instrukcí, které se mají vykonat
# zároveň provádí syntaktické a lexikalní kontroly argumentů a jednotlivých instrukcí
class xmlParser():
    def __init__(self,sourcefileName,inputFileName,dictionaries):
        self.dictionaries = dictionaries
        self.sourceFileName = sourcefileName
        self.inputFileName = inputFileName
        self.instructionList = []

    def checkXml(self):
        self.loadXmlFile()
        self.checkXmlSyntax()

    #nacita XML soubor a zaroven testuje spravnou syntaxi xml elementu
    def loadXmlFile(self):
        try:
            tree = elementTree.parse(self.sourceFileName)
            self.root = tree.getroot()
        except:
            sys.exit(31)
        if self.root.tag != "program":
            sys.exit(31)
        if self.root.attrib.get('language').lower() != "ippcode20":
            sys.exit(31)
        if len(self.root.attrib) > 3:
            sys.exit(31)
        for attrib in self.root.attrib:
            if attrib != "language" and attrib != "name" and attrib != "description":  
                sys.exit(31)
        for child in self.root:
            if child.tag != "instruction":
                sys.exit(31)
            if child.attrib.get("order") == None:
                sys.exit(31)
            if child.attrib.get("opcode") == None:
                sys.exit(31)
            for arg in child:
                if arg.tag not in ["arg1","arg2","arg3"]:
                    sys.exit(31)
                if arg.attrib.get("type") == None or arg.attrib.get("type") not in ["int","string","var","bool","nil","label","type"]:
                    sys.exit(31)
    #procedura checkXmlSyntax prochazi jednotlive insturkce a kontroluje jejich operandy podle jejich typu
    def checkXmlSyntax(self):
        for instruction in self.root:
            opcode = instruction.attrib.get("opcode")
            if opcode in ["CREATEFRAME","PUSHFRAME","POPFRAME","RETURN","BREAK"]: #without arg
                if len(instruction) == 0:
                    self.convertAndAppend(instruction)
                else:
                    sys.exit(31)
            elif opcode in ["MOVE","INT2CHAR","STRLEN","TYPE","NOT"]: #var symb
                if len(instruction) == 2:
                    self.varCheck(instruction[0])
                    self.symbCheck(instruction[1])
                    self.convertAndAppend(instruction)
                else:
                    sys.exit(31)
            elif opcode in ["DEFVAR","POPS"]: #var
                if len(instruction) == 1:
                    self.varCheck(instruction[0])
                    self.convertAndAppend(instruction)
                else:
                    sys.exit(31)
            elif opcode in ["CALL","LABEL","JUMP"]: #label
                if len(instruction) == 1:
                    self.labelCheck(instruction[0])
                    self.convertAndAppend(instruction)
                    if opcode == "LABEL":
                        labelToAdd = label(instruction[0].text,instruction.attrib.get("order"))
                        if self.dictionaries.labelIsDef(labelToAdd) == True:
                            sys.exit(52)                       
                        self.dictionaries.labelIsDef(labelToAdd)
                        self.dictionaries.addLabel(labelToAdd)                      
                else:
                    sys.exit(31)
            elif opcode in ["PUSHS","EXIT","DPRINT","WRITE"]: #symb
                if len(instruction) == 1:
                    self.symbCheck(instruction[0])
                    self.convertAndAppend(instruction)
                else:
                    sys.exit(31)
            elif opcode in ["ADD","SUB","MUL","IDIV","LT","GT","EQ","AND","OR","STRI2INT","CONCAT","GETCHAR","SETCHAR"]: #var symb1 symb2
                if len(instruction) == 3:
                    self.varCheck(instruction[0])
                    self.symbCheck(instruction[1])
                    self.symbCheck(instruction[2])
                    self.convertAndAppend(instruction)
                else:
                    sys.exit(31)
            elif opcode in ["JUMPIFEQ","JUMPIFNEQ"]: #label symb1 symb2
                if len(instruction) == 3:
                    self.labelCheck(instruction[0])
                    self.symbCheck(instruction[1])
                    self.symbCheck(instruction[2])
                    self.convertAndAppend(instruction)
                else:
                    sys.exit(31)
            elif opcode == "READ": #var type
                if len(instruction) == 2:
                    self.varCheck(instruction[0])
                    self.typeCheck(instruction[1])
                    self.convertAndAppend(instruction)
            else:
                sys.exit(31)

    #setridi vysledny seznam instrukci podle insOrder
    def sortInstructionList(self):
        self.instructionList.sort(key=lambda x: int(x.order))

    #vytvoreni objektu instrukce a pridani na konec seznamu
    def convertAndAppend(self,instruction):
        converted = instructionConverter(instruction)
        self.instructionList.append(converted)

    #pomocna procedura kontrolujici datove typy
    def typeCheck(self,type):
        if type.attrib.get("type") != "type":
            sys.exit(31)
        if type.text not in ["int","string","bool","nil"]:
            sys.exit(52)
    #pomocna procedura kontrolujici spravny syntax jmen promennych
    def varCheck(self,var):
        if var.attrib.get("type") != "var":
            sys.exit(32)
        if re.match(r"\AGF@", var.text) or re.match(r"\ATF@", var.text) or re.match(r"\ALF@", var.text) or var.text == None:
            return
        sys.exit(52)

    #pomocna procedura kontrolujici spravny syntax jmen symbolu - {int,bool,string,nil}   
    def symbCheck(self,symb):
        type = symb.attrib.get("type")
        if type == "int":
            pass
        elif type == "bool":
            if symb.text == "true" or symb.text == "false":
                return
            else:
                sys.exit(52)
        elif type == "string": #TODO string regex
            if symb.text == None:
                symb.text = ""
            else:
                symb.text = re.sub(r'\\([0-9]{3})', lambda x: chr(int(x.group(1))), symb.text)               
        elif type == "nil":
            if symb.text == "nil":
                return
            else:
                sys.exit(52)
        elif type == "var":
            self.varCheck(symb)
        else:
            sys.exit(32)

    #pomocna procedura kontrolujici spravny syntax jmen navesti
    def labelCheck(self,label):
        if label.attrib.get("type") != "label":
            sys.exit(32)
        if label.text != None:
            return
        sys.exit(52)    

