package com.automation.tests.day4;

import com.automation.utilities.ConfigurationReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;

public class ORDSTestsDay4 {
    @BeforeAll
    public static void setup() {
        baseURI = ConfigurationReader.getProperty("ords.uri");
    }

    /**
     * Warmup!
     * Given accept type is JSON
     * When users sends a GET request to "/employees"
     * Then status code is 200
     * And Content type is application/json
     * And response time is less than 3 seconds
     */
    @Test
    @DisplayName("Verify that response time is less than 3 seconds")
    public void test1() {
        given().
                accept(ContentType.JSON).
                when().
                get("/employees").
                then().
                assertThat().
                statusCode(200).
                contentType(ContentType.JSON).
                time(lessThan(3L), TimeUnit.SECONDS).
                log().all(true);//payload=body, and our body has JSON format

        //.all(true) < it's like prettyPrint(), prettyPick(), but they just print body
        //log().all(true) <-- print into console all info about response: header, body, status code, schema(http), schema version (1.1)
    }

    /*{{baseUrl}}/countries?q={"country_id":"US"}

    Given accept type is JSON
    And parameters: q = {"country_id":"US"}
    When users sends a GET request to "/countries"
    Then status code is 200
    And Content type is application/json
    And country_name from payload is "United States of America"

 */
    @Test
    @DisplayName("Verify that country_name from payload is \"United States of America\"")
    public void test2() {
        given().
                accept(ContentType.JSON).
                queryParam("q", "{\"country_id\":\"US\"}").
        when().
                get("/countries").
        then().
                assertThat().
                    contentType(ContentType.JSON).
                    statusCode(200).
                    body("items[0].country_name", is("United States of America")).
                    log().all(true);
    }

    @Test
    @DisplayName("Get all links and print them")
    public void test3() {
        Response response = given().
                                accept(ContentType.JSON).
//                                queryParam("q", "{\"country_id\":\"US\"}").
                            when().
                                get("/countries");

        JsonPath jsonPath = response.jsonPath();
        //if I don't put index, I will get collection of properties (only if they exists)
        List<?> links = jsonPath.getList("items.links.href");

        for(Object link: links){
            System.out.println(link);
        }

    }

    @Test
    @DisplayName("Verify that payload contains only 25 countries")
    public void test4(){
        List<?> countries = given().
                                accept(ContentType.JSON).
                            when().
                                get("/countries").prettyPeek().
                            thenReturn().jsonPath().getList("items");

        assertEquals(25, countries.size());
    }

    /**
     * given path parameter is "/countries" and region id is 2
     * when user makes get request
     * then assert that status code is 200
     * and user verifies that body returns following country names
     *  |Argentina                |
     *  |Brazil                   |
     *  |Canada                   |
     *  |Mexico                   |
     *  |United States of America |
     *
     */

    @Test
    @DisplayName("Verify that payload contains following countries")
    public void test5() {
        //to use List.of() set java 9 at least
        List<String> expected = List.of("Argentina", "Brazil", "Canada", "Mexico", "United States of America");

        Response response = given().
                                accept(ContentType.JSON).
                                queryParam("q", "{\"region_id\":\"2\"}").
                            when().
                                get("/countries").prettyPeek();
        List<String> actual = response.jsonPath().getList("items.country_name");

        assertEquals(expected, actual);

        ///with assertThat()

        given().
                accept(ContentType.JSON).
                queryParam("q", "{\"region_id\":\"2\"}").
        when().
                get("/countries").
        then().assertThat().body("items.country_name" , contains("Argentina", "Brazil", "Canada", "Mexico", "United States of America"));
    }

    /**
     * given path parameter is "/employees"
     * when user makes get request
     * then assert that status code is 200
     * Then user verifies that every employee has positive salary
     *
     */
    @Test
    @DisplayName("Verify that every employee has positive salary")
    public void test6(){

    }
}
