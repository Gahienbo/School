from socket import *
import socket
import sys
import re
from urllib.parse import urlparse
from urllib.parse import urlparse, parse_qs
import ast

if len(sys.argv) > 1:
    if re.match("^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$",sys.argv[1]): #port regex
        serverPort = int(sys.argv[1])
    else:
        serverPort = 5353 #default port
#function that processes GET request 
def processGET(line):
    parsedUrl = urlparse(line)
    urlArgs = parse_qs(parsedUrl.query)
    try:
        name = urlArgs.get('name')
        name = name[0]
        type = urlArgs.get('type')
        type = type[0]
    except:
        return("\nHTTP/1.1 400 Bad Request \r\n\r\n")
    if (type != "A" and type != "PTR") or name == None :
        return("\nHTTP/1.1 400 Bad Request \r\n\r\n")
    if type == "A":
        try:
            answer = gethostbyname(name) 
        except:
            return("\nHTTP/1.1 404 Not Found \r\n\r\n")
    elif type == "PTR":
        try:
            answer = gethostbyaddr(name)
            answer = answer[0]
        except socket.herror as e:
            return("\nHTTP/1.1 404 Not Found \r\n\r\n")
    final = name+":"+type+"="+answer+"\n"
    return ("\nHTTP/1.1 200 OK\r\n\r\n"+final)
    pass
#function that processes POST request 
def processPOST(line,protocolData):
    if protocolData[1] != "/dns-query":
        return("\nHTTP/1.1 400 Bad Request \r\n\r\n")
    else:
        if protocolData[len(protocolData)-1] == "*/*":
            return("\nHTTP/1.1 404 Not Found \r\n\r\n")
        elif protocolData[len(protocolData)-1] == "application/x-www-form-urlencoded":
            return("\nHTTP/1.1 404 Not Found \r\n\r\n")
        else:
            i = line.index("")+1
            finalAnswer = ""
            #while cycle, processes all POST requests
            while len(line) > i:
                request = line[i].split(":")
                if len(request) == 2:
                    if request[1].replace(" ","") == "A":
                        try:
                            answer = gethostbyname(request[0]) 
                            finalAnswer += request[0]+":A="+answer+"\n"
                        except:
                            pass
                    elif request[1] == "PTR":
                        try:
                            answer = gethostbyaddr(request[0])
                            answer = answer[0]
                            finalAnswer += request[0]+":PTR="+answer+"\n"
                        except:
                            pass
                else:
                    if i == len(line)-1 and line[i] == "":
                        pass
                    else:
                        return("\nHTTP/1.1 400 Bad Request \r\n\r\n")
                i+=1
    if finalAnswer == "":
        finalAnswer = "\nHTTP/1.1 404 Not Found \r\n\r\n"
    else:
        finalAnswer = "\nHTTP/1.1 200 OK \r\n\r\n" + finalAnswer
    return finalAnswer
    pass
with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as serverSocket:
    serverSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    serverSocket.bind(('', serverPort))
    serverSocket.listen()
    #main loop of the server,server listens on port, waiting for request
    while True:
        conn, addr = serverSocket.accept()
        with conn:
            data = conn.recv(1024)
            if not data:
                break
            data = data.decode("utf-8")
            checkRequest = data.split()
            if checkRequest[0] == "GET":
                getData = data.split()
                reply = processGET(getData[1])
                conn.send(reply.encode())
            elif checkRequest[0] == "POST":
                postData = data.splitlines()
                reply = processPOST(postData,checkRequest)
                conn.send(reply.encode())
            else:
                    conn.send("\nHTTP/1.1 405 Method Not Allowed \r\n\r\n")
            conn.close()
