//write script here

var f = new java.io.FileReader("/home/user/projectstuff/2.txt")
var b = new java.io.BufferedReader(f)
var s=b.readLine()

allm = sim.getMotes()

var motes_iterator = 0
var counter = 0

while(true){
    GENERATE_MSG(5000, "sleep");
    YIELD_THEN_WAIT_UNTIL(msg.equals("sleep"))
    for(counter = 0; counter &lt; allm.length; counter++){
        var s=b.readLine()
        write(allm[counter], s)
    } 
}
