package net.ihiroky.ocm;

import net.ihiroky.ocm.handler.BooleanFieldOptionHandler;
import net.ihiroky.ocm.handler.ByteFieldOptionHandler;
import net.ihiroky.ocm.handler.CharFieldOptionHandler;
import net.ihiroky.ocm.handler.DoubleFieldOptionHandler;
import net.ihiroky.ocm.handler.FloatFieldOptionHandler;
import net.ihiroky.ocm.handler.IntFieldOptionHandler;
import net.ihiroky.ocm.handler.LongFieldOptionHandler;
import net.ihiroky.ocm.handler.ShortFieldOptionHandler;
import net.ihiroky.ocm.handler.StringFieldOptionHandler;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Parses command line arguments and store results to a target object.
 * <p/>
 * {@code ArgumentProcessor} stores values of command line options to a target object which fields are annotated
 * by {@link Option}. A {@code List} field annotated by {@link Arguments} in the target object stores values of
 * arguments. {@code boolean}, {@code byte}, {@code char}, {@code short}, {@code int}, {@code float}, {@code double}
 * and {@code String} type is supported. Wrapper classes for primitive types are also supported.
 * <p/>
 * This class is not synchronized.
 *
 * @author Hiroki Itoh
 */
public class ArgumentProcessor {

    /** handlers to parse options. */
    private List<FieldOptionHandler<?>> optionHandlerList;

    /** handlers to parse arguments. */
    private ArgumentsHandler argumentsHandler;

    /** a map which stores handlers according to their type. */
    private static final Map<Class<?>, Class<? extends FieldOptionHandler<?>>> HANDLER_MAP;

    /** a mark to show that the left command line arguments of this mark is treated as arguments. */
    private static final String ARGUMENT_ONLY = "--";

    /** a mark to show that a current command line arguments is a option. */
    private static final String OPTION_PREFIX = "-";

    static {
        Map<Class<?>, Class<? extends FieldOptionHandler<?>>> map =
                new HashMap<Class<?>, Class<? extends FieldOptionHandler<?>>>();
        map.put(Boolean.TYPE, BooleanFieldOptionHandler.class);
        map.put(Boolean.class, BooleanFieldOptionHandler.class);
        map.put(Byte.TYPE, ByteFieldOptionHandler.class);
        map.put(Byte.class, ByteFieldOptionHandler.class);
        map.put(Character.TYPE, CharFieldOptionHandler.class);
        map.put(Character.class, CharFieldOptionHandler.class);
        map.put(Short.TYPE, ShortFieldOptionHandler.class);
        map.put(Short.class, ShortFieldOptionHandler.class);
        map.put(Integer.TYPE, IntFieldOptionHandler.class);
        map.put(Integer.class, IntFieldOptionHandler.class);
        map.put(Long.TYPE, LongFieldOptionHandler.class);
        map.put(Long.class, LongFieldOptionHandler.class);
        map.put(Float.TYPE, FloatFieldOptionHandler.class);
        map.put(Float.class, FloatFieldOptionHandler.class);
        map.put(Double.TYPE, DoubleFieldOptionHandler.class);
        map.put(Double.class, DoubleFieldOptionHandler.class);
        map.put(String.class, StringFieldOptionHandler.class);
        HANDLER_MAP = Collections.unmodifiableMap(map);
    }

    /** */
    public ArgumentProcessor() {
    }

