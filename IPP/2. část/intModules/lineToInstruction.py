
class instructionConverter():
    def __init__(self,lineArr):
        self.lineArr = lineArr
        """
        self.order = instruction.attrib.get("order")
        self.opcode = instruction.attrib.get("opcode")
        self.args = []
        for arg in instruction:
            args.append(arg.attrib.get("type"),arg.text)
        """
    def getlineArr(self):
        return self.lineArr
    def convert(self):
        self.insOrder = self.lineArr[0]
        self.oppCode = self.lineArr[1]
        self.argArr = self.lineArr[2]
        #print("order: ",self.insOrder,"oppcode: ",self.oppCode,"args: ",self.argArr)

    