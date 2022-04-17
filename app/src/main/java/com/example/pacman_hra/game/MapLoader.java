package com.example.pacman_hra.game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MapLoader {

    private MapLoader() { }

    public static ArrayList<ArrayList<Integer>> loadMap(String fileName)
    {
        ArrayList<ArrayList<Integer>> map = new ArrayList<>();

        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(Game.context.getAssets().open("levels/" + fileName)));

            String line;
            while ((line = br.readLine()) != null)
            {
                map.add(new ArrayList<>());

                for (int i = 0; i < line.length(); ++i)
                {
                    map.get(map.size() - 1).add(line.charAt(i) - '0');
                }
            }

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return map;
    }
}
