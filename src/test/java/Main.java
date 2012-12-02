import net.ihiroky.ocm.ArgumentParseException;
import net.ihiroky.ocm.ArgumentProcessor;
import net.ihiroky.ocm.Arguments;
import net.ihiroky.ocm.Option;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Args a = new Args();
        ArgumentProcessor ap = new ArgumentProcessor();
        try {
            ap.parse(a, args);
            System.out.println(a.str);
            System.out.println(a.bool);
            System.out.println(a.boolObject);
            System.out.println(a.integer);
            System.out.println(a.intObject);
        } catch (ArgumentParseException e) {
            System.out.println(e.getMessage());
            ap.printUsage(System.out);
        }
    }
}

class Args {
    @Option(name = "string", alias = "s", usage = "string option.")
    String str = "default"; // default value.

    @Option(name = "boolean", alias = "b", usage = "boolean option.")
    boolean bool;

    // no alias.
    @Option(name = "Boolean", metaName = "Bool", usage = "boolean object option.")
    Boolean boolObject;

    // a required option.
    @Option(name = "integer", alias = "i", required = true, usage = "integer option.")
    int integer;

    @Option(name = "Integer", alias = "I", metaName = "Integer", usage = "integer object option.")
    Integer intObject;

    @Arguments
    ArrayList<String> args;
}
