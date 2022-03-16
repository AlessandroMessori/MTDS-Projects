/*This script sends a message to one mote with ID 1 every two seconds*/


while(true){
    GENERATE_MSG(10000, "sleep"); //Wait for two sec
    YIELD_THEN_WAIT_UNTIL(msg.equals("sleep"));
    write(sim.getMoteWithID(1), "some string");
}
