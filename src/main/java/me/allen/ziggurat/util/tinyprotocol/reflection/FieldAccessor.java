package me.allen.ziggurat.util.tinyprotocol.reflection;

public interface FieldAccessor<T>
{
    T get(Object p0);
    
    void set(Object p0, Object p1);
    
    boolean hasField(Object p0);
}
