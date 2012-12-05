package net.ihiroky.ocm;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author Hiroki Itoh
 * @version $Revision: 1.8 $ $Date: 2011/04/08 08:23:57 $
 */
public class ArgumentProcessorTest {

    @Test
    public void testName() throws Exception {
        StubArgs a = new StubArgs();
        ArgumentProcessor ap = new ArgumentProcessor();

        String[] args = new String[]{
                "-i", "12345", "-s", "hoge", "-b", "arg1", "-B", "-I", "234567", "arg2", "arg3",
                "-m", "-123",
                "-z", "127", "-Z", "-128", "-y", "c", "-Y", "C", "-x", "32767", "-X", "-32768",
                "-w", "1.1", "-W", "-1.1", "-v", "2.2", "-V", "-2.2", "-u", "1234567890", "-U", "-1234567890"
        };
        ap.parse(a, args);

        assertThat(a.str, is("hoge"));
        assertThat(a.bool, is(true));
        assertThat(a.boolObject, is(Boolean.TRUE));
        assertThat(a.integer, is(12345));
        assertThat(a.intObject, is(234567));
        assertThat(a.args, is(Arrays.asList("arg1", "arg2", "arg3")));

        assertThat(a.minusInteger, is(-123));
        assertThat(a.byteValue, is((byte) 127));
        assertThat(a.byteObjectValue, is((byte) -128));
        assertThat(a.charValue, is('c'));
        assertThat(a.charObjectValue, is('C'));
        assertThat(a.shortValue, is((short) 32767));
        assertThat(a.shortObjectValue, is((short) -32768));
        assertThat(a.floatValue, is(1.1f));
        assertThat(a.floatObjectValue, is(-1.1f));
        assertThat(a.doubleValue, is(2.2d));
        assertThat(a.doubleObjectValue, is(-2.2d));
        assertThat(a.longValue, is(1234567890L));
        assertThat(a.longObjectValue, is(-1234567890L));
    }

    @Test
    public void testNoArgument() throws Exception {
        StubArgs a = new StubArgs();
        ArgumentProcessor ap = new ArgumentProcessor();
        String[] args = new String[]{
                "-i", "12345"
        };
        ap.parse(a, args);
        assertThat(a.integer, is(12345));
        assertThat(a.args, is(Collections.<String>emptyList()));


        StubArgsDefault ad = new StubArgsDefault();
        ArgumentProcessor apd = new ArgumentProcessor();
        apd.parse(ad, args);
        assertThat(ad.integer, is(12345));
        assertThat(ad.args, is(Arrays.asList("hoge"))); // 引数未指定時はデフォルト優先
    }

    @Test
    public void testAlias() throws Exception {
        StubArgs a = new StubArgs();
        ArgumentProcessor ap = new ArgumentProcessor();
        ap.parse(a, new String[]{
                "--integer", "12345",
                "--string", "hoge",
                "--boolean",
                "arg1",
                "--Boolean",
                "--Integer", "234567",
                "arg2",
                "arg3"
        });

        assertThat(a.str, is("hoge"));
        assertThat(a.bool, is(true));
        assertThat(a.boolObject, is(Boolean.TRUE));
        assertThat(a.integer, is(12345));
        assertThat(a.intObject, is(234567));
        assertThat(a.args, is(Arrays.asList("arg1", "arg2", "arg3")));
    }

    @Test
    public void testRequired() throws Exception {
        StubArgsRequired a = new StubArgsRequired();
        ArgumentProcessor ap = new ArgumentProcessor();

        String[] args = new String[]{};
        try {
            ap.parse(a, args);
            fail();
        } catch (ArgumentParseException ape) {
            assertThat(ape.getMessage(), is("option -i/--opti is required."));
        }
        assertThat(a.integer, is(0));
        assertThat(a.args, is(nullValue()));

        args = new String[]{
                "-i", "1"
        };
        try {
            ap.parse(a, args);
            fail();
        } catch (ArgumentParseException ape) {
            assertThat(ape.getMessage(), is("arguments are required."));
        }
        assertThat(a.integer, is(1));
        assertThat(a.args, is(nullValue()));
    }

    @Test
    public void testUnknownOption() {
        StubArgs a = new StubArgs();
        ArgumentProcessor ap = new ArgumentProcessor();
        try {
            ap.parse(a, new String[]{"-g"});
            fail();
        } catch (ArgumentParseException ape) {
            assertThat(ape.getMessage(), is("unknown option : -g"));
        }
    }

    @Test
    public void testUnsupportedType() throws Exception {
        StubArgsUnsupportedType a = new StubArgsUnsupportedType();
        try {
            new ArgumentProcessor().parse(a, new String[0]);
            fail();
        } catch (IllegalArgumentTypeException e) {
            assertThat(e.getMessage(), is("unsupported option type : interface java.util.Map, option : m"));
        }
        assertThat(a.map, is(nullValue()));
    }

