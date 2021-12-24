package com.liziedu.fake.example.domain;

import java.util.List;

public class FakeTestDTO {

    private Long id;

    private List<String> names;

    private FakeTestProductDTO product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public FakeTestProductDTO getProduct() {
        return product;
    }

    public void setProduct(FakeTestProductDTO product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "FakeTestDTO{" +
                "id=" + id +
                ", names=" + names +
                ", product=" + product +
                '}';
    }
}
