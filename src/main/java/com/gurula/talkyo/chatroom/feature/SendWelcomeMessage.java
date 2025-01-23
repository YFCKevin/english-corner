package com.gurula.talkyo.chatroom.feature;

import com.gurula.talkyo.azureai.AudioService;
import com.gurula.talkyo.azureai.dto.ChatAudioDTO;
import com.gurula.talkyo.chatroom.Message;
import com.gurula.talkyo.chatroom.MessageRepository;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class SendWelcomeMessage implements ConversationFeature{
    private final AudioService audioService;
    private final LLMService llmService;
    private final MessageRepository messageRepository;
    private final LessonRepository lessonRepository;
    private final SimpleDateFormat sdf;

    public SendWelcomeMessage(AudioService audioService, LLMService llmService, MessageRepository messageRepository, LessonRepository lessonRepository, @Qualifier("sdf") SimpleDateFormat sdf) {
        this.audioService = audioService;
        this.llmService = llmService;
        this.messageRepository = messageRepository;
        this.lessonRepository = lessonRepository;
        this.sdf = sdf;
    }

    @Override
    public ConversationChainDTO execute(AbstractConversation conversation, ChatRequestDTO chatRequestDTO) throws ExecutionException, InterruptedException {
        System.out.println("SendWelcomeMessage");

        LLMChatResponseDTO llmChatResponseDTO = new LLMChatResponseDTO();

        if (StringUtils.isNotBlank(chatRequestDTO.getLessonId())) {
            final Optional<Lesson> opt = lessonRepository.findById(chatRequestDTO.getLessonId());
            if (opt.isPresent()) {
                final Lesson lesson = opt.get();
                final String sentenceStr = lesson.getSentences().stream()
                        .map(Sentence::getContent)
                        .collect(Collectors.joining("\n"));
                llmChatResponseDTO = llmService.genWelcomeMessage(new LLMChatRequestDTO(chatRequestDTO.getScenario(), sentenceStr));
            }
        } else {
            llmChatResponseDTO = llmService.genWelcomeMessage(new LLMChatRequestDTO(chatRequestDTO.getScenario()));
        }

        ConversationChainDTO dto = new ConversationChainDTO();
        final Message message = new Message();
        message.setConversationId(chatRequestDTO.getConversationId());
        message.setSenderRole(SenderRole.AI);
        message.setCreatedDateTime(sdf.format(new Date()));
        message.setSender(chatRequestDTO.getPartnerId());
        message.setTranslation(llmChatResponseDTO.getTranslation());
        message.setContent(llmChatResponseDTO.getContent());
        message.setAudioPath(audioService.genChattingAudioFile(new ChatAudioDTO(llmChatResponseDTO.getContent(), chatRequestDTO.getMemberId(), chatRequestDTO.getPartnerId(), chatRequestDTO.getConversationId())));
        messageRepository.save(message);
        dto.setMessage(message);
        return dto;
    }
}
