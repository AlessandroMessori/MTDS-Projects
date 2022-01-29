package NoiseSimulationV2;

public class NoiseVehicle extends NoiseObject{
    double posX;
    double posY;
    double speedObject;
    double noiseLevel;

    public NoiseVehicle(double posX, double posY, double speedObject, double noiseLevel) {
        this.posX = posX;
        this.posY = posY;
        this.speedObject = speedObject;
        this.noiseLevel = noiseLevel;
    }

    @Override
    public void moveObject(double posX, double posY, double speedObject) {

    }

    @Override
    public double generateData(double noiseLevel) {
        return 0;
    }
}
