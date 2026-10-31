/**
 * SortingAlgorithms.java
 * ----------------------------------------------------------
 * تحتوي هذه الفئة على خوارزميات الترتيب الخمس المطلوبة:
 *   1) Selection Sort  -> O(N^2)
 *   2) Insertion Sort  -> O(N^2)  (O(N) في الحالة الأفضل)
 *   3) Bubble Sort     -> O(N^2)  (O(N) مع التحسين في الحالة الأفضل)
 *   4) Merge Sort      -> O(N log N) في جميع الحالات
 *   5) Quick Sort      -> O(N log N) متوسطًا، O(N^2) في أسوأ حالة
 *
 * All methods sort the array in place in ascending order.
 */
public class SortingAlgorithms {

    // ======================= 1) Selection Sort =======================
    // في كل دورة نبحث عن أصغر عنصر في الجزء غير المرتب ونضعه في مكانه.
    // عدد المقارنات ثابت تقريبًا N^2/2 مهما كانت حالة البيانات.
    public static void selectionSort(int[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (a[j] < a[minIndex]) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                int tmp = a[i];
                a[i] = a[minIndex];
                a[minIndex] = tmp;
            }
        }
    }

    // ======================= 2) Insertion Sort =======================
    // نأخذ كل عنصر ونُدخله في مكانه الصحيح داخل الجزء المرتب على يساره.
    // في المصفوفة المرتبة لا يحدث أي إزاحة => O(N).
    public static void insertionSort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= 0 && a[j] > key) {
                a[j + 1] = a[j];   // إزاحة العنصر الأكبر خطوة لليمين
                j--;
            }
            a[j + 1] = key;
        }
    }

    // ======================= 3) Bubble Sort =======================
    // نقارن كل عنصرين متجاورين ونبدلهما إذا كانا بترتيب خاطئ.
    // التحسين: إذا لم يحدث أي تبديل في دورة كاملة نتوقف (swapped flag).
    public static void bubbleSort(int[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (a[j] > a[j + 1]) {
                    int tmp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = tmp;
                    swapped = true;
                }
            }
            if (!swapped) break;   // المصفوفة أصبحت مرتبة
        }
    }

    // ======================= 4) Merge Sort =======================
    // نقسم المصفوفة إلى نصفين، نرتب كل نصف بالتعاود، ثم ندمجهما.
    // العمق دائمًا log N والدمج O(N) => O(N log N) في كل الحالات.
    public static void mergeSort(int[] a, int left, int right) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSort(a, left, mid);
        mergeSort(a, mid + 1, right);
        merge(a, left, mid, right);
    }

    private static void merge(int[] a, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        int[] L = new int[n1];
        int[] R = new int[n2];
        System.arraycopy(a, left, L, 0, n1);
        System.arraycopy(a, mid + 1, R, 0, n2);

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) a[k++] = L[i++];
            else              a[k++] = R[j++];
        }
        while (i < n1) a[k++] = L[i++];
        while (j < n2) a[k++] = R[j++];
    }

    // ======================= 5) Quick Sort =======================
    // نختار عنصر المحور (Pivot) = آخر عنصر (Lomuto partition) ثم نقسم
    // المصفوفة إلى عناصر أصغر وأكبر من المحور ونكرر التعاود.
    // إذا كانت المصفوفة مرتبة (تصاعديًا أو عكسيًا) يكون المحور دائمًا
    // أكبر أو أصغر عنصر => تقسيم غير متوازن (N-1 و 0) => O(N^2)
    // وعمق تعاود = N (لذلك نشغله داخل Thread بحجم Stack كبير).
    public static void quickSort(int[] a, int low, int high) {
        if (low < high) {
            int p = partition(a, low, high);
            quickSort(a, low, p - 1);
            quickSort(a, p + 1, high);
        }
    }

    private static int partition(int[] a, int low, int high) {
        int pivot = a[high];          // المحور = آخر عنصر
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (a[j] <= pivot) {
                i++;
                int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
            }
        }
        int tmp = a[i + 1]; a[i + 1] = a[high]; a[high] = tmp;
        return i + 1;
    }
}
