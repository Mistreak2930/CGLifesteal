# Building CGLifesteal

## Requirements

- Java 25 JDK
- Maven 3.8+

## Build

From the repository root, run:

```bash
mvn clean package
```

The packaged plugin is `target/CGLifesteal-1.5.0.jar`.

## Server Requirements

Run the plugin on Paper 26.3 with Java 25. PlayerPoints, Vault with an economy provider, and PlaceholderAPI are optional integrations. Features that require an absent integration are disabled.

Install the JAR in the server's `plugins/` directory and restart the server. Configuration and data files are created in `plugins/CGLifesteal/`.

## Troubleshooting

- Confirm `java -version` reports Java 25 or newer.
- Confirm Maven can reach Maven Central and the PaperMC repository.
- Check `logs/latest.log` for plugin startup errors.
- Check that the server data directory is writable.
