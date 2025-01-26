package com.gurula.talkyo.chatroom.feature;

import com.gurula.talkyo.azureai.AudioService;
import com.gurula.talkyo.azureai.dto.ChatAudioDTO;
import com.gurula.talkyo.chatroom.*;
import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.dto.ConversationChainDTO;
import com.gurula.talkyo.chatroom.enums.SenderRole;
import com.gurula.talkyo.chatroom.factory.AbstractConversation;
import com.gurula.talkyo.course.Lesson;
import com.gurula.talkyo.course.LessonRepository;
import com.gurula.talkyo.course.Sentence;
import com.gurula.talkyo.openai.LLMService;
import com.gurula.talkyo.openai.dto.LLMChatRequestDTO;
import com.gurula.talkyo.openai.dto.LLMChatResponseDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class AIReplyMessage implements ConversationFeature{
    private final LessonRepository lessonRepository;
    private final LLMService llmService;
    private final MessageRepository messageRepository;
    private final AudioService audioService;
    private final SimpleDateFormat sdf;
    private final ConversationRepository conversationRepository;

    public AIReplyMessage(LessonRepository lessonRepository, LLMService llmService, MessageRepository messageRepository, AudioService audioService, SimpleDateFormat sdf,
                          ConversationRepository conversationRepository) {
        this.lessonRepository = lessonRepository;
        this.llmService = llmService;
        this.messageRepository = messageRepository;
        this.audioService = audioService;
        this.sdf = sdf;
        this.conversationRepository = conversationRepository;
    }

    @Override
    public <T extends AbstractConversation> ConversationChainDTO execute(T conversation, ChatRequestDTO chatRequestDTO) throws ExecutionException, InterruptedException, IOException {
        System.out.println("AIReplyMessage");

        final String conversationId = chatRequestDTO.getConversationId();
        final Scenario scenario = conversationRepository.findById(conversationId).get().getScenario();

        LLMChatResponseDTO llmChatResponseDTO = new LLMChatResponseDTO();

        final Message oldMessage = messageRepository.findById(chatRequestDTO.getMessageId()).get();
        List<Message> messages = messageRepository.findAllByConversationIdOrderByCreatedDateTimeAsc(oldMessage.getConversationId());

        final String historyMessages = messages.stream()
                .map(message -> message.getMessageContents().stream()
                        .filter(content -> content instanceof AudioContent)
                        .map(content -> ((AudioContent) content).getParsedText())
                        .findFirst()
                        .orElseGet(() -> message.getMessageContents().stream()
                                .filter(content -> content instanceof TextContent)
                                .map(content -> ((TextContent) content).getText())
                                .findFirst()
                                .orElse(null)))
                .collect(Collectors.joining("\n"));

        if (StringUtils.isNotBlank(chatRequestDTO.getLessonId())) {
            final Optional<Lesson> opt = lessonRepository.findById(chatRequestDTO.getLessonId());
            if (opt.isPresent()) {
                final Lesson lesson = opt.get();
//                final String sentenceStr = lesson.getSentences().stream()
//                        .map(Sentence::getContent)
//                        .collect(Collectors.joining("\n"));
                llmChatResponseDTO = llmService.replyMsg(new LLMChatRequestDTO(historyMessages, scenario));
            }
        } else {
            llmChatResponseDTO = llmService.replyMsg(new LLMChatRequestDTO(historyMessages, scenario));
        }

        ConversationChainDTO dto = new ConversationChainDTO();
        final Message message = new Message();
        message.setConversationId(conversationId);
        message.setSenderContent(new AISender(llmChatResponseDTO.getTranslation()));
        message.setCreatedDateTime(sdf.format(new Date()));
        message.setSender(chatRequestDTO.getPartnerId());
        final String fileName = audioService.textToSpeechInChatting (List.of(new ChatAudioDTO(llmChatResponseDTO.getContent(), chatRequestDTO.getMemberId(), chatRequestDTO.getPartnerId(), conversationId))).get(0).getFileName().toString();
        message.setMessageContents(List.of(new AudioContent(fileName, Files.size(Paths.get(fileName)), llmChatResponseDTO.getContent())));
        messageRepository.save(message);
        dto.setMessage(message);
        return dto;
    }
}
