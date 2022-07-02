import json

filePath = "./config/simulation1.json"
templatePath = "./config/moteTemplate.csc"
simulationPath = "./config/simulationTemplate.csc"
motes = ""

def generateMote(templatePath, id, x, y, mtype):
    f = open(templatePath, "r")
    fileText = f.read()
    f.close()

    # replace params with current value
    fileText = fileText.replace("MOTE_ID", id)
    fileText = fileText.replace("X_COORD", x)
    fileText = fileText.replace("Y_COORD", y)
    fileText = fileText.replace("M_TYPE", mtype)

    return fileText


# Opening JSON file
with open(filePath) as json_file:
    simConfig = json.load(json_file)
 
    for building in simConfig:

        for i in range(0, building["nRooms"]):

            for j in range(0, building["nSensorsPerRoom"]):
               motes += generateMote(templatePath, str(i)+"-"+str(j), "1", "1", "mtype840")

        for j in range(0, building["nRooms"] // 2):
               motes += generateMote(templatePath, str(j), "1", "1", "mtype245")

    f = open(simulationPath, "r")
    fileText = f.read()
    f.close()

    fileText = fileText.replace("<---INSERT MOTES HERE-->", motes)

    f = open("./test.csc", "w")
    f.write(fileText)
    f.close()
  

 