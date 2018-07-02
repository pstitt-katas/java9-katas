package com.bookinggo.business;

public class MyClass_Variant_C extends MyClass {
    public MyClass_Variant_C(String i) {
        super(i);
        System.out.println("C CTOR");
    }

    @Override
    public String someMethod(int a, String b) {
        return "C: " + a + " " + b;
    }
}
