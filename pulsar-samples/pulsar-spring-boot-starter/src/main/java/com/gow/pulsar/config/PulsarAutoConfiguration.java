package com.gow.pulsar.config;

import com.gow.pulsar.core.container.PulsarContainer;
import com.gow.pulsar.core.container.ack.AckMode;
import com.gow.pulsar.core.domain.ContainerProperties;
import com.gow.pulsar.core.domain.PulsarProperties;
import com.gow.pulsar.core.factory.PulsarFactory;
import com.gow.pulsar.core.producer.ProducerTemplate;
import com.gow.pulsar.core.producer.StringProducerTemplate;
import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.internal.PulsarAdminBuilderImpl;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author gow
 * @date 2021/7/2
 */
//@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(PulsarClient.class)
@ConditionalOnProperty(value = {"gow.pulsar.service-url"})
public class PulsarAutoConfiguration {

    @Bean
    public PulsarFactory pulsarFactory() {
        return new PulsarFactory();
    }

    @Bean(destroyMethod = "close")
    public PulsarClient pulsarClient(PulsarProperties properties, PulsarFactory pulsarFactory)
            throws PulsarClientException {

        return pulsarFactory.createClient(properties.getServiceUrl(), properties.getClient());
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnClass(PulsarAdminBuilderImpl.class)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = {"gow.pulsar.admin.web-service-url"})
    public PulsarAdmin pulsarAdmin(PulsarProperties properties) throws PulsarClientException {
        return PulsarAdmin.builder()
                .authentication(AuthenticationFactory.token(properties.getAdmin().getAdminToken()))
                .serviceHttpUrl(properties.getAdmin().getWebServiceUrl())
                .build();
    }

    @Bean("pulsarContainer")
    public PulsarContainer pulsarContainer(PulsarProperties properties, PulsarFactory pulsarFactory,
                                           PulsarClient client) {
        return new PulsarContainer(properties, pulsarFactory, client);
    }

    @Bean("individualContainer")
    public PulsarContainer individualContainer(PulsarProperties properties, PulsarFactory pulsarFactory,
                                               PulsarClient client) {
        PulsarContainer pulsarContainer = new PulsarContainer(properties, pulsarFactory, client);
        ContainerProperties containerProperties = pulsarContainer.containerProperties();
        containerProperties.setAckMode(AckMode.MANUAL_IMMEDIATE);
        return pulsarContainer;
    }

    @Bean("producerTemplate")
    public ProducerTemplate producerTemplate(@Qualifier("pulsarContainer") PulsarContainer container) {
        return new ProducerTemplate(container);
    }

    @Bean("stringProducerTemplate")
    public StringProducerTemplate stringProducerTemplate(@Qualifier("pulsarContainer") PulsarContainer container) {
        return new StringProducerTemplate(container);
    }

    public TaskScheduler defaultTaskScheduler(ObjectProvider<TaskSchedulerCustomizer> taskSchedulerCustomizers) {

        TaskSchedulerBuilder builder = new TaskSchedulerBuilder();
        ThreadPoolTaskScheduler taskScheduler = builder.poolSize(2)
                .awaitTermination(true)
                .awaitTerminationPeriod(Duration.ofSeconds(60))
                .threadNamePrefix("pulsar-scheduler-task-")
                .customizers(taskSchedulerCustomizers).build();
        taskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskScheduler.initialize();
        return taskScheduler;
    }
}