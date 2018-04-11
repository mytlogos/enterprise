package scrape.sources.novel.toc.structure.jsonToc;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Copied from http://technology.finra.org/code/serialize-deserialize-interfaces-in-java.html
 */
public class JsonDeserializerAdapter<E> implements JsonDeserializer<E>, JsonSerializer<E> {
    private static final String CLASSNAME = "CLASSNAME";
    private static final String DATA = "DATA";

    @Override
    public E deserialize(JsonElement jsonElement, Type type,
                         JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
        String className = prim.getAsString();
        Class<? extends E> klass = getObjectClass(className);
        return jsonDeserializationContext.deserialize(jsonObject.get(DATA), klass);
    }

    /****** Helper method to getAll the className of the object to be deserialized *****/
    private Class getObjectClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
            throw new JsonParseException(e.getMessage());
        }
    }

    @Override
    public JsonElement serialize(E jsonElement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(CLASSNAME, jsonElement.getClass().getName());
        jsonObject.add(DATA, jsonSerializationContext.serialize(jsonElement));
        return jsonObject;
    }
}

