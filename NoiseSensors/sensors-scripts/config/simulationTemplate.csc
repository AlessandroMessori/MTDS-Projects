<?xml version="1.0" encoding="UTF-8"?>

<simconf>

  <project EXPORT="discard">[APPS_DIR]/mrm</project>

  <project EXPORT="discard">[APPS_DIR]/mspsim</project>

  <project EXPORT="discard">[APPS_DIR]/avrora</project>

  <project EXPORT="discard">[APPS_DIR]/serial_socket</project>

  <project EXPORT="discard">[APPS_DIR]/powertracker</project>

  <simulation>

    <title>My simulation</title>

    <speedlimit>1.0</speedlimit>

    <randomseed>123456</randomseed>

    <motedelay_us>1000000</motedelay_us>

    <radiomedium>

      org.contikios.cooja.radiomediums.UDGM

      <transmitting_range>50.0</transmitting_range>

      <interference_range>100.0</interference_range>

      <success_ratio_tx>1.0</success_ratio_tx>

      <success_ratio_rx>1.0</success_ratio_rx>

    </radiomedium>

    <events>

      <logoutput>40000</logoutput>

    </events>

    <motetype>

      org.contikios.cooja.mspmote.SkyMoteType

      <identifier>sky1</identifier>

      <description>Sky Mote Type #sky1</description>

      <source EXPORT="discard">[CONTIKI_DIR]/examples/rpl-border-router/border-router.c</source>

      <commands EXPORT="discard">make border-router.sky TARGET=sky</commands>

      <firmware EXPORT="copy">[CONTIKI_DIR]/examples/rpl-border-router/border-router.sky</firmware>

      <moteinterface>org.contikios.cooja.interfaces.Position</moteinterface>

      <moteinterface>org.contikios.cooja.interfaces.RimeAddress</moteinterface>

      <moteinterface>org.contikios.cooja.interfaces.IPAddress</moteinterface>

      <moteinterface>org.contikios.cooja.interfaces.Mote2MoteRelations</moteinterface>

      <moteinterface>org.contikios.cooja.interfaces.MoteAttributes</moteinterface>

      <moteinterface>org.contikios.cooja.mspmote.interfaces.MspClock</moteinterface>

      <moteinterface>org.contikios.cooja.mspmote.interfaces.MspMoteID</moteinterface>

      <moteinterface>org.contikios.cooja.mspmote.interfaces.SkyButton</moteinterface>

      <moteinterface>org.contikios.cooja.mspmote.interfaces.SkyFlash</moteinterface>

      <moteinterface>org.contikios.cooja.mspmote.interfaces.SkyCoffeeFilesystem</moteinterface>

      <moteinterface>org.contikios.cooja.mspmote.interfaces.Msp802154Radio</moteinterface>

      <moteinterface>org.contikios.cooja.mspmote.interfaces.MspSerial</moteinterface>

      <moteinterface>org.contikios.cooja.mspmote.interfaces.SkyLED</moteinterface>

      <moteinterface>org.contikios.cooja.mspmote.interfaces.MspDebugOutput</moteinterface>

      <moteinterface>org.contikios.cooja.mspmote.interfaces.SkyTemperature</moteinterface>

    </motetype>

    <motetype>

      org.contikios.cooja.contikimote.ContikiMoteType

      <identifier>mtype409</identifier>

      <description>Cooja Mote Type #2</description>

      <source>[CONTIKI_DIR]/examples/mqtt-demo/mqtt-demo.c</source>

      <commands>make mqtt-demo.cooja TARGET=cooja</commands>

      <moteinterface>org.contikios.cooja.interfaces.Position</moteinterface>

      <moteinterface>org.contikios.cooja.interfaces.Battery</moteinterface>

      <moteinterface>org.contikios.cooja.contikimote.interfaces.ContikiVib</moteinterface>

      <moteinterface>org.contikios.cooja.contikimote.interfaces.ContikiMoteID</moteinterface>

      <moteinterface>org.contikios.cooja.contikimote.interfaces.ContikiRS232</moteinterface>

      <moteinterface>org.contikios.cooja.contikimote.interfaces.ContikiBeeper</moteinterface>

      <moteinterface>org.contikios.cooja.interfaces.RimeAddress</moteinterface>

      <moteinterface>org.contikios.cooja.contikimote.interfaces.ContikiIPAddress</moteinterface>

      <moteinterface>org.contikios.cooja.contikimote.interfaces.ContikiRadio</moteinterface>

      <moteinterface>org.contikios.cooja.contikimote.interfaces.ContikiButton</moteinterface>

      <moteinterface>org.contikios.cooja.contikimote.interfaces.ContikiPIR</moteinterface>

      <moteinterface>org.contikios.cooja.contikimote.interfaces.ContikiClock</moteinterface>

      <moteinterface>org.contikios.cooja.contikimote.interfaces.ContikiLED</moteinterface>

      <moteinterface>org.contikios.cooja.contikimote.interfaces.ContikiCFS</moteinterface>

      <moteinterface>org.contikios.cooja.contikimote.interfaces.ContikiEEPROM</moteinterface>

      <moteinterface>org.contikios.cooja.interfaces.Mote2MoteRelations</moteinterface>

      <moteinterface>org.contikios.cooja.interfaces.MoteAttributes</moteinterface>

      <symbols>false</symbols>

    </motetype>

    <---INSERT MOTES HERE-->


  </simulation>

  <plugin>

    org.contikios.cooja.plugins.SimControl

    <width>280</width>

    <z>3</z>

    <height>160</height>

    <location_x>400</location_x>

    <location_y>0</location_y>

  </plugin>

  <plugin>

    org.contikios.cooja.plugins.Visualizer

    <plugin_config>

      <moterelations>true</moterelations>

      <skin>org.contikios.cooja.plugins.skins.IDVisualizerSkin</skin>

      <skin>org.contikios.cooja.plugins.skins.GridVisualizerSkin</skin>

      <skin>org.contikios.cooja.plugins.skins.TrafficVisualizerSkin</skin>

      <skin>org.contikios.cooja.plugins.skins.UDGMVisualizerSkin</skin>

      <viewport>3.9372001196801047 0.0 0.0 3.9372001196801047 68.06901846107216 98.01881458691244</viewport>

    </plugin_config>

    <width>400</width>

    <z>2</z>

    <height>400</height>

    <location_x>1</location_x>

    <location_y>1</location_y>

  </plugin>

  <plugin>

    org.contikios.cooja.plugins.LogListener

    <plugin_config>

      <filter />

      <formatted_time />

      <coloring />

    </plugin_config>

    <width>1255</width>

    <z>0</z>

    <height>240</height>

    <location_x>400</location_x>

    <location_y>160</location_y>

  </plugin>

  <plugin>

    org.contikios.cooja.plugins.TimeLine

    <plugin_config>

      <mote>0</mote>

      <mote>1</mote>

      <mote>2</mote>

      <showRadioRXTX />

      <showRadioHW />

      <showLEDs />

      <zoomfactor>500.0</zoomfactor>

    </plugin_config>

    <width>1655</width>

    <z>1</z>

    <height>166</height>

    <location_x>0</location_x>

    <location_y>727</location_y>

  </plugin>

  <plugin>

    org.contikios.cooja.plugins.Notes

    <plugin_config>

      <notes>Enter notes here</notes>

      <decorations>true</decorations>

    </plugin_config>

    <width>975</width>

    <z>5</z>

    <height>160</height>

    <location_x>680</location_x>

    <location_y>0</location_y>

  </plugin>

  <plugin>

    org.contikios.cooja.serialsocket.SerialSocketServer

    <mote_arg>0</mote_arg>

    <plugin_config>

      <port>60001</port>

      <bound>true</bound>

    </plugin_config>

    <width>362</width>

    <z>4</z>

    <height>116</height>

    <location_x>710</location_x>

    <location_y>30</location_y>

  </plugin>

