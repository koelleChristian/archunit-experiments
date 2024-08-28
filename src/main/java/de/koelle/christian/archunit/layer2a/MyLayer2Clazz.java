package de.koelle.christian.archunit.layer2a;

import de.koelle.christian.archunit.layer3a.MyLayer3Clazz;
import de.koelle.christian.archunit.layer3a.MyLayer3Object;

public class MyLayer2Clazz {

    // @Inject
    // @Autowire
    private MyLayer3Clazz data = new MyLayer3Clazz();

    public String doSomething(String x) {
        return data.doSomething(x);
    }
    public MyLayer3Object doSomethingElse(String x) {
        return new MyLayer3Object(x);
    }
}
