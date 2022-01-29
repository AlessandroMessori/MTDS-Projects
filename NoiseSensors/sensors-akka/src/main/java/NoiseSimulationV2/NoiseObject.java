package NoiseSimulationV2;

public abstract class NoiseObject {

    double posX;
    double posY;
    double speedObject;
    double noiseLevel;

    /* Move object across XY axis randomly with speedObject */
    public abstract void moveObject(double posX, double posY, double speedObject);

    /* Write noiseLevel to an output file after each MoveObject */
    public abstract double generateData(double noiseLevel);
}