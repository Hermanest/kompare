package core.serialization

object AppConfig : PersistentStorage<AppConfigData>("app_config", AppConfigData::class, {
    AppConfigData("")
})

data class AppConfigData(
    var name: String
)