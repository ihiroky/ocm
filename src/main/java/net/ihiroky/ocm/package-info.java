/**
 * Provides for a mapper for object and command line arguments. Just like args4j annotations.
 * <p/>
 * {@link net.ihiroky.ocm.ArgumentProcessor} maps command line arguments to an object annotated by
 * {@link net.ihiroky.ocm.Option} and/or {@link net.ihiroky.ocm.Arguments}. Primitive types and
 * {@code java.lang.String} is supported.
 *
 * <h3>{@link net.ihiroky.ocm.Option} annotation</h3>
 * A field annotated by this anotation shows  an option. A property {@link net.ihiroky.ocm.Option#name()}
 * shows long name option, and a property {@link net.ihiroky.ocm.Option#alias()} shows short (in many case,
 * one character) name option. If a property {@link net.ihiroky.ocm.Option#required()} is true, an annotated option
 * is required (default value is false). {@link net.ihiroky.ocm.Option#metaName()} and
 * {@link net.ihiroky.ocm.Option#usage()} is used when you print option usage.
 *
 * <h3>{@link net.ihiroky.ocm.Arguments} annotation</h3>
 * Assigns non-option command line arguments to a field annotated by this annotation. <em>Annotated field must be
 * a concrete subtype of {@code java.util.Collection&lt;String&gt;}</em>.
 *
 * <h3>"--" option</h3>
 * If "--" (exclude ") appears in command line arguments, the right-side arguments of "--" is considered as
 * {@link net.ihiroky.ocm.Arguments} targets even though those are started character "-".
 *
 * <h3>Minus value</h3>
 * Minus value is available for a number type (byte, short, int, float, double and these wrapper class). Minus number
 * after the number type is considered as a value, not an option.
 *
 * <h3>Example</h3>
 * <pre><code>
 * import net.ihiroky.ocm.ArgumentParseException;
 * import net.ihiroky.ocm.ArgumentProcessor;
 * import net.ihiroky.ocm.Arguments;
 * import net.ihiroky.ocm.Option;
 *
 * import java.util.ArrayList;
 *
 * public class Main {
 *     public static void main(String[] args) {
 *         Args a = new Args();
 *         ArgumentProcessor ap = new ArgumentProcessor();
 *         try {
 *             ap.parse(a, args);
 *             System.out.println(a.str);
 *             System.out.println(a.bool);
 *             System.out.println(a.boolObject);
 *             System.out.println(a.integer);
 *             System.out.println(a.intObject);
 *         } catch (ArgumentParseException e) {
 *             System.out.println(e.getMessage());
 *             ap.printUsage(System.out);
 *         }
 *     }
 * }
 *
 * class Args {
 *     {@literal @}Option(name="string", alias="s", usage="string option.")
 *     String str = "default"; // default value.
 *
 *     {@literal @}Option(name="boolean", alias="b", usage="boolean option.")
 *     boolean bool;
 *
 *     // no alias
 *     {@literal @}Option(name="Boolean", metaName="Bool", usage="boolean object option.")
 *     Boolean boolObject;
 *
 *     // a required option.
 *     {@literal @}Option(name="integer", alias="i", required=true, usage="integer option.")
 *     int integer;
 *
 *     {@literal @}Option(name="Integer", alias="I", metaName="Integer", usage="integer object option.")
 *     Integer intObject;
 *
 *     {@literal @}Arguments
 *     ArrayList<String> args;
 * }
 *
 * </code></pre>
 * <pre><code>
 * $ java Main
 * option -i/--integer is required.
 * available options :
 *  -s, --string : string option.
 *  -b, --boolean : boolean option.
 *      --Boolean : boolean object option.
 *  -i, --integer : integer option. (required)
 *  -I, --Integer : integer object option.
 * $ java Main -s hoge -i 12345 -I 23456
 * hoge
 * false
 * null
 * 12345
 * 23456
 *</code></pre>
 *
 * @author Hiroki Itoh
 */
package net.ihiroky.ocm;
