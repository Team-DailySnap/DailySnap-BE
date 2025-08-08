package onepiece.dailysnapbackend.object.dto;

public record LoginResponse(
    String accessToken,
    String refreshToken
) {
}
