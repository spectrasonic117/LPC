# LPC — Memory Bank

## Descripción
Plugin de formato de chat para servidores Paper/Spigot 1.21.x (Java 21) integrado con LuckPerms y PlaceholderAPI. Reescrito desde una versión decompilada.

## Estado actual
- [x] Migración de ACF → CommandAPI completada
- [x] Lombok aplicado a managers, listeners y comando
- [x] LSP limpio: 0 errores, 0 advertencias en `src/main/java`
- [x] Permisos de comando movidos de plugin.yml a LPCCommand.java (CommandAPI los maneja): `lpc.reload`, `lpc.clearchat`, `lpc.debug`
- [x] `lpc.minimessage` declarado en plugin.yml (antes faltaba)
- [x] README.md documentado por completo (características, requisitos, instalación, configuración, permisos, comandos, arquitectura, stack técnico y notas). Está en español, coherente con el proyecto.
- [ ] PENDIENTE (verificado): `pom.xml` `<main.class>` YA apunta correctamente a `com.spectrasonic.lpc.Main` — el problema del memory bank anterior estaba resuelto, no requiere edición.

## Arquitectura
- `com.spectrasonic.lpc.Main` — clase principal (JavaPlugin, con Lombok @Getter)
- `command/LPCCommand.java` — comando `/lpc` con subcomandos reload, clear, debug (CommandAPI 12.0.0)
- `managers/` — CommandManager, ListenerManager, ChatManager, LuckPermsManager (Lombok @RequiredArgsConstructor/@Getter)
- `listener/` — ChatListener (Spigot fallback), PaperChatListener (Paper AsyncChatEvent)
- `util/ColorUtils.java` — utilidad de colores MiniMessage/legacy/hex (no es la util global, se puede editar)

## Dependencias (pom.xml)
- paper-api 26.1.2.build.63-stable (provided)
- lombok 1.18.46 (provided + annotationProcessorPaths)
- luckperms api 5.4 (provided)
- placeholderapi 2.11.6 (provided)
- commandapi-paper-core 12.0.0 (provided)

## Decisiones de diseño
- CommandAPI: registro separado de lógica (skill commandapi), `.register(plugin)` con namespace, permisos por subcomando (`lpc.reload`, `lpc.clearchat`, `lpc.debug`) vía `.withPermission(...)` — no necesitan declaración en plugin.yml.
- `CommandArguments` en CommandAPI 12.x vive en `dev.jorel.commandapi.executors.CommandArguments` (no en `dev.jorel.commandapi`).
- Lambdas de `.executes` con tipos explícitos `(CommandSender, CommandArguments)` para evitar ambigüedad del compilador.
- `plugin.yml` ya declara `depend: [LuckPerms, CommandAPI]` → no requiere inicialización manual de CommandAPI.
- Permisos de feature de chat (`lpc.colorcodes`, `lpc.rgbcodes`, `lpc.minimessage`) SÍ quedan en plugin.yml porque los consulta `ChatManager.processMessage()` con `player.hasPermission(...)` — CommandAPI no puede manejarlos.
- IMPORTANTE: los permisos de comando (reload/clear/debug) eran `default: op` en plugin.yml. Ahora, al ser manejados por CommandAPI sin declaración en plugin.yml, **no tienen default op**: solo podrán ejecutarlos quienes tengan el nodo otorgado (LuckPerms/admin). El comando raíz `/lpc` usa `CommandPermission.OP`.
- ChatManager: `buildFormat()` resuelve `group-formats.<grupo>` o `chat-format`; reemplaza placeholders `{prefix}`, `{suffix}`, `{prefixes}`, `{suffixes}`, `{world}`, `{name}`, `{displayname}`, `{username-color}`, `{message-color}`; aplica PlaceholderAPI si está activo.
- ListenerManager detecta Paper por `Class.forName("io.papermc.paper.event.player.AsyncChatEvent")` y registra PaperChatListener (renderer de Adventure) o ChatListener (mutación de event.message).
- Main.onEnable: aborta y deshabilita el plugin si LuckPerms no está cargado; avisa en consola si detecta plugins de chat conflictivos (EssentialsChat, VentureChat, HeroChat, DeluxeChat, ChatManager, ChatEx, UltraChat, TownyChat).

## Pendientes / Observaciones
- Confirmar con el usuario si quiere versión en inglés del README o badges de CI.
- ColorUtils.java tenía 2 advertencias de raw type (`new HashMap()`) → corregidas con `new HashMap<>()`.
