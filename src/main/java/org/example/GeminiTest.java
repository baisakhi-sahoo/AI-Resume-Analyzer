package org.example;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class GeminiTest {

    public static void main(String[] args) {

        Client client = Client.builder().apiKey(System.getenv("GEMINI_API_KEY")).build();

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-3.6-flash",
                        "Say hello to CareerBoostAI in one sentence.",
                        null
                );

        System.out.println("===== GEMINI RESPONSE =====");
        System.out.println(response.text());
        System.out.println("===== END RESPONSE =====");
    }
}
