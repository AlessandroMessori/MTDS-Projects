package NoiseSimulation;

abstract class NoiseObject {

    double posX;
    double posY;

    public abstract void moveObject(int posX, int posY);
    public abstract void generateData();

}