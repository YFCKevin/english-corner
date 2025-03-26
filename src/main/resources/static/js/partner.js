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
            partnerList: [],
            selectedPartnerId: '',
            selectedList: [],
            showButtons: false,

            async init() {
                let _this = this;

                $.ajax({
                    url: "member/info",
                    type: "get",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        _this.member = response;
                        console.log(_this.member)
                    },
                    error: function (xhr, status, error) {
                        if (xhr.status === 401) {
                            // 401 Unauthorized
                            window.location.href = "sign-in.html";
                        }
                    }
                });

                $.ajax({
                    url: "partner/list",
                    type: "get",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code == 'C000'){
                            _this.partnerList = response.data.map(partner => ({
                                ...partner,
                                selectedTab: ''
                            }));
                            console.log(_this.partnerList);
                        }
                    },
                });
            },

            getPartnerData (){
                return this.partnerList;
            },

            choosePartner(item) {
                $(".fixed-plugin").hide();
                if (this.selectedPartnerId !== item.id) {
                    this.selectedPartnerId = item.id;
                    this.showButtons = true;
                }
            },

            toggleTab(item, tabType) {
                if (item.selectedTab === tabType) {
                    item.selectedTab = '';
                    this.selectedList = [];
                } else {
                    item.selectedTab = tabType;
                    this.selectedList = tabType === 'voicePersonality' ? item.voicePersonality : item.tailoredScenario;
                }
            },

            playSound(shortName) {
                let audioFile = "audio/demo/" + shortName + ".wav";
                if (shortName) {
                    const audio = new Audio(audioFile);
                    audio.play();
                } else {
                    console.warn("No audio file specified.");
                }
            },

            pickUpPartner (){
                $.ajax({
                    url: "member/choosePartner/" + this.selectedPartnerId,
                    type: "patch",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code == 'C000'){
                            location.href = "index.html";
                        }
                    },
                });
            },
        }
    }