package DataGenerators;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import com.NoiseSensors.NoiseReading;

import Callbacks.GeneratorCallback;

public class RandomDataGenerator extends DataGenerator {

    private final int maxNoiseLevel;
    private final int minNoiseLevel;
    private double x;
    private double y;

    public RandomDataGenerator(int maxNoiseLevel, int minNoiseLevel) {
        this.maxNoiseLevel = maxNoiseLevel;
        this.minNoiseLevel = minNoiseLevel;
        this.generateXY();
    }

    public void generateXY() {

        // initializing min and max values for coordinates of the sensor
        double minX = 0;
        double maxX = 100;
        double minY = 0;
        double maxY = 100;

        // generate a random X coordinate
        this.x = Math.floor(Math.random() *
                (maxX - minX + 1) +
                minX);

        // generate a random Y coordinates
        this.y = Math.floor(Math.random() *
                (maxY - minY + 1) +
                minY);

    }

    @Override
    public void generateData(Consumer<NoiseReading> callback) {

        Timer t = new Timer();

        t.schedule(new TimerTask() {
            @Override
            public void run() {

                NoiseReading noiseReading = new NoiseReading();

                // generate a random DB level
                int randomNoiseLevel = (int) Math.floor(Math.random() *
                        (maxNoiseLevel - minNoiseLevel + 1) + minNoiseLevel);
                noiseReading.setNoiseLevel(randomNoiseLevel);
                noiseReading.setX(x);
                noiseReading.setY(y);

                GeneratorCallback.applyFunction(noiseReading, callback);
            }
        }, 0, 1000);
    }

}
