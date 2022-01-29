package NoiseSimulationV2;

public class SensorObject {
    private int posX;
    private int posY;

    private double measuredNoise = 0;

    public double getMeasuredNoise() {
        return measuredNoise;
    }

    public SensorObject(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }
}
