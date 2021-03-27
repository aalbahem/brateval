package au.com.nicta.csp.brateval;

import java.lang.Math;

public class SpanSimilarity {
    static int costOfSubstitution(char x, char y) {
        return (x == y) ? 0 : 1;
    }

    static int minInsDelCost = 1;

    public static int editDistance(String x, String y, int allowed_dist) {
        int lb = Math.min(y.length() - x.length(), 0);
        int ub = Math.max(y.length() - x.length(), 0);
        int margin = (allowed_dist - (ub - lb) * minInsDelCost) / minInsDelCost;
        if (margin < 0) return allowed_dist + 1;

        int[] d = new int[y.length() + 1];
        for (int i = 0; i <= y.length(); ++i) d[i] = i;
        for (int i = 1; i <= x.length(); i++) {
            int j = Math.max(1, lb + i - margin);
            int substBase = d[j - 1]++;

            for (; j <= Math.min(y.length(), ub + i + margin); j++) {
                int newCost = Math.min(Math.min(substBase
                                + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                        d[j] + 1),
                        d[j - 1] + 1);
                substBase = d[j];
                d[j] = newCost;
            }
        }

        //Uncomment to debug Edit Distance
        //System.out.println("SED:: allowed: " + allowed_dist + " measured: " + d[y.length()]+ " " + y + " ~ " + x  );
        return d[y.length()];
    }

}
