package au.com.nicta.csp.brateval;
import java.util.Vector;
import java.util.List;
import java.lang.Iterable;
import java.util.Iterator;

/**
 * 
 * Output markup format abstract class
 * 
 * @author Andrey (Andreas) Scherbakov (andreas@softwareengineer.pro)
 *
 */

public abstract class OutFormat {

	public static class Span {
		byte flags;
		Object inner;
		public Span(Object i, byte f) {inner = i; flags = f;}
	}
	
	//Span type bits
	public static final byte FocusEntity1 = 1;
	public static final byte FocusEntity2 = 2;
	public static final byte MatchEntity  = 4;
	public static final byte SwapEntity   = 8;
	public static final byte OtherEntity  = 16;
	public static final byte MatchRelation= 32;

	public StringBuffer produce(StringBuffer buff, Object o) {
		if (o != null)
		if (o instanceof Span) {
			Span s = (Span)o;
			buff.append(beginSpan(s.flags));
			produce(buff, s.inner);
			buff.append(endSpan(s.flags));
		} else if (o instanceof Iterable) {
			Iterator i = ((Iterable)o).iterator();
			while (i.hasNext())
				produce(buff, i.next());
		} else
			convert(buff, o.toString());
		return buff;
	}
	
	public String produce(Object o) {
		StringBuffer buff = new StringBuffer();
		produce(buff, o);
		return buff.toString();
	}

	public StringBuffer produceTable(StringBuffer buff, TableOut t) {
		buff.append(beginTable());
		List<Object> h = t.getHeader();
		if (h != null) {
			int i = 0;
			buff.append(beginRow());
			for (Object c:h) {
				if (i++ > 0)
					buff.append(nextCell());
				int start = buff.length();
				produce(buff, c);
				convertCell(buff, start, null);
			}
			buff.append(endRow());
		}
		for (Vector<Object> row:t.getContent()) {
			buff.append(beginRow());
			for (int i = 0; i < row.size(); ++i) {
				if (i > 0)
					buff.append(nextCell());
				int start = buff.length();
				produce(buff, row.elementAt(i));
				convertCell(buff, start, null);
			}
			buff.append(endRow());
		}
		buff.append(endTable());
		return buff;
	}
	
	public String produceTable(TableOut t) {
		StringBuffer buff = new StringBuffer();
		produceTable(buff, t);
		return buff.toString();
	}

	public abstract String header();
	public abstract String footer();
	public abstract String beginSpan(byte flags);
	public abstract String endSpan(byte flags);
	public abstract String beginTable();
	public abstract String beginRow();
	public abstract String nextCell();
	public abstract String endRow();
	public abstract String endTable();

	public abstract void convert(StringBuffer buff, String s);
	public String convert(String str) {
		StringBuffer buff = new StringBuffer();
		convert(buff,str);
		return buff.toString();
	}
	public void convertCell(StringBuffer buff, int start, Object header) {  }

	public static OutFormat ofEnum(Options.OutFmt h) {
		switch (h) {
			case HTML:
				return new HtmlOutFormat();
			case CSV:
				return new CsvOutFormat();
			default:
				return new PlainOutFormat(h);
		}
	}
}