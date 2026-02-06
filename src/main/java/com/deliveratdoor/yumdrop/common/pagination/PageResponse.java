package com.deliveratdoor.yumdrop.common.pagination;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResponse<T> {
    // Offset metadata
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;

    // For Cursor based pagination
    // private String nextCursor;
    private Boolean hasNext;
    private List<T> data;
}
