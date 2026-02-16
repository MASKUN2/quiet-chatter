package maskun.quietchatter.web;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class SpecApi {

    @GetMapping("/v1/spec")
    public ResponseEntity<String> getOpenApiSpec() {
        try {
            // 1. 배포 환경 (JAR 내부)
            Resource resource = new ClassPathResource("static/docs/openapi3.json");
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(resource.getContentAsString(StandardCharsets.UTF_8));
            }

            // 2. 로컬 개발 환경 (빌드 디렉토리)
            Path localPath = Paths.get("build/api-spec/openapi3.json");
            if (Files.exists(localPath)) {
                String spec = Files.readString(localPath, StandardCharsets.UTF_8);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(spec);
            }

            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
