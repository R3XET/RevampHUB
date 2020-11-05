package me.allen.ziggurat.impl.universal;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.allen.ziggurat.Ziggurat;
import me.allen.ziggurat.ZigguratTablist;
import me.allen.ziggurat.objects.IZigguratHelper;
import me.allen.ziggurat.objects.SkinTexture;
import me.allen.ziggurat.objects.TabColumn;
import me.allen.ziggurat.objects.TabEntry;
import me.allen.ziggurat.util.ZigguratCommons;
import me.allen.ziggurat.util.player.LegacyClientUtils;
import me.allen.ziggurat.util.player.OfflinePlayerUtil;
import me.allen.ziggurat.util.player.PlayerUtil;
import me.allen.ziggurat.util.reflection.Reflection;
import me.allen.ziggurat.util.reflection.ReflectionUtil;
import me.allen.ziggurat.util.version.PlayerVersion;
import me.allen.ziggurat.util.version.ServerVersion;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.Collections;

public class UniversalTabImpl implements IZigguratHelper
{
    private static Object nmsServer;
    private static Object worldServer;
    private static Object playerInteractManager;
    
    private static void sendPacket(Player player, Object packet) {
        try {
            Object nmsPlayer = ReflectionUtil.invokeMethod(player, "getHandle");
            Object playerConnection = ReflectionUtil.getField(nmsPlayer.getClass(), true, "playerConnection").get(nmsPlayer);
            ReflectionUtil.invokeMethod(playerConnection, "sendPacket", packet);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static String fromText(String text) {
        return ComponentSerializer.toString(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text)));
    }
    
