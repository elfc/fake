package com.liziedu.fake.example.nativedemo;

import com.liziedu.fake.core.Fake;
import com.liziedu.fake.core.FakeTarget;
import com.liziedu.fake.example.domain.FakeExampleQuery;
import com.liziedu.fake.example.domain.FakeTestDTO;
import com.liziedu.fake.example.domain.FakeTestPageResponse;
import com.liziedu.fake.example.domain.FakeTestResponse;
import com.liziedu.fake.example.service.FakeExampleService;
import com.liziedu.fake.example.service.FakeSearchService;

import java.util.List;

public class FakeNativeExampleDemo {

    private final FakeSearchService fakeSearchService;

    private final FakeExampleService fakeExampleService;

    public FakeNativeExampleDemo() {
        FakeTarget<FakeSearchService> target = new FakeTarget.DefaultTarget<>(FakeSearchService.class, "http://127.0.0.1:9001");
        this.fakeSearchService = new Fake.Builder().build().newInstance(target);

        FakeTarget<FakeExampleService> targetExample =
                new FakeTarget.DefaultTarget<>(FakeExampleService.class, "http://127.0.0.1:9001");
        this.fakeExampleService = new Fake.Builder().build().newInstance(targetExample);
    }

    public FakeTestResponse<FakeTestPageResponse<FakeTestDTO>> homePage() {
        return fakeExampleService.homePage();
    }

    public FakeTestResponse<FakeTestDTO> search(String condition, int page) {
        return fakeSearchService.search(condition, page);
    }

    public FakeTestResponse<FakeTestDTO> searchByPath(long id, String condition, int page) {
        return fakeSearchService.searchByPath(condition, page, id);
    }

    public FakeTestResponse<FakeTestDTO> searchIdListByPath(
            FakeExampleQuery query,
            long id,
            String condition,
            int page,
            List<Integer> idList) {
        return fakeSearchService.searchIdListByPath(query, id, condition, page,  idList);
    }

    public FakeTestResponse<FakeTestDTO> searchForQuery(FakeExampleQuery query) {
        return fakeSearchService.searchForQuery(query);
    }

    public static void main(String[] args) {
        FakeNativeExampleDemo fakeNativeExampleDemo = new FakeNativeExampleDemo();
        System.out.println(fakeNativeExampleDemo.homePage());

        fakeNativeExampleDemo.search("1111", 222);
//        requestDemo.searchForQuery(new BaiduQuery());
//        requestDemo.searchByPath(1000L, "5555", 223);

//        List<Integer> idList = new ArrayList<>();
//        idList.add(1);
//        idList.add(2);
//
//        BaiduQuery query = new BaiduQuery();
//        query.setId(1);
//        query.setName("t");
//
//        requestDemo.searchIdListByPath(query, 2000L, "6666", 88, idList);

    }
}
