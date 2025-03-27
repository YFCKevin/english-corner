package com.gurula.talkyo.azureai;

import com.gurula.talkyo.azureai.dto.ChatAudioDTO;
import com.gurula.talkyo.chatroom.ContentAssessment;
import com.gurula.talkyo.chatroom.ConversationScore;
import com.gurula.talkyo.chatroom.DisplayWord;
import com.gurula.talkyo.properties.AzureProperties;
import com.gurula.talkyo.properties.ConfigProperties;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CountDownLatch;
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

    public Path speechSynthesis(String courseTopic, String lessonNumber, String content, String sentenceUnitNumber, String shortName) throws ExecutionException, InterruptedException, IOException {
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(
                azureProperties.getAudio().getKey(),
                azureProperties.getAudio().getRegion()
        );

        final Path audioPath = generateSpeechFile(
                speechConfig,
                content,
                sentenceUnitNumber,
                courseTopic,
                lessonNumber,
                shortName,
                "_" + shortName + ".wav"
        );
        Thread.sleep(3000);

        speechConfig.close();

        return audioPath;
    }

    public Path generateSpeechFile(SpeechConfig speechConfig, String content, String sentenceUnitNumber, String courseTopic, String lessonNumber, String voiceName, String suffix)
            throws ExecutionException, InterruptedException, IOException {
        speechConfig.setSpeechSynthesisVoiceName(voiceName);
        speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Riff16Khz16BitMonoPcm);

        // 存放位置：/audio/{course topic}/{lesson unitNumber}/{sentence unitNumber_partner voice.wav}
        Path filePath = Paths.get(configProperties.getAudioSavePath(), courseTopic, lessonNumber, sentenceUnitNumber + suffix);
        // 檢查並創建資料夾
        Files.createDirectories(filePath.getParent());

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
                    String suffix = partner.getShortName() + "_" + System.currentTimeMillis() + ".wav";
                    // 存放位置：/audio/{chatroomId}/{partner shortName_timestamp.wav}
                    Path filePath = Paths.get(configProperties.getAudioSavePath(), chatAudioDTO.getChatroomId(), suffix);
                    // 檢查並創建資料夾
                    Files.createDirectories(filePath.getParent());

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
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                speechConfig.close();
            }
            return audioPaths;
        } else {
            logger.warn("Partner not found for ID: " + sampleDTO.getPartnerId());
            return Collections.emptyList();
        }
    }



    // 文字轉語音 (直接播放不存檔)
    public String textToSpeechAndPlaySound(ChatAudioDTO chatAudioDTO) throws ExecutionException, InterruptedException {

        final String partnerId = chatAudioDTO.getPartnerId();
        final String content = chatAudioDTO.getContent();
        final String unitNumber = chatAudioDTO.getUnitNumber();

        if (content == null || content.isEmpty()) {
            logger.warn("Text is empty, skipping speech synthesis.");
            return null;
        }

        Optional<Partner> opt = partnerRepository.findById(partnerId);
        if (opt.isEmpty()) {
            logger.warn("Partner not found for ID: " + partnerId);
            return null;
        }

        Partner partner = opt.get();

        SpeechConfig speechConfig = SpeechConfig.fromSubscription(
                azureProperties.getAudio().getKey(),
                azureProperties.getAudio().getRegion()
        );

        // 設定語音
        speechConfig.setSpeechSynthesisVoiceName(partner.getShortName());

        // 產生時間戳檔名
        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileName = timestamp + ".wav";
        String filePath;
        if (StringUtils.isBlank(unitNumber)) {
            filePath = configProperties.getAudioSavePath() + "temp/" + fileName;
        } else {    // 會員收藏的句子音檔路徑
            filePath = configProperties.getAudioSavePath() + "favorite/" + fileName;
            System.out.println("filePath = " + filePath);
        }

        // 存檔
        try (AudioConfig audioConfig = AudioConfig.fromWavFileOutput(filePath);
             SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig, audioConfig)) {

            SpeechSynthesisResult result = speechSynthesizer.SpeakTextAsync(content).get();

            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                logger.info("Speech successfully saved to: " + filePath);
                return fileName; // 回傳檔案名稱
            } else if (result.getReason() == ResultReason.Canceled) {
                SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(result);
                logger.error("CANCELED: Reason=" + cancellation.getReason());

                if (cancellation.getReason() == CancellationReason.Error) {
                    logger.error("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                    logger.error("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            speechConfig.close();
        }

        return null; // 失敗時回傳 null
    }




    public ConversationScore pronunciation(String referenceText, String audioFilePath) {
        try {
            // 創建 SpeechConfig
            SpeechConfig speechConfig = SpeechConfig.fromSubscription(azureProperties.getAudio().getKey(), azureProperties.getAudio().getRegion());

            // 設置發音評估參數
            PronunciationAssessmentConfig pronunciationConfig = new PronunciationAssessmentConfig(
                    referenceText,
                    PronunciationAssessmentGradingSystem.HundredMark,  // 100分制
                    PronunciationAssessmentGranularity.Word           // 以"單字"為單位
            );

            // 開啟韻律分析 (Prosody)
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

                // 顯示錯誤分析結果（如果有錯誤）
                String jsonResult = result.getProperties().getProperty(PropertyId.SpeechServiceResponse_JsonResult);
                final List<DisplayWord> displayWords = constructWords(jsonResult);

                // 完整 JSON 結果
                System.out.println("pronunciation: "+jsonResult);

                // 結果放入 DTO
                return new ConversationScore(
                        assessmentResult.getFluencyScore(),
                        assessmentResult.getAccuracyScore(),
                        assessmentResult.getCompletenessScore(),
                        assessmentResult.getProsodyScore(),
                        result.getText(),
                        displayWords
                );

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

    private static List<DisplayWord> constructWords(String jsonResult) {
        List<DisplayWord> displayWords = new ArrayList<>();
        JSONObject root = new JSONObject(jsonResult);
        JSONArray nBestArray = root.getJSONArray("NBest");
        if (nBestArray.length() > 0) {
            JSONObject firstResult = nBestArray.getJSONObject(0);
            JSONArray wordsArray = firstResult.getJSONArray("Words");
            for (int i = 0; i < wordsArray.length(); i++) {
                JSONObject wordObj = wordsArray.getJSONObject(i);
                String word = wordObj.getString("Word");
                JSONObject paObj = wordObj.getJSONObject("PronunciationAssessment");
                double accuracyScore = paObj.getDouble("AccuracyScore");
                String errorType = paObj.getString("ErrorType");
                DisplayWord displayWord = new DisplayWord(word, accuracyScore, errorType);
                displayWords.add(displayWord);
            }
        }
        return displayWords;
    }


    public ContentAssessment analyzeTopic(String referenceText, String audioFilePath) {
        try {
            // 創建 SpeechConfig
            SpeechConfig speechConfig = SpeechConfig.fromSubscription(
                    azureProperties.getAudio().getKey(),
                    azureProperties.getAudio().getRegion()
            );

            // 設置內容分析參數
            PronunciationAssessmentConfig contentConfig = new PronunciationAssessmentConfig(
                    referenceText,
                    PronunciationAssessmentGradingSystem.HundredMark,  // 100分制
                    PronunciationAssessmentGranularity.FullText        // 分析整段文本
            );
            contentConfig.enableContentAssessmentWithTopic("general");  // 啟用主題相關性分析

            // 創建 AudioConfig
            AudioConfig audioConfig = AudioConfig.fromWavFileInput(audioFilePath);

            // 創建 SpeechRecognizer
            SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig);

            // 應用內容評估設置
            contentConfig.applyTo(recognizer);

            // 使用 recognizeOnceAsync 進行語音識別及評估
            final com.microsoft.cognitiveservices.speech.SpeechRecognitionResult result = recognizer.recognizeOnceAsync().get();

            // 完整 JSON 結果
            String jsonResult = result.getProperties().getProperty(PropertyId.SpeechServiceResponse_JsonResult);
            System.out.println("topic: " + jsonResult);

            if (result.getReason() == ResultReason.RecognizedSpeech) {
                PronunciationAssessmentResult assessmentResult = PronunciationAssessmentResult.fromResult(result);

                System.out.println("文法評分: " + assessmentResult.getContentAssessmentResult().getGrammarScore());
                System.out.println("主題評分: " + assessmentResult.getContentAssessmentResult().getTopicScore());
                System.out.println("單字評分: " + assessmentResult.getContentAssessmentResult().getVocabularyScore());

                // 內容評估結果
                ContentAssessmentResult contentResult = assessmentResult.getContentAssessmentResult();

                return new ContentAssessment(
                        contentResult.getVocabularyScore(),
                        contentResult.getGrammarScore(),
                        contentResult.getTopicScore()
                );
            } else {
                System.out.println("語音識別失敗: " + result.getReason());
            }

            recognizer.close();
            speechConfig.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }



    public String speechToText(String audioFilePath) throws InterruptedException {

        SpeechConfig speechConfig = SpeechConfig.fromSubscription(azureProperties.getAudio().getKey(), azureProperties.getAudio().getRegion());
        speechConfig.setSpeechRecognitionLanguage("en-US");

        AudioConfig audioConfig = AudioConfig.fromWavFileInput(audioFilePath);
        SpeechRecognizer speechRecognizer = new SpeechRecognizer(speechConfig, audioConfig);

        final CountDownLatch latch = new CountDownLatch(1);
        final StringBuilder resultText = new StringBuilder();

        speechRecognizer.recognized.addEventListener((o, e) -> {
            if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                String recognizedText = e.getResult().getText();
                logger.info("Recognized: " + recognizedText);
                resultText.append(recognizedText).append(" ");
            }
        });

        speechRecognizer.canceled.addEventListener((o, e) -> {
            if (e.getReason() == CancellationReason.Error) {
                logger.error("Speech recognition canceled. ErrorDetails: " + e.getErrorDetails());
            }
            latch.countDown();
        });

        speechRecognizer.sessionStopped.addEventListener((o, e) -> latch.countDown());

        speechRecognizer.startContinuousRecognitionAsync();

        latch.await();

        String recognizedText = resultText.toString().trim();
        if (recognizedText.isEmpty()) {
            logger.info("No speech recognized.");
            return "";
        }

        return recognizedText;
    }
}
