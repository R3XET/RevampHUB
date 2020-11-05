package me.allen.ziggurat.impl.v1_7;

import me.allen.ziggurat.ZigguratTablist;
import me.allen.ziggurat.objects.IZigguratHelper;
import me.allen.ziggurat.objects.SkinTexture;
import me.allen.ziggurat.objects.TabColumn;
import me.allen.ziggurat.objects.TabEntry;
import me.allen.ziggurat.util.ZigguratCommons;
import me.allen.ziggurat.util.player.LegacyClientUtils;
import me.allen.ziggurat.util.player.OfflinePlayerUtil;
import me.allen.ziggurat.util.player.PlayerUtil;
import me.allen.ziggurat.util.version.PlayerVersion;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.spigotmc.ProtocolInjector;

public class v1_7TabImpl implements IZigguratHelper
{
    private static MinecraftServer server = MinecraftServer.getServer();
    private static WorldServer world = v1_7TabImpl.server.getWorldServer(0);
    private static PlayerInteractManager manager = new PlayerInteractManager(v1_7TabImpl.world);
    
    @Override
    public void removeSelf(Player player) {
        Bukkit.getOnlinePlayers().forEach(online -> this.sendPacket(player, PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer)online).getHandle())));
    }
    
    @Override
    public TabEntry createFakePlayer(ZigguratTablist zigguratTablist, String string, TabColumn column, Integer slot, Integer rawSlot) {
        OfflinePlayer offlinePlayer = OfflinePlayerUtil.createOfflinePlayer(string);
        Player player = zigguratTablist.getPlayer();
        PlayerVersion protocolVersion = PlayerUtil.getPlayerVersion(player);
        GameProfile profile = new GameProfile(offlinePlayer.getUniqueId(), LegacyClientUtils.tabEntrys.get(rawSlot - 1) + "");
        EntityPlayer entity = new EntityPlayer(v1_7TabImpl.server, v1_7TabImpl.world, profile, v1_7TabImpl.manager);
        if (protocolVersion != PlayerVersion.v1_7) {
            profile.getProperties().put("textures", new Property("textures", ZigguratCommons.defaultTexture.getValue(), ZigguratCommons.defaultTexture.getSignature()));
        }
        entity.ping = 1;
        Packet packet = PacketPlayOutPlayerInfo.addPlayer(entity);
        this.sendPacket(zigguratTablist.getPlayer(), packet);
        return new TabEntry(string, offlinePlayer, "", zigguratTablist, ZigguratCommons.defaultTexture, column, slot, rawSlot, 0);
    }
    
    @Override
    public void updateFakeName(ZigguratTablist zigguratTablist, TabEntry tabEntry, String text) {
        if (tabEntry.getText().equals(text)) {
            return;
        }
        Player player = zigguratTablist.getPlayer();
        String[] newStrings = ZigguratTablist.splitStrings(text, tabEntry.getRawSlot());
        Team team = player.getScoreboard().getTeam(LegacyClientUtils.teamNames.get(tabEntry.getRawSlot() - 1));
        team.setPrefix(ChatColor.translateAlternateColorCodes('&', newStrings[0]));
        if (newStrings.length > 1) {
            team.setSuffix(ChatColor.translateAlternateColorCodes('&', newStrings[1]));
        }
        else {
            team.setSuffix("");
        }
        tabEntry.setText(text);
    }
    
    @Override
    public void updateFakeLatency(ZigguratTablist zigguratTablist, TabEntry tabEntry, Integer latency) {
        if (tabEntry.getLatency() == latency) {
            return;
        }
        GameProfile profile = new GameProfile(tabEntry.getOfflinePlayer().getUniqueId(), LegacyClientUtils.tabEntrys.get(tabEntry.getRawSlot() - 1) + "");
        EntityPlayer entity = new EntityPlayer(v1_7TabImpl.server, v1_7TabImpl.world, profile, v1_7TabImpl.manager);
        entity.ping = latency;
        Packet packet = PacketPlayOutPlayerInfo.updatePing(entity);
        this.sendPacket(zigguratTablist.getPlayer(), packet);
        tabEntry.setLatency(latency);
    }
    
    @Override
    public void updateFakeSkin(ZigguratTablist zigguratTablist, TabEntry tabEntry, SkinTexture skinTexture) {
        if (tabEntry.getTexture() == skinTexture) {
            return;
        }
        GameProfile profile = new GameProfile(tabEntry.getOfflinePlayer().getUniqueId(), LegacyClientUtils.tabEntrys.get(tabEntry.getRawSlot() - 1) + "");
        EntityPlayer entity = new EntityPlayer(v1_7TabImpl.server, v1_7TabImpl.world, profile, v1_7TabImpl.manager);
        profile.getProperties().put("textures", new Property("textures", skinTexture.getValue(), skinTexture.getSignature()));
        Packet removePlayer = PacketPlayOutPlayerInfo.removePlayer(entity);
        this.sendPacket(zigguratTablist.getPlayer(), removePlayer);
        Packet addPlayer = PacketPlayOutPlayerInfo.addPlayer(entity);
        this.sendPacket(zigguratTablist.getPlayer(), addPlayer);
        tabEntry.setTexture(skinTexture);
    }
    
    @Override
    public void updateHeaderAndFooter(ZigguratTablist zigguratTablist, String header, String footer) {
        PlayerVersion protocolVersion = PlayerUtil.getPlayerVersion(zigguratTablist.getPlayer());
        if (protocolVersion != PlayerVersion.v1_7) {
            this.sendPacket(zigguratTablist.getPlayer(), new ProtocolInjector.PacketTabHeader(ChatSerializer.a(ChatColor.translateAlternateColorCodes('&', fromText(header))), ChatSerializer.a(ChatColor.translateAlternateColorCodes('&', fromText(footer)))));
        }
    }
    
    private void sendPacket(Player player, Packet packet) {
        this.getEntity(player).playerConnection.sendPacket(packet);
    }
    
    private EntityPlayer getEntity(Player player) {
        return ((CraftPlayer)player).getHandle();
    }
    
    private static String fromText(String text) {
        return ComponentSerializer.toString(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text)));
    }
}
