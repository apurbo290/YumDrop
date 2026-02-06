package com.deliveratdoor.yumdrop.common.pagination;

import lombok.Data;

@Data
public class PaginationRequest {

    // Offset pagination
    private Integer page = 0;
    private Integer size = 100;

    // Cursor pagination (preferred for feeds)
    // private String cursor;

    // Sorting
    private String sortBy = "id";
    private String direction = "DESC";
}
