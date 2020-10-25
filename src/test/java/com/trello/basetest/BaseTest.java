package com.trello.basetest;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {
    protected static String KEY = "XXX";
    protected static String TOKEN = "XXX";

    protected static final String BASE_URL = "https://api.trello.com/1/";
    protected static final String BOARDS = "boards/";
    protected static final String LISTS = "lists/";
    protected static final String CARDS = "cards/";
    protected static final String ORGANIZATIONS = "organizations/";

    protected static RequestSpecBuilder reqBuilder;
    protected static RequestSpecification reqSpec;
    protected static Faker faker;

    @BeforeAll
    public static void beforeAll() {
        Configuration config = loadConfig();
        KEY = config.getString("trello.key");
        TOKEN = config.getString("trello.token");

        reqBuilder = new RequestSpecBuilder();
        reqBuilder.addQueryParam("key", KEY);
        reqBuilder.addQueryParam("token", TOKEN);
        reqBuilder.setContentType(ContentType.JSON);

        reqSpec = reqBuilder.build();

        faker = new Faker();
    }

    private static FileBasedConfiguration loadConfig() {
        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties().setFileName("test.properties"));
        try {
            return builder.getConfiguration();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
