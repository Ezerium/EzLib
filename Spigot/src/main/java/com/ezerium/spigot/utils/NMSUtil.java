package com.ezerium.spigot.utils;

import com.mojang.authlib.GameProfile;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.ezerium.spigot.utils.Util.getNMSClass;
import static com.ezerium.spigot.utils.Util.getOBCClass;

@UtilityClass
public class NMSUtil {

    public static void sendPacket(Player player, Object packet) {
        try {
            Class<?> craftPlayerClass = getOBCClass("entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object handle = craftPlayerClass.getDeclaredMethod("getHandle").invoke(craftPlayer);

            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getMCServer() {
        try {
            Class<?> craftServerClass = getOBCClass("CraftServer");
            Object craftServer = craftServerClass.cast(Bukkit.getServer());
            return craftServerClass.getDeclaredMethod("getServer").invoke(craftServer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getWorldServer(World world) {
        try {
            Class<?> craftWorldClass = getOBCClass("CraftWorld");
            Object craftWorld = craftWorldClass.cast(world);
            return craftWorldClass.getDeclaredMethod("getHandle").invoke(craftWorld);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object createEntityPlayer(Object mcServer, Object worldServer, GameProfile gameProfile, Location location) {
        try {
            Class<?> interactManagerClass = getNMSClass("PlayerInteractManager");
            Object interactManager = interactManagerClass.getDeclaredConstructors()[0]
                    .newInstance(worldServer);

            Class<?> entityPlayerClass = getNMSClass("EntityPlayer");
            Object entityPlayer = entityPlayerClass.getDeclaredConstructor(
                    getNMSClass("MinecraftServer"),
                    getNMSClass("WorldServer"),
                    GameProfile.class,
                    interactManagerClass
            ).newInstance(mcServer, worldServer, gameProfile, interactManager);

            entityPlayerClass
                    .getMethod("setLocation", double.class, double.class, double.class, float.class, float.class)
                    .invoke(entityPlayer, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            return entityPlayer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getEntityPlayer(Player player) {
        try {
            Class<?> craftPlayerClass = getOBCClass("entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            return craftPlayerClass.getDeclaredMethod("getHandle").invoke(craftPlayer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName).get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getField(Object object, String fieldName) {
        try {
            return object.getClass().getDeclaredField(fieldName).get(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setField(Class<?> clazz, String fieldName, Object value) {
        try {
            clazz.getDeclaredField(fieldName).set(null, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object invokeMethod(Object object, String methodName, Object... args) {
        try {
            Class<?>[] classes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                classes[i] = args[i].getClass();
            }
            return object.getClass().getMethod(methodName, classes).invoke(object, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
