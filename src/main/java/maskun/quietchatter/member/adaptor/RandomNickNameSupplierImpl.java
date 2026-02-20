package maskun.quietchatter.member.adaptor;

import maskun.quietchatter.member.application.in.RandomNickNameSupplier;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@NullMarked
@Component
class RandomNickNameSupplierImpl implements RandomNickNameSupplier {
    private static final List<String> prefixes = List.of(
            "현명한", "행복한", "밝은", "활기찬",
            "친절한", "사랑스러운", "사려깊은", "따뜻한"
    );

    private static final List<String> names = List.of(
            "독서가", "여행자", "탐험가", "다독가",
            "책바라기", "문장수집가", "글벗", "글지기", "책수집가"
    );

    @Override
    public String get() {
        String prefix = prefixes.get(ThreadLocalRandom.current().nextInt(prefixes.size()));
        String name = names.get(ThreadLocalRandom.current().nextInt(names.size()));
        return prefix + " " + name;
    }
}
