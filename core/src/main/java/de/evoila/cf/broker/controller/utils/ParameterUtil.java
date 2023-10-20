package de.evoila.cf.broker.controller.utils;

import java.util.Optional;
import java.util.function.Supplier;

public class ParameterUtil {
    public static <T> Optional<T> resolve(Supplier<T> resolver) {
        try {
            T result = resolver.get();
            return Optional.ofNullable(result);
        }
        catch (NullPointerException e) {
            return Optional.empty();
        }
    }

}
