package maskun.quietchatter.customer.adaptor.in;

import lombok.RequiredArgsConstructor;
import maskun.quietchatter.customer.application.in.CustomerMessageCreatable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/customer/messages")
@RestController
@RequiredArgsConstructor
class CustomerMessageApi {
    private final CustomerMessageCreatable customerMessageCreatable;

    @PostMapping
    ResponseEntity<Void> postMessage(@RequestBody CustomerMessageRequest request) {
        customerMessageCreatable.create(request.content());
        return ResponseEntity.accepted().build();
    }

    record CustomerMessageRequest(String content) {
    }
}
