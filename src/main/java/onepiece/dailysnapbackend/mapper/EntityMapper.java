package onepiece.dailysnapbackend.mapper;

import onepiece.dailysnapbackend.object.dto.KeywordRequest;
import onepiece.dailysnapbackend.object.dto.MemberResponse;
import onepiece.dailysnapbackend.object.postgres.Keyword;
import onepiece.dailysnapbackend.object.postgres.Member;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EntityMapper {

  EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class);

  // Keyword 관련
  KeywordRequest toKeywordRequest(Keyword keyword);

  // Member 관련
  MemberResponse toMemberResponse(Member member);
}
