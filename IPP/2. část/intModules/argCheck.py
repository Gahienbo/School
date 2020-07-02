import sys
import getopt

#trida kontrolujici argumenty programu
class checkArg():
    def __init__(self):
        self.sourceFileName = sys.stdin
        self.inputFileName = sys.stdin
    def check(self):
        try:
            opts, args = getopt.getopt(sys.argv[1:], "", ['help', 'source=','input='])
        except getopt.GetoptError as error:
            exit(10)
        if len(opts) == 1:
            opts = opts[0]
            o, v = opts
            if o == '--help':
                print("Program načte XML reprezentaci programu a tento program s využitím vstupu dle parametrů příkazové řádky interpretuje a generuje výstup.")
                sys.exit(0)
            elif o == '--source':
                self.sourceFileName = v
            elif o == '--input':
                self.inputFileName = v
        elif len(opts) == 2:
            arg_1 = opts[0]
            arg_2 = opts[1]
            a,b = arg_1
            c,d = arg_2
            if (a == '--source' or a == '--input') and (c == '--source' or c == '--input'):
                if a == '--source':
                    self.sourceFileName = b
                    self.inputFileName = d
                elif c == '--source':
                    self.sourceFileName = d
                    self.inputFileName = b
                else:
                    sys.exit(10)
            else:
                sys.exit(10)
        if self.sourceFileName == sys.stdin and self.inputFileName == sys.stdin:
            sys.exit(10)
