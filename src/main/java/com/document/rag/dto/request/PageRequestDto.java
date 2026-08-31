package com.document.rag.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PageRequestDto {

    private int page = 0;

    private int size = 10;

    private List<String> sort;
}
