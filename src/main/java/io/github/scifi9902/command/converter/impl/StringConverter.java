package io.github.scifi9902.command.converter.impl;

import io.github.scifi9902.command.converter.IConverter;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class StringConverter implements IConverter<String> {

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String getFromString(CommandSender sender, String string) {
        return string;
    }

    @Override
    public List<String> tabComplete(CommandSender sender) {
        return Collections.emptyList();
    }
}
