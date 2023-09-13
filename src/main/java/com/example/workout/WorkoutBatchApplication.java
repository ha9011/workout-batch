package com.example.workout;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WorkoutBatchApplication {


	public static void main(String[] args) {
		SpringApplication.run(WorkoutBatchApplication.class, args);
	}

}
