package io.github.scifi9902.command;

import com.google.common.collect.Lists;
import io.github.scifi9902.command.annotations.Command;
import io.github.scifi9902.command.annotations.SubCommand;
import io.github.scifi9902.command.converter.IConverter;
import io.github.scifi9902.command.converter.impl.IntegerConverter;
import io.github.scifi9902.command.converter.impl.LongConverter;
import io.github.scifi9902.command.converter.impl.PlayerConverter;
import io.github.scifi9902.command.converter.impl.StringConverter;
import io.github.scifi9902.command.data.CommandData;
import io.github.scifi9902.command.data.SubCommandData;
import lombok.Getter;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CommandHandler {

    private CommandMap commandMap;

    private final String fallbackPrefix;

    private final List<IConverter<?>> converters = Lists.newArrayList();

    private final List<CustomCommand> customCommands = Lists.newArrayList();

    public CommandHandler(JavaPlugin plugin, String fallbackPrefix) {
        this.fallbackPrefix = fallbackPrefix;

        try {
            //Get the declared field with reflections
            Field field = plugin.getServer().getClass().getDeclaredField("commandMap");
            //Set as accessible
            field.setAccessible(true);
            //Get and cast to CommandMap
            this.commandMap = (CommandMap) field.get(plugin.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        this.registerConverter(new IntegerConverter());
        this.registerConverter(new LongConverter());
        this.registerConverter(new PlayerConverter());
        this.registerConverter(new StringConverter());
    }

    public void registerConverter(IConverter<?> converter) {
        if (converter == null) {
            throw new IllegalArgumentException("The converter you attempted to pass was null");
        }

        this.converters.add(converter);
    }

    public IConverter<?> getConverter(Class<?> clazz) {
        for (IConverter<?> converter : this.converters) {
            if (converter.getType().equals(clazz)) {
                return converter;
            }
        }
        return null;
    }

    public void registerCommand(Object object) {
        Method[] rawMethods = object.getClass().getMethods();
        List<Method> commandMethods = Arrays.stream(rawMethods).filter(method -> method.getAnnotation(Command.class) != null).collect(Collectors.toList());
        List<Method> subCommandMethods = Arrays.stream(rawMethods).filter(method -> method.getAnnotation(SubCommand.class) != null).collect(Collectors.toList());

        for (Method method : commandMethods) {
            Command command = method.getAnnotation(Command.class);
            CommandData commandData = new CommandData(object, method, command);
            CustomCommand customCommand = new CustomCommand(commandData, this);

            customCommands.add(customCommand);
            this.commandMap.register(fallbackPrefix, customCommand);
        }

        for (Method method : subCommandMethods) {
            SubCommand subCommand = method.getAnnotation(SubCommand.class);

            CustomCommand parentCommand = this.customCommands.stream().filter(customCommand ->
                    customCommand.getCommandData().getCommand().name().equalsIgnoreCase(subCommand.parent()) ||
                            Arrays.stream(customCommand.getCommandData().getCommand().aliases()).filter(alias ->
                                    subCommand.parent().equalsIgnoreCase(alias)).findFirst().orElse(null) != null)
                    .findFirst().orElse(null);

            if (parentCommand == null) {
                System.out.println("Failed to find parent command " + subCommand.parent() + " for command " + subCommand.name());
                continue;
            }

            parentCommand.getCommandData().getSubCommands().add(new SubCommandData(object, method, subCommand));
        }
    }


}
