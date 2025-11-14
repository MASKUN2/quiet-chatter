package maskun.quietchatter.hexagon.domain.member;

public record Secret(String raw) {

    @Override
    public String toString() {
        return "숨겨짐";
    }
}
