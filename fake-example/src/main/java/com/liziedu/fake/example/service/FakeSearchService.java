package com.liziedu.fake.example.service;

import com.liziedu.fake.core.*;
import com.liziedu.fake.example.domain.*;

import java.util.List;

@FakeClient(domain = "http://127.0.0.1:9001")
public interface FakeSearchService {

    /**
     * 列表页
     * @param condition 搜索条件
     * @return
     */
    @FakeRequestMapping("/test/search")
    FakeTestResponse<FakeTestDTO> search(@FakeRequestParam(value = "condition") String condition,
                         @FakeRequestParam(value = "page") int page);

    /**
     * 列表页
     * @param condition 搜索条件
     * @return
     */
    @FakeRequestMapping("/test/search/{id}/list")
    FakeTestResponse<FakeTestDTO> searchByPath(
                        @FakeRequestParam(value = "condition") String condition,
                        @FakeRequestParam(value = "page") int page,
                        @FakePathVariable(value = "id") Long id);

    /**
     * 列表页
     * @param condition 搜索条件
     * @return
     */
    @FakeRequestMapping("/test/search/{id}/list/id")
    FakeTestResponse<FakeTestDTO> searchIdListByPath(
            @FakeHeaders("headers") FakeExampleQuery query,
            @FakePathVariable(value = "id") Long id,
            @FakeRequestParam(value = "condition") String condition,
            @FakeRequestParam(value = "page") int page,
            @FakeRequestParam(value = "list") List<Integer> idList);

    /**
     * 搜索页
     * @return
     */
    @FakeRequestMapping("/test/search_for_query")
    FakeTestResponse<FakeTestDTO> searchForQuery(FakeExampleQuery query);
}
