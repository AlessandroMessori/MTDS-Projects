package Callbacks;

import java.util.function.Consumer;

import com.NoiseSensors.NoiseReading;

public class GeneratorCallback {
    public static void applyFunction(NoiseReading noiseReading, Consumer<NoiseReading> consumer){
        consumer.accept(noiseReading);
    }
}