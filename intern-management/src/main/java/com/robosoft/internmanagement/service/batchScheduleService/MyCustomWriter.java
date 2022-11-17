package com.robosoft.internmanagement.service.batchScheduleService;


import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class MyCustomWriter implements ItemWriter<String> {
    @Override
    public void write(List<? extends String> list) throws Exception {
        for (String data : list) {
            System.out.println("MyCustomWriter    : Writing data    : " + data);
        }
        System.out.println("MyCustomWriter    : Writing data    : completed");
    }
}
