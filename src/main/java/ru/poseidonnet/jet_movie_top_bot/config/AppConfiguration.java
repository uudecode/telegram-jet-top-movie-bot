package ru.poseidonnet.jet_movie_top_bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.poseidonnet.jet_movie_top_bot.bot.JetTopMovieBot;

@Configuration
public class AppConfiguration {
    @Value("${telegram.bot.proxy.host:}")
    private String proxyHost;

    @Value("${telegram.bot.proxy.port:0}")
    private int proxyPort;

    @Value("${telegram.bot.proxy.type:HTTP}")
    private String proxyType;

    @Bean
    public DefaultBotOptions defaultBotOptions() {
        DefaultBotOptions options = new DefaultBotOptions();

        if (proxyHost != null && !proxyHost.isEmpty()) {
            options.setProxyHost(proxyHost);
            options.setProxyPort(proxyPort);
            // Превращаем строку из конфига (HTTP/SOCKS5) в Enum
            options.setProxyType(DefaultBotOptions.ProxyType.valueOf(proxyType.toUpperCase()));
        }
        return options;
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(JetTopMovieBot jetTopMovieBot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(jetTopMovieBot);
        return botsApi;
    }

}
