package com.gurula.talkyo.chatroom.feature;

import com.gurula.talkyo.azureai.AudioService;
import com.gurula.talkyo.chatroom.AudioContent;
import com.gurula.talkyo.chatroom.Message;
import com.gurula.talkyo.chatroom.MessageContent;
import com.gurula.talkyo.chatroom.MessageRepository;
import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.dto.ConversationChainDTO;
import com.gurula.talkyo.chatroom.factory.AbstractConversation;
import com.gurula.talkyo.properties.ConfigProperties;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class SpeechToText implements ConversationFeature{
    private final AudioService audioService;
    private final MessageRepository messageRepository;
    private final ConfigProperties configProperties;

    public SpeechToText(AudioService audioService, MessageRepository messageRepository, ConfigProperties configProperties) {
        this.audioService = audioService;
        this.messageRepository = messageRepository;
        this.configProperties = configProperties;
    }

    @Override
    public <T extends AbstractConversation> ConversationChainDTO execute(T conversation, ChatRequestDTO chatRequestDTO) throws ExecutionException, InterruptedException, IOException {
        System.out.println("SpeechToText");

        final String messageId = chatRequestDTO.getMessageId();
        final Optional<Message> opt = messageRepository.findById(messageId);
        if (opt.isPresent()) {
            final Message message = opt.get();
            message.getMessageContents().stream()
                    .filter(content -> content instanceof AudioContent)
                    .map(content -> (AudioContent) content)
                    .findFirst()
                    .ifPresent(audioContent -> {
                        try {
                            final String recognitionText =
                                    audioService.speechToText(configProperties.getAudioSavePath() + audioContent.getAudioName());

                            AudioContent updatedAudioContent = new AudioContent(
                                    audioContent.getAudioName(),
                                    audioContent.getSize(),
                                    recognitionText
                            );

                            List<MessageContent> updatedContents = message.getMessageContents().stream()
                                    .map(content -> content == audioContent ? updatedAudioContent : content)
                                    .toList();

                            message.setMessageContents(updatedContents);

                            messageRepository.save(message);
                        } catch (ExecutionException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        return null;
    }
}
