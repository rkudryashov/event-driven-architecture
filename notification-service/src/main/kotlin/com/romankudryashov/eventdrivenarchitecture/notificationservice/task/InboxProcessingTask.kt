package com.romankudryashov.eventdrivenarchitecture.notificationservice.task

import com.romankudryashov.eventdrivenarchitecture.notificationservice.service.InboxMessageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@Component
class InboxProcessingTask(
    private val inboxMessageService: InboxMessageService,
    private val applicationTaskExecutor: AsyncTaskExecutor,
    @Value("\${inbox.processing.task.batch.size}")
    private val batchSize: Int,
    @Value("\${inbox.processing.task.subtask.timeout}")
    private val subtaskTimeout: Long
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Scheduled(cron = "\${inbox.processing.task.cron}")
    fun execute() {
        log.debug("Start inbox processing task")

        val newInboxMessagesCount = inboxMessageService.markInboxMessagesAsReadyForProcessingByInstance(batchSize)
        log.debug("{} new inbox message(s) marked as ready for processing", newInboxMessagesCount)

        val inboxMessagesToProcess = inboxMessageService.getBatchForProcessing(batchSize)
        if (inboxMessagesToProcess.isNotEmpty()) {
            log.debug("Start processing {} inbox message(s)", inboxMessagesToProcess.size)
            val subtasks = inboxMessagesToProcess.map { inboxMessage ->
                applicationTaskExecutor
                    .submitCompletable { inboxMessageService.process(inboxMessage) }
                    .orTimeout(subtaskTimeout, TimeUnit.SECONDS)
            }
            CompletableFuture.allOf(*subtasks.toTypedArray()).join()
        }

        log.debug("Inbox processing task completed")
    }
}
