package com.rigel.app.config;

import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import com.rigel.app.util.Constaints;
import com.rigel.app.util.RAUtility;

import jakarta.annotation.PostConstruct;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class P12PropertyLoader {

    private final ConfigurableEnvironment environment;

    public P12PropertyLoader(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void loadFromP12() throws Exception {
        String result=RAUtility.callOwnServer();
        File file=new File(Constaints.PROJECT_DIR+"/secure.p12");
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream(file.getAbsoluteFile()), result.toCharArray());

        Map<String, Object> map = new HashMap<>();

        Enumeration<String> aliases = ks.aliases();

        while (aliases.hasMoreElements()) {
            String key = aliases.nextElement();

            KeyStore.SecretKeyEntry entry =
                    (KeyStore.SecretKeyEntry) ks.getEntry(
                            key,
                            new KeyStore.PasswordProtection(result.toCharArray())
                    );

            String value = new String(entry.getSecretKey().getEncoded());

            map.put(key, value);
        }

        // Add highest priority property source
        environment.getPropertySources().addFirst(
                new MapPropertySource("p12Properties", map)
        );
    }
}
