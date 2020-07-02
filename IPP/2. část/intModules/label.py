class label():
    name = None
    order = None
    def __init__(self,name,order = None):
        self.name = name
        if order != None:
            self.order = order