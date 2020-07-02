from intModules.variable import variable
from intModules.label import label
import re
import sys

#trida dictionaries zajistuje praci s ramci GF,TF a LF 
# zaroven obsahuje slovnik navesti a zasobnik promennych pro instrukce PUSHS a POPS
class dictionaries():
    frames = []
    localFrames = []
    labels = {}
    varStack = []
    def __init__(self,inputArray):
        self.frames.append({}) #creating global frame
        self.inputArray = inputArray
        self.inputPosition = 0
    
    def getNextInput(self):
        if self.inputArray == []:
            return input()
        returnInput = self.inputArray[self.inputPosition]
        if self.inputPosition != len(self.inputArray)-1:
            self.inputPosition += 1
        return returnInput.strip("\n")

    def createFrame(self):
        if self.checkTemporaryFrame():
            self.frames[1].clear()
            self.frames.pop()
        self.frames.append({})
    def pushFrame(self):
        if self.checkTemporaryFrame() == False:
            sys.exit(55)
        for key in self.frames[1]:
            self.frames[1][key].frame = "LF"
        self.localFrames.append(self.frames[1])
        self.frames.pop()
    def popFrame(self):
        if len(self.localFrames) == 0:
            sys.exit(55)
        for key in self.localFrames[len(self.localFrames)-1]:
            self.localFrames[len(self.localFrames)-1][key].frame = "TF"
        if len(self.frames) == 2:
            self.frames[1].clear()
        self.frames.append(self.localFrames[len(self.localFrames)-1])
        self.localFrames.pop()
    def checkTemporaryFrame(self):
        if (len(self.frames) == 2):
            return True
        return False 
    def pushs(self,symb):
        self.varStack.append(symb)
    def pops(self,var):
        if len(self.varStack) == 0:
            sys.exit(56)
        popped = self.varStack[len(self.varStack)-1]
        var.value = popped.value
        var.type = popped.type
        self.varUpdate(var)
        self.varStack.pop()
    def varUpdate(self,var):
        if var.frame == "GF":
            self.frames[0].update({var.name : var})
        elif var.frame == "TF":
            if self.checkTemporaryFrame() == False:
                sys.exit(55)
            self.frames[1].update({var.name : var})
        elif var.frame == "LF":
            if len(self.localFrames) == 0:
                sys.exit(55)
            else:
                self.localFrames[len(self.localFrames)-1].update({var.name : var})              
        else:
            sys.exit(56)        
    def varFrameAdd(self,var):
        if var.frame == "GF":
            self.frames[0][var.name] = var
        elif var.frame == "TF":
            if self.checkTemporaryFrame() == False:
                sys.exit(55)
            self.frames[1][var.name] = var
        elif var.frame == "LF":
            if len(self.localFrames) == 0:
                sys.exit(55)
            else:
                self.localFrames[len(self.localFrames)-1][var.name] = var
        else:
            sys.exit(56)
    def getVar(self,var):
        if var.frame == "GF":
            return self.frames[0][var.name]
        elif var.frame == "TF":
            if self.checkTemporaryFrame() == False:
                sys.exit(55)
            return self.frames[1][var.name]
        elif var.frame == "LF":
            if len(self.localFrames) == 0:
                sys.exit(55)
            else:
                return self.localFrames[len(self.localFrames)-1][var.name]
        else:
            sys.exit(56)
    def getVarByName(self,name):
        name = name.split("@")
        if name[0] == "GF":
            return self.frames[0][name[1]]
        elif name[0] == "TF":
            if self.checkTemporaryFrame() == False:
                sys.exit(55)
            return self.frames[1][name[1]]
        elif name[0] == "LF":
            if len(self.localFrames) == 0:
                sys.exit(55)
            else:
                return self.localFrames[len(self.localFrames)-1][name[1]]
        else:
            sys.exit(56)         
    def isDef(self,var):
        if var.frame == "GF":
            if self.frames[0].get(var.name) == None:
                return False
            else:
                return True
        elif var.frame == "TF":
            if self.checkTemporaryFrame() == False:
                sys.exit(55)
            elif self.frames[1].get(var.name) == None:
                return False
            else:
                return True
        elif var.frame == "LF":
            if len(self.localFrames) == 0:
                sys.exit(55)
            elif len(self.localFrames[len(self.localFrames)-1]) == 0 or self.localFrames[len(self.localFrames)-1].get(var.name) == None:
                return False
            else:
                return True
        else:
            sys.exit(56)
    def isDefByName(self,name):
        name = name.split("@")
        if name[0] == "GF":
            if self.frames[0].get(name[1]) == None:
                sys.exit(54)
        elif name[0] == "TF":
            if self.checkTemporaryFrame() == False:
                sys.exit(55)
            elif self.frames[1].get(name[1]) == None:
                sys.exit(54)
            else:
                return
        elif name[0] == "LF":
            if len(self.localFrames) == 0:
                sys.exit(55)
            elif self.localFrames[len(self.localFrames)-1].get(name[1]) == None:
                sys.exit(54)
            else:
                return
    def labelIsDef(self,label):
        if (self.labels.get(label.name) == None):
            return False
        else:
            return True
    def getLabel(self,label):
        return self.labels[label.name]       
    def addLabel(self,label):
        if self.labelIsDef == True:
            sys.exit(52)
        else:
            self.labels[label.name] = label
