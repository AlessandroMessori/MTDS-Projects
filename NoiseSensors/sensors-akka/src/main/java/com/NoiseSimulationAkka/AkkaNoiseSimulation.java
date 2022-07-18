// kafka bridge for simulation
// script simulation for input data

package com.NoiseSimulationAkka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

//    ++wrong data

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

public class AkkaNoiseSimulation {

  public static void main(String[] args) {
    final double maxNoiseLevelPeople = 60.0; //60 for a conversation
    final double minNoiseLevelPeople = 20.0; //20 lowest

    final double maxNoiseLevelVehicles = 100.0; //100 truck at 50mph
    final double minNoiseLevelVehicles = 60.0; //60 normal car at 50mph

    int areaWidth = 1000;
    int areaLength = 1000;

    int timeStep = 1000;
    int timeStamp = 0;

    //44.49379649240147, 11.343115957526301 Bologna
    double posX1 = 44.4937;
    double posY1 = 11.3431;
    //44.51017702071235, 11.344914959441521 Bologna
    double posX2 = 44.5101;
    double posY2 = 11.3449;
    //44.53257343793887, 11.288898485205035 Bologna
    double posX3 = 44.5325;
    double posY3 = 11.2888;

    final int QUEUE_SIZE = 6;

    final ActorSystem sys = ActorSystem.create("System");

    final ActorRef simulatedSensor1 = sys.actorOf(Props.create(SensorObjectActor.class, posX1, posY1, QUEUE_SIZE,
            maxNoiseLevelPeople, minNoiseLevelPeople, maxNoiseLevelVehicles, minNoiseLevelVehicles, 564), "simulatedSensor1");
    final ActorRef simulatedSensor2 = sys.actorOf(Props.create(SensorObjectActor.class, posX2, posY2, QUEUE_SIZE,
          maxNoiseLevelPeople, minNoiseLevelPeople, maxNoiseLevelVehicles, minNoiseLevelVehicles, 278), "simulatedSensor2");
    final ActorRef simulatedSensor3 = sys.actorOf(Props.create(SensorObjectActor.class, posX3, posY3, QUEUE_SIZE,
          maxNoiseLevelPeople, minNoiseLevelPeople, maxNoiseLevelVehicles, minNoiseLevelVehicles, 729), "simulatedSensor3");
    HashMap<String, ActorRef> simulatedPersons = new HashMap<String, ActorRef>();
    HashMap<String, ActorRef> simulatedVehicles = new HashMap<String, ActorRef>();

    for(int i = 0 ; i < 3; i++){
      ActorRef newActor1 = sys.actorOf(Props.create(NoisePersonActor.class, posX1 + i * 0.001, posY1,
              ThreadLocalRandom.current().nextDouble(1, 2)), "simulatedPerson" + 3*i + 0);
      simulatedPersons.put("simulatedPerson" + 3*i + 0, newActor1);
      ActorRef newActor2 = sys.actorOf(Props.create(NoisePersonActor.class, posX2 + i * 0.001, posY2,
              ThreadLocalRandom.current().nextDouble(1, 2)), "simulatedPerson" + 3*i + 1);
      simulatedPersons.put("simulatedPerson" + 3*i + 1, newActor2);
      ActorRef newActor3 = sys.actorOf(Props.create(NoisePersonActor.class, posX3 + i * 0.001, posY3,
              ThreadLocalRandom.current().nextDouble(1, 2)), "simulatedPerson" + 3*i + 2);
      simulatedPersons.put("simulatedPerson" + 3*i + 2, newActor3);
    }

    for(int i = 0 ; i < 3; i++){
      ActorRef newActor1 = sys.actorOf(Props.create(NoiseVehicleActor.class, posX1 + i * 0.001, posY1,
              ThreadLocalRandom.current().nextDouble(1, 2)),"simulatedVehicle" + 3*i + 0);
      simulatedVehicles.put("simulatedVehicle" + 3*i + 0, newActor1);
      ActorRef newActor2 = sys.actorOf(Props.create(NoiseVehicleActor.class, posX2 + i * 0.001, posY2,
              ThreadLocalRandom.current().nextDouble(1, 2)),"simulatedVehicle" + 3*i + 1);
      simulatedVehicles.put("simulatedVehicle" + 3*i + 1, newActor2);
      ActorRef newActor3 = sys.actorOf(Props.create(NoiseVehicleActor.class, posX3 + i * 0.001, posY3,
              ThreadLocalRandom.current().nextDouble(1, 2)),"simulatedVehicle" + 3*i + 2);
      simulatedVehicles.put("simulatedVehicle" + 3*i + 2, newActor3);
    }


   while(true) {
      for (ActorRef actorP : simulatedPersons.values() ) {
        actorP.tell(new RequestReadingMessage(posX1, posY1, timeStamp), simulatedSensor1);
        actorP.tell(new RequestReadingMessage(posX2, posY2, timeStamp), simulatedSensor2);
        actorP.tell(new RequestReadingMessage(posX3, posY3, timeStamp), simulatedSensor3);
      }
      for (ActorRef actorV : simulatedVehicles.values()) {
        actorV.tell(new RequestReadingMessage(posX1, posY1, timeStamp), simulatedSensor1);
        actorV.tell(new RequestReadingMessage(posX2, posY2, timeStamp), simulatedSensor2);
        actorV.tell(new RequestReadingMessage(posX3, posY3, timeStamp), simulatedSensor3);
      }
      System.out.println("Checking all objects...");

      try {
        Thread.sleep(timeStep);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      timeStamp += 10;
    }
  }
}


