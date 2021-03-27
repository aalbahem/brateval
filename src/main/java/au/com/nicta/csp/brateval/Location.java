package au.com.nicta.csp.brateval;

public class Location {
    private int start;
    private int end;

    public Location(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int s) {
        start = s;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int e) {
        end = e;
    }

    public int length() {
        return end - start;
    }
}
