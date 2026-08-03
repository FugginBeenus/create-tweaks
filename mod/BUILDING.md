# Building

## JDK

Two different JDKs are in play and mixing them up is the first thing that goes wrong.

- **Gradle itself** must run on **JDK 17 or 21**. Loom cannot run on JDK 25, which fails with
  `Unsupported class file major version 69`.
- **Compilation** targets 17 via the toolchain block, and Gradle provisions that on its own.

```bash
export JAVA_HOME=/path/to/jdk-17
./gradlew build
```

In IntelliJ set **Settings, Build Tools, Gradle, Gradle JVM** to 17 or 21. Leave the project SDK alone.

## Dependencies

Create is on the dev runtime classpath, so `runClient` has real Create loaded. It drags in the whole
Porting Lib stack, which is spread across five mavens:

| Repo | Provides |
|---|---|
| `mvn.devos.one/snapshots` | Create Fabric |
| `mvn.devos.one/releases` | Porting Lib |
| `maven.createmod.net` | Flywheel, Ponder |
| `maven.jamieswhiteshirt.com/libs-release` | Reach Entity Attributes |
| `raw.githubusercontent.com/Fuzss/modresources` | Forge Config API Port |

`create_version` in `gradle.properties` is the dev runtime version and is **not** the newest release.
The devos maven stops at `6.0.7.0+mc1.20.1-build.1716`; `6.0.8.1` ships on Modrinth but was never
published to maven. This only affects the dev environment, since the mod references exactly one Create
class by name and compiles against none.

## Running

**`./gradlew runClient` works.** Use it.

**`./gradlew runServer` does not, and it is not this mod's fault.** Create 6.0.7.0 crashes during item
registration in a dedicated dev server:

```
Cannot load class net.minecraft.client.network.ClientPlayerEntity in environment type SERVER
  at com.simibubi.create.AllItems.lambda$static$36(AllItems.java:358)
```

Create touches a client-only class from a static initializer. Nothing from this mod appears in that
stack, and `createtweaks` loads successfully before it. Test server behaviour by opening a singleplayer
world in `runClient`, which runs the integrated server, or by dropping the built jar onto a real server.
