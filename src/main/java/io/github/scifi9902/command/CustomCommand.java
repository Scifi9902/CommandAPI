package io.github.scifi9902.command;

import io.github.scifi9902.command.converter.IConverter;
import io.github.scifi9902.command.data.CommandData;
import io.github.scifi9902.command.data.SubCommandData;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public class CustomCommand extends Command {

    private final CommandData commandData;

    private final CommandHandler commandHandler;

    public CustomCommand(CommandData commandData, CommandHandler commandHandler) {
        super(commandData.getCommand().name());

        this.commandData = commandData;
        this.commandHandler = commandHandler;

        io.github.scifi9902.command.annotations.Command command = commandData.getCommand();

        if (command.aliases().length > 0) {
            this.setAliases(Arrays.asList(command.aliases()));
        }

        this.setPermission(command.permission());
    }

    @SneakyThrows
    @Override
    public boolean execute(CommandSender commandSender, String s, String[] arguments) {
        Object object;
        Method method;
        String[] args;
        String permission;

        SubCommandData subCommand = null;

        if (arguments.length >= 1 && this.commandData.getSubCommands().stream()
                .filter(subCommandData -> subCommandData.getSubCommand().name().equalsIgnoreCase(arguments[0])
                        || Arrays.stream(arguments).filter(alias -> arguments[0].equalsIgnoreCase(alias)).findAny().orElse(null) != null)
                .findAny().orElse(null) != null) {


            subCommand = commandData.getSubCommands().stream().filter(subCommandData -> subCommandData.getSubCommand().name().equalsIgnoreCase(arguments[0]) || Arrays.stream(subCommandData.getSubCommand().aliases()).filter(alias -> arguments[0].equalsIgnoreCase(alias)).findAny().orElse(null) != null).findFirst().orElse(null);

            if (subCommand != null) {
                object = subCommand.getObject();
                method = subCommand.getMethod();
                permission = subCommand.getSubCommand().permission();
                args = Arrays.copyOfRange(arguments, 1, arguments.length);

            } else {
                args = Arrays.copyOfRange(arguments, 1, arguments.length);
                object = commandData.getObject();
                method = commandData.getMethod();
                permission = commandData.getCommand().permission();
            }

        } else {
            args = arguments;
            object = commandData.getObject();
            method = commandData.getMethod();
            permission = commandData.getCommand().permission();
        }

        Parameter[] parameters = method.getParameters();

        if (!(commandSender instanceof Player) && parameters[0].getType().equals(Player.class)) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cOnly players may execute this command."));
            return true;
        }

        if (!permission.isEmpty() && !commandSender.hasPermission(permission)) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNo permission."));
            return true;
        }

        if (parameters.length >= 1 && !parameters[0].getType().isArray()) {
            Parameter[] rangedCopy = Arrays.copyOfRange(parameters, 1, parameters.length);
            Object[] objects = new Object[rangedCopy.length];

            if (args.length < rangedCopy.length) {
                if (subCommand == null) {
                    commandSender.sendMessage(ChatColor.RED + "Usage: /" + this.getLabel() + " " + Arrays.stream(rangedCopy).map(parameter -> "<" + (parameter.isAnnotationPresent(io.github.scifi9902.command.annotations.Parameter.class) ? parameter.getAnnotation(io.github.scifi9902.command.annotations.Parameter.class).name() : "arg") + ">").collect(Collectors.joining(" ")));
                } else {
                    commandSender.sendMessage(ChatColor.RED + "Usage: /" + this.getLabel() + " " + subCommand.getSubCommand().name() + " " + Arrays.stream(rangedCopy).map(parameter -> "<" + (parameter.isAnnotationPresent(io.github.scifi9902.command.annotations.Parameter.class) ? parameter.getAnnotation(io.github.scifi9902.command.annotations.Parameter.class).name() : "arg") + ">").collect(Collectors.joining(" ")));
                }
                return true;
            }

            for (int i = 0; i < rangedCopy.length; i++) {

                Parameter parameter = rangedCopy[i];

                IConverter<?> converter = this.commandHandler.getConverter(parameter.getType());

                if (converter == null) {
                    throw new IllegalArgumentException("Unable to find converter for " + parameter.getType().getName());
                }

                objects[i] = converter.getFromString(commandSender, args[i]);
            }

            objects = ArrayUtils.add(objects, 0, parameters[0].getType().cast(commandSender));
            method.invoke(object, objects);
        } else if (parameters.length == 1 && parameters[0].getType().isArray()) {
            method.invoke(object, commandSender, args);
        } else {
            method.invoke(object, commandSender);
        }
        return false;
    }
}
