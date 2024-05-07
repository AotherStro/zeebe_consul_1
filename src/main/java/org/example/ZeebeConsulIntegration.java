package org.example;

/**
 * Hello world!
 *
 */
import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.response.ProcessInstanceResult;
import io.zeebe.client.api.worker.JobClient;
import io.zeebe.client.api.worker.JobHandler;
import io.zeebe.client.api.worker.JobWorker;
//import io.zeebe.client.api.worker.JobWorkerBuilder;
import io.zeebe.client.api.response.ActivatedJob;
import io.zeebe.client.api.response.ProcessInstanceEvent;
import io.zeebe.client.api.worker.JobWorkerBuilderStep1;
import io.zeebe.client.api.worker.JobWorkerBuilderStep1.JobWorkerBuilderStep2;

public class ZeebeConsulIntegration {

    public static void main(String[] args) {
        // 1. 连接到 Zeebe 引擎
        ZeebeClient client = ZeebeClient.newClientBuilder()
                .gatewayAddress("localhost:26500")
                .usePlaintext()
                .build();

        // 2. 创建工作任务处理器
        JobWorker worker = client.newWorker()
                .jobType("your-job-type")
                .handler(new YourJobHandler()) // 你需要实现的工作任务处理器
                .open();

        // 3. 启动工作流实例
        ProcessInstanceResult workflowInstanceResult = client.newCreateInstanceCommand()
                .bpmnProcessId("your-process-id")
                .latestVersion()
                .withResult()
                .send()
                .join();

        long workflowInstanceKey = workflowInstanceResult.getProcessInstanceKey();

        System.out.println("Workflow instance created with key: " + workflowInstanceKey);

        // 4. 关闭 Zeebe 客户端
        client.close();
    }

    // 定义工作任务处理器
    public static class YourJobHandler implements JobHandler {
        @Override
        public void handle(JobClient client, ActivatedJob job) {
            // 在这里编写调用 Consul 服务的逻辑
            System.out.println("Handling job: " + job);
            // 调用 Consul 中的服务
            // your code to call Consul service
            // 处理工作任务
            client.newCompleteCommand(job.getKey()).send().join();
        }
    }
}

