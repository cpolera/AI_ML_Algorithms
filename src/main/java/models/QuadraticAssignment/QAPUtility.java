package models.QuadraticAssignment;

import java.util.Random;

public class QAPUtility {

    public static int[] shuffleArray(int[] array) {
        return shuffleArray(array, array.length);
    }

    public static int[] shuffleArray(int[] array, int limit) {
        Random random = new Random();
        random.nextInt();
        for (int i = 1; i < limit; i++) {
            int index = i;

            if (limit != array.length) {
                index = 1 + random.nextInt(array.length - 2);
            }
            int swapInt = index;
            while (swapInt == index) {
                swapInt = 1 + random.nextInt(array.length - 2);//index
            }
            int tempInt = array[index];//value to be replaced
            array[index] = array[swapInt];
            array[swapInt] = tempInt;
        }
        return array;
    }
}
