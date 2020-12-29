package Utilities;
import Server.*;
import java.io.*;
import java.util.Calendar;

import Client.*;
import Shared.*;
import java.sql.*;
public class test {
    public static void main(String[] args) throws SQLException{
        // JDBC连接的URL, 不同数据库有不同的格式:
        String JDBC_URL = "jdbc:mysql://localhost:3306/learnjdbc?useSSL=false&characterEncoding=utf8";
        String JDBC_USER = "root";
        String JDBC_PASSWORD = "Tt123456";
        // 获取连接:
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT id, grade, name, gender FROM students WHERE gender=? AND grade=?")) {
                ps.setObject(1, "M"); // 注意：索引从1开始
                ps.setObject(2, 3);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        long id = rs.getLong("id");
                        long grade = rs.getLong("grade");
                        String name = rs.getString("name");
                        String gender = rs.getString("gender");
                        System.out.println("id: " + id + " grade: " +grade + " name: " + name
                                + " gender: " + gender);
                    }
                }
            }
        }
        /* test GameJsonUtils
        ArrayList<String> players = new ArrayList<>();
        players.add("pabc");
        players.add("pls");
        players.add("ple");

        var fileIO = new FileIOBasics();
        var MapStr = fileIO.readJsonFile("map.json");

        var jsonUtil = new GameJsonUtils();
        var gameMap = jsonUtil.readJsonToGameMap(MapStr, players);

        var viewer = new GameClientViewer();

        var MapStrAfterOrders = jsonUtil.writeMapToJson(gameMap,null);

        System.out.println(MapStrAfterOrders);

        GameMap gameMap1 = jsonUtil.readJsonToGameMap(MapStrAfterOrders, null);

        var p1 = gameMap1.getPlayerByName("pabc");

        var p2 = gameMap1.getPlayerByName("pls");

        var p3 = gameMap1.getPlayerByName("ple");

        for(Territory territory : p1.getTerritories()) {
            territory.setUnits(13);
        }
        for(Territory territory : p2.getTerritories()) {
            territory.setUnits(14);
        }
        for(Territory territory : p3.getTerritories()) {
            territory.setUnits(15);
        }

        var Str1 = jsonUtil.writeUnits(p1);
        var Str2 = jsonUtil.writeUnits(p2);
        var Str3 = jsonUtil.writeUnits(p3);
        ArrayList<String> strs = new ArrayList<>();
        strs.add(Str1);
        strs.add(Str2);
        strs.add(Str3);
        jsonUtil.readUnits(strs, gameMap);

        // here is the order test
        MoveOrder m = new MoveOrder(p1, "move", gameMap.getTerritoryByName("N"),
                gameMap.getTerritoryByName("O"), 6);
        AttackOrder a = new AttackOrder(p1, "attack", gameMap.getTerritoryByName("O"),
                gameMap.getTerritoryByName("S"), 16);
        var orders = new ArrayList<OrderBasic>();
        orders.add(m);
        orders.add(a);
        String orderStr = jsonUtil.writeOrderListToJson(orders);
        System.out.println("orderStr: " + orderStr);
        var handler = new Server.OrderHandler();

        viewer.printMap(gameMap, gameMap.getPlayerByName("pabc"), "order");

        handler.execute(orders);

        viewer.printMap(gameMap, gameMap.getPlayerByName("pabc"), "simple");

        System.out.println(jsonUtil.writeMapToJson(gameMap,null));
        */
    }

}

