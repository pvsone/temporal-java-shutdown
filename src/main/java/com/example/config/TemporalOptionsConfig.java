package com.example.config;

import com.example.context.MDCContextPropagator;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.spring.boot.TemporalOptionsCustomizer;
import io.temporal.spring.boot.WorkerOptionsCustomizer;
import io.temporal.worker.WorkerFactoryOptions;
import io.temporal.worker.WorkerOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Collections;

@Slf4j
@Configuration
public class TemporalOptionsConfig {
    // Worker specific options customization
    @Bean
    public WorkerOptionsCustomizer customWorkerOptions() {
        return new WorkerOptionsCustomizer() {
            @Nonnull
            @Override
            public WorkerOptions.Builder customize(
                    @Nonnull WorkerOptions.Builder optionsBuilder,
                    @Nonnull String workerName,
                    @Nonnull String taskQueue) {
                log.info("Customizing WorkerOptions for workerName={}, taskQueue={}", workerName, taskQueue);
                optionsBuilder.setStickyTaskQueueDrainTimeout(Duration.ofSeconds(15));
                return optionsBuilder;
            }
        };
    }

    // WorkflowServiceStubsOptions customization
    @Bean
    public TemporalOptionsCustomizer<WorkflowServiceStubsOptions.Builder>
    customServiceStubsOptions() {
        return new TemporalOptionsCustomizer<WorkflowServiceStubsOptions.Builder>() {
            @Nonnull
            @Override
            public WorkflowServiceStubsOptions.Builder customize(
                    @Nonnull WorkflowServiceStubsOptions.Builder optionsBuilder) {
                log.info("Customizing WorkflowServiceStubsOptions");
                optionsBuilder.setRpcLongPollTimeout(Duration.ofSeconds(10));
                return optionsBuilder;
            }
        };
    }

    // WorkflowClientOption customization
    @Bean
    public TemporalOptionsCustomizer<WorkflowClientOptions.Builder> customClientOptions() {
        return new TemporalOptionsCustomizer<WorkflowClientOptions.Builder>() {
            @Nonnull
            @Override
            public WorkflowClientOptions.Builder customize(
                    @Nonnull WorkflowClientOptions.Builder optionsBuilder) {
                log.info("Customizing WorkflowClientOptions");
                optionsBuilder.setContextPropagators(Collections.singletonList(new MDCContextPropagator()));
                return optionsBuilder;
            }
        };
    }

    // WorkerFactoryOptions customization
    @Bean
    public TemporalOptionsCustomizer<WorkerFactoryOptions.Builder> customWorkerFactoryOptions() {
        return new TemporalOptionsCustomizer<WorkerFactoryOptions.Builder>() {
            @Nonnull
            @Override
            public WorkerFactoryOptions.Builder customize(
                    @Nonnull WorkerFactoryOptions.Builder optionsBuilder) {
                log.info("Customizing WorkerFactoryOptions");
                // no customizations for now
                return optionsBuilder;
            }
        };
    }
}
