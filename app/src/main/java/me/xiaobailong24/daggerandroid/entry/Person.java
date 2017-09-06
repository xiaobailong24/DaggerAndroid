package me.xiaobailong24.daggerandroid.entry;

import javax.inject.Inject;

/**
 * Created by xiaobailong24 on 2017/9/6.
 * Dagger Person
 */

public class Person {
    private String name;
    private int age;

    @Inject
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
