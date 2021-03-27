package au.com.nicta.csp.brateval;

/**
 * .html output markup format specification class
 *
 * @author Andrey (Andreas) Scherbakov (andreas@softwareengineer.pro)
 */

public class HtmlOutFormat extends OutFormat {
    public String header() {
        return "<HTML><BODY>\n";
    }

    public String footer() {
        return "</BODY></HTML>\n";
    }

    public String beginSpan(byte flags) {
        String color; //TBR
        if ((flags & FocusEntity1) != 0)
            color = "#0000ff";
        else
            color = "#000000";
        return "<span style=\"color: " + color + "\">";
    }

    public String endSpan(byte flags) {
        return "</span>";
    }

    public String beginTable() {
        return "<table width=100%>";
    }

    public String endTable() {
        return "</table>";
    }

    public String nextCell() {
        return "</td><td>";
    }

    public String beginRow() {
        return "<tr><td>";
    }

    public String endRow() {
        return "</td></tr>";
    }

    public void convert(StringBuffer buff, String s) {
        for (int j = 0; j < s.length(); ++j) {
            char c = s.charAt(j);
            switch (c) {
                case '<':
                case '>':
                case '\n':
                    buff.append(String.format("&#x%x;", (int) c));
                default:
                    buff.append(c);
            }
        }
    }
}