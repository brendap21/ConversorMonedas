
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConversorApp {
    public static void main(String[] args) throws IOException, InterruptedException  {
        Conversor.eleccionUserMenu();
    }
    public static double obtenerTasa(String urlFinal) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlFinal))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> respuesta = client.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = respuesta.statusCode();
        if (statusCode != 200) {
            System.out.println("Error en la solicitud. Código de estado: " + statusCode);
            return 0.0;
        }

        var headers = respuesta.headers();
        System.out.println("date: " + headers.firstValue("date").orElse("N/A"));
        System.out.println("content-type: " + headers.firstValue("content-type").orElse("N/A"));

        String body = respuesta.body();

        //Conversión a JSON
        JsonElement elemento = JsonParser.parseString(body);
        JsonObject objectRoot = elemento.getAsJsonObject();

        //Accediendo a JsonObject
        double tasa = objectRoot.get("conversion_rate").getAsDouble();
        return tasa;
    }
}
