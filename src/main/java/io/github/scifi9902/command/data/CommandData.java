package io.github.scifi9902.command.data;

import com.google.common.collect.Lists;
import io.github.scifi9902.command.annotations.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.List;

@AllArgsConstructor @Getter
public class CommandData {

    private final Object object;

    private final Method method;

    private final Command command;

    private final List<SubCommandData> subCommands = Lists.newArrayList();

}
