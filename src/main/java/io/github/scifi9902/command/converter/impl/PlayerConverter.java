package io.github.scifi9902.command.converter.impl;

import io.github.scifi9902.command.converter.IConverter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PlayerConverter implements IConverter<Player> {

    @Override
    public Class<Player> getType() {
        return Player.class;
    }

    @Override
    public Player getFromString(CommandSender sender, String string) {
        return Bukkit.getServer().getPlayer(string);
    }

    @Override
    public List<String> tabComplete(CommandSender sender) {
        return Collections.emptyList();
    }
}
