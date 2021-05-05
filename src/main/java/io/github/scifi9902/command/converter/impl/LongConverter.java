package io.github.scifi9902.command.converter.impl;

import io.github.scifi9902.command.converter.IConverter;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class LongConverter implements IConverter<Long> {

    @Override
    public Class<Long> getType() {
        return Long.class;
    }

    @Override
    public Long getFromString(CommandSender sender, String string) {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException exception) {
            return -1L;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender) {
        return Collections.emptyList();
    }
}
