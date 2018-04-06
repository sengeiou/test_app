package cn.bevol;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("cn.bevol.internal.dao.mapper")
public class InternalApplication {

	public static void main(String[] args) {

		SpringApplication.run(InternalApplication.class, args);
	}
}
