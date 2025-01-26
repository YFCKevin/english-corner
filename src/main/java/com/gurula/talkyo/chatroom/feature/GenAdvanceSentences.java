package com.gurula.talkyo.chatroom.feature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gurula.talkyo.azureai.AudioService;
import com.gurula.talkyo.azureai.dto.ChatAudioDTO;
import com.gurula.talkyo.chatroom.*;
import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.dto.ConversationChainDTO;
import com.gurula.talkyo.chatroom.factory.AbstractConversation;
import com.gurula.talkyo.course.Sentence;
import com.gurula.talkyo.course.SentenceRepository;
import com.gurula.talkyo.openai.LLMService;
import com.gurula.talkyo.openai.dto.AdvanceSentencesResponseDTO;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collector;

@Component
public class GenAdvanceSentences implements ConversationFeature{

    private final MessageRepository messageRepository;
    private final LLMService llmService;
    private final SentenceRepository sentenceRepository;
    private final AudioService audioService;

    public GenAdvanceSentences(MessageRepository messageRepository, LLMService llmService,
                               SentenceRepository sentenceRepository, AudioService audioService) {
        this.messageRepository = messageRepository;
        this.llmService = llmService;
        this.sentenceRepository = sentenceRepository;
        this.audioService = audioService;
    }

    @Override
    public <T extends AbstractConversation> ConversationChainDTO execute(T conversation, ChatRequestDTO chatRequestDTO) throws JsonProcessingException, ExecutionException, InterruptedException {
        System.out.println("GenAdvanceSentences");

        final String messageId = chatRequestDTO.getMessageId();
        final String partnerId = chatRequestDTO.getPartnerId();
        final String conversationId = chatRequestDTO.getConversationId();

        final Optional<Message> opt = messageRepository.findById(messageId);
        if (opt.isPresent()) {
            final Message message = opt.get();
            final HumanSender human = (HumanSender) message.getSenderContent();
            List<AdvanceSentencesResponseDTO> advanceSentencesResponseDTOS = llmService.advanceSentences(human.getGrammarResult().getCorrectSentence())
                    .stream()
                    // 非正式的句子排在前面
                    .sorted(Comparator.comparing(AdvanceSentencesResponseDTO::isFormal))
                    .toList();
            final List<Path> audioFilePaths = audioService.textToSpeechInChatting(Arrays.asList(
                    new ChatAudioDTO(advanceSentencesResponseDTOS.get(0).getSentence(), partnerId, conversationId),
                    new ChatAudioDTO(advanceSentencesResponseDTOS.get(1).getSentence(), partnerId, conversationId)
            ));
            Map<String, Path> audioMap = Map.of("informal", audioFilePaths.get(0), "formal", audioFilePaths.get(1));
            List<Sentence> sentences = new ArrayList<>();
            advanceSentencesResponseDTOS.forEach(s -> {
                Sentence sentence = new Sentence();
                sentence.setExplanation(s.getExplanation());
                sentence.setContent(s.getSentence());
                sentence.setFormal(s.isFormal());
                if (s.isFormal()) { // formal
                    sentence.setAudioName(List.of(audioMap.get("formal").getFileName().toString()));
                } else {    // informal
                    sentence.setAudioName(List.of(audioMap.get("informal").getFileName().toString()));
                }
                sentences.add(sentence);
            });
            final List<Sentence> savedSentences = sentenceRepository.saveAll(sentences);
            message.setSenderContent(new HumanSender(human.getGrammarResult(), savedSentences.stream().map(Sentence::getId).toList()));
            messageRepository.save(message);
        }

        return null;
    }
}
