package DataGenerators;

import java.util.function.Consumer;

import com.NoiseSensors.NoiseReading;

abstract class DataGenerator {

    public abstract void generateData(Consumer<NoiseReading> callback);
}

