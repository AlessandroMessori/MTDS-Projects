import json

filePath = "./config/simulation1.json"
templatePath = "./config/moteTemplate.csc"
simulationPath = "./config/simulationTemplate.csc"
motes = ""
idCounter = 1
y_hall = 4.0
x_hall = 3.0
buildingDist = 50


SIDE = 5.0


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


# Opening JSON file
with open(filePath) as json_file:
    simConfig = json.load(json_file)
 
    for counter, building in enumerate(simConfig):
    
        routerCounter = 0

        nRooms = building["nRooms"]
        xOffset = -SIDE - x_hall + counter*buildingDist

        for i in range(0, nRooms):

                # Compute the room centroid
            if i % 2 == 0:
                xOffset += SIDE + x_hall 
            yOffset =  0  if i % 2 == 0 else SIDE + y_hall

            nSensorsPerRoom = building["nSensorsPerRoom"]


            # Add senors in top left and bottom right corners
            motes += generateMote(templatePath, str(idCounter), xOffset-SIDE/2, yOffset-SIDE/2, "mtype840")
            idCounter += 1
            motes += generateMote(templatePath, str(idCounter), xOffset+SIDE/2, yOffset+SIDE/2, "mtype840")
            idCounter += 1

            if nSensorsPerRoom > 2:
                motes += generateMote(templatePath, str(idCounter), xOffset-SIDE/2, yOffset+SIDE/2, "mtype840")
                idCounter += 1

            if nSensorsPerRoom > 3:
                motes += generateMote(templatePath, str(idCounter), xOffset+SIDE/2, yOffset-SIDE/2, "mtype840")
                idCounter += 1
						
            if i % 2 == 0 and (nRooms - i >= (4 if nRooms % 2 == 0 else 3) or routerCounter < 2):
                motes += generateMote(templatePath, str(idCounter), xOffset + SIDE/2 + x_hall/2, yOffset + SIDE/2 + y_hall/2, "mtype245")
                idCounter += 1
                routerCounter += 1
               
        

    f = open(simulationPath, "r")
    fileText = f.read()
    f.close()

    fileText = fileText.replace("<---INSERT MOTES HERE-->", motes)

    f = open("./test.csc", "w")
    f.write(fileText)
    f.close()
  

 
  

 
