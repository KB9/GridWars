package cern.ais.gridwars.MachineLearning;

import java.util.Random;

/**
 * Created by kavan on 04/04/17.
 */
public class Utils {

    // Returns a random integer between x and y
    public static int randInt(int x, int y) {
        return new Random().nextInt(Integer.MAX_VALUE - 1) % (y - x + 1) + x;
    }

    // Returns a random float between 0 and 1
    public static double randFloat() {
        return new Random().nextDouble();
    }

    // Returns a random float in the range -1 < n < 1
    public static double randomClamped() {
        return randFloat() - randFloat();
    }
}
