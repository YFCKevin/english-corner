package com.gurula.talkyo.chatroom.feature;

import com.gurula.talkyo.azureai.AudioService;
import com.gurula.talkyo.chatroom.*;
import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.dto.ConversationChainDTO;
import com.gurula.talkyo.chatroom.enums.SenderRole;
import com.gurula.talkyo.chatroom.factory.AbstractConversation;
import com.gurula.talkyo.chatroom.utils.AudioUtil;
import com.gurula.talkyo.openai.LLMService;
import com.gurula.talkyo.properties.ConfigProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class GenLearningReport implements ConversationFeature{

    private final MessageRepository messageRepository;
    private final ConfigProperties configProperties;
    private final AudioService audioService;
    private final ConversationRepository conversationRepository;
    private final LLMService llmService;

    public GenLearningReport(MessageRepository messageRepository, ConfigProperties configProperties, AudioService audioService,
                             ConversationRepository conversationRepository, LLMService llmService) {
        this.messageRepository = messageRepository;
        this.configProperties = configProperties;
        this.audioService = audioService;
        this.conversationRepository = conversationRepository;
        this.llmService = llmService;
    }

    @Override
    public <T extends AbstractConversation> ConversationChainDTO execute(T conversation, ChatRequestDTO chatRequestDTO) throws IOException {
        System.out.println("GenLearningReport");

        final String conversationId = chatRequestDTO.getConversationId();
        final List<Message> messages = messageRepository.findAllByConversationIdOrderByCreatedDateTimeAsc(conversationId);

        // 組裝 reference text
        final String referenceText = messages.stream()
                .sorted(Comparator.comparing(Message::getCreatedDateTime))
                .flatMap(message -> message.getMessageContents().stream()
                        .filter(content -> content instanceof AudioContent)
                        .map(content -> ((AudioContent) content).getParsedText())
                )
                .collect(Collectors.joining("\n"));

        // 取得使用者的語音對話檔
        final List<String> audioFilePaths = messages.stream()
                .filter(message -> message.getSenderContent().getSenderRole().equals(SenderRole.HUMAN) &&
                        message.getMessageContents().stream()
                                .anyMatch(content -> content instanceof AudioContent)
                )
                .flatMap(message -> message.getMessageContents().stream()
                        .filter(content -> content instanceof AudioContent)
                        .map(content -> configProperties.getAudioSavePath() + ((AudioContent) content).getAudioName())
                )
                .toList();

        final String destinationFilePath = configProperties.getAudioSavePath() + conversationId + "_merge.wav";
        AudioUtil.mergeAudioFiles(destinationFilePath, audioFilePaths);

        final ConversationScore conversationScore = audioService.analyzeMultipleAudioFiles(referenceText, destinationFilePath);

        final String dialogueText = messages.stream()
                .sorted(Comparator.comparing(Message::getCreatedDateTime))
                .flatMap(message -> message.getMessageContents().stream()
                        .filter(content -> content instanceof AudioContent)
                        .map(content -> {
                            String prefix = "";
                            if (message.getSenderContent() instanceof AISender) {
                                prefix = "partner: ";
                            } else if (message.getSenderContent() instanceof HumanSender) {
                                prefix = "user: ";
                            }
                            return prefix + ((AudioContent) content).getParsedText();
                        })
                )
                .collect(Collectors.joining("\n"));

        Feedback feedback = llmService.feedback(dialogueText);

        final Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);
        if (conversationOpt.isPresent()) {
            final Conversation conversationInDB = conversationOpt.get();
            conversationInDB.setReport(new LearningReport(conversationScore, feedback));
            conversationRepository.save(conversationInDB);
        }

        return null;
    }
}
