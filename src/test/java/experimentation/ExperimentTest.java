package experimentation;

import com.bookinggo.business.MyClass;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExperimentTest {
    private ThreadLocalExperimentContext context = new ThreadLocalExperimentContext();

    @Test
    void variantClasses() {
        Map<Class, Integer> counts = createNumberOfTestObjectsWithExperimentVariants_and_GetVariantCounts(10);
        String countsString = counts.toString();

        assertHaveSome_A_Variants(countsString);
        assertHaveSome_Variants("B", countsString);
        assertHaveSome_Variants("C", countsString);
    }

    @Test
    void variantMethods() {
        Map<Class, Integer> counts = new HashMap<>();
        MyClass testObject = new MyClass("test");

        for (int i = 0; i < 20; i++) {
            String expectedVariant = setRandomVariantInContext();
            String actualVariant = testObject.someOtherMethod(999, "abc");
            assertEquals(expectedVariant, actualVariant);
            System.out.println("method variant: " + actualVariant);
        }
    }

    private void assertHaveSome_A_Variants(String countsString) {
        assertTrue(countsString.contains("MyClass="), format("expected A variant in %s", countsString));
    }

    private void assertHaveSome_Variants(String variant, String countsString) {
        assertTrue(countsString.contains(format("MyClass_Variant_%s=", variant)), format("expected %s variant in %s", variant, countsString));
    }

    private Map<Class, Integer> createNumberOfTestObjectsWithExperimentVariants_and_GetVariantCounts(int numbertoCreate) {
        Map<Class, Integer> counts = new HashMap<>();

        for (int i = 0; i < numbertoCreate; i++) {
            setRandomVariantInContext();
            MyClass testObject = new MyClass("test");
            count(testObject, counts);
            String result = testObject.someMethod(999, "some string arg");
            assertTrue(result.matches("[ABC]: 999 some string arg"));
        }

        return counts;
    }

    private String setRandomVariantInContext() {
        String variant = Character.toString((char) ('A' + new Random().nextInt(3)));
        context.setVariant(variant);
        return variant;
    }

    private static void count(MyClass testObject, Map<Class, Integer> counts) {
        int newCount = counts.getOrDefault(testObject.getClass(), 0) + 1;
        counts.put(testObject.getClass(), newCount);
    }
}
