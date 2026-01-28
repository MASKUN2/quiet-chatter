# 코드 리뷰 보고서

## 커밋 1: `898d032025c13a4225ba40317351971f20a72c1c`
**제목**: `test: add setup and database cleanup for TalkRepositoryTest`

### 코멘트
1. **`clearAll`의 예외 처리 미흡**
   - **파일**: `src/test/java/maskun/quietchatter/WithTestContainerDatabases.java`
   - **문제점**: `clearAll()` 메서드 내의 `catch (Exception ignored)` 블록이 예외를 무시하고 있습니다. 만약 `psql`이나 `redis-cli` 실행이 실패하더라도 이를 알 수 없어, 테스트 환경이 오염된 상태로 다음 테스트가 진행될 수 있습니다. 이는 테스트 실패 원인을 찾기 어렵게 만듭니다.
   - **제안**: 최소한 예외를 로그로 남겨서(예: `e.printStackTrace()` 또는 Logger 사용) 클린업 실패 여부를 확인할 수 있도록 해야 합니다.

2. **`psql` 바이너리 의존성**
   - **파일**: `src/test/java/maskun/quietchatter/WithTestContainerDatabases.java`
   - **문제점**: `POSTGRESQL.execInContainer("psql", ...)`를 사용하는 방식은 Docker 이미지 내에 `psql` 바이너리가 존재한다고 가정합니다. 표준 이미지는 대부분 포함하고 있지만, 경량화된 이미지에서는 없을 수 있습니다.
   - **제안**: `JdbcTemplate`이나 표준 `DataSource`를 사용하여 SQL(`TRUNCATE` 등)을 실행하는 방식이 더 견고하고 이식성이 좋습니다.

3. **컨테이너 설정 순서**
   - **파일**: `src/test/java/maskun/quietchatter/WithTestContainerDatabases.java`
   - **참고**: `container.withReuse(true)` 설정이 효과가 있는지 확인이 필요합니다. 일반적으로 설정은 `container.start()` 호출 이전에 적용되어야 합니다. 또한 로컬 환경 설정(`testcontainers.reuse.enable=true`)이 되어있는지도 확인해 주세요.

---

## 커밋 2: `63ae1b3b3e1497190036d3307e5d42416747947c`
**제목**: `test: migrate TalkQueryServiceTest to use MockitoJUnit runner`

### 코멘트
1. **`null` 주입을 통한 수동 인스턴스화**
   - **파일**: `src/test/java/maskun/quietchatter/talk/application/TalkQueryServiceTest.java`
   - **문제점**: `new TalkQueryService(talkRepository, null)`와 같이 `RecommendTalkRepository`에 `null`을 주입하고 있습니다. 현재 테스트 케이스에서는 문제가 없지만, 향후 `findBy` 메서드 로직이 변경되어 `recommendTalkRepository`를 사용하게 되면(예: 로깅, 필터링 등) `NullPointerException`이 발생할 수 있어 테스트가 취약해집니다.
   - **제안**: `RecommendTalkRepository`도 Mock 객체로 생성하여 생성자에 주입하거나, `@InjectMocks`를 사용하여 Mockito가 자동으로 주입하도록 하는 것이 좋습니다.

   ```java
   @Mock
   private RecommendTalkRepository recommendTalkRepository;

   // ...

   TalkQueryService talkQueryService = new TalkQueryService(talkRepository, recommendTalkRepository);
   ```

2. **좋은 마이그레이션**
   - **일반**: `@SpringBootTest`에서 `@ExtendWith(MockitoExtension.class)`로 변경한 것은 단위 테스트 실행 속도를 높이는 좋은 개선입니다.
