// Workaround for AGP AndroidLocationsException when both ANDROID_PREFS_ROOT and ANDROID_USER_HOME are set
try {
    val peClass = Class.forName("java.lang.ProcessEnvironment")
    val envField = peClass.getDeclaredField("theEnvironment")
    envField.isAccessible = true
    (envField.get(null) as? MutableMap<Any, Any>)?.let { map ->
        val keysToRemove = map.keys.filter { it.toString() == "ANDROID_PREFS_ROOT" }
        for (k in keysToRemove) {
            map.remove(k)
        }
    }
    val unmodifiableEnvField = peClass.getDeclaredField("theUnmodifiableEnvironment")
    unmodifiableEnvField.isAccessible = true
    val unmodifiableObj = unmodifiableEnvField.get(null)
    if (unmodifiableObj != null) {
        val mField = unmodifiableObj.javaClass.getDeclaredField("m")
        mField.isAccessible = true
        (mField.get(unmodifiableObj) as? MutableMap<Any, Any>)?.let { map ->
            val keysToRemove = map.keys.filter { it.toString() == "ANDROID_PREFS_ROOT" }
            for (k in keysToRemove) {
                map.remove(k)
            }
        }
    }
} catch (_: Throwable) {
}

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TrackWell"
include(":app")
