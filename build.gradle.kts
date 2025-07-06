import earth.terrarium.cloche.api.target.FabricTarget
import earth.terrarium.cloche.api.target.ForgeTarget

plugins {
	id("earth.terrarium.cloche")
    id("maven-publish")
}

val mod_group: String by project
group = mod_group
val mod_version: String by project
version = mod_version

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    withSourcesJar()
    withJavadocJar()
}

val mod_id: String by project
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = mod_group
            artifactId = "${mod_group}.${mod_id}"
            version = mod_version

            from(components["java"])
        }
    }

    repositories {
        maven {
            val maven_organization: String by project
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/${maven_organization}/${mod_id}")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

repositories {
    // Workaround for terrarium-earth/cloche #40 ([Bug]: Windows requires mavencentral() before librariesMinecraft())
    // https://github.com/terrarium-earth/cloche/issues/40
    mavenCentral()

    cloche {
        main()
        librariesMinecraft()
        mavenParchment()
        mavenFabric()
        mavenNeoforged()
        mavenForge()
    }

    maven(url = "https://maven.terraformersmc.com/")
    maven(url = "https://api.modrinth.com/maven")
}

cloche {
    metadata {
        modId = mod_id
        val mod_name: String by project
        name = mod_name
        val mod_description: String by project
        description = mod_description
        val mod_license: String by project
        license = mod_license
        val mod_authors: String by project
        mod_authors.split(',').forEach(::author)
        version = mod_version
        // TODO: Add source and issues links
    }

    mappings {
        official()
    }

    val fabricCommon = common("fabric:common") {}

    targets.withType<FabricTarget> {
        dependsOn(fabricCommon)

        val fabric_loader_version: String by project
        loaderVersion.set(fabric_loader_version)

        metadata {
            entrypoint("main", "io.github.steveplays28.noisium.fabric.NoisiumFabric")
        }

        includedClient()
    }

    targets.withType<ForgeTarget> {
        dependencies {
            val mixin_extras_version: String by project
            val mixin_extras = module("io.github.llamalad7:mixinextras-forge:${mixin_extras_version}")
            annotationProcessor(mixin_extras)
            implementation(mixin_extras)
            include(mixin_extras)
        }
    }

    fabric("fabric:1.20") {
        minecraftVersion.set("1.20")

        dependencies {
            val fabric_api_version: String = "0.83.0+1.20"
            modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:${fabric_api_version}")
            val mod_menu_version: String = "7.0.1"
            modRuntimeOnly("com.terraformersmc:modmenu:${mod_menu_version}")
        }
    }

    fabric("fabric:1.20.1") {
        minecraftVersion.set("1.20.1")

        dependencies {
            val fabric_api_version: String = "0.92.5+1.20.1"
            modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:${fabric_api_version}")
            val mod_menu_version: String = "7.2.2"
            modRuntimeOnly("com.terraformersmc:modmenu:${mod_menu_version}")
            val lithium_version: String = "mc1.20.1-0.11.3-fabric"
            modRuntimeOnly("maven.modrinth:lithium:${lithium_version}")
            val c2me_version: String = "0.2.0+alpha.11.13+1.20.1"
            modRuntimeOnly("maven.modrinth:c2me-fabric:${c2me_version}")
        }
    }

    fabric("fabric:1.20.2") {
        minecraftVersion.set("1.20.2")
    }

    fabric("fabric:1.20.3") {
        minecraftVersion.set("1.20.3")
    }

    fabric("fabric:1.20.4") {
        minecraftVersion.set("1.20.4")
    }

    fabric("fabric:1.20.5") {
        minecraftVersion.set("1.20.5")
    }

    fabric("fabric:1.20.6") {
        minecraftVersion.set("1.20.6")
    }

    fabric("fabric:1.21") {
        minecraftVersion.set("1.21")
    }

    fabric("fabric:1.21.1") {
        minecraftVersion.set("1.21.1")

        dependencies {
            val fabric_api_version: String = "0.115.6+1.21.1"
            modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:${fabric_api_version}")
            val mod_menu_version: String = "11.0.3"
            modRuntimeOnly("com.terraformersmc:modmenu:${mod_menu_version}")
            val lithium_version: String = "mc1.21.1-0.15.0-fabric"
            modRuntimeOnly("maven.modrinth:lithium:${lithium_version}")
            val c2me_version: String = "0.3.0+alpha.0.319+1.21.1"
            modRuntimeOnly("maven.modrinth:c2me-fabric:${c2me_version}")
        }
    }

    fabric("fabric:1.21.2") {
        minecraftVersion.set("1.21.2")
    }

    fabric("fabric:1.21.3") {
        minecraftVersion.set("1.21.3")
    }

    fabric("fabric:1.21.4") {
        minecraftVersion.set("1.21.4")
    }

    fabric("fabric:1.21.5") {
        minecraftVersion.set("1.21.5")
    }

    forge("forge:1.20") {
        minecraftVersion.set("1.20")
        loaderVersion.set("46.0.14")
    }

    forge("forge:1.20.1") {
        minecraftVersion.set("1.20.1")
        loaderVersion.set("47.4.0")
    }

    forge("forge:1.20.2") {
        minecraftVersion.set("1.20.2")
        loaderVersion.set("48.1.0")
    }

    forge("forge:1.20.3") {
        minecraftVersion.set("1.20.3")
        loaderVersion.set("49.0.2")
    }

    forge("forge:1.20.4") {
        minecraftVersion.set("1.20.4")
        loaderVersion.set("49.2.0")
    }

    targets.all {
        when(minecraftVersion.get()) {
            "1.20.4" -> mappings { parchment("2024.04.14") }
            "1.20.3" -> mappings { parchment("2023.12.31") }
            "1.20.2" -> mappings { parchment("2023.12.10") }
            "1.20.1" -> mappings { parchment("2023.09.03") }
            "1.20" -> mappings { parchment("2023.09.03", "1.20.1") }
        }
    }

    // This target is at the bottom instead of at the top, as a workaround for terrarium-earth/cloche #12. See also terrarium-earth/cloche #59.
    // #12: https://github.com/terrarium-earth/cloche/issues/12
    // #59: https://github.com/terrarium-earth/cloche/issues/59
    targets.all {
        mixins.from(file("src/common/main/${mod_id}.mixins.json"))
        accessWideners.from(file("src/common/main/${mod_id}.accesswidener"))

        // TODO: Mark c2me as a suggestion and biox as breaking

        dependencies {
            implementation("org.jetbrains:annotations:26.0.2")
        }

        runs {
            client()
            server()
        }
    }
}
