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
            scenarios: [],
            unitNumber: "",
            filteredScenarios: [],
            historyRecords: [],
            activeTab: 'ALL',
            tabs: [
              { text: '全部', value: 'ALL', href: 'all-tabs-simple', ariaControls: 'all' },
              { text: '職場', value: 'WORKPLACE', href: 'workplace-tabs-simple', ariaControls: 'workplace' },
              { text: '旅行', value: 'TRAVEL', href: 'travel-tabs-simple', ariaControls: 'travel' },
              { text: '情感交流', value: 'FRIENDSHIP', href: 'friendship-tabs-simple', ariaControls: 'friendship' }
            ],

            async init() {
                let _this = this;

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

                fetch('data/scenario.json')
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Network response was not ok ' + response.statusText);
                        }
                        return response.json();
                    })
                    .then(data => {
                        _this.scenarios = data;
                        _this.filteredScenarios = data;
                        _this.getHistoryScenarioRecord();
                    })
                    .catch(error => console.error('Error fetching the JSON file:', error));
            },

            getHistoryScenarioRecord() {
                let _this = this;
                $.ajax({
                    url: "scenario/history/record",
                    type: "get",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code == "C000"){
                            _this.historyRecords = response.data;

                            if (_this.historyRecords.length == 0) {
                                $(".history-record").hide();
                            }

                            _this.historyRecords = _this.historyRecords.map(item => {
                                if (item.closeDate) {
                                    const date = new Date(item.closeDate);
                                    const formatter = new Intl.DateTimeFormat('zh-TW', {
                                        year: 'numeric',
                                        month: 'numeric',
                                        day: 'numeric'
                                    });
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

                                if (item.unitNumber) {
                                    item.coverPath = "image/scenario/" + item.unitNumber + ".png";
                                    const matchingScenario = _this.scenarios.find(scenario => scenario.unitNumber === item.unitNumber);
                                    if (matchingScenario) {
                                        item.zhTitle = matchingScenario.zhTitle;
                                    }
                                }

                                return item;
                            })
                        }
                    }
                });
            },

            getRolePlayData () {
                return this.filteredScenarios;
            },

            filterScenarios (category) {
                this.filteredScenarios = [];
                if (category === 'ALL') {
                    this.filteredScenarios = this.scenarios;
                } else {
                    this.filteredScenarios = this.scenarios.filter(item => item.category === category);
                }
            },

            choosePlan(coverName, zhTitle, humanRole, partnerRole, subject, unitNumber) {
                $("#talkModal").modal('show');
                $("#talkModalLabel").text(zhTitle);
                $("#humanRole").text(humanRole);
                $("#partnerRole").text(partnerRole);
                $("#subject").text(subject);
                $("#modalImg").attr('src', "image/" + coverName);
                this.unitNumber = unitNumber;
            },

            startScenario() {
                location.href = "chatroom.html?chatroomType=SITUATION&action=CREATE&unitNumber=" + this.unitNumber;
            },

            checkReport(record) {
                location.href = "learning-report.html?chatroomId=" + record.chatroomId + "&unitNumber=" + record.unitNumber;
            },
        }
    }