package au.com.nicta.csp.brateval;
import java.util.Vector;
import java.lang.IllegalArgumentException;

/**
 * 
 * BRAT evaluator options
 * 
 * @author Andrey (Andreas) Scerbakov (andreas@softwareengineer.pro)
 *
 */

 public class Options {
	public static Options common = null;
	public enum Highlight {
		PLAIN,
		HTML,
		TERMCOLOR
	}
		
	public Highlight highlight = Highlight.PLAIN;
	public String[] argv;
	
	static String toEnumName(String x) {
		return x.replaceAll("[^A-Za-z0-9]","").toUpperCase();
	}
	public Options(String [] argv) {
		Vector<String> av = new Vector<String>(argv.length);
		for (int j=0; j<argv.length; ++j) {
			if (argv[j].charAt(0) == '-') {
				j++;
				switch(argv[j-1]) {
					case "-hl": case "-highlight":
						highlight = Highlight.valueOf(toEnumName(argv[j]));
						break;
					default:
						throw new IllegalArgumentException("Unsupported option" + argv[j]);
				}
			} else av.add(argv[j]);
		}
		av.toArray(this.argv = new String[av.size()]);
	}

	public  static void main(String [] argv) {
		Options.common = new Options(argv);
		System.out.println(Options.common.argv.length);
		System.out.println(Options.common.argv[0]);
	}
}

