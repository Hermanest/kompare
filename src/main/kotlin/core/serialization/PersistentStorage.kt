package core.serialization

import java.io.File

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.reflect.KClass

abstract class PersistentStorage<T : Any>(
    fileName: String,
    private val klass: KClass<T>,
    private val defaultFactory: () -> T
) {
    val current get() = instance

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    private val file = File(System.getProperty("user.home"), ".komparer/$fileName")
    private var instance: T = load()

    @OptIn(InternalSerializationApi::class)
    fun load(): T {
        return if (file.exists()) {
            runCatching {
                json.decodeFromString(klass.serializer(), file.readText())
            }.getOrElse {
                defaultFactory()
            }
        } else {
            defaultFactory()
        }
    }

    @OptIn(InternalSerializationApi::class)
    fun save() {
        file.parentFile?.mkdirs()
        file.writeText(json.encodeToString(klass.serializer(), instance))
    }

    inline fun mutate(mutator: T.() -> Unit) {
        current.mutator()
        save()
    }

    inline fun <TProp> access(accessor: T.() -> TProp): TProp {
        return current.accessor()
    }
}
