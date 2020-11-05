package me.allen.ziggurat.objects.builder;

import lombok.ToString;
import me.allen.ziggurat.objects.BufferedTabObject;

import java.util.HashSet;
import java.util.Set;

@ToString
public class TabObjectBuilder
{
    private Set<BufferedTabObject> objects;
    
    public TabObjectBuilder() {
        this.objects = new HashSet<>();
    }
    
    public static TabObjectBuilder getInstance() {
        return new TabObjectBuilder();
    }
    
    public TabObjectBuilder append(BufferedTabObject bufferedTabObject) {
        this.objects.add(bufferedTabObject);
        return this;
    }
    
    public Set<BufferedTabObject> build() {
        return this.objects;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TabObjectBuilder)) {
            return false;
        }
        TabObjectBuilder other = (TabObjectBuilder)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Object this$objects = this.objects;
        Object other$objects = other.objects;
        if (this$objects == null) {
            return other$objects == null;
        }
        else return this$objects.equals(other$objects);
    }
    
    protected boolean canEqual(Object other) {
        return other instanceof TabObjectBuilder;
    }
    
    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $objects = this.objects;
        result = result * 59 + (($objects == null) ? 43 : $objects.hashCode());
        return result;
    }
}
