package web.com.config;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JPAConfig {
    private static final EntityManagerFactory FACTORY = createFactory();

    private JPAConfig() {
    }

    private static EntityManagerFactory createFactory() {
        Map<String, String> properties = new HashMap<>();
        overrideFromEnvironment(properties, "DB_URL", "jakarta.persistence.jdbc.url");
        overrideFromEnvironment(properties, "DB_USER", "jakarta.persistence.jdbc.user");
        overrideFromEnvironment(properties, "DB_PASSWORD", "jakarta.persistence.jdbc.password");
        return Persistence.createEntityManagerFactory("ServletCRUDMVC", properties);
    }

    private static void overrideFromEnvironment(Map<String, String> properties, String name, String key) {
        String value = Environment.get(name, null);
        if (value != null) {
            properties.put(key, value);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return FACTORY;
    }

    public static EntityManager getEntityManager() {
        return FACTORY.createEntityManager();
    }
}
