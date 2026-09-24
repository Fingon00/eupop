package ootie.game.persistence;

import java.util.Map;

record GameSaveData(int saveVersion, String name, Map<String, PlayerSaveData> players) {}
