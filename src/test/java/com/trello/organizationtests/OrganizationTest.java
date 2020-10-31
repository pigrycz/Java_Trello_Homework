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

    private Response organizationCreation(String displayName, String name, String desc, String website, Integer status) {
        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", displayName)
                .queryParam("name", name)
                .queryParam("desc", desc)
                .queryParam("website", website)
                .when()
                .post(ORGANIZATIONS)
                .then()
                .statusCode(status)
                .extract()
                .response();

        return response;
    }

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
        Response response = organizationCreation(organizationDisplayName, null, null, null, 200);

        JsonPath json = response.jsonPath();
        assertThat(json.getString("displayName")).isEqualTo(organizationDisplayName);

        System.out.println(response.getBody().asString());

        organizationId = json.get("id");
        deleteOrganization(organizationId);
    }

    @Test
    public void createOrganizationWithNoDisplayName() {
        organizationCreation(null, null, null, null, 400);
    }

    @Test
    public void createNewOrganizationWithDesc() {
        organizationDesc = faker.lorem().paragraph();

        Response response = organizationCreation(organizationDisplayName, null, organizationDesc, null, 200);

        JsonPath json = response.jsonPath();
        assertThat(json.getString("displayName")).isEqualTo(organizationDisplayName);
        assertThat(json.getString("desc")).isEqualTo(organizationDesc);

        organizationId = json.get("id");
        deleteOrganization(organizationId);
    }

    @Test
    public void createNewOrganizationWithValidName() {
        organizationName = faker.company().name().toLowerCase().replaceAll(" ", "").replaceAll("-", "").replaceAll(",","");

        Response response = organizationCreation(organizationDisplayName, organizationName, null, null, 200);

        JsonPath json = response.jsonPath();
        assertThat(json.getString("displayName")).isEqualTo(organizationDisplayName);
        assertThat(json.getString("name")).isEqualTo(organizationName);

        organizationId = json.get("id");
        deleteOrganization(organizationId);
    }

    @Test
    public void createNewOrganizationWithEmptyName() {
        Response response = organizationCreation(organizationDisplayName, "", null, null, 200);

        JsonPath json = response.jsonPath();
        assertThat(json.getString("displayName")).isEqualTo(organizationDisplayName);
        assertThat(json.getString("name")).isEqualTo(organizationDisplayName.toLowerCase().replaceAll(" ", "").replaceAll("-", "").replaceAll(",",""));

        organizationId = json.get("id");

        deleteOrganization(organizationId);
    }

    @Test
    public void createNewOrganizationWithUniqueName() {
        organizationName = faker.company().name().toLowerCase().replaceAll(" ", "").replaceAll("-", "").replaceAll(",","");

        Response response = organizationCreation(organizationDisplayName, organizationName, null, null, 200);

        JsonPath json = response.jsonPath();
        assertThat(json.getString("displayName")).isEqualTo(organizationDisplayName);
        assertThat(json.getString("name")).startsWith(organizationName);

        organizationId = json.get("id");

        response = organizationCreation(organizationDisplayName, organizationName, null, null, 200);

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

        Response response = organizationCreation(organizationDisplayName, null, null, organizationWebsite, 200);

        JsonPath json = response.jsonPath();
        assertThat(json.getString("displayName")).isEqualTo(organizationDisplayName);
        assertThat(json.getString("website")).isEqualTo(organizationWebsite);

        organizationId = json.get("id");
        deleteOrganization(organizationId);
    }

    @Test
    public void createNewOrganizationWithHttpsWebsite() {
        organizationWebsite = "https://" + faker.company().url();

        Response response = organizationCreation(organizationDisplayName, null, null, organizationWebsite, 200);

        JsonPath json = response.jsonPath();
        assertThat(json.getString("displayName")).isEqualTo(organizationDisplayName);
        assertThat(json.getString("website")).isEqualTo(organizationWebsite);

        organizationId = json.get("id");
        deleteOrganization(organizationId);
    }

    @Test
    public void createNewOrganizationWithNoProtocolWebsite() {
        organizationWebsite = faker.company().url();

        Response response = organizationCreation(organizationDisplayName, null, null, organizationWebsite, 200);

        JsonPath json = response.jsonPath();
        assertThat(json.getString("displayName")).isEqualTo(organizationDisplayName);
        assertThat(json.getString("website")).startsWith("http://").endsWith(organizationWebsite);

        organizationId = json.get("id");
        deleteOrganization(organizationId);
    }
}