import java.util.Properties
import java.io.File

apply(plugin = "com.android.application")
apply(plugin = "com.gladed.androidgitversion")
apply(plugin = "witness")

val rpx = project.rootProject.extra

val appTargets = mapOf(
    "oversec" to mapOf(
        "applicationId" to "io.oversec.one",
        "app_name" to "Oversec",
        "acs_label" to "Oversec",
        "targetapp" to "",
        "website" to "https://www.oversec.io",
        "dllink" to "http://dl.oversec.io"
    ),
    "intern" to mapOf(
        "applicationId" to "io.oversec.one",
        "app_name" to "INTERN",
        "acs_label" to "INTERN",
        "targetapp" to "",
        "website" to "https://intern.oversec.io",
        "dllink" to "http://dl-intern.oversec.io"
    )
)

rpx["APPTGTS"] = appTargets

val langs = listOf("", "-de", "-es", "-fr", "-pt", "-ru", "-tr", "-zh-rCN", "-it")
rpx["langs"] = langs

fun applyAppTgt(scope: com.android.build.gradle.internal.dsl.ProductFlavor, appTgt: String) {
    val targets = appTargets[appTgt] ?: return
    scope.applicationId = targets["applicationId"] as String
    scope.resValue("string", "app_name", targets["app_name"] as String)
    scope.resValue("string", "acs_label", targets["acs_label"] as String)
    scope.resValue("string", "targetapp", targets["targetapp"] as String)
    scope.resValue("string", "website", targets["website"] as String)
    scope.resValue("string", "dllink", targets["dllink"] as String)
}

rpx["applyAppTgt"] = ::applyAppTgt

