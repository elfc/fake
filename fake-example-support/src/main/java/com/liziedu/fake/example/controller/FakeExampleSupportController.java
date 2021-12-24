package com.liziedu.fake.example.controller;

import com.liziedu.fake.example.domain.FakeTestDTO;
import com.liziedu.fake.example.domain.FakeTestPageResponse;
import com.liziedu.fake.example.domain.FakeTestProductDTO;
import com.liziedu.fake.example.domain.FakeTestResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/test")
public class FakeExampleSupportController {

    @GetMapping("/homePage")
    public FakeTestResponse<FakeTestPageResponse<FakeTestDTO>> homePage() {
        FakeTestResponse<FakeTestPageResponse<FakeTestDTO>> fakeTestResponse = new FakeTestResponse<>();

        fakeTestResponse.setErrcode(200);
        fakeTestResponse.setErrmsg("成功");


        FakeTestPageResponse<FakeTestDTO> fakeTestPageResponse = new FakeTestPageResponse<>();
        fakeTestPageResponse.setPage(10);
        fakeTestPageResponse.setPagesize(20);
        fakeTestPageResponse.setTotal(1000);

        List<FakeTestDTO> list = new ArrayList<>();
        FakeTestDTO fakeTestDTO = new FakeTestDTO();
        fakeTestDTO.setId(1L);

        List<String> names = new ArrayList<>();
        names.add("name1");
        names.add("name2");
        fakeTestDTO.setNames(names);

        FakeTestProductDTO productDTO = new FakeTestProductDTO();
        productDTO.setName("productDTO1");
        fakeTestDTO.setProduct(productDTO);

        list.add(fakeTestDTO);

        fakeTestPageResponse.setList(list);

        fakeTestResponse.setData(fakeTestPageResponse);

        return fakeTestResponse;
    }
}
