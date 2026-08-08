# 💬 LPC — Chat Formatting for LuckPerms

> Plugin de formato de chat para servidores **Paper/Spigot 1.21.x** (Java 21), integrado con **LuckPerms** y **PlaceholderAPI**.

[![Java](https://img.shields.io/badge/Java-21-orange.svg?logo=openjdk&logoColor=white)](https://adoptium.net)
[![Paper](https://img.shields.io/badge/Paper-1.21.x-white.svg?logo=paper&logoColor=white&color=2C2F33)](https://papermc.io)
[![LuckPerms](https://img.shields.io/badge/LuckPerms-5.4-blue.svg)](https://luckperms.net)
[![CommandAPI](https://img.shields.io/badge/CommandAPI-12.0.0-green.svg)](https://commandapi.jorel.dev)
[![PlaceholderAPI](https://img.shields.io/badge/PlaceholderAPI-2.11.6-purple.svg)](https://www.spigotmc.org/resources/placeholderapi.6245/)
[![Build](https://img.shields.io/badge/Estado-En%20desarrollo-yellow.svg)]()

---

## ✨ Características

- 🎨 **Formato de chat por grupo** — define un formato distinto por grupo de LuckPerms (`group-formats`).
- 🏷️ **Prefijos y sufijos** — usa el prefijo/sufijo de mayor prioridad y también los acumulados por peso.
- 🌈 **Tres sistemas de color** — MiniMessage (`<red>`), códigos legacy (`&a`) y HEX (`&#rrggbb`), cada uno controlado por su propio permiso.
- 🎭 **Colores de nombre y mensaje** — meta de LuckPerms `username-color` y `message-color`.
- 🧩 **PlaceholderAPI** — soporta placeholders de cualquier expansión en el formato.
- 🗺️ **Limpieza de chat** — comando `/lpc clear` con mensaje configurable.
- 🔍 **Modo debug** — `/lpc debug <jugador>` muestra toda la metadata resuelta de un jugador.
- ⚠️ **Detección de conflictos** — avisa si detecta plugins de chat que puedan duplicar el formato (EssentialsChat, VentureChat, etc.).

---

## 📋 Requisitos

| Dependencia | Tipo | Versión mínima |
|---|---|---|
| [Paper](https://papermc.io) | Servidor | 1.21.x (recomendado) |
| Java | Runtime | 21 |
| [LuckPerms](https://luckperms.net) | Dependencia dura | 5.4 |
| [CommandAPI](https://commandapi.jorel.dev) | Dependencia dura | 12.0.0 |
| [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) | Dependencia blanda | 2.11.6 *(opcional)* |

> Funciona también en **Spigot**: el plugin detecta en caliente si el evento `AsyncChatEvent` de Paper está disponible y usa el listener adecuado.

---

## 🚀 Instalación

1. Coloca `LPC-1.0.0.jar` en la carpeta `plugins/` de tu servidor.
2. Reinicia el servidor (o recarga con otro plugin de recarga).
3. Configura `plugins/LPC/config.yml` a tu gusto.
4. Ejecuta `/lpc reload` para aplicar los cambios.

---

## ⚙️ Configuración

El archivo `config.yml` se genera automáticamente en la primera ejecución.

### Placeholders del formato

| Placeholder | Descripción |
|---|---|
| `{message}` | El mensaje del chat |
| `{name}` | El nombre del jugador |
| `{displayname}` | El nombre visible / apodo |
| `{world}` | El mundo actual del jugador |
| `{prefix}` | Prefijo de mayor prioridad |
| `{suffix}` | Sufijo de mayor prioridad |
| `{prefixes}` | Todos los prefijos ordenados por prioridad |
| `{suffixes}` | Todos los sufijos ordenados por prioridad |
| `{username-color}` | Color del nombre (meta de LuckPerms) |
| `{message-color}` | Color del mensaje (meta de LuckPerms) |

### Ejemplo

```yaml
chat-format: "{prefix}{name}&r: {message}"

# Formato por grupo (opcional, anula chat-format)
group-formats:
  default: "{prefix}{name}&r: {message}"
  admin: "&c[Admin] {prefix}{name}&r: {message}"

# Mensaje tras /lpc clear
clear-chat-message: "&7El Chat Ha sido Limpiado"
```

### Meta de LuckPerms para colores

```bash
# Con la consola de LuckPerms (/lp)
lp user <jugador> meta set username-color <color>
lp user <jugador> meta set message-color <color>
lp group <grupo> meta set username-color <color>
```

---

## 🛡️ Permisos

| Permiso | Descripción |
|---|---|
| `lpc.colorcodes` | Usar códigos legacy (`&a`, `&b`, …) en el chat |
| `lpc.rgbcodes` | Usar colores HEX (`&#rrggbb`) en el chat |
| `lpc.minimessage` | Usar tags MiniMessage (`<red>`, `<gradient:…>`, …) en el chat |
| `lpc.admin` | *(Reservado)* — documentado en `config.yml` |

> El comando `/lpc` exige **OP** por defecto (nodo `lpc` gestionado por CommandAPI).

---

## ⌨️ Comandos

| Comando | Descripción |
|---|---|
| `/lpc reload` | Recarga la configuración |
| `/lpc clear` | Limpia el chat de todos los jugadores y envía el mensaje configurado |
| `/lpc debug <jugador>` | Muestra prefijos, sufijos, colores y formato resuelto del jugador |

---

## 🏗️ Arquitectura

```
src/main/java/com/spectrasonic/lpc/
├── Main.java                  # Clase principal (JavaPlugin)
├── command/
│   └── LPCCommand.java        # Comando /lpc (CommandAPI 12)
├── listeners/                 # Listener de chat (Paper + fallback Spigot)
│   ├── ChatListener.java
│   └── PaperChatListener.java
├── managers/
│   ├── CommandManager.java    # Registro centralizado de comandos
│   ├── ListenerManager.java   # Registro centralizado de listeners
│   ├── ChatManager.java       # Lógica de formato y procesado del chat
│   └── LuckPermsManager.java  # Fachada sobre la API de LuckPerms
└── util/
    └── ColorUtils.java        # Utilidad de colores (MiniMessage/legacy/HEX)
```

### Flujo del chat

```
Jugador escribe mensaje
        │
        ▼
AsyncChatEvent (HIGHEST)
        │
        ├── processMessage()  → aplica color según permisos del jugador
        ├── buildFormat()     → resuelve formato del grupo + placeholders
        │                        (LuckPerms meta + PlaceholderAPI)
        └── renderer          → compone el mensaje final con Adventure
```

---

## 🧰 Stack técnico

- **Lenguaje:** Java 21
- **API:** Paper API 26.1.2 (compatible Spigot)
- **Comandos:** CommandAPI 12.0.0 (registro separado de lógica)
- **Mensajes:** Adventure / MiniMessage
- **Permisos:** LuckPerms API 5.4
- **Boilerplate:** Lombok 1.18.46
- **Build:** Maven

---

## 📚 Recursos útiles

- [LuckPerms — Prefijos, Sufijos & Meta](https://luckperms.net/wiki/Prefixes,-Suffixes-&-Meta)
- [MiniMessage — Documentación y visor](https://docs.advntr.dev/minimessage/format.html)
- [CommandAPI — Documentación](https://commandapi.jorel.dev)
- [PlaceholderAPI — Expansiones](https://api.extendedclip.com/all)

---

## 📝 Notas

- Los permisos de color se evalúan en cadena: tener `lpc.colorcodes` **y** `lpc.rgbcodes` permite todo; tener solo uno degrada el otro a texto plano.
- Si usas otro plugin de chat, desactiva su formato para evitar mensajes duplicados (el plugin te avisará en consola si lo detecta).
