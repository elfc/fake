package com.liziedu.fake.example.domain;

public class FakeTestProductDTO {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FakeTestProductDTO{" +
                "name='" + name + '\'' +
                '}';
    }
}
