import time
import os

ports = ["60001", "60003", "60005"]
filePath = "./Makefile.embedded"
lastPort = "PORT"
dirPath = "/home/user/project2stuff/contiki-ng-mw-2122/examples/rpl-border-router"
command = "make TARGET=cooja connect-router-cooja"
sudoPassword = 'user'

def replaceMakeFile(filePath, lastPort,port):
    f = open(filePath, "r")
    fileText = f.read()
    f.close()

    # replace port with current value
    fileText = fileText.replace(lastPort, port)
    lastPort = port

    f = open(filePath, "w")
    f.write(fileText)
    f.close()

    return lastPort

os.system("cd " + dirPath)
for port in ports:
    lastPort = replaceMakeFile(filePath, lastPort, port)

    p = os.system('echo %s|sudo -S %s' % (sudoPassword, command))
    os.system(command)

    print("Started border router on port: " + str(port))

    time.sleep(3)

lastPort = replaceMakeFile(filePath, lastPort, "PORT")
