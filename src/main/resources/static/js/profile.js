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
            profile: {},
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
                        if (_this.member && _this.member.role) {
                            if (_this.member.role === 'ADMIN') {
                                _this.member.role = '管理員';
                            } else if (_this.member.role === 'STUDENT') {
                                _this.member.role = '學員';
                            }
                        }
                    },
                    error: function (xhr, status, error) {
                        if (xhr.status === 401) {
                            // 401 Unauthorized
                            window.location.href = "sign-in.html";
                        }
                    }
                });

                $.ajax({
                    url: "member/profile",
                    type: "get",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        if (response.code == "C000"){
                            _this.profile = response.data;
                            console.log(_this.profile)
                        }
                    },
                });

            },
        }
    }