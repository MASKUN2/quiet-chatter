package maskun.quietchatter.adaptor.naver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NaverBookItem(

        // 책 제목
        @JsonProperty("title")
        String title,

        // 네이버 도서 정보 URL
        @JsonProperty("link")
        String link,

        // 섬네일 이미지의 URL
        @JsonProperty("image")
        String image,

        // 저자 이름
        @JsonProperty("author")
        String author,

        // 정가
        @JsonProperty("price")
        String price,

        // 판매 가격 (할인가)
        @JsonProperty("discount")
        String discount,

        // 출판사
        @JsonProperty("publisher")
        String publisher,

        // ISBN
        @JsonProperty("isbn")
        String isbn,

        // 네이버 도서의 책 소개
        @JsonProperty("description")
        String description,

        // 출간일
        @JsonProperty("pubdate")
        String pubdate
) {
}
