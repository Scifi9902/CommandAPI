package io.github.scifi9902.command.converter;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface IConverter<T> {

    Class<T> getType();

    T getFromString(CommandSender sender, String string);

    List<String> tabComplete(CommandSender sender);
}
