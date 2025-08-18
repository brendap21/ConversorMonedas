
import com.google.gson.Gson;
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

        TasaResponse tasa = new Gson().fromJson(body, TasaResponse.class);
        if (tasa == null || !"success".equals(tasa.getResult())) {
            System.out.println("Error en la respuesta: " + (tasa != null ? tasa.getResult() : "N/A"));
            return 0.0;
        }
        return tasa.getConversion_rate();
    }
}
