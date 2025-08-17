
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;

public class ConversorApp {
    public static void main(String[] args) throws IOException, InterruptedException  {
        Conversor.eleccionUserMenu();
    }
    public static double obtenerTasa(String urlFinal) throws IOException, InterruptedException {
        //Código omitido
        //Conversión a JSON
        JsonElement elemento = JsonParser.parseString(respuesta.body());
        JsonObject objectRoot = elemento.getAsJsonObject();

        //Accediendo a JsonObject
        double tasa = objectRoot.get("conversion_rate").getAsDouble();
        return tasa;
    }
}