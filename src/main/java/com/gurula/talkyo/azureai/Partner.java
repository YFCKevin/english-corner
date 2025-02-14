package com.gurula.talkyo.azureai;

import com.gurula.talkyo.azureai.enums.Locale;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "partner")
public class Partner {
    @Id
    private String id;
    private String name;
    private String displayName;
    private String localName;
    private String shortName;
    private String gender;
    private Locale locale;
    private String localeName;
    private List<String> secondaryLocaleList;
    private String sampleRateHertz;
    private String voiceType;
    private String status;
    private VoiceTag voiceTag;
    private String coverName;
    private String creationDate;
    private String modificationDate;
    private String deletionDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getLocaleName() {
        return localeName;
    }

    public void setLocaleName(String localeName) {
        this.localeName = localeName;
    }

    public List<String> getSecondaryLocaleList() {
        return secondaryLocaleList;
    }

    public void setSecondaryLocaleList(List<String> secondaryLocaleList) {
        this.secondaryLocaleList = secondaryLocaleList;
    }

    public String getSampleRateHertz() {
        return sampleRateHertz;
    }

    public void setSampleRateHertz(String sampleRateHertz) {
        this.sampleRateHertz = sampleRateHertz;
    }

    public String getVoiceType() {
        return voiceType;
    }

    public void setVoiceType(String voiceType) {
        this.voiceType = voiceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public VoiceTag getVoiceTag() {
        return voiceTag;
    }

    public void setVoiceTag(VoiceTag voiceTag) {
        this.voiceTag = voiceTag;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getDeletionDate() {
        return deletionDate;
    }

    public void setDeletionDate(String deletionDate) {
        this.deletionDate = deletionDate;
    }

    public String getCoverName() {
        return coverName;
    }

    public void setCoverName(String coverName) {
        this.coverName = coverName;
    }
}
