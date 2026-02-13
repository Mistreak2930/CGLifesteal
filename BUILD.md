# Building the Lifesteal Plugin

## Prerequisites

- **Maven** 3.6+
- **Java** 8+ JDK
- **Git** (optional)

## Build Instructions

### Step 1: Clone or Navigate to Project
```bash
cd /workspaces/Lifesteal
```

### Step 2: Build with Maven
```bash
mvn clean package
```

This will:
1. Compile all Java source files
2. Package the plugin into a JAR file
3. Place the compiled JAR in the `target/` directory

### Step 3: Find the Built JAR
```bash
ls -la target/
```

The plugin JAR will be named: `Lifesteal-1.0.0.jar`

## Installing on Your Server

1. **Stop your server** (if running)
2. **Place the JAR file** in your server's `plugins/` directory:
   ```bash
   cp target/Lifesteal-1.0.0.jar /path/to/server/plugins/
   ```

3. **Ensure dependencies are installed:**
   - PlayerPoints (required)
   - Spigot/Bukkit 1.12.2 (buildtools or pre-built)

4. **Start your server:**
   ```bash
   java -Xmx1024M -Xms1024M -jar spigot.jar nogui
   ```

5. **Verify installation:**
   - Check console for `[Lifesteal] Lifesteal plugin enabled!`
   - Configuration files will be auto-generated in `plugins/Lifesteal/`

## Troubleshooting Build Issues

### Maven not found
```bash
# Install Maven (Ubuntu/Debian)
sudo apt-get install maven

# Or use your system's package manager
```

### Java not found
```bash
# Install Java JDK
sudo apt-get install openjdk-8-jdk

# Or use your system's package manager
```

### Build fails with dependency errors
- Ensure internet connection (downloads dependencies from Maven Central)
- Try: `mvn clean package -U` (force update)

### Plugin not loading
- Check `logs/latest.log` for errors related to Lifesteal
- Verify `plugins/Lifesteal/` folder exists
- Check that PlayerPoints is installed

## Development Build

For development with live reload:

```bash
# In your IDE (VS Code, IntelliJ, etc.):
# 1. Open this project
# 2. Set up debugging breakpoints
# 3. Use Maven integration to run `mvn clean package`
```

## Structure

```
Lifesteal/
├── src/
│   ├── main/
│   │   ├── java/com/example/lifesteal/
│   │   │   ├── LifestealPlugin.java
│   │   │   ├── commands/
│   │   │   ├── listeners/
│   │   │   ├── gui/
│   │   │   └── system/
│   │   └── resources/
│   │       ├── plugin.yml
│   │       └── config.yml
│   └── test/
├── target/                 (generated after build)
│   └── Lifesteal-1.0.0.jar
├── pom.xml                 (Maven configuration)
└── README.md
```

## Version Management

Current version: **1.0.0**

To update version:
1. Edit `pom.xml` - change `<version>` tag
2. Edit `plugin.yml` - change `version:` field
3. Rebuild with `mvn clean package`

## Performance Notes

- Plugin uses asynchronous tasks where possible
- Data is saved automatically after changes
- Minimal performance impact on server
- Compatible with other plugins (non-invasive)

## Debugging

To see more detailed logs:
1. Add debug statements in IDE
2. Check `plugins/Lifesteal/` folder for data files
3. Review server logs: `logs/latest.log`

---

Happy building!
