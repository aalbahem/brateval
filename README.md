# BRAT-Eval v0.3.1


This tool performs pairwise comparison of annotation sets done on the same set of documents.
The annotated sets are required to be in the BRAT stand-off annotation format (http://brat.nlplab.org/standoff.html).
The current version of the tool has been tested on annotations made with Brat v1.3.
The tool only needs the jar file **brateval.jar** to work, which is included in the distribution file, and no further libraries are required.
The jar file contains the compiled java classes and the java source files.
In the following examples we assume that the jar file **brateval.jar** has been generated and is in the directory from which the java program is called, adjust the classpath parameter (-cp) accordingly.

After downloading brateval, it is possible to generate a jar file using maven:

```
mvn install
```

The jar file will be under the target folder. The name of the generated jar file contains the version of the software, e.g. **BRATEval-0.0.1-SNAPSHOT.jar**.
Change the name **"brateval.jar"** accordingly to run the examples below.

Entities are evaluated using the following command:

```
java -cp brateval.jar au.com.nicta.csp.brateval.CompareEntities evaluation_set_folder groundtruth_set_folder 
```
where:
- **evaluation_set_folder** = folder with annotations to evaluate. This can also be introduced with the commmand line parameter **"-e folder"**.
- **groundtruth_set_folder** = reference (gold) folder. This can also be introduced with the commmand line parameter **"-g folder"**.

The entity evaluation results show the overall statistics for true positives, false negatives and false positives.
Two entities match when they to agree on the entity type and on the span of text.

The default mode of comparison is **EXACT**: exact span match, exact type match.
To allow for relaxed matches, there are options to relax each of these.

For spans, there are three options: **EXACT**, **OVERLAP**, and **APPROXIMATE**. These are set with the **"-s OPTION"** or **"-span-match OPTION"** parameters.
**"-s overlap"** allows two entities to match if their boundaries overlap (at all).
**"-s approx threshold"** or **"-s approximate threshold"** is an overlap match with the additional constraint that the entities must be similar, according to some edit-distance based similarity threshold.

So for example, an approximate span match using a *0.8* similarity threshold would be run as: 
```
java -cp brateval.jar au.com.nicta.csp.brateval.CompareEntities evaluation_set_folder groundtruth_set_folder -s approx 0.8
```   
In this case, annotations will match if the boundaries of the annotation overlap, whether or not the type matches.


For entity type matches, there are also three options: **EXACT**, **INEXACT**, and **HIERARCHICAL**. These are set with the **"-t OPTION"** or **"-type-match OPTION"** parameters.
**"-t inexact"** allows two entities to match even if their type labels are not the same.
**"-t hierarchical"** allows two entities to match only if one type label is more general than the other. This is not currently implemented and falls back to "inexact".

To print detailed results of all individual False Positive and False Negative matches, as well as inexact span or type-based matches, add the **"-v"** (**"-verbose"**) option.
When an **annotation.conf** file containing the full set of entities, including potentially entities in a hierarchy, is supplied, add the **"-ft"** (**"-print-full-taxonomy"**) option to force all entities to be listed in the results.

For instance, with both of these values set, and an overlap span plus inexact type match, use:
```
java -cp brateval.jar au.com.nicta.csp.brateval.CompareEntities -e evaluation_set_folder -g groundtruth_set_folder -ft -v -s overlap -t inexact
```

By default, the BRAT annotation.conf file is expected in the directory where brateval is run. However, its location can be explitily set with the option **"-tc"** or **"-taxon-config"** or just **"-config"** plus the file location+name, e.g.:
```
java -cp brateval.jar au.com.nicta.csp.brateval.CompareEntities -e evaluation_set_folder -g groundtruth_set_folder -config config/annotation.conf
```
If the annotation configuration file is not found, then the tool simply infers the entity types; in that case inexact or hierarchical matching defaults to exact matching.


Relations use the same parameter options; and are evaluated using the following command (with a few parameters set controlling the entity matching):
```
java -cp brateval.jar au.com.nicta.csp.brateval.CompareRelations -e evaluation_set_folder -g groundtruth_set_folder -t exact -s overlap
```
where:
- evaluation_set_folder = folder with annotations to evaluate
- groundtruth_set_folder = reference folder

The relation evaluation results shows the statistics for true positives, false negatives and false positives.
Two relations match when the entities match (controlled by the span and type parameters) and the relation type match.
The statistics show when the statistics for relations in which the entities are matched in the reference set but the relation does not exist in the reference set.



It is possible to run the software directly using maven after installing it:

```
mvn install
```

Based on the examples above, using maven consider the following call from the directory where the software was installed to perform entity comparison:

```
mvn exec:java -Dexec.mainClass=au.com.nicta.csp.brateval.CompareEntities -Dexec.args="-e evaluation_set_folder -g groundtruth_set_folder -s exact"
```

and the following one for relation comparison:

```
mvn exec:java -Dexec.mainClass=au.com.nicta.csp.brateval.CompareRelations -Dexec.args="-e evaluation_set_folder -g groundtruth_set_folder -s exact -verbose"
```

The software has been used to produce results for the Variome corpus presented in the following publication:

Karin Verspoor, Antonio Jimeno Yepes, Lawrence Cavedon, Tara McIntosh, Asha Herten-Crabb, Zoe Thomas, John-Paul Plazzer (2013)
Annotating the Biomedical Literature for the Human Variome.
Database: The Journal of Biological Databases and Curation, virtual issue for BioCuration 2013 meeting. 2013:bat019.
doi:10.1093/database/bat019
