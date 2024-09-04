package com.codegym.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;

public class TinderBoltApp extends SimpleTelegramBot {

    public static final String TELEGRAM_BOT_TOKEN = "7412847542:AAFQj3h72M2EEdJpKzG__K7IvD5Nyh4Ifpw"; //TODO: añadir el token del bot entre comillas
    public static final String OPEN_AI_TOKEN = "gpt:9ZT5tFmuknnv69DyaBG4JFkblB3TnQwI4ewQzWKutY0lfsgf"; //TODO: añadir el token de ChatGPT entre comillas

    private ChatGPTService chatGPT = new ChatGPTService(OPEN_AI_TOKEN);
    public DialogMode mode;
    private ArrayList<String> list = new ArrayList<>();

    public TinderBoltApp() {
        super(TELEGRAM_BOT_TOKEN);
    }

    //TODO: escribiremos la funcionalidad principal del bot aquí

    public void startCommand(){
        mode = DialogMode.MAIN;
        String text = loadMessage("main");
        sendPhotoMessage("main");
        sendTextMessage(text);
        //sendTextMessage(loadMessage("main")); //Mismas dos lineas de arriba

        showMainMenu(
                "start", "menú principal del bot",
                "profile", "generación de perfil de Tinder \uD83D\uDE0E!",
                "opener", "mensaje para iniciar conversación \uD83E\uDD70",
                "message", "correspondencia en su nombre \uD83D\uDE08",
                "date", "correspondencia con celebridades \uD83D\uDD25",
                "gpt", "hacer una pregunta a chat GPT \uD83E\uDDE0"
        );

    }

    public void gptCommand(){
        mode = DialogMode.GPT;
        String text = loadMessage ("gpt");

        sendPhotoMessage("gpt");
        sendTextMessage(text);

    }
    public void gptDialog(){
        String text = getMessageText();
        String prompt = loadPrompt("gpt");
        String answer = chatGPT.sendMessage(prompt, text);
        var myMessage = sendTextMessage("gpt is typing...");
        //sendTextMessage(answer);
        updateTextMessage(myMessage,answer);
    }

    public void dateCommand(){
        mode = DialogMode.DATE;
        String text = loadMessage ("date");

        sendPhotoMessage("date");
        sendTextMessage(text);
        sendTextButtonsMessage(text,
                "date_grande" , "Ariana Grande",
                "date_robbie" , "Margot Robbie",
                "date_zendaya" , "Zendaya",
                "date_gosling" , "Ryan Gosling",
                "date_hardy" , "Tom Hardy");
    }

    public void dateButton(){
        String key = getButtonKey();
        sendPhotoMessage(key);
        sendHtmlMessage(key);

        String prompt = loadPrompt(key);
        chatGPT.setPrompt(prompt);

    }

    public void dateDialog(){
        String text = getMessageText();
        var myMessage = sendTextMessage("user is typing...");
        String answer = chatGPT.addMessage(text);
        //sendTextMessage(answer);
        updateTextMessage(myMessage,answer);
    }

    public void messageCommand(){
        mode = DialogMode.MESSAGE;
        String text = loadMessage("message");
        sendPhotoMessage("message");
        sendTextButtonsMessage(text,
                "message_next", "write next message",
                "message_date", "Ask the person out on date");
        list.clear();

    }
    public void messageButton(){
        String key = getButtonKey();
        String prompt = loadPrompt(key);
        String history = String.join("\n\n", list);
        var myMessage = sendTextMessage("Chat GPT is typing...");
        String answer = chatGPT.sendMessage(prompt, history);
        updateTextMessage(myMessage,answer);

    }
    public void messageDialog(){
        String text = getMessageText();
        list.add(text);

    }

    public void  hello(){
        if(mode == DialogMode.GPT){
            gptDialog();
        } else if (mode == DialogMode.DATE){
            dateDialog();
        } else if (mode == DialogMode.MESSAGE){
            messageDialog();
        } else {
            String text = getMessageText();
            sendTextMessage("*Hello*");
            sendTextMessage("_How are you?_");
            sendTextMessage("You wrote: " + text);

            sendPhotoMessage( "avatar_main");
            sendTextButtonsMessage("Launch process",
                    "Start", "Start",
                    "Stop!", "Stop!");
        }
    }

    public void helloButton(){

        String key = getButtonKey();
        if (key.equals("Start")){
            sendTextMessage("_The process has been launched_");
        }else{
            sendTextMessage("*The process has been stopped*");
        }

    }

    @Override
    public void onInitialize() {
        //TODO: y un poco más aquí :)

        addCommandHandler("start", this::startCommand);
        addCommandHandler("gpt", this::gptCommand);
        addCommandHandler("date", this::dateCommand);
        addCommandHandler("message", this::messageCommand);

        addMessageHandler(this::hello);
        //addButtonHandler("^.*", this::helloButton);
        addButtonHandler("^date_.*", this::dateButton);
        addButtonHandler("^message_.*", this::messageButton);

    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
