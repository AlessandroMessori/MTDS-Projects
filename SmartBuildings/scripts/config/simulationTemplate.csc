<?xml version="1.0" encoding="UTF-8"?>

<simconf>

  <project EXPORT="discard">[APPS_DIR]/mrm</project>

  <project EXPORT="discard">[APPS_DIR]/mspsim</project>

  <project EXPORT="discard">[APPS_DIR]/avrora</project>

  <project EXPORT="discard">[APPS_DIR]/serial_socket</project>

  <project EXPORT="discard">[APPS_DIR]/powertracker</project>

  <simulation>

    <title>project_2</title>

    <speedlimit>1.0</speedlimit>

    <randomseed>123456</randomseed>

    <motedelay_us>1000000</motedelay_us>

    <radiomedium>

      org.contikios.cooja.radiomediums.UDGM

      <transmitting_range>700.0</transmitting_range>

      <interference_range>1000.0</interference_range>

      <success_ratio_tx>1.0</success_ratio_tx>

      <success_ratio_rx>1.0</success_ratio_rx>

    </radiomedium>

    <events>

      <logoutput>40000</logoutput>

    </events>

    <motetype>

      org.contikios.cooja.contikimote.ContikiMoteType

      <identifier>mtype245</identifier>

      <description>border router</description>

      <source>[CONTIKI_DIR]/examples/rpl-border-router/border-router.c</source>

      <commands>make border-router.cooja TARGET=cooja</commands>

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

    <motetype>

      org.contikios.cooja.contikimote.ContikiMoteType

      <identifier>mtype840</identifier>

      <description>iot sensor</description>

      <source>[CONTIKI_DIR]/examples/iot-sensor/iot-sensor.c</source>

      <commands>make iot-sensor.cooja TARGET=cooja</commands>

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

    <z>2</z>

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

      <viewport>1.3267234648544286 0.0 0.0 1.3267234648544286 173.4967754846358 96.24782638795679</viewport>

    </plugin_config>

    <width>400</width>

    <z>0</z>

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

    <z>3</z>

    <height>495</height>

    <location_x>171</location_x>

    <location_y>487</location_y>

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

  <---LISTENERS--->

  <plugin>

    org.contikios.cooja.plugins.ScriptRunner

    <plugin_config>

      <script>var f = new java.io.FileReader("/home/user/GIT/MTDS-Projects/SmartBuildings/resources/temp_hum.txt")

var b = new java.io.BufferedReader(f)

var s=b.readLine()



var f1 = new java.io.FileReader("/home/user/GIT/MTDS-Projects/SmartBuildings/scripts/config/room_building.txt")

var b1 = new java.io.BufferedReader(f1)



allm = sim.getMotes()



var motes_iterator = 0

var counter = 0



var rooms_buildings = []

var string = b1.readLine()



GENERATE_MSG(60000, "sleep");

YIELD_THEN_WAIT_UNTIL(msg.equals("sleep"))



while(string){

    log.log(string + '\n')

    rooms_buildings.push(string)

    string = b1.readLine()

    counter++

}



/*for(counter = 0; counter &lt; allm.length; counter++){

		GENERATE_MSG(1000, "sleep");

    YIELD_THEN_WAIT_UNTIL(msg.equals("sleep"))

    var s1 =b1.readLine()

    write(allm[counter], s1)

}*/



while(true){

    GENERATE_MSG(8000, "sleep");

    YIELD_THEN_WAIT_UNTIL(msg.equals("sleep"))

    for(counter = 0; counter &lt; allm.length; counter++){

        var s=b.readLine()

        write(allm[counter], s + "," + rooms_buildings[counter])

        log.log(s + "," + rooms_buildings[counter] + "\n")

    } 

}</script>

      <active>false</active>

    </plugin_config>

    <width>904</width>

    <z>1</z>

    <height>700</height>

    <location_x>802</location_x>

    <location_y>88</location_y>

  </plugin>

</simconf>


