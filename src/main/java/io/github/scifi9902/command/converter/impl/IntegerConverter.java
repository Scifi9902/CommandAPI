package io.github.scifi9902.command.converter.impl;

import io.github.scifi9902.command.converter.IConverter;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class IntegerConverter implements IConverter<Integer> {

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public Integer getFromString(CommandSender sender, String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender) {
        return Collections.emptyList();
    }
}
