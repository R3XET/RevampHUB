package me.allen.ziggurat.util.reflection;

import com.google.common.primitives.Primitives;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import me.allen.ziggurat.util.version.ServerVersion;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import sun.reflect.ReflectionFactory;

public class Reflection {
    public static final ReflectionFactory rf = ReflectionFactory.getReflectionFactory();

    private static final Map<Class, Field[]> allFieldCache = Collections.synchronizedMap(new WeakHashMap<>());

    private static final ConcurrentHashMap<String, String> nmsRenames = new ConcurrentHashMap<>();

    public static ServerVersion ver = ServerVersion.UNKNOWN;

    public static String version;

    public static boolean classArrayCompare(Class[] l1, Class[] l2) {
        if (l1.length != l2.length)
            return false;
        for (int i = 0; i < l1.length; i++) {
            if (l1[i] != l2[i])
                return false;
        }
        return true;
    }

    public static boolean classArrayCompareLight(Class[] l1, Class[] l2) {
        if (l1.length != l2.length)
            return false;
        for (int i = 0; i < l1.length; i++) {
            if (!Primitives.wrap(l2[i]).isAssignableFrom(Primitives.wrap(l1[i])))
                return false;
        }
        return true;
    }

    public static <T> T convert(Object in, Class<T> to) {
        if (in == null)
            return null;
        to = Primitives.wrap(to);
        String inS = in.getClass().isEnum() ? ((Enum)in).name() : in.toString();
        try {
            Constructor<T> con = to.getConstructor(String.class);
            con.setAccessible(true);
            return con.newInstance(inS);
        } catch (Throwable throwable) {
            try {
                Method m = to.getMethod("valueOf", String.class);
                m.setAccessible(true);
                return (T)m.invoke(null, new Object[] { inS });
            } catch (Throwable throwable1) {
                try {
                    Method m = to.getMethod("fromString", String.class);
                    m.setAccessible(true);
                    return (T)m.invoke(null, new Object[] { inS });
                } catch (Throwable throwable2) {
                    return null;
                }
            }
        }
    }

    public static Field[] getAllFields(Class c) {
        Field[] fs = allFieldCache.get(c);
        if (fs != null)
            return fs;
        ArrayList<Field> out = new ArrayList<>();
        while (c != null) {
            for (Field f : c.getDeclaredFields())
                out.add(setFieldAccessible(f));
            c = c.getSuperclass();
        }
        Field[] oa = new Field[out.size()];
        out.toArray(oa);
        allFieldCache.put(c, oa);
        return oa;
    }

    public static Class getClass(String className) {
        try {
            String[] classNames = className.split("\\$");
            Class<?> c = Class.forName(classNames[0]);
            for (int i = 1; i < classNames.length; i++)
                c = getInnerClass(c, classNames[i]);
            return c;
        } catch (Throwable throwable) {
            return null;
        }
    }

