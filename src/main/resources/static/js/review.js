    var win = navigator.platform.indexOf('Win') > -1;
    if (win && document.querySelector('#sidenav-scrollbar')) {
        var options = {
            damping: '0.5'
        }
        Scrollbar.init(document.querySelector('#sidenav-scrollbar'), options);
    }

    $(document).ready(function() {
        $('#iconNavbarSidenav').click(function() {

            $('body').toggleClass('g-sidenav-pinned');
            $('#iconSidenav').toggleClass('d-none');
            $('#sidenav-main').toggleClass('bg-white');

            $('#iconSidenav').click(function() {
                $('#iconNavbarSidenav').click();
            });
        });
    });

    function loadData() {
        return {
            member: {},
            sentenceList: [],

            async init() {
                let _this = this;
                this.memberInfo();
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
                    console.log(response.savedFavoriteSentences)
                    _this.sentenceList = response.savedFavoriteSentences;
                } catch (xhr) {
                    if (xhr.status === 401) {
                        window.location.href = "sign-in.html";
                    }
                    throw error;
                }
            },

            getSentenceData() {
                this.sentenceList.forEach(sentence => {
                    if (this.member.savedFavoriteSentences.map(item => item.unitNumber).includes(sentence.unitNumber)) {
                        sentence.favorite = true;
                    } else {
                        sentence.favorite = false;
                    }
                });
                return this.sentenceList;
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
                        this.updateFavorite();
                    }
                } catch (error) {
                    console.error("Error in toggleFavorite:", error);
                }
            },

            async translate(unitNumber, content) {
                let _this = this;
                let data = { text: content, unitNumber: unitNumber };
                $(".translate-icon").css("pointer-events", "none");
                try {
                    const response = await $.ajax({
                        url: "member/favorite/translation/save",
                        type: "post",
                        dataType: "json",
                        data: JSON.stringify(data),
                        contentType: "application/json; charset=utf-8",
                    });

                    if (response.code == "C000") {
                        await _this.memberInfo();
                        _this.updateFavorite();
                        $(".translate-icon").css("pointer-events", "auto");
                    }
                } catch (error) {
                    console.error("Error during translation save:", error);
                    $(".translate-icon").css("pointer-events", "auto");
                }
            },

            updateFavorite() {
                if (this.sentenceList.length > 0) {
                    this.sentenceList.forEach(item => {
                        item.favorite = this.member.savedFavoriteSentences
                            .map(sentence => sentence.unitNumber)
                            .includes(item.unitNumber);
                    });
                }
            },

            genAudio(unitNumber, content, audioName) {
                if (audioName != null){
                    let audioUrl = "audio/favorite/" + audioName;
                    const audio = new Audio(audioUrl);
                    audio.play();
                } else {
                    let _this = this;
                    let data = { content: content, unitNumber: unitNumber };
                    $.ajax({
                        url: "chatroom/genAudio",
                        type: "post",
                        dataType: "json",
                        data: JSON.stringify(data),
                        contentType: "application/json; charset=utf-8",
                        success: function (response) {
                            if (response.code == "C000") {
                                const audio = new Audio(response.data);
                                audio.play();
                            }
                        },
                    });
                }
            },
        }
    }