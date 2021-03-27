package au.com.nicta.csp.brateval;

/**
 * Plain text output format specification class
 *
 * @author Andrey (Andreas) Scherbakov (andreas@softwareengineer.pro)
 */

public class PlainOutFormat extends OutFormat {
    Options.OutFmt mode = Options.OutFmt.PLAIN;

    public PlainOutFormat(Options.OutFmt m) {
        mode = m;
    }

    public String header() {
        return "";
    }

    public String footer() {
        return "";
    }

    public String beginSpan(byte flags) {
        switch (mode) {
            case TERMCOLOR:
                return termColorBegin(flags);
            default:
                return "";
        }
    }

    public String endSpan(byte flags) {
        switch (mode) {
            case TERMCOLOR:
                return termColorEnd(flags);
            default:
                return "";
        }
    }

    public String beginTable() {
        return "";
    }

    public String endTable() {
        return "";
    }

    public String nextCell() {
        return "|";
    }

    public String beginRow() {
        return "";
    }

    public String endRow() {
        return "\n";
    }

    public void convert(StringBuffer buff, String s) {
        buff.append(s);
    }

    public String termColorBegin(byte type) {
        if ((type & FocusEntity1) != 0)
            if ((type & OtherEntity) != 0)
                return "\033[1;32m";
            else
                return "\033[1;31m";
        else if ((type & OtherEntity) != 0)
            return "\033[33m";
        else
            return "";
    }

    public String termColorEnd(byte type) {
        return "\033[0m";
    }

}