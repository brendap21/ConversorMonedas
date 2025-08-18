import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public class SmokeTest {

    private static String resolveApiKey() {
        String k = System.getenv("EXCHANGE_API_KEY");
        if (k != null && !k.isBlank()) return k;

        k = System.getProperty("EXCHANGE_API_KEY");
        if (k != null && !k.isBlank()) return k;

        try (InputStream in = new FileInputStream("config.properties")) {
            Properties p = new Properties();
            p.load(in);
            k = p.getProperty("EXCHANGE_API_KEY");
            if (k != null && !k.isBlank()) return k;
        } catch (Exception ignored) {}

        throw new IllegalStateException(
                "SmokeTest: falta EXCHANGE_API_KEY (env, -Dprop o config.properties)."
        );
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);

        String apiKey = resolveApiKey();
        String base = "USD";
        String target = "MXN";
        String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/" + base + "/" + target;

        double rate = ConversorApp.obtenerTasa(url);
        if (rate <= 0.0) {
            System.err.printf("SmokeTest: tasa inválida %s->%s: %.6f%n", base, target, rate);
            System.exit(1);
        }

        System.out.printf("SmokeTest OK: %s->%s rate=%.6f%n", base, target, rate);
        System.exit(0);
    }
}
