var f = new java.io.FileReader("/home/user/projectstuff/measure1.txt")
var b = new java.io.BufferedReader(f);
var s=b.readLine();

allm = sim.getMotes()

var counter = 0;

while(true){
    GENERATE_MSG(1000, "sleep"); //Wait for two sec
    YIELD_THEN_WAIT_UNTIL(msg.equals("sleep"));
    for(counter = 0; counter < allm.length; counter++){
        var s=b.readLine();
        write(allm[counter], s);
    } 
}
