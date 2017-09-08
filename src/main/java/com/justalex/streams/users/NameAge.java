package com.justalex.streams.users;

public class NameAge {

    String name;
    int age;

    public NameAge(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NameAge nameAge = (NameAge) o;

        if (age != nameAge.age) return false;
        return name != null ? name.equals(nameAge.name) : nameAge.name == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + age;
        return result;
    }

    @Override
    public String toString() {
        return "NameAge{" +
            "name='" + name + '\'' +
            ", age=" + age +
            '}';
    }
}
