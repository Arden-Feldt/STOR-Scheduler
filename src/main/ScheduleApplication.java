package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ScheduleApplication {

  public static void main(String[] args) {
    SpringApplication.run(ScheduleApplication.class, args);
  }

  @Bean
  public ScheduleService scheduleService() {
    return new ScheduleService();
  }
}

