var f = new java.io.FileReader("/home/user/Project-1/projectstuff/1.txt")
var b = new java.io.BufferedReader(f)
var s=b.readLine()

var f1 = new java.io.FileReader("/home/user/Project-1/projectstuff/3.txt")
var b1 = new java.io.BufferedReader(f1)
var s1=b1.readLine()

var f2 = new java.io.FileReader("/home/user/Project-1/projectstuff/4.txt")
var b2 = new java.io.BufferedReader(f2)
var s2=b2.readLine()

allm = sim.getMotes()

var motes_iterator = 0
var counter = 0

while(true){
    GENERATE_MSG(10000, "sleep");
    YIELD_THEN_WAIT_UNTIL(msg.equals("sleep"))
    var s=b.readLine()
    write(sim.getMoteWithID(2), s)
    GENERATE_MSG(50, "sleep");
    YIELD_THEN_WAIT_UNTIL(msg.equals("sleep"))
    var s1=b1.readLine()
    write(sim.getMoteWithID(3), s1)
    GENERATE_MSG(50, "sleep");
    YIELD_THEN_WAIT_UNTIL(msg.equals("sleep"))
    var s2=b2.readLine()
    write(sim.getMoteWithID(4), s2)
}
