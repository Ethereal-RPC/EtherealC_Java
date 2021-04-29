package Model;
import Utils.Utils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Objects;

public class RPCTypeConfig {
    public interface IConvert {
        Object convert(Object obj);
    }
    private HashMap<Type, RPCType> typesByType = new HashMap<>();
    private HashMap<String, RPCType> typesByName = new HashMap<>();

    public HashMap<Type, RPCType> getTypesByType() {
        return typesByType;
    }

    public void setTypesByType(HashMap<Type, RPCType> typesByType) {
        this.typesByType = typesByType;
    }

    public HashMap<String, RPCType> getTypesByName() {
        return typesByName;
    }

    public void setTypesByName(HashMap<String, RPCType> typesByName) {
        this.typesByName = typesByName;
    }

    public RPCTypeConfig(){

    }
    public void add(Type type, String abstractName) throws RPCException {
        if (typesByName.containsKey(abstractName) || typesByType.containsKey(type)) throw new RPCException(String.format("类型:{%s}转{%s}发生异常,存在重复键",type, abstractName));
        else{
            RPCType rpcType = new RPCType();
            rpcType.setName(abstractName);
            rpcType.setType(type);
            rpcType.setDeserialize(obj -> Utils.gson.fromJson(obj,type));
            rpcType.setSerialize(obj -> Utils.gson.toJson(rpcType,type));
            this.typesByType.put(type, rpcType);
            this.typesByName.put(abstractName,rpcType);
        }
    }
    public void add(Type type, String abstractName, RPCType.ISerialize serialize, RPCType.IDeserialize deserialize) throws RPCException {
        if (typesByName.containsKey(abstractName) || typesByType.containsKey(type)) throw new RPCException(String.format("类型:{%s}转{%s}发生异常,存在重复键",type, abstractName));
        else{
            RPCType rpcType = new RPCType();
            rpcType.setName(abstractName);
            rpcType.setType(type);
            rpcType.setSerialize(serialize);
            rpcType.setDeserialize(deserialize);
            this.typesByType.put(type, rpcType);
            this.typesByName.put(abstractName,rpcType);
        }
    }
}