</simconf>
<!--
var config = new java.io.FileReader("/home/user/Desktop/data/simulation1.json")
var b = new java.io.BufferedReader(config);

var configStr = "";
var line = b.readLine();

while (line) {
    configStr += line;
    line = b.readLine();
}

b.close();

var readingFiles = []
var contikiRegions = JSON.parse(configStr)
var sensorCounter = 0;

for (var i=0; i<contikiRegions.length; i++) {
    if (contikiRegions[i].virtualSensors) {
       var regionName = contikiRegions[i].regionName;  
       
       var currentFR1 = new java.io.FileReader("/home/user/Desktop/data/"+regionName+"/1.csv")
       var file1 = new java.io.BufferedReader(currentFR1);
       
       var currentFR2 = new java.io.FileReader("/home/user/Desktop/data/"+regionName+"/2.csv")
       var file2 = new java.io.BufferedReader(currentFR2); 
       
       readingFiles.push(null);
       readingFiles.push(file1);
       readingFiles.push(file2);
       sensorCounter += 3;
    } 
}

var counter = 0;
var allm = sim.getMotes();
var waitTime = 10000 / sensorCounter;

while (true) {
    
  GENERATE_MSG(waitTime, "sleep"); //Wait 
  YIELD_THEN_WAIT_UNTIL(msg.equals("sleep"));
  
  
  var currentIndex = counter % sensorCounter;
  
  if (readingFiles[currentIndex]) {
      var currentLine = readingFiles[currentIndex].readLine();
  
      log.log(currentLine);
      write(allm[currentIndex], currentLine);           
      counter++;    
  }
  

}-->