package me.allen.ziggurat.util.version;

import lombok.Getter;

import java.beans.ConstructorProperties;

public enum ServerVersion
{
    UNKNOWN(0), 
    v1_7(1), 
    v1_8(2), 
    v1_9(3), 
    v1_10(4), 
    v1_11(5), 
    v1_12(6), 
    v1_13(7), 
    v1_14(8), 
    v1_15(9);
    
    @Getter
    private int specification;
    
    public boolean isAbove(ServerVersion version) {
        return this.specification > version.getSpecification();
    }
    
    public boolean isBelow(ServerVersion version) {
        return this.specification < version.getSpecification();
    }
    
    @ConstructorProperties({ "specification" })
    ServerVersion(int specification) {
        this.specification = specification;
    }
    
}
