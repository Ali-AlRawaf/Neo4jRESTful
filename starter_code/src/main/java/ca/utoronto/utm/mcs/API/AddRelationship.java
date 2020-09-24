package ca.utoronto.utm.mcs.API;

import java.io.IOException;

import ca.utoronto.utm.mcs.Models.RelationshipModel;
import ca.utoronto.utm.mcs.Neo4JConnector;
import ca.utoronto.utm.mcs.exceptions.BadRequestException;
import org.json.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class AddRelationship implements HttpHandler
{
    private static RelationshipModel relationshipModel;

    public AddRelationship(RelationshipModel relationshipModel) {
        relationshipModel = relationshipModel;
    }

    public void handle(HttpExchange r) {
        try {
            if (r.getRequestMethod().equals("PUT")) {
                handlePut(r);
            }else{
                r.sendResponseHeaders(400, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handlePut(HttpExchange r) throws IOException, JSONException{
        String name = relationshipModel.getActorId();
        String movieId = relationshipModel.getMovieId();

        try{
            String body = Utils.convert(r.getRequestBody());
            JSONObject deserialized = new JSONObject(body);
            name = deserialized.getString("name");
            movieId = deserialized.getString("movieId");
        } catch (JSONException e) {
            r.sendResponseHeaders(400, -1);
        }

        try{
            Neo4JConnector nb = new Neo4JConnector();
            nb.addMovie(name, movieId);
            nb.close();
            relationshipModel.setActorId(name);
            relationshipModel.setMovieId(movieId);
            r.sendResponseHeaders(200, -1);

        } catch (BadRequestException e){
            r.sendResponseHeaders(400, -1);
        } catch(Exception J){
            r.sendResponseHeaders(500, -1);
        }
    }
}
