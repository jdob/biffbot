public class BiffMessage {

    private String message;
    private BiffBot bot;

    public BiffMessage(BiffBot bot, String message) {
        this.bot = bot;
        this.message = message;
    }

    public void reply(String message) {
        String destination = channel();
        if (destination.equals(bot.getNick())) {
            destination = author();
        }

        say(destination, message);
    }

    public void say(String destination, String message) {
        bot.send("PRIVMSG " + destination + " :" + message + "\r\n");
    }

    public String channel() {
        return message.split(" ")[2];
    }

    public String author() {
        return message.substring(1, message.indexOf("!"));
    }
}