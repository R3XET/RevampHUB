package me.allen.ziggurat.util.tinyprotocol.api;

import lombok.Getter;
import me.allen.ziggurat.util.tinyprotocol.reflection.Reflection;
import org.bukkit.Bukkit;

import java.beans.ConstructorProperties;

public enum ProtocolVersion
{
    V1_7(4, "v1_7_R3"), 
    V1_7_10(5, "v1_7_R4"), 
    V1_8(-1, "v1_8_R1"), 
    V1_8_5(-1, "v1_8_R2"), 
    V1_8_9(47, "v1_8_R3"), 
    V1_9(107, "v1_9_R1"), 
    V1_9_1(108, null),
    V1_9_2(109, "v1_9_R2"), 
    V1_9_4(110, "v1_9_R3"), 
    V1_10(-1, "v1_10_R1"), 
    V1_10_2(210, "v1_10_R2"), 
    V1_11(316, "v1_11_R1"), 
    V1_12(335, "v1_12_R1"), 
    V1_12_1(338, null),
    V1_12_2(340, "v1_12_R2"), 
    V1_13(393, "v1_13_R1"), 
    V1_13_1(401, "v1_13_R2"), 
    V1_13_2(404, "v1_13_R3"), 
    V1_14(406, "v1_14"), 
    V1_15(407, "v1_15"), 
    UNKNOWN(-1, "UNKNOWN");
    
    @Getter
    private static ProtocolVersion gameVersion = fetchGameVersion();
    @Getter
    private int version;
    @Getter
    private String serverVersion;
    
    private static ProtocolVersion fetchGameVersion() {
        if (fetchGameVersion2() != ProtocolVersion.UNKNOWN) {
            return fetchGameVersion2();
        }
        for (ProtocolVersion version : values()) {
            if (version.getServerVersion() != null && version.getServerVersion().equals(Reflection.VERSION)) {
                return version;
            }
        }
        return ProtocolVersion.UNKNOWN;
    }
    
    private static ProtocolVersion fetchGameVersion2() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String currentVersion = name.substring(name.lastIndexOf(46) + 1);
        for (ProtocolVersion version : values()) {
            if (version.getServerVersion() != null && currentVersion.toLowerCase().contains(version.getServerVersion().toLowerCase())) {
                return version;
            }
        }
        return ProtocolVersion.UNKNOWN;
    }
    
    public static ProtocolVersion getVersion(int versionId) {
        for (ProtocolVersion version : values()) {
            if (version.getVersion() == versionId) {
                return version;
            }
        }
        return ProtocolVersion.UNKNOWN;
    }
    
    public boolean isBelow(ProtocolVersion version) {
        return this.getVersion() < version.getVersion();
    }
    
    public boolean isAbove(ProtocolVersion version) {
        return this.getVersion() > version.getVersion();
    }
    
    public boolean isOrAbove(ProtocolVersion version) {
        return this.getVersion() >= version.getVersion();
    }
    @ConstructorProperties({ "version", "serverVersion" })
    ProtocolVersion(int version, String serverVersion) {
        this.version = version;
        this.serverVersion = serverVersion;
    }
}
