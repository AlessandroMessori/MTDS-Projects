package DataGenerators;

import com.NoiseSensors.NoiseReading;

import Callbacks.GeneratorCallback;

import java.io.*;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class FileDataGenerator extends DataGenerator {

    File file;
    Scanner scanner;

    public FileDataGenerator() {
        this.file = new File("../sensors-akka/src/main/resources/Datasets/measurement_1.txt");
        try {
            this.scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }



    @Override
    public void generateData(Consumer<NoiseReading> callback) {

        Timer t = new Timer();

        t.schedule(new TimerTask() {
            @Override
            public void run() {

                NoiseReading noiseReading = new NoiseReading();

                String[] string = scanner.nextLine().split("\t");
                noiseReading.setX(Double.parseDouble(string[0]));
                noiseReading.setY(Double.parseDouble(string[1]));
                noiseReading.setNoiseLevel(Double.parseDouble(string[2]));

                GeneratorCallback.applyFunction(noiseReading, callback);
            }
        }, 0, 5000);
    }
}
