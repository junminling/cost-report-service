package junmin.netflix.costreportservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@SpringBootApplication
@EnableSwagger2
public class CostReportServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CostReportServiceApplication.class, args);
	}
	@Bean
	public Docket getDocketConfiguration(){
		Docket docket = new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("junmin.netflix.costreportservice"))
				.build()
				.apiInfo(getApiInfo());
		return docket;
	}

	private ApiInfo getApiInfo() {
		return new ApiInfo(
				"Cost Report Service",
				"Apis to process episode costs",
				"1.0.0",
				"http://www.netflix.com",
				new springfox.documentation.service.Contact("junmin ling", "http://www.netflix.com", "jmling@gmail.com"),
				"free",
				"http://www.netflix.com",
				Collections.emptyList());

	}

}
