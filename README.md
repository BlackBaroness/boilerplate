That's my library I use to avoid copying and pasting the same damn code I wrote over the years to every single project I
create.

It covers a lot of my typical stacks:

- Hibernate, plain JDBC
- Some cryptography
- Fast I/O, mostly with files
- `kotlinx.serialization` (CBOR, JSON, Kaml), `ktor`
- PaperMC, BungeeCord platforms
- Adventure, MiniMessage for Minecraft platforms
- LiteCommands

All these are mostly optional. The library itself does not have any dependencies. If you use a specific function in
runtime, you need associated classes. Otherwise, you don't need anything.

I also heavily avoid adding "global" functions accessible from everywhere, because that fills your IDE suggestions too
much...
Why do Kotlin library devs like doing that? :(

Functions mostly require some receiver. Sometimes it is `Boilerplate` object, but mostly it is linked to the associated
class directly, like `InetAddress.asInt`.

## Some of the big features:

- [Hibernate session factory builder](docs/Hibernate-session-factory-builder.md)
- [More lateinit types](docs/More-lateinit-types.md)
- [Write-only properties](docs/Write-only-properties.md)
- [HikariCP builder](docs/HikariCP-builder.md)
- Message brokers backed by `BungeeCord` channels or Redis, using `kotlinx.serialization` and `zstd` compression.
- [Item configuration](docs/Item-configuration.md)
- [Menu configuration](docs/Menu-configuration.md)

---

```kotlin
maven("https://jitpack.io")
```

```kotlin
implementation("com.github.BlackBaroness.boilerplate:boilerplate-MODULE:VERSION")
```

Java version during the build: 21 (to support latest Minecraft version)

Target java version: 17 (to support older Minecraft versions)

Feel free to make PRs and have fun!
