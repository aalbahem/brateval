package au.com.nicta.csp.brateval;

public class MatchType {

	public enum SpanMatch {
		EXACT, // exact boundary match
		APPROXIMATE, // relaxed boundary match, thresholded by edit-distance similarity
		OVERLAP // relaxed boundary match
	}

	public enum TypeMatch {
		EXACT, // exact type match
		HIERARCHICAL, // types are in a hierarchical relationship (currently not handled; collapses to INEXACT)
		INEXACT // types differ
	}

	private SpanMatch spanMatchType;
	private TypeMatch typeMatchType;
	private double simThreshold = 1.0; // similarity threshold for approximate matching

	/**
	 * Default MatchType is EXACT match for both Span and Type.
	 */
	public MatchType() {
		spanMatchType = SpanMatch.EXACT;
		typeMatchType = TypeMatch.EXACT;
	}

	public MatchType(SpanMatch spanMatchType, TypeMatch typeMatchType) {
		this.spanMatchType = spanMatchType;
		this.typeMatchType = typeMatchType;
	}

	public MatchType(SpanMatch spanMatchType, TypeMatch typeMatchType, double simThreshold) {
		this.spanMatchType = spanMatchType;
		this.typeMatchType = typeMatchType;
		this.simThreshold = simThreshold;
	}


	public static MatchType from(boolean exact_match, boolean verbose) {
		MatchType matchType = new MatchType();

		if (exact_match) {
			matchType.setSpanMatchType(SpanMatch.EXACT);
		} else {
			matchType.setSpanMatchType(SpanMatch.EXACT);
		}

		return matchType;
	}

	public static MatchType buildEntityMatchType(boolean exactMatch, boolean isVerbose, double similarityThreshold) {
		MatchType matchType = new MatchType();

		if (exactMatch) {
			matchType.setTypeMatchType(TypeMatch.EXACT);
		} else {
			matchType.setTypeMatchType(TypeMatch.INEXACT);
		}

		return matchType;
	}

	public SpanMatch getSpanMatchType() {
		return this.spanMatchType;
	}

	public void setSpanMatchType(SpanMatch spanMatchType) {
		this.spanMatchType = spanMatchType;
	}

	public TypeMatch getTypeMatchType() {
		return this.typeMatchType;
	}

	public void setTypeMatchType(TypeMatch typeMatchType) {
		this.typeMatchType = typeMatchType;
	}

	public double getSimThreshold() {
		return this.simThreshold;
	}

	public void setSimThreshold(double simThreshold) {
		this.simThreshold = simThreshold;
	}

	public MatchType spanMatchType(SpanMatch spanMatchType) {
		this.spanMatchType = spanMatchType;
		return this;
	}

	public MatchType typeMatchType(TypeMatch typeMatchType) {
		this.typeMatchType = typeMatchType;
		return this;
	}

	public MatchType simThreshold(double simThreshold) {
		this.simThreshold = simThreshold;
		return this;
	}

	public String toString() {
		String str = "Type match=" + this.typeMatchType + "; Span match=" + this.spanMatchType;
		if (this.spanMatchType.equals(SpanMatch.APPROXIMATE))
			str = str.concat("; Similarity=" + this.simThreshold);

		return str;
	}


	public static MatchType merge(MatchType mta, MatchType mtb) {
		MatchType newMt = new MatchType();
		int compType = mta.getTypeMatchType().compareTo(mtb.getTypeMatchType());
		int compSpan = mta.getSpanMatchType().compareTo(mtb.getSpanMatchType());

		// Want the most permissive (highest) value to be used in result
		if (compType == 0) { // equal
			newMt.setTypeMatchType(mta.getTypeMatchType());
		} else if (compType < 0) { // b higher than a
			newMt.setTypeMatchType(mtb.getTypeMatchType());
		} else if (compType > 0) { // a higher than b
			newMt.setTypeMatchType(mta.getTypeMatchType());
		}

		if (compSpan == 0) { // equal
			newMt.setSpanMatchType(mta.getSpanMatchType());
		} else if (compSpan < 0) { // b higher than a
			newMt.setSpanMatchType(mtb.getSpanMatchType());
		} else if (compSpan > 0) { // a higher than b
			newMt.setSpanMatchType(mta.getSpanMatchType());
		}

		return newMt;
	}

}