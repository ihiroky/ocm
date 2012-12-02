OCM (Object - command line argument mapper)
===
Provides for a mapper for object and command line arguments. Just like args4j annotations.

## ArgumentProcessor
Maps command line arguments to an object annotated by Option and/or Arguments. Primitive types and java.lang.String is supported.


## Option annotation
A field annotated by this anotation shows an option. A property name() shows long name option, and a property alias() shows short (in many case, one character) name option. If a property required() is true, an annotated option is required (default value is false). metaName() and usage() is used when you print option usage.

## Arguments annotation
Assigns non-option command line arguments to a field annotated by this annotation. Annotated field must be a concrete subtype of java.util.Collection&lt;String&gt;.

## "--" option
If "--" (exclude ") appears in command line arguments, the right-side arguments of "--" is considered as Arguments targets even though those are started character "-".

## Minus value
Minus value is available for a number type (byte, short, int, float, double and these wrapper class). Minus number after the number type is considered as a value, not an option.

## Example

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

    $ java Main
    option -i/--integer is required.
    available options :
     -s, --string : string option.
     -b, --boolean : boolean option.
         --Boolean : boolean object option.
     -i, --integer : integer option. (required)
     -I, --Integer : integer object option.
    $ java Main -s hoge -i 12345 -I 23456
    hoge
    false
    null
    12345
    23456

