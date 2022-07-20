import json
  
# Opening JSON file
f = open('./config.json')
f2 = open("./Actuators.json")

buildings = json.loads(f.read())
flows = f2.read()

for building in buildings:
    for nfloor in range(building["nFloors"]):
        for nroom in range(building["nRoomsPerFloor"]):
           currentTopic = "building_{}/floor_{}/room_{}/sensors".format(building["buildingId"], nfloor, nroom+1)

           currentFlow = flows.replace("iot/noise/json", currentTopic)

           with open("generatedFlows/"+currentTopic.replace("/","_")+".json", "w") as outfile:
            outfile.write(currentFlow)
  
# Closing file
f.close()
f2.close()
print(flows)