configure<com.android.build.gradle.AppExtension> {
    applicationVariants.all {
        if (buildType.name == "release") {
            outputs.all {
                val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
                output.outputFileName = output.outputFileName.replace(".apk", "-$versionCode.apk")
            }
        }
    }

    applicationVariants.all {
        if (buildType.isMinifyEnabled) {
            assemble.doLast {
                copy {
                    from(mappingFile)
                    into("${rootDir}/proguardMappings")
                    rename { fileName ->
                        "mapping-$name-$versionCode.txt"
                    }
                }
            }
        }
    }

    compileSdkVersion(28)

    defaultConfig {
        applicationId = "io.oversec.one"
        minSdkVersion(21)
        targetSdkVersion(28)

        //hardcoded for F-droid bot
        versionCode = 1005015
        versionName = "1.5.15"

        //still need to make sure this is in sync with the git tags
        val versionCodeFromGit = (extensions.findByName("androidGitVersion") as? com.gladed.androidgitversion.AndroidGitVersionExtension)?.code() ?: 0
        if (versionCode != versionCodeFromGit) {
            throw Exception(String.format("Version Code Mismatch, %d <-> %d\nPlease update hardcoded versionCode/versionName", versionCode, versionCodeFromGit))
        }

        buildConfigField("java.lang.Boolean", "IS_FRDOID", "new Boolean(${project.hasProperty("fdroid")})")
    }

    buildTypes {
        getByName("release") {
            //isDebuggable = true //for IAB debugging
            //multiDexEnabled = true  //for IAB debugging

            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-android-optimize-patched.txt", "proguard-rules.pro")
            buildConfigField("java.lang.Long", "X_BUILD_TIME", "new Long(${System.currentTimeMillis()}L)")
        }

        getByName("debug") {
            multiDexEnabled = true
            buildConfigField("java.lang.Long", "X_BUILD_TIME", "new Long(${System.currentTimeMillis()}L)")
        }
    }

    flavorDimensions("targetapp")

    productFlavors {
        create("oversec") {
            setDimension("targetapp")
            applyAppTgt(this, "oversec")

            buildConfigField("String", "GOOGLE_PLAY_PUBKEY", "\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoYCl8Ef7/5tRboyPcvcWqzVM1l3yTN28VepCzkTc1iHBJqlDd0d3k+ajZHKvyrvEK8JjjGqX/DkaESi7PNV03FptWI1HQP9P4J02Gm0nP/pwt6a4WAjbE2HSWlleBV/H66ZEQR6MhwKtI9rLFqPIxfNLJMDONYQ4/xIM6bMWNlvb59O0Yb9iEJOA+mJwMOZZoY9vadt5mUqi6bchjTXuOS3iCCrAixkhMIA8kpZSq40LI7ya3QSEnSRZJSRKccBagGxH12w3/5k/s1mnRIO7T/4cX9Kvi8+Q7pb0Zn0CpG0AEm078ON1+5dlJUHYMli0+J7JL2IL11txN/21FuhM4QIDAQAB\"")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_PROMO", "\"sku.oversec.one.fullversion.promo.v0\"")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_A", "\"sku.oversec.one.fullversion.a.v0\"")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_B", "\"sku.oversec.one.fullversion.b.v0\"")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_C", "\"sku.oversec.one.fullversion.c.v0\"")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_D", "\"sku.oversec.one.fullversion.d.v0\"")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_E", "\"sku.oversec.one.fullversion.e.v0\"")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_F", "\"sku.oversec.one.fullversion.f.v1\"")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_G", "\"sku.oversec.one.fullversion.g.v1\"")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_H", "\"sku.oversec.one.fullversion.h.v1\"")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_I", "\"sku.oversec.one.fullversion.i.v1\"")

            buildConfigField("String", "DONATION_BTC", "\"16tan5fBNJ6n1QmVxwvvondyvuwgx1W6fE\"")
            buildConfigField("String", "DONATION_ETH", "\"0xE71c3b07dF8b13f3B99e537f541F91E40d09285a\"")
            buildConfigField("String", "DONATION_DASH", "\"5Jmj6oHucSZrQZ6JUiZu1sxHrX1adwfiJZURSRXJkDvZ6xAMrDg\"")
            buildConfigField("String", "DONATION_IOTA", "\"ZVFLHYRAJWGZDQVKJINUXDZQTXWR9GEMZSAIMNSDBWAYCQWHTFYFMILDVZORSZ9DEXKSLF9EKYSZHTAW9E9ROAZGLZ\"")
        }
        
        create("intern") {
            setDimension("targetapp")
            applyAppTgt(this, "intern")

            buildConfigField("String", "GOOGLE_PLAY_PUBKEY", "null")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_PROMO", "null")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_A", "null")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_B", "null")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_C", "null")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_D", "null")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_E", "null")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_F", "null")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_G", "null")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_H", "null")
            buildConfigField("String", "GOOGLE_PLAY_SKU_FULLVERSION_I", "null")

            buildConfigField("String", "DONATION_BTC", "null")
            buildConfigField("String", "DONATION_ETH", "null")
            buildConfigField("String", "DONATION_DASH", "null")
            buildConfigField("String", "DONATION_IOTA", "null")
        }
    }

    val localProperties = Properties()
    try {
        localProperties.load(project.file("local.properties").inputStream())
    } catch (ex: Exception) {}

    if (localProperties.containsKey("signing.properties") &&
        File(localProperties.getProperty("signing.properties")).exists()) {

        val props = Properties()
        props.load(file(localProperties.getProperty("signing.properties")).inputStream())

        println("Loaded signing properties from ${localProperties.getProperty("signing.properties")}")

        signingConfigs {
            create("release") {
                storeFile = file(props.getProperty("keystore"))
                storePassword = props.getProperty("keystore.password")
                keyAlias = props.getProperty("keyAlias")
                keyPassword = props.getProperty("keyPassword")
            }
        }
        buildTypes.getByName("release").signingConfig = signingConfigs.getByName("release")
    } else {
        buildTypes.getByName("release").signingConfig = null
        println("No signing configuration provided!")
    }

    // NOTE: Lint is disabled because it slows down builds,
    // to enable it comment out the code at the bottom of this build.gradle
    lintOptions {
        // Do not abort build if lint finds errors
        isAbortOnError = false

        isCheckAllWarnings = true
        isHtmlReport = true
        htmlOutput = file("lint-report.html")
    }

    dexOptions {
        threadCount = 6
        preDexLibraries = true
        // dexInProcess requires much RAM, which is not available on all dev systems
        // dexInProcess = false
        jumboMode = true
        javaMaxHeapSize = "6g"
    }

    packagingOptions {
        exclude("LICENSE.txt")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/NOTICE")
        exclude(".readme")
    }
}

