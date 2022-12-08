package tmp;

import common.FileParser;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class Test2 {
    static long fact(long n){
        int i, fact = 1;
        for(i = (int) n; i>1; i--)
            fact *= i;
        return fact;//factorial of given number
    }
    static long nCr(long row, long col){
        long nume = 1;
        for(int i = (int) row; i>col; i--)
            nume *= i;
        return nume/fact(row-col);//generate result of nCr
    }
    static float[] genPascalsTriangle(long n){
        String space = StringUtils.repeat(" ", 2);
        float[] res = new float[30];
        int i = (int) (n - 1);
        for(int j = 0; j<(n-i-1); j++)
            System.out.print(space);;//printing space to show triangular form
        for(int j = 0; j<(i+1); j++) {
            System.out.print(space);
            res[j] = nCr(i, j);
            System.out.print(res[j]);
            System.out.print(space);
        }
        System.out.println();
        return res;
    }


    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input01.txt");

        System.out.println(Arrays.toString(genPascalsTriangle(13)));
    }
}
