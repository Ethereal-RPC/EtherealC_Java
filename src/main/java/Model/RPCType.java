package Model;
import Utils.Utils;

import java.lang.reflect.Type;
import java.util.HashMap;

public class RPCType {
    public interface IConvert {
        Object convert(Object obj);
    }
    private HashMap<Type, String> abstractName = new HashMap<>();
    private HashMap<String, Type> abstractType = new HashMap<>();
    private HashMap<String, IConvert> convert = new HashMap<>();

    public HashMap<Type, String> getAbstractName() {
        return abstractName;
    }

    public HashMap<String, Type> getAbstractType() {
        return abstractType;
    }

    public RPCType(){

    }

    public void setAbstractType(HashMap<String, Type> abstractType) {
        this.abstractType = abstractType;
    }

    public void setAbstractName(HashMap<Type, String> abstractName) {
        this.abstractName = abstractName;
    }

    public HashMap<String, IConvert> getConvert() {
        return convert;
    }

    public void setConvert(HashMap<String, IConvert> convert) {
        this.convert = convert;
    }

    public void add(Type type, String abstractName) throws RPCException {
        if (convert.containsKey(abstractName) || this.abstractName.containsKey(type)) throw new RPCException(String.format("类型:{%s}转{%s}发生异常,存在重复键",type, abstractName));
        else{
            this.abstractName.put(type, abstractName);
            this.abstractType.put(abstractName,type);
            convert.put(abstractName, obj -> obj = Utils.gson.fromJson((String)obj,type));
        }
    }
    public void add(Type type, String abstractName, IConvert convert) throws RPCException {
        if (this.convert.containsKey(abstractName) || this.abstractName.containsKey(type)) throw new RPCException(String.format("类型:{%s}转{%s+}发生异常,存在重复键",type, abstractName));
        else{
            this.abstractName.put(type, abstractName);
            this.abstractType.put(abstractName,type);
            this.convert.put(abstractName,convert);
        }
    }
}
