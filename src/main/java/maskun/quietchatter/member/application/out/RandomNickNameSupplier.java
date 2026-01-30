package maskun.quietchatter.member.application.out;

import org.jspecify.annotations.NullMarked;

@NullMarked
@FunctionalInterface
public interface RandomNickNameSupplier {
    String get();
}
