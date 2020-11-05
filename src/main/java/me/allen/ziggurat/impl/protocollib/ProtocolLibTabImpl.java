package me.allen.ziggurat.impl.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
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
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

public class ProtocolLibTabImpl implements IZigguratHelper
{
    private static void sendPacket(Player player, PacketContainer packetContainer) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer);
        }
        catch (InvocationTargetException var3) {
            var3.printStackTrace();
        }
    }
    
    @Override
    public void removeSelf(Player player) {
    }
    
    @Override
    public TabEntry createFakePlayer(ZigguratTablist zigguratTablist, String string, TabColumn column, Integer slot, Integer rawSlot) {
        OfflinePlayer offlinePlayer = OfflinePlayerUtil.createOfflinePlayer(string);
        Player player = zigguratTablist.getPlayer();
        PlayerVersion playerVersion = PlayerUtil.getPlayerVersion(player);
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        WrappedGameProfile profile = new WrappedGameProfile(offlinePlayer.getUniqueId(), (playerVersion != PlayerVersion.v1_7) ? string : (LegacyClientUtils.tabEntrys.get(rawSlot - 1) + ""));
        PlayerInfoData playerInfoData = new PlayerInfoData(profile, 1, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText((playerVersion != PlayerVersion.v1_7) ? "" : profile.getName()));
        if (playerVersion != PlayerVersion.v1_7) {
            playerInfoData.getProfile().getProperties().put("texture", new WrappedSignedProperty("textures", ZigguratCommons.defaultTexture.getValue(), ZigguratCommons.defaultTexture.getSignature()));
        }
        packet.getPlayerInfoDataLists().write(0,Collections.singletonList(playerInfoData));
        sendPacket(player, packet);
        return new TabEntry(string, offlinePlayer, "", zigguratTablist, ZigguratCommons.defaultTexture, column, slot, rawSlot, 0);
    }
    
    @Override
    public void updateFakeName(ZigguratTablist zigguratTablist, TabEntry tabEntry, String text) {
        Player player = zigguratTablist.getPlayer();
        PlayerVersion playerVersion = PlayerUtil.getPlayerVersion(player);
        String[] newStrings = ZigguratTablist.splitStrings(text, tabEntry.getRawSlot());
        if (playerVersion == PlayerVersion.v1_7) {
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
            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
            packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
            WrappedGameProfile profile = new WrappedGameProfile(tabEntry.getOfflinePlayer().getUniqueId(), tabEntry.getId());
            PlayerInfoData playerInfoData = new PlayerInfoData(profile, 1, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', (newStrings.length > 1) ? (newStrings[0] + newStrings[1]) : newStrings[0])));
            packet.getPlayerInfoDataLists().write(0, Collections.singletonList(playerInfoData));
            sendPacket(player, packet);
        }
        tabEntry.setText(text);
    }
    
    @Override
    public void updateFakeLatency(ZigguratTablist zigguratTablist, TabEntry tabEntry, Integer latency) {
        if (tabEntry.getLatency() != latency) {
            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
            packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.UPDATE_LATENCY);
            WrappedGameProfile profile = new WrappedGameProfile(tabEntry.getOfflinePlayer().getUniqueId(), tabEntry.getId());
            PlayerInfoData playerInfoData = new PlayerInfoData(profile, latency, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', tabEntry.getText())));
            packet.getPlayerInfoDataLists().write(0, Collections.singletonList(playerInfoData));
            sendPacket(zigguratTablist.getPlayer(), packet);
            tabEntry.setLatency(latency);
        }
    }
    
    @Override
    public void updateFakeSkin(ZigguratTablist zigguratTablist, TabEntry tabEntry, SkinTexture skinTexture) {
        if (tabEntry.getTexture() != skinTexture) {
            Player player = zigguratTablist.getPlayer();
            PlayerVersion playerVersion = PlayerUtil.getPlayerVersion(player);
            WrappedGameProfile profile = new WrappedGameProfile(tabEntry.getOfflinePlayer().getUniqueId(), (playerVersion != PlayerVersion.v1_7) ? tabEntry.getId() : (LegacyClientUtils.tabEntrys.get(tabEntry.getRawSlot() - 1) + ""));
            PlayerInfoData playerInfoData = new PlayerInfoData(profile, 1, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText((playerVersion != PlayerVersion.v1_7) ? "" : profile.getName()));
            if (playerVersion != PlayerVersion.v1_7) {
                playerInfoData.getProfile().getProperties().put("texture", new WrappedSignedProperty("textures", skinTexture.getValue(), skinTexture.getSignature()));
            }
            PacketContainer remove = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
            remove.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            remove.getPlayerInfoDataLists().write(0, Collections.singletonList(playerInfoData));
            PacketContainer add = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
            add.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
            add.getPlayerInfoDataLists().write(0, Collections.singletonList(playerInfoData));
            sendPacket(player, remove);
            sendPacket(player, add);
            tabEntry.setTexture(skinTexture);
        }
    }
    
    @Override
    public void updateHeaderAndFooter(ZigguratTablist zigguratTablist, String header, String footer) {
        PacketContainer headerAndFooter = new PacketContainer(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
        Player player = zigguratTablist.getPlayer();
        PlayerVersion playerVersion = PlayerUtil.getPlayerVersion(player);
        if (playerVersion != PlayerVersion.v1_7) {
            headerAndFooter.getChatComponents().write(0, WrappedChatComponent.fromText(header));
            headerAndFooter.getChatComponents().write(1, WrappedChatComponent.fromText(footer));
            sendPacket(player, headerAndFooter);
        }
    }
}
