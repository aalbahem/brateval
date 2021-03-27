package au.com.nicta.csp.brateval;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map;

/**
 * Source backannotator for Brat attotation evaluation
 * 
 * @author Andrey (Andreas) Scherbakov (andreas@softwareengineer.pro)
 *
 */
 
public class BackAnnotate {
	Path sourcePath = null;
	String source = null;
	TreeMap<Integer,Integer> linePositions = new TreeMap<Integer,Integer> ();

	public void readSource(){
		try {
			source = new String(Files.readAllBytes(sourcePath),
				"UTF-8");
		int lineNo = 1;
		linePositions.put(0,lineNo);
		for (int i = 0; i < source.length(); ++i)
		  if  (source.charAt(i)=='\n') {
			lineNo ++;
			linePositions.put(i+1, lineNo);
		}
		} catch (IOException e) {
    		e.printStackTrace();
		}
	}

	public BackAnnotate(String f) {
		sourcePath = Paths.get(f);
		readSource();
	}

	public BackAnnotate(String [] fs) {
		for (String f:fs) {
			Path p = Paths.get(f);
			if (Files.exists(p)) {
				sourcePath = p;
				readSource();
				return;
			}
		}
	}

	public boolean hasSource() {
		return source != null;
	}

	public int getLineNo(int pos) {
		return linePositions.floorEntry(pos).getValue();
	}
	
	public int getLineCharNo(int pos) {
		return pos - linePositions.floorKey(pos);
	}
	
	static class SpanTag {
		boolean side;
		int start;
		int end;
		Entity entity;
		SpanTag onTopS, onTopE;

		SpanTag(int s, int e, boolean sd, Entity ent)
		{	start = s; end = e; side = sd; entity = ent; }
		SpanTag setOnTopS(SpanTag other)
		{	onTopS = other; return this; }
		SpanTag setOnTopE(SpanTag other)
		{	onTopE = other; return this; }
	}
	
	int wordBound(String s, int i, boolean backward, boolean alignBackward)
		{
		while ((i>0 && !Character.isWhitespace(s.charAt(i-1))) &&
				i<s.length() && !Character.isWhitespace(s.charAt(i)))
					if (backward) --i;
					else ++i;
			
		if (alignBackward) {
			while (i>0 && Character.isWhitespace(s.charAt(i-1))) --i;
		} else {
			while  (i<s.length() && Character.isWhitespace(s.charAt(i))) ++i;
		}

		return i;
	}
	
	int bestWordBound(String s, int i, boolean alignBackward) {
		int lower = wordBound(s, i, true, alignBackward);
		int upper = wordBound(s, i, false, alignBackward);
		boolean lower_is_better = (i - lower) * 3 < (upper - i) * 2;
		return lower_is_better? lower : upper;
	}

	void renderSpan(List<Object> dst, int start, int end, Collection<SpanTag> tags)
	{
		byte t = 0;
		for (SpanTag tag : tags) {
			if (tag.entity==null) return; //Muted
			if (!tag.side) t |= OutFormat.FocusEntity1;
			else t |= OutFormat.OtherEntity; //To be extended
		}

		dst.add(new OutFormat.Span(
				source.substring(start, end)
					.replaceAll("\\s+"," ")
			,t)); 
	}

	void renderSpan(List<Object> dst, SortedMap<Integer,SpanTag> spanMap) {
			Iterable<Map.Entry<Integer,SpanTag> > es = spanMap.entrySet();
				HashSet<SpanTag> spanTags = new HashSet<SpanTag>(32);
				int start = es.iterator().next().getKey();
				for (Map.Entry<Integer,SpanTag> e: es) {
					if (e.getValue().start < start) spanTags.add(e.getValue());
				}

				for (Map.Entry<Integer,SpanTag> e: es) {
					int i = e.getKey();
					if (start != i)
						renderSpan(dst, start, i, spanTags);
					SpanTag s = e.getValue();
					while (s != null) {
						if (s.start == e.getKey()) {
							 spanTags.add(e.getValue());
							 s = s.onTopS;
						}
						if (s != null && s.end == e.getKey()) {
							 spanTags.remove(e.getValue());
							 s = s.onTopE;
						}
					}
					start = i;
				}
		}
	
