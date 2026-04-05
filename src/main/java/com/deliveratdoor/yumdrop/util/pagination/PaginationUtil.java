package com.deliveratdoor.yumdrop.util.pagination;

import com.deliveratdoor.yumdrop.common.pagination.PaginationRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import static org.apache.logging.log4j.util.Strings.isBlank;

public class PaginationUtil {

    public static Pageable toPageable(PaginationRequest request) {
        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }

    /**
     * Utility method to decode given cursor to item id for cursor based pagination
     * @param cursor - encoded ast item id
     * @return decoded lat item id
     */
    public static Long decodeCursor(String cursor) {
        if (isBlank(cursor)) return null;
        return Long.valueOf(new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8));
    }

    /**
     * Utility method to encode last item id for cursor based pagination
     * @param id - last item id
     * @return encoded cursor
     */
    public static String encodeCursor(Long id) {
        if (Objects.isNull(id)) return null;
        return Base64.getEncoder().encodeToString(id.toString().getBytes(StandardCharsets.UTF_8));
    }
}
