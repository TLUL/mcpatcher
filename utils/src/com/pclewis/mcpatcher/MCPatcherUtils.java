package com.pclewis.mcpatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Collection of static methods available to mods at runtime.  This class is always injected into
 * the output minecraft jar.
 */
public class MCPatcherUtils {
    private static File minecraftDir;
    private static File propFile = null;
    private static Properties properties = new Properties();
    private static boolean needSaveProps = false;

    private MCPatcherUtils() {
    }

    static {
        String os = System.getProperty("os.name").toLowerCase();
        String baseDir = null;
        String subDir = ".minecraft";
        if (os.contains("win")) {
            baseDir = System.getenv("APPDATA");
        } else if (os.contains("mac")) {
            subDir = "Library/Application Support/minecraft";
        }
        if (baseDir == null) {
            baseDir = System.getProperty("user.home");
        }

        minecraftDir = new File(baseDir, subDir);
        if (minecraftDir.exists()) {
            propFile = new File(minecraftDir, "mcpatcher.properties");

            try {
                if (propFile.exists()) {
                    properties.load(new FileInputStream(propFile));
                } else {
                    set("HDTexture", "enableAnimations", true);
                    set("HDTexture", "useCustomAnimations", true);
                    saveProperties();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the path to a file/directory within the minecraft folder.
     *
     * @param subdirs zero or more path components
     * @return combined path
     */
    public static File getMinecraftPath(String... subdirs) {
        File f = minecraftDir;
        for (String s : subdirs) {
            f = new File(f, s);
        }
        return f;
    }

    private static String getPropertyKey(String mod, String name) {
        if (mod == null || mod.equals("")) {
            return name;
        } else {
            return mod + "." + name;
        }
    }

    /**
     * Gets a value from mcpatcher.properties.
     *
     * @param mod name of mod
     * @param name property name
     * @return String value
     */
    public static String getString(String mod, String name) {
        String value = properties.getProperty(getPropertyKey(mod, name));
        return value == null ? "" : value;
    }

    /**
     * Gets a value from mcpatcher.properties.
     *
     * @param name property name
     * @return String value
     */
    public static String getString(String name) {
        return getString(null, name);
    }

    /**
     * Gets a value from mcpatcher.properties.
     *
     * @param mod name of mod
     * @param name property name
     * @return int value or 0
     */
    public static int getInt(String mod, String name) {
        int value = 0;
        try {
            value = Integer.parseInt(getString(mod, name));
        } catch (NumberFormatException e) {
        }
        return value;
    }

    /**
     * Gets a value from mcpatcher.properties.
     *
     * @param name property name
     * @return int value or 0
     */
    public static int getInt(String name) {
        return getInt(null, name);
    }

    /**
     * Gets a value from mcpatcher.properties.
     *
     * @param mod name of mod
     * @param name property name
     * @return boolean value
     */
    public static boolean getBoolean(String mod, String name) {
        return Boolean.parseBoolean(getString(mod, name));
    }

    /**
     * Gets a value from mcpatcher.properties.
     *
     * @param name property name
     * @return boolean value
     */
    public static boolean getBoolean(String name) {
        return getBoolean(null, name);
    }

    /**
     * Sets a value in mcpatcher.properties.
     *
     * @param mod name of mod
     * @param name property name
     * @param value property value (must support toString())
     */
    public static void set(String mod, String name, Object value) {
        properties.setProperty(getPropertyKey(mod, name), value.toString());
        needSaveProps = true;
    }

    static void set(String name, Object value) {
        set(null, name, value);
    }

    /**
     * Save all properties to mcpatcher.properties.
     *
     * @return true if successful
     */
    public static boolean saveProperties() {
        if (! needSaveProps) {
            return true;
        }
        boolean saved = false;
        FileOutputStream os = null;
        if (properties != null && propFile != null) {
            try {
                os = new FileOutputStream(propFile);
                properties.store(os, "settings for MCPatcher");
                saved = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        needSaveProps = false;
        return saved;
    }
}