package stark.a.is.zhang.utils;

/**
 * This Class used to convert origin object to int, string or other specific object
 * */

public class ConvertUtil {
    public static int convertToInt(Object value, int defaultValue) {
        if (value == null || "".equals(value.toString().trim())) {
            return defaultValue;
        }

        try {
            return Integer.valueOf(value.toString());
        } catch (Exception e) {
            try {
                return Double.valueOf(value.toString()).intValue();
            } catch (Exception e1) {
                return defaultValue;
            }
        }
    }
}