package onepiece.dailysnapbackend.object.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import onepiece.dailysnapbackend.object.constants.PostSortField;
import onepiece.dailysnapbackend.util.PageableConstants;
import onepiece.dailysnapbackend.util.PageableUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PostFilteredRequest {

  private UUID keywordId;

  private String koreanKeyword;

  private String englishKeyword;

  private String description;

  private int pageNumber;

  private int pageSize;

  private PostSortField sortField;

  private Sort.Direction sortDirection;

  public PostFilteredRequest() {
    this.pageNumber = 1;
    this.pageSize = PageableConstants.DEFAULT_PAGE_SIZE;
    this.sortField = PostSortField.CREATED_DATE;
    this.sortDirection = Direction.DESC;
  }

  public Pageable toPageable() {
    return PageableUtil.createPageable(
        pageNumber,
        pageSize,
        PageableConstants.DEFAULT_PAGE_SIZE,
        sortField,
        sortDirection
    );
  }
}
