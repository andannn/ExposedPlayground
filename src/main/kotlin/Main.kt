package org.example

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.count
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

const val MAX_VARCHAR_LENGTH = 128

object Tasks : Table("tasks") {
    val id = integer("id").autoIncrement()
    val title = varchar("name", MAX_VARCHAR_LENGTH)
    val description = varchar("description", MAX_VARCHAR_LENGTH)
    val isCompleted = bool("completed").default(false)
}

fun main() {
    Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

    transaction {
        SchemaUtils.create(Tasks)

        val taskId = Tasks.
        insert {
            it[title] = "Learn Exposed"
            it[description] = "Go through the Get started with Exposed tutorial"
        } get Tasks.id

        val secondTaskId = Tasks.insert {
            it[title] = "Read The Hobbit"
            it[description] = "Read the first two chapters of The Hobbit"
            it[isCompleted] = true
        } get Tasks.id

        println("Created new tasks with ids $taskId and $secondTaskId.")

        Tasks.select(Tasks.id.count(), Tasks.isCompleted).groupBy(Tasks.isCompleted).forEach {
            println("${it[Tasks.isCompleted]}: ${it[Tasks.id.count()]} ")
        }

        println("Remaining tasks: ${Tasks.selectAll().toList()}")
    }
}