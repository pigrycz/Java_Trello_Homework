package com.trello.boardtests;

import com.trello.basetest.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

public class BoardTest extends BaseTest {

    @Test
    public void createNewBoard() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "Board z Javy")
                .when()
                .post(BOARDS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("name")).isEqualTo("Board z Javy");

        String boardId = json.get("id");

        given()
                .spec(reqSpec)
                .when()
                .delete(BOARDS + boardId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void createBoardWithEmptyBoardName() {
        given()
                .spec(reqSpec)
                .queryParam("name", "")
                .when()
                .post(BOARDS)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void createBoardWithoutDeafultLists() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "Board bez defaultów")
                .queryParam("defaultLists", false)
                .when()
                .post(BOARDS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("name")).isEqualTo("Board bez defaultów");

        String boardId = json.get("id");

        Response responseGet = given()
                .spec(reqSpec)
                .when()
                .get(BOARDS + boardId + "/" + LISTS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath jsonGet = responseGet.jsonPath();
        List<String> idList = jsonGet.getList("id");
        assertThat(idList).hasSize(0);

        given()
                .spec(reqSpec)
                .when()
                .delete(BOARDS + boardId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void createBoardWithDeafultLists() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "Board z defaultami")
                .queryParam("defaultLists", true)
                .when()
                .post(BOARDS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("name")).isEqualTo("Board z defaultami");

        String boardId = json.get("id");

        Response responseGet = given()
                .spec(reqSpec)
                .when()
                .get(BOARDS + boardId + "/" + LISTS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath jsonGet = responseGet.jsonPath();

        List<String> namesList = jsonGet.getList("name");

        given()
                .spec(reqSpec)
                .when()
                .delete(BOARDS + boardId)
                .then()
                .statusCode(HttpStatus.SC_OK);

        assertThat(namesList).hasSize(3).contains("To Do", "Doing", "Done");
    }
}