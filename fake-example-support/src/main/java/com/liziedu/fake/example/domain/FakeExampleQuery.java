package com.liziedu.fake.example.domain;

public class FakeExampleQuery {

    public Integer getTest() {
        return test;
    }

    public void setTest(Integer test) {
        this.test = test;
    }

    private Integer test;

    @Override
    public String toString() {
        return "FakeExampleQuery{" +
                "test=" + test +
                '}';
    }
}
