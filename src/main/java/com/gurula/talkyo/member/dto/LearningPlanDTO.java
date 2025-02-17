package com.gurula.talkyo.member.dto;

import com.gurula.talkyo.course.enums.LessonType;

public class LearningPlanDTO {
    private String lessonId;    // 情境單元編號，用於取得 courseId
    private String lessonName;  // 呈現情境單元名稱
    private String lessonImagePath; // 情境單元封面圖片路徑
    private String chatroomId;  // 用於取得學習結案報告、開始 or 繼續課程的 tag
    private String closeDate;   // 完課日期
    private double overallRating;   // 綜合評分 (fluency, accuracy, completeness, prosody 的平均)
    private LessonType lessonType;

    public LearningPlanDTO() {
    }

    public LearningPlanDTO(String lessonId, String lessonName, String lessonImagePath, String chatroomId, String closeDate, double overallRating, LessonType lessonType) {
        this.lessonId = lessonId;
        this.lessonName = lessonName;
        this.lessonImagePath = lessonImagePath;
        this.chatroomId = chatroomId;
        this.closeDate = closeDate;
        this.overallRating = overallRating;
        this.lessonType = lessonType;
    }

    public LearningPlanDTO(String lessonId, String chatroomId, String closeDate) {
        this.lessonId = lessonId;
        this.chatroomId = chatroomId;
        this.closeDate = closeDate;
    }

    public String getLessonName() {
        return lessonName;
    }

    public String getLessonImagePath() {
        return lessonImagePath;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public String getCloseDate() {
        return closeDate;
    }

    public String getLessonId() {
        return lessonId;
    }

    public double getOverallRating() {
        return overallRating;
    }

    public LessonType getLessonType() {
        return lessonType;
    }
}
