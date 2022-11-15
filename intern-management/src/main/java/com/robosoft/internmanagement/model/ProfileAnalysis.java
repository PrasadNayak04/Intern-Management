package com.robosoft.internmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Date;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ProfileAnalysis {

    private String name;
    private String imageUrl;
    private String skills;
    private String position;

}
