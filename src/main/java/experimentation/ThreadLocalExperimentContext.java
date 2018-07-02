package experimentation;

public class ThreadLocalExperimentContext implements ExperimentContext {
    // TODO - just for spike - change to use a map of experiments and variants
    private static ThreadLocal<String> variant = new ThreadLocal<>();

    @Override
    public String getVariant(ExperimentDefinition experiment) {
        String result = variant.get();
        if (result == null) {
            result = "A";
        }
        return result;
    }

    @Deprecated
    public static void setVariant(String variant) {
        ThreadLocalExperimentContext.variant.set(variant);
    }
}
