#Trida variable, pro jednodussi praci s promennymi
#kazda promenna obsahuje typ,hodnotu,ramec ve kterem je definovana a nazev
class variable():
    type = None
    value = None
    frame = None
    name = None
    def __init__(self,name):
        frameName = name.split("@")
        self.name = frameName[1]
        self.frame = frameName[0]