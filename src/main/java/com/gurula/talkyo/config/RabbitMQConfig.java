package com.gurula.talkyo.config;

import com.gurula.talkyo.properties.ConfigProperties;
import com.gurula.talkyo.properties.RabbitMQProperties;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    private final RabbitMQProperties rabbitMQProperties;
    public static final String PRONUNCIATION_QUEUE = "pronunciation.queue";
    public static final String GRAMMAR_QUEUE = "grammar.queue";
    public static final String ADVANCED_SENTENCE_QUEUE = "advancedSentence.queue";
    public static final String PARTNER_REPLY_QUEUE = "partnerReply.queue";
    public static final String ERROR_QUEUE = "error.queue";
    public static final String TALKYO_CHAT_FANOUT_EXCHANGE = "talkyo-chat-fanout-exchange";
    public static final String ERROR_EXCHANGE = "error-exchange";


    public RabbitMQConfig(RabbitMQProperties rabbitMQProperties) {
        this.rabbitMQProperties = rabbitMQProperties;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitMQProperties.getHost());
        connectionFactory.setPort(rabbitMQProperties.getPort());
        connectionFactory.setUsername(rabbitMQProperties.getUsername());
        connectionFactory.setPassword(rabbitMQProperties.getPassword());
        return connectionFactory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter(); // JSON 消息转换器
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Queue errorQueue() {
        return new Queue(ERROR_QUEUE, true);
    }
    @Bean
    public Queue grammarQueue() {
        return new Queue(GRAMMAR_QUEUE, true);
    }
    @Bean
    public Queue advancedSentenceQueue() {
        return new Queue(ADVANCED_SENTENCE_QUEUE, true);
    }
    @Bean
    public Queue partnerReplyQueue() {
        return new Queue(PARTNER_REPLY_QUEUE, true);
    }
    @Bean
    public Queue pronunciationQueue() {
        return new Queue(PRONUNCIATION_QUEUE, true);
    }
    @Bean
    public FanoutExchange chatFanoutExchange() {
        return new FanoutExchange(TALKYO_CHAT_FANOUT_EXCHANGE);
    }
    @Bean
    public TopicExchange errorExchange() {
        return new TopicExchange(ERROR_EXCHANGE);
    }

    // 配置失敗重試策略：將失敗策略改為RepublishMessageRecoverer
    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, ERROR_EXCHANGE, "error.#");
    }

    @Bean
    public Binding bindGrammar() {
        return BindingBuilder.bind(grammarQueue()).to(chatFanoutExchange());
    }

    @Bean
    public Binding bindAdvancedSentence() {
        return BindingBuilder.bind(advancedSentenceQueue()).to(chatFanoutExchange());
    }

    @Bean
    public Binding bindPronunciation() {
        return BindingBuilder.bind(pronunciationQueue()).to(chatFanoutExchange());
    }

    @Bean
    public Binding bindPartnerReply() {
        return BindingBuilder.bind(partnerReplyQueue()).to(chatFanoutExchange());
    }
}
