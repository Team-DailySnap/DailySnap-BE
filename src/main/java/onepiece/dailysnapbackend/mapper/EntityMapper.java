package onepiece.dailysnapbackend.mapper;

import onepiece.dailysnapbackend.object.dto.BestPostResponse;
import onepiece.dailysnapbackend.object.dto.KeywordFilterResponse;
import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.object.postgres.Post;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EntityMapper {
  EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class);

  // Keyword 관련
  KeywordFilterResponse toKeywordFilterResponse(Keyword keyword);
  KeywordRequest toKeywordRequest(Keyword keyword);

  // BestPost 관련
  BestPostResponse toBestPostResponse(Post post);

}
