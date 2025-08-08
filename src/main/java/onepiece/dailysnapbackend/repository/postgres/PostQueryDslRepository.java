package onepiece.dailysnapbackend.repository.postgres;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.dto.PostFilteredRequest;
import onepiece.dailysnapbackend.object.postgres.Post;
import onepiece.dailysnapbackend.object.postgres.QPost;
import onepiece.dailysnapbackend.util.QueryDslUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostQueryDslRepository {

  private static final QPost POST = QPost.post;

  private final JPAQueryFactory queryFactory;

  public Page<Post> filteredPost(PostFilteredRequest request) {

    BooleanExpression whereClause = QueryDslUtil.allOf(
        QueryDslUtil.eqIfNotNull(POST.keyword.keywordId, request.getKeywordId()),
        QueryDslUtil.likeIgnoreCase(POST.keyword.koreanKeyword, request.getKoreanKeyword()),
        QueryDslUtil.likeIgnoreCase(POST.keyword.englishKeyword, request.getEnglishKeyword()),
        QueryDslUtil.likeIgnoreCase(POST.description, request.getDescription())
    );

    Pageable pageable = request.toPageable();

    JPAQuery<Post> contentQuery = queryFactory
        .select(POST)
        .from(POST)
        .join(POST.member).fetchJoin()
        .join(POST.keyword).fetchJoin()
        .where(whereClause);

    QueryDslUtil.applySorting(
        contentQuery,
        pageable,
        Post.class,
        POST.getMetadata().getName()
    );

    JPAQuery<Long> countQuery = queryFactory
        .select(POST.count())
        .from(POST)
        .where(whereClause);

    return QueryDslUtil.fetchPage(contentQuery, countQuery, pageable);
  }
}
