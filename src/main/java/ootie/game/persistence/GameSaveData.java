package ootie.game.persistence;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.Map;
import ootie.game.GameProperties;

record GameSaveData(@JsonUnwrapped GameProperties properties, Map<String, PlayerSaveData> players) {}
