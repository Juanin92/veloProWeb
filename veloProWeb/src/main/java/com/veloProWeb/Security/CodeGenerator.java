package com.veloProWeb.Security;

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.stereotype.Component;

@Component
public class CodeGenerator {

    public String generate() {
        return generateRandomToken();
    }

    private String generateRandomToken() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(CharacterPredicates.DIGITS,CharacterPredicates.LETTERS)
                .build();
        return generator.generate(8);
    }
}
