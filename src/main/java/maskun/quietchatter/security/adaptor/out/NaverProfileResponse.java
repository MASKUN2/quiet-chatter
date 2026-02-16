package maskun.quietchatter.security.adaptor.out;

public record NaverProfileResponse(
        String resultcode,
        String message,
        Response response
) {
    public record Response(
            String id,
            String nickname,
            String name,
            String email,
            String gender,
            String age,
            String birthday,
            String profile_image,
            String birthyear,
            String mobile
    ) {
    }
}
