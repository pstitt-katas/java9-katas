package experimentation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
public class ExperimentAspect {
    @Pointcut("@annotation(experiment)")
    public void experimentableClassMethod(Experiment experiment) {
    }

    @Around("experimentableClassMethod(experiment)")
    public Object around(ProceedingJoinPoint pjp, Experiment experiment) throws Throwable {
        try {
            ExperimentDefinition definition = definition(experiment);
            ExperimentContext context = context(experiment);
            String variant = context.getVariant(definition);
            String aClassname = pjp.getStaticPart().getSignature().getDeclaringType().getCanonicalName();

            if (pjp.getKind().contains("constructor-call")) {
//            System.out.println("classExperiment " + experimentName(experiment) + " being applied to " + pjp.getKind() + " " + aClassname);

                String variantClassname = getVariantClassname(aClassname, variant);
                Constructor variantConstructor = getVariantConstructor(variantClassname, pjp.getArgs());

                if (variantConstructor != null) {
                    return proceedWithConstructorVariant(variantConstructor, pjp.getArgs());
                }
            } else if (pjp.getKind().equals("method-call")) {
                String aVariantMethodName = pjp.getSignature().getName();
                System.out.println("experiment " + experimentName(experiment) + " being applied to " + pjp.getKind() + " " + aVariantMethodName);
                String variantMethodName = getVariantMethodName(aVariantMethodName, variant);
                Method variantMethod = getVariantMethod(pjp.getTarget().getClass(), variantMethodName);

                if (variantMethod != null) {
                    Object target = pjp.getTarget();
                    return proceedWithMethodVariant(target, variantMethod, pjp.getArgs());
                }
            }
        } catch (Exception x) {
            System.err.println("CTOR FAILED - FALLING BACK TO A VARIANT");
            x.printStackTrace();
        }

        return proceedWithA(pjp);
    }

    private static ExperimentDefinition definition(Experiment experiment) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        return experiment.definition().getConstructor().newInstance();
    }

    private static ExperimentContext context(Experiment experiment) throws IllegalAccessException, InstantiationException {
        return experiment.context().newInstance();
    }

    private String experimentName(Experiment experiment) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return experiment.definition().getConstructor().newInstance().name();
    }

    private Object proceedWithA(ProceedingJoinPoint pjp) throws Throwable {
        return pjp.proceed();
    }

    private Object proceedWithConstructorVariant(Constructor variantConstructor, Object... args) throws Throwable {
        return variantConstructor.newInstance(args);
    }

    private Constructor getVariantConstructor(String variantClassname, Object... args) throws ClassNotFoundException {
        Class experimentClass = Class.forName(variantClassname);
        Constructor[] constructors = experimentClass.getConstructors();
        for (Constructor constructor : constructors) {
            if (constructorSignature(constructor).equals(argsSignature(args))) {
                return constructor;
            }
        }
        return null;
    }

    private String argsSignature(Object... args) {
        return Arrays.asList(args).stream()
                .map(arg -> arg.getClass().getCanonicalName())
                .collect(Collectors.joining());
    }

    private String constructorSignature(Constructor constructor) {
        return Arrays.asList(constructor.getParameterTypes()).stream()
                .map(clazz -> clazz.getCanonicalName())
                .collect(Collectors.joining());
    }

    private String getVariantClassname(String aClassname, String variant) {
        if (variant.equals("A")) {
            return aClassname;
        } else {
            return aClassname + "_Variant_" + variant.toUpperCase();
        }
    }

    private Method getVariantMethod(Class<?> experimentClass, String variantMethodName) {
        Method[] methods = experimentClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(variantMethodName)) {
                return method;
            }
        }
        return null;
    }

    private String getVariantMethodName(String aVariantMethodName, String variant) {
        if (variant.equals("A")) {
            return aVariantMethodName;
        } else {
            return aVariantMethodName + "_Variant_" + variant.toUpperCase();
        }
    }

    private Object proceedWithMethodVariant(Object target, Method variantMethod, Object... args) throws Throwable {
        return variantMethod.invoke(target, args);
    }

}
