    $(document).ready(function() {
        // 右上角下拉選單
        $('#dropdownMenuButton').on('show.bs.dropdown', function(event) {
            const dropdownMenu = $(this).next('.dropdown-menu');
            dropdownMenu.css('transform', 'translate3d(-212px, 40px, 0px)');
        });
    });

    $(document).ready(function() {
        const $advancedCheckModal = $('#advancedCheckModal');
        const $overlay = $('#overlay');

        function showModal() {
            $advancedCheckModal.addClass('active').show();
            $overlay.addClass('active').show();
        }

        function hideModal() {
            $advancedCheckModal.removeClass('active').hide();
            $overlay.removeClass('active').hide();
        }

        $overlay.on('click', function() {
            hideModal();
        });
    });

    $(document).ready(function() {
        const $sampleLessonModal = $('#sampleLessonModal');
        const $overlay = $('#overlay');

        function showModal() {
            $sampleLessonModal.addClass('active').show();
            $overlay.addClass('active').show();
            console.log('showModal')
        }

        function hideModal() {
            $sampleLessonModal.removeClass('active').hide();
            $overlay.removeClass('active').hide();
        }

        $(document).on('click', '.sample-lesson', function(event) {
            console.log('.sample-lesson')
            event.stopPropagation();
            showModal();
        });

        $(document).on('click', function(event) {
            if ($sampleLessonModal.hasClass('active') && !$sampleLessonModal.has(event.target).length && !$(event.target).is('#overlay')) {
                hideModal();
            }
        });

        $overlay.on('click', function() {
            hideModal();
        });
    });

    $(document).ready(function() {
        const $translationModal = $('#translationModal');
        const $overlay = $('#overlay');

        function showModal() {
            $translationModal.addClass('active').show();
            $overlay.addClass('active').show();
        }

        function hideModal() {
            $translationModal.removeClass('active').hide();
            $overlay.removeClass('active').hide();
        }

        $(document).on('click', '.translation-icon', function(event) {
            event.stopPropagation();
            showModal();
        });

        $(document).on('click', function(event) {
            if ($translationModal.hasClass('active') && !$translationModal.has(event.target).length && !$(event.target).is('#overlay')) {
                hideModal();
            }
        });

        $overlay.on('click', function() {
            hideModal();
        });
    });


    function loadData() {
        return {
            member: {},
            partner: {},
            messages: [],
            browserType: getBrowserType(),
            editActiveIndex: null,  // 編輯訊息時，用來放置 message 的 index
            isTextInputActive: false,   // 控制 textInput 的顯示與隱藏
            isTipActive: false,
            globalChatroomId: "",
            action: null,
            channel: "talkyo",
            unitNumber: "",         // scenario 的編號，用來查找情境
            currentMessageId: "",   // FREE_TALK 用來查找歷史聊天記錄
            chatroomType: "",
            stompClient: null,
            isRecording: false,
            isRecordCanceled: false,    // 用來判斷 cancelRecord and stopRecord
            previewMessageId: "",
            actionForEdit: "CREATE",
            indexToDeleteForEdit: null,
            chatroomIdTitles: [],                 // FREE_TALK的歷史對話標題集 (key: chatroomId, value: title)
            chatroomTitle: "",
            closed: false,              // 用來判斷是否為查看聊天記錄
            dropdownRecordOpen: null,   // 歷史對話紀錄內的下拉選單顯示判斷
            dropdownRecordStyle: '',    // 歷史對話紀錄內的下拉選單 css style
            dropdownRecordChatroomId: '',   // 選定的歷史對話紀錄 (用來分享、重新命名、刪除)
            dropdownRecordName: '',
            showEditRecordTitle: null,

            // ===== Tip ===== //
            tipContent: "",
            tipTranslation: "",
            showTipTranslation: false,

            // ===== Sample Lesson ===== //
            basicSentences: [],
            advancedSentences: [],
            lesson: {},
            lessonId: "",
            courseId: "",

            // ===== Dropdown Control ===== //
            blurText: true,
            showTranslation: false,
            showTipBtn: true,

            // ===== AdvancedCheck ===== //
            pronunciationText: "",
            grammarOverallScore: -1,
            grammarAccuracy: false,     // 判斷文法校正內容出現隱藏
            errorSentence: "",
            correctSentence: "",
            errorReason: "",
            grammarReasonTranslation: "",
            grammarAudioPath: "",
            correctUnitNumber: "",
            informalContent: "",
            formalContent: "",
            informalExplanation: "",
            formalExplanation: "",
            informalTranslation: "",
            formalTranslation: "",
            informalAudioPath: "",
            formalAudioPath: "",
            informalUnitNumber: "",
            formalUnitNumber: "",
            informalFavorite: false,
            formalFavorite: false,
            correctFavorite: false,
            showInformalTranslation: false,
            showFormalTranslation: false,
            humanContent: "",
            myAudioName: "",

            // ===== message loading ===== //
            openingLineLoading: false,
            receivedMsgLoading: false,
            sentMsgLoading: false,
            receivedMsgCount: 0,

            // ===== snapshot ===== //
            globalLink: null,
            targetSnapshotId: null,
            snapshotChatroomId: null,
            snapshotLink: '',
            snapshotList: [],

            // ===== delete message ===== //
            deletedMsgIndex: "",
            switchGrammar: false,
            switchInformal: false,
            switchFormal: false,

            // ===== shadowing ===== //
            shadowingModel: false,
            shadowingAudioUrl: "",


            async init() {
                let _this = this;

                const urlParams = new URLSearchParams(window.location.search);
                this.chatroomType = urlParams.get("chatroomType");
                this.action = urlParams.get("action");
                this.unitNumber = urlParams.get("unitNumber");
                this.lessonId = urlParams.get("lessonId");
                this.closed = urlParams.get("closed") != null ? urlParams.get("closed") : false;
                this.globalChatroomId = urlParams.get("chatroomId");
                this.globalLink = urlParams.get("link");

                if (!this.globalLink) {
                    this.memberInfo();
                }

                if (!this.closed){
                    // 可以對話
                    this.openingLineLoading = true;
                    $.ajax({
                        url: "chatroom/create",
                        type: "post",
                        dataType: "json",
                        data: JSON.stringify({
                            chatroomType: this.chatroomType,
                            action: this.action,
                            lessonId: this.lessonId,
                            unitNumber: this.unitNumber     // for FREE_TALK 辨識與朋友or與家教
                        }),
                        contentType: "application/json; charset=utf-8",
                        success: function (response) {
                            if (response.code == "C000") {
                                _this.globalChatroomId = response.data;

                                $.ajax({
                                    url: "chatroom/getCurrentMsgId/" + _this.globalChatroomId,
                                    type: "get",
                                    dataType: "json",
                                    success: function (msgResponse) {
                                        if (msgResponse.code === "C000") {
                                            _this.currentMessageId = msgResponse.data;
                                        }
                                        _this.connect();
                                    }
                                });
                            }
                        },
                    });

                } else {
                    // 僅供查看
                    if (!this.globalLink) {
                        // 非共享，是自己的對話紀錄
                        $.ajax({
                            url: "chatroom/history/message/all",
                            type: "post",
                            dataType: "json",
                            data: JSON.stringify({ chatroomId: this.globalChatroomId, chatroomType: this.chatroomType }),
                            contentType: "application/json; charset=utf-8",
                            success: function (response) {
                                if (response.code == "C000") {
                                    _this.messages = response.data.map(obj => {
                                        const key = Number(Object.keys(obj)[0]);
                                        return { key, value: obj[key] };
                                    });
                                    _this.updateMessages();
                                    $(".chat-messages").css("height", "90vh");
                                }
                            },
                        });

                    } else {
                        // 共享
                        $.ajax({
                            url: "snapshot/link/" + this.globalLink,
                            type: "get",
                            dataType: "json",
                            contentType: "application/json; charset=utf-8",
                            success: function (response) {
                                if (response.code == "C000") {
                                    _this.messages = response.data.map(obj => {
                                        const key = Number(Object.keys(obj)[0]);
                                        return { key, value: obj[key] };
                                    });
                                    _this.updateMessages();
                                    $(".chat-messages").css("height", "90vh");
                                } else if (response.code == "C004") {   // 公開連結已被刪除
                                    $("#errorModal").modal('show');
                                }
                            },
                        });
                    }

                }

                if (!this.globalLink) {
                    this.getAllChatRecord();
                }


                const dropdownSetting = JSON.parse(localStorage.getItem("dropdownSettings")) || {
                    blurText: false,
                    showTranslation: false,
                    showTipBtn: true,
                };
                if (dropdownSetting) {
                    this.blurText = dropdownSetting.blurText;
                    this.showTranslation = dropdownSetting.showTranslation;
                    this.showTipBtn = dropdownSetting.showTipBtn;
                }

                // closeDropdownOnOutsideClick 方法在執行時，this 的上下文可能並未正確綁定到 Alpine.js 實例，使用 .bind() 方法顯式綁定 this
                document.addEventListener('click', this.closeDropdownOnOutsideClick.bind(this));
            },

            async memberInfo() {
                let _this = this;
                try {
                    const response = await $.ajax({
                        url: "member/info",
                        type: "get",
                        dataType: "json",
                        contentType: "application/json; charset=utf-8"
                    });
                    _this.member = response;
                    _this.partner = response.partner;
                } catch (xhr) {
                    if (xhr.status === 401) {
                        window.location.href = "sign-in.html";
                    }
                    throw error;
                }
            },

            updateMessages() {
                console.log(this.messages)
                this.messages = this.messages.map(msg => ({
                    ...msg,
                    value: {
                        ...msg.value,
                        showTranslation: this.showTranslation,
                        isBlurred: this.blurText,
                        ...(msg.value.imageName ? { imagePath: "image/" + msg.value.chatroomId + "/" + msg.value.imageName } : {})
                    }
                }));
                this.previewMessageId = this.messages[this.messages.length - 1].value.id;
                if (!this.closed) { // 僅限可對話時
                    this.scrollToMsgBottom();
                    this.genChatroomTitle();
                }

            },

            prevBranch(index) {
                if (this.isPartnerTalking() || this.isHumanTalking()) return;
                if (!this.isFirstPage) {
                    console.log("Previous Branch");
                    this.previewMessageId = this.messages[index - 1].value.id;
                    let targetVersion = this.messages[index].value.version - 1;
                    this.switchBranch(this.previewMessageId, targetVersion);
                }
            },
            nextBranch(index) {
                if (this.isPartnerTalking() || this.isHumanTalking()) return;
                if (!this.isLastPage) {
                    console.log("Next Branch");
                    this.previewMessageId = this.messages[index - 1].value.id;
                    let targetVersion = this.messages[index].value.version + 1;
                    this.switchBranch(this.previewMessageId, targetVersion);
                }
            },

            switchBranch(previewMessageId, targetVersion) {
                let _this = this;
                $.ajax({
                    url: "chatroom/switchMsg/" + previewMessageId + "/" + targetVersion,
                    type: "get",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code == "C000") {
                            _this.messages = response.data.map(obj => {
                                const key = Number(Object.keys(obj)[0]);
                                return { key, value: obj[key] };
                            });
                            _this.updateMessages();
                            _this.currentMessageId = _this.messages[_this.messages.length - 1].value.id;
                            _this.previewMessageId = _this.messages[_this.messages.length - 1].value.id;
                        }
                    },
                });
            },

            toggleBlur(index) {
                this.showText = !this.showText;
                this.messages[index].value.isBlurred = !this.messages[index].value.isBlurred;
            },

            toggleTranslation(index) {
                this.showTranslation = !this.showTranslation;
                this.messages[index].value.showTranslation = !this.messages[index].value.showTranslation;
            },

            edit(index, event) {
                if (this.isPartnerTalking() || this.isHumanTalking()) return;
                const el = event.target;
                const isCurrentlyActive = this.editActiveIndex === index;   // 檢查是否點擊了同一個 icon
                if (isCurrentlyActive) {
                    // 如果點擊了同一個 active 的 icon, 則重置
                    this.resetEdit();
                } else {
                    console.log('啟動編輯')
                    // 否則，設為 active，並執行相應的操作
                    this.editActiveIndex = index;

                    let message = this.messages[index].value;
                    $("#text").val(message.parsedText ? message.parsedText : message.text); //TODO: 只傳圖片情況
                    $("#textInput").removeClass('d-none');
                    $("#tagBar").hide();
                    $('#keyboardInput').hide();
                    this.isTextInputActive = true;

                    el.classList.add('active');

                    this.previewMessageId = this.messages[index - 1].value.id;
                    this.indexToDeleteForEdit = index;
                    this.actionForEdit = "EDIT";
                }
            },

            resetEdit() {
                console.log('resetEdit')
                const activeElement = document.querySelector('.fa-eraser.active');
                if (activeElement) {
                    activeElement.classList.remove('active');
                }
                this.activeIndex = null;

                $("#textInput").addClass('d-none');
                $("#tagBar").show();
                $('#keyboardInput').show();
                $("#text").val('');
                this.editActiveIndex = null;
                this.isTextInputActive = false;

                this.previewMessageId = this.messages[this.messages.length - 1].value.id;
                this.actionForEdit = "CREATE";
            },

            clickOutsideEditIcon(event) {
                const textInput = document.getElementById('textInput');
                if (this.isTextInputActive && !textInput.contains(event.target)) {
                    this.resetEdit();
                }
            },

            openDeleteModal(index) {
                if (this.isPartnerTalking() || this.isHumanTalking()) return;
                this.deletedMsgIndex = index;
                $("#deleteMessageModal").modal('show');
            },

            deleteMessage() {
                let _this = this;
                let currentMessageId = this.messages[this.deletedMsgIndex].value.id;
                let nextMessageId = this.messages[this.deletedMsgIndex + 1]?.value.id;
                console.log(currentMessageId)
                console.log(nextMessageId)

                $.ajax({
                    url: "chatroom/message/delete/" + currentMessageId + "_" + nextMessageId,
                    type: "DELETE",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code == "C000") {
                            _this.messages.splice(_this.deletedMsgIndex, 2);
                            _this.updateMessages();
                            $("#deleteMessageModal").modal('hide');
                        } else {
                            alert('系統錯誤，請稍後再試！');
                        }
                    },
                });
            },

            advancedCheck(index) {
                if (this.isPartnerTalking() || this.isHumanTalking()) return;
                let _this = this;
                let message = this.messages[index].value;

                // 該則訊息的內容賦值變數，之後用來產生 partner voice
                this.humanContent = message.parsedText;
                this.myAudioName = message.audioName;

                // 出現進階文法語句視窗
                this.showAdvancedCheckModal();

                if (message.advancedSentences?.length > 0) {
                    this.constructAdvancedCheckContent(message);
                    return;
                }

                $.ajax({
                    url: "chatroom/advancedCheck/" + message.id,
                    type: "get",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    beforeSend: function () {
                        $(".advanced-check-area").hide();
                        $(".loading-spinner-area").show();
                        $('.loading-spinner-area').css('min-height', '100');
                        $(".advanced-check-loading-spinner").show();
                    },
                    success: function (response) {
                        if (response.code == "C000"){
                            console.log('完成');
                            let message = response.data;
                            _this.messages[index].value.grammarResult = message.grammarResult;
                            _this.messages[index].value.advancedSentences = message.advancedSentences;
                            _this.messages[index].value.conversationScore = message.conversationScore;
                            _this.constructAdvancedCheckContent(message);
                        } else {
                            alert('系統錯誤')
                        }
                    },
                    complete: function () {
                        console.log("請求完成");
                        $(".loading-spinner-area").hide();
                        $('.loading-spinner-area').css('min-height', '0');
                        $(".advanced-check-loading-spinner").hide();
                        $(".advanced-check-area").show();
                    }
                });
            },

            showAdvancedCheckModal() {
                const $advancedCheckModal = $('#advancedCheckModal');
                const $overlay = $('#overlay');

                $advancedCheckModal.addClass('active').show();
                $overlay.addClass('active').show();
                console.log('showAdvancedCheckModal');
            },

            constructAdvancedCheckContent(message) {
                this.errorSentence = "";
                this.correctSentence = "";
                this.errorReason = "";
                this.grammarReasonTranslation = "";
                this.correctUnitNumber = "";
                this.grammarAudioPath = "";
                this.informalContent = "";
                this.formalContent = "";
                this.informalUnitNumber = "",
                this.formalUnitNumber = "",
                this.informalExplanation = "";
                this.formalExplanation = "";
                this.informalTranslation = "";
                this.formalTranslation = "";
                this.informalAudioPath = "";
                this.formalAudioPath = "";
                this.grammarOverallScore = -1;
                this.pronunciationText = "";

                // 文法
                let grammar = message.grammarResult;
                if (grammar != null) {
                    this.errorSentence = grammar.errorSentence || "";
                    this.correctSentence = grammar.correctSentence || "";
                    this.errorReason = grammar.errorReason || "";
                    this.grammarReasonTranslation = grammar.translation || "";
                    this.correctUnitNumber = grammar.unitNumber || "";
                    this.grammarAudioPath = grammar.audioName ? `audio/${this.globalChatroomId}/${grammar.audioName}` : "";
                }
                this.grammarAccuracy = message.accuracy;

                // 精進
                if (message.advancedSentences != null || message.advancedSentences != undefined) {
                    let advancedSentences = message.advancedSentences;
                    this.informalContent = advancedSentences[0].content || "";
                    this.formalContent = advancedSentences[1].content || "";
                    this.informalUnitNumber = advancedSentences[0].unitNumber || "";
                    this.formalUnitNumber = advancedSentences[1].unitNumber || "";
                    this.informalExplanation = advancedSentences[0].explanation || "";
                    this.formalExplanation = advancedSentences[1].explanation || "";
                    this.informalTranslation = advancedSentences[0].translation || "";
                    this.formalTranslation = advancedSentences[1].translation || "";
                    this.informalAudioPath = advancedSentences[0].audioName ? `audio/${this.globalChatroomId}/${advancedSentences[0].audioName[0]}` : "";
                    this.formalAudioPath = advancedSentences[1].audioName ? `audio/${this.globalChatroomId}/${advancedSentences[1].audioName[0]}` : "";
                }

                this.updateFavorite();

                // 發音分析
                if (message.conversationScore != null || message.conversationScore != undefined) {
                    let displayText = message.conversationScore.displayText;
                    let displayWords = message.conversationScore.displayWords;
                    this.grammarOverallScore = Math.floor((message.conversationScore.fluency +
                                message.conversationScore.accuracy +
                                message.conversationScore.completeness +
                                message.conversationScore.prosody) / 4);
                    this.highlightText(displayText, displayWords);
                }

            },

            toggleInformalTranslation() {
                this.showInformalTranslation = !this.showInformalTranslation;
            },

            toggleFormalTranslation() {
                this.showFormalTranslation = !this.showFormalTranslation;
            },

            updateFavorite() {
                this.basicSentences.forEach(item => {
                    item.favorite = this.member.savedFavoriteSentences.map(sentence => sentence.unitNumber).includes(item.unitNumber);
                });
                this.advancedSentences.forEach(item => {
                    item.favorite = this.member.savedFavoriteSentences.map(sentence => sentence.unitNumber).includes(item.unitNumber);
                });
                this.informalFavorite = this.member.savedFavoriteSentences.map(sentence => sentence.unitNumber).includes(this.informalUnitNumber);
                this.formalFavorite = this.member.savedFavoriteSentences.map(sentence => sentence.unitNumber).includes(this.formalUnitNumber);
                this.correctFavorite = this.member.savedFavoriteSentences.map(sentence => sentence.unitNumber).includes(this.correctUnitNumber);
            },

            highlightText(displayText, displayWords) {
                let words = displayText.split(/(\s+|[\.,!?])/);

                words.forEach(word => {
                    if (word.trim() === "") {
                        this.pronunciationText += word;
                        return;
                    }

                    let match = displayWords.find(w => w.word === word);

                    if (match) {
                        let tag = getTag(match.accuracyScore);
                        this.pronunciationText += `<${tag}>${match.word}</${tag}>`;
                    } else {
                        this.pronunciationText += word;
                    }

                    function getTag(score) {
                        if (score >= 80) return "high";
                        if (score >= 60) return "medium";
                        return "low";
                    }

                });
            },

            playMsgSound(index) {
                if (this.isPartnerTalking() || this.isHumanTalking()) return;
                let audioName = this.messages[index].value.audioName;
                if (audioName) {
                    const audioUrl = "audio/" + this.globalChatroomId + "/" + audioName;
                    const audio = new Audio(audioUrl);
                    audio.play().then(() => {
                        setTimeout(() => {
                            audio.muted = false;
                        }, 10);
                    }).catch(error => console.error("播放音訊失敗"));
                }
            },

            playAdvancedSound(el, rate) {
                const audioUrl = $(el).attr("data-url");

                if (audioUrl) {
                    const audio = new Audio(audioUrl);

                    audio.playbackRate = rate;

                    audio.play().then(() => {
                        setTimeout(() => {
                            audio.muted = false;
                        }, 10);
                    }).catch(error => console.error("播放音訊失敗"));

                    // (可選) 添加播放結束事件監聽器
                    audio.addEventListener('ended', function() {
                        console.log('Audio playback finished.');
                        // 在播放結束後執行一些操作，例如更改 icon 或顯示訊息
                    });

                }
            },

            playPartnerSound(audioName) {
                if (audioName) {
                    const audioUrl = "audio/" + this.globalChatroomId + "/" + audioName;
                    const audio = new Audio(audioUrl);
                    audio.muted = true;
                    audio.play().then(() => {
                        setTimeout(() => {
                            audio.muted = false;
                        }, 10);
                    }).catch(error => console.error("播放音訊失敗"));
                }
            },

            pronunciationAudio() {
                let data = { content: this.humanContent };
                $.ajax({
                    url: "chatroom/genAudio",
                    type: "post",
                    dataType: "json",
                    data: JSON.stringify(data),
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code == "C000") {
                            const audio = new Audio(response.data);
                            audio.play().then(() => {
                                setTimeout(() => {
                                    audio.muted = false;
                                }, 10);
                            }).catch(error => console.error("播放音訊失敗"));
                        }
                    },
                });
            },

            myVoice() {
                console.log(this.myAudioName)
                const audioUrl = "audio/" + this.globalChatroomId + "/" + this.myAudioName;
                const audio = new Audio(audioUrl);
                audio.play().then(() => {
                    setTimeout(() => {
                        audio.muted = false;
                    }, 10);
                }).catch(error => console.error("播放音訊失敗"));
            },

            showKeyboard () {
                if (this.isPartnerTalking() || this.isHumanTalking()) return;
                $('#textInput').toggleClass('d-none');
                $('#tagBar').toggle();
                $('#keyboardInput').toggle();

                if ($('#textInput').hasClass('d-none')) {
                    $(document).off('click.textInput');
                } else {
                    $(document).on('click.textInput', function(event) {
                        if (!$(event.target).closest('#textInput').length) {
                            $('#textInput').addClass('d-none');
                            $('#keyboardInput').show();
                            $('#tagBar').show();

                            $(document).off('click.textInput');
                        }
                    });
                }
            },

            // ======== Tips 相關 ======== //
            genTip() {
                if (this.isPartnerTalking() || this.isHumanTalking()) return;

                // 清空文字區
                this.tipContent = "";

                // 顯示視窗和loading圖示
                this.isTipActive = true;
                $(".message-footer").addClass("move-up");
                $(".loading-spinner").show();

                let _this = this;
                let messageId = this.messages[this.messages.length - 1].value.id;
                $.ajax({
                    url: "chatroom/guidingSentence/" + messageId,
                    type: "get",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code == "C000") {
                            $(".loading-spinner").hide();
                            let tip = response.data;
                            _this.tipContent = tip.content;
                            _this.tipTranslation = tip.translation;
                        }
                    },
                });
            },
            renewTip() {
                this.genTip();
            },
            genAudioForTip() {
                let _this = this;
                let data = { content: this.tipContent };
                $.ajax({
                    url: "chatroom/genAudio",
                    type: "post",
                    dataType: "json",
                    data: JSON.stringify(data),
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code == "C000") {
                            const audio = new Audio(response.data);
                            audio.play().then(() => {
                                setTimeout(() => {
                                    audio.muted = false;
                                }, 10);
                            }).catch(error => console.error("播放音訊失敗"));
                        }
                    },
                });
            },
            sendTip () {
                this.isTipActive  = false;
                $(".message-footer").removeClass("move-up");
                $(".loading-spinner").hide();
                if (this.stompClient) {

                    if (this.tipContent === '') {
                        alert("請輸入文字！");
                        return;
                    }

                    let chatMessage = {
                        chatroomId: this.globalChatroomId,
                        chatroomType: this.chatroomType,
                        previewMessageId: this.previewMessageId,
                        action: "CREATE",
                        content: this.tipContent,
                        lessonId: this.lessonId
                    };

                    this.stompClient.send("/chatroom/chat", {}, JSON.stringify(chatMessage));
                    this.tipContent = '';
                }
                event.preventDefault();
            },
            cancelTip() {
                $(".message-footer").removeClass("move-up");
                $(".loading-spinner").hide();
                this.isTipActive  = false;
            },

            // ======== Sample Lesson 相關 ======== //
            showSentences(tabId) {
                const tabElement = document.querySelector(`.nav-link[href="#${tabId}"]`);
                if (tabElement) {
                    if (tabId === 'basic-tabs-simple') {    // 基礎
                        this.basicSentences = [];
                        this.basicSentences = this.lesson.sentences.slice(0, 5);
                        this.basicSentences.forEach(sentence => {
                            if (this.member.savedFavoriteSentences.map(item => item.unitNumber).includes(sentence.unitNumber)) {
                                sentence.favorite = true;
                            } else {
                                 sentence.favorite = false;
                           }
                        });
                    } else if (tabId === 'advanced-tabs-simple') {   // 進階
                        this.advancedSentences = [];
                        this.advancedSentences = this.lesson.sentences.slice(-5);
                        this.advancedSentences.forEach(sentence => {
                            if (this.member.savedFavoriteSentences.map(item => item.unitNumber).includes(sentence.unitNumber)) {
                                sentence.favorite = true;
                            } else {
                                sentence.favorite = false;
                            }
                        });
                    }
                } else {
                    console.log('Tab is already active, doing nothing.');
                }
            },
            playSound(lessonNumber, audioName) {
                let audioFile = "audio/" + this.lesson.courseTopic + "/" + this.lesson.lessonNumber + "/" + audioName;
                if (audioName) {
                    const audio = new Audio(audioFile);
                    audio.play().then(() => {
                        setTimeout(() => {
                            audio.muted = false;
                        }, 10);
                    }).catch(error => console.error("播放音訊失敗"));
                } else {
                    console.warn("No audio file specified.");
                }
            },
            getDemoSentence() {
                if (this.isPartnerTalking() || this.isHumanTalking()) return;
                let _this = this;
                $.ajax({
                    url: "course/lesson/info/" + this.lessonId,
                    type: "get",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code == "C000"){
                            _this.lesson = response.data;
                            _this.courseId = _this.lesson.courseId;
                            _this.basicSentences = _this.lesson.sentences.slice(0, 5);    // 先裝填
                            _this.basicSentences.forEach(sentence => {
                                if (_this.member.savedFavoriteSentences.map(item => item.unitNumber).includes(sentence.unitNumber)) {
                                    sentence.favorite = true;
                                } else {
                                    sentence.favorite = false;
                                }
                            });

                            _this.showSampleLessonModal();
                        }
                    },
                });
            },

            showSampleLessonModal() {
                const $sampleLessonModal = $('#sampleLessonModal');
                const $overlay = $('#overlay');

                $sampleLessonModal.addClass('active').show();
                $overlay.addClass('active').show();
                console.log('showSampleLessonModal');
            },

            showTranslationModal() {
                if (this.isPartnerTalking() || this.isHumanTalking()) return;
                const $translationModal = $('#translationModal');
                const $overlay = $('#overlay');

                $translationModal.addClass('active').show();
                $overlay.addClass('active').show();
                console.log('showTranslationModal');
            },

            // ===== 複製 ===== //
            copy(index) {
                let text = this.messages[index].value.parsedText;
                navigator.clipboard.writeText(text).then(() => {
                    $(".notification").removeClass('d-none');

                    setTimeout(function () {
                        $(".notification").addClass('d-none');
                    }, 1500);

                }).catch(err => {
                    console.error('複製失敗:', err);
                });
            },

            // ===== translation ===== //
            translation() {
                $(".translation-button").css({
                    "pointer-events": "none",
                    "opacity": "0.5"
                });
                let text = $(".translation-input").val();
                if (text == '') {
                    alert('請輸入文字！');
                    return;
                }
                let _this = this;
                let data = { text: text };
                $.ajax({
                    url: "openai/translate",
                    type: "post",
                    dataType: "json",
                    data: JSON.stringify(data),
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code == "C000") {
                            $(".translate-response").text(response.data);
                            $("#translation-response-div").removeClass('d-none');
                            $(".translation-button").css({
                                "pointer-events": "auto",
                                "opacity": "1"
                            });
                        }
                    },
                });
            },
            genAudioForTranslation() {
                let _this = this;
                let data = { content: $(".translate-response").text() };
                $.ajax({
                    url: "chatroom/genAudio",
                    type: "post",
                    dataType: "json",
                    data: JSON.stringify(data),
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code == "C000") {
                            const audio = new Audio(response.data);
                            audio.play().then(() => {
                                setTimeout(() => {
                                    audio.muted = false;
                                }, 10);
                            }).catch(error => console.error("播放音訊失敗"));
                        }
                    },
                });
            },

            // ===== 上傳圖片 ===== //
            uploadImg() {
                if (this.isPartnerTalking() || this.isHumanTalking()) return;
                let _this = this;
                const imageInput = document.getElementById("imageInput");
                imageInput.click();
                imageInput.addEventListener(
                    "change",
                    function () {
                        setTimeout(() => {
                            const file = imageInput.files[0]; // 取得選擇的檔案
                            if (!file) return; // 若未選擇檔案則返回

                            const allowedTypes = ["image/jpeg", "image/png", "image/gif", "image/webp"];
                            if (!allowedTypes.includes(file.type)) {
                                alert("請選擇一張圖片");
                                return;
                            }

                            let formData = new FormData();
                            formData.append("multipartFile", file);
                            formData.append("messageType", "IMAGE");
                            formData.append("chatroomId", _this.globalChatroomId);

                            $.ajax({
                                url: "chatroom/upload",
                                type: "POST",
                                data: formData,
                                processData: false,
                                contentType: false,
                                success: function (response) {
                                    if (response.code === "C000") {
                                        console.log("上傳成功，文件 URL:", response.data);
                                        $(".uploaded-img").attr('src', response.data);
                                        $(".image-display").removeClass("d-none");
                                        $("#textInput").css("transform", "translateY(-100.5px)");
                                        _this.showKeyboard ();
                                    } else {
                                        alert("上傳失敗：" + response.message);
                                    }
                                },
                                error: function (xhr, status, error) {
                                    console.error("上傳錯誤:", error);
                                    alert("上傳過程中發生錯誤！");
                                },
                            });
                        }, 500); // 延遲 500ms
                    },
                    { once: true }
                );
            },
            removeImg() {
                let _this = this;
                let data = {
                    fileName: $(".uploaded-img").attr("src").split('/').pop(),
                    messageType: "IMAGE",
                    chatroomId: this.globalChatroomId
                }

                $.ajax({
                    url: "chatroom/file/delete",
                    type: "DELETE",
                    dataType: "json",
                    data: JSON.stringify(data),
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code === "C000") {
                            console.log("刪除成功");
                            $(".image-display").addClass("d-none");
                            $("#textInput").css("transform", "translateY(0)");
                            $(".uploaded-img").attr('src', "");
                        } else {
                            console.log("刪除失敗");
                        }
                    },
                });
            },

            // ===== 變更與儲存 dropdown setting ===== //
            saveAndChangeDropdownSettings() {
                this.updateMessages();
                let settings = {
                    blurText: this.blurText,
                    showTranslation: this.showTranslation,
                    showTipBtn: this.showTipBtn,
                };
                localStorage.setItem("dropdownSettings", JSON.stringify(settings));
            },

            // ===== Websocket ===== //
            connect() {
                console.log('connect')
                this.initializeWebSocketConnection(event);
            },
            initializeWebSocketConnection(event) {
                console.log('initializeWebSocketConnection')
                // 如果已有連接，先關閉舊的連線
                if (this.stompClient !== null) {
                    this.stompClient.disconnect(() => {
                        console.log('舊的連線已斷開');
                    });
                }

                var socket = new SockJS('ws');
                this.stompClient = Stomp.over(socket);

                this.stompClient.heartbeat.outgoing = 10000; // 每 10 秒發送一次心跳
                this.stompClient.heartbeat.incoming = 10000; // 每 10 秒接收心跳

                this.stompClient.connect({}, (frame) => {
                    console.log('連接成功: ' + frame);
                    this.messages = [];         //TODO: _this.messages.length = 0;
                    this.openingLineLoading = true;
                    this.receivedMsgCount = 0;
                    this.onConnected();
                    reconnectAttempts = 0;
                }, (error) => {
                    console.error('連接失敗: ' + error);
                    if (reconnectAttempts < 3) {
                        reconnectAttempts++;
                        this.connect(event);
                    } else {
                        console.error('達到最大重連次數，停止重連');
                    }
                });
            },
            onConnected (){
                let _this = this;
                // 訂閱
                this.stompClient.subscribe("/chatroom/" + this.globalChatroomId, function (message) {
                    if (message.body) {
                        let data = JSON.parse(message.body);
                        if (data.messages.length > 0) {
                            data.messages.forEach(obj => {
                                const key = Number(Object.keys(obj)[0]);
                                const value = obj[key];

                                _this.messages.push({ key, value: obj[key] });

                                // 產生partner訊息後馬上播放音檔
                                if (data.messages.length === 1 &&   // 避掉取歷史紀錄和重新連線
                                    value.senderRole === 'AI' &&
                                    value.audioName
                                ) {
                                    _this.playPartnerSound(value.audioName);
                                }

                            });
                            _this.updateMessages();
                        }
                    }

                    if (!_this.openingLineLoading) {
                        _this.receivedMsgCount++;

                        // 設定 sentMsgLoading，奇數為 false，偶數略過
                        if (_this.receivedMsgCount % 2 !== 0) _this.sentMsgLoading = false;
                        // 設定 receivedMsgLoading，奇數為 true，偶數為 false
                        _this.receivedMsgLoading = _this.receivedMsgCount % 2 !== 0;
                    }

                    _this.openingLineLoading = false;
                });

                const dto = {
                    chatroomType: this.chatroomType,
                    chatroomId: this.globalChatroomId,
                    currentMessageId: this.currentMessageId,
                    lessonId: this.lessonId,
                    courseId: this.courseId,
                    scenario: this.scenario,
                    unitNumber: this.unitNumber,
                };

                this.stompClient.send("/chatroom/init", {}, JSON.stringify(dto));
            },
            sendTextMsg() {
                if (this.stompClient) {
                    let text = $("#text").val().trim();
                    let imgPath = $(".uploaded-img").attr("src");

                    if (text === '' && imgPath === '') {
                        alert("請輸入文字！");
                        return;
                    }

                    this.sentMsgLoading = true;
                    this.scrollToBottom();      // 滾到最底端

                    // 如果在「修改模式」下，將修改的這筆與他的後續對話紀錄從 this.messages 中刪除
                    if (this.actionForEdit == "EDIT") {
                        this.messages.splice(this.indexToDeleteForEdit, this.messages.length - this.indexToDeleteForEdit);
                    }

                    let chatMessage = {
                        chatroomId: this.globalChatroomId,
                        chatroomType: this.chatroomType,
                        previewMessageId: this.previewMessageId,
                        action: this.actionForEdit,
                        content: text,
                        lessonId: this.lessonId,
                        imageFileName: $(".uploaded-img").attr("src") ? $(".uploaded-img").attr("src").split("/").pop() : null
                    };

                    this.stompClient.send("/chatroom/chat", {}, JSON.stringify(chatMessage));

                    // 處理文字邏輯部分
                    text = '';
                    $("#text").val('');

                    // 處理圖片邏輯部分
                    if ($(".uploaded-img").attr("src")) {
                        $(".image-display").addClass("d-none");
                        $("#textInput").css("transform", "translateY(0)");
                        $(".uploaded-img").attr('src', "");
                    }

                    this.resetEdit();   // 回覆成語音介面
                }
                event.preventDefault();
            },
            disconnect() {
                let _this = this;
                if (this.stompClient) {
                    this.stompClient.disconnect(function() {
                        console.log('斷開連接成功');
                    });
                }
            },

            async startRecord() {
                if (this.isPartnerTalking() || this.isHumanTalking() || this.isRecording) return;
                this.isRecording = true;
                this.isRecordCanceled = false;

                try {
                    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });

                    if (this.mediaRecorder && this.mediaRecorder.state !== "inactive") {
                        this.mediaRecorder.stop();
                    }

                    this.audioChunks = [];
                    this.mediaRecorder = new MediaRecorder(stream);

                    this.mediaRecorder.ondataavailable = event => {
                        if (!this.isRecordCanceled) {
                            this.audioChunks.push(event.data);
                        }
                    };

                    this.mediaRecorder.onstop = async () => {
                        if (this.isRecordCanceled || this.audioChunks.length === 0) {
                            console.log("錄音已取消");
                            return;
                        }

                        const audioBlob = new Blob(this.audioChunks, { type: "audio/webm" });
                        if (this.shadowingModel) {
                            this.shadowingAudioUrl = URL.createObjectURL(audioBlob);
                            this.shadowingModel = false;
                            return;
                        }

                        $("#translationModal").removeClass("active");   // 關閉翻譯視窗
                        $("#sampleLessonModal").removeClass("active");   // 關閉範例視窗
                        $("#overlay").removeClass('active').hide();

                        await this.uploadAudio(audioBlob);
                        this.isRecording = false;
                    };

                    this.mediaRecorder.start();
                } catch (error) {
                    console.error("錄音初始化失敗：", error);
                    this.isRecording = false;
                }
            },
            stopRecord() {
                this.isRecording = !this.isRecording;
                this.isRecordCanceled = false;
                if (this.mediaRecorder && this.mediaRecorder.state !== "inactive") {
                    this.mediaRecorder.stop();
                }
                this.mediaRecorder.stream.getTracks().forEach(track => track.stop());

                this.cancelTip();
            },
            cancelRecord() {
                if (this.isPartnerTalking() || this.isHumanTalking()) return;
                this.isRecording = !this.isRecording;
                this.isRecordCanceled = true;
                this.shadowingModel = false;
                if (this.mediaRecorder && this.mediaRecorder.state !== "inactive") {
                    this.mediaRecorder.stop();
                }
                this.mediaRecorder.stream.getTracks().forEach(track => track.stop());

                this.mediaRecorder = null;
            },
            async uploadAudio(audioBlob) {
                let _this = this;

                this.sentMsgLoading = true;
                this.scrollToBottom();      // 滾到最底端

                const formData = new FormData();
                formData.append("multipartFile", audioBlob, "recording.webm");
                formData.append("messageType", "AUDIO");
                formData.append("chatroomId", this.globalChatroomId);

                $.ajax({
                    url: "chatroom/upload",
                    type: "POST",
                    data: formData,
                    processData: false,
                    contentType: false,
                    success: function (response) {
                        if (response.code === "C000") {
                            const fileName = response.data.split('/').pop();

                            let chatMessage = {
                                chatroomId: _this.globalChatroomId,
                                chatroomType: _this.chatroomType,
                                audioFileName: fileName,
                                previewMessageId: _this.previewMessageId,
                                action: _this.actionForEdit,
                            };
                            _this.stompClient.send("/chatroom/chat", {}, JSON.stringify(chatMessage));
                        } else {
                            console.error("上傳失敗");
                        }
                    },
                    error: function (xhr, status, error) {
                        console.error("發生錯誤：", error);
                    }
                });
            },

            endLesson() {
                if (this.isPartnerTalking() || this.isHumanTalking()) return;
                let _this = this;
                _this.showWaitingAnimation();

                setTimeout(() => {
                    let url = `chatroom/leave?chatroomId=${_this.globalChatroomId}&chatroomType=${_this.chatroomType}`;

                    if (_this.lessonId) url += `&lessonId=${_this.lessonId}`;
                    if (_this.unitNumber) url += `&unitNumber=${_this.unitNumber}`;

                    window.location.href = url;
                }, 500);
            },

            showWaitingAnimation() {
                let loadingScreen = document.getElementById("waiting-screen");
                // 讓畫面從右滑入
                loadingScreen.style.right = "0";
            },

            scrollToMsgBottom() {
                this.$nextTick(() => {
                    let messages = $(".message");
                    if (messages.length > 0) {
                        messages.last().get(0).scrollIntoView({ behavior: "smooth" });
                    }
                })
            },

            scrollToBottom() {
                this.$nextTick(() => {
                    let chatContainer = $(".chat-messages");
                    if (chatContainer.length > 0) {
                        chatContainer.scrollTop(chatContainer[0].scrollHeight);
                    }
                });
            },

            // Partner 講話時不能有其他動作的判斷方法
            isPartnerTalking() {
                if (this.openingLineLoading == true || this.receivedMsgLoading == true) {
                    $(".partner-block-area").removeClass('d-none');

                    setTimeout(function () {
                        $(".partner-block-area").addClass('d-none');
                    }, 1500);

                    return true;
                }
                return false;
            },
            // 自己講話時不能有其他動作的判斷方法
            isHumanTalking() {
                if (this.openingLineLoading == true || this.sentMsgLoading == true) {
                    $(".human-block-area").removeClass('d-none');

                    setTimeout(function () {
                        $(".human-block-area").addClass('d-none');
                    }, 1500);

                    return true;
                }
                return false;
            },

            // ===== FREE_TALK ===== //
            startNewTalk() {
                location.href = "chatroom.html?chatroomType=FREE_TALK&action=CREATE&unitNumber=" + this.unitNumber;
            },

            openTalkRecord() {
                $("#chatroom-history").css({ top: "0", right: "0" });
            },

            hideTalkRecord() {
                $("#chatroom-history").css({ top: "-100%", right: "-100%" });
                $("#dropdownMenuBtn").toggleClass("show");
                $(".dropdown-menu.dropdown-menu-end.main").removeClass("show");
            },

            enterChatroom(key) {
                this.showEditRecordTitle = null;    // 關閉「重新命名」功能
                let _this = this;
                const parts = key.split("_");
                const chatroomId = parts[0];
                const unitNumber = parts[1];
                this.globalChatroomId = chatroomId;
                this.unitNumber = unitNumber;
                $.ajax({
                    url: "chatroom/getCurrentMsgId/" + this.globalChatroomId,
                    type: "get",
                    dataType: "json",
                    success: function (msgResponse) {
                        if (msgResponse.code === "C000") {
                            _this.currentMessageId = msgResponse.data;

                            const dto = {
                                chatroomType: _this.chatroomType,
                                chatroomId: _this.globalChatroomId,
                                currentMessageId: _this.currentMessageId,
                                unitNumber: _this.unitNumber,
                            };

                            $.ajax({
                                url: "chatroom/enter",
                                type: "POST",
                                dataType: "json",
                                data: JSON.stringify(dto),
                                contentType: "application/json; charset=utf-8",
                                success: function (response) {
                                    if (response.code === "C000") {
                                        if (response.data.length > 0) {
                                            _this.messages = response.data.map(obj => {
                                                const key = Number(Object.keys(obj)[0]);
                                                const value = obj[key];
                                                return { key, value };
                                            });
                                            _this.updateMessages();
                                            _this.hideTalkRecord();
                                        }
                                    }
                                },
                            });
                        }
                    }
                });

            },

            genChatroomTitle() {
                let _this = this;
                let key = this.globalChatroomId + "_" + this.unitNumber;
                if (this.chatroomType === "FREE_TALK" &&
                    this.messages.length == 2 &&
                    !this.chatroomIdTitles.some(item => item.key === key)
                ) {
                    $.ajax({
                        url: "chatroom/title/generate",
                        type: "POST",
                        dataType: "json",
                        data: JSON.stringify({ chatroomId: this.globalChatroomId }),
                        contentType: "application/json; charset=utf-8",
                        success: function (response) {
                            if (response.code === "C000") {
                                _this.chatroomTitle = response.data;
                                console.log("產生聊天室標題成功");
                                _this.chatroomIdTitles.unshift({
                                    key: Object.keys(_this.chatroomTitle)[0],
                                    value: _this.chatroomTitle[Object.keys(_this.chatroomTitle)[0]]
                                });
                            }
                        },
                    });
                }
            },

            getAllChatRecord() {
                let _this = this;
                $.ajax({
                    url: "chatroom/history",
                    type: "get",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code == "C000") {
                            _this.chatroomIdTitles = Object.entries(response.data).map(([key, value]) => ({
                                key: key,
                                value: value
                            }));
                        }
                    },
                });
            },

            toggleRecordDropdown(index, event, key, value) {
                this.showEditRecordTitle = null;    // 關閉「重新命名」功能
                if (this.dropdownRecordOpen === index) {
                    this.dropdownRecordOpen = null;
                } else {
                    this.dropdownRecordOpen = index;
                    // 設定選單樣式
                    this.dropdownRecordStyle = `position: absolute; inset: 0px auto auto 0px; margin: 0px; transform: translate3d(-109.5px, 40px, 0px);`;

                    const chatroomId = key.split("_")[0];
                    this.dropdownRecordChatroomId = chatroomId;
                    this.dropdownRecordName = value;
                }
            },

            closeDropdownOnOutsideClick(event) {
                const dropdown = document.querySelector('.dropdown-record.show');
                if (dropdown) {
                    this.dropdownRecordOpen = null;
                }
            },

            deleteChatRecord() {
                let _this = this;
                $.ajax({
                    url: "chatroom/chatRecord/delete/" + this.dropdownRecordChatroomId,
                    type: "DELETE",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    beforeSend: function () {
                        $("#deleteRecordModal").modal('hide');
                    },
                    success: function (response) {
                        if (response.code === "C000") {
                            console.log("刪除成功");
                            _this.dropdownRecordOpen = null;
                            _this.getAllChatRecord();
                        } else {
                            alert("刪除失敗，請稍後再試！");
                        }
                    },
                });
            },

            editTitle(item) {
                let _this = this;
                const chatroomId = item.key.split("_")[0];
                const editName = item.value;

                $.ajax({
                    url: "chatroom/chatRecord/editName",
                    type: "PATCH",
                    dataType: "json",
                    data: JSON.stringify({ chatroomId: chatroomId, editName: editName }),
                    contentType: "application/json; charset=utf-8",
                    beforeSend: function () {
                        this.showEditRecordTitle = null;
                    },
                    success: function (response) {
                        if (response.code === "C000") {
                            console.log("修改成功");
                            _this.showEditRecordTitle = null;
                            _this.getAllChatRecord();
                        } else {
                            alert("修改失敗，請稍後再試！");
                        }
                    },
                });
            },

            openSharingModal(key) {
                let _this = this;
                const chatroomId = key.split("_")[0];
                this.snapshotChatroomId = chatroomId;

                $.ajax({
                    url: "snapshot/info/" + chatroomId,
                    type: "GET",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    beforeSend: function () {
                        _this.targetSnapshotId = null;
                        _this.snapshotLink = '';
                    },
                    success: function (response) {
                        if (response.code === "C000") {
                            _this.targetSnapshotId = response.data.id;
                        }
                    },
                });
            },

            createLink() {
                let _this = this;
                $.ajax({
                    url: "snapshot/create",
                    type: "POST",
                    dataType: "json",
                    data: JSON.stringify({ chatroomId: this.snapshotChatroomId }),
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code === "C000") {
                            _this.targetSnapshotId = response.data.id;
                            _this.snapshotLink = "https://gurula.cc/talkyo/share/" + response.data.link;
                            _this.copyLink(_this.snapshotLink);
                        }
                    },
                });
            },

            updateLink() {
                let _this = this;
                $.ajax({
                    url: "snapshot/update",
                    type: "PATCH",
                    dataType: "json",
                    data: JSON.stringify({ chatroomId: this.snapshotChatroomId }),
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code === "C000") {
                            _this.snapshotLink = "https://gurula.cc/talkyo/share/" + response.data;
                            _this.copyLink(_this.snapshotLink);
                        }
                    },
                });
            },

            copyLink(text) {
                navigator.clipboard.writeText(text).then(() => {
                    console.log('連結已複製到剪貼簿！');
                }).catch(err => {
                    console.error('複製失敗', err);
                });
            },

            openSettingModal() {
                $("#shareModal").modal('hide');
                this.updateSnapshotList();
            },

            updateSnapshotList() {
                let _this = this;
                $.ajax({
                    url: "snapshot/all",
                    type: "GET",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code === "C000") {
                            _this.snapshotList = response.data;
                        }
                    },
                });
            },

            showLink(link) {
                window.open("share/" + link, "_blank");
            },

            deleteLink(id) {
                let _this = this;
                $.ajax({
                    url: "snapshot/delete/" + id,
                    type: "DELETE",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code === "C000") {
                            _this.updateSnapshotList();
                        }
                    },
                });
            },

            // ===== favorite ===== //
            async toggleFavorite(unitNumber, content, translation) {
                let data = { unitNumber, content, translation };

                try {
                    const response = await $.ajax({
                        url: "member/favorite/toggle",
                        type: "POST",
                        dataType: "json",
                        data: JSON.stringify(data),
                        contentType: "application/json; charset=utf-8"
                    });

                    if (response.code === 'C000') {
                        await this.memberInfo();
                        this.updateFavorite();
                    }
                } catch (error) {
                    console.error("Error in toggleFavorite:", error);
                }
            },

            // ===== 跟讀 shadowing ===== //
            shadowing(tag) {
                this.switchGrammar = false;
                this.switchInformal = false;
                this.switchFormal = false;
                if (tag == "grammar") {
                    this.switchGrammar = true;
                } else if (tag == "informal") {
                    this.switchInformal = true;
                } else if (tag == "formal") {
                    this.switchFormal = true;
                }
                $("#shadowingModal").modal('show');
            },

            async startShadowing() {
                // 發音分析

                this.shadowingModel = true;
                this.startRecord();
            },
        }
    }