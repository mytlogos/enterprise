package Enterprise.misc;

/**
 * Utility class.
 * At the moment its only function is to merge multiple Arrays.
 */
public class Utils {

    /**
     * Merges multiple integer Arrays with their position still intact.
     * <p>Example:</p>
     * {0,1,2} + {4,5,9}
     * <p>will be merged into:</p>
     * {0,1,2,4,5,9}
     *
     * @param arrays integer arrays to merge
     * @return integerArray - the merged array
     */
    public static int[] addIntArrays(int[]... arrays) {
        int position = 0;
        int arraySize = 0;
        for (int[] array : arrays) {
            arraySize += array.length;
        }
        int[] integerArray = new int[arraySize];

        for (int[] array: arrays) {
            for (int anInt : array) {
                integerArray[position] = anInt;
                position++;
            }
        }
        return integerArray;
    }
}
