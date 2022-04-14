package com.NoiseSimulationAkka;

import akka.actor.AbstractActor;

public abstract class NoiseObject extends AbstractActor {
    int posX;
    int posY;
    double speedObject;

    /* Move object across XY axis randomly with speedObject */
    public abstract void moveObject();

    /* Write noiseLevel to an output file after each MoveObject */
    public abstract double generateData();

}
