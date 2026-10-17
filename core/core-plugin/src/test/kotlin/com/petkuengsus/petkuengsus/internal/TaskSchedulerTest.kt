package com.petkuengsus.petkuengsus.internal

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TaskSchedulerTest {
    @Test
    fun `recognizes Folia by global region scheduler availability`() {
        assertTrue(TaskScheduler.supportsFolia(FoliaLikeServer::class.java))
        assertFalse(TaskScheduler.supportsFolia(PaperLikeServer::class.java))
    }

    class FoliaLikeServer {
        fun getGlobalRegionScheduler(): Any = Any()
    }

    class PaperLikeServer
}
