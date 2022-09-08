import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;

public class ComplexExamples {

    static class Person {
        final int id;

        final String name;

        Person(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Person person)) return false;
            return getId() == person.getId() && getName().equals(person.getName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId(), getName());
        }
    }

    private static Person[] RAW_DATA = new Person[]{
            new Person(0, "Harry"),
            new Person(0, "Harry"), // дубликат
            new Person(1, "Harry"), // тёзка
            new Person(2, "Harry"),
            new Person(3, "Emily"),
            new Person(4, "Jack"),
            new Person(4, "Jack"),
            new Person(5, "Amelia"),
            new Person(5, "Amelia"),
            new Person(6, "Amelia"),
            new Person(7, "Amelia"),
            new Person(8, "Amelia")
    };

    /*  Raw data:

    0 - Harry
    0 - Harry
    1 - Harry
    2 - Harry
    3 - Emily
    4 - Jack
    4 - Jack
    5 - Amelia
    5 - Amelia
    6 - Amelia
    7 - Amelia
    8 - Amelia

    **************************************************

    Duplicate filtered, grouped by name, sorted by name and id:

    Amelia:
    1 - Amelia (5)
    2 - Amelia (6)
    3 - Amelia (7)
    4 - Amelia (8)
    Emily:
    1 - Emily (3)
    Harry:
    1 - Harry (0)
    2 - Harry (1)
    3 - Harry (2)
    Jack:
    1 - Jack (4)
 */

    /**
     * Removes nulls and duplicate Persons.
     * Counts number of Persons with the same name.
     * Returns TreeMap, which is naturally ordered by key (by default), or can be sorted with comparator.
     * If argument is null returns empty TreeMap.
     */
    public static TreeMap<String, Long> countNames(Person[] array) {
        if (array == null)
            return new TreeMap<>();
        return Arrays.stream(array)
                       .filter(Objects::nonNull)
                       .distinct()
                       .collect(Collectors.groupingBy(
                               Person::getName,
                               TreeMap::new,
                               mapping(Person::getId, Collectors.counting()))
                       );
    }

    /**
     * Finds the first pair of numbers in the array, which in total gives the number "target" passed as an argument.
     * If it doesn't find "target" or original array is null it returns an empty array.
     */
    public static int[] findPairNLogN(int[] originalArray, int targetSum) { // algorithm complexity: O(n * Log n)
        if (originalArray == null)
            return new int[]{};
        // We need extra memory for the sorted auxiliary array to find exactly the first pair
        int[] sortedArray = Arrays.stream(originalArray).sorted().toArray(); // Java quick sort O(n * Log n)
        for (int i = 0; i < originalArray.length - 1; i++) {
            // First number - from the ORIGINAL array:
            int firstNum = originalArray[i];
            int secondNum = targetSum - firstNum;
            // Search for the second number in the SORTED auxiliary array:
            int indexOfSecond = Arrays.binarySearch(sortedArray, secondNum); // binarySearch O(Log n)
            if (indexOfSecond > -1) {
                // We need to check that such a number (first = second) occurs in the sorted array at least 2 times
                if (firstNum == secondNum
                            && !checkForDuplicate(sortedArray, indexOfSecond))
                    return new int[]{};
                else
                    return new int[]{firstNum, secondNum};
            }
        }
        return new int[]{};
    }

    /**
     * Checks that the sorted array contains more than one number equal to the number with the specified index
     */
    private static boolean checkForDuplicate(int[] array, int index) {
        int number = array[index];
        // Need a check on the array boundary
        if (index < array.length - 1 && array[index + 1] == number)
            return true;
        else if (index > 0 && array[index - 1] == number)
            return true;
        else
            return false;
    }

    /**
     * Finds the first pair of numbers in the array, which in total gives the number "target" passed as an argument.
     * If it doesn't find "target" or original array is null it returns an empty array.
     */
    public static int[] findPairQuadratic(int[] array, int target) { // algorithm complexity: O(n * n)
        if (array == null)
            return new int[]{};
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (array[i] + array[j] == target)
                    return new int[]{array[i], array[j]};
            }
        }
        return new int[]{};
    }

    /**
     * Checks that the characters in the string 'word' are in the same order as in the string 'section'.
     * If 'word' argument is empty or null, it always returns true.
     * If the 'section' argument is empty or null, but the 'word' argument is not empty, it returns false.
     */
    public static boolean fuzzySearch(String word, String section) {
        if (word == null || word.length() == 0)       return true;
        if (section == null || section.length() == 0) return false;
        // Quick check that 'word' and 'section' are the same string literal in the string pool:
        if (word == section)                          return true;

        // algorithm complexity:
        //      O(wl + sl) where wl = word.length, sl = section.length - in the case when the result is 'true'
        //      O(sl) - in the best case (when the first character of the 'word' is missing in the 'section')
        for (int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i); // Very fast "index in string" mechanism in java
            int letterIndex = section.indexOf(letter);
            if (letterIndex == -1)
                return false;
            section = section.substring(letterIndex + 1); // Very fast substring mechanism in java
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println("Raw data:");
        System.out.println();

        for (Person person : RAW_DATA) {
            System.out.println(person.id + " - " + person.name);
        }

        System.out.println();
        System.out.println("**************************************************");
        System.out.println();
        System.out.println("Duplicate filtered, grouped by name, sorted by name and id:");
        System.out.println();

        /*
        Task1
            Убрать дубликаты, отсортировать по идентификатору, сгруппировать по имени

            Что должно получиться Key: Amelia
                Value:4
                Key: Emily
                Value:1
                Key: Harry
                Value:3
                Key: Jack
                Value:1
         */

        testNames(); // Unit Test
        countNames(RAW_DATA).forEach((key, value) ->
                                             System.out.printf("Key: %s \nValue: %d\n", key, value));

        /*
        Task2

            [3, 4, 2, 7], 10 -> [3, 7] - вывести пару именно в скобках, которые дают сумму - 10
         */

        testPairs(); // Unit Test
        int[] initialArray = {3, 4, 2, 7};
        int targetSum = 10;

// ЛИНЕЙНО-ЛОГАРИФМИЧЕСКИЙ алгоритм с дополнительной памятью, равной размеру исходного массива:
        // Плюсы: гарантированна линейно-логарифмическая производительность (также нужны затраты на копирование массива)
        //       - в лучшем случае n * log n
        //       - в худшем случае 2n * log n
        // Минусы: нужна дополнительная память для соблюдения требования о поиске именно первой пары
        int[] resultPair = findPairNLogN(initialArray, targetSum);
        System.out.println("N Log N algorithm:");
        System.out.println(Arrays.toString(resultPair));

// КВАДРАТИЧНЫЙ алгоритм без дополнительной памяти:
        // Плюсы:
        //      - Не нужна дополнительная память
        //      - Простая реализация
        // Минусы:
        //      - Средняя производительность квадратичная (n/2 * n/2)
        //      - В худшем случае (когда искомая пара в самом конце) производительность n * n
        //      - Квадратичный алгоритм не приемлем для больших массивов.
        resultPair = findPairQuadratic(initialArray, targetSum);
        System.out.println("Quadratic algorithm:");
        System.out.println(Arrays.toString(resultPair));

        /*
        Task3
            Реализовать функцию нечеткого поиска

                    fuzzySearch("car", "ca6$$#_rtwheel"); // true
                    fuzzySearch("cwhl", "cartwheel"); // true
                    fuzzySearch("cwhee", "cartwheel"); // true
                    fuzzySearch("cartwheel", "cartwheel"); // true
                    fuzzySearch("cwheeel", "cartwheel"); // false
                    fuzzySearch("lw", "cartwheel"); // false
         */

        testFuzzySearch(); // Unit Test
        System.out.println(fuzzySearch("car", "ca6$$#_rtwheel"));
        System.out.println(fuzzySearch("cwhl", "cartwheel"));
        System.out.println(fuzzySearch("cwhee", "cartwheel"));
        System.out.println(fuzzySearch("cartwheel", "cartwheel"));
        System.out.println(fuzzySearch("cwheeel", "cartwheel"));
        System.out.println(fuzzySearch("lw", "cartwheel"));
    }

    /* T E S T S */

    // Tests for Task1
    private static void testNames() {
        Person A0 = new Person(0, "A");
        Person A1 = new Person(1, "A");
        Person B2 = new Person(2, "B");
        Person B3 = new Person(3, "B");
        Person C4 = new Person(4, "C");
        Person C5 = new Person(5, "C");
        Person C6 = new Person(6, "C");
        Person D7 = new Person(7, "D");
        Person D8 = new Person(8, "D");
        Person E9 = new Person(9, "E");

        TreeMap<String, Long> resultMap;

        // Tests for duplicates filter (Task1):

        Person[] duplicates1 = {A0, A0, A0, A0, A0};
        resultMap = countNames(duplicates1);
        assert (resultMap.get(A0.name) == 1) : "Wrong Persons duplicates filter";

        Person[] duplicates2 = {A0, A0, A0, A0, A0, A1, A1, A1, A1};
        resultMap = countNames(duplicates2);
        assert (resultMap.get(A0.name) == 2) : "Wrong Persons duplicates filter";
        assert (resultMap.get(A1.name) == 2) : "Wrong Persons duplicates filter";

        System.out.println("Persons: Test passed - duplicates filter");

        // Tests for nulls filter (Task1):

        Person[] nulls1 = {null, null, null};
        resultMap = countNames(nulls1);
        assert (resultMap.size() == 0) : "Wrong Persons nulls filter";

        Person[] nulls2 = {null, A1, null, B2, null};
        resultMap = countNames(nulls2);
        assert (resultMap.size() == 2) : "Wrong Persons nulls filter";

        System.out.println("Persons: Test passed - nulls filter");

        // Test for correct sorting of the result (Task1):

        Person[] full = {D8, C5, B3, A1, C4, B2, C4, C4, C6, A1, D7, A1, C4, B2, C4, E9, C6, A0};
        resultMap = countNames(full);
        StringBuilder sb = new StringBuilder();
        resultMap.forEach((key, value) -> sb.append(key).append(value));
        assert (sb.toString().equals("A2B2C3D2E1")) : "Wrong Persons result map sort";

        System.out.println("Persons: Test passed - map sorted");
    }

    // Tests for Task2
    private static void testPairs() {
        int[] array;
        int[] result;

        // Empty array -> empty result
        array = new int[]{};
        result = findPairNLogN(array, 10);
        assert (Arrays.equals(result, new int[]{})) : "Wrong empty array result";
        result = findPairQuadratic(array, 10);
        assert (Arrays.equals(result, new int[]{})) : "Wrong empty array result";

        // Should not find
        array = new int[]{3, 7, 2, 4};
        result = findPairNLogN(array, 15);
        assert (Arrays.equals(result, new int[]{})) : "Wrong result. Should be {}";
        result = findPairQuadratic(array, 15);
        assert (Arrays.equals(result, new int[]{})) : "Wrong result. Should be {}";

        // Should not find (only one number)
        array = new int[]{3, 7, 2, 4, 15};
        result = findPairNLogN(array, 15);
        assert (Arrays.equals(result, new int[]{})) : "Wrong result. Should be {}";
        result = findPairQuadratic(array, 15);
        assert (Arrays.equals(result, new int[]{})) : "Wrong result. Should be {}";

        // Pair at the beginning
        array = new int[]{3, 7, 2, 4};
        result = findPairNLogN(array, 10);
        assert (result[0] == 3 && result[1] == 7) : "Wrong pair found in {3, 7, 2, 4}. Should be {3, 7}";
        result = findPairQuadratic(array, 10);
        assert (result[0] == 3 && result[1] == 7) : "Wrong pair found in {3, 7, 2, 4}. Should be {3, 7}";

        // Pair at the end
        array = new int[]{7, 3, 2, 6, 5};
        result = findPairNLogN(array, 11);
        assert (result[0] == 6 && result[1] == 5) : "Wrong pair found in {7, 3, 2, 6, 5}. Should be {6, 5}";
        result = findPairQuadratic(array, 11);
        assert (result[0] == 6 && result[1] == 5) : "Wrong pair found in {7, 3, 2, 6, 5}. Should be {6, 5}";

        // One number at the beginning, the other at the end
        array = new int[]{7, 15, 4, 3, 2, 6, 8};
        result = findPairNLogN(array, 15);
        assert (result[0] == 7 && result[1] == 8) : "Wrong pair found in {7, 15, 4, 3, 2, 6, 8}. Should be {7, 8}";
        result = findPairQuadratic(array, 15);
        assert (result[0] == 7 && result[1] == 8) : "Wrong pair found in {7, 15, 4, 3, 2, 6, 8}. Should be {7, 8}";

        // Pair in the middle
        array = new int[]{7, 9, 3, 6, 5, 1};
        result = findPairNLogN(array, 15);
        assert (result[0] == 9 && result[1] == 6) : "Wrong pair found in {7, 9, 3, 6, 5, 1}. Should be {9, 6}";
        result = findPairQuadratic(array, 15);
        assert (result[0] == 9 && result[1] == 6) : "Wrong pair found in {7, 9, 3, 6, 5, 1}. Should be {9, 6}";

        // Pair of two same numbers at the beginning
        array = new int[]{7, 1, 9, 3, 6, 5, 1};
        result = findPairNLogN(array, 2);
        assert (result[0] == 1 && result[1] == 1) : "Wrong pair found in {7, 1, 9, 3, 6, 5, 1}. Should be {1, 1}";
        result = findPairQuadratic(array, 2);
        assert (result[0] == 1 && result[1] == 1) : "Wrong pair found in {7, 1, 9, 3, 6, 5, 1}. Should be {1, 1}";

        // Pair of two same numbers at the end
        array = new int[]{7, 10, 9, 3, 6, 5, 10};
        result = findPairNLogN(array, 20);
        assert (result[0] == 10 && result[1] == 10) : "Wrong pair found in {7, 10, 9, 3, 6, 5, 10}. Should be {10, 10}";
        result = findPairQuadratic(array, 20);
        assert (result[0] == 10 && result[1] == 10) : "Wrong pair found in {7, 10, 9, 3, 6, 5, 10}. Should be {10, 10}";

        // Pair of two same numbers in the middle
        array = new int[]{7, 10, 9, 3, 6, 5, 1, 9};
        result = findPairNLogN(array, 18);
        assert (result[0] == 9 && result[1] == 9) : "Wrong pair found in {7, 10, 9, 3, 6, 5, 1, 9}. Should be {9, 9}";
        result = findPairQuadratic(array, 18);
        assert (result[0] == 9 && result[1] == 9) : "Wrong pair found in {7, 10, 9, 3, 6, 5, 1, 9}. Should be {9, 9}";

        // 2 pairs. Should find first
        array = new int[]{7, 9, 3, 6, 5, 1};
        result = findPairNLogN(array, 10);
        assert (result[0] == 7 && result[1] == 3) : "Wrong pair found in {7, 9, 3, 6, 5, 1}. Should be {7, 3}";
        result = findPairQuadratic(array, 10);
        assert (result[0] == 7 && result[1] == 3) : "Wrong pair found in {7, 9, 3, 6, 5, 1}. Should be {7, 3}";

        System.out.println("\nFindPairs: Test passed");
    }

    // Tests for Task3
    private static void testFuzzySearch() {
        String word;
        String section;

        // Tests for EMPTY arguments (Task3):

        word = "";
        section = "ABC";
        assert (fuzzySearch(word, section)) : "Wrong fuzzySearch '' in 'ABC'. Should be true";

        word = "ABC";
        section = "";
        assert (!fuzzySearch(word, section)) : "Wrong fuzzySearch 'ABC' in ''. Should be false";

        word = "";
        section = "";
        assert (fuzzySearch(word, section)) : "Wrong fuzzySearch '' in ''. Should be true";
        
        word = null;
        section = "ABC";
        assert (fuzzySearch(word, section)) : "Wrong fuzzySearch null in 'ABC'. Should be true";

        word = "ABC";
        section = null;
        assert (!fuzzySearch(word, section)) : "Wrong fuzzySearch 'ABC' in null. Should be false";

        word = null;
        section = null;
        assert (fuzzySearch(word, section)) : "Wrong fuzzySearch null in null. Should be true";

        System.out.println("\nFuzzySearch: Test passed for empty arguments");

        // Tests for TRUE result (Task3):

        word = "ABC";
        section = "ABC";
        assert (fuzzySearch(word, section)) : "Wrong fuzzySearch 'ABC' in 'ABC'. Should be true";

        word = "ABC";
        section = "AABBCC";
        assert (fuzzySearch(word, section)) : "Wrong fuzzySearch 'ABC' in 'AABBCC'. Should be true";

        word = "AABBCC";
        section = "AABBCC";
        assert (fuzzySearch(word, section)) : "Wrong fuzzySearch 'AABBCC' in 'AABBCC'. Should be true";

        word = "ABC";
        section = "xyzABC";
        assert (fuzzySearch(word, section)) : "Wrong fuzzySearch 'ABC' in 'xyzABC'. Should be true";

        word = "ABC";
        section = "ABCxyz";
        assert (fuzzySearch(word, section)) : "Wrong fuzzySearch 'ABC' in 'ABCxyz'. Should be true";

        word = "ABC";
        section = "xyzAxyzBxyzCxyz";
        assert (fuzzySearch(word, section)) : "Wrong fuzzySearch 'ABC' in 'xyzAxyzBxyzCxyz'. Should be true";

        word = "AABBCC";
        section = "xyzAxyzAxyzBxyzBxyzCxyzCxyz";
        assert (fuzzySearch(word, section)) :
                "Wrong fuzzySearch 'AABBCC' in 'xyzAxyzAxyzBxyzBxyzCxyzCxyz'.Should be true";

        word = "AABBCC";
        section = "xAyAzAxByBzBxCyCzCx";
        assert (fuzzySearch(word, section)) : "Wrong fuzzySearch 'AABBCC' in 'xAyAzAxByBzBxCyCzCx'. Should be true";

        word = "AABBCC";
        section = "AyAzAxByBzBxCyCzC";
        assert (fuzzySearch(word, section)) : "Wrong fuzzySearch 'AABBCC' in 'AyAzAxByBzBxCyCzC'. Should be true";

        System.out.println("FuzzySearch: Test passed for true result");

        // Tests for FALSE result (Task3):

        word = "ABC";
        section = "AB";
        assert (!fuzzySearch(word, section)) : "Wrong fuzzySearch 'ABC' in 'AB'. Should be false";

        word = "ABC";
        section = "BC";
        assert (!fuzzySearch(word, section)) : "Wrong fuzzySearch 'ABC' in 'BC'. Should be false";

        word = "ABC";
        section = "AC";
        assert (!fuzzySearch(word, section)) : "Wrong fuzzySearch 'ABC' in 'AC'. Should be false";

        word = "AABBCC";
        section = "ABC";
        assert (!fuzzySearch(word, section)) : "Wrong fuzzySearch 'AABBCC' in 'ABC'. Should be false";

        // Test for FALSE with RANDOM 'word' (Task3):

        // 100_000 random combinations of characters 'A', 'B', 'C', 'D', 'E' with a length of 5,
        // except for the exact "ABCDE"
        char[] wordChars = new char[]{'A', 'B', 'C', 'D', 'E'};
        section = "ABCDE";
        for (int i = 0; i < 100_000; i++) {
            word = randomWord(wordChars, 5);

            if (word.equals(section)) continue;

            assert (!fuzzySearch(word, section)) :
                    "Wrong fuzzySearch 5 random A', 'B', 'C', 'D', 'E' in 'ABCDE'. Should be false";
        }

        // 100_000 random combinations of characters 'A', 'B', 'C', 'D', 'E'  with a length of 6,
        // but length of 'section' is 5
        wordChars = new char[]{'A', 'B', 'C', 'D', 'E'};
        section = "ABCDE";
        for (int i = 0; i < 100_000; i++) {
            word = randomWord(wordChars, 6);

            assert (!fuzzySearch(word, section)) :
                    "Wrong fuzzySearch 6 random A', 'B', 'C', 'D', 'E' in 'ABCDE'. Should be false";
        }

        System.out.println("FuzzySearch: Test passed for false result");
    }

    /**
     * Creates a 'word' as a random combination of characters
     *
     * @param wordChars An array of chars that the 'word' will consist of
     * @param wordSize  Size of the 'word'
     */
    private static String randomWord(char[] wordChars, int wordSize) {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < wordSize; j++) {
            int randomIndex = rnd.nextInt(wordChars.length);
            sb.append(wordChars[randomIndex]);
        }
        return sb.toString();
    }
}