    @Test
    public void testNoNameOption() throws Exception {
        Object a0 = new Object() {
            @Option(name = "")
            String a;
        };
        try {
            new ArgumentProcessor().parse(a0, new String[0]);
            fail();
        } catch (IllegalArgumentTypeException iate) {
            assertThat(iate.getMessage(), is("option name for field a is required."));
        }
    }

    @Test
    public void testInvalidType() {
        StubArgs a = new StubArgs();
        ArgumentProcessor ap = new ArgumentProcessor();
        try {
            ap.parse(a, new String[]{"-i", "fuga"});
            fail();
        } catch (ArgumentParseException ape) {
            assertThat(ape.getMessage(), is("failed to set argument. option : -i/--integer"));
            assertThat(ape.getCause(), is(instanceOf(NumberFormatException.class)));
        }
    }

    @Test
    public void testDefault() throws Exception {
        StubArgsDefault a = new StubArgsDefault();
        ArgumentProcessor ap = new ArgumentProcessor();
        ap.parse(a, new String[0]);
        assertThat(a.str, is("default"));
        assertThat(a.integer, is(Integer.MIN_VALUE));
        assertThat(a.bool, is(Boolean.TRUE));
        assertThat(a.args, is(Arrays.asList("hoge")));
    }

    @Test
    public void testPrintOneLineUsage() throws Exception {
        StubArgs a = new StubArgs();
        ArgumentProcessor ap = new ArgumentProcessor();
        ap.parse(a, new String[0]);

        ByteArrayOutputStream base = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(base);
        ap.printOneLineUsage(out);
        out.flush();
        assertThat(base.toString(),
                is("available options :"
                        + " [-s/--string str] [-b/--boolean bool] [-B/--Boolean Bool] [-i/--integer int] "
                        + "[-I/--Integer Int] [-m/--minus mint] [-z/--byte byte] [-Z/--Byte Byte] [-y/--char char] "
                        + "[-Y/--Character Character] [-x/--short short] [-X/--Short Short] [-w/--float float] "
                        + "[-W/--Float Float] [-v/--double double] [-V/--Double Double] [-u/--long long] "
                        + "[-U/--Long Long]" + System.getProperty("line.separator")));
    }

    @Test
    public void testPrintOneLineUsageDefaultMetaName() throws Exception {
        Object obj1 = new Object() {
            @Option(name = "a")
            private String noMetaOption;
            @Arguments
            private CopyOnWriteArrayList<String> noMetaArgs;
        };
        ArgumentProcessor ap = new ArgumentProcessor();
        ap.parse(obj1, new String[0]);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ap.printOneLineUsage(new PrintStream(out, true));
        assertThat(out.toString(), is("available options : [--a noMetaOption]\n"));

    }

    @Test
    public void testPrintUsage() throws Exception {
        StubArgs a = new StubArgs();
        ArgumentProcessor ap = new ArgumentProcessor();
        ap.parse(a, new String[0]);

        ByteArrayOutputStream base = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(base);
        ap.printUsage(out);
        out.flush();

        String ls = System.getProperty("line.separator");
        assertThat(base.toString(),
                is("available options :" + ls
                        + " -s, --string : string option." + ls
                        + " -b, --boolean : boolean option(switch)." + ls
                        + " -B, --Boolean : boolean object option(switch)." + ls
                        + " -i, --integer : integer option." + ls
                        + " -I, --Integer : integer object option." + ls
                        + " -m, --minus : minus integer option." + ls
                        + " -z, --byte : byte option." + ls
                        + " -Z, --Byte : Byte option." + ls
                        + " -y, --char : char option." + ls
                        + " -Y, --Character : Character option." + ls
                        + " -x, --short : short option." + ls
                        + " -X, --Short : Short option." + ls
                        + " -w, --float : float option." + ls
                        + " -W, --Float : Float option." + ls
                        + " -v, --double : double option." + ls
                        + " -V, --Double : Double option." + ls
                        + " -u, --long : long option." + ls
                        + " -U, --Long : Long option." + ls));
    }

    @Test
    public void testStringHyphenValue() throws Exception {
        StubArgs a = new StubArgs();
        ArgumentProcessor ap = new ArgumentProcessor();
        try {
            ap.parse(a, new String[]{
                    "-s", "-o", "value"
            });
            fail();
        } catch (ArgumentParseException ape) {
            assertThat(ape.getMessage(), is("no value is found for option -s/--string"));
        }

        ap.parse(a, new String[]{
                "-s", "value"
        });
        assertThat(a.str, is("value"));
    }

    @Test
    public void testLeftArgs() throws Exception {
        StubArgs a = new StubArgs();
        ArgumentProcessor ap = new ArgumentProcessor();
        ap.parse(a, new String[]{
                "-s", "value", "arg", "--", "-1", "a", "-s", "-i", "100"
        });
        assertThat(a.str, is("value"));
        assertThat(a.args.get(0), is("arg"));
        assertThat(a.args.get(1), is("-1"));
        assertThat(a.args.get(2), is("a"));
        assertThat(a.args.get(3), is("-s"));
        assertThat(a.args.get(4), is("-i"));
        assertThat(a.args.get(5), is("100"));
    }

