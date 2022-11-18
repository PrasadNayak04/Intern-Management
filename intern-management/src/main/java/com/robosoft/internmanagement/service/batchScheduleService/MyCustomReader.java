package com.robosoft.internmanagement.service.batchScheduleService;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class MyCustomReader implements ItemReader<String> {
    private String[] stringArray = { "Zero", "One", "Two", "Three", "Four", "Five" };

    private int index = 0;

    @Override
    public String read() throws Exception, UnexpectedInputException,
            ParseException, NonTransientResourceException {
        if (index >= stringArray.length) {
            return null;
        }

        String data = index + " " + stringArray[index];
        index++;
        System.out.println("MyCustomReader    : Reading data    : "+ data);
        return data;
    }
}
