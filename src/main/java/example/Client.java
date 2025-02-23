package example;

import com.fasterxml.jackson.databind.ObjectMapper;

import example.domain.MapAnalyzer;
import example.domain.Request;
import example.domain.Response;
import example.domain.game.Cave;
import example.domain.game.Direction;
import example.domain.game.Item;
import example.domain.game.Location;
import example.domain.game.Player;
import example.domain.strategy.Strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Collection;

public class Client {
   private static final String HOST = "35.208.184.138";
    // private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        new Client().startClient();
    }

    public void startClient() {
        try (final var socket = new Socket(HOST, PORT);
             final var is = socket.getInputStream();
             final var isr = new InputStreamReader(is);
             final var reader = new BufferedReader(isr);
             final var os = socket.getOutputStream();
             final var osr = new OutputStreamWriter(os);
             final var writer = new BufferedWriter(osr)) {
            logger.info("Connected to server at {}:{}", HOST, PORT);

            {
                final var json = objectMapper.writeValueAsString(new Request.Authorize("WU6bDxAg"));
                writer.write(json);
                writer.newLine();
                writer.flush();
                logger.info("Sent command: {}", json);
            }

            Cave cave;
            Player player = null;
            Collection<Response.StateLocations.ItemLocation> itemLocations;
            Collection<Response.StateLocations.PlayerLocation> playerLocations;
            String[][] map = new String[0][0];
            int health, gold;
            MapAnalyzer mapAnalyzer = new MapAnalyzer();

            while (!Thread.currentThread().isInterrupted()) {
                var line = reader.readLine();
                if (line == null) {
                    logger.info("Empty line");
                    break;
                }

                final var response = objectMapper.readValue(line, Response.class);
                switch (response) {
                    case Response.Authorized authorized -> {
                        player = authorized.humanPlayer();
                        logger.info("authorized: {}", authorized);
                    }
                    case Response.Unauthorized unauthorized -> {
                        logger.error("unauthorized: {}", unauthorized);
                        return;
                    }
                    case Response.StateCave stateCave -> {
                        cave = stateCave.cave();
                        logger.info("cave: {}", cave);
                        map = new String[cave.rows()][cave.columns()];
                        for (int i = 0; i < map.length; i++) {
                            for (int j = 0; j < map[0].length; j++){
                                if(cave.rock(i,j)){
                                    map[i][j] = "x";
                                }
                                else{
                                    map[i][j]= " ";
                                }
                            }
                        }
                    }
                    case Response.StateLocations stateLocations -> {
                        String entity;
                        itemLocations = stateLocations.itemLocations();
                        playerLocations = stateLocations.playerLocations();
                        health = stateLocations.health();
                        gold = stateLocations.gold();
                        logger.info("itemLocations: {}", itemLocations);
                        logger.info("playerLocations: {}", playerLocations);

                        for (int i = 0; i < map.length; i++) {
                            for (int j = 0; j < map[0].length; j++) {
                                if(map[i][j] != "x"){
                                    entity = mapAnalyzer.isCharacter(playerLocations, i, j, player);
                                    if (entity == " ")
                                        entity = mapAnalyzer.isItem(itemLocations,i,j);
                                    
                                    if(entity != "D"){
                                        map[i][j]= entity;
                                    }
                                    else{
                                        map = mapAnalyzer.drawDragon(map, i, j, playerLocations);
                                    }
                                }
                            }
                        }
                        System.out.println("tu sa przedmioty: " + itemLocations);
                        mapAnalyzer.drawMap(map);

                        final var cmd = new Request.Command(
                                Strategy.strategy(
                                        mapAnalyzer.deepCopy(map),
                                        itemLocations,
                                        playerLocations,
                                        player,
                                        health,
                                        gold));
                        final var cmdJson = objectMapper.writeValueAsString(cmd);
                        writer.write(cmdJson);
                        writer.newLine();
                        writer.flush();
                        logger.info("Sent command: {}", cmd);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error in client operation", e);
        } finally {
            logger.info("Client exiting");
        }
    }

}