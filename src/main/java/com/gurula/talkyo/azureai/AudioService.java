package com.gurula.talkyo.azureai;

import com.gurula.talkyo.azureai.dto.ChatAudioDTO;
import com.gurula.talkyo.properties.AzureProperties;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class AudioService {
    private final Logger logger = LoggerFactory.getLogger(AudioService.class);
    private final AzureProperties azureProperties;
    private final PartnerRepository partnerRepository;

    public AudioService(AzureProperties azureProperties, PartnerRepository partnerRepository) {
        this.azureProperties = azureProperties;
        this.partnerRepository = partnerRepository;
    }

    public List<String> speechSynthesis(String content, String unitNumber) throws ExecutionException, InterruptedException {
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(
                azureProperties.getAudio().getKey(),
                azureProperties.getAudio().getRegion()
        );

        final String maleAudio = generateSpeechFile(speechConfig, content, unitNumber, "en-US-BrianMultilingualNeural", "_en-US-BrianMultilingualNeural.wav");

        final String femaleAudio = generateSpeechFile(speechConfig, content, unitNumber, "en-US-PhoebeMultilingualNeural", "_en-US-PhoebeMultilingualNeural.wav");

        speechConfig.close();

        return Arrays.asList(maleAudio, femaleAudio);
    }

    public String generateSpeechFile(SpeechConfig speechConfig, String content, String unitNumber, String voiceName, String suffix)
            throws ExecutionException, InterruptedException {
        speechConfig.setSpeechSynthesisVoiceName(voiceName);
        speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Riff16Khz16BitMonoPcm);

        String filePath = "/audio/" + unitNumber + suffix;
        AudioConfig audioConfig = AudioConfig.fromWavFileOutput(filePath);
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

    public String genChattingAudioFile(ChatAudioDTO chatAudioDTO) throws ExecutionException, InterruptedException {

        final Optional<Partner> opt = partnerRepository.findById(chatAudioDTO.getPartnerId());

        if (opt.isPresent()) {
            final Partner partner = opt.get();

            SpeechConfig speechConfig = SpeechConfig.fromSubscription(
                    azureProperties.getAudio().getKey(),
                    azureProperties.getAudio().getRegion()
            );

            speechConfig.setSpeechSynthesisVoiceName(partner.getShortName());
            speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Riff16Khz16BitMonoPcm);

            String suffix = "_" + partner.getShortName() + ".wav";
            String filePath = "/audio/" + chatAudioDTO.getConversationId() + suffix;
            AudioConfig audioConfig = AudioConfig.fromWavFileOutput(filePath);
            SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig, audioConfig);

            SpeechSynthesisResult result = speechSynthesizer.SpeakTextAsync(chatAudioDTO.getContent()).get();

            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                logger.info("Speech synthesized to file [" + filePath + "] using voice [" + partner.getShortName() + "]");
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

        return null;
    }
}