    private static Object convertComponent(String json) {
        try {
            return ReflectionUtil.invokeMethod(null, Reflection.getInnerClass(ReflectionUtil.PackageType.MINECRAFT_SERVER.getClass("IChatBaseComponent"), "ChatSerializer"), "a", json);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void removeSelf(Player player) {
        try {
            Bukkit.getOnlinePlayers().forEach((online) -> {
                try {
                    Object entityPlayer = ReflectionUtil.invokeMethod(player, "getHandle");
                    Object playerInfo = ReflectionUtil.instantiateObject("PacketPlayOutPlayerInfo", ReflectionUtil.PackageType.MINECRAFT_SERVER, Reflection.getEnum(Reflection.getInnerClass(ReflectionUtil.PackageType.MINECRAFT_SERVER.getClass("PacketPlayOutPlayerInfo"), "EnumPlayerInfoAction"), "REMOVE_PLAYER"), Collections.singletonList(entityPlayer));
                    sendPacket(online, playerInfo);
                } catch (Exception var4) {
                    var4.printStackTrace();
                }

            });
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    
    @Override
    public TabEntry createFakePlayer(ZigguratTablist zigguratTablist, String string, TabColumn column, Integer slot, Integer rawSlot) {
        try {
            OfflinePlayer offlinePlayer = OfflinePlayerUtil.createOfflinePlayer(string);
            Player player = zigguratTablist.getPlayer();
            PlayerVersion protocolVersion = PlayerUtil.getPlayerVersion(player);
            GameProfile gameProfile = new GameProfile(offlinePlayer.getUniqueId(), (protocolVersion != PlayerVersion.v1_7) ? string : (LegacyClientUtils.tabEntrys.get(rawSlot - 1) + ""));
            Object entityPlayer = ReflectionUtil.instantiateObject("EntityPlayer", ReflectionUtil.PackageType.MINECRAFT_SERVER, UniversalTabImpl.nmsServer, UniversalTabImpl.worldServer, gameProfile, UniversalTabImpl.playerInteractManager);
            Object playerInfo = ReflectionUtil.instantiateObject("PacketPlayOutPlayerInfo", ReflectionUtil.PackageType.MINECRAFT_SERVER, Reflection.getEnum(Reflection.getInnerClass(ReflectionUtil.PackageType.MINECRAFT_SERVER.getClass("PacketPlayOutPlayerInfo"), "EnumPlayerInfoAction"), "ADD_PLAYER"), Collections.singletonList(entityPlayer));
            if (protocolVersion != PlayerVersion.v1_7) {
                gameProfile.getProperties().put("texture", new Property("textures", ZigguratCommons.defaultTexture.getValue(), ZigguratCommons.defaultTexture.getSignature()));
            }
            sendPacket(player, playerInfo);
            return new TabEntry(string, offlinePlayer, "", zigguratTablist, ZigguratCommons.defaultTexture, column, slot, rawSlot, 0);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void updateFakeName(ZigguratTablist zigguratTablist, TabEntry tabEntry, String text) {
        try {
            Player player = zigguratTablist.getPlayer();
            PlayerVersion protocolVersion = PlayerUtil.getPlayerVersion(player);
            String[] newStrings = ZigguratTablist.splitStrings(text, tabEntry.getRawSlot());
            if (protocolVersion == PlayerVersion.v1_7) {
                Team team = player.getScoreboard().getTeam(LegacyClientUtils.teamNames.get(tabEntry.getRawSlot() - 1));
                if (team == null) {
                    team = player.getScoreboard().registerNewTeam(LegacyClientUtils.teamNames.get(tabEntry.getRawSlot() - 1));
                }
                team.setPrefix(ChatColor.translateAlternateColorCodes('&', newStrings[0]));
                if (newStrings.length > 1) {
                    team.setSuffix(ChatColor.translateAlternateColorCodes('&', newStrings[1]));
                }
                else {
                    team.setSuffix("");
                }
            }
            else {
                GameProfile gameProfile = new GameProfile(tabEntry.getOfflinePlayer().getUniqueId(), tabEntry.getId());
                Object entityPlayer = ReflectionUtil.instantiateObject("EntityPlayer", ReflectionUtil.PackageType.MINECRAFT_SERVER, UniversalTabImpl.nmsServer, UniversalTabImpl.worldServer, gameProfile, UniversalTabImpl.playerInteractManager);
                ReflectionUtil.setValue(entityPlayer, false, "listName", convertComponent(fromText((newStrings.length > 1) ? (newStrings[0] + newStrings[1]) : newStrings[0])));
                Object playerInfo = ReflectionUtil.instantiateObject("PacketPlayOutPlayerInfo", ReflectionUtil.PackageType.MINECRAFT_SERVER, Reflection.getEnum(Reflection.getInnerClass(ReflectionUtil.PackageType.MINECRAFT_SERVER.getClass("PacketPlayOutPlayerInfo"), "EnumPlayerInfoAction"), "UPDATE_DISPLAY_NAME"), Collections.singletonList(entityPlayer));
                sendPacket(player, playerInfo);
            }
            tabEntry.setText(text);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void updateFakeLatency(ZigguratTablist zigguratTablist, TabEntry tabEntry, Integer latency) {
        if (tabEntry.getLatency() == latency) {
            return;
        }
        try {
            GameProfile gameProfile = new GameProfile(tabEntry.getOfflinePlayer().getUniqueId(), tabEntry.getId());
            Object entityPlayer = ReflectionUtil.instantiateObject("EntityPlayer", ReflectionUtil.PackageType.MINECRAFT_SERVER, UniversalTabImpl.nmsServer, UniversalTabImpl.worldServer, gameProfile, UniversalTabImpl.playerInteractManager);
            ReflectionUtil.setValue(entityPlayer, false, "ping", latency);
            Object playerInfo = ReflectionUtil.instantiateObject("PacketPlayOutPlayerInfo", ReflectionUtil.PackageType.MINECRAFT_SERVER, Reflection.getEnum(Reflection.getInnerClass(ReflectionUtil.PackageType.MINECRAFT_SERVER.getClass("PacketPlayOutPlayerInfo"), "EnumPlayerInfoAction"), "UPDATE_LATENCY"), Collections.singletonList(entityPlayer));
            sendPacket(zigguratTablist.getPlayer(), playerInfo);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        tabEntry.setLatency(latency);
    }
    
    @Override
    public void updateFakeSkin(ZigguratTablist zigguratTablist, TabEntry tabEntry, SkinTexture skinTexture) {
        try {
            if (tabEntry.getTexture() == skinTexture) {
                return;
            }
            Player player = zigguratTablist.getPlayer();
            PlayerVersion protocolVersion = PlayerUtil.getPlayerVersion(player);
            GameProfile gameProfile = new GameProfile(tabEntry.getOfflinePlayer().getUniqueId(), (protocolVersion != PlayerVersion.v1_7) ? tabEntry.getId() : (LegacyClientUtils.tabEntrys.get(tabEntry.getRawSlot() - 1) + ""));
            Object entityPlayer = ReflectionUtil.instantiateObject(Reflection.getNMSClass("EntityPlayer"), UniversalTabImpl.nmsServer, UniversalTabImpl.worldServer, gameProfile, UniversalTabImpl.playerInteractManager);
            Object remove = ReflectionUtil.instantiateObject("PacketPlayOutPlayerInfo", ReflectionUtil.PackageType.MINECRAFT_SERVER, Reflection.getEnum(Reflection.getInnerClass(ReflectionUtil.PackageType.MINECRAFT_SERVER.getClass("PacketPlayOutPlayerInfo"), "EnumPlayerInfoAction"), "REMOVE_PLAYER"), Collections.singletonList(entityPlayer));
            Object add = ReflectionUtil.instantiateObject("PacketPlayOutPlayerInfo", ReflectionUtil.PackageType.MINECRAFT_SERVER, Reflection.getEnum(Reflection.getInnerClass(ReflectionUtil.PackageType.MINECRAFT_SERVER.getClass("PacketPlayOutPlayerInfo"), "EnumPlayerInfoAction"), "ADD_PLAYER"), Collections.singletonList(entityPlayer));
            if (protocolVersion != PlayerVersion.v1_7) {
                gameProfile.getProperties().put("texture", new Property("textures", skinTexture.getValue(), skinTexture.getSignature()));
            }
            sendPacket(player, remove);
            sendPacket(player, add);
            tabEntry.setTexture(skinTexture);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void updateHeaderAndFooter(ZigguratTablist zigguratTablist, String header, String footer) {
        Player player = zigguratTablist.getPlayer();
        PlayerVersion protocolVersion = PlayerUtil.getPlayerVersion(player);
        if (protocolVersion != PlayerVersion.v1_7) {
            try {
                Object headerAndFooter = ReflectionUtil.instantiateObject("PacketPlayOutPlayerListHeaderFooter", ReflectionUtil.PackageType.MINECRAFT_SERVER);
                if (Ziggurat.getInstance().getVersion().isAbove(ServerVersion.v1_12)) {
                    ReflectionUtil.setValue(headerAndFooter, true, "header", convertComponent(fromText(header)));
                    ReflectionUtil.setValue(headerAndFooter, true, "footer", convertComponent(fromText(footer)));
                }
                else {
                    ReflectionUtil.setValue(headerAndFooter, true, "a", convertComponent(fromText(header)));
                    ReflectionUtil.setValue(headerAndFooter, true, "b", convertComponent(fromText(footer)));
                }
                sendPacket(player, headerAndFooter);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    static {
        try {
            UniversalTabImpl.nmsServer = ReflectionUtil.invokeMethod(ReflectionUtil.PackageType.CRAFTBUKKIT.getClass("CraftServer").cast(Bukkit.getServer()), "getServer");
            UniversalTabImpl.worldServer = ReflectionUtil.invokeMethod(UniversalTabImpl.nmsServer, "getWorldServer", 0);
            UniversalTabImpl.playerInteractManager = ReflectionUtil.instantiateObject(ReflectionUtil.PackageType.MINECRAFT_SERVER.getClass("PlayerInteractManager"), UniversalTabImpl.worldServer);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
