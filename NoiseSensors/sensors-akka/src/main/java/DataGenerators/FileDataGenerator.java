package DataGenerators;

import com.NoiseSensors.NoiseReading;

import java.io.*;
import java.util.Scanner;

public class FileDataGenerator extends DataGenerator {

    File file;
    Scanner scanner;

    public FileDataGenerator() {
        this.file = new File("./NoiseSensors/sensors-akka/src/main/resources/Datasets/measurement_1.txt");
        try {
            this.scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }



    @Override
    public void generateData(Runnable callback, NoiseReading noiseReading) {
        String[] string = scanner.nextLine().split("\t");
        noiseReading.setX(Double.parseDouble(string[0]));
        noiseReading.setY(Double.parseDouble(string[1]));
        noiseReading.setNoiseLevel(Double.parseDouble(string[2]));
        callback.run();
    }
}
