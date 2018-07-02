package com.bookinggo.business;

import experimentation.Experiment;
import experimentation.catalogue.MyExperiment;

public class MyClass {
    @Experiment(definition = MyExperiment.class)
    public MyClass(String i) {
        System.out.println("A CTOR");
    }

    public String someMethod(int a, String b) {
        return "A: " + a + " " + b;
    }

    @Experiment(definition = MyExperiment.class)
    public String someOtherMethod(int a, String b) {
        return "A";
    }

    public String someOtherMethod_Variant_B(int a, String b) {
        return "B";
    }

    public String someOtherMethod_Variant_C(int a, String b) {
        return "C";
    }
}
