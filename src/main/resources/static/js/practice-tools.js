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
            },

            choosePlan(unitNumber) {
                location.href = "chatroom.html?chatroomType=FREE_TALK&action=LEGACY&unitNumber=" + unitNumber;
            },
        }
    }