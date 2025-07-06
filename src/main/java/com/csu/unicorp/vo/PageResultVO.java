package com.csu.unicorp.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResultVO<T> {
    private long total;
    private List<T> list;
}
