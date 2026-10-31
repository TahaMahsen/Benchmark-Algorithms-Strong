import java.util.Arrays;
import java.util.Random;

/**
 * Main.java
 * ----------------------------------------------------------
 * برنامج قياس أداء خوارزميات الترتيب الخمس على 4 حالات بيانات
 * بحجم ثابت N = 100,000 وطباعة جدول النتائج بالميكروثانية (µs).
 *
 * - يتم أخذ نسخة مستقلة من المصفوفة الأصلية قبل كل خوارزمية.
 * - يُقاس زمن الترتيب فقط (بدون وقت النسخ أو التعبئة).
 * - تُنفَّذ الخوارزميات داخل Thread ذي Stack كبير لأن Quick Sort
 *   يصل عمق التعاود فيه إلى N في الحالات المرتبة (بدون هذا تحدث StackOverflowError).
 */
public class Main {

    static final int N = 100_000;
    static final long SEED = 12345L;   // بذرة ثابتة لتكون النتائج قابلة للتكرار

    // أسماء الخوارزميات وحالات البيانات
    static final String[] ALGORITHMS = {
        "Selection Sort", "Insertion Sort", "Bubble Sort", "Merge Sort", "Quick Sort"
    };
    static final String[] CASES = {
        "Random", "Sorted Reversely", "Sorted Partially", "Sorted Fully"
    };

    // ---------------------- توليد حالات البيانات ----------------------

    /** 1) أعداد عشوائية تمامًا */
    static int[] generateRandom(int n, Random rnd) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = rnd.nextInt(1_000_000);
        return a;
    }

    /** 4) مرتبة تصاعديًا بالكامل */
    static int[] generateSortedFully(int n, Random rnd) {
        int[] a = generateRandom(n, rnd);
        Arrays.sort(a);
        return a;
    }

    /** 2) مرتبة عكسيًا (من الأكبر إلى الأصغر) */
    static int[] generateSortedReversely(int n, Random rnd) {
        int[] a = generateSortedFully(n, rnd);
        for (int i = 0, j = n - 1; i < j; i++, j--) {
            int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
        }
        return a;
    }

    /** 3) مرتبة جزئيًا بنسبة ~80%: نرتب المصفوفة ثم نستبدل 20% من المواضع بقيم عشوائية */
    static int[] generateSortedPartially(int n, Random rnd) {
        int[] a = generateSortedFully(n, rnd);
        int randomCount = (int) (n * 0.20);
        for (int k = 0; k < randomCount; k++) {
            int pos = rnd.nextInt(n);
            a[pos] = rnd.nextInt(1_000_000);
        }
        return a;
    }

    // ---------------------- تشغيل خوارزمية وقياس زمنها ----------------------

    /** يرجع زمن تنفيذ الخوارزمية بالميكروثانية (الترتيب فقط) */
    static long measure(int algorithm, int[] original) {
        // نسخة مستقلة (خارج القياس)
        int[] testArray = Arrays.copyOf(original, original.length);

        long startTime = System.nanoTime();
        switch (algorithm) {
            case 0: SortingAlgorithms.selectionSort(testArray); break;
            case 1: SortingAlgorithms.insertionSort(testArray); break;
            case 2: SortingAlgorithms.bubbleSort(testArray); break;
            case 3: SortingAlgorithms.mergeSort(testArray, 0, testArray.length - 1); break;
            case 4: SortingAlgorithms.quickSort(testArray, 0, testArray.length - 1); break;
        }
        long endTime = System.nanoTime();

        // التحقق من صحة الترتيب (خارج القياس)
        if (!isSorted(testArray)) {
            throw new IllegalStateException(ALGORITHMS[algorithm] + " failed to sort the array!");
        }
        return (endTime - startTime) / 1000;   // nano -> micro
    }

    static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) if (a[i - 1] > a[i]) return false;
        return true;
    }

    // ---------------------- البرنامج الرئيسي ----------------------

    static void runBenchmark() {
        Random rnd = new Random(SEED);

        // توليد حالات البيانات الأربع مرة واحدة
        int[][] datasets = {
            generateRandom(N, rnd),
            generateSortedReversely(N, rnd),
            generateSortedPartially(N, rnd),
            generateSortedFully(N, rnd)
        };

        // إحماء JIT (Warm-up) على مصفوفة صغيرة حتى لا تتأثر أول نتيجة بوقت التهيئة
        int[] warm = generateRandom(2000, new Random(1));
        for (int alg = 0; alg < ALGORITHMS.length; alg++) measure(alg, warm);

        long[][] results = new long[ALGORITHMS.length][CASES.length];
        for (int alg = 0; alg < ALGORITHMS.length; alg++) {
            for (int c = 0; c < CASES.length; c++) {
                results[alg][c] = measure(alg, datasets[c]);
                System.out.printf("%s - %s: %,d µs%n", ALGORITHMS[alg], CASES[c], results[alg][c]);
            }
        }

        printTable(results);
    }

    static void printTable(long[][] r) {
        String line = "+------------------+--------------------+--------------------+--------------------+--------------------+";
        System.out.println();
        System.out.println("Array size N = " + String.format("%,d", N));
        System.out.println(line);
        System.out.printf("| %-16s | %-18s | %-18s | %-18s | %-18s |%n",
                "Algorithm", "Random", "Sorted Reversely", "Sorted Partially", "Sorted Fully");
        System.out.println(line);
        for (int alg = 0; alg < ALGORITHMS.length; alg++) {
            System.out.printf("| %-16s |", ALGORITHMS[alg]);
            for (int c = 0; c < CASES.length; c++) {
                System.out.printf(" %15s µs |", String.format("%,d", r[alg][c]));
            }
            System.out.println();
        }
        System.out.println(line);
    }

    public static void main(String[] args) throws InterruptedException {
        // Thread بحجم Stack = 1 GB (يُحجز افتراضيًا فقط عند الحاجة) لتفادي StackOverflowError في Quick Sort
        Thread t = new Thread(null, Main::runBenchmark, "benchmark", 1024L * 1024 * 1024);
        t.start();
        t.join();
    }
}
