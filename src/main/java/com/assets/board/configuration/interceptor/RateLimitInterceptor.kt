package com.assets.board.configuration.interceptor

import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import java.io.IOException
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class RateLimitInterceptor(private val maxRequests: Int, period: Duration) : ClientHttpRequestInterceptor {
    private val semaphore: Semaphore
    private val scheduler: ScheduledExecutorService
    private val period: Duration?

    init {
        this.period = period
        this.semaphore = Semaphore(maxRequests)
        this.scheduler = Executors.newScheduledThreadPool(1)

        // Schedule permit release
        val periodMillis = period.toMillis()
        val delayPerRequest = periodMillis / maxRequests

        scheduler.scheduleAtFixedRate(
            Runnable {
                if (semaphore.availablePermits() < maxRequests) {
                    semaphore.release()
                }
            },
            delayPerRequest,
            delayPerRequest,
            TimeUnit.MILLISECONDS
        )
    }

    @Throws(IOException::class)
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        try {
            // Wait for available permit (blocks if rate limit reached)
            semaphore.acquire()

            // Execute the request
            return execution.execute(request, body)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IOException("Rate limit interrupt", e)
        }
    }
}