dependencies {
    "implementation"(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    "implementation"(project(":libraries:oversec_crypto:crypto"))

    "implementation"("io.github.kobakei:ratethisapp:1.2.0")
    "implementation"("org.sufficientlysecure:html-textview:3.6")
    "implementation"("org.commonjava.googlecode.markdown4j:markdown4j:2.2-cj-1.1")

    "implementation"("com.android.support:multidex:1.0.3")
    "implementation"("com.android.support:support-v13:28.0.0")
    "implementation"("com.android.support:appcompat-v7:28.0.0")
    "implementation"("com.android.support:cardview-v7:28.0.0")
    "implementation"("com.android.support:support-v4:28.0.0")
    "implementation"("com.android.support:design:28.0.0")

    "testImplementation"("junit:junit:4.12")
    "testImplementation"(project(":libraries:oversec_crypto:crypto"))
}

//Note: update with  ./gradlew  -q calculateChecksums
apply(plugin = "witness")

configure<Any> {
    val verifyList = listOf(
        "org.commonjava.googlecode.markdown4j:markdown4j:2.2-cj-1.1:markdown4j-2.2-cj-1.1.jar:28eb991f702c6d85d6cafd68c24d1ce841d1f5c995c943f25aedb433c0c13f60",
        "org.sufficientlysecure:html-textview:3.6:html-textview-3.6.aar:39c1a1a8ca1d127f0120527b2d1a9b035d0c726237a4e7e9d28f8703da542aa1"
    )
    javaClass.getMethod("setVerify", List::class.java).invoke(this, verifyList)
}

// NOTE: This disables Lint!
tasks.whenTaskAdded {
    if (name.contains("lint")) {
        enabled = false
    }
}

val currentDir = File("app/src")
currentDir.listFiles()?.filter { it.isDirectory }?.forEach {
    try {
        apply(from = "src/${it.name}/mixin.gradle")
    } catch (ex: Exception) {}
}

// poor-man's preprocess to generate app-specific string resources
fun replaceEntities(apptgt: String, sfile: File, tdir: File, tfile: File) {
    val targets = appTargets[apptgt] ?: return
    ant.withGroovyBuilder {
        "mkdir"("dir" to tdir)
        "copy"("file" to sfile.canonicalPath, "tofile" to tfile.canonicalPath)
        "replace"("file" to tfile.canonicalPath, "token" to "&appname;", "value" to targets["app_name"])
        "replace"("file" to tfile.canonicalPath, "token" to "&targetapp;", "value" to targets["targetapp"])
        "replace"("file" to tfile.canonicalPath, "token" to "&acs_label;", "value" to targets["acs_label"])
        "replace"("file" to tfile.canonicalPath, "token" to "&website;", "value" to targets["website"])
        "replace"("file" to tfile.canonicalPath, "token" to "&dllink;", "value" to targets["dllink"])
    }
}

rpx["replaceEntities"] = ::replaceEntities

tasks.register("preBuildMangleEntities") {
    doLast {
        val tasknames = project.gradle.startParameter.taskNames

        val taskname = tasknames[0]
        var apptgt = taskname.replace(":app:", "").replace("assemble", "").replace("test", "")
            .replace("generate", "").replace("install", "").replace("Debug", "")
            .replace("Release", "").replace("Sources", "").replace("UnitTest", "").lowercase()
        if (apptgt.isEmpty()) {
            apptgt = "intern"
        }

        val rootDir = project.projectDir.parent

        langs.forEach { lang ->
            val tdir = File(rootDir, "app/src/$apptgt/res/values$lang")

            var sfile = File(rootDir, "app/src/appsec-common/res/values$lang/strings.xml")
            var tfile = File(tdir, "strings_generated.xml")
            replaceEntities(apptgt, sfile, tdir, tfile)

            sfile = File(rootDir, "libraries/oversec_crypto/crypto/src/main/res/values$lang/strings.xml")
            tfile = File(tdir, "strings_crypto_generated.xml")
            replaceEntities(apptgt, sfile, tdir, tfile)

            sfile = File(rootDir, "libraries/oversec_crypto/crypto/src/main/res/values$lang/strings_core.xml")
            tfile = File(tdir, "strings_crypto_core_generated.xml")
            replaceEntities(apptgt, sfile, tdir, tfile)
        }
    }
}

gradle.projectsEvaluated {
    tasks.named("preBuild").configure {
        dependsOn("preBuildMangleEntities")
    }
}

tasks.named<Delete>("clean") {
    doFirst {
        ant.withGroovyBuilder {
            "delete"(mapOf<String, Any>()) {
                "fileset"("dir" to "src") {
                    "include"("name" to "**/strings*_generated.xml")
                }
            }
        }
    }
}
