package au.com.nicta.csp.brateval;

import java.lang.Character;
import java.io.*;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Taxonomy Configuration
 *
 * @author Andrey (Andreas) Scherbakov (andreas@softwareengineer.pro)
 * @author Karin Verspoor (karin.verspoor@unimelb.edu.au)
 */

public class TaxonomyConfig {
    public final int TOP_LEVEL = 0;
    public final String TOP = "ALL";

    public class EntityDesc {
        String name;
        int depth = 0;
        EntityDesc parent = null;

        EntityDesc(String s) {
            name = s;
        }
    }

    public class RelationDesc {
        String name;

        // Add args
        RelationDesc(String s) {
            name = s;
        } //Rewrite!
    }

    HierList<EntityDesc> entities = new HierList<EntityDesc>();
    HierList<RelationDesc> relations = new HierList<RelationDesc>();
    TreeMap<String, EntityDesc> entityByName = new TreeMap<String, EntityDesc>(String.CASE_INSENSITIVE_ORDER);
    TreeMap<String, RelationDesc> relationByName = new TreeMap<String, RelationDesc>(String.CASE_INSENSITIVE_ORDER);

    TaxonomyConfig(String confFile) {
        readConfigFile(confFile);
    }

    TaxonomyConfig() {
        //readConfigFile(findConfigFile());
    }

    private interface EntryAdder {
        void add(int level, String spec);
    }

    String consumeSection(BufferedReader r, EntryAdder a)
            throws IOException {
        String text;
        a.add(this.TOP_LEVEL, this.TOP); // Dummy TOP node
        int level_default = this.TOP_LEVEL + 1;
        while ((text = r.readLine()) != null) {
            int level = level_default;
            for (int i = 0; i < text.length() && Character.isWhitespace(text.charAt(i)); ++i)
                switch (text.charAt(i)) {
                    case ' ':
                        level++;
                        break;
                    case '\t':
                        level += 4;
                        break;
                    default:
                        level = level_default;
                        break;
                }
            String s = text.trim();
            if (!s.isEmpty()) {
                if (s.charAt(0) == '[')
                    return text; //New section started
                a.add(level, s);
            }
        }
        return text; //null
    }

    static File findConfigFile() {
        File fn = new File("./annotation.conf");
        if (fn.isFile() && fn.canRead()) return fn;
        System.out.println("Annotation taxonomy ignored. No file 'annotation.conf' found in current directory " + System.getProperty("user.dir"));
        return null;
    }

    void readConfigFile(String confFile) {
        File fn = new File(confFile);
        if (!(fn.isFile() && fn.canRead())) {
            fn = null;
            System.out.println("Annotation taxonomy ignored. No file " + confFile + " found.");
        }

        readConfigFile(fn);
    }

    void readConfigFile(File file) {
        if (file == null) return;
        System.out.println("Loading configuration from " + file.getAbsolutePath());
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String text = reader.readLine();

            while (text != null) {
                String tt = text.trim();
                if (tt == null || tt.isEmpty())
                    text = reader.readLine();
                else if (tt.equals("[entities]"))
                    text = consumeSection(reader, new EntryAdder() {
                        public void add(int l, String s) {
                            entities.add(l, new EntityDesc(s));
                        }
                    });
                else if (tt.equals("[relations]"))
                    text = consumeSection(reader, new EntryAdder() {
                        public void add(int l, String s) {
                            relations.add(l, new RelationDesc(s));
                        }
                    });
                else if (tt.charAt(0) == '[')
                    text = consumeSection(reader, new EntryAdder() {
                        public void add(int l, String s) {
                        }
                    });
                else
                    text = reader.readLine();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
            }
        }
        fillDerived();
    }

    private void fillDerived() {
        entities.traverse(new HierList.Visitor<EntityDesc>() {
            public void pre(int lev, EntityDesc x, EntityDesc p) {
                entityByName.put(x.name, x);
                //System.out.println("* " + x.name + ((p!=null)? ("->"+p.name):""));
                x.depth = (p != null) ? p.depth + 1 : 0;
                x.parent = p;
            }

            public void post(int lev, EntityDesc x, EntityDesc p) {
            }
        });
        relations.traverse(new HierList.Visitor<RelationDesc>() {
            public void pre(int lev, RelationDesc x, RelationDesc p) {
                relationByName.put(x.name, x);
            }

            public void post(int lev, RelationDesc x, RelationDesc p) {
            }
        });
    }

    private ArrayList<String> levelIndents = new ArrayList<String>(16);

    public EntityDesc getEntityDesc(String n) {
        return entityByName.get(n);
    }

    public RelationDesc getRelationDesc(String n) {
        return relationByName.get(n);
    }

    public String levelIndent(int level) {
        if (levelIndents.size() <= level ||
                levelIndents.get(level) == null) {
            StringBuffer buffer = new StringBuffer();
            int j = level;
            while (j >= 4) {
                buffer.append("\t");
                j -= 4;
            }
            while (j > 0) {
                buffer.append(" ");
                j--;
            }
            while (levelIndents.size() <= level)
                levelIndents.add(null);
            levelIndents.set(level, buffer.toString());
        }
        return levelIndents.get(level);
    }

    public String levelPrefix(int level) {
        if (levelIndents.size() <= level ||
                levelIndents.get(level) == null) {
            StringBuffer buffer = new StringBuffer();
            int j = level;
            while (j >= 4) {
                buffer.append("+");
                j -= 4;
            }
            while (j > 0) {
                buffer.append(" ");
                j--;
            }
            while (levelIndents.size() <= level)
                levelIndents.add(null);
            levelIndents.set(level, buffer.toString());
        }
        return levelIndents.get(level);
    }

    public void traverseEntities(HierList.Visitor<EntityDesc> v) {
        entities.traverse(v);
    }

    public void traverseRelations(HierList.Visitor<RelationDesc> v) {
        relations.traverse(v);
    }

    static public EntityDesc lowestCommonSubsumer(EntityDesc e1, EntityDesc e2) {
        while (e1 != null && e2 != null) {
            if (e1 == e2) return e1;
            if (e1.depth >= e2.depth) e1 = e1.parent;
            if (e2.depth > e1.depth) e2 = e2.parent;
        }
        return null;
    }

    public static TaxonomyConfig common = null;

    public static TaxonomyConfig singleton() {
        if (common == null) common = new TaxonomyConfig();
        return common;
    }


}
