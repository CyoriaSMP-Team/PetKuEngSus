package com.petkuengsus.petkuengsus.internal

import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.plugin.Plugin
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import java.util.function.Consumer

/** Routes plugin work through the owning Folia scheduler, with Bukkit scheduler fallbacks for Paper. */
object TaskScheduler {
    private val folia by lazy { supportsFolia(Bukkit.getServer().javaClass) }

    internal fun supportsFolia(serverClass: Class<*>): Boolean =
        runCatching { serverClass.getMethod("getGlobalRegionScheduler") }.isSuccess

    fun entity(entity: Entity, plugin: Plugin, delay: Long = 1L, task: Runnable) {
        if (folia) {
            entity.scheduler.runDelayed(plugin, Consumer<ScheduledTask> { task.run() }, null, delay)
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delay)
        }
    }

    fun repeatingGlobal(plugin: Plugin, delay: Long, period: Long, task: Runnable) {
        if (folia) {
            Bukkit.getServer().globalRegionScheduler.runAtFixedRate(
                plugin, Consumer<ScheduledTask> { task.run() }, delay, period
            )
        } else {
            Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period)
        }
    }

    fun repeatingEntity(entity: Entity, plugin: Plugin, delay: Long, period: Long, task: Runnable) {
        if (folia) {
            entity.scheduler.runAtFixedRate(
                plugin, Consumer<ScheduledTask> { task.run() }, null, delay, period
            )
        } else {
            Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period)
        }
    }
}