	static void addSpanTag(Map<Integer,SpanTag> m, int start, int end, boolean side, Entity entity) {
		SpanTag tag = new SpanTag(start, end, side, entity);
		m.compute(start, (k,v)->tag.setOnTopS(v));
		m.compute(end, (k,v)->tag.setOnTopE(v));
		}
	
	static TreeMap<Integer,SpanTag> makeTagMap(Collection<Entity> es)
	{
		TreeMap<Integer,SpanTag> m = new TreeMap<Integer,SpanTag>();
		for (Entity e:es)
			for (Location l: e.getLocations())
				addSpanTag(m, l.getStart(), l.getEnd(), true, e);
		return m;
	}
	
	public void renderContext(List<Object> dst, Entity e, SortedMap<Integer,SpanTag> spans) {
		final int recommendedTotalLen = 75;
		final int minMargin = 7;
		
		List<Location> locs = e.getLocations();
		
		Location[] interleave_sorted = new Location[locs.size() + 1];
		int n = 0;
		int payloadLen = 0;
		int gap_start = 0;
		for (Location l:locs) {
			interleave_sorted[n++] = new Location(gap_start, l.getStart());
			gap_start = l.getEnd();
			payloadLen += l.length();
		}
		Location firstGap = interleave_sorted[0];
		Location lastGap = interleave_sorted[n] = new Location(gap_start,source.length());
		Arrays.sort(interleave_sorted,(x,y) -> x.length() - y.length());
		
		int margin = Math.max(recommendedTotalLen - payloadLen, minMargin);
		for (int i = 0; i <= n; ++i) {
			Location gap = interleave_sorted[i];
			int recommended_len = Math.min(margin / (n + 1 - i), gap.length());
			margin -= gap.length();
			if (gap == firstGap) {
					gap.setEnd(bestWordBound(source,gap.getEnd()-recommended_len, false));
			} else if (gap == lastGap) {
					gap.setStart(bestWordBound(source,gap.getStart()+recommended_len, true));
			} else {
					gap.setEnd(bestWordBound(source,gap.getEnd()-recommended_len/2, false));
					gap.setStart(bestWordBound(source,gap.getStart()+recommended_len/2, true));
			}
			margin += gap.length();
			margin = Math.max(margin, minMargin);
		}

		SortedMap<Integer,SpanTag> m = 
			new TreeMap<Integer,SpanTag>(
				spans.tailMap(firstGap.getEnd())
				.headMap(lastGap.getStart())
			);
			
		for (Location l : locs) {
			addSpanTag(m, l.getStart(), l.getEnd(), false, e);
		}
		for (Location g: interleave_sorted) {
			addSpanTag(m, g.getStart(), g.getEnd(), false, null);
		}
		renderSpan(dst, m);
	}

	public List<Object>  renderContext(Entity e, SortedMap<Integer,SpanTag> spans) {
		LinkedList<Object> r = new LinkedList<Object>();
		renderContext(r, e, spans);
		return r;
	}
		
	public static void main(String [] args) {
		BackAnnotate back = new BackAnnotate("bat2");
		for (int i=0; i<2000; ++i)
			System.out.println(Integer.toString(back.getLineNo(i)) + " " +
			Integer.toString(i) + " " +
			Integer.toString(back.getLineCharNo(i)));
	}
	
	public String locationInfo(Entity e) {
		if (hasSource())
			return sourcePath.toString()+":"+Integer.toString(getLineNo(
				e.getLocations().iterator().next().getStart()));
		else
			return e.locationInfo();
	}
}
	
	
