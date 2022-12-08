package tmp;

import common.FileParser;

import java.util.Arrays;
import java.util.List;

public class Test {
    private static final int MAX_RADIUS_ARR_SIZE = 16;
    static int fact(int n){
        int i;
        int fact = 1;
        for(i = n; i>1; i--) {
            fact *= i;
        }
        return fact;//factorial of given number
    }
    static int triangleVal(int row, int col){
        int nume = 1;
        for(int i = row; i > col; i--)
            nume *= i;
        return nume/fact(row-col);//generate result of nCr
    }

    static float pow(float a, int b) {
        float res = 1.0f;
        for (int i = 0; i < b; i++) {
            res *= a;
        }
        return res;
    }

    static float sum(float[] res, int len) {
        float sum = res[0];
        for (int i = 1; i < len; ++i) {
            sum += res[i] * 2;
        }
        return sum;
    }

    static float[] getValues(int len) {
        float[] res = new float[MAX_RADIUS_ARR_SIZE];
        int triangleRow = len + 7;
        for (int i = 0; i < len; i++) {
            res[len - i - 1] = triangleVal(triangleRow, i + 2)/pow(2.0f, triangleRow);
        }
        return res;
    }

    static float[] getValuesAlt(int len) {
        float[] res = new float[MAX_RADIUS_ARR_SIZE];
        int triangleRow = len + 7;
        for (int i = 0; i < len; i++) {
            res[len - i - 1] = triangleVal(triangleRow, i + 2);
        }
        float divideBy = sum(res, len);
        System.out.println(divideBy);
        for (int i = 0; i < len; i++) {
            res[i] /= divideBy;
        }
        return res;
    }



    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input01.txt");

        System.out.println(Arrays.toString(getValues(5)));
        System.out.println(Arrays.toString(getValuesAlt(5)));
    }
}
