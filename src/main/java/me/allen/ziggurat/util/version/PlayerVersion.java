package me.allen.ziggurat.util.version;

import lombok.Getter;

import java.util.Arrays;

public enum PlayerVersion
{
    v1_7(new Integer[] { 4, 5 }), 
    v1_8(new Integer[] { 47 }), 
    v1_9(new Integer[] { 107, 108, 109, 110 }), 
    v1_10(new Integer[] { 210 }), 
    v1_11(new Integer[] { 315, 316 }), 
    v1_12(new Integer[] { 335, 338, 340 }), 
    v1_13(new Integer[] { 393, 401, 404 }), 
    v1_14(new Integer[] { 393, 401, 404 }), 
    v1_15(new Integer[] { 393, 401, 404 });
    
    @Getter
    private Integer[] rawVersion;
    
    PlayerVersion(Integer[] rawVersionNumbers) {
        this.rawVersion = rawVersionNumbers;
    }
    
    public static PlayerVersion getVersionFromRaw(Integer input) {
        for (PlayerVersion playerVersion : values()) {
            if (Arrays.asList(playerVersion.rawVersion).contains(input)) {
                return playerVersion;
            }
        }
        return PlayerVersion.v1_8;
    }
}
