package me.allen.ziggurat.objects;

import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;
import java.util.Objects;

@Getter
@Setter
public class SkinTexture
{
    private String value;
    private String signature;
    
    @ConstructorProperties({ "value", "signature" })
    public SkinTexture(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }
    
    @Override
    public String toString() {
        return "SkinTexture(value=" + this.getValue() + ", signature=" + this.getSignature() + ")";
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SkinTexture)) {
            return false;
        }
        SkinTexture other = (SkinTexture)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$value = this.getValue();
        String other$value = other.getValue();
        if (!Objects.equals(this$value, other$value)) {
            return false;
        }
        String this$signature = this.getSignature();
        String other$signature = other.getSignature();
        if (this$signature == null) {
            return other$signature == null;
        }
        return this$signature.equals(other$signature);
    }

    
    protected boolean canEqual(Object other) {
        return other instanceof SkinTexture;
    }
    
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $value = this.getValue();
        result = result * 59 + (($value == null) ? 43 : $value.hashCode());
        Object $signature = this.getSignature();
        result = result * 59 + (($signature == null) ? 43 : $signature.hashCode());
        return result;
    }
}
