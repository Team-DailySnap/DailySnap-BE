package onepiece.dailysnapbackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import onepiece.dailysnapbackend.object.dto.CustomUserDetails;
import onepiece.dailysnapbackend.object.dto.PhotoPostRequest;
import onepiece.dailysnapbackend.object.postgres.Member;
import onepiece.dailysnapbackend.service.PhotoService;
import onepiece.dailysnapbackend.util.log.LogMonitoringInvocation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/photo")
@Tag(
    name = "사진 게시물 관련 API",
    description = "사진 게시물 관련 API 제공"
)
public class PhotoController implements PhotoControllerDocs{

  private final PhotoService photoService;

  @Override
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @LogMonitoringInvocation
  public ResponseEntity<UUID> uploadPhoto(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @ModelAttribute PhotoPostRequest request) {
    Member member = userDetails.getMember();
    return ResponseEntity.ok(photoService.uploadPhoto(request, member));
  }
}
