package com.gurula.talkyo.chatroom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gurula.talkyo.chatroom.dto.*;
import com.gurula.talkyo.member.Member;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface ChatroomService {

    String createChatroom(Member member, ChatroomDTO chatroomDTO);

    ConversationChainDTO reply(ChatDTO chatDTO, Member member) throws ExecutionException, InterruptedException, IOException;

    void genLearningReport(ChatDTO chatDTO) throws IOException, ExecutionException, InterruptedException;

    CompletableFuture<Void> speechToText(ChatRequestDTO chatRequestDTO) throws ExecutionException, InterruptedException;

    void grammarCheck(ChatRequestDTO chatRequestDTO) throws JsonProcessingException, ExecutionException, InterruptedException;

    void genAdvanceSentences(ChatRequestDTO chatRequestDTO) throws ExecutionException, InterruptedException, JsonProcessingException;

    void partnerReply(ChatRequestDTO chatRequestDTO) throws ExecutionException, InterruptedException, IOException;

    void loadScenario(ChatRequestDTO chatRequestDTO);

    String openingLine(ChatRequestDTO chatRequestDTO) throws IOException, ExecutionException, InterruptedException;

    void init(ChatInitDTO chatInitDTO, Member member) throws IOException, ExecutionException, InterruptedException;

    void analyzePronunciation(ChatRequestDTO chatRequestDTO) throws InterruptedException, JsonProcessingException, ExecutionException;

    Feedback feedback(ReportRequestDTO reportRequestDTO) throws JsonProcessingException;

    ConversationScore pronunciationResult(ReportRequestDTO reportRequestDTO);

    ContentAssessment topicResult(ReportRequestDTO reportRequestDTO);
}
