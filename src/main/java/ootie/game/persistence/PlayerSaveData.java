package ootie.game.persistence;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import ootie.game.PlayerProperties;

record PlayerSaveData(@JsonUnwrapped PlayerProperties properties) {}
