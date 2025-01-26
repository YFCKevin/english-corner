package com.gurula.talkyo.azureai;

import com.gurula.talkyo.azureai.dto.ChatAudioDTO;
import com.gurula.talkyo.chatroom.ContentAssessment;
import com.gurula.talkyo.chatroom.ConversationScore;
import com.gurula.talkyo.properties.AzureProperties;
import com.gurula.talkyo.properties.ConfigProperties;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class AudioService {
    private final Logger logger = LoggerFactory.getLogger(AudioService.class);
    private final AzureProperties azureProperties;
    private final ConfigProperties configProperties;
    private final PartnerRepository partnerRepository;

    public AudioService(AzureProperties azureProperties, ConfigProperties configProperties, PartnerRepository partnerRepository) {
        this.azureProperties = azureProperties;
        this.configProperties = configProperties;
        this.partnerRepository = partnerRepository;
    }

    public List<Path> speechSynthesis(String content, String unitNumber) throws ExecutionException, InterruptedException {
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(
                azureProperties.getAudio().getKey(),
                azureProperties.getAudio().getRegion()
        );

        final Path maleAudioPath = generateSpeechFile(speechConfig, content, unitNumber, "en-US-BrianMultilingualNeural", "_en-US-BrianMultilingualNeural.wav");

        final Path femaleAudioPath = generateSpeechFile(speechConfig, content, unitNumber, "en-US-PhoebeMultilingualNeural", "_en-US-PhoebeMultilingualNeural.wav");

        speechConfig.close();

        return Arrays.asList(maleAudioPath, femaleAudioPath);
    }

    public Path generateSpeechFile(SpeechConfig speechConfig, String content, String unitNumber, String voiceName, String suffix)
            throws ExecutionException, InterruptedException {
        speechConfig.setSpeechSynthesisVoiceName(voiceName);
        speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Riff16Khz16BitMonoPcm);

        Path filePath = Paths.get(configProperties.getAudioSavePath(), unitNumber + suffix);
        AudioConfig audioConfig = AudioConfig.fromWavFileOutput(filePath.toString());
        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig, audioConfig);

        SpeechSynthesisResult result = speechSynthesizer.SpeakTextAsync(content).get();

        if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
            logger.info("Speech synthesized to file [" + filePath + "] using voice [" + voiceName + "]");
            return filePath;
        } else if (result.getReason() == ResultReason.Canceled) {
            SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(result);
           logger.error("CANCELED: Reason=" + cancellation.getReason());

            if (cancellation.getReason() == CancellationReason.Error) {
                logger.error("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                logger.error("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
            }
        }

        speechSynthesizer.close();
        audioConfig.close();

        return filePath;
    }

    public List<Path> textToSpeechInChatting(List<ChatAudioDTO> chatAudioDTOList) throws ExecutionException, InterruptedException {

        if (chatAudioDTOList == null || chatAudioDTOList.isEmpty()) {
            return Collections.emptyList();
        }

        ChatAudioDTO sampleDTO = chatAudioDTOList.get(0);
        Optional<Partner> opt = partnerRepository.findById(sampleDTO.getPartnerId());

        if (opt.isPresent()) {
            Partner partner = opt.get();

            SpeechConfig speechConfig = SpeechConfig.fromSubscription(
                    azureProperties.getAudio().getKey(),
                    azureProperties.getAudio().getRegion()
            );

            speechConfig.setSpeechSynthesisVoiceName(partner.getShortName());
            speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Riff16Khz16BitMonoPcm);

            List<Path> audioPaths = new ArrayList<>();

            try {
                for (ChatAudioDTO chatAudioDTO : chatAudioDTOList) {
                    String suffix = "_" + partner.getShortName() + "_" + System.currentTimeMillis() + ".wav";
                    Path filePath = Paths.get(configProperties.getAudioSavePath(), chatAudioDTO.getConversationId() + suffix);

                    AudioConfig audioConfig = AudioConfig.fromWavFileOutput(filePath.toString());

                    try (audioConfig; SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig, audioConfig)) {
                        SpeechSynthesisResult result = speechSynthesizer.SpeakTextAsync(chatAudioDTO.getContent()).get();

                        if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                            logger.info("Speech synthesized to file [" + filePath + "] using voice [" + partner.getShortName() + "]");
                            audioPaths.add(filePath);
                        } else if (result.getReason() == ResultReason.Canceled) {
                            SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(result);
                            logger.error("CANCELED: Reason=" + cancellation.getReason());

                            if (cancellation.getReason() == CancellationReason.Error) {
                                logger.error("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                                logger.error("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                            }
                        }
                    }
                }
            } finally {
                speechConfig.close();
            }

            return audioPaths;
        } else {
            logger.warn("Partner not found for ID: " + sampleDTO.getPartnerId());
            return Collections.emptyList();
        }
    }



    public ConversationScore analyzeMultipleAudioFiles(String referenceText, String audioFilePath) {
        try {
            // 創建 SpeechConfig
            SpeechConfig speechConfig = SpeechConfig.fromSubscription(azureProperties.getAudio().getKey(), azureProperties.getAudio().getRegion());

            // 設置發音評估參數
            PronunciationAssessmentConfig pronunciationConfig = new PronunciationAssessmentConfig(
                    referenceText,
                    PronunciationAssessmentGradingSystem.HundredMark,  // 100分制
                    PronunciationAssessmentGranularity.Word           // 以"單字"為單位
            );

            // 開啟內容分析 (與主題相關) 和 韻律分析 (Prosody)
            pronunciationConfig.enableContentAssessmentWithTopic("general");
            pronunciationConfig.enableProsodyAssessment();          // 韻律分析

            // 創建 AudioConfig
            AudioConfig audioConfig = AudioConfig.fromWavFileInput(audioFilePath);

            // 創建 SpeechRecognizer，並使用音檔作為輸入
            SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig);

            // 設置發音評估
            pronunciationConfig.applyTo(recognizer);

            // 使用 recognizeOnceAsync 進行語音識別及評估
            final com.microsoft.cognitiveservices.speech.SpeechRecognitionResult result = recognizer.recognizeOnceAsync().get();

            // 結果
            if (result.getReason() == ResultReason.RecognizedSpeech) {
                System.out.println("識別結果: " + result.getText());

                // 發音評估結果
                PronunciationAssessmentResult assessmentResult = PronunciationAssessmentResult.fromResult(result);
                System.out.println("發音準確度 (Accuracy): " + assessmentResult.getAccuracyScore());
                System.out.println("流利度 (Fluency): " + assessmentResult.getFluencyScore());
                System.out.println("完整性 (Completeness): " + assessmentResult.getCompletenessScore());
                System.out.println("韻律 (Prosody): " + assessmentResult.getProsodyScore());
                System.out.println("文法評分: " + assessmentResult.getContentAssessmentResult().getGrammarScore());
                System.out.println("主題評分: " + assessmentResult.getContentAssessmentResult().getTopicScore());
                System.out.println("單字評分: " + assessmentResult.getContentAssessmentResult().getVocabularyScore());

                // 結果放入 DTO
                ContentAssessment contentAssessment = new ContentAssessment(
                        assessmentResult.getContentAssessmentResult().getVocabularyScore(),
                        assessmentResult.getContentAssessmentResult().getGrammarScore(),
                        assessmentResult.getContentAssessmentResult().getTopicScore()
                );
                ConversationScore conversationScore = new ConversationScore(
                        assessmentResult.getFluencyScore(),
                        assessmentResult.getAccuracyScore(),
                        assessmentResult.getCompletenessScore(),
                        assessmentResult.getProsodyScore(),
                        contentAssessment
                );

                // 顯示錯誤分析結果（如果有錯誤）
                String jsonResult = result.getProperties().getProperty(PropertyId.SpeechServiceResponse_JsonResult);

                // 完整 JSON 結果
                System.out.println(jsonResult);

                return conversationScore;

            } else {
                System.out.println("語音識別失敗: " + result.getReason());
            }

            // 關閉資源
            recognizer.close();
            speechConfig.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }



    public String speechToText (String audioFilePath) throws ExecutionException, InterruptedException {

        SpeechConfig speechConfig = SpeechConfig.fromSubscription(azureProperties.getAudio().getKey(), azureProperties.getAudio().getRegion());
        speechConfig.setSpeechRecognitionLanguage("en-US");

        AudioConfig audioConfig = AudioConfig.fromWavFileInput(audioFilePath);
        SpeechRecognizer speechRecognizer = new SpeechRecognizer(speechConfig, audioConfig);

        final Future<SpeechRecognitionResult> task = speechRecognizer.recognizeOnceAsync();
        final com.microsoft.cognitiveservices.speech.SpeechRecognitionResult speechRecognitionResult = task.get();

        if (speechRecognitionResult.getReason() == ResultReason.RecognizedSpeech) {
            final String resultText = speechRecognitionResult.getText();
            logger.info("RECOGNIZED: Text=" + resultText);
            return resultText;
        }
        else if (speechRecognitionResult.getReason() == ResultReason.NoMatch) {
            logger.info("NOMATCH: Speech could not be recognized.");
        }
        else if (speechRecognitionResult.getReason() == ResultReason.Canceled) {
            CancellationDetails cancellation = CancellationDetails.fromResult(speechRecognitionResult);
            logger.info("CANCELED: Reason=" + cancellation.getReason());

            if (cancellation.getReason() == CancellationReason.Error) {
                logger.info("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                logger.info("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                logger.info("CANCELED: Did you set the speech resource key and region values?");
            }
        }
        return "";
    }
}