    public static Constructor getConstructor(Class cl, Class... classes) {
        try {
            Constructor c = cl.getDeclaredConstructor(classes);
            c.setAccessible(true);
            return c;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getData(Object obj, List<Object> data) {
        try {
            Class<?> ocl = obj.getClass();
            Object[] input = ArrayUtils.EMPTY_OBJECT_ARRAY;
            Class[] classes = ArrayUtils.EMPTY_CLASS_ARRAY;
            for (Object o : data) {
                Class<?> cl = o.getClass();
                if (cl.isArray()) {
                    input = (Object[])o;
                    classes = new Class[input.length];
                    for (int i = 0; i < input.length; i++)
                        classes[i] = input[i].getClass();
                    continue;
                }
                for (String name : String.valueOf(o).split("\\.")) {
                    if (input.length == 0) {
                        Field f = getField(ocl, name);
                        if (f != null) {
                            obj = f.get(obj);
                            ocl = obj.getClass();
                            continue;
                        }
                    }
                    Method m = getSimiliarMethod(ocl, name, classes);
                    Class[] parCls = m.getParameterTypes();
                    Object[] pars = new Object[parCls.length];
                    for (int i = 0; i < parCls.length; i++)
                        pars[i] = convert(input[i], parCls[i]);
                    obj = m.invoke(obj, pars);
                    if (obj == null)
                        throw new RuntimeException("Null return value of method call (method " + m.getName() + ", entered parameters: " + StringUtils.join(pars, ", ") + ".");
                    ocl = obj.getClass();
                    input = ArrayUtils.EMPTY_OBJECT_ARRAY;
                    classes = ArrayUtils.EMPTY_CLASS_ARRAY;
                    continue;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
        return obj;
    }

    public static Object getEnum(Class enumType, String value) {
        try {
            return enumType.getMethod("valueOf", new Class[] { String.class }).invoke(null, value);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Field getField(Class clazz, String name) {
        try {
            return setFieldAccessible(clazz.getDeclaredField(name));
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getFieldData(Class clazz, String name) {
        return getFieldData(clazz, name, null);
    }

    public static Object getFieldData(Class clazz, String name, Object object) {
        try {
            return setFieldAccessible(clazz.getDeclaredField(name)).get(object);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Field getFirstFieldOfType(Class clazz, Class type) {
        try {
            for (Field f : clazz.getDeclaredFields()) {
                if (f.getType().equals(type))
                    return setFieldAccessible(f);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class getInnerClass(Class cl, String name) {
        try {
            name = cl.getName() + "$" + name;
            for (Class<?> c : cl.getDeclaredClasses()) {
                if (c.getName().equals(name))
                    return c;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Field getLastFieldOfType(Class clazz, Class type) {
        Field field = null;
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getType().equals(type))
                field = f;
        }
        return setFieldAccessible(field);
    }

    public static Method getMethod(Class cl, String name, Class... args) {
        if (cl == null || name == null || ArrayUtils.contains(args, null))
            return null;
        String originalClassName = cl.getName();
        if (args.length == 0) {
            while (cl != null) {
                Method m = methodCheckNoArg(cl, name);
                if (m != null) {
                    m.setAccessible(true);
                    return m;
                }
                cl = cl.getSuperclass();
            }
        } else {
            while (cl != null) {
                Method m = methodCheck(cl, name, args);
                if (m != null) {
                    m.setAccessible(true);
                    return m;
                }
                cl = cl.getSuperclass();
            }
            StringBuilder sb = new StringBuilder();
            for (Class c : args)
                sb.append(", ").append(c.getName());
        }
        return null;
    }

    public static Class getNMSClass(String className) {
        String newName = nmsRenames.get(className);
        if (newName != null) {
            className = newName;
            if (className.contains("."))
                return getClass(className);
        }
        return getClass("net.minecraft.server." + version + className);
    }

    public static Class getOBCClass(String className) {
        return getClass("org.bukkit.craftbukkit." + version + className);
    }

    public static Method getSimiliarMethod(Class ocl, String name, Class[] classes) {
        Method m = getMethod(ocl, name, classes);
        if (m == null) {
            m = getMethod(ocl, "get" + name, classes);
            if (m == null)
                m = getMethod(ocl, "is" + name, classes);
        }
        if (m != null)
            return m;
        name = name.toLowerCase();
        Class origCl = ocl;
        while (ocl != null) {
            for (Method m2 : ocl.getDeclaredMethods()) {
                if ((m2.getParameterTypes()).length == classes.length) {
                    String mn = m2.getName().toLowerCase();
                    if (mn.endsWith(name) && (mn.startsWith(name) || mn.startsWith("get") || mn.startsWith("is")))
                        return m2;
                }
            }
            ocl = ocl.getSuperclass();
        }
        return null;
    }

    public static Class getUtilClass(String className) {
        if (ver.isAbove(ServerVersion.v1_8) || ver.equals(ServerVersion.v1_8))
            return getClass(className);
        return getClass("net.minecraft.util." + className);
    }

    public static void init() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        version = name.substring(name.lastIndexOf('.') + 1);
        try {
            ver = ServerVersion.valueOf(version.substring(0, version.length() - 3));
        } catch (Throwable throwable) {}
        if (ver == ServerVersion.v1_7) {
            nmsRenames.put("PacketLoginOutSetCompression", "org.spigotmc.ProtocolInjector$PacketLoginCompression");
            nmsRenames.put("PacketPlayOutTitle", "org.spigotmc.ProtocolInjector$PacketTitle");
            nmsRenames.put("PacketPlayOutPlayerListHeaderFooter", "org.spigotmc.ProtocolInjector$PacketTabHeader");
            nmsRenames.put("PacketPlayOutResourcePackSend", "org.spigotmc.ProtocolInjector$PacketPlayResourcePackSend");
            nmsRenames.put("PacketPlayInResourcePackStatus", "org.spigotmc.ProtocolInjector$PacketPlayResourcePackStatus");
        }
        if (!version.equals("v1_8_R1")) {
            nmsRenames.put("PacketPlayInLook", "PacketPlayInFlying$PacketPlayInLook");
            nmsRenames.put("PacketPlayInPosition", "PacketPlayInFlying$PacketPlayInPosition");
            nmsRenames.put("PacketPlayInPositionLook", "PacketPlayInFlying$PacketPlayInPositionLook");
            nmsRenames.put("PacketPlayInRelPositionLook", "PacketPlayInFlying$PacketPlayInPositionLook");
            nmsRenames.put("PacketPlayOutEntityLook", "PacketPlayOutEntity$PacketPlayOutEntityLook");
            nmsRenames.put("PacketPlayOutRelEntityMove", "PacketPlayOutEntity$PacketPlayOutRelEntityMove");
            nmsRenames.put("PacketPlayOutRelEntityMoveLook", "PacketPlayOutEntity$PacketPlayOutRelEntityMoveLook");
        }
        if (ver.isAbove(ServerVersion.v1_9))
            nmsRenames.put("WorldSettings$EnumGamemode", "EnumGamemode");
        version += ".";
    }

    private static Method methodCheck(Class cl, String name, Class[] args) {
        try {
            return cl.getDeclaredMethod(name, args);
        } catch (Throwable e) {
            Method[] mtds = cl.getDeclaredMethods();
            for (Method met : mtds) {
                if (classArrayCompare(args, met.getParameterTypes()) && met.getName().equals(name))
                    return met;
            }
            for (Method met : mtds) {
                if (classArrayCompareLight(args, met.getParameterTypes()) && met.getName().equals(name))
                    return met;
            }
            for (Method met : mtds) {
                if (classArrayCompare(args, met.getParameterTypes()) && met.getName().equalsIgnoreCase(name))
                    return met;
            }
            for (Method met : mtds) {
                if (classArrayCompareLight(args, met.getParameterTypes()) && met.getName().equalsIgnoreCase(name))
                    return met;
            }
            return null;
        }
    }

    private static Method methodCheckNoArg(Class cl, String name) {
        try {
            return cl.getDeclaredMethod(name);
        } catch (Throwable e) {
            Method[] mtds = cl.getDeclaredMethods();
            for (Method met : mtds) {
                if ((met.getParameterTypes()).length == 0 && met.getName().equalsIgnoreCase(name))
                    return met;
            }
            return null;
        }
    }

    public static Object newInstance(Class cl, Class[] classes, Object... objs) {
        try {
            Constructor c = cl.getDeclaredConstructor(classes);
            c.setAccessible(true);
            return c.newInstance(objs);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object newInstance(Class<?> cl) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        try {
            return cl.newInstance();
        } catch (Throwable err) {
            return rf.newConstructorForSerialization(cl, Object.class.getDeclaredConstructor()).newInstance();
        }
    }

    public static Field setFieldAccessible(Field f) {
        try {
            f.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            int modifiers = modifiersField.getInt(f);
            modifiersField.setInt(f, modifiers & 0xFFFFFFEF);
            return f;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
