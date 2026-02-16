package maskun.quietchatter.customer.adaptor.in;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import maskun.quietchatter.MockSecurityTestConfig;
import maskun.quietchatter.customer.application.in.CustomerMessageCreatable;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerMessageApi.class)
@Import(MockSecurityTestConfig.class)
@AutoConfigureRestDocs
@Tag("restdocs")
class CustomerMessageApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerMessageCreatable customerMessageCreatable;

    @Test
    void postCustomerMessage() throws Exception {
        String content = "Hello, this is a message.";
        // Use reflection or just map since record is private in class? No, request is inner record but used in signature
        // Actually CustomerMessageRequest is package-private inner record in Api class?
        // Wait, the record is declared INSIDE CustomerMessageApi.
        // We cannot instantiate it easily from outside if it's package-private and we are in different package (test package is same)
        // Since test is in same package `maskun.quietchatter.customer.adaptor.in`, we can access it if it is package-private.

        // But to construct JSON, we can just use a Map or inner static class representation for test.
        // Or simply use a string for content if we construct JSON manually.

        // Since I cannot access CustomerMessageApi.CustomerMessageRequest easily if it's inside the class, 
        // I'll create a Map for the request body.

        java.util.Map<String, String> requestMap = java.util.Map.of("content", content);

        willDoNothing().given(customerMessageCreatable).create(eq(content));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/customer/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestMap)))
                .andExpect(status().isAccepted())
                .andDo(MockMvcRestDocumentationWrapper.document("create-customer-message",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Customer Messages")
                                        .description("Send a message to customer service")
                                        .requestFields(
                                                fieldWithPath("content").description("Message content")
                                        )
                                        .requestSchema(Schema.schema("CustomerMessageRequest"))
                                        .build()
                        )
                ));
    }
}
