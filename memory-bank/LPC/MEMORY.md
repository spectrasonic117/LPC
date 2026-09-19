# LPC — Memory Bank

## Descripción
Plugin de formato de chat para servidores Paper/Spigot 1.21.x (Java 21) integrado con LuckPerms y PlaceholderAPI. Reescrito desde una versión decompilada.

## Estado actual
- [x] Migración de ACF → CommandAPI completada
- [x] Lombok aplicado a managers, listeners y comando
- [x] LSP limpio: 0 errores, 0 advertencias en `src/main/java`
- [x] Sistema de mensajes completo (MessagesManager) implementado siguiendo la skill messages-manager:
  - `managers/MessageManager.java` — singleton estricto: constructor privado + `MessageManager.init(plugin)` (invocado una sola vez desde ConfigManager); `getMessage(key)`, `getMessage(key, replacements)` con placeholders `<x>`, `component(key)` que deserializa MiniMessage
  - `util/MessageUtils.java` — @UtilityClass, única clase que envía texto player-facing; prefijos tipados (success/alert/deny/warning/info/debug), `sendComponent()`, broadcast, titles y action bars
  - `managers/ConfigManager.java` — posee config.yml y delega messages.yml; `reload()` = config + messages
  - `resources/messages.yml` — todos los mensajes por keys `messages.<categoría>.<clave>`, MiniMessage, en español; categorías: reload, permission, command, chat, debug
  - `clear-chat-message` migrado de config.yml → `messages.chat.cleared` (config.yml ya no lo contiene; un config.yml antiguo con esa clave quedará ignorado sin romper nada)
  - `/lpc reload` ahora pasa por `plugin.getConfigManager().reload()`
  - `LPCCommand` sin mensajes hardcodeados: reload, clear y debug leen TODO de messages.yml
  - Debug: labels desde messages.yml como Component + valores legacy/hex de LuckPerms resueltos con `ColorUtils.deserialize()` y `Component.append()` (los valores legacy NO se interpolan en strings MiniMessage)
- [x] Permisos de comando en LPCCommand.java (CommandAPI): `lpc.reload`, `lpc.clearchat`, `lpc.debug`
- [x] `lpc.minimessage` declarado en plugin.yml
- [x] README.md documentado por completo

## Arquitectura
- `com.spectrasonic.lpc.Main` — clase principal (JavaPlugin, Lombok @Getter); expone `getConfigManager()`, `getMessageManager()`, `getChatManager()`, `getLuckPermsManager()`
- `managers/` — CommandManager, ListenerManager, ChatManager, LuckPermsManager, MessageManager (singleton), ConfigManager
- `command/LPCCommand.java` — `/lpc` con subcomandos reload, clear, debug (CommandAPI 12.0.0)
- `listener/` — ChatListener (Spigot fallback), PaperChatListener (Paper AsyncChatEvent)
- `util/` — ColorUtils (legacy/hex/MiniMessage) y MessageUtils (envío de mensajes)
- `resources/` — plugin.yml, config.yml (formatos de chat), messages.yml (todos los textos)

## Dependencias (pom.xml)
- paper-api 26.1.2.build.63-stable (provided)
- lombok 1.18.46 (provided + annotationProcessorPaths)
- luckperms api 5.4 (provided)
- placeholderapi 2.11.6 (provided)
- commandapi-paper-core 12.0.0 (provided)

## Decisiones de diseño
- CommandAPI: registro separado de lógica, `.register(plugin)` con namespace, permisos por subcomando vía `.withPermission(...)`.
- `CommandArguments` en CommandAPI 12.x vive en `dev.jorel.commandapi.executors.CommandArguments`.
- Lambdas de `.executes` con tipos explícitos `(CommandSender, CommandArguments)`.
- `plugin.yml` declara `depend: [LuckPerms, CommandAPI]`.
- Permisos de feature de chat (`lpc.colorcodes`, `lpc.rgbcodes`, `lpc.minimessage`) en plugin.yml (los consulta ChatManager).
- MessageManager: patrón singleton con `init()` idempotente (solo instancia si `instance == null`); `reloadMessages()` solo recarga la configuración en memoria, nunca recrea el archivo; el constructor se mantuvo privado (no package-private) para blindar el singleton.
- Los placeholders de `getMessage(key, replacements)` se sustituyen antes de deserializar MiniMessage, por lo que los valores reemplazados pueden contener tags MiniMessage.
- Mensajes por defecto en español (coherente con el mensaje original de clear-chat).
- ChatManager: `buildFormat()` resuelve `group-formats.<grupo>` o `chat-format`; ListenerManager detecta Paper vía AsyncChatEvent.
- Main.onEnable: ConfigManager se crea PRIMERO (config + messages disponibles siempre), luego el check de LuckPerms que deshabilita el plugin si falta.

## Pendientes / Observaciones
- Confirmar con el usuario si quiere versión en inglés del README o badges de CI.
- Pendiente: `/lpc` no valida `only_player` porque todos los subcomandos aceptan CommandSender (correcto); `MessageUtils.onlyPlayerMessage()` disponible para futuros subcomandos player-only.
- El README aún no documenta messages.yml (solo config.yml) — candidata a actualización de documentación.
