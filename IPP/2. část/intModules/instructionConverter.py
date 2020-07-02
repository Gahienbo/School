#trida prevadi instrukce z xml reprezentace
class instructionConverter():
    def __init__(self,instruction):
        self.order = instruction.attrib.get("order")
        self.opcode = instruction.attrib.get("opcode")
        self.args = []
        self.argCount = 0
        for arg in instruction:
            self.args.append([arg.attrib.get("type"),arg.text])
            self.argCount += 1
