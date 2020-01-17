package de.evoila.cf.broker.util;

public class UuidUtils {

    public static final String UUID_REGEX = "^[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}$";

    public static final String NOT_A_UUID_MESSAGE = "The ServiceInstanceID is not a valid UUID. Please only use valid UUIDs," +
            " too avoid unexpected errors.";

}
