package com.music.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.music.project.config.SpotifyConfig;

@SpringBootApplication
@EnableConfigurationProperties(SpotifyConfig.class)
public class Main
{
    public static void main( String[] args )
    {
        SpringApplication.run(Main.class, args);
    }
}