package com.example.workflows;

import com.example.activities.SimpleActivities;
import com.example.model.SimpleInput;
import com.example.model.SimpleOutput;
import io.temporal.activity.ActivityOptions;
import io.temporal.activity.LocalActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

@WorkflowImpl(workers = "shutdown-worker")
public class SimpleWorkflowImpl implements SimpleWorkflow {

    private static final Logger log = Workflow.getLogger(SimpleWorkflow.class);

    private final RetryOptions retryOptions = RetryOptions.newBuilder()
            .setMaximumAttempts(1)
            .build();

    private final SimpleActivities regularActivities =
            Workflow.newActivityStub(SimpleActivities.class, ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(2))
                    .setRetryOptions(retryOptions)
                    .build());

    private final SimpleActivities regularActivitiesWithHeartbeatTimeout =
            Workflow.newActivityStub(SimpleActivities.class, ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(4))
                    .setHeartbeatTimeout(Duration.ofSeconds(2))
                    .setRetryOptions(retryOptions)
                    .build());

    private final SimpleActivities localActivities =
            Workflow.newLocalActivityStub(SimpleActivities.class, LocalActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(2))
                    .setRetryOptions(retryOptions)
                    .build());

    @Override
    public SimpleOutput execute(SimpleInput input) {
        log.info("BEGIN: Simple workflow, input = {}", input.toString());

        String result1 = regularActivities.aOne(input.getVal());
        String result2 = regularActivitiesWithHeartbeatTimeout.bTwo(result1);

        log.info("Workflow sleep for 1 second...");
        Workflow.sleep(Duration.ofSeconds(1));

        String result3 = localActivities.cThree(result2);

        SimpleOutput output = new SimpleOutput(result3);
        log.info("END: Simple workflow , output = {}", output);
        return output;
    }
}
