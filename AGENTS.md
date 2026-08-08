# PaperMC Plugin Agent

## Role

This file guides code agents in creating and maintaining Minecraft Java 21 plugins under the project's conventions: package architecture, centralized registration, code style, messaging with MiniMessage, redude bolerplate with Lombok and use of existing utilities. It keeps instructions for agents separate from the README, in line with the emerging `AGENTS.md` and `global_rules.md` standard.

Important All responses are in `Spanish`, incluiding thinking if is posible

## Project Scope

- Objective: Plugins for Spigot/Paper compatible servers on Minecraft 1.21.x, compiled with Java 21.
- Target Platform: `Paper/Spigot API`, with Spigot-style listeners and compatibility on Paper.
- Language: Java 21 with Lombok to reduce boilerplate.

## Code Style Rules

- Main.java always be Main file of the project
- Use CommandAPI to create all plugin commands, without exception.
- Use the MessageUtils class and its methods for all messages; the format will be MiniMessage. Avoid direct calls to Adventure APIs outside of this utility.
- Build items exclusively with ItemBuilder.
- Play sounds using the SoundUtils class.
- Use Lombok annotations where appropriate (@Getter, @Setter, @Builder, @RequiredArgsConstructor, etc.) for readability and consistency.
- Java 21 as the minimum version to compile and run.

# Global Rules

- Always first read `@memory-bank` folder for context MCP memory-bank
- Whenever you finish implementing a feature or making significant changes to the project, update `@memory-bank`
- Try breaking down the task into smaller steps or task
- Never write Javadocs comments
- Always use Good design patterns in the code
- Always Use `context7 MCP`
- If need more context before coding, you can search in the internet using `web-search`
- Write all comments in-line in Spanish.
- If you write comments in the code, write them in Spanish.
- In the case of Java classes, modularize classes into different classes. We aim for classes not to exceed `200 lines of code`, so we can keep everything more organized. Depending on what is needed, you can create classes or packages within, following the logic and consistency of the project.
- The names of the classes or packages that are created should have a name that makes sense for the project.
- Use Code Architecture
- Use SOLID Principles
- Use Design Patterns
- Use Clean Code SKILL
- Use Clean Architecture Principles
- Always use `Context7` when I need library/API documentation, code generation, setup or configuration steps without me having to explicitly ask.
- If the task mentions `architecture`, `events`, `persistence`, `concurrency`, `version compatibility,` or `unclear debugging`, use `sequential_thinking MCP` before coding
- Do not modify utilities under `com.spectrasonic.utils.*` or `com.spectrasonic.Utils.*`; they are considered correct and stable classes inmutables.
- Reuse existing utilities (MessageUtils, ItemBuilder, SoundUtils, CommandUtils, PluginLogger) instead of duplicating them.
- For Messages use `messages-manager` SKILL when need to create or modify messages. with a single line of code, and using a MessagesManager class.

## Restrictions

- The agent **must not generate comments in Javadoc format** (`/** ... */`) under any circumstances.
- Never delete the .git directory.
- Do not print or modify pom.xml unless explicitly requested.
- Do not use block comments `\* */`; use brief and clear line comments `//`.
- Do not write Javadocs comments.
- Do not modify utilities under com.spectrasonic.utils._ or com.spectrasonic.Utils._.
- Do not compile the project or request to compile it; the user will compile it manually.
- Do not use typical Javadoc annotations such as:
- Javadoc comments create unnecessary noise in the code and are not part of the desired standard for this project. We prioritize clean, self-explanatory code without redundant inline documentation.
    - `@param`
    - `@return`
    - `@throws`
    - `@see`
    - `@link`
- Do not document classes, methods, or attributes using Javadoc comments

## Permitted Alternatives

- Clear and self-explanatory code (descriptive names)
- Use of simple inline comments only if strictly necessary:
    ```java
    // Brief explanation if the context is not obvious
    ```

## Architecture and Organization

## File Tree Project

Proyect package always is `com.spectrasonic.{PojectName}`
**Basic file tree structure:**

```
src/main/
├── java/com/spectrasonic/
│   ├── {ProjectName}/
│   │   ├── commands/
│   │   ├── listeners/
│   │   ├── enums/
│   │   ├── managers/
│   │   │   ├── CommandManager.java
│   │   │   ├── EventManager.java
│   │   │   ├── MessagesManager.java
│   │   │   └── ConfigManager.java
│   │   ├── config/
│   │   │   └── ConfigLoader.java
│   │   └── Main.java // Proyect Main class
│   └── Util/
│       ├── CommandUtils.java
│       ├── ItemBuilder.java
│       ├── MessageUtils.java
│       ├── SoundUtils.java
│       └── PluginLogger.java
└── resources/
    ├── config.yml
    ├── plugin.yml
    └── messages.yml
```

### Commands

- Register all commands in CommandManager.
- Implement each command in the Command package and maintain one class per command.
- Declare CommandAPI literals and arguments in these classes; avoid ad-hoc registrations.
- Use rules written bellow to more info of CommandAPI Rules
- Use `commandapi` SKILL to create commands
- The logic for executing commands will be separated from the main method where the command is registered; in other words, the command is registered in one method, while the separate logic will be implemented in other methods using handles, resulting in cleaner and more understandable code.

### Events

- Register all events in EventManager.
- Implement each event/handler in the Event package.

### Listeners

- Register all listeners in ListenerManager.
- Implement listeners with the Spigot API (compatible with Paper).

### Configuration

- Register all configurations in ConfigManager.
- Implement configuration classes in the Config package.

### Messages

- All strings in messages.yml; access via MessageManager using keys like "message.category.messagekey", rendered with MiniMessage.

## Message Conventions (MiniMessage)

- Format: MiniMessage for chat, action bars, and titles; use MessageUtils/MessageManager to parse and send.
- YML keys: "message.category.<namekey>", e.g., "message.player.no_permission".
- Use MiniMessage features where applicable: HEX colors, gradients, hover/click events; verify support based on context (e.g., titles have limited interactivity).
- Useful references: MiniMessage documentation and viewer to validate templates.

## Research and Documentation

To research dependencies, use “Context7” with official sources:

- `SpigotMC/Paper`: requirements, startup, and API.
- `MiniMessage` (Adventure): format, tags, and viewer.
- `CommandAPI`: check official docs before defining arguments/brigadier.
- `Java 21`: installation/use in local environments.

### Events and Listeners:

- Keep classes small and specific to their responsibility; register them in their corresponding Manager.

### Configuration:

- Load by keys; validate input and fallback to default values; do not hardcode strings.

### Manual Testing:

- Verify MiniMessage messages in the viewer before integrating them; validate placeholders.

## MessageUtils definitions

In the `MessageUtils.java` class, several methods for sending messages are defined:

- sucessMessage (green) - correct messages
- alertMessage (yellow) - alerts
- denyMessage (red) - incorrect or denial
- warningMessage (red) - errors
- infoMessage (aqua) - information
- debugMessage (blue) - debug messages
