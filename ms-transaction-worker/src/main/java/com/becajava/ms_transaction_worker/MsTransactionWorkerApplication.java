package com.becajava.ms_transaction_worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsTransactionWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsTransactionWorkerApplication.class, args);
	}

}
