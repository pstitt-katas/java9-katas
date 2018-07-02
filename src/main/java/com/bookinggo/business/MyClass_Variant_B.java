package com.bookinggo.business;

public class MyClass_Variant_B extends MyClass {
    public MyClass_Variant_B(String i) {
        super(i);
        System.out.println("B CTOR");
    }

    @Override
    public String someMethod(int a, String b) {
        return "B: " + a + " " + b;
    }
}
