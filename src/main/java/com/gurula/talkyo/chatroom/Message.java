package com.gurula.talkyo.chatroom;

import com.gurula.talkyo.chatroom.enums.MessageType;
import com.gurula.talkyo.chatroom.enums.SenderRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "message")
@CompoundIndex(name = "chatroomId_createdDateTime", def = "{'chatroomId': 1, 'createdDateTime': -1}")
@CompoundIndex(name = "branch_createdDateTime", def = "{'branch': 1, 'createdDateTime': -1}")
public class Message {
    @Id
    private String id;
    private String chatroomId;
    private String sender;  // memberId or partnerId
    private String createdDateTime;
    private String updatedDateTime;
    private boolean accuracy;
    private SenderRole senderRole;
    private String previewMessageId;    // 指向回覆的訊息編號
    private String branch; // 分支號碼 (UUID)
    private int version;    // 訊息編輯次數 (從 1 開始)

    // human
    private GrammarResult grammarResult;
    private List<AdvancedSentence> advancedSentences;
    private ConversationScore conversationScore;

    // partner
    private String translation;

    // text
    private String text;

    // image
    private String imageName;

    // audio
    private String audioName;
    private String parsedText;
    private long size;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(String updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public boolean isAccuracy() {
        return accuracy;
    }

    public void setAccuracy(boolean accuracy) {
        this.accuracy = accuracy;
    }

    public SenderRole getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(SenderRole senderRole) {
        this.senderRole = senderRole;
    }

    public GrammarResult getGrammarResult() {
        return grammarResult;
    }

    public void setGrammarResult(GrammarResult grammarResult) {
        this.grammarResult = grammarResult;
    }

    public List<AdvancedSentence> getAdvancedSentences() {
        return advancedSentences;
    }

    public void setAdvancedSentences(List<AdvancedSentence> advancedSentences) {
        this.advancedSentences = advancedSentences;
    }

    public ConversationScore getConversationScore() {
        return conversationScore;
    }

    public void setConversationScore(ConversationScore conversationScore) {
        this.conversationScore = conversationScore;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public String getParsedText() {
        return parsedText;
    }

    public void setParsedText(String parsedText) {
        this.parsedText = parsedText;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPreviewMessageId() {
        return previewMessageId;
    }

    public void setPreviewMessageId(String previewMessageId) {
        this.previewMessageId = previewMessageId;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", createdDateTime='" + createdDateTime + '\'' +
                ", senderRole=" + senderRole +
                ", previewMessageId='" + previewMessageId + '\'' +
                ", branch=" + branch +
                ", version=" + version +
                '}';
    }
}
