package com.gubee.stockreconciliation.infrastructure.config;

import com.gubee.stockreconciliation.adapters.outbound.persistence.repository.StockRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {
    @Bean
    CommandLineRunner test(StockRepository stockRepository) {
        return args -> {

            System.out.println("Repository carregado com sucesso!");

            System.out.println(stockRepository.count());

        };
    }
}
