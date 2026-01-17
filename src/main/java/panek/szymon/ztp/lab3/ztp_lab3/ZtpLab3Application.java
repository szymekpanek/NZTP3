package panek.szymon.ztp.lab3.ztp_lab3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRetry
@EnableScheduling
public class ZtpLab3Application {

    public static void main(String[] args) {
        SpringApplication.run(ZtpLab3Application.class, args);
    }

}
