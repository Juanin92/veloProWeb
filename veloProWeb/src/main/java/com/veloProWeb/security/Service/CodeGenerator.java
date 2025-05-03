package com.veloProWeb.security.Service;

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.stereotype.Component;

@Component
public class CodeGenerator {

    public String generate() {
        return generateRandomToken();
    }

    /**
     * Genera un token aleatorio de 8 caracteres alfanuméricos
     * @return token aleatorio
     */
    private String generateRandomToken() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(CharacterPredicates.DIGITS,CharacterPredicates.LETTERS)
                .build();
        return generator.generate(8);
    }
}
