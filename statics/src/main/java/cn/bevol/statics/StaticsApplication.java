package cn.bevol.statics;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("cn.bevol.statics.dao.mapper")
public class StaticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(StaticsApplication.class, args);
	}
}
