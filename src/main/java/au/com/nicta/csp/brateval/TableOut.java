package au.com.nicta.csp.brateval;

/**
 * Table output producer
 *
 * @author Andrey (Andreas) Scherbakov (andreas@softwareengineer.pro)
 */

import java.util.List;
import java.util.LinkedList;
import java.util.Vector;

public class TableOut {
    private LinkedList<Vector<Object>> content = new LinkedList<Vector<Object>>();
    private int nColumn = 0;
    private List<Object> header = null;
    private Vector<Object> currRow = null;

    public TableOut(int col_num) {
    	nColumn = col_num;
    }

    public TableOut(List<Object> hdr) {
        nColumn = hdr.size();
        header = hdr;
    }

    public void setCell(int j, Object o) {
        if (currRow == null) {
            currRow = new Vector<Object>(nColumn);
            currRow.setSize(nColumn);
        }
        currRow.set(j, o);
    }

    public int nextRow() {
        if (currRow != null) {
            content.add(currRow);
            currRow = null;
        }
        return content.size();
    }

    public int commitRow() {
        if (currRow != null) return nextRow();
        return content.size();
    }

    public LinkedList<Vector<Object>> getContent() {
        commitRow();
        return content;
    }

    public List<Object> getHeader() {
        return header;
    }
}

	