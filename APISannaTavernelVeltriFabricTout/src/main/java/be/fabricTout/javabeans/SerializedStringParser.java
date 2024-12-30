package be.fabricTout.javabeans;

import org.json.JSONObject;


public class SerializedStringParser {

    /**
     * Parse une chaîne sérialisée Java de type "ClassName{key=value, key=value}" en un JSONObject.
     *
     * @param serializedString La chaîne sérialisée Java à parser.
     * @return Un objet JSONObject contenant les clés et valeurs extraites.
     */
    public static JSONObject parseJavaSerializedString(String serializedString) {
        JSONObject json = new JSONObject();

        try {
            // Supprimer le nom de la classe et les accolades externes
            int startIndex = serializedString.indexOf('{');
            int endIndex = serializedString.lastIndexOf('}');
            if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
                throw new IllegalArgumentException("Format incorrect: " + serializedString);
            }
            String content = serializedString.substring(startIndex + 1, endIndex);

            // Séparer les paires clé-valeur
            String[] parts = content.split(",(?![^{}]*\\})"); // Gère les virgules internes aux objets JSON
            for (String part : parts) {
                String[] keyValue = part.split("=", 2); // Séparer clé et valeur
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();

                    // Vérifier si la valeur est un autre objet sérialisé ou une valeur simple
                    if (value.startsWith("{") && value.endsWith("}")) {
                        json.put(key, parseJavaSerializedString(key + value)); // Désérialisation récursive
                    } else {
                        // Supprimer les guillemets externes pour les chaînes simples
                        value = value.replaceAll("^'|'$", "");
                        json.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing de la chaîne: " + serializedString);
            e.printStackTrace();
        }

        return json;
    }

    // Méthode de test
    public static void main(String[] args) {
        String serializedString = "Manager{maintenances=[], registrationCode='MGR02', password='GDPBDKAB=', person={idPerson=2, firstName='James', lastName='Smith', phoneNumber='222-222-2222'}}";

        JSONObject json = parseJavaSerializedString(serializedString);
        System.out.println("JSON obtenu : " + json.toString(4));
    }
}
