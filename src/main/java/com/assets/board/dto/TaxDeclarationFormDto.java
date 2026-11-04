package com.assets.board.dto;

import lombok.Data;

@Data
public class TaxDeclarationFormDto {
    private String name;
    private String tin;
    private short year;
    private String taxOfficeId;
    private String city;
    private String street;
    private String building;
}