package com.trello.organizationtests;

import com.trello.basetest.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

public class OrganizationTest extends BaseTest {

    private static String organizationId;
    private static String secondOrganizationId;
    private static String organizationDisplayName;
    private static String organizationName;
    private static String organizationDesc;
    private static String organizationWebsite;

    protected void deleteOrganization(String id){
        given()
                .spec(reqSpec)
                .pathParam("id", id)
                .when()
                .delete(ORGANIZATIONS + "{id}")
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @BeforeEach
    public void beforeEach(){
        organizationDisplayName = faker.company().name();
    }

    @Test
    public void createNewOrganization() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", organizationDisplayName)
                .when()
                .post(ORGANIZATIONS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("displayName")).isEqualTo(organizationDisplayName);

        organizationId = json.get("id");
        deleteOrganization(organizationId);
    }

    @Test
    public void createOrganizationWithNoDisplayName() {
       given()
                .spec(reqSpec)
                .when()
                .post(ORGANIZATIONS)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void createNewOrganizationWithDesc() {
        organizationDesc = faker.lorem().paragraph();

        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", organizationDisplayName)
                .queryParam("desc", organizationDesc)
                .when()
                .post(ORGANIZATIONS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("displayName")).isEqualTo(organizationDisplayName);
        assertThat(json.getString("desc")).isEqualTo(organizationDesc);

        organizationId = json.get("id");
        deleteOrganization(organizationId);
    }

    @Test
    public void createNewOrganizationWithValidName() {
        organizationName = faker.company().name().toLowerCase().replaceAll(" ", "").replaceAll("-", "").replaceAll(",","");

        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", organizationDisplayName)
                .queryParam("name", organizationName)
                .when()
                .post(ORGANIZATIONS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("displayName")).isEqualTo(organizationDisplayName);
        assertThat(json.getString("name")).isEqualTo(organizationName);

        organizationId = json.get("id");
        deleteOrganization(organizationId);
    }

    @Test
    public void createNewOrganizationWithEmptyName() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", organizationDisplayName)
                .queryParam("name", "")
                .when()
                .post(ORGANIZATIONS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("displayName")).isEqualTo(organizationDisplayName);
        assertThat(json.getString("name")).isEqualTo(organizationDisplayName.toLowerCase().replaceAll(" ", "").replaceAll("-", "").replaceAll(",",""));

        organizationId = json.get("id");

        deleteOrganization(organizationId);
    }

    @Test
    public void createNewOrganizationWithUniqueName() {
        organizationName = faker.company().name().toLowerCase().replaceAll(" ", "").replaceAll("-", "").replaceAll(",","");

        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", organizationDisplayName)
                .queryParam("name", organizationName)
                .when()
                .post(ORGANIZATIONS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("displayName")).isEqualTo(organizationDisplayName);
        assertThat(json.getString("name")).startsWith(organizationName);

        organizationId = json.get("id");

        response = given()
                .spec(reqSpec)
                .queryParam("displayName", organizationDisplayName)
                .queryParam("name", organizationName)
                .when()
                .post(ORGANIZATIONS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        json = response.jsonPath();
        assertThat(json.getString("displayName")).isEqualTo(organizationDisplayName);
        assertThat(json.getString("name")).doesNotMatch(organizationName).contains(organizationName);

        secondOrganizationId = json.get("id");

        deleteOrganization(organizationId);
        deleteOrganization(secondOrganizationId);
    }

    @Test
    public void createNewOrganizationWithWebsite() {
        organizationWebsite = "http://" + faker.company().url();

        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", organizationDisplayName)
                .queryParam("website", organizationWebsite)
                .when()
                .post(ORGANIZATIONS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("displayName")).isEqualTo(organizationDisplayName);
        assertThat(json.getString("website")).isEqualTo(organizationWebsite);

        organizationId = json.get("id");
        deleteOrganization(organizationId);
    }

    @Test
    public void createNewOrganizationWithHttpsWebsite() {
        organizationWebsite = "https://" + faker.company().url();

        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", organizationDisplayName)
                .queryParam("website", organizationWebsite)
                .when()
                .post(ORGANIZATIONS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("displayName")).isEqualTo(organizationDisplayName);
        assertThat(json.getString("website")).isEqualTo(organizationWebsite);

        organizationId = json.get("id");
        deleteOrganization(organizationId);
    }

    @Test
    public void createNewOrganizationWithNoProtocolWebsite() {
        organizationWebsite = faker.company().url();

        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", organizationDisplayName)
                .queryParam("website", organizationWebsite)
                .when()
                .post(ORGANIZATIONS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("displayName")).isEqualTo(organizationDisplayName);
        assertThat(json.getString("website")).startsWith("http://").endsWith(organizationWebsite);

        organizationId = json.get("id");
        deleteOrganization(organizationId);
    }
}