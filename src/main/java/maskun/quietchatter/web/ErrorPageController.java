package maskun.quietchatter.web;

import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@NullMarked
@RestController
@RequestMapping("/errors")
public class ErrorPageController {

    @GetMapping("/member-deactivated")
    public ResponseEntity<Map<String, String>> memberDeactivated() {
        return ResponseEntity.ok(Map.of(
                "title", "Member Deactivated",
                "detail", "Your account has been deactivated.",
                "resolution", "If you want to reactivate your account, please click the 'Reactivate' button in the popup."
        ));
    }
}
