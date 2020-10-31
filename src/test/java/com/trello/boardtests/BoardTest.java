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

    private Response getLists(String boardId){
        Response response = given()
                .spec(reqSpec)
                .when()
                .get(BOARDS + boardId + "/" + LISTS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        return response;
    }

    @Test
    public void createNewBoard() {
        Response response = createBoard("Board z Javy", null, 200);

        JsonPath json = response.jsonPath();
        assertThat(json.getString("name")).isEqualTo("Board z Javy");

        String boardId = json.get("id");

        deleteBoard(boardId);
    }

    @Test
    public void createBoardWithEmptyBoardName() {
        createBoard("Board bez defaultów", null, 400);
    }

    @Test
    public void createBoardWithoutDeafultLists() {
        Response response = createBoard("Board bez defaultów", false, 200);

        JsonPath json = response.jsonPath();
        assertThat(json.getString("name")).isEqualTo("Board bez defaultów");

        String boardId = json.get("id");

        Response responseGet = getLists(boardId);

        JsonPath jsonGet = responseGet.jsonPath();

        List<String> idList = jsonGet.getList("id");
        assertThat(idList).hasSize(0);

        deleteBoard(boardId);
    }

    @Test
    public void createBoardWithDeafultLists() {
        Response response = createBoard("Board z defaultami", true, 200);

        JsonPath json = response.jsonPath();
        assertThat(json.getString("name")).isEqualTo("Board z defaultami");

        String boardId = json.get("id");

        Response responseGet = getLists(boardId);

        JsonPath jsonGet = responseGet.jsonPath();

        List<String> namesList = jsonGet.getList("name");
        assertThat(namesList).hasSize(3).containsExactly("To Do", "Doing", "Done");

        deleteBoard(boardId);
    }
}