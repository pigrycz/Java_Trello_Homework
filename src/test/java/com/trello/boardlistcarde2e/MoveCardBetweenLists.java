package com.trello.boardlistcarde2e;

import com.trello.basetest.BaseTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MoveCardBetweenLists extends BaseTest {
    private static String boardName = "My e2e board";
    private static String firstListName = "first e2e list";
    private static String secondListName = "second e2e list";
    private static String cardName = "e2e card";

    private static String boardId;
    private static String firstListId;
    private static String secondListId;
    private static String cardId;

    @Test
    @Order(1)
    public void createNewBoard() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("name", boardName)
                .queryParam("defaultLists", false)
                .when()
                .post(BOARDS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("name")).isEqualTo(boardName);

        boardId = json.get("id");
    }

    @Test
    @Order(2)
    public void createFirstList() {
        Response responseList = given()
                .spec(reqSpec)
                .queryParam("idBoard", boardId)
                .queryParam("name", firstListName)
                .when()
                .post(LISTS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath jsonFirstList = responseList.jsonPath();
        assertThat(jsonFirstList.getString("name")).isEqualTo(firstListName);

        firstListId = jsonFirstList.get("id");
    }

    @Test
    @Order(3)
    public void createSecondSecondList() {
        Response responseList = given()
                .spec(reqSpec)
                .queryParam("idBoard", boardId)
                .queryParam("name", secondListName)
                .when()
                .post(LISTS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath jsonSecondList = responseList.jsonPath();
        assertThat(jsonSecondList.getString("name")).isEqualTo(secondListName);

        secondListId = jsonSecondList.get("id");
    }

    @Test
    @Order(4)
    public void addNewCard() {
        Response responseCardCreation = given()
                .spec(reqSpec)
                .queryParam("idList", firstListId)
                .queryParam("name", cardName)
                .when()
                .post(CARDS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath jsonAddCard = responseCardCreation.jsonPath();

        assertThat(jsonAddCard.getString("name")).

                isEqualTo(cardName);

        cardId = jsonAddCard.get("id");
    }


    @Test
    @Order(5)
    public void moveCardToSecondList() {
        Response responseMoveCard = given()
                .spec(reqSpec)
                .pathParam("id", cardId)
                .queryParam("idList", secondListId)
                .when()
                .put(CARDS + "{id}")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath jsonMoveCard = responseMoveCard.jsonPath();
        assertThat(jsonMoveCard.getString("idList")).isEqualTo(secondListId);
    }

    @Test
    @Order(6)
    public void deleteBoard() {
        given()
                .spec(reqSpec)
                .when()
                .delete(BOARDS + boardId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }
}