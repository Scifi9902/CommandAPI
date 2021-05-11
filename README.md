# Command

Annotation based command api

Example Usage Below:


Main class:
```java
public class ExamplePlugin extends JavaPlugin {

   private CommandHandler commandHandler;
   
   @Override
   public void onEnable() {
      this.commandHandler = new CommandHandler(this, "example");
      
      //Registering commands are easy
      this.commandHandler.registerCommand(new ExampleCommand());
      
      //Same with converters
      this.commandHandler.registerConverter(new ExampleConverter());
   }

}
```

Example command:

```java
public class ExampleCommand() {

   @Command(label = "example", aliases = {"hello", "helloworld"}, permission = "command.example")
   public void execute(CommandSender sender) {
      sender.sendMessage("Hello");
   }
    
//Sub command:
   
   @SubCommand(label "test", parent = "example", permission = "command.example")
   public void executeSubCommand(CommandSender sender) {
     sender.sendMessage("Sub command");  
   }
}
```

Converter example:

```java
public class ExampleConverter implements IConverter<Example> {
    @Override
    public Class<Example> getType() {
       return Example.class
    }
    
    @Override
    public Example fromString(CommandSender sender, String string) {
       //Convert or get the example from the string 
       return example
    }

   @Override 
   public List<String> tabComplete(CommandSender sender) {
       return Collections.emptyList();
    }
}
```
