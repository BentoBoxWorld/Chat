package world.bentobox.chat;

/**
 * Utility class for setting private/static fields via reflection in tests.
 */
public class WhiteBox {

    /**
     * Sets the value of a private static field using Java Reflection.
     * @param targetClass The class containing the static field.
     * @param fieldName   The name of the private static field.
     * @param value       The value to set the field to.
     */
    public static void setInternalState(Class<?> targetClass, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(
                    "Failed to set static field '" + fieldName + "' on class " + targetClass.getName(), e);
        }
    }

    /**
     * Sets the value of a private instance field using Java Reflection.
     * @param target    The object instance containing the field.
     * @param fieldName The name of the private field.
     * @param value     The value to set the field to.
     */
    public static void setInternalState(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(
                    "Failed to set field '" + fieldName + "' on " + target.getClass().getName(), e);
        }
    }

    /**
     * Creates an instance of a class without calling its constructor.
     * Useful for classes that require special classloaders (e.g., JavaPlugin subclasses).
     * @param clazz The class to instantiate.
     * @param <T>   The type of the class.
     * @return A new uninitialized instance.
     */
    @SuppressWarnings("unchecked")
    public static <T> T newUninitializedInstance(Class<T> clazz) {
        try {
            return (T) getUnsafe().allocateInstance(clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create uninitialized instance of " + clazz.getName(), e);
        }
    }

    private static sun.misc.Unsafe getUnsafe() {
        try {
            java.lang.reflect.Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (sun.misc.Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get Unsafe instance", e);
        }
    }
}
