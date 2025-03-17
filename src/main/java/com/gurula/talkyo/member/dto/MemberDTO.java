package com.gurula.talkyo.member.dto;

import com.gurula.talkyo.azureai.dto.PartnerResponseDTO;
import com.gurula.talkyo.course.Sentence;
import com.gurula.talkyo.course.enums.Level;

import java.util.ArrayList;
import java.util.List;

public class MemberDTO {
    private String id;
    private String userId;  //line使用
    private String pictureUrl;  //line大頭貼
    private String coverName;   //上傳大頭貼
    private String name;
    private String email;
    private Level chosenLevel;
    private PartnerResponseDTO partner;
    private String role;
    private List<Sentence> savedFavoriteSentences = new ArrayList<>();

    public MemberDTO() {
    }

    public MemberDTO(String id, String userId, String pictureUrl, String coverName, String name, String email, Level chosenLevel, PartnerResponseDTO partner, String role, List<Sentence> savedFavoriteSentences) {
        this.id = id;
        this.userId = userId;
        this.pictureUrl = pictureUrl;
        this.coverName = coverName;
        this.name = name;
        this.email = email;
        this.chosenLevel = chosenLevel;
        this.partner = partner;
        this.role = role;
        this.savedFavoriteSentences = savedFavoriteSentences;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getCoverName() {
        return coverName;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Level getChosenLevel() {
        return chosenLevel;
    }

    public PartnerResponseDTO getPartner() {
        return partner;
    }

    public String getRole() {
        return role;
    }

    public List<Sentence> getSavedFavoriteSentences() {
        return savedFavoriteSentences;
    }
}