    /**
     * Parses annotations.
     *
     * @param target an object to be parsed.
     * @throws IllegalArgumentTypeException if target has unsupported type fields.
     */
    private void parseAnnotation(Object target) {
        List<FieldOptionHandler<?>> fieldOptionHandlerList = new ArrayList<FieldOptionHandler<?>>();
        ArgumentsHandler argsHandler = new ArgumentsHandler();
        Set<String> parsedNames = new HashSet<String>();
        Set<String> parsedAliases = new HashSet<String>();
        for (Class<?> c = target.getClass(); c != null; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                Option option = field.getAnnotation(Option.class);
                if (option != null) {
                    if (option.name().length() == 0) { // a property of annotation is not null.
                        throw new IllegalArgumentTypeException(
                                "option name for field " + field.getName() + " is required.");
                    }
                    Class<?> type = field.getType();
                    Class<? extends FieldOptionHandler<?>> factory = HANDLER_MAP.get(type);
                    if (factory == null) {
                        throw new IllegalArgumentTypeException(
                                "unsupported option type : " + type + ", option : " + option.name());
                    }
                    try {
                        FieldOptionHandler<?> handler = factory.newInstance();
                        handler.setTarget(target);
                        handler.setField(field);
                        handler.setOption(option);
                        if (parsedNames.contains(handler.getName())
                                || (handler.getAlias().length() > FieldOptionHandler.SHORT_NAME_SUFFIX.length()
                                && parsedAliases.contains(handler.getAlias()))) {
                            throw new IllegalArgumentTypeException("name [" + handler.getName()
                                    + "] or alias [" + handler.getAlias() + "] is duplicated.");
                        }
                        fieldOptionHandlerList.add(handler);
                        parsedAliases.add(handler.getAlias());
                        parsedNames.add(handler.getName());
                    } catch (IllegalArgumentTypeException iate) {
                        throw iate;
                    } catch (Exception e) {
                        // it's a bug if an exception is caught.
                        throw new RuntimeException("failed to instantiate OptionHandler.", e);
                    }
                    continue;
                }
                Arguments arguments = field.getAnnotation(Arguments.class);
                if (arguments != null) {
                    if (!Collection.class.isAssignableFrom(field.getType())) {
                        throw new IllegalArgumentTypeException(
                                "arguments type must be " + Collection.class.getName() + '.');
                    }
                    argsHandler.setTarget(target);
                    argsHandler.setField(field);
                    argsHandler.setArguments(arguments);
                }
            }
        }
        optionHandlerList = fieldOptionHandlerList;
        argumentsHandler = argsHandler;
    }

    /**
     * Parses command line arguments.
     *
     * @param target an object to store options and arguments, which fields are
     *               annotated by {@link Option} and {@link Arguments}.
     * @param args command line arguments.
     * @throws NullPointerException if {@code target} or {@code args} is null.
     * @throws ArgumentParseException if {@code args} contains invalid options or arguments.
     * @throws IllegalArgumentTypeException if {@code target} object has invalid option
     * @return target.
     */
    public <T> T parse(T target, String[] args) throws ArgumentParseException {

        if (target == null || args == null) {
            throw new NullPointerException("target or args is null.");
        }

        parseAnnotation(target);

        if (optionHandlerList == null || argumentsHandler == null) {
            throw new IllegalStateException("target object is not parsed."
                    + " instantiate with constructor ArgumentProcessor(Object) to use this method "
                    + " or use method parse(Object, String[]).");
        }

        // copy the list because of destructive operations.
        List<FieldOptionHandler<?>> ohlist = new ArrayList<FieldOptionHandler<?>>(this.optionHandlerList);
        ArgumentsHandler ah = this.argumentsHandler;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals(ARGUMENT_ONLY)) {
                // left args is arguments.
                for (int j = i + 1; j < args.length; j++) {
                    ah.add(args[j]);
                }
                break;
            } else if (arg.startsWith(OPTION_PREFIX)) {
                // options
                FieldOptionHandler<?> handler = search(arg, ohlist);
                if (handler == null) {
                    throw new ArgumentParseException("unknown option : " + arg);
                }

                Field field = handler.getField();
                if (Boolean.TYPE.equals(field.getType())
                        || Boolean.class.equals(field.getType())) {
                    // boolean if the option has no value.
                    handler.set(Boolean.TRUE.toString());
                } else {
                    i++;
                    if (i == args.length) {
                        throw new ArgumentParseException(
                                "no value is found for option " + handler.toString());
                    }
                    loadValuedOption(handler, args[i], ohlist);
                }
                ohlist.remove(handler);
            } else {
                // arguments
                ah.add(arg);
            }
        }
        for (FieldOptionHandler<?> oh : ohlist) {
            Option option = oh.getOption();
            if (option.required()) {
                throw new ArgumentParseException("option " + oh.toString() + " is required.");
            }
        }

        Arguments arguments = ah.getArguments();
        if (arguments != null && arguments.required() && ah.isEmpty()) {
            throw new ArgumentParseException("arguments are required.");
        }
        ah.ensureArgument();

        return target;
    }

    /**
     * Parses options which has a value.
     * @param handler a hanlder to hold {@code value}
     * @param value a valude to be held by {@code handler}
     * @param ohlist {@link FieldOptionHandler}
     * @throws ArgumentParseException if no value found for the {@code handler}.
     */
    private void loadValuedOption(FieldOptionHandler<?> handler, String value,
                                  List<FieldOptionHandler<?>> ohlist) throws ArgumentParseException {

        if (value.startsWith("-") && !handler.canAcceptHyphenValue()) {
            throw new ArgumentParseException("no value is found for option " + handler.toString());
        }
        if (search(value, ohlist) != null) {
            // error because no value found.
            throw new ArgumentParseException("no value is found for option " + handler.toString());
        }
        try {
            handler.set(value);
        } catch (RuntimeException re) {
            throw new ArgumentParseException("failed to parse option " + handler.toString(), re);
        }
    }

    /**
     * Searches handlers.
     *
     * @param name a name or alias of a handler to be searched.
     * @param handlers available handlers.
     * @return a handler that matches name.
     */
    private FieldOptionHandler<?> search(String name, List<FieldOptionHandler<?>> handlers) {
        for (FieldOptionHandler<?> handler : handlers) {
            if ((handler.getAlias().length() > 0 && handler.getAlias().equals(name))
                    || handler.getName().equals(name)) {
                return handler;
            }
        }
        return null;
    }

    /**
     * Prints one line usage an {@code PrintStream}.

     * @param out an output.
     */
    public void printOneLineUsage(PrintStream out) {
        StringBuilder usage = new StringBuilder();
        usage.append("available options : ");
        for (FieldOptionHandler<?> oh : optionHandlerList) {
            Option option = oh.getOption();

            if (!option.required()) {
                usage.append('[');
            }
            usage.append(oh.toString());
            usage.append(' ');
            String metaName = (option.metaName().length() == 0) ? oh.getField().getName() : option.metaName();
            usage.append(metaName);
            if (!option.required()) {
                usage.append(']');
            }
            usage.append(' ');
        }
        if (usage.length() > 0) {
            usage.deleteCharAt(usage.length() - 1);
        }
        out.println(usage.toString());
    }

    /**
     * Prints usage.
     * @param out output.
     */
    public void printUsage(PrintStream out) {
        StringBuilder usage = new StringBuilder();
        String ls = System.getProperty("line.separator");
        usage.append("available options :").append(ls);
        for (FieldOptionHandler<?> oh : optionHandlerList) {
            Option option = oh.getOption();

            usage.append(' ');
            if (oh.getAlias().endsWith(FieldOptionHandler.SHORT_NAME_SUFFIX)) {
                usage.append("    ");
            } else {
                usage.append(oh.getAlias()).append(", ");
            }
            String longName = oh.getName().equals(FieldOptionHandler.LONG_NAME_SUFFIX) ? "" : oh.getName();
            usage.append(longName).append(" : ").append(option.usage());
            if (option.required()) {
                usage.append(" (required)");
            }
            usage.append(ls);
        }

        int length = usage.length();
        if (length > 0) {
            usage.delete(length - ls.length(), length);
        }
        out.println(usage.toString());
    }
}
