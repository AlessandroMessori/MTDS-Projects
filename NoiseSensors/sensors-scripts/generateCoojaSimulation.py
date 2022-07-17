import json

filePath = "./config/simulation1.json"
templatePath = "./config/moteTemplate.csc"
routerPath = "./config/routerTemplate.csc"
simulationPath = "./config/simulationTemplate.csc"
listening_router = "./config/router_listening.csc"
ports = "./config/ports.txt"


motes = ""
listeners = ""
room_building_string = ""
portsText = ""

idCounter = 1
regionDist = 100
sensorDist = 15
regionCounter = 1


def generateMote(templatePath, id, x, y, mtype):
    f = open(templatePath, "r")
    fileText = f.read()
    f.close()

    # replace params with current value
    fileText = fileText.replace("MOTE_ID", id)
    fileText = fileText.replace("X_COORD", str(x))
    fileText = fileText.replace("Y_COORD", str(y))
    fileText = fileText.replace("M_TYPE", mtype)

    return fileText


def activateListeners(listening_router, index, port):
    f = open(listening_router, "r")
    fileText = f.read()
    f.close()

    fileText = fileText.replace("MOTE_INDEX", str(index))
    fileText = fileText.replace("PORT_NUMBER", str(port + 60000))

    return fileText


# Opening JSON file
with open(filePath) as json_file:
    simConfig = json.load(json_file)

    for region in enumerate(simConfig):

        # Add border routers
        motes += generateMote(routerPath, str(idCounter),
                                  regionCounter * regionDist, regionDist, "sky1")

        listeners += activateListeners(listening_router, idCounter-1, idCounter)
        portsText += (str(idCounter + 60000) + "\n")

        idCounter += 1

        # Add senors in top left and bottom right corners
        motes += generateMote(templatePath, str(idCounter),
                                  regionCounter * regionDist+sensorDist, regionDist+sensorDist, "mtype409")
        idCounter += 1

        motes += generateMote(templatePath, str(idCounter),
                                  regionCounter * regionDist-sensorDist, regionDist-sensorDist, "mtype409")
        idCounter += 1


        regionCounter+=1

    portsText = portsText[:-1]

    f = open(ports, "w")
    f.write(portsText)
    f.close()

    f = open(simulationPath, "r")
    fileText = f.read()
    f.close()

    fileText = fileText.replace("<---INSERT MOTES HERE-->", motes)
    fileText = fileText.replace("<-- INSERT LISTENERS HERE -->", listeners)

    f = open("./test.csc", "w")
    f.write(fileText)
    f.close()
