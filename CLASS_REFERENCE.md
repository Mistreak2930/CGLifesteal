# Lifesteal Plugin - Class Reference Guide

## 📂 Java Classes Overview

This document provides a quick reference for all Java classes in the Lifesteal plugin.

---

## 🎯 Main Plugin Class

### LifestealPlugin.java
**Purpose:** Main plugin entry point  
**Location:** `src/main/java/com/example/lifesteal/LifestealPlugin.java`  
**Key Methods:**
- `onEnable()` - Initialize plugin, load managers, register commands/listeners
- `onDisable()` - Save data when server stops
- `setupPlayerPoints()` - Load PlayerPoints via reflection
- `registerCommands()` - Register all command executors
- `registerListeners()` - Register all event listeners

**Instance Methods:**
- `getHeartManager()` - Get hearts manager
- `getTeamManager()` - Get team manager
- `getPlayerPointsAPI()` - Get PlayerPoints API object
- `hasPlayerPoints()` - Check if PlayerPoints available

---

## 💻 Command Classes

Located in: `src/main/java/com/example/lifesteal/commands/`

### HeartsCommand.java
**Purpose:** Display player hearts count  
**Commands:**
- `/hearts` - View own hearts
- `/hearts [player]` - View another player's hearts
**Permissions:** `lifesteal.hearts` (default: true)

### GiveHeartCommand.java
**Purpose:** Admin command to give hearts  
**Command:** `/giveheart <player> <amount>`  
**Permissions:** `lifesteal.admin` (default: op)  
**Features:**
- Validates player exists
- Validates amount is positive
- Shows before/after heart count
- Notifies recipient

### TakeHeartCommand.java
**Purpose:** Admin command to remove hearts  
**Command:** `/takeheart <player> <amount>`  
**Permissions:** `lifesteal.admin` (default: op)  
**Features:**