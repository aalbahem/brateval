package au.com.nicta.csp.brateval;

import java.util.HashSet;
import java.util.Set;

/**
 * Data structure to store the relation comparison results
 *
 * @author Antonio Jimeno Yepes (antonio.jimeno@gmail.com)
 */
public class RelationComparison {
    private Set<Relation> tp = new HashSet<Relation>();
    private Set<Relation> fp = new HashSet<Relation>();
    private Set<Relation> fn = new HashSet<Relation>();

    // Relations added to the set
    private Set<Relation> nr = new HashSet<Relation>();

    public void addTP(Relation relation) {
        tp.add(relation);
    }

    public Set<Relation> getTP() {
        return tp;
    }

    public void addFP(Relation relation) {
        fp.add(relation);
    }

    public Set<Relation> getFP() {
        return fp;
    }

    public void addFN(Relation relation) {
        fn.add(relation);
    }

    public Set<Relation> getFN() {
        return fn;
    }

    public void addNR(Relation relation) {
        nr.add(relation);
    }

    public Set<Relation> getNR() {
        return nr;
    }
}
