package jp.ecom_plat.saigaitask;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;

import jp.ecom_plat.saigaitask.service.InitService;

@SpringBootApplication
@RestController
@EnableScheduling
public class SpringBootS2jdbcDemoApplication extends SpringBootServletInitializer {

	/**
	 * アプリケーション起動時の処理
	 * @return
	 */
	@Bean
	CommandLineRunner init(InitService initService) {
		CommandLineRunner runner = new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {
				boolean dbMigration = false;
				initService.init(dbMigration);
			}
		};
		return runner;
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringBootS2jdbcDemoApplication.class);
	}

    public static void main(String[] args) {
		SpringApplication.run(SpringBootS2jdbcDemoApplication.class, args);
	}
}
