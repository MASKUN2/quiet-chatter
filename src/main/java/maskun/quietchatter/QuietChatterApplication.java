package maskun.quietchatter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class QuietChatterApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuietChatterApplication.class, args);
    }

}
