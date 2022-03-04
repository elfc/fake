package com.liziedu.fake.example.controller;

import com.liziedu.fake.example.domain.FakeExampleQuery;
import com.liziedu.fake.example.domain.FakeTestDTO;
import com.liziedu.fake.example.domain.FakeTestPageResponse;
import com.liziedu.fake.example.domain.FakeTestResponse;
import com.liziedu.fake.example.service.FakeExampleService;
import com.liziedu.fake.example.service.FakeSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/fake")
@RestController
public class FakeExampleController {

    @Autowired
    private FakeSearchService fakeSearchService;

    @Autowired
    private FakeExampleService fakeExampleService;

    @GetMapping()
    public FakeTestResponse<FakeTestDTO> searchByPath() {
        return fakeSearchService.searchByPath("example", 1, 666L);
    }

    @GetMapping("/example")
    public FakeTestResponse<FakeTestPageResponse<FakeTestDTO>> example() {
        return fakeExampleService.homePage();
    }

    @GetMapping("/query")
    public FakeTestResponse<FakeTestDTO> searchByQuery() {
        FakeExampleQuery query = new FakeExampleQuery();
        query.setTest(1);

        return fakeSearchService.searchForQuery(query);
    }
}
