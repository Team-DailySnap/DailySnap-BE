package onepiece.dailysnapbackend.repository.postgres;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.dto.KeywordFilterRequest;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.object.postgres.QKeyword;
import onepiece.dailysnapbackend.util.QueryDslUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KeywordQueryDslRepository {

  private static final QKeyword KEYWORD = QKeyword.keyword;

  private final JPAQueryFactory queryFactory;

  public Page<Keyword> filteredKeyword(KeywordFilterRequest request) {
    BooleanExpression whereClause = QueryDslUtil.allOf(
        QueryDslUtil.likeIgnoreCase(KEYWORD.koreanKeyword, request.getKoreanKeyword()),
        QueryDslUtil.eqIfNotNull(KEYWORD.keywordCategory, request.getKeywordCategory()),
        QueryDslUtil.eqIfNotNull(KEYWORD.providedDate, request.getProvidedDate()),
        QueryDslUtil.eqIfNotNull(KEYWORD.used, request.getUsed())
    );

    Pageable pageable = request.toPageable();

    JPAQuery<Keyword> contentQuery = queryFactory
        .select(KEYWORD)
        .from(KEYWORD)
        .where(whereClause);

    QueryDslUtil.applySorting(
        contentQuery,
        pageable,
        Keyword.class,
        KEYWORD.getMetadata().getName()
    );

    JPAQuery<Long> countQuery = queryFactory
        .select(KEYWORD.count())
        .from(KEYWORD)
        .where(whereClause);

    return QueryDslUtil.fetchPage(contentQuery, countQuery, pageable);
  }
}
