package diego.newsproject;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.zaxxer.hikari.HikariDataSource;

@SpringBootApplication
@EntityScan(basePackages = "entities")
public class NewsprojectApplication extends WebMvcConfigurerAdapter{

	public static void main(String[] args) {
		SpringApplication.run(NewsprojectApplication.class, args);
	}

	
	
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("/static/**")
        .addResourceLocations("classpath:/static/");
	}




	@Bean
	public DataSource datasource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setJdbcUrl("jdbc:mysql://dbHost:3306/newsproject");
		dataSource.setUsername("admin");
		dataSource.setPassword("admin");
		return dataSource;
	}
	
	@Bean
	public HibernatePropertiesCustomizer hibernateProperties() {
		return new HibernatePropertiesCustomizer() {
			@Override
			public void customize(Map<String, Object> hibernateProperties) {
				hibernateProperties.put("hibernate.hbm2ddl.auto", "update");
			}
		};
	}
}
