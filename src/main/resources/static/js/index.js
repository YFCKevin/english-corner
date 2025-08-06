    var win = navigator.platform.indexOf('Win') > -1;
    if (win && document.querySelector('#sidenav-scrollbar')) {
        var options = {
            damping: '0.5'
        }
        Scrollbar.init(document.querySelector('#sidenav-scrollbar'), options);
    }

    function loadData() {
        return {
            partner: {},
            member: {},
            learningPlan: [],
            lesson: {},
            lessonId: "",
            showChallenge: false,   // 用來判斷「前往挑戰」出現與隱藏，只要有進行中的課程就隱藏
            visibilityState: 0, // 判斷用
            translationVisible: false,  // 遮翻譯
            originalVisible: false,     // 遮原文
            iconText: '翻譯',
            basicSentences: [],
            advancedSentences: [],

            // ===== shadowing 跟讀 ===== //
            switchBasic: false,
            switchAdvanced: false,
            currentIndex: 0,
            totalIndex: 4,
            minPercent: 20,
            shadowingAudioUrl: "",
            isRecording: false,
            isRecordCanceled: false,    // 用來判斷 cancelRecord and stopRecord
            shadowingModel: false,


            async init() {
                let _this = this;

                this.memberInfo();

                $.ajax({
                    url: "member/projects",
                    type: "get",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code == "C000"){
                            _this.learningPlan = response.data;
                            _this.getLearningPlanData();
                            _this.scrollToAnchor();
                        }
                    },
                });
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
                        console.log(123)
                        window.location.href = "sign-in.html";
                    }
                }
            },

            getLearningPlanData() {
                const formatter = new Intl.DateTimeFormat('zh-TW', {
                    year: 'numeric',
                    month: 'numeric',
                    day: 'numeric'
                });

                let firstNotStartedFound = false;

                this.learningPlan = this.learningPlan.map(item => {
                    if (item.closeDate) {
                        const date = new Date(item.closeDate);
                        item.closeDate = formatter.format(date);
                    }
                    if (item.overallRating >= 0 && item.overallRating < 60) {
                        item.starCount = 1;
                    } else if (item.overallRating >= 60 && item.overallRating < 80) {
                        item.starCount = 2;
                    } else if (item.overallRating >= 80) {
                        item.starCount = 3;
                    } else {
                        item.starCount = 0;
                    }

                    if (item.lessonType === 'COMPLETED') {
                        item.showBtn = false;
                    } else if (item.lessonType === 'NOT_STARTED' || item.lessonType === 'IN_PROGRESS') {
                        if (!firstNotStartedFound) {
                            item.showBtn = false;
                            item.anchor = true;
                            firstNotStartedFound = true;
                        } else {
                            item.showBtn = true;
                        }
                    } else {
                        item.showBtn = true;
                    }

<!--                    this.showChallenge = this.learningPlan.some(item => item.chatroomId && item.closeDate)-->
<!--                        ? false-->
<!--                        : this.learningPlan.some(item => item.chatroomId) ? true : this.showChallenge;-->

                    return item;
                });
                console.log(this.learningPlan)
                return this.learningPlan;
            },

          challenge(lessonId) {
              let _this = this;

              $.ajax({
                  url: "course/lesson/info/" + lessonId,
                  type: "get",
                  dataType: "json",
                  contentType: "application/json; charset=utf-8",
                  success: function (response) {
                      if (response.code == "C000"){
                          _this.lesson = response.data;
                          _this.lessonId = lessonId;
                          $("#lessonModalLabel").text(_this.lesson.name);
                          $("#desc").text(_this.lesson.desc);
                          $("#coverPath").attr('src', "image/" + _this.lesson.lessonNumber + "/" + _this.lesson.coverName);
                      }
                  },
              });
          },

          nextModal(current, next) {
              let currentModal = bootstrap.Modal.getInstance(document.getElementById(current));
              currentModal.hide();

              let nextModal = new bootstrap.Modal(document.getElementById(next));
              nextModal.show();

              if (next === 'prepareModal') {
                  this.basicSentences = [];
                  this.basicSentences = this.lesson.sentences.slice(0, 5);
                  this.basicSentences.forEach(sentence => {
                      if (this.member.savedFavoriteSentences.map(item => item.unitNumber).includes(sentence.unitNumber)) {
                          sentence.favorite = true;
                      } else {
                          sentence.favorite = false;
                      }
                  });
                  console.log(this.basicSentences)
              }
          },

          toggleTranslationVisibility() {
              this.visibilityState = (this.visibilityState + 1) % 3;

              const translationParagraphs = document.querySelectorAll('.translate');
              const originalParagraphs = document.querySelectorAll('.english');

              translationParagraphs.forEach(p => {
                  p.classList.remove("hide-translation-bg");
              });

              originalParagraphs.forEach(p => {
                  p.classList.remove("hide-original-bg");
              });

              if (this.visibilityState === 0) {
                this.translationVisible = true;
                this.originalVisible = true;
                this.iconText = '翻譯';
              } else if (this.visibilityState === 1) {
                this.translationVisible = false;
                this.originalVisible = true;
                this.iconText = '遮翻譯';
                translationParagraphs.forEach(p => {
                    p.classList.add("hide-translation-bg");
                });
              } else if (this.visibilityState === 2) {
                this.translationVisible = false;
                this.originalVisible = false;
                this.iconText = '遮原文';
                originalParagraphs.forEach(p => {
                    p.classList.add("hide-original-bg");
                });
              }
          },

          showSentences(tabId) {
              const tabElement = document.querySelector(`.nav-link[href="#${tabId}"]`);
              if (tabElement) {
                  if (tabId === 'profile-tabs-simple') {    // 基礎
                      this.basicSentences = [];
                      this.basicSentences = this.lesson.sentences.slice(0, 5);
                      console.log(this.member.favoriteSentences)
                      this.basicSentences.forEach(sentence => {
                          if (this.member.savedFavoriteSentences.map(item => item.unitNumber).includes(sentence.unitNumber)) {
                              sentence.favorite = true;
                          } else {
                              sentence.favorite = false;
                          }
                      });
                  } else if (tabId === 'dashboard-tabs-simple') {   // 進階
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
                  audio.play();
              } else {
                  console.warn("No audio file specified.");
              }
          },

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

                      const updateFavorite = sentences => {
                          sentences.forEach(item => {
                              item.favorite = this.member.savedFavoriteSentences.map(sentence => sentence.unitNumber).includes(item.unitNumber);
                          });
                      };

                      updateFavorite(this.basicSentences);
                      updateFavorite(this.advancedSentences);
                  }
              } catch (error) {
                  console.error("Error in toggleFavorite:", error);
              }
          },

          startLesson() {
              location.href = "chatroom.html?chatroomType=PROJECT&action=CREATE&lessonId=" + this.lessonId;
          },

          scrollToAnchor() {
              this.$nextTick(() => {
                  let anchor = document.querySelector(".anchor");
                  if (anchor) {
                      anchor.scrollIntoView({ behavior: "smooth", block: "start" });
                  }
              })
          },

          // ===== 跟讀 shadowing ===== //
          shadowing(tag) {
              this.switchBasic = false;
              this.switchAdvanced = false;
              if (tag == "basic") {
                  this.switchBasic = true;
              } else if (tag == "advanced") {
                  this.switchAdvanced = true;
              }
              $("#prepareModal").modal('hide');
              $("#shadowingModal").modal('show');
          },
          async startRecord() {
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
          },
          cancelRecord() {
              this.isRecording = !this.isRecording;
              this.isRecordCanceled = true;
              this.shadowingModel = false;
              if (this.mediaRecorder && this.mediaRecorder.state !== "inactive") {
                  this.mediaRecorder.stop();
              }
              this.mediaRecorder.stream.getTracks().forEach(track => track.stop());

              this.mediaRecorder = null;
          },

          async startShadowing() {
              this.shadowingModel = true;
              this.startRecord();
          },

          playShadowingSound(el, rate) {
              const audioName = $(el).attr("data-url");
              let audioUrl = "audio/" + this.lesson.courseTopic + "/" + this.lesson.lessonNumber + "/" + audioName;

              if (audioName) {
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

          async myVoice(el, rate) {
              const audioUrl = $(el).attr("data-url");

              if (!audioUrl) {
                  console.warn("沒有 audio URL");
                  return;
              }

              try {
                  const AudioContext = window.AudioContext || window.webkitAudioContext;
                  const audioContext = new AudioContext();

                  const response = await fetch(audioUrl);
                  const arrayBuffer = await response.arrayBuffer();

                  const audioBuffer = await audioContext.decodeAudioData(arrayBuffer);

                  const source = audioContext.createBufferSource();
                  source.buffer = audioBuffer;
                  source.playbackRate.value = rate; // 播放速度
                  source.connect(audioContext.destination);

                  source.start(0);

                  source.onended = () => {
                      console.log("音訊播放完畢");
                  };
              } catch (error) {
                  console.error("Web Audio 播放失敗：", error);
              }
          },

          leaveShadowing() {
              $("#shadowingModal").modal('hide');
              this.currentIndex = 0;
              $("#prepareModal").modal('show');
          },

          next() {
              if (this.currentIndex < this.basicSentences.length - 1) {
                  this.currentIndex++;
                  this.shadowingAudioUrl = "";
              }

          },
          preview() {
              if (this.currentIndex > 0) {
                  this.currentIndex--;
                  this.shadowingAudioUrl = "";
              }
          },

        }
    }