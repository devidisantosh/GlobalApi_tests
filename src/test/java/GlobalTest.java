package Tests;

import com.global.restassured.GraphQLQuery;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


public class GlobalTest {
    @Test
    public void returnNumberOfLaunches(){
        GraphQLQuery query = new GraphQLQuery();
        query.setQuery("{ launches { id mission_name ships { id} rocket { first_stage {cores { flight }} second_stage {block } } } }");
         Response response = getResponse(query);
         checkAssertions(response);
    }


    @ParameterizedTest
    @ValueSource(ints = {2,5,10})
    public void updateLimitAndReturnNumberOfLaunches(int limit){
        GraphQLQuery query = new GraphQLQuery();
        query.setQuery("query getLaunches($limit: Int!){ launches (limit: $limit) { id mission_name ships { id} rocket { first_stage {cores { flight }} second_stage {block } } } }");

        JSONObject variables = new JSONObject();
        variables.put("limit", limit);
        query.setVariables(variables.toString());

        Response response = getResponse(query);
        checkAssertions(response);
    }


    @ParameterizedTest
    @ValueSource(ints = {5,10,15})
    public void updateOffsetAndReturnNumberOfLaunches(int offset){
        GraphQLQuery query = new GraphQLQuery();
        query.setQuery("query getLaunches($offset: Int!){ launches (offset: $offset) { id mission_name ships { id} rocket { first_stage {cores { flight }} second_stage {block } } } }\n");

        JSONObject variables = new JSONObject();
        variables.put("offset", offset);
        query.setVariables(variables.toString());

        Response response = getResponse(query);
        checkAssertions(response);

    }
    public void checkAssertions(Response response){
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(response.jsonPath().getList("data.launches.mission_name").size()>0);
        Assertions.assertTrue(response.jsonPath().getList("data.launches.ships").size()>0);
        Assertions.assertTrue(response.path("data.launches.rocket.first_stage")!=null);
        Assertions.assertTrue(response.path("data.launches.rocket.second_stage")!=null);
    }

    public Response getResponse( GraphQLQuery query){
        RestAssured.baseURI = "https://api.spacex.land/graphql";
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(query)
                .when()
                .post();

    }

}