    @Test
    public void testNameOnly() throws Exception {
        StubArgsNoName a = new StubArgsNoName();
        ArgumentProcessor ap = new ArgumentProcessor();
        try {
            ap.parse(a, new String[0]);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ap.printUsage(new PrintStream(out, true));
        String ls = System.getProperty("line.separator");
        assertThat(out.toString(),
                is("available options :" + ls
                        + "     --hoge : " + ls
                        + " -f, --fuga : " + ls));
    }

    @Test
    public void testDuplicate() throws Exception {
        Object a0 = new Object() {
            @Option(name = "n", alias = "aa")
            String a;
            @Option(name = "n", alias = "ab")
            String b;
        };
        try {
            new ArgumentProcessor().parse(a0, new String[]{"-n", "1"});
            fail();
        } catch (IllegalArgumentTypeException iate) {
            assertThat(iate.getMessage(), is("name [--n] or alias [-ab] is duplicated."));
        }

        Object a1 = new Object() {
            @Option(name = "n0", alias = "a")
            String a;
            @Option(name = "n1", alias = "a")
            String b;
        };
        try {
            new ArgumentProcessor().parse(a1, new String[]{"-n", "1"});
            fail();
        } catch (IllegalArgumentTypeException iate) {
            assertThat(iate.getMessage(), is("name [--n1] or alias [-a] is duplicated."));
        }

    }

    @Test
    public void testHashSetArguments() throws Exception {
        HashSetArgs args = new ArgumentProcessor().parse(new HashSetArgs(), new String[]{"arg0", "arg1"});
        assertThat(args.args, is(new HashSet<String>(Arrays.asList("arg0", "arg1"))));
    }

    private static class StubArgs {
        @Option(name = "string", alias = "s", metaName = "str", usage = "string option.")
        private String str;

        @Option(name = "boolean", alias = "b", metaName = "bool", usage = "boolean option(switch).")
        private boolean bool;

        @Option(name = "Boolean", alias = "B", metaName = "Bool", usage = "boolean object option(switch).")
        private Boolean boolObject;

        @Option(name = "integer", alias = "i", metaName = "int", usage = "integer option.")
        private int integer;

        @Option(name = "Integer", alias = "I", metaName = "Int", usage = "integer object option.")
        private Integer intObject;

        @Option(name = "minus", alias = "m", metaName = "mint", usage = "minus integer option.")
        private int minusInteger;

        @Option(name = "byte", alias = "z", metaName = "byte", usage = "byte option.")
        private byte byteValue;

        @Option(name = "Byte", alias = "Z", metaName = "Byte", usage = "Byte option.")
        private Byte byteObjectValue;

        @Option(name = "char", alias = "y", metaName = "char", usage = "char option.")
        private char charValue;

        @Option(name = "Character", alias = "Y", metaName = "Character", usage = "Character option.")
        private Character charObjectValue;

        @Option(name = "short", alias = "x", metaName = "short", usage = "short option.")
        private short shortValue;

        @Option(name = "Short", alias = "X", metaName = "Short", usage = "Short option.")
        private Short shortObjectValue;

        @Option(name = "float", alias = "w", metaName = "float", usage = "float option.")
        private float floatValue;

        @Option(name = "Float", alias = "W", metaName = "Float", usage = "Float option.")
        private Float floatObjectValue;

        @Option(name = "double", alias = "v", metaName = "double", usage = "double option.")
        private double doubleValue;

        @Option(name = "Double", alias = "V", metaName = "Double", usage = "Double option.")
        private Double doubleObjectValue;

        @Option(name = "long", alias = "u", metaName = "long", usage = "long option.")
        private long longValue;

        @Option(name = "Long", alias = "U", metaName = "Long", usage = "Long option.")
        private Long longObjectValue;

        @Arguments(metaName = "args")
        private LinkedList<String> args;
    }

    private static class StubArgsRequired {
        @Option(name = "opti", alias = "i", required = true)
        private int integer;

        @Arguments(required = true)
        private ArrayList<String> args;
    }

    private static class StubArgsUnsupportedType {
        @Option(name = "m", alias = "optm")
        private Map<String, String> map;
    }

    private static class StubArgsDefault {
        @Option(name = "opti", alias = "i")
        private int integer = Integer.MIN_VALUE;

        @Option(name = "opts", alias = "s")
        private String str = "default";

        @Option(name = "optb", alias = "b")
        private Boolean bool;

        @Arguments
        private ArrayList<String> args;

        public StubArgsDefault() {
            bool = Boolean.TRUE;
            args = new ArrayList<String>();
            args.add("hoge");
        }
    }

    private static class StubArgsNoName {
        @Option(name = "hoge")
        private String hoge;

        @Option(name = "fuga", alias = "f")
        private String fuga;
    }

    private static class HashSetArgs {
        @Arguments
        HashSet<String> args;
    }
}
