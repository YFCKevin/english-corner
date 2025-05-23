package com.gurula.talkyo.azureai.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Locale {
    EN_IE("en-IE", "愛爾蘭英語"),
    IU_CANS_CA("iu-Cans-CA", "加拿大庫克語（原始）"),
    AR_KW("ar-KW", "科威特阿拉伯語"),
    SW_TZ("sw-TZ", "坦桑尼亞斯瓦希里語"),
    MS_MY("ms-MY", "馬來語（馬來西亞）"),
    EN_IN("en-IN", "印度英語"),
    ES_BO("es-BO", "玻利維亞西班牙語"),
    AR_SY("ar-SY", "敘利亞阿拉伯語"),
    EN_ZA("en-ZA", "南非英語"),
    TA_IN("ta-IN", "印度泰米爾語"),
    IU_LATN_CA("iu-Latn-CA", "拉丁字母庫克語（加拿大）"),
    ZH_CN_LIAONING("zh-CN-liaoning", "遼寧中文"),
    EL_GR("el-GR", "希臘語"),
    SR_LATN_RS("sr-Latn-RS", "拉丁塞爾維亞語"),
    NL_NL("nl-NL", "荷蘭語"),
    ZU_ZA("zu-ZA", "祖魯語（南非）"),
    ZH_CN_SHANDONG("zh-CN-shandong", "山東中文"),
    WUU_CN("wuu-CN", "吳語（中國）"),
    AR_LB("ar-LB", "黎巴嫩阿拉伯語"),
    EN_AU("en-AU", "澳大利亞英語"),
    HE_IL("he-IL", "希伯來語（以色列）"),
    MK_MK("mk-MK", "馬其頓語"),
    AR_TN("ar-TN", "突尼西亞阿拉伯語"),
    OR_IN("or-IN", "印度奧里亞語"),
    AR_LY("ar-LY", "利比亞阿拉伯語"),
    HU_HU("hu-HU", "匈牙利語"),
    ML_IN("ml-IN", "印度馬拉雅拉姆語"),
    ES_SV("es-SV", "薩爾瓦多西班牙語"),
    ES_CR("es-CR", "哥斯達黎加西班牙語"),
    AS_IN("as-IN", "印度阿薩姆語"),
    ES_CL("es-CL", "智利西班牙語"),
    EU_ES("eu-ES", "巴斯克語"),
    FR_CA("fr-CA", "加拿大法語"),
    ES_CO("es-CO", "哥倫比亞西班牙語"),
    JV_ID("jv-ID", "印尼爪哇語"),
    PL_PL("pl-PL", "波蘭語"),
    PT_PT("pt-PT", "葡萄牙語"),
    AR_EG("ar-EG", "埃及阿拉伯語"),
    ES_CU("es-CU", "古巴西班牙語"),
    FR_BE("fr-BE", "比利時法語"),
    GA_IE("ga-IE", "愛爾蘭語"),
    CY_GB("cy-GB", "威爾士語（英國）"),
    AR_DZ("ar-DZ", "阿爾及利亞阿拉伯語"),
    EN_SG("en-SG", "新加坡英語"),
    AR_MA("ar-MA", "摩洛哥阿拉伯語"),
    FIL_PH("fil-PH", "菲律賓語"),
    TA_SG("ta-SG", "新加坡泰米爾語"),
    EN_KE("en-KE", "肯尼亞英語"),
    ES_HN("es-HN", "洪都拉斯西班牙語"),
    NB_NO("nb-NO", "挪威博克馬爾語"),
    HR_HR("hr-HR", "克羅地亞語"),
    ES_PR("es-PR", "波多黎各西班牙語"),
    AF_ZA("af-ZA", "南非荷蘭語"),
    GL_ES("gl-ES", "加利西亞語"),
    ES_PY("es-PY", "巴拉圭西班牙語"),
    DE_AT("de-AT", "奧地利德語"),
    TA_LK("ta-LK", "斯里蘭卡泰米爾語"),
    ZH_CN_SICHUAN("zh-CN-sichuan", "四川中文"),
    IS_IS("is-IS", "冰島語"),
    MY_MM("my-MM", "緬甸語"),
    BG_BG("bg-BG", "保加利亞語"),
    CS_CZ("cs-CZ", "捷克語"),
    EN_PH("en-PH", "菲律賓英語"),
    UZ_UZ("uz-UZ", "烏茲別克語"),
    ZH_TW("zh-TW", "台灣中文"),
    EN_HK("en-HK", "香港英語"),
    KO_KR("ko-KR", "韓語"),
    SK_SK("sk-SK", "斯洛伐克語"),
    PS_AF("ps-AF", "普什圖語（阿富汗）"),
    AR_OM("ar-OM", "阿曼阿拉伯語"),
    RU_RU("ru-RU", "俄語"),
    SQ_AL("sq-AL", "阿爾巴尼亞語"),
    ES_AR("es-AR", "阿根廷西班牙語"),
    SV_SE("sv-SE", "瑞典語"),
    AM_ET("am-ET", "阿姆哈拉語"),
    MR_IN("mr-IN", "印度馬拉地語"),
    ZH_CN_HENAN("zh-CN-henan", "河南中文"),
    DA_DK("da-DK", "丹麥語"),
    MN_MN("mn-MN", "蒙古語"),
    UK_UA("uk-UA", "烏克蘭語"),
    EN_US("en-US", "美國英語"),
    TA_MY("ta-MY", "馬來西亞泰米爾語"),
    GU_IN("gu-IN", "印度古吉拉特語"),
    LV_LV("lv-LV", "拉脫維亞語"),
    NL_BE("nl-BE", "比利時荷蘭語"),
    ZH_CN("zh-CN", "簡體中文"),
    UR_PK("ur-PK", "巴基斯坦烏爾都語"),
    TE_IN("te-IN", "印度泰盧固語"),
    HI_IN("hi-IN", "印度印地語"),
    HY_AM("hy-AM", "亞美尼亞語"),
    EN_NG("en-NG", "奈及利亞英語"),
    DE_CH("de-CH", "瑞士德語"),
    JA_JP("ja-JP", "日語"),
    BS_BA("bs-BA", "波斯尼亞語"),
    AR_YE("ar-YE", "也門阿拉伯語"),
    NE_NP("ne-NP", "尼泊爾語"),
    KA_GE("ka-GE", "喬治亞語"),
    AR_QA("ar-QA", "卡塔爾阿拉伯語"),
    ES_GT("es-GT", "危地馬拉西班牙語"),
    ES_GQ("es-GQ", "赤道幾內亞西班牙語"),
    ES_PE("es-PE", "秘魯西班牙語"),
    EN_NZ("en-NZ", "新西蘭英語"),
    FA_IR("fa-IR", "波斯語"),
    ES_PA("es-PA", "巴拿馬西班牙語"),
    RO_RO("ro-RO", "羅馬尼亞語"),
    MT_MT("mt-MT", "馬爾他語"),
    ET_EE("et-EE", "愛沙尼亞語"),
    TR_TR("tr-TR", "土耳其語"),
    FR_FR("fr-FR", "法語"),
    VI_VN("vi-VN", "越南語"),
    EN_GB("en-GB", "英國英語"),
    KM_KH("km-KH", "高棉語"),
    FI_FI("fi-FI", "芬蘭語"),
    AZ_AZ("az-AZ", "阿塞拜疆語"),
    EN_CA("en-CA", "加拿大英語"),
    ZH_CN_SHAANXI("zh-CN-shaanxi", "陝西中文"),
    LT_LT("lt-LT", "立陶宛語"),
    AR_AE("ar-AE", "阿聯酋阿拉伯語"),
    SL_SI("sl-SI", "斯洛文尼亞語"),
    ES_DO("es-DO", "多米尼加共和國西班牙語"),
    AR_IQ("ar-IQ", "伊拉克阿拉伯語"),
    BN_IN("bn-IN", "印度孟加拉語"),
    SI_LK("si-LK", "斯里蘭卡僧伽羅語"),
    PA_IN("pa-IN", "印度旁遮普語"),
    FR_CH("fr-CH", "瑞士法語"),
    ES_EC("es-EC", "厄瓜多爾西班牙語"),
    ES_US("es-US", "美國西班牙語"),
    KN_IN("kn-IN", "印度卡納達語"),
    LO_LA("lo-LA", "老撾語"),
    AR_SA("ar-SA", "沙烏地阿拉伯阿拉伯語"),
    CA_ES("ca-ES", "加泰羅尼亞語"),
    DE_DE("de-DE", "德語"),
    ZH_HK("zh-HK", "香港中文"),
    PT_BR("pt-BR", "巴西葡萄牙語"),
    SR_RS("sr-RS", "塞爾維亞語"),
    ES_UY("es-UY", "烏拉圭西班牙語"),
    SW_KE("sw-KE", "肯尼亞斯瓦希里語"),
    AR_BH("ar-BH", "巴林阿拉伯語"),
    ES_ES("es-ES", "西班牙西班牙語"),
    KK_KZ("kk-KZ", "哈薩克語"),
    AR_JO("ar-JO", "約旦阿拉伯語"),
    ES_VE("es-VE", "委內瑞拉西班牙語"),
    SO_SO("so-SO", "索馬里語"),
    EN_TZ("en-TZ", "坦桑尼亞英語"),
    SU_ID("su-ID", "印尼巽他語"),
    ES_MX("es-MX", "墨西哥西班牙語"),
    IT_IT("it-IT", "義大利語"),
    UR_IN("ur-IN", "印度烏爾都語"),
    ZH_CN_GUANGXI("zh-CN-guangxi", "廣西中文"),
    BN_BD("bn-BD", "孟加拉語（孟加拉）"),
    YUE_CN("yue-CN", "粵語（中國）"),
    ID_ID("id-ID", "印尼語"),
    ES_NI("es-NI", "尼加拉瓜西班牙語"),
    TH_TH("th-TH", "泰語"),
    OTHER("other", "未分類");

    private String origin;
    private String label;

    Locale(String origin, String label) {
        this.origin = origin;
        this.label = label;
    }

    public String getOrigin() {
        return origin;
    }

    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static Locale fromString(String value) {
        for (Locale locale : Locale.values()) {
            if (locale.getOrigin().equalsIgnoreCase(value)) {
                return locale;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);
    }

    @JsonValue
    public String toJson() {
        return this.name();
    }
}

