package com.csu.unicorp.dto;

import lombok.Data;

@Data
public class ProjectFundReviewDTO {
    private String status; // approved/rejected
    private Integer reviewerId;
    private String comment;
}
