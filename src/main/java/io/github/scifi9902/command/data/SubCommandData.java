package io.github.scifi9902.command.data;

import io.github.scifi9902.command.annotations.SubCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@AllArgsConstructor @Getter
public class SubCommandData {

    private final Object object;

    private final Method method;

    private final SubCommand subCommand;

}
