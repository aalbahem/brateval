package au.com.nicta.csp.brateval;

import java.util.Objects;

public class RelationMatchResult {

    private Relation rel;
    private EntityMatchResult e1Result;
    private EntityMatchResult e2Result;
    private MatchType entMatch;
    private MatchType relMatch;

    public RelationMatchResult(Relation rel, EntityMatchResult e1Result, EntityMatchResult e2Result) {
        this.rel = rel;
        this.e1Result = e1Result;
        this.e2Result = e2Result;
        this.entMatch = MatchType.merge(e1Result.getMatchType(), e2Result.getMatchType());
        // by default the relation match is exact span and exact type
        this.relMatch = new MatchType(MatchType.SpanMatch.EXACT, MatchType.TypeMatch.EXACT);
    }

    public RelationMatchResult(Relation rel, EntityMatchResult e1Result, EntityMatchResult e2Result, MatchType relMt) {
        this.rel = rel;
        this.e1Result = e1Result;
        this.e2Result = e2Result;
        this.entMatch = MatchType.merge(e1Result.getMatchType(), e2Result.getMatchType());
        this.relMatch = relMt;
    }

    public Relation getRel() {
        return this.rel;
    }

    public void setRel(Relation rel) {
        this.rel = rel;
    }

    public MatchType getEntMatch() {
        return this.entMatch;
    }

    public void setEntMatch(MatchType entMatch) {
        this.entMatch = entMatch;
    }

    public MatchType getRelMatch() {
        return this.relMatch;
    }

    public void setRelMatch(MatchType relMatch) {
        this.relMatch = relMatch;
    }

    public EntityMatchResult getE1Result() {
        return this.e1Result;
    }

    public void setE1Result(EntityMatchResult e1Result) {
        this.e1Result = e1Result;
    }

    public EntityMatchResult getE2Result() {
        return this.e2Result;
    }

    public void setE2Result(EntityMatchResult e2Result) {
        this.e2Result = e2Result;
    }

    public RelationMatchResult e1Result(EntityMatchResult e1Result) {
        this.e1Result = e1Result;
        return this;
    }

    public RelationMatchResult e2Result(EntityMatchResult e2Result) {
        this.e2Result = e2Result;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof RelationMatchResult)) {
            return false;
        }
        RelationMatchResult relationMatchResult = (RelationMatchResult) o;
        return Objects.equals(rel, relationMatchResult.rel) && Objects.equals(e1Result, relationMatchResult.e1Result) && Objects.equals(e2Result, relationMatchResult.e2Result) && Objects.equals(entMatch, relationMatchResult.entMatch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rel, e1Result, e2Result, entMatch);
    }


}
