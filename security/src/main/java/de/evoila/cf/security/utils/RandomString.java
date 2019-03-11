package de.evoila.cf.security.utils;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

/**
 * @author Johannes Hiemer-
 */
public class RandomString {

    /**
     * Generate a random string.
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];

        String randomString = new String(buf);

        if (this.lowerCase)
            return randomString.toLowerCase();
        else
            return randomString;
    }

    public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String lower = upper.toLowerCase(Locale.ROOT);

    public static final String digits = "0123456789";

    private final Random random;

    private boolean lowerCase;

    private boolean useNumbers;

    private final char[] symbols;

    private final char[] buf;

    public RandomString(int length, Random random, boolean useNumbers, boolean lowerCase) {
        this.lowerCase = lowerCase;
        this.useNumbers = useNumbers;

        if (length < 1)
            throw new IllegalArgumentException();

        String symbols = upper + lower;
        if (this.useNumbers)
            symbols = symbols + digits;

        if (symbols.length() < 2)
            throw new IllegalArgumentException();

        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    public RandomString(int length, boolean useNumbers, boolean lowerCase) {
        this(length, new SecureRandom(), useNumbers, lowerCase);
    }

    public RandomString() {
        this(21);
    }

    public RandomString(int length) {
        this(length, true, false);
    }

}
