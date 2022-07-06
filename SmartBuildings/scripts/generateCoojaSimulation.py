import json

filePath = "./config/simulation1.json"
templatePath = "./config/moteTemplate.csc"
simulationPath = "./config/simulationTemplate.csc"
listening_router = "./config/router_listening.csc"
room_building = "./config/room_building.txt"
ports = "./config/ports.txt"

  
motes = ""
listeners = ""
room_building_string = ""
portsText = ""

idCounter = 1
y_hall = 4.0
x_hall = 3.0
buildingDist = 50
buildingCounter = 1;
roomCounter = 1;


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
 
    for counter, building in enumerate(simConfig):

        nRooms = building["nRooms"]
        xOffset = -SIDE - x_hall + counter*buildingDist

        for i in range(0, nRooms):
        
            routerCounter = 0

                # Compute the room centroid
            if i % 2 == 0:
                xOffset += SIDE + x_hall 
            yOffset =  0  if i % 2 == 0 else SIDE + y_hall

            nSensorsPerRoom = building["nSensorsPerRoom"]


            # Add senors in top left and bottom right corners
            motes += generateMote(templatePath, str(idCounter), xOffset-SIDE/2, yOffset-SIDE/2, "mtype840")
            idCounter += 1
            room_building_string += (str(buildingCounter) + "," + str(roomCounter) + "\n")
            motes += generateMote(templatePath, str(idCounter), xOffset+SIDE/2, yOffset+SIDE/2, "mtype840")
            room_building_string += (str(buildingCounter) + "," + str(roomCounter) + "\n")
            idCounter += 1

            if nSensorsPerRoom > 2:
                motes += generateMote(templatePath, str(idCounter), xOffset-SIDE/2, yOffset+SIDE/2, "mtype840")
                room_building_string += (str(buildingCounter) + "," + str(roomCounter) + "\n")
                idCounter += 1

            if nSensorsPerRoom > 3:
                motes += generateMote(templatePath, str(idCounter), xOffset+SIDE/2, yOffset-SIDE/2, "mtype840")
                room_building_string += (str(buildingCounter) + "," + str(roomCounter) + "\n")
                idCounter += 1
						
            if i % 2 == 0 and (nRooms - i >= (4 if nRooms % 2 == 0 else 3) or routerCounter < 2):
                motes += generateMote(templatePath, str(idCounter), xOffset + SIDE/2 + x_hall/2, yOffset + SIDE/2 + y_hall/2, "mtype245")
                listeners += activateListeners(listening_router, idCounter - 1, idCounter)
                room_building_string += (str(buildingCounter) + "," + str(roomCounter) + "\n")
                portsText += (str(idCounter + 60000) + "\n")
                idCounter += 1
                
            roomCounter += 1
        roomCounter = 1 
        buildingCounter += 1
        
    portsText = portsText[:-1]
    
    f = open(ports, "w")
    f.write(portsText)
    f.close()
 
    f = open(room_building, "w");
    f.write(room_building_string)
    f.close()
		
    f = open(simulationPath, "r")
    fileText = f.read()
    f.close()

    fileText = fileText.replace("<---INSERT MOTES HERE-->", motes)
    fileText = fileText.replace("<---LISTENERS--->", listeners);

    f = open("./test.csc", "w")
    f.write(fileText)
    f.close()
  

 
  

 
