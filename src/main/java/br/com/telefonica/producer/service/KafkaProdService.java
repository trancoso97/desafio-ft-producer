package br.com.telefonica.producer.service;

import br.com.telefonica.producer.model.Order;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

@Service
public class KafkaProdService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaProdService.class);
    private final String topic;
    private final KafkaTemplate<String, Order> kafkaTemplate;

    public KafkaProdService(@Value("${topic.name}") String topic, KafkaTemplate<String, Order> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(Order order) {
        kafkaTemplate.send(topic, order).addCallback(
            success -> logger.info("Mensagem enviada! " + success.getProducerRecord().value()),
            failure -> logger.info("ERRO: mensagem não enviada!" + failure.getMessage())
        );
    }

}
