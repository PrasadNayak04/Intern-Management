package com.robosoft.internmanagement.service.batchScheduleService;


import org.springframework.batch.item.ItemProcessor;

public class MyCustomProcessor implements ItemProcessor<String, String> {
    @Override
    public String process(String data) throws Exception {
        System.out.println("MyCustomProcessor : Processing data : "+data);
        data = data.toUpperCase();
        return data;
    }
}
