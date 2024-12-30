package be.fabricTout.javabeans;

import org.json.JSONObject;

public class SerializedStringParser {

    public static JSONObject parseJavaSerializedString(String serializedString) {
        JSONObject json = new JSONObject();

        try {
            serializedString = serializedString.trim();

            if (serializedString.startsWith("{") && serializedString.endsWith("}")) {
                return new JSONObject(serializedString); 
            }

            int startIndex = serializedString.indexOf('{');
            int endIndex = serializedString.lastIndexOf('}');
            if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
                throw new IllegalArgumentException("Format incorrect: " + serializedString);
            }
            String content = serializedString.substring(startIndex + 1, endIndex);

            String[] parts = content.split(",(?![^{}]*\\})"); 
            for (String part : parts) {
                String[] keyValue = part.split("=", 2); 
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();

                    if (value.startsWith("{") && value.endsWith("}")) {
                        json.put(key, parseJavaSerializedString(value));
                    } else {
                        value = value.replaceAll("^'|'$", "").replaceAll("\"", "");
                        json.put(key, value);
                    }
                }
            }

            System.out.println("JSON obtenu : " + json.toString(4));
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing de la chaîne: " + serializedString);
            e.printStackTrace();
        }

        return json;
    }
}
