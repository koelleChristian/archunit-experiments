package de.koelle.christian.archunit.layer1a;

import de.koelle.christian.archunit.layer2a.MyLayer2Clazz;
import de.koelle.christian.archunit.layer2a.MyLayer2Ids;
import de.koelle.christian.archunit.layer2a.MyLayer2StaticUtil;
import de.koelle.christian.archunit.layer3a.MyLayer3Ids;
import de.koelle.christian.archunit.layer3a.MyLayer3Object;

public class MyLayer1Clazz {

    // @Inject
    // @Autowire
    private MyLayer2Clazz service = new MyLayer2Clazz();


    public void doSomething1(String x) {
        String result = service.doSomething(x);
    }

    public void doSomething2(String x) {
        // -------------------- Constant Access
        // Next statement: Valid as expected
        System.out.println(MyLayer2Ids.CONSTANT_LAYER_2);
        // Next statement: Valid, not wanted, but for Strings and Primitives not testable
        // by Archunit as 'not referenced as dependencies in the byte code, but just inlined
        // in the constant pool of the class that uses
        // it' see: https://github.com/TNG/ArchUnit/issues/1115
        System.out.println(MyLayer3Ids.CONSTANT_LAYER_3);

        // -------------------- Static Acces
        // Next statement: Valid as expected
        System.out.println(MyLayer2StaticUtil.myStaticMethod("whatever"));
        // Next statement: Invalid as expected (for all variants: layer, dependOnClassesThat,
        // accessClassesThat)
//        System.out.println(MyLayer3StaticUtil.myStaticMethod("whatever"));

        // -------------------- Normal Access
        // Next statement: Valid: It is valid to obtain a 'forbidden' object on layer1 from
        // layer3 via the public signature of 'layer2'
        MyLayer3Object layer3Obj = service.doSomethingElse(x);
        // Next statement: Valid: Dito and you can do something with it.
        System.out.println(layer3Obj);

        // Next statement: invalid as expected: You cannot access a 'forbidden' object.
        // (for all variants: layer, dependOnClassesThat, accessClassesThat)
//         System.out.println(layer3Obj.toString());

        // Next statement: invalid as expected: You cannot create a 'forbidden' object.
        // (for all variants: layer, dependOnClassesThat, accessClassesThat)
//         new MyLayer3Object(x); // invalid
    }
}
