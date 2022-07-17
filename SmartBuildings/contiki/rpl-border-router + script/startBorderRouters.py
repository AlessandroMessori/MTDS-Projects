import time
import os

filePath = "/home/user/GIT/MTDS-Projects/SmartBuildings/contiki/Contiki-Os-sources/os/services/rpl-border-router/embedded/Makefile.embedded"
lastPort = "PORT"
portsFile = "/home/user/GIT/MTDS-Projects/SmartBuildings/scripts/config/ports.txt"
dirPath = "/home/user/GIT/MTDS-Projects/SmartBuildings/contiki/Contiki-Os-sources/examples/rpl-border-router"
command = "nohup make TARGET=cooja connect-router-cooja &>/dev/null &"
sudoPassword = 'user'

ports = ""

f = open(portsFile, "r")
ports = f.read()
ports = ports.split("\n")
print(ports)

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

#os.system("cd " + dirPath)
#os.system("pwd")

for port in ports:
    lastPort = replaceMakeFile(filePath, lastPort, port)

    p = os.system('echo %s|sudo -S %s' % (sudoPassword, command))
    #os.system(command)

    print("Started border router on port: " + str(port))

    time.sleep(0.5)

lastPort = replaceMakeFile(filePath, lastPort, "PORT")
