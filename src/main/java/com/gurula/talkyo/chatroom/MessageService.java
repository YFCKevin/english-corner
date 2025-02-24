package com.gurula.talkyo.chatroom;

import java.util.List;
import java.util.Map;

public interface MessageService {
    List<Map<Integer, Message>> getHistoryMessageWhenSwitchBranch(String previewMessageId, int targetVersion);

    List<Map<Integer, Message>> getHistoryMessages(String currentMessageId);
}
