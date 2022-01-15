package DataGenerators;

import com.NoiseSensors.NoiseReading;

abstract class DataGenerator {

    public abstract void generateData(Runnable callback, NoiseReading noiseReading);

}
