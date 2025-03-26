    var win = navigator.platform.indexOf('Win') > -1;
    if (win && document.querySelector('#sidenav-scrollbar')) {
        var options = {
            damping: '0.5'
        }
        Scrollbar.init(document.querySelector('#sidenav-scrollbar'), options);
    }

    function loadData() {
        return {
            member: {},
            report: {},
            lessonId: "",
            unitNumber: "",
            chatroomId: "",
            historyReports: [],

            async init() {
                let _this = this;

                const urlParams = new URLSearchParams(window.location.search);
                this.lessonId = urlParams.get('lessonId');
                lessonId = urlParams.get('lessonId');
                this.chatroomId = urlParams.get('chatroomId');
                this.unitNumber = urlParams.get('unitNumber');

                $.ajax({
                    url: "member/info",
                    type: "get",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        _this.member = response;
                    },
                    error: function (xhr, status, error) {
                        if (xhr.status === 401) {
                            // 401 Unauthorized
                            window.location.href = "sign-in.html";
                        }
                    }
                });

                $.ajax({
                    url: "chatroom/learningReport",
                    type: "get",
                    dataType: "json",
                    data: {
                        chatroomId: this.chatroomId,
                        lessonId: this.lessonId || null,
                        unitNumber: this.unitNumber || null
                    },
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code == "C000"){
                            _this.report = response.data;
                            console.log(_this.report)

                            let overallRating = _this.report.overallRating;

                            if (overallRating < 60) {
                                _this.report.starCount = 1;
                            } else if (overallRating >= 60 && overallRating < 80) {
                                _this.report.starCount = 2;
                            } else if (overallRating >= 80) {
                                _this.report.starCount = 3;
                            } else {
                                _this.report.starCount = 1;
                            }
                        }
                    },
                });


                $.ajax({
                    url: "member/project/finish/" + this.lessonId,
                    type: "get",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code == "C000"){
                            _this.historyReports = response.data;
                        }
                    }
                });
            },
            reTryLesson() {
                location.href = "chatroom.html?chatroomType=PROJECT&action=CREATE&lessonId=" + this.lessonId;
            },
            showChatRecord() {
                location.href = "chatroom.html?chatroomType=PROJECT&action=CREATE&closed=true&chatroomId=" + this.chatroomId;
            },
            chatRecord(chatroomId) {
                location.href = "chatroom.html?chatroomType=PROJECT&action=CREATE&closed=true&chatroomId=" + chatroomId;
            },
        }
    }