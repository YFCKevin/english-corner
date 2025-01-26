package com.gurula.talkyo.chatroom;

import com.gurula.talkyo.chatroom.enums.SenderRole;

import java.util.List;

public class HumanSender implements MessageSender{
    private boolean accuracy;
    private GrammarResult grammarResult;
    private ConversationScore conversationScore;
    private List<String> advancedSentences;
    private final SenderRole senderRole = SenderRole.HUMAN;

    public HumanSender() {
    }

    public HumanSender(GrammarResult grammarResult, ConversationScore conversationScore) {
        this.grammarResult = grammarResult;
        this.conversationScore = conversationScore;
    }

    public HumanSender(GrammarResult grammarResult, List<String> advancedSentences) {
        this.grammarResult = grammarResult;
        this.advancedSentences = advancedSentences;
    }

    public HumanSender(GrammarResult grammarResult) {
        this.grammarResult = grammarResult;
    }

    public HumanSender(ConversationScore conversationScore) {
        this.conversationScore = conversationScore;
    }

    public HumanSender(List<String> advancedSentences) {
        this.advancedSentences = advancedSentences;
    }

    @Override
    public SenderRole getSenderRole() {
        return this.senderRole;
    }

    public boolean isAccuracy() {
        return accuracy;
    }

    public GrammarResult getGrammarResult() {
        return grammarResult;
    }

    public ConversationScore getConversationScore() {
        return conversationScore;
    }

    public List<String> getAdvancedSentences() {
        return advancedSentences;
    }
}
