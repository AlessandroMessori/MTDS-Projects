package NoiseSimulationV2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//    Input parameters:
//    P = number of people -> random generated?
//    V = number of vehicles -> random generated?
//    areaWidth, areaLength (W, L) = width and length of the (rectangular) region where individuals move (in meters)
//    Np = level of noise (dB) produced by each person -> a threshold
//    Nv = level of noise (dB) produced by each vehicle -> a threshold
//    Dp = distance (side of the square area, in meters) affected by the presence of a person
//    Dv = distance (side of the square area, in meters) affected by the presence of a vehicle
//    speedIndividual (Vp) = moving speed for an individual
//    speedVehicle (Vv) = moving speed for a vehicle
//    timeStep (t) = time step (in seconds): the simulation recomputes the position of people and vehicle, and the level of
//    noise of each square meter in the region with a temporal granularity of t (simulated) seconds

public class NoiseSimulation {

    private final int maxNoiseLevelPeople; //60 for a conversation
    private final int minNoiseLevelPeople; //20 lowest

    private final int maxNoiseLevelVehicles; //100 truck at 50mph
    private final int minNoiseLevelVehicles; //60 normal car at 50mph

    private double areaWidth;
    private double areaLength;

    private int timeStep;
    private double areaStep;

    private double posX = 0.0;
    private double posY = 0.0;

    /* A list of the sensor present in the area */
    private List<SensorObject> sensorList = new ArrayList<SensorObject>();

    public NoiseSimulation (int maxNoiseLevelPeople, int minNoiseLevelPeople,
                            int maxNoiseLevelVehicles, int minNoiseLevelVehicles,
                            int areaWidth, int areaLength, int timeStep) {
        this.maxNoiseLevelPeople = maxNoiseLevelPeople;
        this.minNoiseLevelPeople = minNoiseLevelPeople;
        this.maxNoiseLevelVehicles = maxNoiseLevelVehicles;
        this.minNoiseLevelVehicles = minNoiseLevelVehicles;
        this.areaWidth = areaWidth;
        this.areaLength = areaLength;
        this.timeStep = timeStep;
        this.areaStep = areaStep;
    }

    /* Generate a couple of NoiseVehicle and NoiseIndividual and move them according to their speed in the rectangle
       defined by areaLength and areaWidth;

       The new positions of the object should be computed at each timeStep seconds when the noise generated will be
       record in a text file containing the posX, posY and the value of noise in dB as a tuple (posX, posY, dB);

       The simulation should take into consideration when an object is out of the area;

       Question: should we consider "sensors" across the area or just record the noise level of the object?
    */


    /* Should create multiple file according to area */
    File simulationData = new File("SimulationData.txt");

    /* Create Individuals and Vehicles */

    /* Move objects for a period of time and collect data for each square meter*/


}
