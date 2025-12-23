import org.example.MAX_VARCHAR_LENGTH
import org.jetbrains.exposed.v1.core.Index
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.CompositeIdTable
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.plus
import org.jetbrains.exposed.v1.core.stringLiteral
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.exists
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.vendors.DatabaseDialectMetadata
import kotlin.collections.get
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.expect
import kotlin.text.orEmpty

class DdlTest {

    @BeforeTest
    fun setUp() {
        Database.connect(
            "jdbc:h2:mem:test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver"
        )
    }

    @Test
    fun `create table`() {
        val tasks = object : Table("tasks") {
            val id = integer("id").autoIncrement()
            val title = varchar("name", MAX_VARCHAR_LENGTH)
            val description = varchar("description", MAX_VARCHAR_LENGTH).nullable()
            val isCompleted = bool("completed").default(false)
            val withComment = varchar("withComment", length = MAX_VARCHAR_LENGTH).nullable()
                .withDefinition("COMMENT", stringLiteral("Test Comment"))
            val invisible = varchar("invisible", length = MAX_VARCHAR_LENGTH).nullable().withDefinition("INVISIBLE")
        }

        withTransaction {
            /**
             * default() -> DEFAULT FALSE
             * nullable() -> NOT NULL
             * withDefinition("INVISIBLE") -> INVISIBLE
             */
            SchemaUtils.create(tasks)
        }
    }

    @Test
    fun `list database`() {
        withTransaction {
            SchemaUtils.createDatabase("newdb")
            SchemaUtils.listDatabases().also {
                println("$it")
                assert(it.contains("newdb"))
            }
        }
    }

    @Test
    fun `standard index`() {
        val testTable = object : Table("test_table") {
            val id = integer("id")
            val name = varchar("name", length = 42)

            override val primaryKey = PrimaryKey(id)

            init {
                index("test_table_by_name", false, name)
            }
        }

        withTransaction {
            SchemaUtils.create(testTable)
            assertTrue(testTable.exists())
        }
    }

    @Test
    fun `table extension index()`() {
        val testTable = object : Table("test_table") {
            val id = integer("id")
            val name = varchar("name", length = 42).index("name_index")

            override val primaryKey = PrimaryKey(id)
        }

        withTransaction {
            SchemaUtils.create(testTable)
            assertTrue(testTable.exists())
        }
    }

    @Test
    fun `create and drop index`() {
        val tester = object : IntIdTable("tester") {
            val amount = integer("amount")
            val price = integer("price")
            val item = varchar("item", 32).nullable()

            init {
                index(customIndexName = "tester_plus_index", isUnique = false, amount)
            }
        }

        withTransaction {
            SchemaUtils.create(tester)
            getIndices(tester).also {
                assertEquals(1, it.size)
            }
            val dropStatements = getIndices(tester).map { it.dropStatement().first() }
//            execInBatch(dropStatements)
            exec(
                "ALTER TABLE TESTER DROP INDEX IF EXISTS tester_plus_index"
            )

            getIndices(tester).also {
                assertEquals(0, it.size)
            }
        }
    }

    @Test
    fun `id Table`() {
        val testTable = object : IdTable<String>("test_table") {
            val column1 = varchar("column_1", 30)
            override val id = column1.entityId()

            override val primaryKey = PrimaryKey(id)
        }

        withTransaction {
            SchemaUtils.create(testTable)
        }
    }

    @Test
    fun `composite`() {
        withTransaction {
            SchemaUtils.create(DirectorsTable)
        }
    }
}

fun <T> withTransaction(
    block: JdbcTransaction.() -> T
) {
    transaction {
        maxAttempts = 1
        addLogger(StdOutSqlLogger)
        block()
    }
}

val currentDialectMetadataTest: DatabaseDialectMetadata
    get() = TransactionManager.current().db.dialectMetadata

private fun JdbcTransaction.getIndices(table: Table): List<Index> {
    db.dialectMetadata.resetCaches()
    return currentDialectMetadataTest.existingIndices(table)[table].orEmpty()
}

enum class Genre { HORROR, DRAMA, THRILLER, SCI_FI }

const val NAME_LENGTH = 50
object DirectorsTable : CompositeIdTable("directors") {
    val name = varchar("name", NAME_LENGTH).entityId()
    val guildId = uuid("guild_id").autoGenerate().entityId()
    val genre = enumeration<Genre>("genre")

    override val primaryKey = PrimaryKey(name, guildId)
}