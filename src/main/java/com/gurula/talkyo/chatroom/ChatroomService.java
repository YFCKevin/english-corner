package com.gurula.talkyo.chatroom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gurula.talkyo.chatroom.dto.*;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.openai.dto.LLMChatResponseDTO;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface ChatroomService {

    String createChatroom(Member member, ChatroomDTO chatroomDTO);

    ConversationChainDTO reply(ChatDTO chatDTO, Member member) throws ExecutionException, InterruptedException, IOException;

    void genLearningReport(String chatroomId) throws IOException, ExecutionException, InterruptedException;

    String speechToText(Message message) throws ExecutionException, InterruptedException, IOException;

    CompletableFuture<Void> advancedCheck(ChatRequestDTO chatRequestDTO) throws IOException, ExecutionException, InterruptedException;

    void grammarCheck(ChatRequestDTO chatRequestDTO) throws JsonProcessingException, ExecutionException, InterruptedException;

    void genAdvanceSentences(ChatRequestDTO chatRequestDTO) throws ExecutionException, InterruptedException, JsonProcessingException;

    Message partnerReply(ChatRequestDTO chatRequestDTO) throws ExecutionException, InterruptedException, IOException;

    void loadScenario(ChatRequestDTO chatRequestDTO);

    String openingLine(ChatRequestDTO chatRequestDTO) throws IOException, ExecutionException, InterruptedException;

    List<Map<Integer, Message>> init(ChatInitDTO chatInitDTO, Member member) throws IOException, ExecutionException, InterruptedException;

    void analyzePronunciation(ChatRequestDTO chatRequestDTO) throws InterruptedException, JsonProcessingException, ExecutionException;

    Feedback feedback(ReportRequestDTO reportRequestDTO) throws JsonProcessingException;

    ConversationScore pronunciationResult(ReportRequestDTO reportRequestDTO);

    ContentAssessment topicResult(ReportRequestDTO reportRequestDTO);

    void close(String chatroomId);

    ConversationChainDTO handleHumanMsg(ChatDTO chatDTO, Member member) throws IOException, ExecutionException, InterruptedException;

    LearningReport getLearningReport(String chatroomId);

    LLMChatResponseDTO genGuidingSentence(String messageId) throws JsonProcessingException;
}
