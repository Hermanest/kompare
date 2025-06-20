package core.serialization

object AppCache : PersistentStorage<AppCacheData>("", AppCacheData::class, {
    AppCacheData("")
})

data class AppCacheData(
    var name: String
)